package com.zimbra.qa.selenium.projects.admin.items;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;


public class AccountItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	protected static String Id=null;
	
	protected String localName; // Email Address is LocalName@DomainName
	protected String domainName;
	
	protected String Password;	// The password is encrypted in the attrs, so need to keep it separate

	protected Map<String, String> accountAttrs;
	
	
/*	public AccountItem() {
		super();
		
		accountAttrs = new HashMap<String, String>();
		
		localName = "email" + ZimbraSeleniumProperties.getUniqueString();
		domainName = ZimbraSeleniumProperties.getStringProperty("testdomain");
		Id = null;
		
		// Surname is required in Admin Console
		accountAttrs.put("sn", "Lastname"+ ZimbraSeleniumProperties.getUniqueString());

	}
*/	
	public AccountItem(String emailAddress, String lastName) {
		
		accountAttrs = new HashMap<String, String>();
		
		if ( emailAddress.contains("@") ) {
			localName = emailAddress.split("@")[0];
			domainName = emailAddress.split("@")[1];
		} else {
			localName = emailAddress;
			domainName = ZimbraSeleniumProperties.getStringProperty("testdomain");
		}

		//Id = null;
		
		// Surname is required in Admin Console
		accountAttrs.put("sn", lastName);
		
	}


	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(AccountItem.class.getSimpleName()).append('\n');
		sb.append("Email: ").append(getEmailAddress());
		sb.append("ID: ").append(getID());
		
		for (Map.Entry<String, String> entry : accountAttrs.entrySet()) {
			sb.append("Attr: ").append(entry.getKey()).append("=").append(entry.getValue());
		}
		
		return (sb.toString());
	}

	@Override
	public String getName() {
		return (getEmailAddress());
	}
	
	public String getID() {
		return (Id);
	}
	
	public String getEmailAddress() {
		return (localName + "@" + domainName);
	}
	
	public void setLocalName(String name) {
		localName = name;
	}
	
	public String getLocalName() {
		return (localName);
	}

	public void setDomainName(String domain) {
		domainName = domain;
	}
	
	public String getDomainName() {
		return (domainName);
	}
	
	public void setPassword(String password) {
		Password = password;
	}
	
	public Map<String, String> getAccountAttrs() {
		return (accountAttrs);
	}

	// ImgAdminUser ImgAccount ImgSystemResource (others?)
	protected String GAccountType = null;
	public void setGAccountType(String imagetype) {
		GAccountType = imagetype;
	}
	public String getGAccountType() {
		return (GAccountType);
	}

	protected String GEmailAddress = null;
	public void setGEmailAddress(String email) {
		GEmailAddress = email;
	}
	public String getGEmailAddress() {
		return (GEmailAddress);
	}

	public static AccountItem createUsingSOAP(AccountItem account) throws HarnessException {
		
		StringBuilder elementPassword = new StringBuilder();
		if ( account.Password != null ) {
			elementPassword.append("<password>").append(account.Password).append("</password>");
		}
		
		StringBuilder elementAttrs = new StringBuilder();
		for ( Map.Entry<String,String> entry : account.accountAttrs.entrySet() ) {
			elementAttrs.append("<a n='").append(entry.getKey()).append("'>").append(entry.getValue()).append("</a>");
		}
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
							"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
				+				"<name>"+ account.getEmailAddress() +"</name>"
				+				elementPassword.toString()
				+				elementAttrs.toString()
				+			"</CreateAccountRequest>");
		
		
		Id = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:CreateAccountResponse/admin:account", "id").toString();
		// TODO: Need to create a new AccountItem and set the account values to it, then return the new item
		
		return (account);
	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("not applicable for this IItem type");
	}

}
