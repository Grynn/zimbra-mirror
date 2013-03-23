/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
package com.zimbra.clientuploader;

import com.zimbra.common.localconfig.KnownKey;

/**
 * <code>ClientUploaderLC</code> holds the user configurations for Client Upload extension.
 *
 * @author Dongwei Feng
 * @since 2012.3.15
 */
public final class ClientUploaderLC {
    /**
     * A directory for client repository, default: /opt/zimbra/jetty/webapps/zimbra/downloads
     */
    public static final KnownKey client_repository_location = new KnownKey("client_repository_location", "/opt/zimbra/jetty/webapps/zimbra/downloads");

    /**
     * Max size of the uploaded file, default: 2G
     */
    public static final KnownKey client_software_max_size = new KnownKey("client_software_max_size").setDefault(2 * 1024 * 1024 * 1024);
}
