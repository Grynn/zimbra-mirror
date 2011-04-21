package com.zimbra.cs.ldap;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.util.Zimbra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class LdapTODO {
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SDKTODO {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SDKDONE {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TODO {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TODOEXCEPTIONMAPPING {
        // verify if the exception mapping is indeed needed, and if so fix it.
        // test shows even in JNDI the exceptions cannot be thrown.
    }
    
    public static void TODO() {
        TODO(null);
    }
    
    public static void TODO(String message) {
        LdapException e = LdapException.TODO(); // get get the stack printed
        Zimbra.halt("TODO", e);
    }
    
    public static enum FailCode {
        /*
         * Turn JVM assertion on.  If this failure never happens, remove 
         * catching the LdapNameNotFoundException exception from all callsites.
         */
        NameNotFoundExceptionShouldNeverBeThrown,
        
        /*
         * Turn JVM assertion on.  If this failure never happens, remove 
         * catching the LdapInvalidNameException exception from all callsites.
         */
        LdapInvalidNameExceptionShouldNeverBeThrown,
        
    }
    /**
     * Should never happen.
     * 
     * Can be turned on/off by turning assertion in the JVM on/off
     */
    public static void FAIL(FailCode code) {
        ZimbraLog.ldap.error("assertion failure: " + code.name());
        assert(false);
    }
}
