package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.external;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShareRevoke;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;


public class RevokeShare extends PrefGroupMailByMessageTest {

	public RevokeShare() {
		logger.info("New "+ RevokeShare.class.getCanonicalName());
		
		
	}
	
	@Test(	description = "Revoke a folder share - External",
			groups = { "smoke" })
	public void RevokeShare_01() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String externalEmail = ZimbraSeleniumProperties.getStringProperty("external.yahoo.account");

		// Create a subfolder in Inbox
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername +"' l='" + inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");
		String folderid = app.zGetActiveAccount().soapSelectValue("//mail:folder", "id");

		// Create a subfolder in Inbox
		app.zGetActiveAccount().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folderid +"' op='grant'>"
				+			"<grant d='"+ externalEmail +"' inh='1' gt='guest' pw='' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");

		//Need to do Refresh by clicking on getmail button to see folder in the list 
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Make sure the folder was created on the server
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(subfolder, "Verify the folder exists on the server");


		// Right click on folder, select "Edit"
		DialogEditFolder editdialog = (DialogEditFolder)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, subfolder);
		ZAssert.assertNotNull(editdialog, "Verify the edit dialog pops up");

		//Click Edit link on Edit properties dialog
		DialogShareRevoke sharedialog = (DialogShareRevoke)editdialog.zClickButton(Button.O_REVOKE_LINK);
		ZAssert.assertTrue(sharedialog.zIsActive(), "Verify that the share dialog pops up");

		//click Yes
		sharedialog.zClickButton(Button.B_YES);
		
		
		// Verify the account has shared the folder
		app.zGetActiveAccount().soapSend(
					"<GetFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder l='" + folderid + "'/>"
				+	"</GetFolderRequest>");
		
		/** Example response:
		 *     <GetFolderResponse xmlns="urn:zimbraMail">
		 *           <folder f="i" rev="2" i4next="258" i4ms="2" ms="4" n="0" activesyncdisabled="0" l="2" id="257" s="0" name="folder13379798458754" uuid="a4d8c530-d8f5-46e2-9798-c87c86968c82" luuid="9dce7c49-ec67-4315-868f-bbf090605034">
		 *             <acl guestGrantExpiry="1345755322480">
		 *               <grant zid="zimbraexternal@yahoo.com" gt="guest" pw="" perm="r"/>
		 *             </acl>
 		 *          </folder>
		 *         </GetFolderResponse>
		 * 
		 **/

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:grant");
		ZAssert.assertEquals(nodes.length, 0, "Verify the folder is not granted");

	}

	

	

}
