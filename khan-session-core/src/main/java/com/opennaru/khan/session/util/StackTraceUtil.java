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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Stack Trace Printer utility class
 *
 * @since 1.1.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class StackTraceUtil {

    /**
     * Get StackTrace String from Throwable
     *
     * @param t Throwable
     * @return StackTrace String
     */
    public static String getStackTrace(Throwable t) {
        if( System.getProperty("KHAN_SM_DEBUG_STACK", "false").equals("true") ) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            return sw.toString();
        } else {
            return "";
        }
    }

    /**
     * Get StackTrace String from StackTraceElement Array
     *
     * @param trace
     * @return StackTrace String
     */
    public static String getStackTrace(StackTraceElement[] trace) {
        if( System.getProperty("KHAN_SM_DEBUG_STACK", "false").equals("true") ) {
            StringBuffer sb = new StringBuffer();

            sb.append("============================================\n");
            for (int i = 0; i < trace.length; i++)
                sb.append("\tat " + trace[i] + "\n");

            sb.append("--------------------------------------------\n");
            return sb.toString();
        } else {
            return "";
        }
    }
}
