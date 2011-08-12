package com.zimbra.qa.selenium.projects.ajax.tests.preferences.zimlets;

import java.util.Hashtable;

import org.testng.annotations.Test;

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
		ZAssert.assertEquals(count, 5, "Verify 5 zimlets are shown in the preferences page");
	}
	
	@Test(
			description = "Verify the LinkedIn table text",
			groups = { "functional" }
			)
	public void GetZimlets_02() throws HarnessException {

		Hashtable<String, String> table = new Hashtable<String, String>();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String tablelocator = "css=div[id='ZmPrefZimletListView']";
		
		// The locator to the rows (not including the header)
		String locator = tablelocator + " div[id$='__rows']";

		// Get how many rows there are
		int count = app.zPagePreferences.sGetCssCount(locator + ">div");
		
		// Scroll through all the rows.  Make sure the details are correct
		// TODO: need to I18N
		for (int i = 1; i <= count; i++) {
			String itemlocator = locator +">div:nth-of-type("+ i +")";

			String name = app.zPagePreferences.sGetText(itemlocator + " td[id$='__na']");
			String description = app.zPagePreferences.sGetText(itemlocator + " td[id$='__ds']");

			table.put(name, description);
			
		}
		
		ZAssert.assertNotNull(table.get("LinkedIn"), "Verify the LinkedIn entry exists");
		ZAssert.assertEquals(table.get("LinkedIn"), "Hooks on to email Zimlet; shows LinkedIn search result for a given email.", "Verify the LinkedIn description");
		
		
	}

	@Test(
			description = "Verify the Phone table text",
			groups = { "functional" }
			)
	public void GetZimlets_03() throws HarnessException {

		Hashtable<String, String> table = new Hashtable<String, String>();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String tablelocator = "css=div[id='ZmPrefZimletListView']";
		
		// The locator to the rows (not including the header)
		String locator = tablelocator + " div[id$='__rows']";

		// Get how many rows there are
		int count = app.zPagePreferences.sGetCssCount(locator + ">div");
		
		// Scroll through all the rows.  Make sure the details are correct
		// TODO: need to I18N
		for (int i = 1; i <= count; i++) {
			String itemlocator = locator +">div:nth-of-type("+ i +")";

			String name = app.zPagePreferences.sGetText(itemlocator + " td[id$='__na']");
			String description = app.zPagePreferences.sGetText(itemlocator + " td[id$='__ds']");

			table.put(name, description);
			
		}
		
		ZAssert.assertNotNull(table.get("Phone"), "Verify the Phone entry exists");
		ZAssert.assertEquals(table.get("Phone"), "Highlights phone numbers to enable Skype calls.", "Verify the Phone description");
		
		
	}

	
	@Test(
			description = "Verify the Search Highlighter table text",
			groups = { "functional" }
			)
	public void GetZimlets_04() throws HarnessException {

		Hashtable<String, String> table = new Hashtable<String, String>();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String tablelocator = "css=div[id='ZmPrefZimletListView']";
		
		// The locator to the rows (not including the header)
		String locator = tablelocator + " div[id$='__rows']";

		// Get how many rows there are
		int count = app.zPagePreferences.sGetCssCount(locator + ">div");
		
		// Scroll through all the rows.  Make sure the details are correct
		// TODO: need to I18N
		for (int i = 1; i <= count; i++) {
			String itemlocator = locator +">div:nth-of-type("+ i +")";

			String name = app.zPagePreferences.sGetText(itemlocator + " td[id$='__na']");
			String description = app.zPagePreferences.sGetText(itemlocator + " td[id$='__ds']");

			table.put(name, description);
			
		}
		
		ZAssert.assertNotNull(table.get("Search Highlighter"), "Verify the Search Highlighter entry exists");
		ZAssert.assertEquals(table.get("Search Highlighter"), "After a mail search, this Zimlet highlights Search terms with yellow color.", "Verify the Search Highlighter description");
		
		
	}


	@Test(
			description = "Verify the WebEx table text",
			groups = { "functional" }
			)
	public void GetZimlets_05() throws HarnessException {

		Hashtable<String, String> table = new Hashtable<String, String>();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String tablelocator = "css=div[id='ZmPrefZimletListView']";
		
		// The locator to the rows (not including the header)
		String locator = tablelocator + " div[id$='__rows']";

		// Get how many rows there are
		int count = app.zPagePreferences.sGetCssCount(locator + ">div");
		
		// Scroll through all the rows.  Make sure the details are correct
		// TODO: need to I18N
		for (int i = 1; i <= count; i++) {
			String itemlocator = locator +">div:nth-of-type("+ i +")";

			String name = app.zPagePreferences.sGetText(itemlocator + " td[id$='__na']");
			String description = app.zPagePreferences.sGetText(itemlocator + " td[id$='__ds']");

			table.put(name, description);
			
		}
		
		ZAssert.assertNotNull(table.get("WebEx"), "Verify the WebEx entry exists");
		ZAssert.assertEquals(table.get("WebEx"), "Easily schedule, start or join WebEx meetings.", "Verify the WebEx description");
		
		
	}

	@Test(
			description = "Verify the Zimbra Social table text",
			groups = { "functional" }
			)
	public void GetZimlets_06() throws HarnessException {

		Hashtable<String, String> table = new Hashtable<String, String>();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String tablelocator = "css=div[id='ZmPrefZimletListView']";
		
		// The locator to the rows (not including the header)
		String locator = tablelocator + " div[id$='__rows']";

		// Get how many rows there are
		int count = app.zPagePreferences.sGetCssCount(locator + ">div");
		
		// Scroll through all the rows.  Make sure the details are correct
		// TODO: need to I18N
		for (int i = 1; i <= count; i++) {
			String itemlocator = locator +">div:nth-of-type("+ i +")";

			String name = app.zPagePreferences.sGetText(itemlocator + " td[id$='__na']");
			String description = app.zPagePreferences.sGetText(itemlocator + " td[id$='__ds']");

			table.put(name, description);
			
		}
		
		ZAssert.assertNotNull(table.get("Zimbra Social"), "Verify the Zimbra Social entry exists");
		ZAssert.assertEquals(table.get("Zimbra Social"), "Access social services like Twitter, Facebook, Digg and TweetMeme.", "Verify the Zimbra Social description");
		
		
	}



}
