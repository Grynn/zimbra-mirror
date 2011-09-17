package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.findshares;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShareFind;


public class FindShare extends PrefGroupMailByMessageTest {

	
	public FindShare() {
		logger.info("New "+ FindShare.class.getCanonicalName());
		
		
		
		
		
		
	}
	
	@Test(	description = "Search for a share from another user.  Add it.",
			groups = { "functional" })
	public void CreateMountpoint_01() throws HarnessException {
		
		ZimbraAccount Owner = new ZimbraAccount();
		Owner.provision();
		Owner.authenticate();

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerfolder"+ ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem ownerInbox = FolderItem.importFromSOAP(Owner, FolderItem.SystemFolder.Inbox);
		ZAssert.assertNotNull(ownerInbox, "Verify the new owner folder exists");

		Owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + ownerFoldername +"' l='" + ownerInbox.getId() +"' view='message'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem ownerFolder = FolderItem.importFromSOAP(Owner, ownerFoldername);
		ZAssert.assertNotNull(ownerFolder, "Verify the new owner folder exists");
		
		Owner.soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ ownerFolder.getId() +"' op='grant'>"
				+			"<grant d='" + app.zGetActiveAccount().EmailAddress + "' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Click Find Shares
		DialogShareFind dialog = (DialogShareFind)app.zTreeMail.zPressButton(Button.B_TREE_FIND_SHARES);
		
		// Find shares from the user
		dialog.zSetFindEmail(Owner.EmailAddress);
		dialog.zClickButton(Button.B_SEARCH);

		// Check the folder item
		List<String> items = dialog.zListGetFolders();
		String found = null;
		for ( String f : items) {
			if ( f.contains(ownerFoldername) ) { 
				// Found it!
				found = f;
				break;
			}
		}
		
		ZAssert.assertNotNull(found, "verify the shared folder shows up in the tree");
		
		
		// Check the box and add the share
		dialog.zTreeItem(Action.A_TREE_CHECKBOX, found);
		

		dialog.zClickButton(Button.B_ADD);

		
		
		// Verify the mountpoint exists
		app.zGetActiveAccount().soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'/>");
		
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:link[@owner='"+ Owner.EmailAddress +"']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the mountpoint is listed in the folder tree");

		String rid = app.zGetActiveAccount().soapSelectValue("//mail:link[@owner='"+ Owner.EmailAddress +"']", "rid");
		ZAssert.assertEquals(rid, ownerFolder.getId(), "Verify the mountpoint is listed in the folder tree");
	}



}
