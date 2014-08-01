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

import com.opennaru.khan.session.util.ClassUtil;
import com.opennaru.khan.session.util.StackTraceUtil;
import org.infinispan.commons.marshall.Marshaller;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by jjeon on 14. 7. 31.
 */
public class JBossMarshaller implements KhanMarshaller {

    private static boolean DEBUG = false;
    private Marshaller marshaller;
    private boolean USE_SNAPPY_COMPRESSION = false;



    public JBossMarshaller() {
        if( System.getProperty("khan.marshaller.compress", "false").equals("true") ) {
            USE_SNAPPY_COMPRESSION = true;
        }

        if( System.getProperty("khan.marshaller.debug", "false").equals("true") ) {
            DEBUG = true;
        }

        marshaller = ClassUtil.getInstance("org.infinispan.commons.marshall.jboss.GenericJBossMarshaller",
                this.getClass().getClassLoader());
    }


    public boolean isMarshallable(Object o) {
        return true;
    }

    public Object objectFromByteBuffer(byte[] buf) throws IOException {
        byte[] uncompressBuf;
        ByteArrayInputStream bais = null;
        Object o = null;

//        System.out.println(StackTraceUtil.getStackTrace(Thread.currentThread().getStackTrace()) );

        try {
            if (USE_SNAPPY_COMPRESSION) {
                uncompressBuf = Snappy.uncompress(buf);
                if (DEBUG) {
                    System.out.println("KhanFSTMarshaller/toObject/SIZE=" + buf.length + "/UNCOMPRESS=" + uncompressBuf.length);
                }

                return MarshallerUtil.bytes2obj(marshaller, uncompressBuf);
            } else {
                if (DEBUG) {
                    System.out.println("KhanFSTMarshaller/toObject/SIZE=" + buf.length);
                }
                return MarshallerUtil.bytes2obj(marshaller, buf);
            }


        } catch (Exception e) {
            throw new IOException("Exception");
        }
    }


    public byte[] objectToBytes(Object o) throws IOException {
        try {
            byte[] bytes = MarshallerUtil.obj2bytes(marshaller, o);

//            System.out.println(StackTraceUtil.getStackTrace(Thread.currentThread().getStackTrace()) );

            if( USE_SNAPPY_COMPRESSION ) {
                byte[] compressBuf = Snappy.compress(bytes);
                if( DEBUG ) {
                    System.out.println("KhanGridMarshaller/SIZE=" + bytes.length + "/COMPRESS=" + compressBuf.length);
                }
                return compressBuf;
            } else {
                if( DEBUG ) {
                    System.out.println("KhanGridMarshaller/SIZE=" + bytes.length );
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new IOException("Exception");
        }
    }

}
