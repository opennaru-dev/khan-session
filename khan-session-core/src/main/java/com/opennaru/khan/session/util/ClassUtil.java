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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * ClassLoader Utility
 *
 * @since 1.1.0
 * @author Junshik Jeon(service@opennaru.com, nameislocus@gmail.com)
 */
public class ClassUtil {

    /**
     * Load Class
     * @param classname
     * @param userClassLoader
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> Class<T> loadClass(String classname, ClassLoader userClassLoader) throws ClassNotFoundException {
        ClassLoader[] cls = getClassLoaders(userClassLoader);
        ClassNotFoundException e = null;
        NoClassDefFoundError ne = null;

        for (ClassLoader cl : cls) {
            if (cl == null)
                continue;

            try {
                return (Class<T>) Class.forName(classname, true, cl);
            } catch (ClassNotFoundException ce) {
                e = ce;
            } catch (NoClassDefFoundError ce) {
                ne = ce;
            }
        }

        if (e != null) {
            throw e;
        } else if (ne != null) {
            throw new ClassNotFoundException(classname, ne);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * create instance with class
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> T getInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        // first look for a getInstance() constructor
        T instance = null;
        try {
            Method factoryMethod = getFactoryMethod(clazz);
            if (factoryMethod != null) instance = (T) factoryMethod.invoke(null);
        } catch (Exception e) {
            // no factory method or factory method failed.  Try a constructor.
            instance = null;
        }

        if (instance == null) {
            instance = clazz.newInstance();
        }
        return instance;
    }

    /**
     * create instance with classname string
     *
     * @param classname
     * @param cl
     * @param <T>
     * @return
     */
    public static <T> T getInstance(String classname, ClassLoader cl) {
        if (classname == null) throw new IllegalArgumentException("Cannot load null class!");
        try {
            Class<T> clazz = loadClass(classname, cl);
            return getInstance(clazz);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        }
        return null;
    }

    /**
     * return resource as stream
     *
     * @param resourcePath
     * @param userClassLoader
     * @return
     */
    public static InputStream getResourceAsStream(String resourcePath, ClassLoader userClassLoader) {
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        InputStream is = null;
        for (ClassLoader cl : getClassLoaders(userClassLoader)) {
            if (cl != null) {
                is = cl.getResourceAsStream(resourcePath);
                if (is != null) {
                    break;
                }
            }
        }
        return is;
    }

    /**
     * return classloaders
     *
     * @param appClassLoader
     * @return
     */
    public static ClassLoader[] getClassLoaders(ClassLoader appClassLoader) {
        return new ClassLoader[]{
                appClassLoader,
                ClassUtil.class.getClassLoader(),
                ClassLoader.getSystemClassLoader(),
                Thread.currentThread().getContextClassLoader()
        };
    }

    /**
     * get factory method
     *
     * @param c
     * @return
     */
    private static Method getFactoryMethod(Class<?> c) {
        for (Method m : c.getMethods()) {
            if (m.getName().equals("getInstance") && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()))
                return m;
        }
        return null;
    }
}
