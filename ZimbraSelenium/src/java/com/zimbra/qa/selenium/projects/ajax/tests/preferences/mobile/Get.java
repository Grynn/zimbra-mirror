package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mobile;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class Get extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public Get() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String , String>() {{
		    put("zimbraFeatureMobileSyncEnabled", "TRUE");
		}};
	}


	@Test(
			description = "View the shortcuts preference page",
			groups = { "functional", "network" }
			)
	public void Get_01() throws HarnessException {

		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MobileDevices);

		
		// Verify the page is showing
		String locator = "css=div[id$='_deviceList']";
		
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(locator), "Verify the page is present");
		ZAssert.assertTrue(app.zPagePreferences.zIsVisiblePerPosition(locator, 0, 0), "Verify the page is visible");
		
	}
}
