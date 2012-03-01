package com.zimbra.qa.selenium.projects.admin.items;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class DomainItem implements IItem {

	protected String domainName;
	protected String Id;
	
	public DomainItem() {
		super();

		domainName = "domain" + ZimbraSeleniumProperties.getUniqueString() + ".com";
		Id = null;

	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return domainName;
	}

	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}

}
