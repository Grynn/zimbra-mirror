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

}