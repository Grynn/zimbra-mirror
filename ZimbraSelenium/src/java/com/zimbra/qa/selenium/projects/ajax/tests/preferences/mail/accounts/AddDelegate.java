package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogDelegate;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class AddDelegate extends AjaxCommonTest {

	public AddDelegate() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}


	@Test(
			description = "Add a 'Send As' delegate to the primary account",
			groups = { "functional" }
			)
	public void AddDelegate_01() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		
		//-- GUI Steps
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Add Delegate')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		dialog.zSetEmailAddress(delegate.EmailAddress);
		dialog.zCheckRight(DialogDelegate.Rights.SendAs);
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		String gt = app.zGetActiveAccount().soapSelectValue("//acct:ace[@d='"+ delegate.EmailAddress +"']", "gt");
		String right = app.zGetActiveAccount().soapSelectValue("//acct:ace[@d='"+ delegate.EmailAddress +"']", "right");
	
		ZAssert.assertEquals(gt, "usr", "Verify the user (usr) right was set correctly");
		ZAssert.assertEquals(right, "sendAs", "Verify the sendAs (sendAs) right was set correctly");
	}
	
	@Test(
			description = "Add a 'Send On Behalf Of' delegate to the primary account",
			groups = { "functional" }
			)
	public void AddDelegate_02() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		
		//-- GUI Steps
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Add Delegate')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		dialog.zSetEmailAddress(delegate.EmailAddress);
		dialog.zCheckRight(DialogDelegate.Rights.SendOnBehalfOf);
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		String gt = app.zGetActiveAccount().soapSelectValue("//acct:ace[@d='"+ delegate.EmailAddress +"']", "gt");
		String right = app.zGetActiveAccount().soapSelectValue("//acct:ace[@d='"+ delegate.EmailAddress +"']", "right");
	
		ZAssert.assertEquals(gt, "usr", "Verify the user (usr) right was set correctly");
		ZAssert.assertEquals(right, "sendOnBehalfOf", "Verify the sendAs (sendAs) right was set correctly");
	}

}
