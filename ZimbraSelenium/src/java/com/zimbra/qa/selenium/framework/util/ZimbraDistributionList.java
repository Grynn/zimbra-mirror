/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.util;

import org.apache.log4j.*;

public class ZimbraDistributionList {
	private static Logger logger = LogManager.getLogger(ZimbraDistributionList.class);

	public String ZimbraId = null;
	public String DisplayName = null;
	public String EmailAddress = null;
	public String Password = null;
	public ZimbraDomain Domain = null;

	public ZimbraDistributionList() {
		this(null, null);
	}
	
	/*
	 * Create an account with the email address <name>@<domain>
	 * The password is set to config property "adminPwd"
	 */
	public ZimbraDistributionList(String email, String password) {

		EmailAddress = email;
		if ( (email == null) || (email.trim().length() == 0) ) {
			EmailAddress = "dl" + ZimbraSeleniumProperties.getUniqueString() + "@" + ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");
		}

		Password = password;
		if ( (password == null) || (password.trim().length() == 0) ) {
			password = ZimbraSeleniumProperties.getStringProperty("adminPwd", "test123");
		}
	}

	/**
	 * Creates the account on the ZCS using CreateAccountRequest
	 */
	public ZimbraDistributionList provision() {
		
		try {

			// Make sure domain exists
			Domain = new ZimbraDomain( EmailAddress.split("@")[1]);
			Domain.provision();


			// Create the account
			ZimbraAdminAccount.GlobalAdmin().soapSend(
						"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
					+		"<name>"+ this.EmailAddress +"</name>"
					+		"<a n='description'>description"+ ZimbraSeleniumProperties.getUniqueString() +"</a>"
					+	"</CreateDistributionListRequest>");

			ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:dl", "id");

			// You can't add a logger to a DL
//			if ( ZimbraSeleniumProperties.getStringProperty("soap.trace.enabled", "false").toLowerCase().equals("true") ) {
//				
//				ZimbraAdminAccount.GlobalAdmin().soapSend(
//							"<AddAccountLoggerRequest xmlns='urn:zimbraAdmin'>"
//						+		"<account by='name'>"+ this.EmailAddress + "</account>"
//						+		"<logger category='zimbra.soap' level='trace'/>"
//						+	"</AddAccountLoggerRequest>");
//
//			}
			
			// Need to sync the GSA
			Domain.syncGalAccount();
			
			// Need to flush galgroup cache after creating a new DL (https://bugzilla.zimbra.com/show_bug.cgi?id=78970#c7)
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<FlushCacheRequest  xmlns='urn:zimbraAdmin'>" +
						"<cache type='galgroup'/>" +
	            	"</FlushCacheRequest>");

		} catch (HarnessException e) {

			logger.error("Unable to provision DL: "+ EmailAddress, e);
			ZimbraId = null;

		}


		return (this);
	}

	
	public ZimbraDistributionList addMember(ZimbraAccount account) throws HarnessException {
		return(addMember(account.EmailAddress));
	}
	
	public ZimbraDistributionList addMember(ZimbraDistributionList list) throws HarnessException {
		return (addMember(list.EmailAddress));
	}
	
	protected ZimbraDistributionList addMember(String email) throws HarnessException {
		
		ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<AddDistributionListMemberRequest xmlns='urn:zimbraAdmin'>"
				+		"<id>"+ this.ZimbraId +"</id>"
				+		"<dlm>"+ email +"</dlm>"
				+	"</AddDistributionListMemberRequest>");

		// Sync the GSA
		Domain.syncGalAccount();
		
		// Need to flush galgroup cache after creating a new DL (https://bugzilla.zimbra.com/show_bug.cgi?id=78970#c7)
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<FlushCacheRequest  xmlns='urn:zimbraAdmin'>" +
					"<cache type='galgroup'/>" +
            	"</FlushCacheRequest>");

		return (this);
	}
	
	public ZimbraDistributionList grantRight(ZimbraAccount grantee, String right) throws HarnessException {

		ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<GrantRightRequest xmlns='urn:zimbraAdmin'>"
				+		"<target by='name' type='dl'>"+ this.EmailAddress +"</target>"
				+		"<grantee by='name' type='usr'>"+ grantee.EmailAddress +"</grantee>"
				+		"<right>"+ right +"</right>"
				+	"</GrantRightRequest>");

		return (this);

	}
}
