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
package com.zimbra.cs.im.interop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.JID;

import com.zimbra.common.util.TaskScheduler;
import com.zimbra.common.util.ZimbraLog;

/**
 * Manages a set of {@link Service} instances which each connect to a remote IM
 * service. This class handles startup/shutdown of all interop services
 */
public class Interop {
    
    static final TaskScheduler<Void> sTaskScheduler = new TaskScheduler<Void>("IM Interop", 1, 4);
    
    
    private static final String SERVICES_GROUP = "Interop";
    
    private boolean mIsRunning = false;

    public boolean isRunning() { return mIsRunning; } 
    
    /**
     * Called to initialize and start all enabled services. May be called
     * repeatedly, will enable/disable services if the system configuration
     * changes
     */
    public synchronized void start(XMPPServer srv, ComponentManager cm) {
        assert(mCm == null || mCm == cm); // cm better not change at runtime!
        mCm = cm;
        
        mIsRunning = true;
        enableServices();
    }
    
    void enableServices() {
        if (!mIsRunning) 
            return;
        
        for (SessionFactory fact : ServiceRegistry.values()) {
            if (fact.isEnabled()) {
                try {
                    if (!mAvailableServices.containsKey(fact.getName())) {
                        Service service = new Service(fact);
                        mAvailableServices.put(fact.getName(), service);
                        mCm.addComponent(fact.getName(), service);
                    }
                } catch (Exception e) {
                    ZimbraLog.im.error("Interop: Caught exception initializing "+fact.getName()+" Service", e);
                    mAvailableServices.remove(fact.getName());
                }
            } else {
                if (mAvailableServices.containsKey(fact.getName())) {
                    Service service = mAvailableServices.remove(fact.getName());
                    if (service != null)
                        service.shutdown();
                    try {
                        mCm.removeComponent(fact.getName());
                    } catch (ComponentException e) {
                        ZimbraLog.im.info("Interop: Caught exception removing "+fact.getName()+" Service.  Ignoring.");
                    }
                }
            }
        }
    }
    
    /**
     * Stop the interop subsystem
     */
    public synchronized void stop() {
        mIsRunning = false;
        
        for (Service service : mAvailableServices.values()) {
            if (service != null)
                service.shutdown();
            try {
                mCm.removeComponent(service.getName());
            } catch (ComponentException e) {
                ZimbraLog.im.info("Interop: Caught exception removing "+service.getName()+" Service.  Ignoring.");
            }
        }
        mAvailableServices.clear();
    }

    
    public UserStatus getRegistrationStatus(String serviceName, JID jid) throws ComponentException{
        UserStatus toRet = mAvailableServices.get(serviceName).getConnectionStatus(jid);
        if (toRet != null) 
            toRet.password = "*****";
        return toRet;
    }
    
    public void reconnectUser(String serviceName, JID jid) throws ComponentException, UserNotFoundException  {
        Service service = mAvailableServices.get(serviceName);
        UserStatus status = service.getConnectionStatus(jid);
        if (status == null) {
            throw new ComponentException("User not registered");
        }
        service.disconnectUser(jid);
        service.connectUser(jid, status.username, status.password, service.getDescription(), SERVICES_GROUP);
    }
    
    /**
     * Called to register a user to a specified Interop service. It only needs to
     * be called one time per user, or after disconnectUser() is called to
     * re-register the user
     * 
     * @param service  The service
     * @param          jid JID of the the local user that wants to connect
     * @param          name The user's name on the remote service (e.g. foo@hotmail.com)
     * @param password The user's password on the remote service
     * @return
     * @throws ComponentException
     * @throws UserNotFoundException
     *         The specified JID was invalid
     */
    public void registerUser(String serviceName, JID jid, String name, String password)
                throws ComponentException, UserNotFoundException {
        Service service = mAvailableServices.get(serviceName);
        service.registerUser(jid, name, password, service.getDescription(), SERVICES_GROUP);
    }
    
    /**
     * Disconnect the specified user from the remote service.
     * 
     * @param type
     *        The service to disconnect from
     * @param jid
     *        The jid of the local user to be disconnected
     * @throws ComponentException
     * @throws UserNotFoundException
     *         The specified JID was invalid
     */
    public void unregisterUser(String serviceName, JID jid) throws ComponentException, UserNotFoundException {
        Service service = mAvailableServices.get(serviceName);
        service.disconnectUser(jid);
    }
    
    /**
     * Shameless hack to get around auth forbidden with probe requests right now
     *  
     * @param jid
     *        The jid of the user that needs refreshed presence info
     */
    public void refreshAllPresence(JID jid) {
        for (Service service : mAvailableServices.values())
                service.refreshAllPresence(jid);
    }
    
    /**
     * @param jid
     * @return
     */
    public boolean isInteropJid(JID jid) {
        String domain = jid.getDomain();
        String[] split = domain.split("\\.");
        if (split.length >= 3) {
            for (Service service : mAvailableServices.values()) {
                if (split[0].equals(service.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @return an array of available services. 
     */
    public synchronized List<String> getAvailableServices() {
        List<String> toRet = new ArrayList<String>(mAvailableServices.size());
        for (String name : mAvailableServices.keySet())
            toRet.add(name);
        return toRet;
    }
    
    public static final void setDataProvider(InteropRegistrationProvider provider) {
        mProvider = provider;
    }

    public static final InteropRegistrationProvider getDataProvider() { return mProvider; }
    
    private Interop() { }

    private static InteropRegistrationProvider mProvider = null;
    private ComponentManager mCm = null;
    private Map<String, Service> mAvailableServices = new HashMap<String, Service>();
    private static Interop sInstance = new Interop();
    public static Interop getInstance() { return sInstance; }
}
