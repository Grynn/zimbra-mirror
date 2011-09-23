package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;



import java.util.List;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;


public class ViewContactGroup extends AjaxCommonTest  {
	public ViewContactGroup() {
		logger.info("New "+ ViewContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private ContactGroupItem createContactGroup(String firstLetterOfGroupName) throws HarnessException {
	 	ContactGroupItem group = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
	    group.groupName = firstLetterOfGroupName + group.groupName;
	    group.fileAs    = group.groupName;

   	   	StringBuilder sb= new StringBuilder("");
   		for (ContactItem contactItem: group.dlist) {
   			String e= contactItem.email;
   			sb.append("<m type='I' value='" + e + "' />");
   		}

   		app.zGetActiveAccount().soapSend(
            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
            "<cn >" +
            "<a n='type'>group</a>" +
            "<a n='nickname'>" + group.groupName +"</a>" +
            "<a n='fileAs'>8:" +  group.fileAs +"</a>" +
            sb.toString() +
            "</cn>" +
            "</CreateContactRequest>");
       
       return group;
	}
	
	
	private void verifyGroupDisplayed(ContactGroupItem group) throws HarnessException {
		// Select the contact group
		DisplayContactGroup groupView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);
	  
		// verify groupname
		ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Company), group.fileAs  , "Verify contact group email (" + group.fileAs + ") displayed");	
		
		// verify group members
		for (int i=0; i<group.dlist.size(); i++) {
	       ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Email), group.dlist.get(i).email, "Verify contact group email (" + group.dlist.get(i).email + ") displayed");	
		}		
	}
    
	@Test(	description = "View a contact group created via soap",
			groups = { "smoke" })
	public void DisplayContactGroupInfo() throws HarnessException {
		         		
	    // Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);

		// Verify group name and members displayed
		verifyGroupDisplayed(group);
   	}

	@Test(	description = "Click Alphabetbar button All: Verify contact groups started with digit and A-Z listed ",
			groups = { "smoke" })
	public void ClickAllVerifyDisplayAllContactGroups() throws HarnessException {
	
		 // Create  contact groups 
		ContactGroupItem group1 = createContactGroup("B");    
    	ContactGroupItem group2 = createContactGroup("5");
    	ContactGroupItem group3 = createContactGroup("b");    
    	
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_ALL);
					
		// Verify group name and members displayed
		verifyGroupDisplayed(group1);
		verifyGroupDisplayed(group2);
		verifyGroupDisplayed(group3);
		
	}

	@Test(	description = "Click Alphabetbar button 123: Verify contact groups started with digit listed and A-Z not-listed ",
			groups = { "functional" })
	public void Click123VerifyDisplayContactStartWithDigit() throws HarnessException {
	
		 // Create  contact groups 
		ContactGroupItem group1 = createContactGroup("B");    
    	ContactGroupItem group2 = createContactGroup("5");
    	ContactGroupItem group3 = createContactGroup("b");
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click 123      
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_123);
		
		//verify all group started with 123 listed, group started with alphabet are not listed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		int countGroup= 0;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group2.fileAs)) 
			{
	            countGroup++;
			}
			if (ci.fileAs.equals(group1.fileAs) ||
				ci.fileAs.equals(group3.fileAs) 	
			    ) {
				countGroup--; 
			}
				
		}
        
		//
		ZAssert.assertTrue(countGroup==1, "Verify contact groups " + group1.fileAs + "," + group3.fileAs + " not displayed, and "+ group2.fileAs + " displayed ");
		
		// Verify group name and members displayed
		verifyGroupDisplayed(group2);
		
	}

	@Test(	description = "Click Alphabetbar button Z: Verify only contact groups started with Z|z is listed ",
			groups = { "functional" })
	public void ClickBVerifyDisplayContactStartWithZz() throws HarnessException {
	
		 // Create  contact groups 
		ContactGroupItem group0 = createContactGroup("z");
		ContactGroupItem group1 = createContactGroup("Z");		
    	ContactGroupItem group2 = createContactGroup("5");
    	ContactGroupItem group3 = createContactGroup("V");
    	
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click Z      
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_Z);
		
		//verify all group started with B listed, other groups not listed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		int countGroup= 0;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group1.fileAs) || ci.fileAs.equals(group0.fileAs)) 
			{
	            countGroup++;
			}
			if (ci.fileAs.equals(group2.fileAs) || ci.fileAs.equals(group3.fileAs) )  {
				countGroup--; 
			}
				
		}
	
		ZAssert.assertTrue(countGroup==2, "Verify contact groups " + group1.fileAs + " " + group0.fileAs + " displayed, and " + group2.fileAs + "," + group3.fileAs + " not displayed ");

		
		// Verify group name and members displayed
		verifyGroupDisplayed(group0);
		verifyGroupDisplayed(group1);


	}
	
	@Test(	description = "Click all Alphabetbar buttons: Verify only contact group started with the alphabet is listed ",
			groups = { "functional" })
	public void ClickAllAlphabetBarButtons() throws HarnessException {
	
		 // Create  contact groups 
		ContactGroupItem[]    cgiArray = new ContactGroupItem[26];
		Button[]           buttonArray = 
		 {Button.B_AB_A,Button.B_AB_B,Button.B_AB_C,Button.B_AB_D,Button.B_AB_E,Button.B_AB_F,Button.B_AB_G,
		  Button.B_AB_H,Button.B_AB_I,Button.B_AB_J,Button.B_AB_K,Button.B_AB_L,Button.B_AB_M,Button.B_AB_N,
		  Button.B_AB_O,Button.B_AB_P,Button.B_AB_Q,Button.B_AB_R,Button.B_AB_S,Button.B_AB_T,Button.B_AB_U,
		  Button.B_AB_V,Button.B_AB_W,Button.B_AB_X,Button.B_AB_Y,Button.B_AB_Z};
		
		for (int i=0; i< cgiArray.length ; i++) {
			cgiArray[i] = createContactGroup(Character.toString((char)((int)'a' + i)));
		}		
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        for (int i=0; i< buttonArray.length ; i++) {
          //click button      
		  app.zPageAddressbook.zToolbarPressButton(buttonArray[i]);
		
		  // Verify group name and members displayed
		  verifyGroupDisplayed(cgiArray[i]);
			
		  //verify only group started with button name listed
		  List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		  for (ContactItem ci : contacts) {
			  ZAssert.assertTrue(ci.fileAs.toLowerCase().startsWith(Character.toString((char)((int)'a' + i))),
					  "Verify contact groups names start with " + Character.toString((char)((int)'a' + i)) + " displayed");								     				
		}
	
	  }
    }   
}

