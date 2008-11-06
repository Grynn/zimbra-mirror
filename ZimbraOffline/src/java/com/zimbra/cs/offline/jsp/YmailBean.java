package com.zimbra.cs.offline.jsp;

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
	host = "imap-ssl.mail.yahoo.com";
	isSsl = true;
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

