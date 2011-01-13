package com.zimbra.qa.selenium.projects.zcs.tests.addressbook.folders;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.util.RetryFailedTests;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.zcs.tests.CommonTest;
import com.zimbra.qa.selenium.projects.zcs.ui.ActionMethod;


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
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
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
		addressbook.setName("original" + ZimbraSeleniumProperties.getUniqueString());
		
		// Create the addressbook
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		zWaitTillObjectExist("folder", addressbook.getName());
		
		// Verify the folder with the new name exists
		obj.zFolder.zExists(addressbook.getName());

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
		addressbook.setName("folder" + ZimbraSeleniumProperties.getUniqueString());
		
		// Create addressbook
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		
		obj.zButton .zRtClick(replaceUserNameInStaticId(replaceUserNameInStaticId(page.zABCompose.zNewABOverviewPaneIcon)));
		obj.zMenuItem.zClick(localize(locator.newAddrBook));
		SleepUtil.sleep(1000);
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				addressbook.getName(), localize(locator.createNewAddrBook));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewAddrBook));
		assertReport(localize(locator.errorAlreadyExists, addressbook.getName(), ""),
				obj.zDialog.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewAddrBook));

		SelNGBase.needReset.set(false);
	}
}