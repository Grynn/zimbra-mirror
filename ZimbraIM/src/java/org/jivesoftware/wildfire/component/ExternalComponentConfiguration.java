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
package org.jivesoftware.wildfire.component;

/**
 * Holds the configuration for external components that want to connect to this server. The
 * configuration specifies if the external component is allowed to connect to the server as well
 * as the shared secret between the server and the component. If no secret or configuration was
 * defined then the default shared secret will be used.
 *
 * @author Gaston Dombiak
 */
public class ExternalComponentConfiguration {

    private String subdomain;

    private Permission permission;

    private String secret;

    public ExternalComponentConfiguration(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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
