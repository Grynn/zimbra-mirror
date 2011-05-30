package com.zimbra.qa.selenium.projects.admin.items;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class ResourceItem implements IItem {
	protected String resourceLocalName;
	protected String resourceDomainName;
	protected String resourceId;

	public ResourceItem() {
		super();

		resourceLocalName = "resource" + ZimbraSeleniumProperties.getUniqueString();
		resourceDomainName = ZimbraSeleniumProperties.getStringProperty("testdomain");
		resourceId = null;	
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
		return (getLocalName());
	}

	public String getID() {
		return (resourceId);
	}

	public String getEmailAddress() {
		return (resourceLocalName + "@" + resourceDomainName);
	}

	public void setLocalName(String name) {
		resourceLocalName = name;
	}

	public String getLocalName() {
		return (resourceLocalName);
	}

	public void setDomainName(String domain) {
		resourceDomainName = domain;
	}

	public String getDomainName() {
		return (resourceDomainName);
	}
}
