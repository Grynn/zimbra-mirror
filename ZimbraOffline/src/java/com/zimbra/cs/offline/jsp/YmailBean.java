package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class YmailBean extends XmailBean {

	public YmailBean() {}
	
	private void fixup() {
		if (verb == null)
			return;
		
		if (isEmpty(dsName))
			addInvalid("dataSourceName");
		if (isEmpty(email))
			addInvalid("email");
		if (isEmpty(password))
	    	addInvalid("password");
		
		domain = "yahoo.com";  //this is solely for datasource.properties mapping
		if (email.indexOf('@') < 0)
			email += '@' + domain;
		
		if (email.endsWith("@" + domain)) //username of yahoo.com email is without @domain
			username = email.substring(0, email.length() - 1 -  domain.length());
		else
			username = email;
		
		protocol = DataSource.Type.imap.toString();
		host = "imap.mail.yahoo.com";
		port = "143";
		isSsl = false;
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
