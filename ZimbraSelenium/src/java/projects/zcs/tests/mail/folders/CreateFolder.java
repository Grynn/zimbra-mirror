package projects.zcs.tests.mail.folders;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
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
public class CreateFolder extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "FolderDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createAndVerifyFolder")) {
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
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zMailApp.zNavigateToMailApp();
		SelNGBase.isExecutionARetry.set(false);
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * This test creates folder and verifies whether properly created or not
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "test" }, retryAnalyzer = RetryFailedTests.class)
	public void createAndVerifyFolder(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zMailApp.zCreateFolder(folderName);
		obj.zFolder.zExists(folderName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates folder using special characters and verifies it
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void validSpecialCharFolderTest(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zMailApp.zCreateFolder(folderName);
		obj.zFolder.zExists(folderName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This negative test creates folder using invalid special characters
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void invalidSpecialCharFolderTest(String folderName,
			String renameFolderName, String errDlgName, String errMsg)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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