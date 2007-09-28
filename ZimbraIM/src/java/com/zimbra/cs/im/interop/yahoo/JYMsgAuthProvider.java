/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.yahoo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This auth provider uses the jYMSG auth library, if it is available.
 */
public class JYMsgAuthProvider implements YMSGAuthProvider {
    
    public static boolean available() {
        try {
            Class c = Class.forName("ymsg.network.ChallengeResponseV10");
            c.getDeclaredMethod("getStrings", new Class[] { String.class, String.class, String.class });
            return true;
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) { 
        }
        return false;
    }
    
    public String[] calculateChallengeResponse(String username, String password, String challenge) {
        try {
            Class c = Class.forName("ymsg.network.ChallengeResponseV10");
            Method m = c.getDeclaredMethod("getStrings", new Class[] { String.class, String.class, String.class });
            m.setAccessible(true);
            return (String[]) m.invoke(null, new Object[] { username, password, challenge });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) { 
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return new String[] { "", "" };
    }
}