package com.zimbra.qa.selenium.projects.admin.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class CosItem implements IItem {

	protected static Logger logger = LogManager.getLogger(IItem.class);

	protected static String Id=null;

	protected static String cosName;

	public CosItem() {
		super();

		cosName = "cos" + ZimbraSeleniumProperties.getUniqueString();
		//Id = null;

	}
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return cosName;
	}

	public String getID() {
		return (Id);
	}

	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}
}
