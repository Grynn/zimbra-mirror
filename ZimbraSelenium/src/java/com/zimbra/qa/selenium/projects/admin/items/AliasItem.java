package com.zimbra.qa.selenium.projects.admin.items;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.*;

public class AliasItem implements IItem {

	protected String AliasLocalName;
	protected String AliasDomainName;
	protected String AliasId;
	protected String AliasTargetEmail;
	protected String AliasTargetId;
	
	public AliasItem() {
		super();
		
		AliasLocalName = "alias" + ZimbraSeleniumProperties.getUniqueString();
		AliasDomainName = ZimbraSeleniumProperties.getStringProperty("testdomain");
		AliasId = null;	
	}
	
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub

	}

	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getName() {
		return (getEmailAddress());
	}
	
	public String getID() {
		return (AliasId);
	}
	
	public String getEmailAddress() {
		return (AliasLocalName + "@" + AliasDomainName);
	}
	
	public void setLocalName(String name) {
		AliasLocalName = name;
	}
	
	public String getLocalName() {
		return (AliasLocalName);
	}

	public void setDomainName(String domain) {
		AliasDomainName = domain;
	}
	
	public String getDomainName() {
		return (AliasDomainName);
	}
	
	public void setTarget(AccountItem account) throws HarnessException {
		if ( (account.getEmailAddress() == null) || (account.getEmailAddress().trim().length() == 0) )
			throw new HarnessException("AccountItem email address is not set");
		if ( (account.Id == null) || (account.Id.trim().length() == 0) )
			throw new HarnessException("AccountItem ID is not set");

		AliasTargetEmail = account.getEmailAddress();		
		AliasTargetId = account.Id;

	}

	public void setTargetAccountEmail(String emailAddress) throws HarnessException {
		if ( (AliasTargetEmail != null) && (AliasTargetEmail.equals(emailAddress)) )
			return; // Nothing to update
		
		if ( (emailAddress == null) || (emailAddress.trim().length() == 0) )
			throw new HarnessException("emailAddress cannot be null or blank");
		
		// Need to get the AccountID
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
							"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+                "<account by='name'>"+ emailAddress +"</account>"
                +            "</GetAccountRequest>");
		String id = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:account", "id");
		
		AliasTargetEmail = emailAddress;		
		AliasTargetId = id;
	}
	
	public String getTargetAccountEmail() {
		return (AliasTargetEmail);
	}

	public void setTargetAccountId(String id) throws HarnessException {
		if ( (AliasTargetId != null) && (AliasTargetId.equals(id)) )
			return; // Nothing to update
		
		if ( (id == null) || (id.trim().length() == 0) )
			throw new HarnessException("id cannot be null or blank");
		
		// Need to get the AccountID
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
							"<GetAccountRequest xmlns='urn:zimbraAdmin'>"
				+                "<account by='id'>"+ id +"</account>"
                +            "</GetAccountRequest>");
		String emailAddress = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:account", "name");

		AliasTargetEmail = emailAddress;		
		AliasTargetId = id;

	}
	
	public String getTargetAccountId() {
		return (AliasTargetId);
	}


}
