/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yc.oauth;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.YContactException;

public class OAuthGetContactsResponse extends OAuthResponse {

    private static DocumentBuilder docBuilder = Xml.newDocumentBuilder();

    public OAuthGetContactsResponse(String resp) throws YContactException {
        super(resp);
    }

    @Override
    protected void handleResponse() throws YContactException {
        try {
            Document doc = docBuilder.parse(new InputSource(new StringReader(getRawResponse())));
            Element root = doc.getDocumentElement();

            OfflineLog.yab.info("client rev: %s, server rev: %s", root.getAttribute("yahoo:clientrev"),
                    root.getAttribute("yahoo:rev"));

        } catch (Exception e) {
            throw new YContactException("error while creating xml document", "", false, e, null);
        }
    }

}
