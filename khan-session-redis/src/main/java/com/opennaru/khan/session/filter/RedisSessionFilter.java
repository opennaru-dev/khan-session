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
package com.opennaru.khan.session.filter;

import com.opennaru.khan.session.store.SessionCache;
import com.opennaru.khan.session.store.SessionStoreImpl;
import com.opennaru.khan.session.store.redis.RedisClientImpl;
import com.opennaru.khan.session.util.StringUtils;
import com.opennaru.khan.session.util.VersionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * KHAN SessionFilter for Redis
 *
 * @since 1.1.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class RedisSessionFilter extends KhanSessionFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Get name of redis Config file from web.xml
     * @param config
     * @return
     */
    protected String getRedisConfigFile(FilterConfig config) {
        String configFile = getConfigValue(config, Constants.REDIS_CONFIGFILE_KEY);
        if (log.isDebugEnabled()) {
            log.debug("######### configFile=" + configFile);
        }
        StringUtils.isNotNull(Constants.REDIS_CONFIGFILE_KEY, configFile);
        return configFile;
    }

    /**
     * Initialize Session Filter
     * @param config
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);

        // get Redis config file
        String configFileName = getRedisConfigFile(config);

        try {
            SessionCache sessionCache = null;

            sessionCache = new RedisClientImpl();
            sessionCache.initialize(configFileName, "", "");

            sessionStore = new SessionStoreImpl(sessionCache);
            sessionManager.setSessionStore(sessionStore);
        } catch (Exception e) {
            throw new IllegalStateException("Failed", e);
        }

        System.out.println("KHAN [session manager] Redis filter initialized.");
        System.out.println(VersionUtil.getVersion("KHAN-session-redis"));
    }

}
