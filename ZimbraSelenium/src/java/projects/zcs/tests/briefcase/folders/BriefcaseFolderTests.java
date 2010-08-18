package projects.zcs.tests.briefcase.folders;

import java.lang.reflect.Method;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class BriefcaseFolderTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "briefcaseDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createDeleteBriefcaseFolder")
				|| test.equals("renameBriefcaseFolder")
				|| test.equals("moveBriefcaseFolder")
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
	private void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Briefcase");
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	@Test(dataProvider = "briefcaseDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createDeleteBriefcaseFolder(String briefcaseName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zCreateNewBriefcaseFolder(briefcaseName);
		zMoveFolderToTrash(briefcaseName);
		zPermanentlyDeleteFolder(briefcaseName);
		obj.zFolder.zNotExists(briefcaseName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "briefcaseDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameBriefcaseFolder(String briefcaseName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String newBriefcase = getLocalizedData_NoSpecialChar();
		page.zBriefcaseApp.zCreateNewBriefcaseFolder(briefcaseName);
		Thread.sleep(1000);
		obj.zFolder.zRtClick(briefcaseName);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), newBriefcase);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zFolder.zExists(newBriefcase);
		obj.zFolder.zNotExists(briefcaseName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "briefcaseDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveBriefcaseFolder(String briefcaseName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zCreateNewBriefcaseFolder(briefcaseName);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Briefcase') and contains(text(), '"
						+ briefcaseName + "')]",
				page.zBriefcaseApp.zTrashFolder);
		Assert
				.assertTrue(SelNGBase.selenium.get()
						.isElementPresent("//div[@id='zti__main_Briefcase__3']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
								+ briefcaseName + "')]"));

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
		Thread.sleep(1000);
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

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}