package com.zimbra.qa.selenium.projects.ajax.tests.preferences.addressbook;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class AddNewContactsToEmailedContactOptIn extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public AddNewContactsToEmailedContactOptIn() {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{				
		 		put("zimbraPrefAutoAddAddressEnabled", "TRUE");
			}
		};
	}

	/**
	 * Test case : Verify select checkbox works (e.g make the option changed to opt-out)
	 * @throws HarnessException
	 */
	@Test(description= " select the checkbox to toggle the opt-in option to opt-out ", groups= {"smoke" })
	public void UnSelectAutoAddAddressCheckbox() throws HarnessException {
		// Go to "Addressbook"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.AddressBook);

		// Verify the status of the checkbox is TRUE
		ZAssert.assertTrue(app.zPagePreferences.zGetCheckboxStatus("zimbraPrefAutoAddAddressEnabled"),
				  "Verify if zimbraPrefAutoAddAddressEnabled is TRUE, the preference box is checked" );			
	
		// Uncheck the box
		app.zPagePreferences.zCheckboxSet("css=input[id$=_AUTO_ADD_ADDRESS]",false);
			
		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);		
		
		// Verify the status of the checkbox is FALSE
		// frontend check
		ZAssert.assertFalse(app.zPagePreferences.zGetCheckboxStatus("zimbraPrefAutoAddAddressEnabled"),
				  "Verify the preference box is unchecked" );
		
		// backend check
		app.zGetActiveAccount().soapSend(
                   "<GetPrefsRequest xmlns='urn:zimbraAccount'>"
                 +     "<pref name='zimbraPrefAutoAddAddressEnabled'/>"
                 + "</GetPrefsRequest>");

		ZAssert.assertEquals(app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefAutoAddAddressEnabled']", null),
				"FALSE", "Verify zimbraPrefAutoAddAddressEnabled is FALSE" );
				
		// Revert to original value for subsequent test cases
		// Check the box
		app.zPagePreferences.zCheckboxSet("css=input[id$=_AUTO_ADD_ADDRESS]",true);
			
		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);		
		
	}

	
}
