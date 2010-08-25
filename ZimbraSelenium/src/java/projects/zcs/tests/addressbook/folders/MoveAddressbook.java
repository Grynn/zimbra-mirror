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
public class MoveAddressbook extends CommonTest {
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


	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}

}