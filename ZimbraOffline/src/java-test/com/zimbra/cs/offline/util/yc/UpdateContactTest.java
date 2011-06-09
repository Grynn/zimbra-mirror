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

import java.io.InputStream;
import java.io.StringReader;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.oauth.OAuthGetRequestTokenRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthGetRequestTokenResponse;
import com.zimbra.cs.offline.util.yc.oauth.OAuthGetTokenRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthGetTokenResponse;
import com.zimbra.cs.offline.util.yc.oauth.OAuthHelper;
import com.zimbra.cs.offline.util.yc.oauth.OAuthPutContactRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthResponse;
import com.zimbra.cs.offline.util.yc.oauth.OAuthToken;

//sorry, right now it only works for my profile as fields ids are unique for each yahoo account
public class UpdateContactTest {

    @Test
    public void testUpdateContact() throws Exception {
        OAuthRequest req = new OAuthGetRequestTokenRequest(new OAuthToken());
        String resp = req.send();
        OAuthResponse response = new OAuthGetRequestTokenResponse(resp);
        System.out.println("paste it into browser and input the highlighted codes below: "
                + response.getToken().getNextUrl());

        System.out.print("Verifier: ");
        Scanner scan = new Scanner(System.in);
        String verifier = scan.nextLine();
        req = new OAuthGetTokenRequest(response.getToken(), verifier);
        resp = req.send();
        response = new OAuthGetTokenResponse(resp);
        OAuthToken token = response.getToken();

        try {
            InputStream stream = this.getClass().getClassLoader()
                    .getResourceAsStream("yahoo_contacts_client_update_dummy.xml");
            String content = OAuthHelper.getStreamContents(stream);

            req = new OAuthPutContactRequest(token, content);
            resp = req.send();
            System.out.println("resp:" + resp);
            DocumentBuilder builder = Xml.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(resp)));
            Element syncResult = doc.getDocumentElement();
            int rev = Xml.getIntAttribute(syncResult, "yahoo:rev");
            System.out.println("new rev: " + rev);
            Element result = Xml.getChildren(syncResult).get(0);
            Element contacts = Xml.getChildren(result).get(0);
            boolean success = false;
            for (Element child : Xml.getChildren(contacts)) {
                if ("response".equals(child.getNodeName())) {
                    Assert.assertEquals("success", child.getTextContent());
                    success = true;
                }
                if ("id".equals(child.getNodeName())) {
                    String id = child.getTextContent();
                    System.out.println("get new contact id: " + id);
                }
            }
            if (!success) {
                Assert.fail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
