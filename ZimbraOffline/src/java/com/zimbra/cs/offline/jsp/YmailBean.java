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
package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class YmailBean extends ImapBean {
    public static final String Domain = "yahoo.com";

    public YmailBean() {}

    @Override
    protected void doRequest() {
	domain = Domain;
	if (verb != null && (verb.isAdd() || verb.isModify())) {
	    if (!isEmpty(email)) {
	    	if (email.indexOf('@') < 0)
		    email += '@' + domain;
	
		int atSign = email.indexOf("@yahoo.");

		if (atSign > 0)	// username of yahoo.* email is without @domain
		    username = email.substring(0, atSign);
		else
		    username = email;
	    }
	}
	host = email.endsWith("@yahoo.co.jp") ?
            "zimbra.imap.mail.yahoo.co.jp" : "zimbra.imap.mail.yahoo.com";
        connectionType = DataSource.ConnectionType.ssl;
	port = "993";
	super.doRequest();
    }

    public boolean isCalendarSyncSupported() {
	return true;
    }

    public boolean isContactSyncSupported() {
	return true;
    }

    public boolean isServerConfigSupported() {
	return false;
    }

    public boolean isSmtpConfigSupported() {
	return false;
    }

    public boolean isUsernameRequired() {
	return false;
    }
}

