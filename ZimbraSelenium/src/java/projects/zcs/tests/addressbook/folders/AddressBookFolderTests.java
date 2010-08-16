package projects.zcs.tests.addressbook.folders;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.util.RetryFailedTests;

/**
 * @written by Prashant Jaiswal & updated by Jitesh
 * 
 */
@SuppressWarnings("static-access")
public class AddressBookFolderTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createAndRenameABFolder")) {
			return new Object[][] { {
					"newAB" + getLocalizedData_NoSpecialChar(),
					"renamedAB" + getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("createAndQDeleteABFolder")) {
			return new Object[][] { { "newAB"
					+ getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("moveABFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("tryToCreateDuplicateABFolder")) {
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
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to create a notebook folder and then rename the notebook folder and
	 * verify
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAndRenameABFolder(String newAddBookName,
			String renamedABName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateNewAddBook(newAddBookName);
		zWaitTillObjectExist("folder", newAddBookName);
		obj.zFolder.zRtClick(newAddBookName);
		obj.zMenuItem.zClick(localize(locator.renameFolder));
		obj.zEditField.zTypeInDlg(localize(locator.newName), renamedABName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		obj.zFolder.zExists(renamedABName);

		needReset = false;
	}

	/**
	 * To create AB folder and then delete the same.Verify the creation and
	 * deletion of the AB folder
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAndQDeleteABFolder(String newAddBookName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateNewAddBook(newAddBookName);
		zWaitTillObjectExist("folder", newAddBookName);
		page.zMailApp.zDeleteFolder(newAddBookName);
		obj.zFolder.zClick(localize(locator.trash));
		obj.zFolder.zExists(newAddBookName);

		needReset = false;
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveABFolder(String newAddBookName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateNewAddBook(newAddBookName);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Contacts') and contains(text(), '"
						+ newAddBookName + "')]",
				page.zABCompose.zEmailedContactsFolder);
		Assert
				.assertTrue(selenium
						.isElementPresent("//div[@id='zti__main_Contacts__13']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
								+ newAddBookName + "')]"));

		needReset = false;
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateABFolder(String newAddBookName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateNewAddBook(newAddBookName);
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(replaceUserNameInStaticId(page.zABCompose.zNewABOverviewPaneIcon)));
		obj.zMenuItem.zClick(localize(locator.newAddrBook));
		Thread.sleep(1000);
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				newAddBookName, localize(locator.createNewAddrBook));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewAddrBook));
		assertReport(localize(locator.errorAlreadyExists, newAddBookName, ""),
				obj.zDialog.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewAddrBook));

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

}