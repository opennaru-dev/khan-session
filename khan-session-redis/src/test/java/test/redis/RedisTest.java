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

package test.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.net.URI;

/**
 * Created by jjeon on 14. 7. 31.
 */
public class RedisTest {

    @Test
    public void testSetKey() {
        Jedis jedis = new Jedis("localhost");
        jedis.select(1);

        jedis.set("foo", "bar");

        jedis.set("test", "test11", "NX", "EX", 10 );
        System.out.println("foo=" + jedis.get("foo"));
        System.out.println("test=" + jedis.get("test"));

    }

    @Test
    public void testSetUpdateKey() {
        Jedis jedis = new Jedis("localhost");
        jedis.select(1);

        if( jedis.exists("test") ) {
            jedis.set("test", "test12", "XX", "EX", 10);
        } else {
            jedis.set("test", "test12", "NX", "EX", 10);
        }
        System.out.println("test=" + jedis.get("test"));
    }


    @Test
    public void testGetKey() {
        Jedis jedis = new Jedis("localhost");
        jedis.select(1);
        System.out.println(jedis.get("test"));
    }

    @Test
    public void testDelKey() {
        Jedis jedis = new Jedis("localhost");
        jedis.select(1);
        jedis.del("test");
        jedis.del("foo");
    }

//    @Test
//    public void testURL() {
//
//        String host = "redis://localhost:6379/1";
//        URI uri = URI.create(host);
//        if (uri.getScheme() != null && uri.getScheme().equals("redis")) {
//            String h = uri.getHost();
//            int port = uri.getPort();
//            String password = uri.getUserInfo().split(":", 2)[1];
//            int database = Integer.parseInt(uri.getPath().split("/", 2)[1]);
//
//            System.out.println("h=" + h);
//            System.out.println("port=" + port);
//            System.out.println("password=" + password );
//            System.out.println("database=" + database );
//        }
//    }
}
