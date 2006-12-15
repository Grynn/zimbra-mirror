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

package com.zimbra.zme.client;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Auth extends Command{

    private String mAuthToken;
    
    public Auth(String url) {
        super(url);
    }

    public void exec(String acctName,
                     String passwd)
            throws IOException,
                   XmlPullParserException {
        beginReq();
        beginReqBody();

        mSerializer.setPrefix("", NS_ZIMBRA_ACCT);
        mSerializer.startTag(NS_ZIMBRA_ACCT, EL_AUTH_REQ);

        mSerializer.startTag(null, EL_ACCT);
        mSerializer.attribute(null, AT_BY, NAME);
        mSerializer.text(acctName);
        mSerializer.endTag(null, EL_ACCT);

        mSerializer.startTag(null, EL_PASSWD);
        mSerializer.text(passwd);
        mSerializer.endTag(null, EL_PASSWD);

        mSerializer.endTag(NS_ZIMBRA_ACCT, EL_AUTH_REQ);

        endReqBody();
        endReq();

        handleResp();
    }

}
