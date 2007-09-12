/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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