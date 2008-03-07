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
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Document;

public class SearchRequest extends Request {
    private static final String ACTION = "searchContacts";
    
    public SearchRequest(Session session) {
        super(session);
    }

    @Override
    protected String getAction() {
        return ACTION;
    }

    @Override
    protected Response parseResponse(Document doc) {
        return SearchResponse.fromXml(doc.getDocumentElement());
    }
}
