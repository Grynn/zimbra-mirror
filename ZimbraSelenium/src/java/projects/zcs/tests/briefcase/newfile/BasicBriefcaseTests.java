package projects.zcs.tests.briefcase.newfile;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

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
public class BasicBriefcaseTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "BriefcaseFileUpload")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("newBriefcaseFileUpload"))
			return new Object[][] { { "testexcelfile.xls",
					getLocalizedData_NoSpecialChar() } };
		else if (test.equals("deleteBriefcaseFile"))
			return new Object[][] { { "testwordfile.doc",
					getLocalizedData_NoSpecialChar() } };
		else if (test.equals("moveBriefcaseFiletoNewFolder"))
			return new Object[][] { { "samlejpg.jpg",
					getLocalizedData_NoSpecialChar() } };
		else if (test.equals("sendBriefcaseFileAsAttachment"))
			return new Object[][] { { "testexcelfile.xls",
					getLocalizedData_NoSpecialChar() } };
		else if (test.equals("verifyDownloadMenuDisabledForNewDoc"))
			return new Object[][] { { "CreateNewDoc",
					getLocalizedData_NoSpecialChar() } };
		else
			return new Object[][] { { "samlejpg.jpg",
					getLocalizedData_NoSpecialChar() } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		page.zBriefcaseApp.zGoToBriefcaseApp();
		Thread.sleep(2000);
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * This test uploads files in briefcase folder and verifies file exist
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void newBriefcaseFileUpload(String filename, String newBFFolder)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zExists(filename);

		needReset = false;
	}

	/**
	 * This test uploads files in briefcase folder and verifies file deletion
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteBriefcaseFile(String filename, String newBFFolder)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zClick(filename);
		obj.zButton.zClick(page.zBriefcaseApp.zDeleteIconBtn);
		obj.zDialog.zExists(localize(locator.confirmTitle));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.confirmTitle));
		obj.zMenuItem.zNotExists(filename);

		needReset = false;
	}

	/**
	 * This test uploads files in system briefcase folder, create new briefcase
	 * folder, moves file there and verifies file deletion
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveBriefcaseFiletoNewFolder(String filename, String newBFFolder)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zClick(filename);
		obj.zButton.zClick(page.zBriefcaseApp.zMoveItemIconBtn);
		Thread.sleep(1000);
		obj.zDialog.zExists(localize(locator.chooseFolder));
		obj.zButton.zClickInDlgByName(localize(locator._new),
				localize(locator.chooseFolder));
		Thread.sleep(1000);
		obj.zEditField.zTypeInDlgByName(localize(locator.name), newBFFolder,
				localize(locator.createNewBriefcaseItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewBriefcaseItem));
		Thread.sleep(1000);
		obj.zFolder.zClickInDlgByName(newBFFolder,
				localize(locator.chooseFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.chooseFolder));
		obj.zBriefcaseItem.zNotExists(filename);
		obj.zFolder.zClick(newBFFolder);
		obj.zBriefcaseItem.zExists(filename);

		needReset = false;
	}

	/**
	 * Test Case:-Briefcase :File Upload and click on' Send->Send as
	 * Attachment(s) or right click on file-> select Send As Attachment to
	 * verify it Should jump to compose view with directly attaching
	 * corresponding documents
	 * 
	 * @param filename
	 * @param newBFFolder
	 * @author Girish
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendBriefcaseFileAsAttachment(String filename,
			String newBFFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zBriefcaseApp.zBriefcaseFileUpload(filename, "");
		obj.zBriefcaseItem.zExists(filename);
		obj.zBriefcaseItem.zClick(filename);
		selenium.clickAt("id=zb__BCC__SEND_FILE_MENU_title", "");
		Thread.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.sendAsAttachment));
		Thread.sleep(1000);
		obj.zTextAreaField.zWait(localize(locator.toLabel));
		obj.zCheckbox.zVerifyIsChecked(filename);
		obj.zButton.zClick(ComposeView.zCancelIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		obj.zBriefcaseItem.zClick(filename);
		obj.zBriefcaseItem.zRtClick(filename);
		Thread.sleep(500);
		obj.zMenuItem.zClick(localize(locator.sendAsAttachment));
		Thread.sleep(1000);
		obj.zTextAreaField.zWait(localize(locator.toLabel));
		obj.zCheckbox.zVerifyIsChecked(filename);
		obj.zButton.zClick(ComposeView.zCancelIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));

		needReset = false;
	}

	/**
	 * 1. Login to web client 2. Go to Briefcase > create any document Or
	 * presentation Or spreadsheet 3. Right click to Saved doc. 4. Verify
	 * 'Download' menu remains disabled 5. Right side column 'Download' link as
	 * disabled.
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyDownloadMenuDisabledForNewDoc(String filename,
			String newBFFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			obj.zButton.zClick(localize(locator.newDocument));
			Thread.sleep(1500);
			selenium.selectWindow(selenium.getAllWindowTitles()[1]);
			selenium.windowFocus();
			zWaitTillObjectExist("button", localize(locator.save));
			selenium.type("xpath=//input[@type='text']", filename);
			obj.zButton.zClick(localize(locator.save));
			Thread.sleep(1000);
			selenium.close();
			selenium.selectWindow(null);
			obj.zFolder.zClick(page.zBriefcaseApp.zBriefcaseFolder);
			Thread.sleep(1000);
			obj.zBriefcaseItem.zClick(filename);
			obj.zBriefcaseItem.zRtClick(filename);
			Thread.sleep(500);
			String download = selenium
					.getEval("selenium.browserbot.getCurrentWindow().document.getElementById('zmi__Briefcase__SAVE_FILE').className");
			Assert.assertTrue(download.contains("ZDisabled"),
					"Download is in enable state");

			Boolean downloadLink = selenium.isElementPresent("Link="
					+ localize(locator.saveFile));
			assertReport("false", downloadLink.toString(),
					"Verifying Download link exist");
		}

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}