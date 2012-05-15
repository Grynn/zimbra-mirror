package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.accounts;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class RemoveDelegate extends AjaxCommonTest {

	public RemoveDelegate() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}


	@Test(
			description = "Remove a delegate",
			groups = { "functional" }
			)
	public void RemoveDelegate_01() throws HarnessException {

		
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
		String buttonLocator = "css=div[id$='_PRIMARY'] td[id$='_title']:contains('Remove')"; // TODO: I18N
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(buttonLocator), "Verify the add delegate button is present");
		app.zPagePreferences.zClickAt(buttonLocator, "");
		
		// Wait for the dialog to appear (?)
//		DialogDelegate dialog = new DialogDelegate(app, app.zPagePreferences);
//		dialog.zClickButton(Button.B_OK);
		
		
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
	
}
