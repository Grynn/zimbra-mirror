package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class YmailBean extends XmailBean {

	public YmailBean() {}
	
	private void fixup() {
		if (verb == null)
			return;
		
		if (isEmpty(accountName))
			addInvalid("accountName");
		if (isEmpty(email))
			addInvalid("email");
		if (isEmpty(password))
	    	addInvalid("password");
		
		domain = "yahoo.com";  //this is solely for datasource.properties mapping
		if (email.indexOf('@') < 0)
			email += '@' + domain;
		
		int atSign = email.indexOf("@yahoo.");
		if (atSign > 0) //username of yahoo.com or yahoo.co.* email is without @domain
			username = email.substring(0, atSign);
		else
			username = email;
		
		protocol = DataSource.Type.imap.toString();
		host = "imap-ssl.mail.yahoo.com";
		port = "993";
		isSsl = true;
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
