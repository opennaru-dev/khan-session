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
import com.opennaru.khan.session.store.SessionId;
import com.opennaru.khan.session.store.SessionIdThreadStore;
import com.opennaru.khan.session.store.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Extends HttpServletRequestWrapper class
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionHttpRequest extends HttpServletRequestWrapper {
    private static Logger log = LoggerFactory.getLogger("KhanSessionHttpRequest");

    private KhanHttpSession session;

    private final String namespace;
    private String sessionId;
    private final SessionStore store;

    private Integer timeoutMin;
    private KhanSessionManager sessionManager;
    private String clientIp;

    /**
     * Consturctor
     *
     * @param request
     * @param sessionId
     * @param namespace
     * @param timeoutMin
     * @param store
     * @param sessionManager
     */
    public KhanSessionHttpRequest(HttpServletRequest request, String sessionId,
                                  String namespace, Integer timeoutMin, SessionStore store,
                                  KhanSessionManager sessionManager) {
        super(request);

        this.sessionId = sessionId;
        this.namespace = namespace;
        this.store = store;
        clientIp = getClientIp(request);

        this.timeoutMin = timeoutMin;
        this.sessionManager = sessionManager;

        SessionIdThreadStore.set(sessionId);
        HttpSession session = super.getSession();
//        SessionId.setKhanSessionId(session.getId(), sessionId);

        this.session = new KhanHttpSession(sessionId, store, namespace,
                timeoutMin, session, sessionManager, clientIp);
    }

    @Override
    public KhanHttpSession getSession() {
        if( session == null ) {
            session = getSession(true);
        }

        return session;
    }

    @Override
    public KhanHttpSession getSession(boolean create) {
        HttpSession jsession = super.getSession(false);
        if( jsession != null ) {
            return this.session;
        }

        if( jsession != null && jsession.getId() != null ) {
            if( this.session != null ) {
                HttpSession _session = super.getSession(false);

                store.put(
                        session.getKeyGenerator().generate(KhanHttpSession.ATTRIBUTES_KEY),
                        new ConcurrentHashMap<Object, Object>(),
                        timeoutMin
                );

                this.session = new KhanHttpSession(sessionId, store, namespace,
                        timeoutMin, _session, sessionManager, clientIp);
            }
        }

        if( create ) {
//            this.sessionId = UUID.randomUUID().toString();
//            SessionIdThreadStore.set(sessionId);
//            session.setNew();
//            log.debug("&&&&& createNewSession=" + sessionId);
            //
//            log.debug("khan.session.getSession.create=" + System.getProperty("khan.session.getSession.create"));

            log.debug("***** >> SessionIdThreadStore.get()=" + SessionIdThreadStore.get());
            HttpSession _session = super.getSession(true);

            this.session = new KhanHttpSession(sessionId, store, namespace,
                    timeoutMin, _session, sessionManager, clientIp);

            _session.setAttribute("khan.session.id", this.sessionId);
            log.debug("&&&&& createNewRawSession=" + _session.getId());

            store.put(
                    session.getKeyGenerator().generate(KhanHttpSession.ATTRIBUTES_KEY),
                    new ConcurrentHashMap<Object, Object>(),
                    timeoutMin
            );
        }

        return this.session;
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