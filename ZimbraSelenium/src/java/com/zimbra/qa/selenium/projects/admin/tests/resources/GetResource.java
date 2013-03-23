/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.admin.tests.resources;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.ResourceItem;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class GetResource extends AdminCommonTest {
	

	public GetResource() {
		logger.info("New "+ GetResource.class.getCanonicalName());

		// All tests start at the "Resources" page
		super.startingPage = app.zPageManageResources;

	}



	/**
	 * Testcase : Verify delete resource operation -- Manage resource View -- Location
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Select a resource.
	 * 4. Delete a resource using delete button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Manage resource View -- Location",
			groups = { "smoke" })
	public void GetResource_01() throws HarnessException {
	
		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Refresh list to populate account.
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageResources.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the account is found");
	
	}



	/**
	 * Testcase : Verify delete resource operation -- Manage resource View -- Equipment
	 * Steps :
	 * 1. Create a resource using SOAP.
	 * 2. Go to Manage resource View.
	 * 3. Select a resource.
	 * 4. Delete a resource using delete button in Gear box menu.
	 * 5. Verify resource is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete resource operation -- Manage resource View -- Equipment",
			groups = { "smoke" })
	public void GetResource_02() throws HarnessException {
	
		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Refresh list to populate account.
		app.zPageManageResources.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
	
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageManageResources.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for resource "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the account is found");
	
	}

	/**
	 * Testcase : Verify created resource is displayed in UI.
	 * Steps :
	 * 1. Create a resource of type Location using SOAP.
	 * 2. Verify resource is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created resource is present in the resource list view",
			groups = { "smoke" })
	public void GetResource_03() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Location" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(resource.getEmailAddress());
		
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for account "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the account is found");

	}
	
	/**
	 * Testcase : Verify created resource is displayed in UI.
	 * Steps :
	 * 1. Create a resource of type Equipment using SOAP.
	 * 2. Verify resource is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created resource is present in the resource list view",
			groups = { "smoke" })
	public void GetResource_04() throws HarnessException {

		// Create a new resource in the Admin Console using SOAP
		ResourceItem resource = new ResourceItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>"
				 		+ "<name>" + resource.getEmailAddress() + "</name>"
				 		+ "<a n=\"displayName\">" + resource.getName() + "</a>"
				 		+ "<a n=\"zimbraCalResType\">" + "Equipment" + "</a>"
				 		+ "<password>test123</password>"
				 		+ "</CreateCalendarResourceRequest>");
		
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(resource.getEmailAddress());
		
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Get the list of displayed accounts
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the resource list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for account "+ resource.getEmailAddress() + " found: "+ a.getGEmailAddress());
			if ( resource.getEmailAddress().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the account is found");

	}

}
