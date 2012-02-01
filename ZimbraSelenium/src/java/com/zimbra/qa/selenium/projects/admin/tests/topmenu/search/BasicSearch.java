package com.zimbra.qa.selenium.projects.admin.tests.topmenu.search;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class BasicSearch extends AdminCommonTest {
	
	public BasicSearch() {
		logger.info("New "+ BasicSearch.class.getCanonicalName());
	}
	
	@Test(	description = "Verify the Top Menu displays the Search bar correctly",
			groups = { "skip" })
	public void TopMenu_BasicSearch_01() throws HarnessException {
		throw new HarnessException("Implement me!");
	}


}
