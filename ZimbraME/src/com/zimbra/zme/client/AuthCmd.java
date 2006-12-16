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

import com.zimbra.zme.ZmeException;

public class AuthCmd extends Command{

    private static String EL_AUTH_REQ = "AuthRequest";
    private static String EL_AUTH_RESPONSE = "AuthResponse";

    private String mAuthToken;
    private String mSessionId;
    
    public AuthCmd(String url)
            throws ZmeException {
        super(url);
    }

    public void exec(String acctName,
                     String passwd)
            throws ZmeException {
        try {
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
        } catch (IOException ex1) {
            throw new ZmeException(ZmeException.IO_ERROR, ex1.getMessage());
        } catch (XmlPullParserException ex2) {
            throw new ZmeException(ZmeException.PARSER_ERROR, ex2.getMessage());                
        }
    }

    protected void processCmd(XmlPullParser parser)
            throws IOException,
                   XmlPullParserException {
        int matches = 0;
        while (matches < 2) {
            parser.nextTag();
            String elName = parser.getName();
            if (elName.equalsIgnoreCase(EL_AUTH_TOKEN)) {
                mAuthToken = parser.nextText();
                matches++;
            } else if (elName.equalsIgnoreCase(EL_SESSION_ID)) {
                mSessionId = parser.nextText();
                matches++;
            } else {
                skipToEnd(elName);
            }
        }
        skipToEnd(EL_AUTH_RESPONSE);
    }
}
