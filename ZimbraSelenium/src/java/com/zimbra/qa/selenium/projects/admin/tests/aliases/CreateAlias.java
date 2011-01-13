package com.zimbra.qa.selenium.projects.admin.tests.aliases;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class CreateAlias extends AdminCommonTest {
	
	public CreateAlias() {
		logger.info("New "+ CreateAlias.class.getCanonicalName());
	}
	
	@Test(	description = "Create a basic alias",
			groups = { "sanity" })
	public void CreateAlias_01() throws HarnessException {
		throw new HarnessException("implement me!");
	}


}
