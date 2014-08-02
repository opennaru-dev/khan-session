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

import java.io.Serializable;

/**
 * Session Store Interface
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public interface SessionStore {

    /**
     * Check if Session ID is in cache
     * @param key
     * @return
     */
    public boolean contains(String key);

    /**
     * Get Session ID
     * @param key
     * @param <V>
     * @return
     */
    public <V extends Serializable> V get(String key);

    /**
     * Put Session ID
     * @param key
     * @param value
     * @param expire
     * @param <V>
     */
    public <V extends Serializable> void put(String key, V value, long expire);

    /**
     * Remove Session ID
     * @param key
     */
    public void remove(String key);

    /**
     * Get number of Session
     * @return
     */
    public int size();


    /**
     * Check if login session is in
     * @param key
     * @return
     */
    public boolean loginContains(String key);

    /**
     * Get login session object
     * @param key
     * @param <V>
     * @return
     */
    public <V extends Serializable> V loginGet(String key);

    /**
     * Put login session object
     * @param key
     * @param value
     * @param expire
     * @param <V>
     */
    public <V extends Serializable> void loginPut(String key, V value, long expire);

    /**
     * remove login session
     * @param key
     */
    public void loginRemove(String key);

    /**
     * return number of login sessions
     * @return
     */
    public int loginSize();
}
