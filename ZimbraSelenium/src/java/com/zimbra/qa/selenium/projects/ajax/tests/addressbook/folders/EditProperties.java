package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import java.util.List;

import org.testng.annotations.Test;


import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DialogEditFolder.FolderColor;


public class EditProperties extends AjaxCommonTest {

	public EditProperties() {
		logger.info("New "+ EditProperties.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	private void RenameAndVerify(FolderItem folderItem, DialogEditFolder dialog, FolderItem parent)
	    throws HarnessException {
		// Set the name, click OK
		String oldName = folderItem.getName();
		String name    = "folder" + ZimbraSeleniumProperties.getUniqueString();
		dialog.zSetNewName(name);
		dialog.zClickButton(Button.B_OK);

		// Verify folder names created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),oldName);
		ZAssert.assertNull(folder, "Verify the old folder name not found on the server");
		
		folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),name);
		ZAssert.assertNotNull(folder, "Verify the new folder name found on the server");		
	
	}

	private void ChangeColorAndVerify(FolderItem folderItem, DialogEditFolder dialog) 
	  throws HarnessException {
		// Change the color, click OK 
		dialog.zSetNewColor(FolderColor.Gray);
		dialog.zClickButton(Button.B_OK);

		throw new HarnessException("Implement me!");
		// TODO: Verify the new color rendered
		
		//FolderItem subfolder2 = FolderItem.importFromSOAP(app
		//		.zGetActiveAccount(), color);
		//ZAssert.assertNotNull(subfolder2, "Verify the subfolder is available");
	}
	
	private void RenameChangeColorAndVerify(FolderItem folderItem, DialogEditFolder dialog, FolderItem parent)
    throws HarnessException {
	 	// Set the name, click OK
		String oldName = folderItem.getName();
		String name    = "folder" + ZimbraSeleniumProperties.getUniqueString();

		dialog.zSetNewName(name);
		// Change the color, click OK 
		dialog.zSetNewColor(FolderColor.Gray);
		
		dialog.zClickButton(Button.B_OK);
	
		//Verify created folder listed on the left menu
		boolean isNewFolderDisplayed=false;
		boolean isOldFolderDisplayed=false;
		
		List<FolderItem> list= app.zPageAddressbook.zListGetFolders(app.zGetActiveAccount(),parent);
		for (FolderItem i: list) {
			if (i.getName().equals(name)) {
				isNewFolderDisplayed=true;			
			}
			else if (i.getName().equals(oldName)) {
				isOldFolderDisplayed=true;			
			}
		}
		
		ZAssert.assertTrue(isNewFolderDisplayed, "Verify new folder (" + name + ") displayed ");		
		ZAssert.assertFalse(isOldFolderDisplayed, "Verify old folder (" + oldName + ") not displayed ");		
		
		throw new HarnessException("Verify color change");
		

}
	@Test(description = "Edit a folder, change the color (Context menu -> Edit)", groups = { "functional" })
    public void ChangeColorOfTopLevelFolder() throws HarnessException {
	
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(userRoot, "Verify can get the userRoot ");
	
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);
			
		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		ChangeColorAndVerify(folderItem,dialog);
	    	
	}

	@Test(description = "Edit a folder, change the color (Context menu -> Edit)", groups = { "functional" })
    public void ChangeColorOfSystemFolders() throws HarnessException {
	
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(userRoot, "Verify can get the userRoot ");
	
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);
			
		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		ChangeColorAndVerify(folderItem,dialog);	
	}
	
	@Test(description = "Edit a folder, change the color (Context menu -> Edit)", groups = { "functional" })
    public void ChangeColorOfSubFolder() throws HarnessException {
	
		FolderItem contact= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(contact, "Verify can get the contact folder ");
	
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(contact,app);
		
		// Expand parent node to show up sub folder
		app.zTreeContacts.zExpand(contact);
		
		// Change the folder's color using context menu
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		ChangeColorAndVerify(folderItem,dialog);	
	}
	
	
	@Test(description = "Edit a folder, change name(Context menu -> Edit)", groups = { "smoke" })
    public void ChangeNameOfTopLevelFolder() throws HarnessException {
	
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(userRoot, "Verify can get the userRoot ");
	
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);
			
		// Rename the folder 
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
	
	    RenameAndVerify(folderItem, dialog, userRoot);		
	}

	
	 @Test(description = "Edit a folder, change name(Context menu -> Edit)", groups = { "smoke" })
	 public void ChangeNameOfSubFolder() throws HarnessException {
	
		FolderItem contact= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		ZAssert.assertNotNull(contact, "Verify can get the userRoot ");
	
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(contact,app);
			
		// Expand parent node to show up sub folder
		app.zTreeContacts.zExpand(contact);
	
		// Rename the folder 
		DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
			
	    RenameAndVerify(folderItem, dialog, contact);		
	}
		
	 
	 @Test(	description = "Cannot rename an addressbook system folder- Right Click - Edit Properties - verify input name not displayed",
				groups = { "functional" })
     public void CannotRenameSystemFolders() throws HarnessException {
		 String newName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		 FolderItem folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		 ZAssert.assertNotNull(folder, "Verify can get the Contacts ");								
		 // Rename the folder 
		 DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);
		 ZAssert.assertNotNull(dialog.zSetNewName(newName), "Verify the folder name input not displayed");;
		 dialog.zClickButton(Button.B_OK);
		 
         folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
	     ZAssert.assertNotNull(folder, "Verify can get the EmailedContacts ");												  
  	     // Rename the folder 
		 dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);
		 ZAssert.assertNotNull(dialog.zSetNewName(newName), "Verify the folder name input not displayed");;
		 dialog.zClickButton(Button.B_OK);
		 
	     folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
	     ZAssert.assertNotNull(folder, "Verify can get the Trash ");										
	     // Rename the folder 
		 dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);
		 ZAssert.assertNull(dialog, "Verify Edit Properties not enabled for Trash folder ");		
	 }	

	 
	
	
	 @Test(description = "Edit a folder, change name and color Context menu -> Edit)", groups = { "functional" })
	 public void ChangeNameColorOfTopLevelFolder() throws HarnessException {
		
		 FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		 ZAssert.assertNotNull(userRoot, "Verify can get the userRoot ");
		
		 FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);
				
		 DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		 ZAssert.assertNotNull(dialog, "Verify the dialog opened");
			
		 RenameChangeColorAndVerify(folderItem, dialog, userRoot);		
	 }

		
    @Test(description = "Edit a folder, change name(Context menu -> Edit)", groups = { "functional" })
    public void ChangeNameColorOfSubFolder() throws HarnessException {
		
		 FolderItem contact= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
		 ZAssert.assertNotNull(contact, "Verify can get the userRoot ");
		
		 FolderItem folderItem = CreateFolder.createNewFolderViaSoap(contact,app);
				
		 // Expand parent node to show up sub folder
		 app.zTreeContacts.zExpand(contact);
		
		 DialogEditFolder dialog = (DialogEditFolder)app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folderItem);
		 ZAssert.assertNotNull(dialog, "Verify the dialog opened");
				
		 RenameChangeColorAndVerify(folderItem, dialog, contact);		
		
    }

}
