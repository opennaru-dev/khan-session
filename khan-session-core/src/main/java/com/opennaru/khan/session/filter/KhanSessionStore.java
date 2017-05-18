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

package com.opennaru.khan.session.filter;

import com.opennaru.khan.session.KhanHttpSession;
import com.opennaru.khan.session.KhanSessionKeyGenerator;
import com.opennaru.khan.session.KhanSessionMetadata;
import com.opennaru.khan.session.store.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jjeon on 14. 8. 30.
 */
public class KhanSessionStore {
    private static Logger log = LoggerFactory.getLogger(KhanSessionStore.class);

    public static <V extends Serializable> void setAttribute(HttpSession session, String name, V value) throws Exception {
        String khanSessionId = SessionId.getKhanSessionId(session.getId());

        String nameSpace = KhanSessionFilter.getKhanSessionConfig().getNamespace();

        String sidKey = KhanSessionKeyGenerator.generate(nameSpace, khanSessionId, KhanHttpSession.ATTRIBUTES_KEY);
        Integer timeoutMin = KhanSessionFilter.getKhanSessionConfig().getSessionTimeoutMin();
        long timeoutSecond = (long) (timeoutMin * 60);

        if( log.isDebugEnabled() ) {
            log.debug("sidKey=" + sidKey);
        }
        ConcurrentHashMap<Object, Object> attributes = null;

        attributes = KhanSessionFilter.getSessionStore().get(sidKey);
        if( log.isDebugEnabled() ) {
            log.debug("attributes=" + attributes);
        }
        if( attributes == null ) {
            attributes = new ConcurrentHashMap<Object, Object>();

            String metadataKey = KhanSessionKeyGenerator.generate(nameSpace, khanSessionId, KhanHttpSession.METADATA_KEY);

            KhanSessionMetadata khanSessionMetadata = KhanSessionFilter.getSessionStore().get(metadataKey);
            if (khanSessionMetadata == null) {
                khanSessionMetadata = new KhanSessionMetadata();
                khanSessionMetadata.setInvalidated(false);
                khanSessionMetadata.setCreationTime(new Date());
                KhanSessionFilter.getSessionStore().put(metadataKey, khanSessionMetadata, timeoutSecond);
            }
        }
        attributes.put(name, value);
        if( log.isDebugEnabled() ) {
            log.debug("attributes=" + attributes);
        }
        KhanSessionFilter.getSessionStore().put(sidKey, attributes, timeoutSecond);

//        ConcurrentHashMap<Object, Object> att = KhanSessionFilter.getSessionStore().get(sidKey);
//        System.out.println("att=" + att);

    }

    public static Object getAttribute(HttpSession session, String name) throws Exception {
        String khanSessionId = SessionId.getKhanSessionId(session.getId());

        String nameSpace = KhanSessionFilter.getKhanSessionConfig().getNamespace();

        String sidKey = KhanSessionKeyGenerator.generate(nameSpace, khanSessionId, KhanHttpSession.ATTRIBUTES_KEY);

        if( log.isDebugEnabled() ) {
            log.debug("sidKey=" + sidKey);
        }
        ConcurrentHashMap<Object, Object> attributes = null;

        attributes = KhanSessionFilter.getSessionStore().get(sidKey);
        if( log.isDebugEnabled() ) {
            log.debug("attributes=" + attributes);
        }
        if( attributes == null ) {
            return null;
        }

        return attributes.get(name);
    }

    public static boolean contains(String khanSessionId) {

        String nameSpace = KhanSessionFilter.getKhanSessionConfig().getNamespace();

        String sidKey = KhanSessionKeyGenerator.generate(nameSpace, khanSessionId, KhanHttpSession.METADATA_KEY);

        boolean isContains = false;

        if( log.isDebugEnabled() ) {
            log.debug("sidKey=" + sidKey);
        }

        isContains = KhanSessionFilter.getSessionStore().contains(sidKey);

        if( log.isDebugEnabled() ) {
            log.debug("isContains=" + isContains);
        }

        return isContains;
    }

}
