/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2011 Zimbra, Inc.  All Rights Reserved.
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
