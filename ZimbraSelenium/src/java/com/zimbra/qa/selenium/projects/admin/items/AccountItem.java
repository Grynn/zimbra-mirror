package com.zimbra.qa.selenium.projects.admin.items;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;


public class AccountItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	public String EmailAddress;
	public String Id;
	
	public Map<String, String> AccountAttrs;
	
	public String Password;	// The password is encrypted in the attrs, so need to keep it separate
	
	public AccountItem() {
		super();
		
		AccountAttrs = new HashMap<String, String>();
		
		EmailAddress = 
			"email" + ZimbraSeleniumProperties.getUniqueString() + 
			"@" + ZimbraSeleniumProperties.getStringProperty("testdomain");
		Id = null;
		
		// Surname is required in Admin Console
		AccountAttrs.put("sn", "Lastname"+ ZimbraSeleniumProperties.getUniqueString());

	}
	
	public AccountItem(String emailAddress, String lastName) {
		
		AccountAttrs = new HashMap<String, String>();

		this.EmailAddress = emailAddress;
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
		logger.error("implement me!", new Throwable("implement me!"));
		return (sb.toString());
	}

	@Override
	public String getName() {
		return (EmailAddress);
	}
	
	
}
