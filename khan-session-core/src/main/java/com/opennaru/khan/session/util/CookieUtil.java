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
package com.opennaru.khan.session.util;

import javax.servlet.http.Cookie;

/**
 * Create Cookie Header
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class CookieUtil {

    /**
     * Create Cookie header
     *
     * @param cookie
     * @param isHttpOnly
     * @return
     */
    public static String createCookieHeader(Cookie cookie, boolean isHttpOnly) {
        StringBuilder sb = new StringBuilder();
        sb = sb.append(cookie.getName()).append("=").append(cookie.getValue());

        if (cookie.getDomain() != null && !cookie.getDomain().equals("") ) {
            sb.append(";Domain=").append(cookie.getDomain());
        }
        if (cookie.getPath() != null && !cookie.getPath().equals("")) {
            sb.append(";Path=").append(cookie.getPath());
        }
        if (cookie.getComment() != null && !cookie.getComment().equals("")) {
            sb.append(";Comment=").append(cookie.getComment());
        }
        if (cookie.getMaxAge() > -1) {
            sb.append(";Max-Age=").append(cookie.getMaxAge());
        }
        if (cookie.getSecure()) {
            sb.append(";Secure");
        }
        if (isHttpOnly) {
            sb.append(";HttpOnly");
        }

        return sb.toString();
    }



}
