package com.zimbra.qa.selenium.projects.admin.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class DistributionListItem implements IItem {

	protected static Logger logger = LogManager.getLogger(IItem.class);

	protected static String Id=null;
	
	protected String distributionListLocalName; // Email Address is LocalName@DomainName
	protected String distributionListDomainName;

	public DistributionListItem() {
		super();
		
		distributionListLocalName = "dl" + ZimbraSeleniumProperties.getUniqueString();
		distributionListDomainName = ZimbraSeleniumProperties.getStringProperty("testdomain");
		//Id = null;
		
	}
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return (getEmailAddress());
	}
	
	public String getID() {
		return (Id);
	}
	
	public String getEmailAddress() {
		return (distributionListLocalName + "@" + distributionListDomainName);
	}
	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setLocalName(String name) {
		distributionListLocalName = name;
	}
	
	public String getLocalName() {
		return (distributionListLocalName);
	}

	public void setDomainName(String domain) {
		distributionListDomainName = domain;
	}
	
	public String getDomainName() {
		return (distributionListDomainName);
	}
}
