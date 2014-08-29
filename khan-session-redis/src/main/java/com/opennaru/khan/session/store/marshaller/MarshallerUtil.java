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

package com.opennaru.khan.session.store.marshaller;

import org.infinispan.commons.marshall.Marshaller;

import java.io.IOException;

/**
 * Marshaller Utility
 *
 * @since 1.1.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class MarshallerUtil {

    /**
     * Byte Array를 객체로 변환
     * @param marshaller
     * @param bytes
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T bytes2obj(Marshaller marshaller, byte[] bytes) {
        if (bytes == null) return null;
        Object object = null;

        try {
            object = (T) marshaller.objectFromByteBuffer(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T) object;
    }

    /**
     * 객체를 byte array로 변환
     *
     * @param marshaller
     * @param o
     * @return
     */
    public static byte[] obj2bytes(Marshaller marshaller, Object o) {
        try {
            return marshaller.objectToByteBuffer(o);
        } catch (IOException ioe) {
            //throw new IOException("Unable to marshall object of type [" + o.getClass().getName() + "]", ioe);
            return null;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
