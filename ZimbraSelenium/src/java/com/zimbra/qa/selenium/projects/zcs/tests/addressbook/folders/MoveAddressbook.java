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
public class MoveAddressbook extends CommonTest {
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
	
	@Test(
			description = "Drag and Drop an addressbook to a different folder",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void moveABFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		FolderItem addressbook = new FolderItem();
		addressbook.setName("folder" + ZimbraSeleniumProperties.getUniqueString());
		
		page.zABCompose.createAddressBookItem(ActionMethod.DEFAULT, addressbook);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Contacts') and contains(text(), '"
						+ addressbook.getName() + "')]",
				page.zABCompose.zEmailedContactsFolder);
		
		Assert
				.assertTrue(ClientSessionFactory.session().selenium()
						.isElementPresent("//div[@id='zti__main_Contacts__13']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
								+ addressbook.getName() + "')]"));

		SelNGBase.needReset.set(false);
	}

}