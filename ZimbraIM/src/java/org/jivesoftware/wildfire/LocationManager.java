/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire;

import java.util.List;

import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.packet.JID;

import com.zimbra.common.service.ServiceException;

/**
 * This class handles intra-cloud routing of users. 
 */
public interface LocationManager {
    public static class ComponentIdentifier {
        // These allow us to handle disco#items requests, as well as boot the component
        public String serviceName; // just the short name of the service
        public String serviceDomain; // routable XMPP domain of this service
        
        // These allow us to handle toplevel disco#info requests
        public String category;
        public String type;
        public List<String> features;
        
        // classname of class that actually runs the component
        public String className;
        
        public String toString() {
            return serviceName+"."+serviceDomain+" category="+category+" type="+type+" class="+className;
        }
    }
    
    /**
     * Returns true if the given address is local to the server (managed by this
     * server domain). Return false even if the jid's domain matches a local component's
     * service JID.
     *
     * @param jid the JID to check.
     * @return true if the address is a local address to this server.
     */
    public boolean isLocal(JID jid) throws UserNotFoundException;

    public boolean isRemote(JID jid)  throws UserNotFoundException;
    
    public List<ComponentIdentifier> getAllServerComponents() throws ServiceException;
    
    public List<ComponentIdentifier> getRemoteServerComponents() throws ServiceException;
    
    /**
     * Return a list of components which are running on this server.  Used to determine
     * which components to initialize at starup (and over what domains) 
     * 
     * @param componentType
     * @return
     * @throws ServiceException
     */
    public List<ComponentIdentifier> getThisServerComponents(String componentType) throws ServiceException;
    
    
    /**
     * Return true if this domain points to a component which is running in this cloud.  Used for
     * routing.
     * 
     * @param jid
     * @return
     */
    public boolean isCloudComponent(String domain);
    
    /**
     * Return the server name that is hosting the specified component domain
     * 
     * @param domain
     * @return
     */
    public String getServerForComponent(String domain);
}
