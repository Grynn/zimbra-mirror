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
	
	private void DeleteItems(ContactItem contactItem, ContactGroupItem group  ) throws HarnessException {
		 	             
		 	
		  
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

	}
		  
	@Test(	description = "Delete a contact group permanently by Empty Trash folder on context menu",
			groups = { "smoke" })
	public void EmptyTrashFolderClickOK() throws HarnessException {

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
		
			
		DeleteItems(contactItem, group);
		       
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		// refresh Trash folder
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, trash);
   	 		
				
		DialogWarning dialogWarning = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY ,trash);
	
		// Click OK
		dialogWarning.zClickButton(Button.B_OK);
		
        //verify deleted contact group not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if ((ci.fileAs.equals(group.groupName)) || (ci.fileAs.equals(contactItem.fileAs))) 
			{
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact group " + group.groupName + " deleted");        
		
	}
	@Test(	description = "Cancel  Empty Trash folder on context menu",
			groups = { "functional" })
	public void EmptyTrashFolderClickCancel() throws HarnessException {
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
			
		DeleteItems(contactItem, group);
		       
		
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		// refresh Trash folder
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, trash);
   	 		
		
		DialogWarning dialogWarning = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY ,trash);
	
		// Click Cancel
		dialogWarning.zClickButton(Button.B_CANCEL);
		
        //verify deleted contact group still displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		int countFileExisted=0;
		for (ContactItem ci : contacts) {
			if ((ci.fileAs.equals(group.groupName)) || (ci.fileAs.equals(contactItem.fileAs))) 
			{
	            countFileExisted++;	 				
			}
		}
		
        ZAssert.assertTrue(countFileExisted==2, "Verify contact group " + group.groupName + " and contact " + contactItem.fileAs + " existed");        
		
	}




}
