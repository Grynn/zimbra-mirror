package com.zimbra.qa.selenium.projects.admin.tests.downloads;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class DownloadsIndex extends AdminCommonTest {

	public DownloadsIndex() {
		logger.info("New "+ DownloadsIndex.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageDownloads;
	}



	@Test(	description = "Verify the Downloads Index opens",
			groups = { "smoke" })
	public void DownloadsTab_01() throws HarnessException {


	}

	@Test(	description = "Verify the Downloads Tab contains the correct FOSS vs NETWORK links",
			groups = { "smoke" })
	public void DownloadsTab_02() throws HarnessException {


	}

	@Test(	description = "Verify the downloads links return 200 rather than 404",
			groups = { "smoke" })
	public void DownloadsTab_03() throws HarnessException {


	}



}
