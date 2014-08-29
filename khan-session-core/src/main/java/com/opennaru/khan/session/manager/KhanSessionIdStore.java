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
package com.opennaru.khan.session.manager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * KhanSessionIdStore : Manage SessionIds
 * 세션 ID에 대한 그 메모리 크기를 저장
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionIdStore {
    /**
     * Session ID Store, 세션의 ID만 저장된다.
     */
    private ConcurrentHashMap<String, Object> sessionStore = new ConcurrentHashMap<String, Object>();

    /**
     * Constructor
     * @param namespace
     */
    public KhanSessionIdStore(String namespace) {
        ConcurrentHashMap<String, Long> sessionIds = new ConcurrentHashMap<String, Long>();
        sessionStore.put(namespace, sessionIds);
    }

    /**
     * SessionStore를 반환
     * @param namespace
     * @return
     */
    public ConcurrentHashMap<String, Long> getSessionStore(String namespace) {
        return (ConcurrentHashMap<String, Long>) sessionStore.get(namespace);
    }
}
