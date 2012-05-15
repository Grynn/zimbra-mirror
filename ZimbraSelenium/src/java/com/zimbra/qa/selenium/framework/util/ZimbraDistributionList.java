package com.zimbra.qa.selenium.framework.util;

import org.apache.log4j.*;

public class ZimbraDistributionList {
	private static Logger logger = LogManager.getLogger(ZimbraDistributionList.class);

	public String ZimbraId = null;
	public String DisplayName = null;
	public String EmailAddress = null;
	public String Password = null;

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
			String domain = EmailAddress.split("@")[1];


			// If the domain does not exist, create it
			ZimbraAdminAccount.GlobalAdmin().soapSend(
						"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
					+		"<name>"+ domain +"</name>"
					+	"</CreateDomainRequest>");




			// Create the account
			ZimbraAdminAccount.GlobalAdmin().soapSend(
						"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
					+		"<name>"+ this.EmailAddress +"</name>"
					+		"<a n='description'>description"+ ZimbraSeleniumProperties.getUniqueString() +"</a>"
					+	"</CreateDistributionListRequest>");

			ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:dl", "id");

			if ( ZimbraSeleniumProperties.getStringProperty("soap.trace.enabled", "false").toLowerCase().equals("true") ) {
				
				ZimbraAdminAccount.GlobalAdmin().soapSend(
							"<AddAccountLoggerRequest xmlns='urn:zimbraAdmin'>"
						+		"<account by='name'>"+ this.EmailAddress + "</account>"
						+		"<logger category='zimbra.soap' level='trace'/>"
						+	"</AddAccountLoggerRequest>");

			}
			

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
