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
package com.opennaru.khan.session.management;

import com.opennaru.khan.counter.*;
import com.opennaru.khan.session.manager.KhanSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SessionMonitorMBeanImpl, 세션 정보에 대한 MBean 구현체
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class SessionMonitorMBeanImpl extends StandardMBean implements
        SessionMonitorMBean {

    private static final int DEFAULT_HISTORY_SIZE = 30;
    private static final int DEFAULT_INTERVAL_SECS = 1;
    private final KhanSessionManager sessionManager;
    private final AtomicLong duplicatedLogin;
    private final Counter duplicatedLoginStatistic;
    private final SampledStatistic duplicatedLoginSampled;
    private final AtomicLong sessionsCreated;
    private final Counter sessionsCreatedStatistic;
    private final SampledStatistic sessionsCreatedSampled;
    private final AtomicLong sessionsDestroyed;
    private final Counter sessionsDestroyedStatistic;
    private final SampledStatistic sessionsDestroyedSampled;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private boolean statisticsEnabled = false;
    private volatile SampledStatisticManager samplingManager;

    /**
     * SessionMBeanImpl
     *
     * @throws javax.management.NotCompliantMBeanException
     */
    public SessionMonitorMBeanImpl(KhanSessionManager sessionManager)
            throws NotCompliantMBeanException {
        super(SessionMonitorMBean.class);

        this.sessionManager = sessionManager;

        samplingManager = new SampledStatisticManager();

        sessionsCreatedStatistic = new SimpleCounterImpl();
        sessionsCreatedSampled = createSampledStatistic(sessionsCreatedStatistic);
        sessionsCreated = new AtomicLong();

        duplicatedLoginStatistic = new SimpleCounterImpl();
        duplicatedLoginSampled = createSampledStatistic(duplicatedLoginStatistic);
        duplicatedLogin = new AtomicLong();

        sessionsDestroyedStatistic = new SimpleCounterImpl();
        sessionsDestroyedSampled = createSampledStatistic(sessionsDestroyedStatistic);
        sessionsDestroyed = new AtomicLong();

        if (log.isDebugEnabled()) {
            log.debug("Session Monitor MBean Constructed");
        }
    }

    public void shutdown() {
        this.samplingManager.shutdown();
    }

    private SampledStatistic createSampledStatistic(Statistic statistic) {
        return samplingManager.createSampler(statistic, DEFAULT_INTERVAL_SECS,
                DEFAULT_HISTORY_SIZE, true);
    }

    public String getAppName() {
        return sessionManager.getAppName();
    }

    public synchronized boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    /**
     * Set Statistics Enabled
     *
     * @param enabled
     */
    public void setStatisticsEnabled(boolean enabled) {
        boolean oldValue = isStatisticsEnabled();
        if (oldValue != enabled) {
            synchronized (this) {
                this.statisticsEnabled = enabled;
                if (enabled) {
                    reset();
                }
            }
        }
    }

    /**
     * Increase Duplicate Login Count
     */
    public void duplicatedLogin() {
        if (isStatisticsEnabled()) {
            synchronized (this) {
                duplicatedLogin.incrementAndGet();
                duplicatedLoginStatistic.increment();
            }
        }
    }

    /**
     * Get duplicate login count
     * @return
     */
    public long getDuplicatedLoginCount() {
        return duplicatedLogin.get();
    }

    /**
     * Duplicated Login count sampling
     * @return
     */
    public long getDuplicatedLoginRateMostRecentSample() {
        return duplicatedLoginSampled.getMostRecentSample().getValue();
    }

    /**
     * Get created Session count
     * @return
     */
    public long getCreatedSessionCount() {
        return sessionsCreated.get();
    }

    /**
     * Get created session count sampling
     * @return
     */
    public long getCreatedSessionRateMostRecentSample() {
        return sessionsCreatedSampled.getMostRecentSample().getValue();
    }

    /**
     * Get destroyed session count
     * @return
     */
    public long getDestroyedSessionCount() {
        return sessionsDestroyed.get();
    }

    /**
     * Get destroyed session count sampling
     * @return
     */
    public long getDestroyedSessionRateMostRecentSample() {
        return sessionsDestroyedSampled.getMostRecentSample().getValue();
    }

    /**
     * Get active session count
     * @return
     */
    public synchronized long getActiveSessionCount() {
        return sessionManager.getSessionIdCount();
    }

    /**
     * Get sampling data :
     *      Session created count per seconds
     *      Session destroyed count per seconds
     *      Duplicated login count per seconds
     * @return
     */
    public Map<String, Long> getPerformanceMetrics() {
        Map<String, Long> result = new HashMap<String, Long>();
        result.put("SessionsCreatedPerSecond",
                getCreatedSessionRateMostRecentSample());
        result.put("SessionsDestroyedPerSecond",
                getDestroyedSessionRateMostRecentSample());
        result.put("DuplicatedLoginPerSecond",
                getDuplicatedLoginRateMostRecentSample());

        return result;
    }

    /**
     * Reset all statistics
     */
    public synchronized void reset() {
        sessionsCreatedStatistic.getAndReset();
        sessionsCreated.set(0);
        sessionsDestroyedStatistic.getAndReset();
        sessionsDestroyed.set(0);
        duplicatedLoginStatistic.getAndReset();
        duplicatedLogin.set(0);

    }

    /**
     * Increase session created count
     */
    public void sessionCreated() {
        if (isStatisticsEnabled()) {
            synchronized (this) {
                sessionsCreated.incrementAndGet();
                sessionsCreatedStatistic.increment();
            }
        }
    }

    /**
     * Increase session destroyed count
     */
    public void sessionDestroyed() {
        if (isStatisticsEnabled()) {
            synchronized (this) {
                sessionsDestroyed.incrementAndGet();
                sessionsDestroyedStatistic.increment();
            }
        }
    }

    /**
     * Get list of session id
     * @param batchSize
     * @return
     */
    public ArrayList<String> getSessionIds(int batchSize) {
        return sessionManager.getSessionIds(batchSize);
    }

    /**
     * get session attributes
     * @param sessionId
     * @return
     */
    public Map<String, Object> getSessionAttributes(String sessionId) {
        return sessionManager.getSessionAttributes(sessionId);
    }

    /**
     * get session id count
     * @return
     */
    public long getSessionIdCount() {
        return sessionManager.getSessionIdCount();
    }

    /**
     * get memory size of sessions
     * @return
     */
    public long getMemorySize() {
        return sessionManager.getSessionMemorySize();
    }

    /**
     * Get occupied memory size of sessionId
     * @param sessionId
     * @return
     */
    public long getMemorySize(String sessionId) {
        return sessionManager.getSessionMemorySize(sessionId);
    }

}