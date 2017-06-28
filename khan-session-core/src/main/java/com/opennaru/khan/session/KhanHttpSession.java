/*
 * Opennaru, Inc. http://www.opennaru.com/
 *
 *  Copyright (C) 2014 Opennaru, Inc. and/or its affiliates.
 *  All rights reserved by Opennaru, Inc.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.opennaru.khan.session;

import com.opennaru.khan.session.filter.KhanSessionFilter;
import com.opennaru.khan.session.manager.KhanSessionManager;
import com.opennaru.khan.session.store.SessionStore;
import com.opennaru.khan.session.util.StackTraceUtil;
import com.opennaru.khan.session.util.StringUtils;
import com.opennaru.khan.session.util.SysOutUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * KHAN [session manager]에서 관리하는 Session Object
 * 세션 객체들은 SessionStore에 저장된다.
 * Infinispan Library Mode/HotRod Client Mode나 Redis 구현체가 있음.
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
@SuppressWarnings("deprecation")
public class KhanHttpSession implements HttpSession, Serializable {
    /**
     *  Store에 저장할 Attribute Key
     */
    public static final String ATTRIBUTES_KEY = "_ATTR_";
    /**
     *  Store에 저장할 Metadata Key
     */
    public static final String METADATA_KEY = "_META_";

    /**
     *  logger
     */
    private transient Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     *  Session ID
     */
    private String khanSessionId;

    private String khanNamespace;

    /**
     *  SessionStore - Interface
     */
    private transient final SessionStore sessionStore;

    /**
     *  HttpSession Object
     */
    private transient final HttpSession session;
    /**
     *  Khan SessionStore 저장을 위한 Key 생성기
     */
    private transient KhanSessionKeyGenerator keyGenerator;
    /**
     * Session Attribute를 저장할 객체
     */
    private ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<Object, Object>();
    /**
     * Session Metadata를 저장할 객체
     */
    private KhanSessionMetadata khanSessionMetadata;
    /**
     *  새로 생성된 세션인지
     */
    private boolean isNewlyCreated = false;

    /**
     *  max inactive interval
     */
    private Integer maxInactiveIntervalSec = null;

    /**
     *  Khan Session manager
     */
    private transient KhanSessionManager sessionManager = null;

    private int numberOfChangeAttribute = 0;
    private int numberOfGetAttribute = 0;
    private int numberOfNotNullGet = 0;

    /**
     * Constructor
     *
     * @param sessionId
     * @param sessionStore
     * @param namespace
     * @param timeoutMin
     * @param session
     * @param sessionManager
     * @param clientIp
     */
    public KhanHttpSession(String sessionId, SessionStore sessionStore, String namespace, Integer timeoutMin, HttpSession session, KhanSessionManager sessionManager, String clientIp) {
        // Check argument is not null
        StringUtils.isNotNull("khanSessionId", sessionId);
        StringUtils.isNotNull("sessionStore", sessionStore);
        StringUtils.isNotNull("khanNamespace", namespace);
        StringUtils.isNotNull("timeoutMin", timeoutMin);
        StringUtils.isNotNull("session", session);

        this.khanSessionId = sessionId;
        this.sessionStore = sessionStore;
        this.khanNamespace = namespace;
        this.session = session;
        this.keyGenerator = new KhanSessionKeyGenerator(sessionId, namespace);
        this.sessionManager = sessionManager;

        if( log.isDebugEnabled() ) {
            log.debug("session.getMaxInactiveInterval()=" + session.getMaxInactiveInterval());
            log.debug("timeoutMinutes=" + timeoutMin);
        }

        // set session timeout seconds
        setMaxInactiveInterval(timeoutMin * 60);

        khanSessionMetadata = sessionStore.get(keyGenerator.generate(METADATA_KEY));
        if (khanSessionMetadata == null) {
            isNewlyCreated = true;
            khanSessionMetadata = new KhanSessionMetadata();
            khanSessionMetadata.setInvalidated(false);
            khanSessionMetadata.setCreationTime(new Date());
            khanSessionMetadata.setClientIp(clientIp);
            sessionStore.put(keyGenerator.generate(METADATA_KEY), khanSessionMetadata, getMaxInactiveInterval());
            sessionStore.put(keyGenerator.generate(ATTRIBUTES_KEY), attributes, getMaxInactiveInterval());
        }

        attributes = sessionStore.get(keyGenerator.generate(ATTRIBUTES_KEY));

        SysOutUtil.printGetStore(attributes);

        if( log.isDebugEnabled() ) {
            log.debug("keyGenerator.generate(ATTRIBUTES_KEY)=" + keyGenerator.generate(ATTRIBUTES_KEY));
            log.debug("KhanHttpSession.attributes=" + attributes);
        }

        //session.setAttribute("khansid", khanSessionId);
        //KhanSessionManager.getInstance().addSessionId(khanSessionId);
        //sessionManager.addSessionId(khanSessionId);

        if (log.isDebugEnabled()) {
            log.debug("New KhanHttpSession is created. (khanSessionId: " + sessionId + ", attributes: " + attributes + ")");
        }
    }

    /**
     * Get KhanSessionManager
     * @return
     */
    public KhanSessionManager getSessionManager() {
        return this.sessionManager;
    }

    /**
     * Get Session Key Generator
     * @return
     */
    public KhanSessionKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * Check session is valid status
     * @return
     */
    public boolean isValid() {

        if (log.isDebugEnabled()) {
            log.debug(">>>isValid/ metadata=" + khanSessionMetadata + ",attribute=" + attributes);
            if( khanSessionMetadata == null ) {
                log.debug("isValid called. ");

                Throwable t = new Throwable();
                String message = ">>> isValid called !!";
                log.debug(message + StackTraceUtil.getStackTrace(t));
            }
        }

        boolean isNotInvalidated = khanSessionMetadata != null &&
                khanSessionMetadata.getInvalidated() != null &&
                khanSessionMetadata.getInvalidated() == false;

        boolean isNotExpired = attributes != null;

        if (log.isDebugEnabled()) {
            log.debug("isValid is called. (isNotInvalidated: " + isNotInvalidated + ", isNotExpired: " + isNotExpired + ")");
        }

        return isNotInvalidated && isNotExpired;
    }

    /**
     * reload attributes
     */
    public void reloadAttributes() {
        attributes = sessionStore.get(keyGenerator.generate(ATTRIBUTES_KEY));
    }

    public ConcurrentHashMap<Object, Object> toMap() {
        return attributes;
    }

    /**
     * Save session attributes
     */
    public void save() {
        if (isValid()) {
            saveAttributesToStore();
            if (!isNewlyCreated) {
                khanSessionMetadata.setLastAccessedTime(new Date());
            }
            sessionStore.put(keyGenerator.generate(METADATA_KEY), khanSessionMetadata, getMaxInactiveInterval());
        } else {
            SysOutUtil.println("save isNotValid removeAttributesFromStore call");
            removeAttributesFromStore();
        }
    }

    @Override
    public String getId() {
        return khanSessionId;
    }

    /**
     * Returns the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound under the name.
     *
     * @param name		a string specifying the name of the object
     * @return			the object with the specified name
     */
    @Override
    public Object getAttribute(String name) {

        Object value = null;
        if (isValid()) {
            value = attributes.get(name);
            numberOfGetAttribute++;
            SysOutUtil.println("getAttribute key: ", name, ", value: " + value);
        } else {
            value = null;
            SysOutUtil.println("getAttribute key: ", name, " isNotValid");
        }

        if( value != null ) {
            numberOfNotNullGet++;
        }

        if (log.isDebugEnabled()) {
            log.debug("getAttribute is called. (khanSessionId: " + khanSessionId + ", " + name + " -> " + value + ")");
        }

        return value;
    }

    /**
     * Get attibute names
     * @return
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        if (isValid()) {
            final Iterator<Object> names = attributes.keySet().iterator();
            Enumeration<String> e = new Enumeration<String>() {
                public boolean hasMoreElements() {
                    return names.hasNext();
                }

                public String nextElement() {
                    return names.next().toString();
                }
            };
            numberOfGetAttribute++;
            SysOutUtil.println("getAttributeNames: " + e);

            return e;
        } else {
            Enumeration<String> e = new Enumeration<String>() {
                public boolean hasMoreElements() {
                    return false;
                }

                public String nextElement() {
                    return null;
                }
            };
            SysOutUtil.println("getAttributeNames isNotValid");

            return e;
        }
    }

    /**
     * Invalidates this session then unbinds any objects bound
     * to it.
     */
    @Override
    public void invalidate() {
        SysOutUtil.println("invalidate called. (khanSessionId: ", khanSessionId, ")");

        if (log.isDebugEnabled()) {
            log.debug("invalidate called. (khanSessionId: " + khanSessionId + ")");

            Throwable t = new Throwable();
            String message = ">>> Invalidate !!";
            log.debug(message + StackTraceUtil.getStackTrace(t));
        }

        try {
            KhanSessionManager.getInstance(this.getServletContext().getContextPath()).removeSessionId(this);

            session.invalidate();
            attributes.clear();
            khanSessionMetadata.setInvalidated(true);
            removeAttributesFromStore();

            this.khanSessionMetadata = null;
            this.attributes = null;

        } catch (NullPointerException e) {
            log.debug("invalidate." + e.getMessage() );
        } catch (IllegalStateException ise) {
            log.debug("invalidate." + ise.getMessage() );
        }
    }

    /**
     * remove attribute from session
     * @param name
     */
    @Override
    public void removeAttribute(String name) {
//        BUG : Session attribute restored when remove attribute
//        reloadAttributes();
        if( attributes != null ) {
            attributes.remove(name);
            numberOfChangeAttribute++;
            SysOutUtil.println("removeAttribute key: ", name);
        }
        if (config.isEnableImmediateSave())
            saveAttributesToStore();
    }

    /**
     * Set attribute
     * @param name
     * @param value
     */
    @Override
    public void setAttribute(String name, Object value) {

        if (log.isDebugEnabled()) {
            log.debug("setAttribute called. (khanSessionId: " + khanSessionId + ", " + name + " -> [" + value + "])");
            log.debug("value=[" + value + "]");
        }

        if (value == null) {
            removeAttribute(name);
            return;
        }

        if (value instanceof Serializable) {
            try {
                if (!isValid()) {
                    throw new IllegalStateException("Invalid Session");
                }

                attributes.put(name, (Serializable) value);

                numberOfChangeAttribute++;
                SysOutUtil.println("setAttribute key: ", name, ", value: " + value);

                // spring-security 사용할 때 켜기
                KhanSessionConfig config = KhanSessionFilter.getKhanSessionConfig();
                if (config.isEnableImmediateSave())
                    saveAttributesToStore();
            } catch (NullPointerException e) {
                if (log.isDebugEnabled()) {
                    log.debug("NullPointerException", e);
                }
            }
        } else {
            String message = "The value should be an instance of java.io.Serializable. (name=[" + name + "], value=[" + value + "])";
            if (log.isDebugEnabled()) {
                Throwable t = new Throwable();
                log.debug(">>> Not serialized!\n\n" + StackTraceUtil.getStackTrace(t));
            }
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * get value
     * @param name
     * @return
     */
    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    /**
     * return value names as String array
     * @return
     */
    @Override
    public String[] getValueNames() {
        Enumeration<String> names = (Enumeration<String>) getAttributeNames();
        return Collections.list(names).toArray(new String[]{});
    }

    /**
     * return Session creation time
     * @return
     */
    @Override
    public long getCreationTime() {
        if (khanSessionMetadata == null || khanSessionMetadata.getCreationTime() == null) {
            return 0L;
        } else {
            return khanSessionMetadata.getCreationTime().getTime();
        }
    }

    /**
     * return last accessed time
     * @return
     */
    @Override
    public long getLastAccessedTime() {
        if (khanSessionMetadata == null || khanSessionMetadata.getLastAccessedTime() == null) {
            return 0L;
        } else {
            return khanSessionMetadata.getLastAccessedTime().getTime();
        }
    }

    /**
     * get servlet context
     * @return
     */
    @Override
    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    /**
     * get HttpSessionContext
     * @return
     */
    @SuppressWarnings("deprecation")
    @Override
    public HttpSessionContext getSessionContext() {
        return session.getSessionContext();
    }

    /**
     * is new session
     * @return
     */
    @Override
    public boolean isNew() {
        return isNewlyCreated;
    }

    public void setNew() {
        isNewlyCreated = true;
    }

    /**
     * put value to session
     * @param name
     * @param value
     */
    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    /**
     * remove session attribute from session
     * @param name
     */
    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    /**
     * get max inactive seconds
     * @return
     */
    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveIntervalSec;
    }

    /**
     * set max inactive seconds
     * @param interval
     */
    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveIntervalSec = interval;
        session.setMaxInactiveInterval(interval);
    }

    /**
     * Save Attributes to SessionStore
     */
    private void saveAttributesToStore() {
        SysOutUtil.printSaveStore(toMap(), numberOfChangeAttribute, numberOfGetAttribute);
        sessionStore.put(keyGenerator.generate(ATTRIBUTES_KEY), toMap(), getMaxInactiveInterval());
        KhanSessionManager.getInstance(this.getServletContext().getContextPath()).putSessionId(this);
    }

    /**
     * Remove attributes from SessionStore
     */
    private void removeAttributesFromStore() {
        SysOutUtil.println("removeAttributesFromStore");
        sessionStore.remove(keyGenerator.generate(ATTRIBUTES_KEY));
        sessionStore.remove(keyGenerator.generate(METADATA_KEY));

        KhanSessionManager.getInstance(this.getServletContext().getContextPath()).removeSessionId(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object attr : Collections.list(getAttributeNames())) {
            sb.append(attr);
            sb.append(",");
        }
        String attrs = sb.toString().replaceFirst(",$", "");

        StringBuilder sessionSb = new StringBuilder();
        sessionSb.append("KhanHttpSession(id: " + getId() + ", ");
        sessionSb.append("attributes: [" + attrs + "]");
        sessionSb.append(", creationTime: " + getCreationTime() + ", lastAccessedTime: " + getLastAccessedTime());
        sessionSb.append(", maxInactiveInterval: " + getMaxInactiveInterval() + ")");

        return sessionSb.toString();
    }

}
