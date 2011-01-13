package com.zimbra.qa.selenium.projects.zcs.tests.addressbook.folders;

import junit.framework.Assert;

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
import com.zimbra.qa.selenium.projects.zcs.ui.ABCompose.ABComposeActionMethod;


/**
 * @written by Prashant Jaiswal & updated by Jitesh
 * 
 */
@SuppressWarnings("static-access")
public class DeleteAddressbook extends CommonTest {
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
		addressbook.setName("folder" + ZimbraSeleniumProperties.getUniqueString());
		
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		zWaitTillObjectExist("folder", addressbook.getName());
		
		page.zMailApp.zDeleteFolder(addressbook.getName());
		
		obj.zFolder.zClick(localize(locator.trash));
		obj.zFolder.zExists(addressbook.getName());

		SelNGBase.needReset.set(false);
	}

}