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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Version
 *
 * @since 1.3.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class Version {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    protected static Version version = null;
    protected static Properties properties = null;

    public static Version getInstance() {
        if( version == null ) {
            try {
                version = new Version();
                InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("version.properties");
                properties = new Properties();
                properties.load(in);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return version;
    }

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDateAsString(String timeStamp) {
        Date date = new Date(Long.parseLong(timeStamp));
        return simpleDateFormat.format(date);
    }

    public static Date getDate(String timeStamp) {
        Date date = new Date(Long.parseLong(timeStamp));
        return date;
    }

    public static String getVersion() {
        StringBuffer sb = new StringBuffer();
        sb.append( "Version : " + properties.getProperty("khan.session.version") + ", ");
        sb.append( "Build : " + properties.getProperty("buildNumber") + ", ");
        sb.append( "Time : " + getDateAsString(properties.getProperty("timestamp")) );

        return sb.toString();
    }

    public static String getVersionShort() {
        String version = properties.getProperty("khan.session.version");
        return version;
    }


    public static String getBuild() {
        return properties.getProperty("buildNumber");
    }


    public static Date getBuildDate() {
        return getDate( properties.getProperty("timestamp") );
    }


    public static String getBuildDateAsString() {
        return getDateAsString( properties.getProperty("timestamp") );
    }


}
