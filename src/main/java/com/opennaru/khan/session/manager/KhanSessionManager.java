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
package com.opennaru.khan.session.manager;

import com.opennaru.khan.session.KhanHttpSession;
import com.opennaru.khan.session.KhanSessionConfig;
import com.opennaru.khan.session.KhanSessionKeyGenerator;
import com.opennaru.khan.session.filter.KhanSessionFilter;
import com.opennaru.khan.session.management.SessionMonitorMBean;
import com.opennaru.khan.session.management.SessionMonitorMBeanImpl;
import com.opennaru.khan.session.store.SessionStore;
import org.github.jamm.MemoryMeter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.http.HttpSession;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * KhanSessionManager
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionManager {

    private static Logger log = LoggerFactory.getLogger("KhanSessionManager");
    private static ConcurrentHashMap<String, KhanSessionManager> instances = new ConcurrentHashMap<String, KhanSessionManager>();
    private SessionMonitorMBean sessionMonitor;
    private String appName = "";
    private SessionStore sessionStore = null;
    private KhanSessionConfig khanSessionConfig = null;
    private boolean statsEnabled = true;
    private KhanSessionIdStore sessionIdStore = null;

    public KhanSessionManager(String appName, SessionStore store) {
        this.appName = appName;
        this.sessionStore = store;
        this.khanSessionConfig = KhanSessionFilter.getKhanSessionConfig();
        this.sessionIdStore = new KhanSessionIdStore(appName);
        registerSessionMonitor();
        instances.put(appName, this);

        if( log.isDebugEnabled() ) {
            log.debug(">>>>>>>>>> KhanSessionManager=" + this);
            log.debug(">>>>>>>>>> appName=" + appName);
        }
    }

    public static KhanSessionManager getInstance(String appName) {
        if ( instances.get(appName) == null ) {
            System.err.println("KhanSessionManager is not initialized.");
        }
        if( log.isDebugEnabled() ) {
            log.debug("KhanSessionManager/instance=" + instances.get(appName));
        }
        return instances.get(appName);
    }

    public SessionMonitorMBean getSessionMonitor() {
        return this.sessionMonitor;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getSessionIdCount() {
        if( log.isDebugEnabled() ) {
            log.debug("getSessionCount/size=" + sessionIdStore.getSessionStore(appName).size());
        }
        return (long) sessionIdStore.getSessionStore(appName).size();
    }

    public void cleanup() {
        ConcurrentHashMap<String, Long> sessionIds = sessionIdStore.getSessionStore(appName);
        Enumeration<String> keys = sessionIds.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String storeMetaKey = KhanSessionKeyGenerator.generate(khanSessionConfig.getNamespace(), key, KhanHttpSession.METADATA_KEY);
            String storeAttrKey = KhanSessionKeyGenerator.generate(khanSessionConfig.getNamespace(), key, KhanHttpSession.ATTRIBUTES_KEY);

            if( log.isDebugEnabled() ) {
                log.debug("key=" + key);
                log.debug("storeMetaKey=" + storeMetaKey);
                log.debug("storeAttrKey=" + storeAttrKey);
            }

            Object attr = sessionStore.get(storeAttrKey);
            Object meta = sessionStore.get(storeMetaKey);

            if( log.isDebugEnabled() ) {
                log.debug("attr,meta=" + attr + "," + meta);
            }
            if (attr == null && meta == null) {
                sessionIds.remove(key);
            }
        }
    }

//	public boolean killSession(String sessionId) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public void expireAllSessions() {
//		// TODO Auto-generated method stub
//
//	}

    public long getSessionMemorySize() {
        Enumeration<String> e = sessionIdStore.getSessionStore(appName).keys();
        long memorySize = 0;

        while (e.hasMoreElements()) {
            memorySize += (Long) sessionIdStore.getSessionStore(appName).get(e.nextElement());
        }

        return memorySize;
    }


    public long getSessionMemorySize(String sessionId) {
        long memorySize = 0;
        memorySize = (Long) sessionIdStore.getSessionStore(appName).get(sessionId);

        return memorySize;
    }

    public ArrayList<String> getSessionIds(int batchSize) {
        if( log.isDebugEnabled() ) {
            log.debug(">>>>>>>>>> sessionIdStore.getSessionStore(" + appName + ")=" + sessionIdStore.getSessionStore(appName));
        }
        Enumeration<String> e = sessionIdStore.getSessionStore(appName).keys();

        return Collections.list(e);
    }

    public Map<String, Object> getSessionAttributes(String sessionId) {
        ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
        try {
            String key = KhanSessionKeyGenerator.generate(khanSessionConfig.getNamespace(), sessionId, KhanHttpSession.ATTRIBUTES_KEY);
            //log.debug( ">>>>>>>>>>>>>>>>>>> sessionStore=" + sessionStore);
            //log.debug( ">>>>>>>>>>>>>>>>>>> key=" + key);
            attributes = sessionStore.get(key);
            //log.debug( ">>>>>>>>>>>>>>>>>>> attrs=" + attributes);
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("Exception=" + e.getMessage());
        }
        return attributes;
    }

    public void putSessionId(HttpSession session) {
        sessionIdStore.getSessionStore(appName).put(session.getId(), getSessionMemorySize(session));
        if( log.isDebugEnabled() ) {
            log.debug("addSessionId/size=" + sessionIdStore.getSessionStore(appName).size());
        }
    }

    private long getSessionMemorySize(HttpSession session) {
        long memorySize = 0;
        try {
            MemoryMeter meter = new MemoryMeter();
            memorySize += meter.measureDeep(getSessionAttributes(session.getId()));
        } catch (Exception e) {
            log.error("Session memory size calculation error", e);
        }
        return memorySize;
    }

    public void removeSessionId(HttpSession session) {
        String khanSessionId = (String) session.getAttribute("khan.session.id");
        if (khanSessionId != null) {
            sessionIdStore.getSessionStore(appName).remove(khanSessionId);
            if( log.isDebugEnabled() ) {
                log.debug("removeSessionId/size=" + sessionIdStore.getSessionStore(appName).size());
            }
        }
    }

    private void registerSessionMonitor() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            sessionMonitor = new SessionMonitorMBeanImpl(this);
            mbs.registerMBean(sessionMonitor, getJMXObjectName());
            sessionMonitor.setStatisticsEnabled(statsEnabled);
        } catch (Exception e) {
            log.warn("Unable to register SessionMonitorMBean. Statistics gathering will be disabled for '" + getAppName() + "'", e);
        }
    }

    private ObjectName getJMXObjectName() {

        try {
            String appNameStr = appName.replaceAll(":|=|\n", ".");
            appNameStr = appNameStr.replaceAll("/", "");

            StringBuilder sb = new StringBuilder(
                    "com.opennaru.khan.session:type=SessionMonitor").append(",appName=")
                    .append(appNameStr);
            return new ObjectName(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        sessionMonitor.shutdown();
    }

    @PreDestroy
    public void destroy() {
        sessionMonitor.shutdown();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            mbs.unregisterMBean(getJMXObjectName());
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        }

    }

}
