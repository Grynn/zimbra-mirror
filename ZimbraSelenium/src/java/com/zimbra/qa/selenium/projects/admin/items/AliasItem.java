package com.zimbra.qa.selenium.projects.admin.items;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class AliasItem implements IItem {

	protected String AliasLocalName;
	protected String AliasDomainName;
	protected String AliasId;
	
	public AliasItem() {
		super();
		
		AliasLocalName = "email" + ZimbraSeleniumProperties.getUniqueString();
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


}
