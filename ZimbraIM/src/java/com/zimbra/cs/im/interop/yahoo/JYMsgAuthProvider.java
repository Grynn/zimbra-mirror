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