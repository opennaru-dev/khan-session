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


import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class TestJedis {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testSequential();
        testParallel();
    }

    public static void testSequential() throws InterruptedException {
        final Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.set("abc", Long.toString(0));
        for (int i = 0; i < 500; i++) {
            jedis.incrBy("abc", i);
        }

        System.out.println("Done: " + jedis.get("abc"));
    }

    /**
     * Exception in thread "main" java.util.concurrent.ExecutionException: redis.clients.jedis.exceptions.JedisConnectionException: It seems like server has closed the connection.

     */
    public static void testParallel() throws ExecutionException, InterruptedException {
        final Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.set("abc", "0");
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        List<Future<Runnable>> futureList = new ArrayList<Future<Runnable>>();
        for (final AtomicInteger i = new AtomicInteger(); i.get() < 500; i.getAndIncrement()) {
            Future future = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    jedis.incrBy("abc", i.get());
                    System.out.println("Incremented by " + Integer.toString(i.get()));
                }
            });
            futureList.add(future);
        }

        for (Future<Runnable> future : futureList) {
            future.get();
        }

        System.out.println("Done: " + jedis.get("abc"));
    }

    //jedis.quit();
}