package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.*;
import java.util.*;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DialogCreateFolder;

public class CreateFolder extends AjaxCommonTest {

	
	private static final SOAP_DESTINATION_HOST_TYPE _soapDestination = SOAP_DESTINATION_HOST_TYPE.SERVER;

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
	}

    static FolderItem createNewFolderViaSoap(FolderItem parent, AppAjaxClient app) throws HarnessException{
		
		// Create a folder 
		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name + "' view='contact' l='"+ parent.getId() +"'/>" +
                "</CreateFolderRequest>");

		// Refresh addressbook
	   	app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
		
		
		FolderItem folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(folderItem, "Verify the folderItem is available");

		return folderItem;
	}
   
	private void CreateFolderAndVerify(DialogCreateFolder createFolderDialog, SystemFolder parent) throws HarnessException{
		
		String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		createFolderDialog.zEnterFolderName(folderName);
		createFolderDialog.zClickButton(Button.B_OK);
	
		// Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),folderName);
		ZAssert.assertNotNull(folder, "Verify the folder created on the server");
		ZAssert.assertEquals(folder.getName(), folderName,"Verify folder name found on server");
		
	}

	
	@Test(description = "Create a new folder by clicking 'new folder' on folder tree", groups = { "sanity" })
	public void ClickNewFolderOnFolderTree() throws HarnessException {
	
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zTreeContacts.zPressButton(Button.B_TREE_NEWADDRESSBOOK);	
		   
		CreateFolderAndVerify(createFolderDialog, FolderItem.SystemFolder.UserRoot);
	}
 
	

	@Test(description = "Create a new folder using context menu from root folder", groups = { "sanity" })
	public void ClickContextMenuNewAddressbook() throws HarnessException {	
		FolderItem folderItem = FolderItem.importFromSOAP(app
                 .zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
	
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zTreeContacts.zTreeItem
		    (Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,folderItem);
		
		CreateFolderAndVerify(createFolderDialog,FolderItem.SystemFolder.UserRoot);
	}

	@Test(description = "Create a new folder using context menu from root folder", groups = { "smoke" })
	public void CreateSubFolderUnderContactsClickContextMenuNewAddressbook() throws HarnessException {	
		FolderItem folderItem = FolderItem.importFromSOAP(app
                 .zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
	
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zTreeContacts.zTreeItem
		    (Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,folderItem);
		
		CreateFolderAndVerify(createFolderDialog, FolderItem.SystemFolder.Contacts);
	}

	@Test(description = "Create a new folder using   New -> New Addressbook", groups = { "functional" })
	public void ClickMenuNewNewAddressbook() throws HarnessException {

		// using the context menu + New Folder
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_ADDRESSBOOK);
		ZAssert.assertNotNull(createFolderDialog, "Verify the new dialog opened");

		CreateFolderAndVerify(createFolderDialog, FolderItem.SystemFolder.UserRoot);
	}


}


