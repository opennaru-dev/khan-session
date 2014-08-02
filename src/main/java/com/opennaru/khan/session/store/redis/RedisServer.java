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

import com.opennaru.khan.session.util.StringUtils;

import java.net.URI;

/**
 * @since 1.1.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class RedisServer {
    private String hostname = "localhost";
    private int port = 6379;
    private int database = 1;
    private String password = null;

    private int timeout = 5000;

    public RedisServer() {

    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void parseUrl(String url) {
        URI uri = URI.create(url);
        if (uri.getScheme() != null && uri.getScheme().equals("redis")) {
            setHostname( uri.getHost() );
            setPort( uri.getPort() );
            String passwd = uri.getUserInfo().split(":", 2)[1];
            if( StringUtils.isNullOrEmpty(passwd) ) {
                setPassword( null );
            } else {
                setPassword( passwd );
            }

            setDatabase( Integer.parseInt(uri.getPath().split("/", 2)[1]) );
        }
    }

    @Override
    public String toString() {
        return "RedisServer{" +
                "hostname='" + hostname + '\'' +
                ", port=" + port +
                ", database=" + database +
                ", password='" + password + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
