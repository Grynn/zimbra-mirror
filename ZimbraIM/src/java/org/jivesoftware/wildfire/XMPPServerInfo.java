/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package org.jivesoftware.wildfire;

import org.jivesoftware.util.Version;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Information 'snapshot' of a server's state. Useful for statistics
 * gathering and administration display.
 *
 * @author Iain Shigeoka
 */
public interface XMPPServerInfo {

    /**
     * Obtain the server's version information. Typically used for iq:version
     * and logging information.
     *
     * @return the version of the server.
     */
    public Version getVersion();

    /**
     * Obtain the server name (ip address or hostname).
     *
     * @return the server's name as an ip address or host name.
     */
    public Collection<String> getNames();
    
    
    /**
     * @return a default name for this server
     */
    public String getDefaultName();

    /**
     * Obtain the date when the server was last started.
     *
     * @return the date the server was started or null if server has not been started.
     */
    public Date getLastStarted();

    /**
     * Obtain the date when the server was last stopped.
     *
     * @return the date the server was stopped or null if server has not been
     *      started or is still running
     */
    public Date getLastStopped();

    /**
     * Obtain the server ports active on this server.
     *
     * @return an iterator over the server ports for this server.
     */
    public Iterator getServerPorts();
}