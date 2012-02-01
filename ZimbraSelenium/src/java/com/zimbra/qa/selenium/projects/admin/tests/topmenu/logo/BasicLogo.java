package com.zimbra.qa.selenium.projects.admin.tests.topmenu.logo;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class BasicLogo extends AdminCommonTest {
	
	public BasicLogo() {
		logger.info("New "+ BasicLogo.class.getCanonicalName());
	}
	
	@Test(	description = "Verify the Top Menu displays the Logo image correctly",
			groups = { "skip" })
	public void TopMenu_BasicLogo_01() throws HarnessException {
		throw new HarnessException("Implement me!");
	}


}
