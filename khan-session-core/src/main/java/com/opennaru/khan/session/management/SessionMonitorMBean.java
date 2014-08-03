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

import java.util.ArrayList;
import java.util.Map;

/**
 * SessionMonitorMBean
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public interface SessionMonitorMBean {
    /**
     * TODO : 설정에서 통계정보를 수집할 것인지 결정하도록 변경해야 함
     */
    public static final String STATISTICS_ENABLED = "statisticsEnabled";

    public String getAppName();

    public boolean isStatisticsEnabled();

    public void setStatisticsEnabled(boolean enabled);

    public ArrayList<String> getSessionIds(int batchSize);

    public Map<String, Object> getSessionAttributes(String sessionId);

    /**
     * Reset sampling
     */
    public void reset();

    /**
     * Event indicating that a session was created.
     */
    public void sessionCreated();

    /**
     * Event indicating that a session was destroyed.
     */
    public void sessionDestroyed();


    /**
     * Event indicating that a session was already logined.
     */
    public void duplicatedLogin();

    /**
     * @return Duplicated Login count
     */
    public long getDuplicatedLoginCount();

    /**
     * @return Duplicated Login count sampling
     */
    public long getDuplicatedLoginRateMostRecentSample();


    /**
     * Event indicating that a session hopped to another node.
     */
    public long getActiveSessionCount();

    /**
     * @return Sessions created sampling
     */
    public long getCreatedSessionCount();

    /**
     * @return Session creation rate in sample
     */
    public long getCreatedSessionRateMostRecentSample();

    /**
     * @return Sessions destroyed in sample
     */
    public long getDestroyedSessionCount();

    /**
     * @return Sessions destroyed rate in sample
     */
    public long getDestroyedSessionRateMostRecentSample();


    /**
     * @return map of performance metrics: SessionsCreatedPerSecond,
     * SessionsDestroyedPerSecond, RequestsPerSecond. DuplicatedLoginPerSecond
     */
    public Map<String, Long> getPerformanceMetrics();

    /**
     * Get count of Session Id
     * @return
     */
    public long getSessionIdCount();

    /**
     * Get Memory Size of Total Sessions
     * @return
     */
    public long getMemorySize();

    /**
     * get memory size of specified sessionId
     * @param sessionId
     * @return
     */
    public long getMemorySize(String sessionId);

    public void shutdown();
}