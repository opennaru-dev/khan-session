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
import com.opennaru.khan.session.util.StringUtils;
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
 * KhanSessionManager MBean을 관리하는 클래스
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionManager {
    private static Logger log = LoggerFactory.getLogger("KhanSessionManager");

    /**
     * 애플리케이션 별로 SessionManager를 보관
     */
    private static ConcurrentHashMap<String, KhanSessionManager> instances = new ConcurrentHashMap<String, KhanSessionManager>();

    /**
     * 세션 모니터링 MBean
     */
    private SessionMonitorMBean sessionMonitor;

    /**
     *  WebApp의 이름
     */
    private String appName = "";

    /**
     * 세션 저장소
     */
    private SessionStore sessionStore = null;

    /**
     * KHAN 세션 설정
     */
    private KhanSessionConfig khanSessionConfig = null;

    /**
     * 통계 정보 수집 여부
     */
    private boolean statsEnabled = true;

    /**
     * 세션 ID 저장소
     */
    private KhanSessionIdStore sessionIdStore = null;

    /**
     * Constructor
     *
     * @param appName
     */
    public KhanSessionManager(String appName) {
        this.appName = appName;
        this.khanSessionConfig = KhanSessionFilter.getKhanSessionConfig();
        this.sessionIdStore = new KhanSessionIdStore(appName);

        statsEnabled = khanSessionConfig.isEnableStatistics();

        registerSessionMonitor();

        instances.put(appName, this);

        log.info("KHAN [session manager] initialized.");

        if( log.isDebugEnabled() ) {
            log.debug(">>>>>>>>>> KhanSessionManager=" + this);
            log.debug(">>>>>>>>>> appName=" + appName);
        }
    }

    /**
     * set SessionStore
     * @param sessionStore
     */
    public void setSessionStore(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    /**
     * Singleton Instance
     *
     * @param appName
     * @return
     */
    public static KhanSessionManager getInstance(String appName) {
        if ( instances.get(appName) == null ) {
            System.err.println("KhanSessionManager is not initialized.");
        }
        if( log.isDebugEnabled() ) {
            log.debug("KhanSessionManager/instance=" + instances.get(appName));
        }
        return instances.get(appName);
    }

    /**
     * get SessionMonitorMBean
     *
     * @return
     */
    public SessionMonitorMBean getSessionMonitor() {
        return this.sessionMonitor;
    }

    /**
     * Returen name of application(webapp)
     *
     * @return
     */
    public String getAppName() {
        return this.appName;
    }

    /**
     * Set name of application(webapp)
     *
     * @param appName
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * return number of sessions
     *
     * @return
     */
    public long getSessionIdCount() {
        if( statsEnabled ) {
            if (log.isDebugEnabled()) {
                log.debug("getSessionCount/size=" + sessionIdStore.getSessionStore(appName).size());
            }
            return (long) sessionIdStore.getSessionStore(appName).size();
        } else {
            return 0;
        }
    }

    /**
     * Cleanup all sessions
     *
     */
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
        if( statsEnabled ) {
            Enumeration<String> e = sessionIdStore.getSessionStore(appName).keys();
            long memorySize = 0;

            while (e.hasMoreElements()) {
                memorySize += sessionIdStore.getSessionStore(appName).get(e.nextElement());
            }

            return memorySize;
        } else {
            return 0;
        }
    }

    /**
     * 특정 세션에 대한 메모리 사이즈 반환
     *
     * @param sessionId
     * @return
     */
    public long getSessionMemorySize(String sessionId) {
        if( statsEnabled ) {
            return sessionIdStore.getSessionStore(appName).get(sessionId);
        } else {
            return 0;
        }
    }

    /**
     * 세션 ID들을 String Array로 반환
     * @param batchSize
     * @return
     */
    public ArrayList<String> getSessionIds(int batchSize) {
        if( statsEnabled ) {
            if (log.isDebugEnabled()) {
                log.debug(">>>>>>>>>> sessionIdStore.getSessionStore(" + appName + ")=" + sessionIdStore.getSessionStore(appName));
            }
            Enumeration<String> e = sessionIdStore.getSessionStore(appName).keys();

            return Collections.list(e);
        } else {
            return null;
        }
    }

    /**
     * 세션 ID에 대한 세션 속성 반환
     * @param sessionId
     * @return
     */
    public Map<String, Object> getSessionAttributes(String sessionId) {
        ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
        try {
            if( !StringUtils.isNullOrEmpty(sessionId) ) {
                String key = KhanSessionKeyGenerator.generate(khanSessionConfig.getNamespace(), sessionId, KhanHttpSession.ATTRIBUTES_KEY);
//                System.out.println( ">>>>>>>>>>>>>>>>>>> sessionStore=" + sessionStore);
//                System.out.println( ">>>>>>>>>>>>>>>>>>> key=" + key);
                attributes = sessionStore.get(key);
//                System.out.println( ">>>>>>>>>>>>>>>>>>> attrs=" + attributes);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("Exception=" + e.getMessage(), e);
        }
        return attributes;
    }

    /**
     * SessionIdStore에 세션 ID와 메모리 점유 사이즈를 저장
     *
     * @param session
     */
    public void putSessionId(HttpSession session) {
        if( statsEnabled )
            sessionIdStore.getSessionStore(appName).put(session.getId(), getSessionMemorySize(session));

        if( log.isDebugEnabled() ) {
            log.debug("addSessionId/size=" + sessionIdStore.getSessionStore(appName).size());
        }
    }

    /**
     * SessionIdStorea에서 세션 ID에 대한 정보를 제거
     *
     * @param session
     */
    public void removeSessionId(HttpSession session) {
        if ( statsEnabled == false )
            return;

        String khanSessionId = (String) session.getAttribute("khan.session.id");
        if ( !StringUtils.isNullOrEmpty(khanSessionId) ) {
            sessionIdStore.getSessionStore(appName).remove(khanSessionId);
            if( log.isDebugEnabled() ) {
                log.debug("removeSessionId/size=" + sessionIdStore.getSessionStore(appName).size());
            }
        }
    }

    /**
     * HttpSession 객체에 대한 메모리 점유 사이즈를 계산
     *
     * @param session
     * @return
     */
    private long getSessionMemorySize(HttpSession session) {
        if( statsEnabled == false )
            return 0;
        else if( khanSessionConfig.isEnableMemoryStatistics() == false )
            return 0;

        // TODO : agent를 설정하지 않을 경우를 체크해야 함
        long memorySize = 0;
        try {
            MemoryMeter meter = new MemoryMeter();
            memorySize += meter.measureDeep(getSessionAttributes(session.getId()));
        } catch (Exception e) {
            log.error("Session memory size calculation error");
        }
        return memorySize;
    }

    /**
     * Register Session Monitor MBean
     */
    private void registerSessionMonitor() {
        if( statsEnabled ) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                sessionMonitor = new SessionMonitorMBeanImpl(this);
                mbs.registerMBean(sessionMonitor, getJMXObjectName());
                sessionMonitor.setStatisticsEnabled(statsEnabled);
            } catch (Exception e) {
                log.warn("Unable to register SessionMonitorMBean. Statistics gathering will be disabled for '" + getAppName() + "'", e);
            }
        }
    }

    /**
     * JMX ObjectName
     * @return
     */
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

    /**
     * Stop
     */
    public void stop() {
        sessionMonitor.shutdown();
    }

    /**
     * Destroy
     */
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
