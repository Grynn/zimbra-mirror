package com.zimbra.qa.selenium.projects.ajax.tests.preferences.gui.features;

import java.util.HashMap;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class ZimbraFeatureOptionsEnabled extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraFeatureOptionsEnabled() {
		logger.info("New "+ ZimbraFeatureOptionsEnabled.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		super.startingAccountPreferences = new HashMap<String, String>() {
			{

				// Only options/Preferences is enabled
				put("zimbraFeatureOptionsEnabled", "TRUE");
				put("zimbraFeatureTasksEnabled", "FALSE");
				put("zimbraFeatureMailEnabled", "FALSE");
				put("zimbraFeatureContactsEnabled", "FALSE");
				put("zimbraFeatureCalendarEnabled", "FALSE");
				put("zimbraFeatureBriefcasesEnabled", "FALSE");
				put("zimbraZimletAvailableZimlets", "-com_zimbra_social");
				put("zimbraPrefAutocompleteAddressBubblesEnabled", "TRUE");

			}
		};

	}
	
	/**
	 * See http://bugzilla.zimbra.com/show_bug.cgi?id=62011 - WONTFIX
	 * @throws HarnessException
	 */
	@Bugs(ids="62011")	
	@Test(description = "Load the Preferences tab with just Preferences enabled", groups = { "deprecated" })
	public void ZimbraFeatureOptionsEnabled_01() throws HarnessException {
		
		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);

		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences
				.zGetCheckboxStatus("zimbraPrefAutocompleteAddressBubblesEnabled");

		// Since zimbraPrefAutocompleteAddressBubblesEnabled is set to TRUE, the
		// Bubble checkbox should be checked
		ZAssert.assertTrue(checked, "Verify if Address Bubbles check box is checked");

	}
}