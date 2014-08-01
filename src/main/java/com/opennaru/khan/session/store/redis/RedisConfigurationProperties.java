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

import com.opennaru.khan.session.store.marshaller.JBossMarshaller;
import org.infinispan.commons.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jjeon on 14. 8. 1.
 */
public class RedisConfigurationProperties {
    public static final String REDIS_SERVER_URL = "redis.client.server_url";
    public static final String MARSHALLER = "redis.client.marshaller";
    public static final String POOL_MAX_TOTAL = "redis.client.pool.maxTotal";
    public static final String POOL_MAX_IDLE = "redis.client.pool.maxIdle";
    public static final String POOL_MIN_IDLE = "redis.client.pool.minIdle";
    public static final String POOL_TEST_WHILE_IDLE = "redis.client.pool.testWhileIdle";
    public static final String POOL_TEST_ON_BORROW = "redis.client.pool.testOnBorrow";
    public static final String POOL_JMX_ENABLED = "redis.client.pool.jmxEnabled";

    private Properties properties;
    private RedisServer redisServer;

    public RedisConfigurationProperties() {
        properties = new Properties();
    }

    public void loadProperties(String configFile) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InputStream stream = cl.getResourceAsStream(configFile);

        if (stream == null) {
            System.err.println("Can't Found configFile=" + configFile);
        } else {
            try {
                properties.load(stream);
            } finally {
                Util.close(stream);
            }
        }
    }

    public RedisServer getRedisServer() {
        String redisServerUrl = properties.getProperty(REDIS_SERVER_URL, "redis://:@localhost:6379/1");

        redisServer = new RedisServer();
        redisServer.parseUrl(redisServerUrl);

        return  redisServer;
    }

    public String getMarshaller() {
        return properties.getProperty(MARSHALLER, JBossMarshaller.class.getName() );
    }

    public int getPoolMaxTotal() {
        return Integer.parseInt( properties.getProperty(POOL_MAX_TOTAL, "100") );
    }

    public int getPoolMaxIdle() {
        return Integer.parseInt( properties.getProperty(POOL_MAX_IDLE, "20") );
    }

    public int getPoolMinIdle() {
        return Integer.parseInt( properties.getProperty(POOL_MIN_IDLE, "10") );
    }

    public boolean getPoolTestWhileIdle() {
        return Boolean.parseBoolean(properties.getProperty(POOL_TEST_WHILE_IDLE, "true"));
    }

    public boolean getPoolTestOnBorrow() {
        return Boolean.parseBoolean( properties.getProperty(POOL_TEST_ON_BORROW, "true") );
    }

    public boolean getPoolJMXEnabled() {
        return Boolean.parseBoolean( properties.getProperty(POOL_JMX_ENABLED, "true") );
    }

}

