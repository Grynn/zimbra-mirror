package com.zimbra.qa.selenium.projects.zcs.tests.briefcase.folders;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.RetryFailedTests;
import com.zimbra.qa.selenium.projects.zcs.tests.CommonTest;


/**
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class DeleteBriefcaseFolder extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "briefcaseDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("deleteBriefcaseFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="briefcase";
		super.zLogin();
	}

	@Test(dataProvider = "briefcaseDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteBriefcaseFolder(String briefcaseName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zCreateNewBriefcaseFolder(briefcaseName);
		zMoveFolderToTrash(briefcaseName);
		zPermanentlyDeleteFolder(briefcaseName);
		obj.zFolder.zNotExists(briefcaseName);

		SelNGBase.needReset.set(false);
	}
}