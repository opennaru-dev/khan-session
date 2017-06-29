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

import com.opennaru.khan.session.*;
import com.opennaru.khan.session.listener.SessionLoginManager;
import com.opennaru.khan.session.manager.KhanSessionManager;
import com.opennaru.khan.session.store.SessionId;
import com.opennaru.khan.session.store.SessionIdThreadStore;
import com.opennaru.khan.session.store.SessionStore;
import com.opennaru.khan.session.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Servlet Filter를 구현한 KhanSessionFilter abstract 클래스
 * 구현체는 InfinispanLibSessionFilter, InfinispanHotRodSessionFilter,
 *         RedisSessionFilter가 있음
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public abstract class KhanSessionFilter implements Filter {
    /**
     * KHAN Session Manager 세션 설정
     */
    public static KhanSessionConfig khanSessionConfig = null;

    /**
     * 세션 저장소
     */
    protected static SessionStore sessionStore;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static boolean haveToSaveForce = true;

    /**
     * 세션 모니터링
     */
    protected KhanSessionManager sessionManager = null;

    public static KhanSessionConfig getKhanSessionConfig() {
        return khanSessionConfig;
    }

    /**
     * web.xml에 설정된 FilterConfig에서 설정값을 가져온다
     *
     * @param config
     * @param keyName
     * @return
     */
    protected static String getConfigValue(FilterConfig config, String keyName) {
        String fromInitParam = config.getInitParameter(keyName);
        return PropertyUtil.getProperty(fromInitParam);
    }

    /**
     * 세션 상태를 저장
     * @param req
     * @param status
     */
    protected static void setSessionStatus(HttpServletRequest req,
                                           SessionStatus status) {
        req.setAttribute(Constants.SESSION_STATUS, status);
    }

    /**
     * get status of HTTP Session
     * @param req
     * @return
     */
    protected static SessionStatus getSessionStatus(HttpServletRequest req) {
        Object status = req.getAttribute(Constants.SESSION_STATUS);
        if (status == null) {
            return SessionStatus.UNKNOWN;
        } else {
            return (SessionStatus) status;
        }
    }

    /**
     * Check if this request's session is valid
     * @param req
     * @return
     */
    protected static boolean isValidSession(KhanSessionHttpRequest req) {
        if (getSessionStatus(req) == SessionStatus.FIXED) {
            return true;
        }
        return req.getSession(false).isValid();
    }

    /**
     * Check if Http Request is KhanHttpSession
     * @param req
     * @return
     */
    protected static boolean isKhanSessionHttpRequest(HttpServletRequest req) {
        return req.getSession(false) instanceof KhanHttpSession;
    }

    /**
     * SessionStore 객체를 반환
     *
     * @return
     */
    public static SessionStore getSessionStore() {
        return sessionStore;
    }

    /**
     * 세션 필터 설정
     *
     * @param config
     */
    protected void getSessionFilterConfig(FilterConfig config) {

        khanSessionConfig = new KhanSessionConfig();

        // use library mode
        khanSessionConfig.setUseLibraryMode(getConfigValue(config, Constants.USE_LIBRARY_MODE) != null
                && getConfigValue(config, Constants.USE_LIBRARY_MODE).equals("true"));

        // namespace
        khanSessionConfig.setNamespace(getConfigValue(config, Constants.NAMESPACE));
        if ( StringUtils.isNullOrEmpty( khanSessionConfig.getNamespace() ) ) {
            khanSessionConfig.setNamespace(Constants.GLOBAL_NAMESPACE);
        }

        // exclude regexp
        khanSessionConfig.setExcludeRegExp(getConfigValue(config, Constants.EXCLUDE_REG_EXP));

        // session id
        khanSessionConfig.setSessionIdKey(getConfigValue(config, Constants.SESSION_ID));
        if ( StringUtils.isNullOrEmpty( khanSessionConfig.getSessionIdKey() ) ) {
            khanSessionConfig.setSessionIdKey(Constants.DEFAULT_SESSION_ID_NAME);
        }

        // domain name
        khanSessionConfig.setDomain(getConfigValue(config, Constants.DOMAIN));

        // path
        khanSessionConfig.setPath(getConfigValue(config, Constants.PATH));
        if ( StringUtils.isNullOrEmpty(khanSessionConfig.getPath() ) ) {
            khanSessionConfig.setPath("/");
        }

        // is secure
        khanSessionConfig.setSecure(getConfigValue(config, Constants.SECURE) != null
                && getConfigValue(config, Constants.SECURE).equals("true"));

        // http only
        khanSessionConfig.setHttpOnly(getConfigValue(config, Constants.HTTP_ONLY) != null
                && getConfigValue(config, Constants.HTTP_ONLY).equals("true"));

        // session time out
        String sessionTimeout = getConfigValue(config, Constants.SESSION_TIMEOUT);

        if ( StringUtils.isNullOrEmpty(sessionTimeout) ) {
            khanSessionConfig.setSessionTimeoutMin(10);
        } else {
            try {
                khanSessionConfig.setSessionTimeoutMin(Integer.valueOf(sessionTimeout));
            } catch (NumberFormatException e) {
                khanSessionConfig.setSessionTimeoutMin(10);
                log.error("sessionTimeout value is invalid number format : " + sessionTimeout);
                log.error("use default value(10 minutes)");
            }
        }

        // use authenticator
        khanSessionConfig.setUseAuthenticator(getConfigValue(config, Constants.USE_AUTHENTICATOR) != null
                && getConfigValue(config, Constants.USE_AUTHENTICATOR).equals("true"));

        // allow duplicated login
        khanSessionConfig.setAllowDuplicateLogin(getConfigValue(config, Constants.ALLOW_DUPLICATE_LOGIN) != null
                && getConfigValue(config, Constants.ALLOW_DUPLICATE_LOGIN).equals("true"));

        khanSessionConfig.setInvalidateDuplicateLogin(getConfigValue(config, Constants.INVALIDATE_DUPLICATE_LOGIN) != null
                && getConfigValue(config, Constants.INVALIDATE_DUPLICATE_LOGIN).equals("true"));

        // force logout url
        khanSessionConfig.setLogoutUrl(getConfigValue(config, Constants.LOGOUT_URL));
        if (StringUtils.isNullOrEmpty( khanSessionConfig.getLogoutUrl().trim() ) ) {
            khanSessionConfig.setLogoutUrl("");
        }

        // enableStatistics
        boolean enableStatistics = true;
        if( getConfigValue(config, Constants.ENABLE_STATISTICS) != null &&
            getConfigValue(config, Constants.ENABLE_STATISTICS).equals("false") ) {
            enableStatistics = false;
        }
        khanSessionConfig.setEnableStatistics(enableStatistics);

        // enableStatistics
        boolean enableMemoryStatistics = false;
        if( getConfigValue(config, Constants.ENABLE_MEMORY_STATISTICS) != null &&
                getConfigValue(config, Constants.ENABLE_MEMORY_STATISTICS).equals("true") ) {
            enableMemoryStatistics = true;
        }
        khanSessionConfig.setEnableMemoryStatistics(enableMemoryStatistics);

        boolean enableImmediateSave = false;
        if( getConfigValue(config, Constants.ENABLE_IMMEDIATED_SAVE) != null &&
                getConfigValue(config, Constants.ENABLE_IMMEDIATED_SAVE).equals("true") ) {
            enableImmediateSave = true;
        }
        khanSessionConfig.setEnableImmediateSave(enableImmediateSave);
    }

    /**
     * Get Session Id Cookie
     * @param req
     * @return
     */
    protected Cookie getCurrentValidSessionIdCookie(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if( cookie.getName().equals( khanSessionConfig.getSessionIdKey() )
                        && cookie.getValue() != null
                        && cookie.getValue().trim().length() > 0 ) {

                    String sessionKey = cookie.getValue();
//                    // check session key is in store
                    boolean isContains = KhanSessionStore.contains(sessionKey);
                    if( log.isDebugEnabled() ) {
                        log.debug("Check Session id key [" + sessionKey + "] is exist. / isContains=" + isContains);
                    }
                    if( isContains == false ) {
                        if (log.isDebugEnabled()) {
                            log.debug("SessionId cookie expired. ("
                                    + khanSessionConfig.getSessionIdKey() + " -> "
                                    + cookie.getValue() + ")");
                        }
                        return null;
                    }

                    if( isValidSession(createSessionRequest(req, sessionKey)) ) {
                        if (log.isDebugEnabled()) {
                            log.debug("SessionId cookie found. ("
                                    + khanSessionConfig.getSessionIdKey() + " -> "
                                    + cookie.getValue() + ")");
                        }
                        return cookie;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("SessionId cookie found but it's invalid. ("
                                    + khanSessionConfig.getSessionIdKey()
                                    + " -> "
                                    + sessionKey + ")");
                        }
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("SessionId cookie not found.");
        }
        return null;
    }

    /**
     * Generate Session Id Cookie
     * @param sessionIdValue
     * @return
     */
    protected Cookie generateSessionIdCookie(String sessionIdValue) {

        Cookie sessionIdCookie = new Cookie(khanSessionConfig.getSessionIdKey(), sessionIdValue);
        if (khanSessionConfig.getDomain() != null && !khanSessionConfig.getDomain().equals("")) {
            sessionIdCookie.setDomain(khanSessionConfig.getDomain());
        }
        if (khanSessionConfig.getPath() != null && !khanSessionConfig.getPath().equals("")) {
            sessionIdCookie.setPath(khanSessionConfig.getPath());
        } else {
            sessionIdCookie.setPath("/");
        }
        sessionIdCookie.setSecure(khanSessionConfig.isSecure());

        // httpOnly 는 Servlet 2.x에서 지원하지 않음. Error in WLS 11g
//        try {
//            sessionIdCookie.setHttpOnly(khanSessionConfig.isHttpOnly());
//        } catch( NoSuchFieldError e ) {
//        }

        return sessionIdCookie;
    }

    /**
     * HttpServletRequest를 Wrapping한 KhanSessionHttpRequest 객체를 생성한다.
     *
     * @param request
     * @param sessionIdValue
     * @return
     */
    protected KhanSessionHttpRequest createSessionRequest(
            HttpServletRequest request, String sessionIdValue) {

        if (log.isDebugEnabled()) {
            log.debug("***** createSessionRequest");
        }

        return new KhanSessionHttpRequest(request, sessionIdValue,
                khanSessionConfig.getNamespace(), khanSessionConfig.getSessionTimeoutMin(),
                sessionStore, sessionManager);
    }

    /**
     * Initialize Session Filter
     * @param config
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        System.out.println("KHAN [session manager] starting... ");
        System.out.println(Version.getInstance().getVersion() );

        getSessionFilterConfig(config);

        if (sessionManager == null) {
            sessionManager = new KhanSessionManager(config.getServletContext().getContextPath());
            if (log.isDebugEnabled()) {
                log.debug("***** init filter");
                log.debug("***** sessionManager=" + sessionManager);
                log.debug("***** sessionMonitor=" + sessionManager.getSessionMonitor());
            }

        }
    }

    public static final String ALREADY_FILTERED = ".FILTERED";

    private String alreadyFilteredAttributeName = getClass().getName().concat(ALREADY_FILTERED);

    /**
     * Filter 메인 함수
     * TODO : Refactoring....
     *
     * @param request
     * @param response
     * @param chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        SysOutUtil.printFilterStart(request);

        if (log.isDebugEnabled()) {
            log.debug("===== >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        if (log.isTraceEnabled()) {
            Throwable t = new Throwable();
            String message = ">>> doFilter <<<";
            log.trace(message + StackTraceUtil.getStackTrace(t));
        }

        HttpServletRequest _request = (HttpServletRequest) request;
        HttpServletResponse _response = (HttpServletResponse) response;

        if (_request instanceof HttpServletRequest) {

            boolean hasAlreadyFilteredAttribute = _request.getAttribute(alreadyFilteredAttributeName) != null;

            if( hasAlreadyFilteredAttribute ) {
                chain.doFilter(_request, _response);
            } else {
                _request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);

                try {
                    // 제외한 요청이면
                    if (khanSessionConfig.getExcludeRegExp() != null
                            && _request.getRequestURI().matches(khanSessionConfig.getExcludeRegExp())) {

                        if (log.isDebugEnabled()) {
                            log.debug("******* This URI is excluded. (URI: " + _request.getRequestURI() + ")");
                        }
                        chain.doFilter(_request, _response);
                        // KHAN Request이면
                    } else if (isKhanSessionHttpRequest(_request)) {
                        if (log.isDebugEnabled()) {
                            log.debug("******* KhanSessionHttpRequest is already applied.");
                            log.debug("requestedSessionId=" + _request.getRequestedSessionId());
                        }
                        chain.doFilter(_request, _response);
                        // 새로운 요청
                    } else {
                        // doFilter with the request wrapper

                        Cookie cookie = getCurrentValidSessionIdCookie(_request);
                        log.debug(SessionIdThreadStore.get());
                        HttpSession s = _request.getSession(false);
                        log.debug(s + "");

                        if (log.isDebugEnabled())
                            log.debug(">>>>> cookie=" + cookie);

                        String sessionIdValue = null;

                        log.debug("******* new request");
                        log.debug("*************** SessionIdThreadStore.get()=" + SessionIdThreadStore.get());

                        if (SessionIdThreadStore.get() != null) {
                            sessionIdValue = SessionIdThreadStore.get();
                        } else if (cookie == null) {
                            HttpSession session = _request.getSession(false);
                            if (session == null || session.isNew()) {
                                sessionIdValue = UUID.randomUUID().toString();
                            } else {
                                // copy JSESSIONID value to original session
                                sessionIdValue = session.getId();
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(">> current session cookie=" + cookie.getValue());
                                log.debug(">> session id=" + _request.getSession().getId());
                            }
                            // current session is valid
                            sessionIdValue = cookie.getValue();
                        }

                        if (log.isDebugEnabled()) {
                            log.debug("*****[[[[[ sessionIdValue=" + sessionIdValue + "]]]]]*****");
                        }

                        if (cookie == null || SessionIdThreadStore.get() != null) {
                            Cookie newSessionIdCookie = generateSessionIdCookie(sessionIdValue);
                            cookie = newSessionIdCookie;

                            // httpOnly 는 Servlet 2.x에서 지원하지 않음.
                            // addCookie대신 addHeader를 사용
                            String setCookie = CookieUtil.createCookieHeader(newSessionIdCookie, khanSessionConfig.isHttpOnly());
                            _response.addHeader("Set-Cookie", setCookie);
                            setSessionStatus(_request, SessionStatus.FIXED);

                            if (log.isDebugEnabled()) {
                                log.debug("SessionId cookie is updated. (" + sessionIdValue + ")");
                            }
                        }

                        //_request.getSession().setAttribute("__khan.session.id__", sessionIdValue);

                        if (log.isDebugEnabled()) {
                            try {
                                log.debug(">>>>> current session cookie=" + cookie.getValue());
                                log.debug(">>>>> khan session id=" + SessionId.getKhanSessionId(_request.getSession(false).getId()));
                                log.debug(">>>>> jsession id=" + _request.getSession(false).getId());
                            } catch (Exception e) {
                            }
                        }

                        // doFilter with the request wrapper
                        KhanSessionHttpRequest _wrappedRequest = null;
                        _wrappedRequest = createSessionRequest(_request, sessionIdValue);

                        boolean redirectLogoutUrl = false;
                        String khan_uid = "";


                        // Authenticator 사용할 경우
                        if (khanSessionConfig.useAuthenticator() == true) {
                            log.debug("> _wrappedRequest.getRemoteUser()=" + _wrappedRequest.getRemoteUser());
                            if (_wrappedRequest.getRemoteUser() == null) {
                                khan_uid = (String) _wrappedRequest.getSession(false).getAttribute("khan.uid");
                                log.debug("> khan_uid=" + khan_uid);
                                if (khan_uid != null) {
                                    _wrappedRequest.login(khan_uid, khan_uid);
                                }
                            } else {
                                HttpSession _session = _request.getSession(false);
                                if (_session.getAttribute("___KHAN___") == null) {
                                    _request.getSession(false).setAttribute("___KHAN___", "1");
                                }
                            }
                        }

                        // 중복 로그인을 허용하지 않는다고 설정되어 있는 경우
                        if (khanSessionConfig.isAllowDuplicateLogin() == false) {

                            khan_uid = (String) _wrappedRequest.getSession(false).getAttribute("khan.uid");
                            if (log.isDebugEnabled()) {
                                log.debug("$$$$$ khan_uid=" + khan_uid);
                            }
                            String key = KhanSessionKeyGenerator.generate("$", "SID", _wrappedRequest.getSession(false).getId());
                            String loginStatus = KhanSessionFilter.getSessionStore().loginGet(key);
                            if (log.isDebugEnabled()) {
                                log.debug("$$$$$ loginStatus=" + loginStatus);
                            }

                            if (loginStatus != null && loginStatus.equals("DUPLICATED")) {
                                redirectLogoutUrl = true;
                            } else {
                                // update login info
                                if (khan_uid != null && !khan_uid.equals("")) {
                                    try {
                                        SessionLoginManager.getInstance().login(_wrappedRequest, khan_uid);
                                    } catch (Exception e) {
                                        log.error("login ", e);
                                    }
                                    if (log.isDebugEnabled()) {
                                        log.debug("$$$$$ login");
                                    }
                                }
                            }
                        }

                        // do KHAN Session filter
                        chain.doFilter(_wrappedRequest, _response);
                        // after KHAN Session filter

                        String currentSessionId = _wrappedRequest.getSession(false).getId();
                        if (!currentSessionId.equals(sessionIdValue)) {
                            Cookie newSessionIdCookie = generateSessionIdCookie(currentSessionId);
                            // httpOnly 는 Servlet 2.x에서 지원하지 않음.
                            // addCookie대신 addHeader를 사용
                            String setCookie = CookieUtil.createCookieHeader(newSessionIdCookie, khanSessionConfig.isHttpOnly());
                            _response.addHeader("Set-Cookie", setCookie);
                            setSessionStatus(_request, SessionStatus.FIXED);
                            if( log.isDebugEnabled() ) {
                                log.debug("########### new session id=" + currentSessionId);
                            }
                        }

                        // update attributes, expiration
                        KhanHttpSession session = _wrappedRequest.getSession(false);

                        HttpSession httpSession = _request.getSession(false);
                        if (httpSession != null) {
                            httpSession.setAttribute("khan.session.id", session.getId());
                        }


                        // TEST
                        if (log.isDebugEnabled()) {
                            HttpSession debugSession = _request.getSession(false);
                            if (debugSession != null) {
                                log.debug("Session ID=" + debugSession.getId());
                                Enumeration<String> e = debugSession.getAttributeNames();
                                while (e.hasMoreElements()) {
                                    String key = e.nextElement();
                                    log.debug("KEY/VALUE=" + key + "/" + debugSession.getAttribute(key));
                                }
                            }
                        }
                        // END

                        if (log.isDebugEnabled()) {
                            log.debug("*****[[[[[ session.getId()=" + session.getId() + "]]]]]*****");
                        }


                        // 중복로그인 되었을 경우 url forward
                        if (redirectLogoutUrl) {
                            try {
                                SysOutUtil.printDuplicatedInvalidate(request);

                                // 중복로그인된 세션의 정보를 지운다
                                String key = KhanSessionKeyGenerator.generate("$", "SID", _wrappedRequest.getSession(false).getId());
                                KhanSessionFilter.getSessionStore().loginRemove(key);
                                SessionLoginManager.getInstance().logout(_wrappedRequest);

                                if( khanSessionConfig.isInvalidateDuplicateLogin() ) {
                                    _wrappedRequest.getSession().invalidate();
                                }

                                if( !StringUtils.isNullOrEmpty( khanSessionConfig.getLogoutUrl() ) ) {
                                    SysOutUtil.printDuplicatedRedirect(request);
                                    request.getRequestDispatcher(khanSessionConfig.getLogoutUrl()).forward(request, response);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // need reloading from the store to work
                        //session.reloadAttributes();
                        session.save();

                        if (log.isDebugEnabled()) {
                            log.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< =====");
                        }
                    }

                } finally {

                    _request.removeAttribute(alreadyFilteredAttributeName);
                    SessionIdThreadStore.remove();

                    SysOutUtil.printFilterEnd(request);
                }

            }
        }

    }

    /**
     * Destroy
     */
    @Override
    public void destroy() {
        if (log.isDebugEnabled()) {
            log.debug("KhanSessionFilter destroy..");
        }
        sessionManager.destroy();
    }


//    private final class KhanSessionResponseWrapper extends HttpServletResponseWrapper
//    {
//
//        MyServletOutputStream stream = new MyServletOutputStream();
//
//        public KhanSessionResponseWrapper(KhanSessionHttpRequest request, HttpServletResponse httpServletResponse)
//        {
//            super(httpServletResponse);
//        }
//
//        public ServletOutputStream getOutputStream() throws IOException
//        {
//            return stream;
//        }
//
//        public PrintWriter getWriter() throws IOException
//        {
//            return new PrintWriter(stream);
//        }
//
//        public byte[] getWrapperBytes()
//        {
//            return stream.getBytes();
//        }
//    }
//
//    private final class MyServletOutputStream extends ServletOutputStream
//    {
//        private ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        public void write(int b) throws IOException
//        {
//            out.write(b);
//        }
//
//        public byte[] getBytes()
//        {
//            return out.toByteArray();
//        }
//
//    }
}
