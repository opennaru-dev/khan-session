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
package com.opennaru.khan.session.store.infinispan;

import com.opennaru.khan.session.store.SessionCache;
import com.opennaru.khan.session.util.StringUtils;
import org.infinispan.Cache;
import org.infinispan.commons.util.concurrent.FutureListener;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Session Store using Infinispan library mode
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class InfinispanLibrayImpl implements SessionCache {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DefaultCacheManager cacheManager;
    private Cache<Object, Object> cache;
    private Cache<Object, Object> loginCache;
    @SuppressWarnings("unused")
    private String cacheName = SessionCache.DEFAULT_CACHENAME;
    @SuppressWarnings("unused")
    private String loginCacheName = SessionCache.DEFAULT_LOGIN_CACHENAME;

    public InfinispanLibrayImpl() {

    }

    void waitForConnectionReady() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean isInitialized() {
        return cacheManager != null;
    }

    @Override
    public void initialize(String configFile, String cacheName, String loginCacheName)
            throws IOException {
        try {
            StringUtils.isNotNull("configFile", configFile);

            cacheManager = new DefaultCacheManager(configFile);

            this.cacheName = cacheName;
            this.loginCacheName = loginCacheName;

            cache = cacheManager.getCache(cacheName);
            loginCache = cacheManager.getCache(loginCacheName);

            waitForConnectionReady();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> boolean contains(String key) {
        return cache.containsKey(key);
    }


    @Override
    public <T> void put(String key, T value, long secondsToExpire)
            throws IOException {
        cache.put(key, value, secondsToExpire, TimeUnit.SECONDS);
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void putAndEnsure(String key, T value, long secondsToExpire)
            throws IOException {

        @SuppressWarnings("rawtypes")
        FutureListener futureListener = new FutureListener() {

            public void futureDone(Future future) {
                try {
                    future.get();
                } catch (Exception e) {
                    // Future did not complete successfully
                    log.debug("Future did not complete successfully!");
                }
            }
        };

        cache.putAsync(key, value, secondsToExpire, TimeUnit.SECONDS).attachListener(futureListener);
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws IOException {
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());
        return (T) cache.get(key);
    }

    @Override
    public <T> void delete(String key) throws IOException {
        cache.remove(key);
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());
    }

    @Override
    public int size() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("sizeof=" + cache.size());
        }
        return cache.size();
    }

    @Override
    public <T> boolean loginContains(String key) throws IOException {
        return loginCache.containsKey(key);
    }

    @Override
    public <T> void loginPut(String key, T value, long secondsToExpire)
            throws IOException {
        loginCache.put(key, value, secondsToExpire, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loginGet(String key) throws IOException {
        return (T) loginCache.get(key);
    }

    @Override
    public <T> void loginDelete(String key) throws IOException {
        loginCache.remove(key);
    }

    @Override
    public int loginSize() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("sizeof=" + loginCache.size());
        }
        return loginCache.size();
    }

}