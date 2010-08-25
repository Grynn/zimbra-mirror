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
public class DeleteAddressbook extends CommonTest {
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



	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}

}