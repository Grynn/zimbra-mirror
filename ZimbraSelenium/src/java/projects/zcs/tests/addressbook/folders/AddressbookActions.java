package projects.zcs.tests.addressbook.folders;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import projects.zcs.ui.ABCompose.ABComposeActionMethod;
import framework.core.SelNGBase;
import framework.items.FolderItem;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

/**
 * @written by Prashant Jaiswal & updated by Jitesh
 * 
 */
@SuppressWarnings("static-access")
public class AddressBookFolderTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Test to create a notebook folder and then rename the notebook folder and
	 * verify
	 */
	@Test(
			description = "Test to create a notebook folder and then rename the notebook folder and verify",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void createAndRenameABFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		FolderItem addressbook = new FolderItem();
		addressbook.name = "original" + ZimbraSeleniumProperties.getUniqueString();
		String renamed = "renamed" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create the addressbook
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		zWaitTillObjectExist("folder", addressbook.name);
		
		// Right click and rename
		page.zABCompose.renameAddressBookItem(ABComposeActionMethod.RightClickEdit, addressbook, renamed);
		
		// Verify the folder with the new name exists
		obj.zFolder.zExists(renamed);

		SelNGBase.needReset.set(false);
	}

	/**
	 * To create AB folder and then delete the same.Verify the creation and
	 * deletion of the AB folder
	 */
	@Test(
			description = "To create AB folder and then delete the same.Verify the creation and deletion of the AB folder",
			groups = { "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void createAndQDeleteABFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		FolderItem addressbook = new FolderItem();
		addressbook.name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		zWaitTillObjectExist("folder", addressbook.name);
		
		page.zMailApp.zDeleteFolder(addressbook.name);
		
		obj.zFolder.zClick(localize(locator.trash));
		obj.zFolder.zExists(addressbook.name);

		SelNGBase.needReset.set(false);
	}

	@Test(
			description = "Drag and Drop an addressbook to a different folder",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void moveABFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		FolderItem addressbook = new FolderItem();
		addressbook.name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Contacts') and contains(text(), '"
						+ addressbook.name + "')]",
				page.zABCompose.zEmailedContactsFolder);
		
		Assert
				.assertTrue(SelNGBase.selenium.get()
						.isElementPresent("//div[@id='zti__main_Contacts__13']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
								+ addressbook.name + "')]"));

		SelNGBase.needReset.set(false);
	}

	@Test(
			description = "Create a duplicate folder with same name.  Verify error dialog box appears.",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateABFolder() throws Exception {
		
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		FolderItem addressbook = new FolderItem();
		addressbook.name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create addressbook
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		
		obj.zButton .zRtClick(replaceUserNameInStaticId(replaceUserNameInStaticId(page.zABCompose.zNewABOverviewPaneIcon)));
		obj.zMenuItem.zClick(localize(locator.newAddrBook));
		SleepUtil.sleep(1000);
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				addressbook.name, localize(locator.createNewAddrBook));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewAddrBook));
		assertReport(localize(locator.errorAlreadyExists, addressbook.name, ""),
				obj.zDialog.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewAddrBook));

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}

}