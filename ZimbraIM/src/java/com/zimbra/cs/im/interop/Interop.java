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

import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.JID;

/**
 * Manages a set of {@link Service} instances which each connect to a remote IM
 * service. This class handles startup/shutdown of all interop services
 */
public class Interop {
    /**
     * A list of all the services we support
     */
    public static enum ServiceName {
        msn("MSN IM Service");

        ServiceName(String description) {
            mDescription = description;
        }
        public String getDescription() {
            return mDescription;
        }

        private String mDescription;
    }

    /**
     * Called to initialize and start all enabled services. May be called
     * repeatedly, will enable/disable services if the system configuration
     * changes
     */
    public synchronized static void start(XMPPServer srv, ComponentManager cm) {
        sCm = cm;

        try {
            sMsn =
                new Service("msn", "MSN Gateway", new SessionFactory() {
                    public Session createSession(Service service, JID jid, String name,
                                String password) {
                        return new MsnSession(service, new JID(jid.toBareJID()), name,
                                    password);
                    }
                });
            sAvailableServices.add(ServiceName.msn);
            sCm.addComponent(ServiceName.msn.name(), sMsn);
        } catch (Exception e) {
            Log.error("Caught exception initializing Interop", e);
            sMsn = null;
        }
    }

    /**
     * Stop the interop subsystem
     */
    public synchronized static void stop() {
        if (sMsn != null) {
            try {
                sCm.removeComponent(ServiceName.msn.name());
            } catch (ComponentException ex) {
                Log.info("Caught exception shutting down msn component", ex);
            }
            sMsn = null;
        }
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
    public static void connectUser(ServiceName type, JID jid, String name, String password)
                throws ComponentException, UserNotFoundException {
        switch (type) {
            case msn:
                if (sMsn == null)
                    throw new ComponentException("Service not running: " + type);
                else
                    sMsn.connectUser(jid, name, password, "MSN Gateway", "Interop");
                break;
            default:
                throw new ComponentException("Unknown service type: " + type);
        }
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
    public static void disconnectUser(ServiceName type, JID jid)
                throws ComponentException, UserNotFoundException {
        switch (type) {
            case msn:
                sMsn.disconnectUser(jid);
            break;
            default:
                throw new ComponentException("Unknown service type: " + type);
        }
    }
    
    /**
     * Shameless hack to get around auth forbidden with probe requests right now
     *  
     * @param jid
     *        The jid of the user that needs refreshed presence info
     */
    public static void refreshPresence(JID jid) {
        if (sMsn != null) 
            sMsn.refreshPresence(jid);
    }
    
    /**
     * @param jid
     * @return
     */
    public static boolean isInteropJid(JID jid) {
        String domain = jid.getDomain();
        String[] split = domain.split("\\.");
        if (split.length < 3)
            return false;
        if (split[0].equals(ServiceName.msn.name()))
            return true;
        return false;
    }

    /**
     * @return an array of available services. Note that you can get descriptive
     *         info about the service by calling ServiceName.getDescription()
     */
    public static List<ServiceName> getAvailableServices() {
        return sAvailableServices;
    }

    private static ComponentManager sCm = null;
    private static Service sMsn = null;
    private static List<ServiceName> sAvailableServices = new ArrayList<ServiceName>();
}
