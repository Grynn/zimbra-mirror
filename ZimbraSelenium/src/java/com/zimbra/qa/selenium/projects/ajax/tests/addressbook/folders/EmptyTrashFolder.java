package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;


public class EmptyTrashFolder extends AjaxCommonTest {

	public EmptyTrashFolder() {
		logger.info("New "+ EmptyTrashFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	private void DeleteItems(ContactItem contactItem, ContactGroupItem group, FolderItem folderItem , FolderItem trash ) throws HarnessException {
		 	             
		 			  
	      // Refresh the view, to pick up the newly created ones
	      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");	      
	      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	  
	      // Select the items
	      app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group.fileAs);		       
	      app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contactItem.fileAs);        
	  
  	     //delete contact + group by click Delete button on toolbar
          app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

          //verify toasted message 2 contacts moved to Trash
          String expectedMsg = "2 contacts moved to Trash";
          ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
	        expectedMsg , "Verify toast message '" + expectedMsg + "'");

	      //verify deleted contact + group not displayed
          List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
	           
        
	      int count=0;
	      for (ContactItem ci : contacts) {
		    if (ci.fileAs.equals(group.groupName) ||			  
			    ci.fileAs.equals(contactItem.fileAs)
		      ) {
             count++;	 			
	  	    }
	      }
	
          ZAssert.assertTrue(count==0, "Verify contact + group " + contactItem.fileAs  + "," +  group.groupName + " deleted");                  

  		  // Delete the folder using context menu
  		  AbsPage page= app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, folderItem);

  		  // Verify Delete option is enabled
  		  ZAssert.assertNotNull(page, "Verify Delete option is enabled");

 		
  		  // refresh Trash folder
          app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, trash);
     	  
  		  // Verify the folder is now in the trash
  		  folderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderItem.getName());
  		  ZAssert.assertNotNull(folderItem, "Verify the folder Item is again available");
  		  ZAssert.assertEquals(trash.getId(), folderItem.getParentId(), "Verify the folder's parent is now the trash folder ID");

	}
		  
	@Test(	description = "Delete a contact, group, and folder permanently by Empty Trash folder on context menu",
			groups = { "smoke" })
	public void ClickOK() throws HarnessException {

		// Create a contact group via Soap
		ContactGroupItem group = ContactGroupItem.createUsingSOAP(app);
		group.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
		String[] dlist = app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn/mail:a[@n='dlist']", null).split(","); //a[2]   
		for (int i=0; i<dlist.length; i++) {
			  group.addDListMember(dlist[i]);
		  }
		  
		// Create a contact via Soap
	    ContactItem contactItem = ContactItem.createUsingSOAP(app);			             			
		contactItem.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));

		// Create a new folder
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);		
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);

        // Get the trash folder item
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");

		// Move items to trash
		DeleteItems(contactItem, group, folderItem, trash);

		// Now open empty trash dialog
		DialogWarning dialogWarning = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY ,trash);
	
		// Click OK
		dialogWarning.zClickButton(Button.B_OK);
		
        //verify Trash folder is empty
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();  	           	
        ZAssert.assertTrue((contacts.size()==0), "Verify no contact and group in Trash folder ");   
        
        List<FolderItem> list= app.zPageAddressbook.zListGetFolders(app.zGetActiveAccount(),  trash);
        ZAssert.assertTrue((list.size()==0), "Verify no folder in Trash folder");   
        
		
	}
	@Test(	description = "Cancel Empty Trash folder option",
			groups = { "functional" })
	public void ClickCancel() throws HarnessException {
		// Create a contact group via Soap
		ContactGroupItem group = ContactGroupItem.createUsingSOAP(app);
		group.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
		String[] dlist = app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn/mail:a[@n='dlist']", null).split(","); //a[2]   
		for (int i=0; i<dlist.length; i++) {
			  group.addDListMember(dlist[i]);
		  }
		  
		// Create a contact via Soap
	    ContactItem contactItem = ContactItem.createUsingSOAP(app);			             			
		contactItem.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
			
		// Create a new folder
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);		
		FolderItem folderItem = CreateFolder.createNewFolderViaSoap(userRoot,app);

        // Get the trash folder item
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");

		// Move items to trash
		DeleteItems(contactItem, group, folderItem, trash);
							
		// Now open empty trash dialog
		DialogWarning dialogWarning = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY ,trash);
	
		// Click Cancel
		dialogWarning.zClickButton(Button.B_CANCEL);
		
        //verify deleted contact & group are still displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		int countFileExisted=0;
		for (ContactItem ci : contacts) {
			if ((ci.fileAs.equals(group.groupName)) || (ci.fileAs.equals(contactItem.fileAs))) 
			{
	            countFileExisted++;	 				
			}
		}
		
        ZAssert.assertTrue(countFileExisted==2, "Verify contact group " + group.groupName + " and contact " + contactItem.fileAs + " existed");        
	
        //verify the folder is still displayed    
        boolean isFolderDisplayed=false;
        List<FolderItem> list= app.zPageAddressbook.zListGetFolders(app.zGetActiveAccount(),  trash);
		for (FolderItem i: list) {
			if (i.getName().equals(folderItem.getName())) {
				isFolderDisplayed=true;
				break;
			}
		}
		
		ZAssert.assertTrue(isFolderDisplayed, "Verify the folder (" + folderItem.getName() + ") still  displayed ");		
	
	}




}
