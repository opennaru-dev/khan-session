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

import com.opennaru.khan.session.manager.KhanSessionManager;
import com.opennaru.khan.session.store.SessionStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Extends HttpServletRequestWrapper class
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionHttpRequest extends HttpServletRequestWrapper {

    private final KhanHttpSession session;

    private final String namespace;
    private final String sessionId;
    private final SessionStore store;

    public KhanSessionHttpRequest(HttpServletRequest request, String sessionId,
                                  String namespace, Integer timeoutMin, SessionStore store,
                                  KhanSessionManager sessionManager) {
        super(request);

        this.sessionId = sessionId;
        this.namespace = namespace;
        this.store = store;
        String clientIp = getClientIp(request);
        this.session = new KhanHttpSession(sessionId, store, namespace,
                timeoutMin, super.getSession(), sessionManager, clientIp);
    }

    @Override
    public KhanHttpSession getSession() {
        return session;
    }

    @Override
    public KhanHttpSession getSession(boolean create) {
        return session;
    }

    /**
     * Get Client's IP / Check Proxy's header
     * @param request
     * @return
     */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = "";
        clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getHeader("Proxy-SessionCache-IP");
            if (clientIp == null) {
                clientIp = request.getHeader("WL-Proxy-SessionCache-IP");
                if (clientIp == null) {
                    clientIp = request.getRemoteAddr();
                }
            }
        }
        return clientIp;
    }

}