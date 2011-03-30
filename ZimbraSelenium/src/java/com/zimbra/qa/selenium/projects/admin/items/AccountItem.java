package com.zimbra.qa.selenium.projects.admin.items;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;


public class AccountItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	protected String Id;
	
	protected String LocalName; // Email Address is LocalName@DomainName
	protected String DomainName;
	
	protected String Password;	// The password is encrypted in the attrs, so need to keep it separate

	protected Map<String, String> AccountAttrs;
	
	
	public AccountItem() {
		super();
		
		AccountAttrs = new HashMap<String, String>();
		
		LocalName = "email" + ZimbraSeleniumProperties.getUniqueString();
		DomainName = ZimbraSeleniumProperties.getStringProperty("testdomain");
		Id = null;
		
		// Surname is required in Admin Console
		AccountAttrs.put("sn", "Lastname"+ ZimbraSeleniumProperties.getUniqueString());

	}
	
	public AccountItem(String emailAddress, String lastName) {
		
		AccountAttrs = new HashMap<String, String>();
		
		if ( emailAddress.contains("@") ) {
			LocalName = emailAddress.split("@")[0];
			DomainName = emailAddress.split("@")[1];
		} else {
			LocalName = emailAddress;
			DomainName = ZimbraSeleniumProperties.getStringProperty("testdomain");
		}

		Id = null;
		
		// Surname is required in Admin Console
		AccountAttrs.put("sn", lastName);
		
	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(AccountItem.class.getSimpleName()).append('\n');
		sb.append("Email: ").append(getEmailAddress());
		sb.append("ID: ").append(getID());
		
		for (Map.Entry<String, String> entry : AccountAttrs.entrySet()) {
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
		return (LocalName + "@" + DomainName);
	}
	
	public void setLocalName(String name) {
		LocalName = name;
	}
	
	public String getLocalName() {
		return (LocalName);
	}

	public void setDomainName(String domain) {
		DomainName = domain;
	}
	
	public String getDomainName() {
		return (DomainName);
	}
	
	public void setPassword(String password) {
		Password = password;
	}
	
	public Map<String, String> getAccountAttrs() {
		return (AccountAttrs);
	}

}
