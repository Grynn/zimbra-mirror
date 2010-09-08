package projects.zcs.tests.briefcase.folders;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

/**
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class CreateBriefcaseFolder extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "briefcaseDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createBriefcaseFolder")
				|| test.equals("tryToCreateDuplicateBriefcaseFolder")) {
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

	@Test(
			dataProvider = "briefcaseDataProvider", 
			groups = { "sanity", "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void createBriefcaseFolder(String briefcaseName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zCreateNewBriefcaseFolder(briefcaseName);
		zMoveFolderToTrash(briefcaseName);
		zPermanentlyDeleteFolder(briefcaseName);
		obj.zFolder.zNotExists(briefcaseName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "briefcaseDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateBriefcaseFolder(String briefcaseName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zCreateNewBriefcaseFolder(briefcaseName);
		obj.zButton.zRtClick(page.zBriefcaseApp.zNewBriefcaseOverviewPaneIcon);
		obj.zMenuItem.zClick(localize(locator.newBriefcase));
		SleepUtil.sleep(1000);
		obj.zFolder.zClickInDlgByName(localize(locator.folders),
				localize(locator.createNewBriefcaseItem));
		obj.zEditField.zTypeInDlgByName(localize(locator.name), briefcaseName,
				localize(locator.createNewBriefcaseItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewBriefcaseItem));
		assertReport(localize(locator.errorAlreadyExists, briefcaseName, ""),
				obj.zDialog.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewBriefcaseItem));

		SelNGBase.needReset.set(false);
	}
}