package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class YmailBean extends XmailBean {

	public YmailBean() {}
	
	private void fixup() {
		if (verb == null)
			return;
		
		if (isEmpty(dsName))
			addInvalid("dataSourceName");
		if (isEmpty(password))
	    	addInvalid("password");
		
		domain = "yahoo.com";
		if (username.endsWith("@" + domain)) {
			username = username.substring(0, username.length() - 1 -  domain.length());
		} else if (username.indexOf('@') >= 0 || isEmpty(username)) {
			addInvalid("username");
		}
		email = username + '@' + domain;
		
		protocol = DataSource.Type.imap.toString();
		host = "imap.mail.yahoo.com";
		port = "143";
		isSsl = false;
		
		smtpHost = "smtp.mail.yahoo.com";
		smtpPort = "465";
		isSmtpSsl = true;
		isSmtpAuth = true;
		smtpUsername = username;
		smtpPassword = password;
	}
	
	@Override
	protected void doRequest() {
		fixup();
		super.doRequest();
	}

	@Override
	protected void reload() {
		super.reload();
	}
}
