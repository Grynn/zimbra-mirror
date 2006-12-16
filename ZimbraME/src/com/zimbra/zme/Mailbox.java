// ***** BEGIN LICENSE BLOCK *****
// Version: MPL 1.1
//
// The contents of this file are subject to the Mozilla Public License
// Version 1.1 ("License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at
// http://www.zimbra.com/license
//
// Software distributed under the License is distributed on an "AS IS"
// basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
// the License for the specific language governing rights and limitations
// under the License.
//
// The Original Code is: Zimbra Collaboration Suite Server.
//
// The Initial Developer of the Original Code is Zimbra, Inc.
// Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
// All Rights Reserved.
//
// Contributor(s):
//
// ***** END LICENSE BLOCK *****
package com.zimbra.zme;

import com.zimbra.zme.client.AuthCmd;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

public class Mailbox {

    private AuthCmd mAuth;
    private String mUrl;

    public Mailbox(String url) {
        mUrl = url;
    }
    public void login(String username,
                      String passwd) {
        try {
            if (mAuth == null)
                mAuth = new AuthCmd(mUrl);
            mAuth.exec(username, passwd);
        } catch (ZmeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
