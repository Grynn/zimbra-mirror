package com.zimbra.qa.selenium.projects.ajax.tests.preferences.quickcommands;

import java.util.ArrayList;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.QuickCommand;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.items.QuickCommand.QCAction;
import com.zimbra.qa.selenium.framework.items.QuickCommand.QCItemTypeId;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class GetQuickCommand extends AjaxCommonTest {

	public QuickCommand command1;
	public QuickCommand command2;

	
	public GetQuickCommand() {

		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;


	}

	@BeforeClass( groups = { "always" } )
	public void addQuickCommands() throws HarnessException {
	
		// Create a tag
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
				"<tag name='"+ tagname +"' color='1' />" +
		"</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(ZimbraAccount.AccountZWC(), tagname);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		// Create a subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
				"<folder name='"+ foldername +"' l='"+ FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), FolderItem.SystemFolder.Inbox).getId() +"'/>" +
		"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		ArrayList<QCAction> actions = new ArrayList<QCAction>();
		actions.add(new QCAction(1, QCAction.QCTypeId.actionTag, tag.getId(), true));
		actions.add(new QCAction(2, QCAction.QCTypeId.actionFileInto, folder.getId(), true));


		String command1name = "name" + ZimbraSeleniumProperties.getUniqueString();
		String command1desc = "description" + ZimbraSeleniumProperties.getUniqueString();
		this.command1 = new QuickCommand(1, command1name, command1desc, QCItemTypeId.MSG, true, actions);

		String command2name = "name" + ZimbraSeleniumProperties.getUniqueString();
		String command2desc = "description" + ZimbraSeleniumProperties.getUniqueString();
		this.command2 = new QuickCommand(2, command2name, command2desc, QCItemTypeId.MSG, true, actions);

		// Create a quick command
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
				+		"<pref name='zimbraPrefQuickCommand'>"+ this.command1.toString() +"</pref>"
				+		"<pref name='zimbraPrefQuickCommand'>"+ this.command2.toString() +"</pref>"
				+	"</ModifyPrefsRequest>");

		
		// Re-login to pick up the new preferences
		ZimbraAccount account = ZimbraAccount.AccountZWC();
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(account);

	}

	@Test(
			description = "Get a list of basic Quick Commands",
			groups = { "functional" }
	)
	public void GetQuickCommand_01() throws HarnessException {

				// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		// Verify that the quick commands exist in the list
		int count = app.zTreePreferences.sGetCssCount("css=div[id='zl__QCV__rows'] div[id^='zli__QCV__']");
		ZAssert.assertEquals(count, 2, "Verify the two quick commands exist in the list");

	}
	
	@Test(
			description = "Verify the Quick Command data in the list",
			groups = { "functional" }
	)
	public void GetQuickCommand_02() throws HarnessException {

				// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		// Verify that the quick commands exist in the list
		int count = app.zTreePreferences.sGetCssCount("css=div[id='zl__QCV__rows'] div[id^='zli__QCV__']");
		ZAssert.assertEquals(count, 2, "Verify the two quick commands exist in the list");

		
		// See: https://bugzilla.zimbra.com/show_bug.cgi?id=63991
		
		QuickCommand found1 = null;
		QuickCommand found2 = null;
		for (int i = 1; i <= count; i++) {
			String locator = "css=div[id='zl__QCV__rows'] div[id^='zli__QCV__']:nth-of-type("+ i +")";
			String name = app.zTreePreferences.sGetText(locator + " td[id$='_na']");
			String description = app.zTreePreferences.sGetText(locator + " td[id$='_de']");
			if ( name.equals(command1.getName()) && description.equals(command1.getDescription()))
				found1 = command1;
			if ( name.equals(command2.getName()) && description.equals(command2.getDescription()))
				found2 = command2;	
		}
		
		ZAssert.assertNotNull(found1, "Verify Quick Command #1 appears in the list");
		ZAssert.assertNotNull(found2, "Verify Quick Command #2 appears in the list");
		
	}

}
