package com.zimbra.qa.selenium.projects.ajax.tests.preferences.zimlets;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class GetZimlets extends AjaxCommonTest {

	public GetZimlets() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}


	@Test(
			description = "View the number of zimlets in the default list",
			groups = { "functional" }
			)
	public void GetZimlets_01() throws HarnessException {

		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String tablelocator = "css=div[id='ZmPrefZimletListView']";
		
		// The locator to the rows (not including the header)
		String locator = tablelocator + " div[id$='__rows']";

		// Get how many rows there are
		int count = app.zPagePreferences.sGetCssCount(locator + ">div[id^='zli__']");
		
		// IronMaiden: 5 zimlets - LinkedIn, Phone, Search Highlighter, Webex, Zimbra Social
		// IronMaiden: Bug 50123: 3 zimlets - Phone, Search Highlighter, Webex, Y-Emoticons
		ZAssert.assertEquals(count, 4, "Verify 4 zimlets are shown in the preferences page");
	}
	
	// IronMaiden: Bug 50123: 3 zimlets - Phone, Search Highlighter, Webex
	@Bugs(ids = "50123")
	@Test(
			description = "Verify the LinkedIn table text",
			groups = { "deprecated" }
			)
	public void GetZimlets_02() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);
		
		// The locator to the rows (not including the header)
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_linkedin__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_linkedin__ds']");

		ZAssert.assertEquals(name, "LinkedIn", "Verify the LinkedIn entry name");
		ZAssert.assertEquals(description, "Hooks on to email Zimlet; shows LinkedIn search result for a given email.", "Verify the LinkedIn description");		
		
	}

	@Test(
			description = "Verify the Phone table text",
			groups = { "functional" }
			)
	public void GetZimlets_03() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_phone__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_phone__ds']");
		
		ZAssert.assertEquals(name, "Phone", "Verify the Phone entry exists");
		ZAssert.assertEquals(description, "Highlights phone numbers to enable Skype calls.", "Verify the Phone description");
		
		
	}

	
	@Test(
			description = "Verify the Search Highlighter table text",
			groups = { "functional" }
			)
	public void GetZimlets_04() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_srchhighlighter__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_srchhighlighter__ds']");
		
		ZAssert.assertEquals(name, "Search Highlighter", "Verify the Search Highlighter entry exists");
		ZAssert.assertEquals(description, "After a mail search, this Zimlet highlights Search terms with yellow color.", "Verify the Search Highlighter description");
		
		
	}


	@Test(
			description = "Verify the WebEx table text",
			groups = { "functional" }
			)
	public void GetZimlets_05() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_webex__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_webex__ds']");
		
		ZAssert.assertEquals(name, "WebEx", "Verify the WebEx entry exists");
		ZAssert.assertEquals(description, "Easily schedule, start or join WebEx meetings.", "Verify the WebEx description");
		
		
	}

	// IronMaiden: Bug 50123: 3 zimlets - Phone, Search Highlighter, Webex
	@Bugs(ids = "50123")
	@Test(
			description = "Verify the Zimbra Social table text",
			groups = { "deprecated" }
			)
	public void GetZimlets_06() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_social__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_social__ds']");
		
		ZAssert.assertEquals(name, "Zimbra Social", "Verify the Zimbra Social entry exists");
		ZAssert.assertEquals(description, "Access social services like Twitter, Facebook, Digg and TweetMeme.", "Verify the Zimbra Social description");
		
		
	}

	@Test(
			description = "Verify the Y-Emoticons table text",
			groups = { "functional" }
			)
	public void GetZimlets_07() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_ymemoticons__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_ymemoticons__ds']");
		
		ZAssert.assertEquals(name, "Yahoo! Emoticons", "Verify the Y Emoticons entry exists");
		ZAssert.assertEquals(description, "Displays Yahoo! Emoticons images in email messages.", "Verify the Y Emoticons description");
		
		
	}



}
