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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * JBoss marshalling-rivier marshaller for Object from/to byte array
 * KHAN [session manager]의 기본 marshaller
 *
 * @since 1.1.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class JBossMarshaller implements KhanMarshaller {

    private Logger log = LoggerFactory.getLogger(this.getClass());

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
        if( log.isDebugEnabled() ) {
            DEBUG = true;
        }

        // 기본 Marshaller
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

        if( DEBUG )
            log.debug(StackTraceUtil.getStackTrace(Thread.currentThread().getStackTrace()) );

        try {
            if (USE_SNAPPY_COMPRESSION) {
                uncompressBuf = Snappy.uncompress(buf);
                if (DEBUG) {
                    log.debug("KhanFSTMarshaller/toObject/SIZE=" + buf.length + "/UNCOMPRESS=" + uncompressBuf.length);
                }

                return MarshallerUtil.bytes2obj(marshaller, uncompressBuf);
            } else {
                if (DEBUG) {
                    log.debug("KhanFSTMarshaller/toObject/SIZE=" + buf.length);
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

            if( DEBUG )
                log.debug( StackTraceUtil.getStackTrace(Thread.currentThread().getStackTrace()) );

            if( USE_SNAPPY_COMPRESSION ) {
                byte[] compressBuf = Snappy.compress(bytes);
                if( DEBUG ) {
                    log.debug("KhanGridMarshaller/SIZE=" + bytes.length + "/COMPRESS=" + compressBuf.length);
                }
                return compressBuf;
            } else {
                if( DEBUG ) {
                    log.debug("KhanGridMarshaller/SIZE=" + bytes.length );
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new IOException("Exception");
        }
    }

}
