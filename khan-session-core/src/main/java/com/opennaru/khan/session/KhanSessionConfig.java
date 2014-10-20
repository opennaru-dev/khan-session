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
 * KHAN Session Configuration
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionConfig {
    // if use library mode
    @Deprecated
    private boolean useLibraryMode;

    // namespace
    private String namespace;
    // exclude regular expression
    private String excludeRegExp;

    // session id key
    private String sessionIdKey;
    // cookie domain name
    private String domain;
    // cookie path
    private String path;
    // cookie secure
    private boolean secure;
    // cookie is httpOnly
    private boolean httpOnly;
    // if allow duplicate login
    private boolean allowDuplicateLogin;

    // 중복로그인 강제 logout url
    private String logoutUrl;

    // Session timout minute
    private Integer sessionTimeoutMin;

    // enable MBean statistics
    private boolean enableStatistics;

    // enable Memory statistics
    private boolean enableMemoryStatistics;

    // for spring security
    private boolean enableImmediateSave;

    /**
     * check if library mode
     * @return
     */
    public boolean isUseLibraryMode() {
        return useLibraryMode;
    }

    public void setUseLibraryMode(boolean useLibraryMode) {
        this.useLibraryMode = useLibraryMode;
    }

    /**
     * return namespace
     * @return
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * set namespace
     * @param namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * return session id key
     * @return
     */
    public String getSessionIdKey() {
        return sessionIdKey;
    }

    /**
     * set session id key
     * @param sessionIdKey
     */
    public void setSessionIdKey(String sessionIdKey) {
        this.sessionIdKey = sessionIdKey;
    }

    /**
     * exclude url pattern
     * @return
     */
    public String getExcludeRegExp() {
        return excludeRegExp;
    }

    /**
     * set exclude url pattern
     * @param excludeRegExp
     */
    public void setExcludeRegExp(String excludeRegExp) {
        this.excludeRegExp = excludeRegExp;
    }

    /**
     * get cookie domain name
     * @return
     */
    public String getDomain() {
        return domain;
    }

    /**
     * set cookie domain name
     * @param domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * get cookie path
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * set cookie path
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * set is secure cookie
     * @param secure
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * is httponly cookie
     * @return
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * set httponly cookie
     * @param httpOnly
     */
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    /**
     * is allow duplicate login
      * @return
     */
    public boolean isAllowDuplicateLogin() {
        return allowDuplicateLogin;
    }

    /**
     * set allow duplicate login
     * @param allowDuplicateLogin
     */
    public void setAllowDuplicateLogin(boolean allowDuplicateLogin) {
        this.allowDuplicateLogin = allowDuplicateLogin;
    }

    /**
     * get logout url
     * @return
     */
    public String getLogoutUrl() {
        return logoutUrl;
    }

    /**
     * set logout url
     * @param logoutUrl
     */
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    /**
     * get session timeout min
     * @return
     */
    public Integer getSessionTimeoutMin() {
        return sessionTimeoutMin;
    }

    /**
     * set session timeout min
     * @param sessionTimeoutMin
     */
    public void setSessionTimeoutMin(Integer sessionTimeoutMin) {
        this.sessionTimeoutMin = sessionTimeoutMin;
    }

    /**
     * is secure
     * @return
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * set secure
     * @param secure
     */
    public void setSecure(Boolean secure) {
        this.secure = secure != null && secure;
    }

    /**
     * enable JMX statistics
     * @return
     */
    public boolean isEnableStatistics() {
        return enableStatistics;
    }

    /**
     * set enable JMX statistics
     * @param enableStatistics
     */
    public void setEnableStatistics(boolean enableStatistics) {
        this.enableStatistics = enableStatistics;
    }

    /**
     * enable memory JMX statistics
     * @return
     */
    public boolean isEnableMemoryStatistics() {
        return enableMemoryStatistics;
    }

    /**
     * set enable memory JMX statistics
     * @param enableMemoryStatistics
     */
    public void setEnableMemoryStatistics(boolean enableMemoryStatistics) {
        this.enableMemoryStatistics = enableMemoryStatistics;
    }

    public boolean isEnableImmediateSave() {
        return enableImmediateSave;
    }

    public void setEnableImmediateSave(boolean enableImmediateSave) {
        this.enableImmediateSave = enableImmediateSave;
    }

    @Override
    public String toString() {
        return "KhanSessionConfig{" +
                "useLibraryMode=" + useLibraryMode +
                ", namespace='" + namespace + '\'' +
                ", excludeRegExp='" + excludeRegExp + '\'' +
                ", sessionIdKey='" + sessionIdKey + '\'' +
                ", domain='" + domain + '\'' +
                ", path='" + path + '\'' +
                ", secure=" + secure +
                ", httpOnly=" + httpOnly +
                ", allowDuplicateLogin=" + allowDuplicateLogin +
                ", logoutUrl='" + logoutUrl + '\'' +
                ", sessionTimeoutMin=" + sessionTimeoutMin +
                ", enableStatistics=" + enableStatistics +
                ", enableMemoryStatistics=" + enableMemoryStatistics +
                '}';
    }
}
