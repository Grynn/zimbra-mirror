package com.zimbra.qa.selenium.projects.admin.tests.cos;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.CosItem;

public class GetCos extends AdminCommonTest {
	
	public GetCos() {
		logger.info("New " + GetCos.class.getCanonicalName());
		
		//All tests starts at "Cos" page
		super.startingPage=app.zPageManageCOS;
	}
	/**
	 * Testcase : Verify created cos is displayed in UI - Search list view.
	 * Steps :
	 * 1. Create a cos using SOAP.
	 * 2. Search cos created in Step-1
	 * 3. Verify cos is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created cos is displayed in UI - Search list view.",
			groups = { "smoke" })
			public void GetCos_01() throws HarnessException {

		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");
		
		
		SleepUtil.sleepSmall();

		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(cosName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the cos list is returned");

		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for cos "+ cosName + " found: "+ a.getGEmailAddress());
			if ( cosName.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the cos is returned correctly");

	}
}
