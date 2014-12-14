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

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by jjeon on 12/13/14.
 */
public class TestPropertyUtil {
    @Test
    public void testProperty() {
        Assert.assertEquals("1", PropertyUtil.getProperty("${test:1}"));
        Assert.assertEquals("", PropertyUtil.getProperty("${test}"));

        System.setProperty("test", "2");
        Assert.assertEquals("2", PropertyUtil.getProperty("${test}"));
        Assert.assertEquals("2", PropertyUtil.getProperty("${test:1}"));
    }
}
