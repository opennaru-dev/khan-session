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

import com.opennaru.khan.session.util.StackTraceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Session Store Implementation
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class SessionStoreImpl implements SessionStore {

    private final SessionCache sessionCache;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SessionStoreImpl(SessionCache sessionCache1) {
        this.sessionCache = sessionCache1;
    }

    /**
     * Check if Session ID is in cache
     * @param key
     * @return
     */
    @Override
    public boolean contains(String key) {
        try {
            return sessionCache.contains(key);
        } catch (Exception e) {
            log.debug("Failed to get value for " + key, e);
            return false;
        }
    }

    /**
     * Get Session ID
     * @param key
     * @param <V>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <V extends Serializable> V get(String key) {
        try {
            V value = (V) sessionCache.get(key);

            if (log.isTraceEnabled()) {
                Throwable t = new Throwable();
                String message = ">>> GET [" + key + " -> " + value + "]";
                log.trace(message + StackTraceUtil.getStackTrace(t));
            }
            return value;

        } catch (Exception e) {
            log.debug("Failed to get value for " + key, e);
            return null;
        }
    }

    /**
     *  Put Session ID
     * @param key
     * @param value
     * @param expire
     * @param <V>
     */
    @Override
    public <V extends Serializable> void put(String key, V value, long expire) {

        if (log.isTraceEnabled()) {
            Throwable t = new Throwable();
            String message = ">>> SET (expire:" + expire + ") [" + key + " -> "
                    + value + "]";
            log.trace(message + StackTraceUtil.getStackTrace(t));
        }

        try {
            if (value == null) {
                sessionCache.delete(key);
            } else {
                sessionCache.put(key, value, expire);
            }
        } catch (Exception e) {
            log.debug("Failed to set value for " + key, e);
        }
    }

    /**
     * Remove Session ID
     * @param key
     */
    @Override
    public void remove(String key) {

        if (log.isTraceEnabled()) {
            Throwable t = new Throwable();
            String message = ">>> DELETE: [" + key + "]";
            log.trace(message + StackTraceUtil.getStackTrace(t));
        }

        try {
            sessionCache.delete(key);
        } catch (Exception e) {
            log.debug("Failed to delete value for " + key, e);
        }
    }

    /**
     * Get number of Session
     * @return
     */
    @Override
    public int size() {
        int size = 0;
        try {
            size = sessionCache.size();
        } catch (Exception e) {
            log.debug("Failed to get size of session", e);
        }
        return size;
    }

    /**
     * Check if login session is in
     * @param key
     * @return
     */
    @Override
    public boolean loginContains(String key) {
        try {
            return sessionCache.loginContains(key);
        } catch (Exception e) {
            log.debug("Failed to get value for " + key, e);
            return false;
        }
    }

    /**
     * Get login session object
     * @param key
     * @param <V>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <V extends Serializable> V loginGet(String key) {
        try {
            V value = (V) sessionCache.loginGet(key);

            if (log.isTraceEnabled()) {
                Throwable t = new Throwable();
                String message = ">>> GET [" + key + " -> " + value + "]";
                log.trace(message + StackTraceUtil.getStackTrace(t));
            }
            return value;

        } catch (Exception e) {
            log.debug("Failed to get value for " + key, e);
            return null;
        }
    }

    /**
     *  Put login session object
     * @param key
     * @param value
     * @param expire
     * @param <V>
     */
    @Override
    public <V extends Serializable> void loginPut(String key, V value,
                                                  long expire) {
        if (log.isTraceEnabled()) {
            Throwable t = new Throwable();
            String message = ">>> SET (expire:" + expire + ") [" + key + " -> "
                    + value + "]";
            log.trace(message + StackTraceUtil.getStackTrace(t));
        }

        try {
            if (value == null) {
                sessionCache.loginDelete(key);
            } else {
                sessionCache.loginPut(key, value, expire);
            }
        } catch (Exception e) {
            log.debug("Failed to set value for " + key, e);
        }

    }

    /**
     *  remove login session
     * @param key
     */
    @Override
    public void loginRemove(String key) {
        if (log.isTraceEnabled()) {
            Throwable t = new Throwable();
            String message = ">>> DELETE: [" + key + "]";
            log.trace(message + StackTraceUtil.getStackTrace(t));
        }

        try {
            sessionCache.loginDelete(key);
        } catch (Exception e) {
            log.debug("Failed to delete value for " + key, e);
        }
    }

    /**
     * return number of login sessions
     * @return
     */
    @Override
    public int loginSize() {
        int size = 0;
        try {
            size = sessionCache.loginSize();
        } catch (Exception e) {
            log.debug("Failed to get size of session", e);
        }
        return size;
    }
}
