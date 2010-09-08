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
import projects.zcs.ui.ComposeView;

/**
 * @author Jitesh Sojitra
 * 
 *         Class contains 3 methods regarding 1.new briefcase file upload
 *         2.delete uploaded file 3.move briefcase file to new folder
 * 
 *         Below parameter used to pass values from data provider
 * 
 * @param filename
 *            - file name to be uploaded
 * @param newBFFolder
 *            - specify this parameter if you want to upload file in new
 *            briefcase folder
 */
@SuppressWarnings("static-access")
public class CreateBriefcaseFile extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "BriefcaseFileUpload")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("newBriefcaseFileUpload"))
			return new Object[][] { { "testexcelfile.xls",
					getLocalizedData_NoSpecialChar() } };
		else if (test.equals("sendBriefcaseFileAsAttachment"))
			return new Object[][] { { "testexcelfile.xls",
					getLocalizedData_NoSpecialChar() } };
		else
			return new Object[][] { { "samlejpg.jpg",
					getLocalizedData_NoSpecialChar() } };
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
	 * This test uploads files in briefcase folder and verifies file exist
	 */
	@Test(
			dataProvider = "BriefcaseFileUpload", 
			groups = { "sanity", "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void newBriefcaseFileUpload(String filename, String newBFFolder)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zExists(filename);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:-Briefcase :File Upload and click on' Send->Send as
	 * Attachment(s) or right click on file-> select Send As Attachment to
	 * verify it Should jump to compose view with directly attaching
	 * corresponding documents
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendBriefcaseFileAsAttachment(String filename,
			String newBFFolder) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zExists(filename);
		obj.zBriefcaseItem.zClick(filename);
		ClientSessionFactory.session().selenium()
				.clickAt("id=zb__BCC__SEND_FILE_MENU_title", "");
		SleepUtil.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.sendAsAttachment));
		SleepUtil.sleep(1000);
		obj.zTextAreaField.zWait(localize(locator.toLabel));
		obj.zCheckbox.zVerifyIsChecked(filename);
		obj.zButton.zClick(ComposeView.zCancelIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		obj.zBriefcaseItem.zClick(filename);
		obj.zBriefcaseItem.zRtClick(filename);
		SleepUtil.sleep(500);
		obj.zMenuItem.zClick(localize(locator.sendAsAttachment));
		SleepUtil.sleep(1000);
		obj.zTextAreaField.zWait(localize(locator.toLabel));
		obj.zCheckbox.zVerifyIsChecked(filename);
		obj.zButton.zClick(ComposeView.zCancelIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}
}