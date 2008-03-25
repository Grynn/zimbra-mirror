package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class AmailBean extends XmailBean {

	public AmailBean() {}
	
	private void fixup() {
		if (verb == null)
			return;
		
		if (isEmpty(dsName))
			addInvalid("dataSourceName");
		if (isEmpty(password))
	    	addInvalid("password");
		
		domain = "aol.com";
		if (username.endsWith("@" + domain)) {
			username = username.substring(0, username.length() - 1 -  domain.length());
		} else if (username.indexOf('@') >= 0 || isEmpty(username)) {
			addInvalid("username");
		}
		email = username + '@' + domain;
		
		protocol = DataSource.Type.imap.toString();
		host = "imap.aol.com";
		port = "143";
		isSsl = false;
		
		smtpHost = "smtp.aol.com";
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
