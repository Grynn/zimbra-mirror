/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 ("License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc. Portions created
 * by Zimbra are Copyright (C) 2005 Zimbra, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.JID;

import com.zimbra.common.util.ZimbraLog;

/**
 * Manages a set of {@link Service} instances which each connect to a remote IM
 * service. This class handles startup/shutdown of all interop services
 */
public class Interop {
    /**
     * A list of all the services we support
     */
    public static enum ServiceName {
        msn   ("MSN IM",      MsnSession.getFactory()),
        aol   ("AIM",          AolSession.getFactory()),
        
//        yahoo("Yahoo IM",   YahooSession.getFactory()),
        ;

        private ServiceName(String description, SessionFactory fact) { 
            mDescription = description;
            mSessionFact = fact;
        }
        public String getDescription() { return mDescription; }
        public boolean isRunning() { return mService != null; }
        public boolean isEnabled() { return true; }
        private void start() {
            assert(isEnabled());
            assert(!isRunning());
            if (!isRunning()) {
                mService = new Service(this, mSessionFact);
            }
        }
        private void stop() { }
        private Service getService() throws ComponentException {
            if (!isRunning()) {
                throw new ComponentException("Service: "+this.name()+" is not running");
            }
            return mService; 
        }
        
        private String mDescription;
        private Service mService = null;
        private SessionFactory mSessionFact = null;
    }
    
    private static final String SERVICES_GROUP = "Interop";
    
    /**
     * Called to initialize and start all enabled services. May be called
     * repeatedly, will enable/disable services if the system configuration
     * changes
     */
    public synchronized void start(XMPPServer srv, ComponentManager cm) {
        assert(mCm == null || mCm == cm); // cm better not change at runtime!
        mCm = cm;
        mAvailableServices = new ArrayList<ServiceName>();
        
        for (ServiceName service : ServiceName.values()) {
            if (service.isEnabled() && !service.isRunning()) {
                try {
                    service.start();
                    mAvailableServices.add(service);
                    mCm.addComponent(service.name(), service.getService());
                } catch (Exception e) {
                    ZimbraLog.im.error("Interop: Caught exception initializing "+service.name()+" Service", e);
                    mAvailableServices.remove(service);
                    service.stop();
                }
            } else if (!service.isEnabled() && service.isRunning()) {
                service.stop();
                try {
                    mCm.removeComponent(service.name());
                } catch (ComponentException e) {
                    ZimbraLog.im.info("Interop: Caught exception removing "+service.name()+" Service.  Ignoring.");
                }
            }
        }
    }
    
    /**
     * Stop the interop subsystem
     */
    public synchronized void stop() {
        for (ServiceName service : ServiceName.values()) {
            if (service.isRunning()) {
                try {
                    mCm.removeComponent(service.name());
                } catch (ComponentException e) {
                    ZimbraLog.im.info("Interop: Caught exception removing "+service.name()+" Service.  Ignoring.");
                }
            }
        }
        mAvailableServices = new ArrayList<ServiceName>();
    }

    /**
     * Called to connect a user to a specified Interop service. It only needs to
     * be called one time per user, or after disconnectUser() is called to
     * re-connect the user
     * 
     * @param type
     *        The service
     * @param jid
     *        JID of the the local user that wants to connect
     * @param name
     *        The user's name on the remote service (e.g. foo@hotmail.com)
     * @param password
     *        The user's password on the remote service
     * @return
     * @throws ComponentException
     * @throws UserNotFoundException
     *         The specified JID was invalid
     */
    public void connectUser(ServiceName service, JID jid, String name, String password)
                throws ComponentException, UserNotFoundException {
        service.getService().connectUser(jid, name, password, service.getDescription(), SERVICES_GROUP);
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
    public void disconnectUser(ServiceName service, JID jid) throws ComponentException, UserNotFoundException {
        service.getService().disconnectUser(jid);
    }
    
    /**
     * Shameless hack to get around auth forbidden with probe requests right now
     *  
     * @param jid
     *        The jid of the user that needs refreshed presence info
     */
    public void refreshAllPresence(JID jid) {
        for (ServiceName service : ServiceName.values()) {
            if (service.isRunning()) { 
                try {
                    service.getService().refreshAllPresence(jid);
                } catch (ComponentException e) {
                    ZimbraLog.im.debug("Caught ComponentException in refreshPresence("
                                +jid+") for service "+service.name(), e);
                }
            }
        }
    }
    
    /**
     * @param jid
     * @return
     */
    public boolean isInteropJid(JID jid) {
        String domain = jid.getDomain();
        String[] split = domain.split("\\.");
        if (split.length >= 3) {
            for (ServiceName service : ServiceName.values()) {
                if (split[0].equals(service.name())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @return an array of available services. Note that you can get descriptive
     *         info about the service by calling ServiceName.getDescription()
     */
    public synchronized List<ServiceName> getAvailableServices() {
        return mAvailableServices;
    }
    
    private Interop() { }

    private ComponentManager mCm = null;
    private List<ServiceName> mAvailableServices = new ArrayList<ServiceName>();
    private static Interop sInstance = new Interop();
    public static Interop getInstance() { return sInstance; }
}
