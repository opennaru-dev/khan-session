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

import com.opennaru.khan.session.manager.KhanSessionManager;
import com.opennaru.khan.session.store.SessionId;
import com.opennaru.khan.session.store.SessionStore;
import com.opennaru.khan.session.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * KHAN [session manager]에서 관리하는 Session Object
 * 세션 객체들은 SessionStore에 저장된다.
 * Infinispan Library Mode/HotRod Client Mode나 Redis 구현체가 있음.
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
@SuppressWarnings("deprecation")
public class KhanHttpSession implements HttpSession {
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
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     *  Session ID
     */
    private final String sessionId;

    /**
     *  SessionStore - Interface
     */
    private final SessionStore sessionStore;

    /**
     *  HttpSession Object
     */
    private final HttpSession session;
    /**
     *  Khan SessionStore 저장을 위한 Key 생성기
     */
    private final KhanSessionKeyGenerator keyGenerator;
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
    private Integer maxInactiveIntervalSeconds = null;

    /**
     *  Khan Session manager
     */
    private KhanSessionManager sessionManager = null;

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
        StringUtils.isNotNull("sessionId", sessionId);
        StringUtils.isNotNull("sessionStore", sessionStore);
        StringUtils.isNotNull("namespace", namespace);
        StringUtils.isNotNull("timeoutMin", timeoutMin);
        StringUtils.isNotNull("session", session);

        this.sessionId = sessionId;
        this.sessionStore = sessionStore;
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

        if( log.isDebugEnabled() ) {
            log.debug("keyGenerator.generate(ATTRIBUTES_KEY)=" + keyGenerator.generate(ATTRIBUTES_KEY));
            log.debug("KhanHttpSession.attributes=" + attributes);
        }

        //session.setAttribute("khansid", sessionId);
        //KhanSessionManager.getInstance().addSessionId(sessionId);
        //sessionManager.addSessionId(sessionId);

        if (log.isDebugEnabled()) {
            log.debug("New KhanHttpSession is created. (sessionId: " + sessionId + ", attributes: " + attributes + ")");
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

        KhanSessionMetadata metadata = sessionStore.get(keyGenerator.generate(METADATA_KEY));
        boolean isNotInvalidated = metadata != null &&
                metadata.getInvalidated() != null &&
                metadata.getInvalidated() == false;

        boolean isNotExpired = sessionStore.get(keyGenerator.generate(ATTRIBUTES_KEY)) != null;

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
            removeAttributesFromStore();
        }
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
        } else {
            value = null;
        }

        if (log.isDebugEnabled()) {
            log.debug("getAttribute is called. (sessionId: " + sessionId + ", " + name + " -> " + value + ")");
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

            return e;
        }
    }

    /**
     * Invalidates this session then unbinds any objects bound
     * to it.
     */
    @Override
    public void invalidate() {

        if (log.isDebugEnabled()) {
            log.debug("invalidate called. (sessionId: " + sessionId + ")");
        }

        try {
            KhanSessionManager.getInstance(this.getServletContext().getContextPath()).removeSessionId(this);

            session.invalidate();
            attributes.clear();
            khanSessionMetadata.setInvalidated(true);
            removeAttributesFromStore();
            //
            //sessionManager.getSessionMonitor().sessionDestroyed();
        } catch (NullPointerException e) {

        } catch (IllegalStateException ise) {

        }
    }

    /**
     * remove attribute from session
     * @param name
     */
    @Override
    public void removeAttribute(String name) {
        reloadAttributes();
        attributes.remove(name);
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
            log.debug("setAttribute called. (sessionId: " + sessionId + ", " + name + " -> " + value + ")");
        }

        if (value == null) {
            removeAttribute(name);
        }
        if (value instanceof Serializable) {
            try {
                reloadAttributes();
                attributes.put(name, (Serializable) value);
                saveAttributesToStore();
            } catch (NullPointerException e) {
            }
        } else {
            String message = "The value should be an instance of java.io.Serializable. (" + value + ")";
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

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public long getCreationTime() {
        if (khanSessionMetadata == null || khanSessionMetadata.getCreationTime() == null) {
            return 0L;
        } else {
            return khanSessionMetadata.getCreationTime().getTime();
        }
    }

    @Override
    public long getLastAccessedTime() {
        if (khanSessionMetadata == null || khanSessionMetadata.getLastAccessedTime() == null) {
            return 0L;
        } else {
            return khanSessionMetadata.getLastAccessedTime().getTime();
        }
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveIntervalSeconds;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveIntervalSeconds = interval;
        session.setMaxInactiveInterval(interval);
    }

    @Override
    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    @SuppressWarnings("deprecation")
    @Override
    public HttpSessionContext getSessionContext() {
        return session.getSessionContext();
    }

    @Override
    public boolean isNew() {
        return isNewlyCreated;
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    /**
     * Save Attributes to SessionStore
     */
    private void saveAttributesToStore() {
        sessionStore.put(keyGenerator.generate(ATTRIBUTES_KEY), toMap(), getMaxInactiveInterval());

        KhanSessionManager.getInstance(this.getServletContext().getContextPath()).putSessionId(this);

    }

    /**
     * Remove attributes from SessionStore
     */
    private void removeAttributesFromStore() {
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
