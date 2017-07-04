package com.opennaru.khan.session.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ddakker on 17. 6. 28.
 */
public class SysOutUtil {
    private static Logger log = LoggerFactory.getLogger("KHAN_SM_SYSOUT_STACK");

    public static final String KHAN_SM_SYSOUT_STACK = "KHAN_SM_SYSOUT_STACK";

    public static void println(String...strArr) {
        if( System.getProperty(KHAN_SM_SYSOUT_STACK, "false").equals("true") ) {
            StringBuffer logStr = new StringBuffer();

            if (strArr != null) {
                for (int i=0,size=strArr.length; i<size; i++ ) {
                    logStr.append(strArr[i]);
                }
            }

            log.info("===== " +logStr.toString());
        }
    }

    public static void printFilterStart(ServletRequest req) {
        if( System.getProperty(KHAN_SM_SYSOUT_STACK, "false").equals("true") ) {
            HttpServletRequest request = (HttpServletRequest) req;

            StringBuffer logStr = new StringBuffer();
            logStr.append("==================== KHAN SESSION MANAGER FILTER START ====================").append("\n");
            logStr.append("==================== Request INFO ====================").append("\n");
            logStr.append("===== URL: ").append(request.getRequestURL()).append("\n");
            logStr.append("===== URI: ").append(request.getRequestURI()).append("\n");
            logStr.append("===== METHOD: ").append(request.getMethod()).append("\n");
            logStr.append("===== IP: ").append(request.getRemoteAddr()).append("\n");

            logStr.append("==================== Request HEADER ====================").append("\n");
            Enumeration headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()){
                String name = (String) headerNames.nextElement();
                String value = request.getHeader(name);
                logStr.append("===== " + name + ": [" + value + "]").append("\n");
            }
            logStr.append("========================================");

            log.info(logStr.toString());
        }
    }

    public static void printDuplicatedInvalidate(ServletRequest request) {
        if( System.getProperty(KHAN_SM_SYSOUT_STACK, "false").equals("true") ) {
            log.info("==================== LOGIN DUPLICATED INVALIDATE ====================");
        }
    }

    public static void printDuplicatedRedirect(ServletRequest request) {
        if( System.getProperty(KHAN_SM_SYSOUT_STACK, "false").equals("true") ) {
            log.info("==================== LOGIN DUPLICATED FORWARD ====================");
        }
    }

    public static void printGetStore(ConcurrentHashMap<Object, Object> sessionValueMap) {
        if( System.getProperty(KHAN_SM_SYSOUT_STACK, "false").equals("true") ) {
            log.info("===== GET STORE sessionValueMap: {}", sessionValueMap);
        }
    }

    public static void printSaveStore(ConcurrentHashMap<Object, Object> sessionValueMap, int numberOfChangeAttribute, int numberOfGetAttribute) {
        if( System.getProperty(KHAN_SM_SYSOUT_STACK, "false").equals("true") ) {
            log.info("===== SAVE STORE numberOfChangeAttribute: {}", numberOfChangeAttribute);
            log.info("===== SAVE STORE numberOfGetAttribute: {}", numberOfGetAttribute);
            log.info("===== SAVE STORE save sessionValueMap: {}", sessionValueMap);

        }
    }

    public static void printFilterEnd(ServletRequest req) {
        if( System.getProperty(KHAN_SM_SYSOUT_STACK, "false").equals("true") ) {
            log.info("===== ==================== KHAN SESSION MANAGER FILTER END ====================\n\n\n");
        }
    }



}
