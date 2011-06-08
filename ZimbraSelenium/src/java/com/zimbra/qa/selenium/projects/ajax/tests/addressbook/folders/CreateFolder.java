package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.*;
import java.util.*;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
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

	private void CreateFolderAndVerify(DialogCreateFolder createFolderDialog) throws HarnessException{
		String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		createFolderDialog.zEnterFolderName(folderName);
		createFolderDialog.zClickButton(Button.B_OK);
	
		// Make sure the folder was created on the ZCS server
		//FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),folderName);
		//ZAssert.assertNotNull(folder, "Verify the folder created on the srver");
		//ZAssert.assertEquals(folder.getName(), folderName,"Verify the server and client folder names match");

		//Verify created folder listed on the left menu
		boolean isFolderDisplayed=false;
		List<FolderItem> list= app.zPageAddressbook.zListGetFolders();
		for (FolderItem i: list) {
			if (i.getName().equals(folderName)) {
				isFolderDisplayed=true;
				break;
			}
		}
		
		ZAssert.assertTrue(isFolderDisplayed, "Verify folder (" + folderName + ") displayed ");		
	}

	
	@Test(description = "Create a new folder by clicking 'new folder' on folder tree", groups = { "sanity" })
	public void ClickNewFolderOnFolderTree() throws HarnessException {
	
		FolderItem folderItem = FolderItem.importFromSOAP(app
				.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot,
				_soapDestination, app.zGetActiveAccount().EmailAddress);
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, Button.B_TREE_NEWFOLDER, folderItem);
	
		CreateFolderAndVerify(createFolderDialog);
	}
 
	

	@Test(description = "Create a new folder using context menu from root folder", groups = { "functional" })
	public void ClickContextMenuNewAddressbook() throws HarnessException {
	
		FolderItem folderItem = FolderItem.importFromSOAP(app
				.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot,
				_soapDestination, app.zGetActiveAccount().EmailAddress);
		
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zPageAddressbook
				.zListItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,
						folderItem);

		CreateFolderAndVerify(createFolderDialog);
	}

	@Test(description = "Create a new folder using   New -> New Addressbook", groups = { "functional" })
	public void ClickMenuNewNewAddressbook() throws HarnessException {

		// using the context menu + New Folder
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_ADDRESSBOOK);
		ZAssert.assertNotNull(createFolderDialog, "Verify the new dialog opened");

		CreateFolderAndVerify(createFolderDialog);
	}


}


