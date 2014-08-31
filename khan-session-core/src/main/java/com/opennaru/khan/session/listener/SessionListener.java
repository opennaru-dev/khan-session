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
package com.opennaru.khan.session.listener;

import com.opennaru.khan.session.filter.KhanSessionFilter;
import com.opennaru.khan.session.management.SessionMonitorMBean;
import com.opennaru.khan.session.manager.KhanSessionManager;
import com.opennaru.khan.session.store.SessionId;
import com.opennaru.khan.session.store.SessionIdThreadStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * SessionListener for destroy KHAN Session
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class SessionListener implements HttpSessionListener {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Default Constructor
     */
    public SessionListener() {

    }

    /**
     * Session Created
     * @param sessionEvent
     */
    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        // Get the session that was created
        HttpSession session = sessionEvent.getSession();
        // Store something in the session, and log a message
        try {
            if( log.isDebugEnabled() ) {
                log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> created sessionEvent=" + sessionEvent);
                log.debug("Session created=" + session.getId());
            }
            String appName = sessionEvent.getSession().getServletContext().getContextPath();
            SessionMonitorMBean sessionMonitorMBean = KhanSessionManager.getInstance(appName).getSessionMonitor();
            if( sessionMonitorMBean != null )
                sessionMonitorMBean.sessionCreated();

            SessionId.setKhanSessionId(session.getId(), SessionIdThreadStore.get());
//            SessionId.setKhanSessionId(session.getId(), (String)session.getAttribute("khan.session.id"));
            SessionIdThreadStore.remove();

            if (KhanSessionFilter.getKhanSessionConfig().isAllowDuplicateLogin() == false) {
//                session.setAttribute("khan.uid", SessionLoginManager.getInstance());
                session.setAttribute("khan.uid", null);
            }
            //KhanSessionManager.getInstance(appName).putSessionId(session);

        } catch (Exception e) {
            log.error("Error in setting session attribute: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Session Destroyed : Remove Session Id from KHAN Session Store
     *
     * @param sessionEvent
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        // Get the session that was invalidated
        try {
            HttpSession session = sessionEvent.getSession();
            String sessionId = session.getId();

            SessionId.removeKhanSessionId(sessionId);
            // Log a message
            if( log.isDebugEnabled() ) {
                log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> destroy sessionEvent=" + sessionEvent);
                log.debug("Session invalidated: " + sessionId);
            }
            String appName = sessionEvent.getSession().getServletContext().getContextPath();

            KhanSessionManager.getInstance(appName).removeSessionId(session);

            SessionMonitorMBean sessionMonitorMBean = KhanSessionManager.getInstance(appName).getSessionMonitor();
            if ( sessionMonitorMBean != null )
                sessionMonitorMBean.sessionDestroyed();

            SessionId.removeKhanSessionId(sessionId);

            session.invalidate();
        } catch (Exception e) {
            log.error("Error destroy event", e);
        }
    }

}