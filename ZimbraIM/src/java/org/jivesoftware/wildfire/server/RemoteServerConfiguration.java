/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.server;

/**
 * Holds the configuration when connecting to/from a remote server. The configuration specifies
 * if incoming or outgoing connections are allowed to the remote server and the port to use
 * when creating an outgoing connection.
 *
 * @author Gaston Dombiak
 */
public class RemoteServerConfiguration {

    private String domain;

    private Permission permission;

    private int remotePort;

    public RemoteServerConfiguration(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public enum Permission {
        /**
         * The XMPP entity is allowed to connect to the server.
         */
        allowed,

        /**
         * The XMPP entity is NOT allowed to connect to the server.
         */
        blocked;
    }
}
