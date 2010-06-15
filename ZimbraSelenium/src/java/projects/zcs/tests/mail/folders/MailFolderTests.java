package projects.zcs.tests.mail.folders;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;

import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 * 
 *         Class contains 5 methods regarding 1.create folder 2.rename folder
 *         3.delete folder 4.valid special character 5.invalid special character
 * 
 *         Below parameter used to pass values from data provider
 * 
 * @param folderName
 *            - specify folder name in data provider according to test
 * @param renameFolderName
 *            - this argument used only when you are renaming folder
 * @param errDlgName
 *            - error dialog name specified in data provider according to test
 *            (for e.g. critical)
 * @param errMsg
 *            - error message specified in data provider according to test
 */
@SuppressWarnings( { "static-access" })
public class MailFolderTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "FolderDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createAndVerifyFolder")
				|| test.equals("renameAndVerifyFolder")
				|| test.equals("moveAndVerifyFolder")
				|| test.equals("deleteAndVerifyFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					localize(locator.criticalMsg),
					localize(locator.errorInvalidName) } };
		} else if (test.equals("validSpecialCharFolderTest")) {
			return new Object[][] { { "!@#$", getLocalizedData_NoSpecialChar(),
					localize(locator.criticalMsg),
					localize(locator.errorInvalidName) } };
		} else if (test.equals("invalidSpecialCharFolderTest")) {
			return new Object[][] { { ":#%", getLocalizedData_NoSpecialChar(),
					localize(locator.criticalMsg),
					localize(locator.errorInvalidName, ":#%", "") } };
		} else {// default
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					localize(locator.criticalMsg),
					localize(locator.errorInvalidName) } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zMailApp.zNavigateToMailApp();
		isExecutionARetry = false;
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * This test creates folder and verifies whether properly created or not
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAndVerifyFolder(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();
		page.zMailApp.zCreateFolder(folderName);
		obj.zFolder.zExists(folderName);
		needReset = false;
	}

	/**
	 * This test creates folder, renames and verifies it
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameAndVerifyFolder(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();
		page.zMailApp.zCreateFolder(folderName);
		obj.zFolder.zRtClick(folderName);
		obj.zMenuItem.zClick(localize(locator.renameFolder));
		obj.zDialog.zExists(localize(locator.renameFolder) + ": " + folderName);
		obj.zEditField.zTypeInDlgByName(localize(locator.newName),
				renameFolderName, localize(locator.renameFolder) + ": "
						+ folderName);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.renameFolder) + ": " + folderName);
		obj.zFolder.zExists(renameFolderName);
		needReset = false;
	}

	/**
	 * This test creates folder, deletes and verifies deletion
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAndVerifyFolder(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();
		page.zMailApp.zCreateFolder(folderName);
		page.zMailApp.zDeleteFolder(folderName);
		needReset = false;
	}

	/**
	 * This test creates folder and moves it
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveAndVerifyFolder(String folderName, String renameFolderName,
			String errDlgName, String errMsg) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();
		page.zMailApp.zCreateFolder(folderName);
		page.zMailApp.zDeleteFolder(folderName);
		obj.zFolder.zRtClick(folderName);
		/* Lot of wait because move test continiuosly retrying here */
		Thread.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.move));
		Thread.sleep(2000);
		if (config.getString("locale").equals("nl")) {
			obj.zFolder.zClickInDlgByName(localize(locator.folders), localize(
					locator.moveFolder, folderName, ""));
			Thread.sleep(1000);
			obj.zButton.zClickInDlgByName(localize(locator.ok), localize(
					locator.moveFolder, folderName, ""));
		} else if (config.getString("locale").equals("de")) {
			obj.zFolder.zClickInDlgByName(localize(locator.folders),
					localize(locator.folder));
			Thread.sleep(1000);
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.folder));
		} else {
			obj.zFolder.zClickInDlgByName(localize(locator.folders),
					localize(locator.move));
			Thread.sleep(1000);
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.move));
		}
		zWaitTillObjectExist("folder", folderName);
		obj.zFolder.zExists(folderName);

		needReset = false;
	}

	/**
	 * This test creates folder using special characters and verifies it
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void validSpecialCharFolderTest(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();
		page.zMailApp.zCreateFolder(folderName);
		obj.zFolder.zExists(folderName);
		needReset = false;
	}

	/**
	 * This negative test creates folder using invalid special characters
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void invalidSpecialCharFolderTest(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();
		page.zMailApp.zCreateFolder(folderName, renameFolderName, errDlgName,
				errMsg);
		String actualMsg = obj.zDialog.zGetMessage(errDlgName);
		actualMsg = actualMsg.replace("'", "");
		errMsg = errMsg.replace("'", "");
		errMsg = errMsg.replace((char) 160, (char) 32);
		Assert.assertEquals(actualMsg, errMsg, "Dialog with message (" + errMsg
				+ ") not found");
		obj.zButton.zClickInDlgByName(localize(locator.ok), errDlgName);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewFolder));
		obj.zFolder.zNotExists(folderName);
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