/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.cs.offline.yab;

import com.zimbra.cs.offline.OfflineLC;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Yahoo Address book access.
 */
public class Yab {
    private final RawAuth auth;
    private final String format;
    private final NameValuePair[] params;
    
    private static final String BASE_URI = OfflineLC.zdesktop_yab_baseuri.value();
    private static final String FORMAT = "format";

    public static final String XML = "xml";
    public static final String JSON = "json";

    public static final String SEARCH_CONTACTS = "searchContacts";
    public static final String GET_CATEGORIES = "getCategories";
    public static final String ADD_CONTACTS = "addContacts";
    public static final String SYNCHRONIZE = "synchronize";

    public Yab(RawAuth auth, String format, NameValuePair[] params) {
        this.auth = auth;
        this.format = format;
        this.params = params;
    }

    public Yab(RawAuth auth, NameValuePair[] params) {
        this(auth, XML, params);
    }

}
