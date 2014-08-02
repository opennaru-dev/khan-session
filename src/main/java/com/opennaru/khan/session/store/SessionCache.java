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
package com.opennaru.khan.session.store;

import java.io.IOException;

/**
 * Store Interface for Session Data
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public interface SessionCache {
    public static final String DEFAULT_CACHENAME = "KHAN_SESSION";
    public static final String DEFAULT_LOGIN_CACHENAME = "KHAN_SESSION_LOGIN";

    public boolean isInitialized();

    public void initialize(String servers, String cacheName, String loginCacheName) throws IOException;

    public <T> boolean contains(String key) throws IOException;

    public <T> void put(String key, T value, long secondsToExpire) throws IOException;

    public <T> T get(String key) throws IOException;

    public <T> void delete(String key) throws IOException;

    public int size() throws IOException;


    // login cache
    public <T> boolean loginContains(String key) throws IOException;

    public <T> void loginPut(String key, T value, long secondsToExpire) throws IOException;

    public <T> T loginGet(String key) throws IOException;

    public <T> void loginDelete(String key) throws IOException;

    public int loginSize() throws IOException;

}