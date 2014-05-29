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

import java.io.Serializable;
import java.util.Date;

/**
 * Session Meta Data
 *
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class KhanSessionMetadata implements Serializable {
    private Boolean invalidated;
    private Date creationTime;
    private Date lastAccessedTime;
    private String clientIp = null;

    public KhanSessionMetadata() {

    }

    public Boolean getInvalidated() {
        return invalidated;
    }

    public void setInvalidated(Boolean invalidated) {
        this.invalidated = invalidated;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date created) {
        this.creationTime = created;
    }

    public Date getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Date lastAccessed) {
        this.lastAccessedTime = lastAccessed;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}