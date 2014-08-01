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
 * Constants
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class Constants {
    // Global namespace
    public static final String GLOBAL_NAMESPACE = "KHAN";
    // Session ID name : cookie name
    public static final String DEFAULT_SESSION_ID_NAME = "__KSMSID__";

    // Configuration KEY
    public static final String NAMESPACE = "namespace";
    public static final String SESSION_ID = "sessionId";
    public static final String DOMAIN = "domain";
    public static final String PATH = "path";
    public static final String SECURE = "secure";
    public static final String HTTP_ONLY = "httpOnly";
    public static final String SESSION_TIMEOUT = "sessionTimeout";
    public static final String EXCLUDE_REG_EXP = "excludeRegExp";
    public static final String ALLOW_DUPLICATE_LOGIN = "allowDuplicateLogin";

    public static final String LOGOUT_URL = "logoutUrl";
    public static final String USE_LIBRARY_MODE = "useLibraryMode";

    public static final String INFINISPAN_CONFIGFILE_KEY = "configFile";
    public static final String INFINISPAN_CACHE_KEY = "infinispanCache";
    public static final String INFINISPAN_LOGIN_CACHE_KEY = "infinispanLoginCache";
    protected static final String SESSION_STATUS = "__sessionStatus__";

    public static final String REDIS_CONFIGFILE_KEY = "configFile";

}
