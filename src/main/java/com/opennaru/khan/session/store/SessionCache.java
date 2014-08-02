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
    // Session Cache name key
    public static final String DEFAULT_CACHENAME = "KHAN_SESSION";
    // Login Session Cache name key
    public static final String DEFAULT_LOGIN_CACHENAME = "KHAN_SESSION_LOGIN";

    /**
     * Check if initialized
     * @return
     */
    public boolean isInitialized();

    /**
     * Initialize Session Store
     *
     * @param servers
     * @param cacheName
     * @param loginCacheName
     * @throws IOException
     */
    public void initialize(String servers, String cacheName, String loginCacheName) throws IOException;

    /**
     * Check if Session ID key is in.
     *
     * @param key
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> boolean contains(String key) throws IOException;

    /**
     * Put Session ID
     * @param key
     * @param value
     * @param secondsToExpire
     * @param <T>
     * @throws IOException
     */
    public <T> void put(String key, T value, long secondsToExpire) throws IOException;

    /**
     * Get Session Value
     *
     * @param key
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T get(String key) throws IOException;

    /**
     * Delete Session with ID
     * @param key
     * @param <T>
     * @throws IOException
     */
    public <T> void delete(String key) throws IOException;

    /**
     * Size of Session
     * @return
     * @throws IOException
     */
    public int size() throws IOException;


    // login cache

    /**
     * Check if Session ID is in login cache
     * @param key
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> boolean loginContains(String key) throws IOException;

    /**
     * Put login info into login cache
     * @param key
     * @param value
     * @param secondsToExpire
     * @param <T>
     * @throws IOException
     */
    public <T> void loginPut(String key, T value, long secondsToExpire) throws IOException;

    /**
     * Get Login Session info
     *
     * @param key
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T loginGet(String key) throws IOException;

    /**
     * Delete Login session info
     * @param key
     * @param <T>
     * @throws IOException
     */
    public <T> void loginDelete(String key) throws IOException;

    /**
     * Get Size of login session
     * @return
     * @throws IOException
     */
    public int loginSize() throws IOException;

}