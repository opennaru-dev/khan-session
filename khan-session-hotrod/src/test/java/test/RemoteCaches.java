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

package test;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import java.util.Iterator;
import java.util.Set;

/**
 * Remote Cache 데이터 조회 테스트
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class RemoteCaches {
    public RemoteCaches() {

    }

    public void test() {

        RemoteCacheManager cacheManager = new RemoteCacheManager("192.168.0.58:11322;192.168.0.58:11422", true);
        RemoteCache<Object, Object> cache = cacheManager.getCache("KHAN_SESSION");

        Set<Object> keySet = cache.keySet();

        Iterator<Object> i = keySet.iterator();
        System.out.println("============= KHAN_SESSION");
        while (i.hasNext()) {
            Object key = i.next();
            System.out.println("> key=" + key);
            Object value = cache.get(key);
            System.out.println("> value=" + value);
            System.out.println("");
        }
        System.out.println("=============");
    }


    public void test2() {
        RemoteCacheManager cacheManager = new RemoteCacheManager("192.168.0.58:11322;192.168.0.58:11422", true);
        RemoteCache<Object, Object> cache = cacheManager.getCache("KHAN_SESSION_LOGIN");

        Set<Object> keySet = cache.keySet();

        Iterator<Object> i = keySet.iterator();
        System.out.println("============= KHAN_SESSION_LOGIN");
        while (i.hasNext()) {
            Object key = i.next();
            System.out.println("> key=" + key);
            Object value = cache.get(key);
            System.out.println("> value=" + value);
            System.out.println("");
        }
        System.out.println("=============");
    }

    public void testRemoteCache() {
        RemoteCacheManager cacheManager = new RemoteCacheManager("192.168.0.58:11422;192.168.0.58:11322", true);
        RemoteCache<Object, Object> cache = cacheManager.getCache("KHAN_SESSION_REMOTE");

        Set<Object> keySet = cache.keySet();

        Iterator<Object> i = keySet.iterator();
        System.out.println("============= KHAN_SESSION_REMOTE");
        while (i.hasNext()) {
            Object key = i.next();
            System.out.println("> key=" + key);
            Object value = cache.get(key);
            System.out.println("> value=" + value);
            System.out.println("");
        }
        System.out.println("=============");
    }

    public void testRemoteLoginCache() {
        RemoteCacheManager cacheManager = new RemoteCacheManager("192.168.0.58:11422;192.168.0.58:11322", true);
        RemoteCache<Object, Object> cache = cacheManager.getCache("KHAN_SESSION_LOGIN_REMOTE");

        Set<Object> keySet = cache.keySet();

        Iterator<Object> i = keySet.iterator();
        System.out.println("============= KHAN_SESSION_LOGIN_REMOTE");
        while (i.hasNext()) {
            Object key = i.next();
            System.out.println("> key=" + key);
            Object value = cache.get(key);
            System.out.println("> value=" + value);
            System.out.println("");
        }
        System.out.println("=============");
    }

    public static void main(String[] args) {
        RemoteCaches remoteCaches = new RemoteCaches();
        remoteCaches.test();
        remoteCaches.test2();

        remoteCaches.testRemoteCache();
        remoteCaches.testRemoteLoginCache();
    }
}
