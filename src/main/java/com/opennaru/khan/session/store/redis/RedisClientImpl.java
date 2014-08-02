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
package com.opennaru.khan.session.store.redis;

import com.opennaru.khan.session.store.SessionCache;
import com.opennaru.khan.session.store.marshaller.KhanMarshaller;
import com.opennaru.khan.session.util.ClassUtil;
import com.opennaru.khan.session.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

/**
 * Session Store using Jedis Client
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class RedisClientImpl implements SessionCache {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private RedisServer redisServer;

    private JedisPool pool = null;

    private KhanMarshaller marshaller;

    private RedisConfigurationProperties redisProp;

    public RedisClientImpl() {

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
        return pool != null;
    }


    @Override
    public void initialize(String configFile, String cacheName, String loginCacheName)
            throws IOException {
        StringUtils.isNotNull("configFile", configFile);

        redisProp = new RedisConfigurationProperties();
        redisProp.loadProperties(configFile);

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisProp.getPoolMaxTotal());
        jedisPoolConfig.setMaxIdle(redisProp.getPoolMaxIdle());
        jedisPoolConfig.setMinIdle(redisProp.getPoolMinIdle());
        jedisPoolConfig.setTestWhileIdle(redisProp.getPoolTestWhileIdle());
        jedisPoolConfig.setJmxEnabled(redisProp.getPoolJMXEnabled());
        jedisPoolConfig.setTestOnBorrow(redisProp.getPoolTestOnBorrow());

        // URL : redis://:foobared@localhost:6380/1
//        pool = new JedisPool("redis://:password@localhost:6379/1");
        RedisServer redisServer = redisProp.getRedisServer();

        if( StringUtils.isNullOrEmpty( redisServer.getPassword() ) ) {
            pool = new JedisPool(
                jedisPoolConfig, redisServer.getHostname(), redisServer.getPort(), redisServer.getTimeout()
            );
        } else {
            pool = new JedisPool(
                jedisPoolConfig, redisServer.getHostname(), redisServer.getPort(), redisServer.getTimeout(),
                redisServer.getPassword()
            );
        }

        String marshallerClass = redisProp.getMarshaller();
        marshaller = (KhanMarshaller)ClassUtil.getInstance(marshallerClass, this.getClass().getClassLoader());

        waitForConnectionReady();
    }

    @Override
    public <T> boolean contains(String key) {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase());
            return jedis.exists(key);
        } finally {
            pool.returnResource(jedis);
        }
    }


    @Override
    public <T> void put(String key, T value, long secondsToExpire)
            throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase());

            if (jedis.exists(key)) {
                jedis.set(key.getBytes(), marshaller.objectToBytes(value), "XX".getBytes(), "EX".getBytes(), secondsToExpire);
            } else {
                jedis.set(key.getBytes(), marshaller.objectToBytes(value), "NX".getBytes(), "EX".getBytes(), secondsToExpire);
            }
        } finally {
            pool.returnResource(jedis);
        }
        //cache.put(key, value, secondsToExpire, TimeUnit.SECONDS);
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void putAndEnsure(String key, T value, long secondsToExpire)
            throws IOException {
        put(key, value, secondsToExpire);
/*
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
*/
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws IOException {
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());
        Jedis jedis = pool.getResource();
        try {

            jedis.select(redisServer.getDatabase());
            return (T) marshaller.objectFromByteBuffer(jedis.get(key.getBytes()));
        } finally {
            pool.returnResource(jedis);
        }
        //return (T) cache.get(key);
    }

    @Override
    public <T> void delete(String key) throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase());
            jedis.del(key);
        } finally {
            pool.returnResource(jedis);
        }
//        cache.remove(key);
        //logger.debug("@@@@@@@@@@@@@ cache.size=" + cache.size());
    }

    @Override
    public int size() throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase());
            if (log.isDebugEnabled()) {
                log.debug("sizeof=" + jedis.dbSize());
            }
            return (jedis.dbSize()).intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public <T> boolean loginContains(String key) throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase()+1);
            return jedis.exists(key);
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public <T> void loginPut(String key, T value, long secondsToExpire)
            throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase()+1);
            if (jedis.exists(key)) {
                jedis.set(key.getBytes(), marshaller.objectToBytes(value), "XX".getBytes(), "EX".getBytes(), secondsToExpire);
            } else {
                jedis.set(key.getBytes(), marshaller.objectToBytes(value), "NX".getBytes(), "EX".getBytes(), secondsToExpire);
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public <T> T loginGet(String key) throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase()+1);
            return (T) marshaller.objectFromByteBuffer(jedis.get(key.getBytes()));
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public <T> void loginDelete(String key) throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase()+1);
            jedis.del(key.getBytes());
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int loginSize() throws IOException {
        Jedis jedis = pool.getResource();
        try {
            jedis.select(redisServer.getDatabase()+1);
            if (log.isDebugEnabled()) {
                log.debug("sizeof=" + jedis.dbSize());
            }
            return (jedis.dbSize()).intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

}