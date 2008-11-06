package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class LiveBean extends XmailBean {
    public static final String Domain = "hotmail.com";

    public LiveBean() {}

    @Override
    protected void doRequest() {
	domain = Domain;
        type = DataSource.Type.live.toString();
        if (verb != null && (verb.isAdd() || verb.isModify())) {
            if (!isEmpty(email) && email.indexOf('@') < 0)
                email += '@' + domain;
	    username = email;
        }
        host = "www.hotmail.com";
        isSsl = false;
        port = "80";
        super.doRequest();
    }

    public boolean isContactSyncSupported() {
	return true;
    }

    public boolean isFolderSyncSupported() {
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

