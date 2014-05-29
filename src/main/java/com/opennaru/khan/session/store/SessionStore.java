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
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public interface SessionStore {

    public boolean contains(String key);

    public <V extends Serializable> V get(String key);

    public <V extends Serializable> void put(String key, V value, long expire);

    public void remove(String key);

    public int size();


    public boolean loginContains(String key);

    public <V extends Serializable> V loginGet(String key);

    public <V extends Serializable> void loginPut(String key, V value, long expire);

    public void loginRemove(String key);

    public int loginSize();
}
