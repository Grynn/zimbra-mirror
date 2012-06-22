package com.zimbra.qa.selenium.projects.admin.tests.globalacl;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageGlobalACL;;

public class NavigateGlobalACL extends AdminCommonTest {
	public NavigateGlobalACL() {
		logger.info("New "+ NavigateGlobalACL.class.getCanonicalName());

		// All tests start at the "Cos" page
		super.startingPage = app.zPageManageACL;
	}
	
	/**
	 * Testcase : Navigate to Global ACL page
	 * Steps :
	 * 1. Go to Accounts
	 * 2. Verify navigation path -- "Home --> Configure --> Global ACL"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Global ACL",
			groups = { "sanity" })
			public void NavigateGlobalACL_01() throws HarnessException {
		
		/*
		 * Verify navigation path -- "Home --> Configure --> Global ACL"
		 */
		ZAssert.assertTrue(app.zPageManageACL.zVerifyHeader(PageManageGlobalACL.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageACL.zVerifyHeader(PageManageGlobalACL.Locators.CONFIGURE), "Verfiy the \"Configure\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageACL.zVerifyHeader(PageManageGlobalACL.Locators.GLOBAL_ACL), "Verfiy the \"Global ACL\" text exists in navigation path");
		
	}

}
