package projects.zcs.tests.addressbook.folders;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
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
public class CreateAddressbook extends CommonTest {
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
	 * Create a new addressbook
	 * 
	 */
	@Test(
			description = "Test to create a addressbook folder",
			groups = { "sanity", "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void createAddressbookFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		FolderItem addressbook = new FolderItem();
		addressbook.name = "original" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create the addressbook
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		zWaitTillObjectExist("folder", addressbook.name);
		
		// Verify the folder with the new name exists
		obj.zFolder.zExists(addressbook.name);

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