package projects.zcs.tests.briefcase.files;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class DeleteBriefcaseFile extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "BriefcaseFileUpload")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("deleteBriefcaseFile"))
			return new Object[][] { { "testwordfile.doc",
					getLocalizedData_NoSpecialChar() } };
		else
			return new Object[][] { { "" } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		page.zBriefcaseApp.zGoToBriefcaseApp();
		SleepUtil.sleep(2000);
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/**
	 * Test uploads files in the briefcase folder and verifies file deletion
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteBriefcaseFile(String filename, String newBFFolder)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zClick(filename);
		obj.zButton.zClick(page.zBriefcaseApp.zDeleteIconBtn);
		obj.zDialog.zExists(localize(locator.confirmTitle));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.confirmTitle));
		obj.zMenuItem.zNotExists(filename);

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}