/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.util;

import java.io.InputStream;

/**
 * A utility class to assist with loading classes or resources by name. Many application servers use
 * custom classloaders, which will break uses of:
 * <pre>
 *    Class.forName(className);
 * </pre>
 *
 * This utility attempts to load the class or resource using a number of different mechanisms to
 * work around this problem.
 *
 * @author Matt Tucker
 */
public class ClassUtils {

    private static ClassUtils instance = new ClassUtils();

    /**
     * Loads the class with the specified name.
     *
     * @param className the name of the class
     * @return the resulting <code>Class</code> object
     * @throws ClassNotFoundException if the class was not found
     */
    public static Class forName(String className) throws ClassNotFoundException {
        return instance.loadClass(className);
    }

    /**
     * Loads the given resource as a stream.
     *
     * @param name the name of the resource that exists in the classpath.
     * @return the resource as an input stream or <tt>null</tt> if the resource was not found.
     */
    public static InputStream getResourceAsStream(String name) {
        return instance.loadResource(name);
    }

    /**
     * Not instantiatable.
     */
    private ClassUtils() {}

    public Class loadClass(String className) throws ClassNotFoundException {
        Class theClass = null;
        try {
            theClass = Class.forName(className);
        }
        catch (ClassNotFoundException e1) {
            try {
                theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
            }
            catch (ClassNotFoundException e2) {
                theClass = getClass().getClassLoader().loadClass(className);
            }
        }
        return theClass;
    }

    private InputStream loadResource(String name) {
        InputStream in = getClass().getResourceAsStream(name);
        if (in == null) {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
            if (in == null) {
                in = getClass().getClassLoader().getResourceAsStream(name);
            }
        }
        return in;
    }
}
