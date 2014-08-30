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
 * Infinispan Library mode의 세션 저장소 구현
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class InfinispanLibrayImpl implements SessionCache {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * library mode cache manager
     */
    private DefaultCacheManager cacheManager;
    /**
     * Session cache
     */
    private Cache<Object, Object> cache;
    /**
     * login session cache
     */
    private Cache<Object, Object> loginCache;

    /**
     * CacheName
     */
    private String cacheName = SessionCache.DEFAULT_CACHENAME;

    /**
     * Login Cache Name
     */
    private String loginCacheName = SessionCache.DEFAULT_LOGIN_CACHENAME;

    /**
     * Default Constructor
     */
    public InfinispanLibrayImpl() {

    }

    /**
     * Wait
     */
    void waitForConnectionReady() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 초기화되었는지 체크
     * @return
     */
    @Override
    public boolean isInitialized() {
        return cacheManager != null;
    }

    /**
     * 초기화
     * @param configFile
     * @param cacheName
     * @param loginCacheName
     * @throws IOException
     */
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

    /**
     * 캐시에 세션 키 값을 가지고 있는지 체크
     * @param key
     * @param <T>
     * @return
     */
    @Override
    public <T> boolean contains(String key) {
        return cache.containsKey(key);
    }

    /**
     * 캐시에 추가
     * @param key
     * @param value
     * @param secondsToExpire
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> void put(String key, T value, long secondsToExpire)
            throws IOException {
        cache.put(key, value, secondsToExpire, TimeUnit.SECONDS, secondsToExpire, TimeUnit.SECONDS);

        if( log.isTraceEnabled() )
            log.trace("@@@@@@@@@@@@@ cache.size=" + cache.size());
    }

    /**
     * 캐시에서 가져옴
     * @param key
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws IOException {
        if( log.isTraceEnabled() )
            log.trace("@@@@@@@@@@@@@ cache.size=" + cache.size());

        return (T) cache.get(key);
    }

    /**
     * 캐시에서 삭제
     * @param key
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> void delete(String key) throws IOException {
        cache.remove(key);

        if( log.isTraceEnabled() )
            log.trace("@@@@@@@@@@@@@ cache.size=" + cache.size());
    }

    /**
     * 캐시에 저장된 세션의 개수
     * @return
     * @throws IOException
     */
    @Override
    public int size() throws IOException {
        if( log.isTraceEnabled() )
            log.trace("sizeof=" + cache.size());

        return cache.size();
    }

    /**
     * 로그인 정보를 가지고 있는지 체크
     * @param key
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> boolean loginContains(String key) throws IOException {
        return loginCache.containsKey(key);
    }

    /**
     * 캐시에 로그인 정보 추가
     * @param key
     * @param value
     * @param secondsToExpire
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> void loginPut(String key, T value, long secondsToExpire)
            throws IOException {
        loginCache.put(key, value, secondsToExpire, TimeUnit.SECONDS, secondsToExpire, TimeUnit.SECONDS);
    }

    /**
     * 캐시에서 로그인 정보를 가져온다.
     * @param key
     * @param <T>
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T loginGet(String key) throws IOException {
        return (T) loginCache.get(key);
    }

    /**
     * 캐시에서 로그인 정보를 삭제
     * @param key
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> void loginDelete(String key) throws IOException {
        loginCache.remove(key);
    }

    /**
     *  로그인 정보의 갯수
     * @return
     * @throws IOException
     */
    @Override
    public int loginSize() throws IOException {
        if( log.isTraceEnabled() )
            log.debug("sizeof=" + loginCache.size());

        return loginCache.size();
    }

}