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
import com.opennaru.khan.session.store.infinispan.InfinispanHotRodImpl;
import com.opennaru.khan.session.util.StringUtils;
import com.opennaru.khan.session.util.VersionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * Session filter for Infinispan HotRod Client
 *
 * @since 1.1.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class InfinispanHotRodSessionFilter extends KhanSessionFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Return Infinispan config file
     * @param config
     * @return
     */
    protected String getInfinispanConfigFile(FilterConfig config) {
        String configFile = getConfigValue(config, Constants.INFINISPAN_CONFIGFILE_KEY);
        if (log.isDebugEnabled()) {
            log.debug("######### configFile=" + configFile);
        }
        StringUtils.isNotNull(Constants.INFINISPAN_CONFIGFILE_KEY, configFile);
        return configFile;
    }

    /**
     * Initialize Infinispan HotRod client Session Filter
     *
     * @param config
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);

        // get infinspan config file
        String configFileName = getInfinispanConfigFile(config);

        String cacheName = getConfigValue(config, Constants.INFINISPAN_CACHE_KEY);
        if ( StringUtils.isNullOrEmpty(cacheName) ) {
            cacheName = SessionCache.DEFAULT_CACHENAME;
        }

        String loginCacheName = getConfigValue(config, Constants.INFINISPAN_LOGIN_CACHE_KEY);
        if ( StringUtils.isNullOrEmpty(loginCacheName) ) {
            loginCacheName = SessionCache.DEFAULT_LOGIN_CACHENAME;
        }

        try {
            SessionCache sessionCache = null;

            sessionCache = new InfinispanHotRodImpl();
            sessionCache.initialize(configFileName, cacheName, loginCacheName);

            sessionStore = new SessionStoreImpl(sessionCache);
            sessionManager.setSessionStore(sessionStore);
        } catch (Exception e) {
            throw new IllegalStateException("Failed", e);
        }

        log.info("KHAN [session manager] Infinispan Hotrod filter initialized.");
        log.info(VersionUtil.getVersion("KHAN-session-hotrod"));

    }

}
