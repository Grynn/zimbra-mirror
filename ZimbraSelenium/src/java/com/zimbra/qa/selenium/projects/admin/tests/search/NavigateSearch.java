/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.admin.tests.search;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageSearch;

public class NavigateSearch extends AdminCommonTest {
	
	public NavigateSearch() {
		logger.info("New "+ NavigateSearch.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageSearch;
	}
	
	/**
	 * Testcase : Navigate to Search page
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Search --> Search --> Options"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Search",
			groups = { "sanity" })
			public void NavigateSearch_01() throws HarnessException {

		
		/*
		 * Verify navigation path -- "Home --> Search --> Search --> All Results"
		 */
		app.zPageManageSearch.zClickTreeItemOfSearch(PageManageSearch.Locators.ALL_RESULT);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.ALL_RESULT), "Verfiy the \"All Result\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search --> Accounts"
		 */
		
		app.zPageManageSearch.zClickTreeItemOfSearch(PageManageSearch.Locators.ACCOUNTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.ACCOUNTS), "Verfiy the \"Accounts\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search --> Domains"
		 */
		app.zPageManageSearch.zClickTreeItemOfSearch(PageManageSearch.Locators.DOMAINS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.DOMAINS), "Verfiy the \"Domains\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search --> Distribution Lists"
		 */
		app.zPageManageSearch.zClickTreeItemOfSearch(PageManageSearch.Locators.DISTRIBUTION_LISTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.DISTRIBUTION_LISTS), "Verfiy the \"Distribution Lists\" text exists in navigation path");

		
	}

	/**
	 * Testcase : Navigate to Search page -- Search
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Search --> Search --> Options"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Search",
			groups = { "sanity" })
			public void NavigateSearch_02() throws HarnessException {

		
		/*
		 * Verify navigation path -- "Home --> Search --> Search Options --> Basci Attributes"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.BASIC_ATTRIBUTES);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH_OPTIONS), "Verfiy the \"Search Options\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.BASIC_ATTRIBUTES), "Verfiy the \"Basic Attributes\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search Options --> Status"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.STATUS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH_OPTIONS), "Verfiy the \"Search Options\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.STATUS), "Verfiy the \"Status\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search Options --> Last Login Time"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.LAST_LOGIN_TIME);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH_OPTIONS), "Verfiy the \"Search Options\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.LAST_LOGIN_TIME), "Verfiy the \"Last Login Time\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search Options --> External Email Address"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.EXTERNAL_EMAIL_ADDRESS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH_OPTIONS), "Verfiy the \"Search Options\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.EXTERNAL_EMAIL_ADDRESS), "Verfiy the \"External Email Address\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search Options --> COS"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.COS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH_OPTIONS), "Verfiy the \"Search Options\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.COS), "Verfiy the \"COS\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search Options --> Server"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.SERVER);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH_OPTIONS), "Verfiy the \"Search Options\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.SERVER), "Verfiy the \"Server\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Search Options --> Domains"
		 */
		app.zPageManageSearch.zClickTreeItemOfSearch(PageManageSearch.Locators.SEARCH_OPTION_DOMAINS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH_OPTIONS), "Verfiy the \"Search Options\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.DOMAINS), "Verfiy the \"Domains\" text exists in navigation path");

	}
	
	/**
	 * Testcase : Navigate to Search page -- Search
	 * Steps :
	 * 1. Verify navigation path -- "Home --> Search --> Search --> Options"
	 * @throws HarnessException
	 */
	@Test(	description = "Navigate to Search",
			groups = { "sanity" })
			public void NavigateSearch_03() throws HarnessException {

		
		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> Inactive Accounts (90 days)"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.INACTIVE_ACCOUNTS_90);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.INACTIVE_ACCOUNTS_90), "Verfiy the \"Inactive Accounts (90 days)\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> Locked Out Accounts"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.LOCKED_OUT_ACCOUNTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.LOCKED_OUT_ACCOUNTS), "Verfiy the \"Locked Out Accounts\" text exists in navigation path");
		
		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> Non-Active Accounts"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.NON_ACTIVE_ACCOUNTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.NON_ACTIVE_ACCOUNTS), "Verfiy the \"Non-Active Accounts\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> Inactive Accounts (30 days)"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.INACTIVE_ACCOUNTS_30);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.INACTIVE_ACCOUNTS_30), "Verfiy the \"Inactive Accounts (30 days)\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> Admin Accounts"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.ADMIN_ACCOUNTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.ADMIN_ACCOUNTS), "Verfiy the \"Admin Accounts\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> External Accounts"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.EXTERNAL_ACCOUNTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.EXTERNAL_ACCOUNTS), "Verfiy the \"External Accounts\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> Closed Accounts"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.CLOSED_ACCOUNTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.CLOSED_ACCOUNTS), "Verfiy the \"Closed Accounts\" text exists in navigation path");

		/*
		 * Verify navigation path -- "Home --> Search --> Saved Searches --> Maintenance Accounts"
		 */
		app.zPageManageSearch.zClickTreeItem(PageManageSearch.TreeItem.MAINTENANCE_ACCOUNTS);
		
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.HOME), "Verfiy the \"Home\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SEARCH), "Verfiy the \"Search\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.Locators.SAVED_SEARCHES), "Verfiy the \"Saved Searches\" text exists in navigation path");
		ZAssert.assertTrue(app.zPageManageSearch.zVerifyHeader(PageManageSearch.TreeItem.MAINTENANCE_ACCOUNTS), "Verfiy the \"Maintenance Accounts\" text exists in navigation path");
		
	}

}
