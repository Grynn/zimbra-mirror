package com.zimbra.cs.offline.jsp;

public class AmailBean extends ImapBean {
    public static final String Domain = "aol.com";

    public AmailBean() {}

    @Override
    protected void doRequest() {
	domain = Domain;
        if (verb != null && (verb.isAdd() || verb.isModify())) {
            if (!isEmpty(email) && email.indexOf('@') < 0)
                email += '@' + domain;
	    if (username.endsWith("@" + domain))
		username = username.substring(0, username.length() - 1 -  domain.length());
	    else if (username.indexOf('@') >= 0 || isEmpty(username))
		addInvalid("username");
	    else
		username = email;
        }
	host = "imap.aol.com";
	isSsl = false;
	port = "143";

	smtpHost = "smtp.aol.com";
	smtpPort = "465";
	isSmtpSsl = true;
	isSmtpAuth = true;
	smtpUsername = username;
	smtpPassword = password;
	super.doRequest();
    }

    public boolean isServerConfigSupported() {
	return false;
    }

    public boolean isSmtpConfigSupported() {
	return false;
    }
}

