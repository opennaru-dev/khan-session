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
package com.opennaru.khan.session;

/**
 * Session KEY generator for Cache Store's KEY
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionKeyGenerator {

    private final String sessionId;
    private final String namespace;

    /**
     * Constructor
     *
     * @param sessionId
     * @param namespace
     */
    public KhanSessionKeyGenerator(String sessionId, String namespace) {
        this.sessionId = sessionId;
        this.namespace = namespace;
    }

    /**
     * Cache에 저장할 KEY 생성
     *
     * @param namespace
     * @param sessionId
     * @param name
     * @return
     */
    public static String generate(String namespace, String sessionId, String name) {
        String key = "KHAN_SESSION__" + sessionId + "_" + namespace + "_" + name;
        key = key.replaceAll("\\s", "_");
        return key;
    }

    /**
     * Cache에 저장할 KEY 생성
     *
     * @param name
     * @return
     */
    public String generate(String name) {
        String key = "KHAN_SESSION__" + sessionId + "_" + namespace + "_" + name;
        key = key.replaceAll("\\s", "_");
        return key;
    }
}
