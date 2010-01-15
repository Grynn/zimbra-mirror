/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.data.BaseEntry;

import java.net.URL;
import java.net.MalformedURLException;

public final class Gab {
    public static final String BASE_URL = OfflineLC.zdesktop_gab_base_url.value();

    public static final String APP_NAME = String.format("Zimbra-%s-%s",
        OfflineLC.zdesktop_name.value(), OfflineLC.zdesktop_version.value());

    public static final String CONTACTS = "/contacts/";
    public static final String GROUPS = "/groups/";


    public static boolean isContactId(String id) {
        return id != null && id.contains(CONTACTS);
    }

    public static boolean isGroupId(String id) {
        return id != null && id.contains(GROUPS);
    }

    public static URL getEditUrl(BaseEntry entry) throws MalformedURLException {
        return new URL(entry.getEditLink().getHref());
    }

    public static URL toUrl(String url) throws ServiceException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw ServiceException.FAILURE("Bad URL format: " + url, null);
        }
    }
}
