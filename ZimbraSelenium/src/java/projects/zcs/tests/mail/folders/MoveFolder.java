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
public class MoveFolder extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "FolderDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("moveAndVerifyFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					localize(locator.criticalMsg),
					localize(locator.errorInvalidName) } };
		} else {
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
	 * This test creates folder and moves it
	 */
	@Test(dataProvider = "FolderDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveAndVerifyFolder(String folderName, String renameFolderName,
			String errDlgName, String errMsg) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zMailApp.zCreateFolder(folderName);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Mail') and contains(text(), '"
						+ folderName + "')]", page.zMailApp.zTrashFldr);
		Assert
				.assertTrue(SelNGBase.selenium
						.get()
						.isElementPresent(
								"//div[@id='zti__main_Mail__3']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
										+ folderName + "')]"));

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