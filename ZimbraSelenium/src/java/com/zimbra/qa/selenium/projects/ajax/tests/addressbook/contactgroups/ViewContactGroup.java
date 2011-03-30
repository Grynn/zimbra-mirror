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
	
       app.zGetActiveAccount().soapSend(
            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
            "<cn >" +
            "<a n='type'>group</a>" +
            "<a n='nickname'>" + group.groupName +"</a>" +
            "<a n='dlist'>" + group.getDList() + "</a>" +
            "<a n='fileAs'>8:" +  group.fileAs +"</a>" +
            "</cn>" +
            "</CreateContactRequest>");
       
       return group;
	}
	
    
	@Test(	description = "View a contact group created via soap",
			groups = { "functional" })
	public void DisplayContactGroup_01() throws HarnessException {
		         		
	    // Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app);
	
	    // Select the contact group
		DisplayContactGroup groupView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);
	  
		ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.FileAs), group.fileAs, "Verify contact group email (" + group.fileAs + ") displayed");	
		
		for (int i=0; i<group.dlist.size(); i++) {
	       ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Email), group.dlist.get(i), "Verify contact group email (" + group.dlist.get(i) + ") displayed");	
		}            
   	}

	@Test(	description = "Click Alphabetbar button All: Verify contact groups started with digit and A-Z listed ",
			groups = { "functional" })
	public void DisplayContactGroup_02() throws HarnessException {
	
		 // Create  contact groups 
		ContactGroupItem group1 = createContactGroup("B");    
    	ContactGroupItem group2 = createContactGroup("5");
    	
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_ALL);
		
		
		//verify all contact groups name are listed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		int countGroup=0;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group1.fileAs) ||
			    ci.fileAs.equals(group2.fileAs) ) 
			{
	            countGroup++;
	            if (countGroup == 2) 
	 			   break;
			}			
		}
	
		ZAssert.assertTrue(countGroup==2, "Verify contact groups (" + group1.fileAs + " and "+ group2.fileAs + ") displayed ");
	}

	@Test(	description = "Click Alphabetbar button 123: Verify contact groups started with digit listed and A-Z not-listed ",
			groups = { "functional" })
	public void DisplayContactGroup_03() throws HarnessException {
	
		 // Create  contact groups 
		ContactGroupItem group1 = createContactGroup("B");    
    	ContactGroupItem group2 = createContactGroup("5");
    	
        
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
			if (ci.fileAs.equals(group1.fileAs)) {
				countGroup--; 
			}
				
		}
	
		ZAssert.assertTrue(countGroup==1, "Verify contact groups " + group1.fileAs + " not displayed, and "+ group2.fileAs + " displayed ");
	}

	@Test(	description = "Click Alphabetbar button B: Verify only contact groups started with B|b is listed ",
			groups = { "functional" })
	public void DisplayContactGroup_04() throws HarnessException {
	
		 // Create  contact groups 
		ContactGroupItem group0 = createContactGroup("b");
		ContactGroupItem group1 = createContactGroup("B");		
    	ContactGroupItem group2 = createContactGroup("5");
    	ContactGroupItem group3 = createContactGroup("V");
    	
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click 123      
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_B);
		SleepUtil.sleepLong();
		
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
	}

}

