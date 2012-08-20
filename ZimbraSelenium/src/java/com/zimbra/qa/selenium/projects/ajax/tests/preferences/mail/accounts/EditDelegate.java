package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.accounts;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogDelegate;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogError.DialogErrorID;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class EditDelegate extends AjaxCommonTest {

	public EditDelegate() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}


	@Test(
			description = "Edit a 'Send As' delegate - Add 'Send On Behalf Of'",
			groups = { "functional" }
			)
	public void EditDelegate_01() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		// Grant Send-As
		app.zGetActiveAccount().soapSend(
					"<GrantRightsRequest xmlns='urn:zimbraAccount'>"
				+		"<ace gt='usr' d='"+ delegate.EmailAddress +"' right='sendAs'/>"
				+	"</GrantRightsRequest>");
		
		
		
		//-- GUI Steps

		// Login to pick up the new delegate
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// Select the grant in the list
		String itemLocator = "css=div[id$='_PRIMARY'] div[id$='__na_name']:contains('"+ delegate.EmailAddress +"')";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(itemLocator), "Verify the delegate item is present in the list");
		app.zPagePreferences.zClickAt(itemLocator, "");

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Edit Permissions')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		// Send As is already checked
		// Also, check Send On Behalf Of
		dialog.zCheckRight(DialogDelegate.Rights.SendOnBehalfOf);
		dialog.zClickButton(Button.B_OK);
		
		SleepUtil.sleepSmall();
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		boolean foundSendAs = false;
		boolean foundSendOnBehalfOf = false;
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//acct:ace[@d='"+ delegate.EmailAddress +"']");
		for (Element e : nodes) {
			String right = e.getAttribute("right", null);
			if ( right != null ) {
				if ( right.equals("sendAs") ) {
					foundSendAs = true;
				} else if ( right.equals("sendOnBehalfOf") ) {
					foundSendOnBehalfOf = true;
				}
			}
		}
	
		ZAssert.assertTrue(foundSendAs, "Verify the sendAs is set");
		ZAssert.assertTrue(foundSendOnBehalfOf, "Verify the sendOnBehalfOf is set");

	}
	
	@Test(
			description = "Edit a 'Send As' delegate - Remove 'Send As' and Add 'Send On Behalf Of'",
			groups = { "functional" }
			)
	public void EditDelegate_02() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		// Grant Send-As
		app.zGetActiveAccount().soapSend(
					"<GrantRightsRequest xmlns='urn:zimbraAccount'>"
				+		"<ace gt='usr' d='"+ delegate.EmailAddress +"' right='sendAs'/>"
				+	"</GrantRightsRequest>");
		
		
		
		//-- GUI Steps

		// Login to pick up the new delegate
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		AbsDialog errorDialog = app.zPageMain.zGetErrorDialog(DialogErrorID.Zimbra);
		if ( (errorDialog != null) && (errorDialog.zIsActive()) ) {

		    // Dismiss the dialog and carry on
		    errorDialog.zClickButton(Button.B_OK);
		}

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// Select the grant in the list
		String itemLocator = "css=div[id$='_PRIMARY'] div[id$='__na_name']:contains('"+ delegate.EmailAddress +"')";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(itemLocator), "Verify the delegate item is present in the list");
		app.zPagePreferences.zClickAt(itemLocator, "");

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Edit Permissions')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		dialog.zCheckRight(DialogDelegate.Rights.SendOnBehalfOf);
		dialog.zClickButton(Button.B_OK);
		app.zPagePreferences.zClickAt(itemLocator, "");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		dialog.zWaitForActive();
		dialog.zUnCheckRight(DialogDelegate.Rights.SendAs);
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		boolean foundSendAs = false;
		boolean foundSendOnBehalfOf = false;
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//acct:ace[@d='"+ delegate.EmailAddress +"']");
		for (Element e : nodes) {
			String right = e.getAttribute("right", null);
			if ( right != null ) {
				if ( right.equals("sendAs") ) {
					foundSendAs = true;
				} else if ( right.equals("sendOnBehalfOf") ) {
					foundSendOnBehalfOf = true;
				}
			}
		}
	
		ZAssert.assertFalse(foundSendAs, "Verify the sendAs is NOT set");
		ZAssert.assertTrue(foundSendOnBehalfOf, "Verify the sendOnBehalfOf is set");

	}
	
	@Test(
			description = "Edit a 'Send As' delegate - Remove 'Send As'",
			groups = { "functional" }
			)
	public void EditDelegate_03() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		// Grant Send-As
		app.zGetActiveAccount().soapSend(
					"<GrantRightsRequest xmlns='urn:zimbraAccount'>"
				+		"<ace gt='usr' d='"+ delegate.EmailAddress +"' right='sendAs'/>"
				+	"</GrantRightsRequest>");
		
		
		
		//-- GUI Steps

		// Login to pick up the new delegate
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// Select the grant in the list
		String itemLocator = "css=div[id$='_PRIMARY'] div[id$='__na_name']:contains('"+ delegate.EmailAddress +"')";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(itemLocator), "Verify the delegate item is present in the list");
		app.zPagePreferences.zClickAt(itemLocator, "");

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Edit Permissions')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		dialog.zUnCheckRight(DialogDelegate.Rights.SendAs);
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		boolean foundSendAs = false;
		boolean foundSendOnBehalfOf = false;
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//acct:ace[@d='"+ delegate.EmailAddress +"']");
		for (Element e : nodes) {
			String right = e.getAttribute("right", null);
			if ( right != null ) {
				if ( right.equals("sendAs") ) {
					foundSendAs = true;
				} else if ( right.equals("sendOnBehalfOf") ) {
					foundSendOnBehalfOf = true;
				}
			}
		}
	
		ZAssert.assertFalse(foundSendAs, "Verify the sendAs is NOT set");
		ZAssert.assertFalse(foundSendOnBehalfOf, "Verify the sendOnBehalfOf is NOT set");

	}
	
	@Test(
			description = "Edit a 'Send On Behalf Of' delegate - Add 'Send As'",
			groups = { "functional" }
			)
	public void EditDelegate_11() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		// Grant Send-As
		app.zGetActiveAccount().soapSend(
					"<GrantRightsRequest xmlns='urn:zimbraAccount'>"
				+		"<ace gt='usr' d='"+ delegate.EmailAddress +"' right='sendOnBehalfOf'/>"
				+	"</GrantRightsRequest>");
		
		
		
		//-- GUI Steps

		// Login to pick up the new delegate
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// Select the grant in the list
		String itemLocator = "css=div[id$='_PRIMARY'] div[id$='__na_name']:contains('"+ delegate.EmailAddress +"')";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(itemLocator), "Verify the delegate item is present in the list");
		app.zPagePreferences.zClickAt(itemLocator, "");

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Edit Permissions')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		dialog.zCheckRight(DialogDelegate.Rights.SendAs);
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		boolean foundSendAs = false;
		boolean foundSendOnBehalfOf = false;
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//acct:ace[@d='"+ delegate.EmailAddress +"']");
		for (Element e : nodes) {
			String right = e.getAttribute("right", null);
			if ( right != null ) {
				if ( right.equals("sendAs") ) {
					foundSendAs = true;
				} else if ( right.equals("sendOnBehalfOf") ) {
					foundSendOnBehalfOf = true;
				}
			}
		}
	
		ZAssert.assertTrue(foundSendAs, "Verify the sendAs is set");
		ZAssert.assertTrue(foundSendOnBehalfOf, "Verify the sendOnBehalfOf is set");
	}

	
	@Test(
			description = "Edit a 'Send On Behalf Of' delegate - Remove 'Send On Behalf Of' and Add 'Send As'",
			groups = { "functional" }
			)
	public void EditDelegate_12() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		// Grant Send-As
		app.zGetActiveAccount().soapSend(
					"<GrantRightsRequest xmlns='urn:zimbraAccount'>"
				+		"<ace gt='usr' d='"+ delegate.EmailAddress +"' right='sendOnBehalfOf'/>"
				+	"</GrantRightsRequest>");
		
		
		
		//-- GUI Steps

		// Login to pick up the new delegate
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		AbsDialog errorDialog = app.zPageMain.zGetErrorDialog(DialogErrorID.Zimbra);
		if ( (errorDialog != null) && (errorDialog.zIsActive()) ) {

		    // Dismiss the dialog and carry on
		    errorDialog.zClickButton(Button.B_OK);
		}
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// Select the grant in the list
		String itemLocator = "css=div[id$='_PRIMARY'] div[id$='__na_name']:contains('"+ delegate.EmailAddress +"')";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(itemLocator), "Verify the delegate item is present in the list");
		app.zPagePreferences.zClickAt(itemLocator, "");

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Edit Permissions')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		dialog.zCheckRight(DialogDelegate.Rights.SendAs);
		dialog.zClickButton(Button.B_OK);
		app.zPagePreferences.zClickAt(itemLocator, "");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		dialog.zWaitForActive();
		dialog.zUnCheckRight(DialogDelegate.Rights.SendOnBehalfOf);
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		boolean foundSendAs = false;
		boolean foundSendOnBehalfOf = false;
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//acct:ace[@d='"+ delegate.EmailAddress +"']");
		for (Element e : nodes) {
			String right = e.getAttribute("right", null);
			if ( right != null ) {
				if ( right.equals("sendAs") ) {
					foundSendAs = true;
				} else if ( right.equals("sendOnBehalfOf") ) {
					foundSendOnBehalfOf = true;
				}
			}
		}
	
		ZAssert.assertTrue(foundSendAs, "Verify the sendAs is set");
		ZAssert.assertFalse(foundSendOnBehalfOf, "Verify the sendOnBehalfOf is NOT set");
	}

	@Test(
			description = "Edit a 'Send On Behalf Of' delegate - Remove 'Send On Behalf Of'",
			groups = { "functional" }
			)
	public void EditDelegate_13() throws HarnessException {

		
		//-- Data Setup
		
		// Create an account to delegate to
		ZimbraAccount delegate = new ZimbraAccount();
		delegate.provision();
		delegate.authenticate();
		
		// Grant Send-As
		app.zGetActiveAccount().soapSend(
					"<GrantRightsRequest xmlns='urn:zimbraAccount'>"
				+		"<ace gt='usr' d='"+ delegate.EmailAddress +"' right='sendOnBehalfOf'/>"
				+	"</GrantRightsRequest>");
		
		
		
		//-- GUI Steps

		// Login to pick up the new delegate
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		// Select the grant in the list
		String itemLocator = "css=div[id$='_PRIMARY'] div[id$='__na_name']:contains('"+ delegate.EmailAddress +"')";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(itemLocator), "Verify the delegate item is present in the list");
		app.zPagePreferences.zClickAt(itemLocator, "");

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=74282
		// TODO: Maybe this button should be abstracted?
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Edit Permissions')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear
		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
		dialog.zWaitForActive();

		dialog.zUnCheckRight(DialogDelegate.Rights.SendOnBehalfOf);
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<GetRightsRequest xmlns='urn:zimbraAccount' >"
				+		"<ace right='sendAs'/>"
				+		"<ace right='sendOnBehalfOf'/>"
				+	"</GetRightsRequest>");
		
		boolean foundSendAs = false;
		boolean foundSendOnBehalfOf = false;
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//acct:ace[@d='"+ delegate.EmailAddress +"']");
		for (Element e : nodes) {
			String right = e.getAttribute("right", null);
			if ( right != null ) {
				if ( right.equals("sendAs") ) {
					foundSendAs = true;
				} else if ( right.equals("sendOnBehalfOf") ) {
					foundSendOnBehalfOf = true;
				}
			}
		}
	
		ZAssert.assertFalse(foundSendAs, "Verify the sendAs is NOTset");
		ZAssert.assertFalse(foundSendOnBehalfOf, "Verify the sendOnBehalfOf is NOT set");
	}
}
