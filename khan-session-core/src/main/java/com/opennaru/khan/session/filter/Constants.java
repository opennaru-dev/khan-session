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

/**
 * KHAN [session manager]에서 사용하는 상수(Constants)
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class Constants {
    /**
     * Global namespace
     */
    public static final String GLOBAL_NAMESPACE = "KHAN";

    /**
     *  기본 Session ID name : cookie name
     */
    public static final String DEFAULT_SESSION_ID_NAME = "__KSMSID__";

    /**
     * namespace KEY
     */
    public static final String NAMESPACE = "namespace";

    /**
     *  session id key
     */
    public static final String SESSION_ID = "sessionId";

    /**
     * cookie domain key
     */
    public static final String DOMAIN = "domain";

    /**
     * cookie path key
     */
    public static final String PATH = "path";

    /**
     * cookie secure key
     */
    public static final String SECURE = "secure";

    /**
     * cookie httpOnly key
     */
    public static final String HTTP_ONLY = "httpOnly";

    /**
     * session timeout key
     */
    public static final String SESSION_TIMEOUT = "sessionTimeout";

    /**
     * Session save delay when use get access only(in sec.)
     */
    public static final String SESSION_SAVE_DELAY = "sessionSaveDelay";

    /**
     * session check exclude regular expression
     */
    public static final String EXCLUDE_REG_EXP = "excludeRegExp";

    /**
     * use authenticator
     */
    public static final String USE_AUTHENTICATOR = "useAuthenticator";

    /**
    /**
     * duplicate login key
     */
    public static final String ALLOW_DUPLICATE_LOGIN = "allowDuplicateLogin";

    public static final String INVALIDATE_DUPLICATE_LOGIN = "invalidateDuplicateLogin";

    /**
     * logout url
     */
    public static final String LOGOUT_URL = "logoutUrl";

    /**
     * library mode key
     */
    @Deprecated
    public static final String USE_LIBRARY_MODE = "useLibraryMode";

    /**
     * name of configuration file
     */
    public static final String INFINISPAN_CONFIGFILE_KEY = "configFile";

    /**
     * name of infinispan cache
     */
    public static final String INFINISPAN_CACHE_KEY = "infinispanCache";

    /**
     * name of infinispan login cache
     */
    public static final String INFINISPAN_LOGIN_CACHE_KEY = "infinispanLoginCache";

    /**
     *  session status
     */
    protected static final String SESSION_STATUS = "__sessionStatus__";

    /**
     * name of redis configuration file
     */
    public static final String REDIS_CONFIGFILE_KEY = "configFile";

    /**
     * enable statistics
     */
    public static final String ENABLE_STATISTICS = "enableStatistics";

    /**
     * enable memory statistics
     */
    public static final String ENABLE_MEMORY_STATISTICS = "enableMemoryStatistics";

    public static final String ENABLE_IMMEDIATED_SAVE = "enableImmediateSave";
}
