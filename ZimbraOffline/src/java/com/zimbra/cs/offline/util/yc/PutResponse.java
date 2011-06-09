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
package com.zimbra.cs.offline.util.yc;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.zimbra.cs.offline.util.Xml;

/**
 * 
 * for syncresult as a response for client's add/update/remove requests
 *
 */
public class PutResponse extends Response {

    private static DocumentBuilder docBuilder = Xml.newDocumentBuilder();
    
    public PutResponse(int retCode, String resp) {
        super(retCode, resp);
    }
    
    public void extract(SyncResult result) throws YContactException {
        Document doc;
        Element root;

        try {
            doc = docBuilder.parse(new InputSource(new StringReader(getResp())));
            root = doc.getDocumentElement();
            result.extractFromXml(root);
        } catch (Exception e) {
            throw new YContactException("parsing response error", "", false, e, null);
        }
    }
}
