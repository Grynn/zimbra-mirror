package projects.zcs.tests.briefcase.files;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class MoveBriefcaseFile extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "BriefcaseFileUpload")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("moveBriefcaseFiletoNewFolder"))
			return new Object[][] { { "samlejpg.jpg",
					getLocalizedData_NoSpecialChar() } };
		else
			return new Object[][] { { "" } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="briefcase";
		super.zLogin();
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * This test uploads files in system briefcase folder, create new briefcase
	 * folder, moves file there and verifies file deletion
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveBriefcaseFiletoNewFolder(String filename, String newBFFolder)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zClick(filename);
		obj.zButton.zClick(page.zBriefcaseApp.zMoveItemIconBtn);
		SleepUtil.sleep(1000);
		obj.zDialog.zExists(localize(locator.chooseFolder));
		obj.zButton.zClickInDlgByName(localize(locator._new),
				localize(locator.chooseFolder));
		SleepUtil.sleep(1000);
		obj.zEditField.zTypeInDlgByName(localize(locator.name), newBFFolder,
				localize(locator.createNewBriefcaseItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewBriefcaseItem));
		SleepUtil.sleep(1000);
		obj.zFolder.zClickInDlgByName(newBFFolder,
				localize(locator.chooseFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.chooseFolder));
		obj.zBriefcaseItem.zNotExists(filename);
		obj.zFolder.zClick(newBFFolder);
		obj.zBriefcaseItem.zExists(filename);

		SelNGBase.needReset.set(false);
	}
}