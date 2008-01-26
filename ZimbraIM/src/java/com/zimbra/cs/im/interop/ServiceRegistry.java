package com.zimbra.cs.im.interop;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry of all interop services in the system.  Entries are added to this registry at any
 * time during the boot process.  When the IM subsystem is activated, all of the services in this
 * registry (that return isEnabled=TRUE) are instantiated and added to the list of active services
 * see {@see Interop.getAvailableServices}
 */
public final class ServiceRegistry {
    private static Map<String, SessionFactory> sServiceMap = Collections.synchronizedMap(new HashMap<String, SessionFactory>());
    
    public static void register(SessionFactory factory) {
        if (sServiceMap.containsKey(factory.getName()))
            throw new IllegalStateException("A service of name "+factory.getName()+" has already been registered");
        sServiceMap.put(factory.getName(), factory);
        
        Interop.getInstance().enableServices();
    }
    
    public static Collection<SessionFactory> values() { return sServiceMap.values(); }
    public static SessionFactory valueOf(String name) { return sServiceMap.get(name); }
}