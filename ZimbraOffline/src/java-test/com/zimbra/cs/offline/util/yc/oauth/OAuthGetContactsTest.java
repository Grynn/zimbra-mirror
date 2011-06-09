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

import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

public class OAuthGetContactsTest {

    @Test
    public void getContacts() {
        try {
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

            // use 0 for get all contacts, use other revision number (< server
            // rev) to retrieve the delta part
            req = new OAuthGetContactsRequest(response.getToken(), 0);
            resp = req.send();

            System.out.println(resp);
            Assert.assertNotNull(resp);

            response = new OAuthGetContactsResponse(resp);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
