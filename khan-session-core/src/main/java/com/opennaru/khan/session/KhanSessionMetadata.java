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
    // 세션의 메타데이터
    // 세션이 invalidate되었는지 정보
    private Boolean invalidated;
    // 세션 생성 시간
    private Date creationTime;
    // 세션 최종 접근 시간
    private Date lastAccessedTime;
    // 접속한 클라이언트 IP
    private String clientIp = null;

    public KhanSessionMetadata() {

    }

    /**
     * 세션이 invalidate되었는지 정보
     * @return
     */
    public Boolean getInvalidated() {
        return invalidated;
    }

    /**
     * 세션이 invalidate되었는지 정보 설정
     * @param invalidated
     */
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

    @Override
    public String toString() {
        return "KhanSessionMetadata{" +
                "invalidated=" + invalidated +
                ", creationTime=" + creationTime +
                ", lastAccessedTime=" + lastAccessedTime +
                ", clientIp='" + clientIp + '\'' +
                '}';
    }
}