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

import com.jcabi.manifests.Manifests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Get Implementation version from MANIFEST.MF
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class VersionUtil {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDateAsString(String timeStamp) {
        Date date = new Date(Long.parseLong(timeStamp));
        return simpleDateFormat.format(date);
    }

    public static Date getDate(String timeStamp) {
        Date date = new Date(Long.parseLong(timeStamp));
        return date;
    }

    public static String getVersion(String p) {
        StringBuffer sb = new StringBuffer();
        sb.append( p + " ");
        sb.append( "Version : " + Manifests.read( p + "-version") + ", ");
        sb.append( "Build : " + Manifests.read( p + "-build") + ", ");
        sb.append( "Time : " + getDateAsString(Manifests.read( p + "-timestamp")) );

        return sb.toString();
    }

    public static String getVersionShort(String p) {
        String version = Manifests.read( p + "-version" );
        return version;
    }

    public static String getVersionShort() {
        return getVersionShort("KHAN-session-core");
    }

    public static String getBuild(String p) {
        return Manifests.read( p + "-build");
    }

    public static String getBuild() {
        return getBuild("KHAN-session-core");
    }

    public static Date getBuildDate(String p) {
        return getDate( Manifests.read( p + "-timestamp") );
    }

    public static Date getBuildDate() {
        return getBuildDate("KHAN-session-core");
    }

    public static String getBuildDateAsString(String p) {
        return getDateAsString( Manifests.read( p + "-timestamp") );
    }

    public static String getBuildDateAsString() {
        return getBuildDateAsString("KHAN-session-core");
    }

}
