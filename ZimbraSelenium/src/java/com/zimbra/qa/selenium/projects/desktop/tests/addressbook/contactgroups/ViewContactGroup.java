package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contactgroups;

import java.util.List;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.addressbook.*;


public class ViewContactGroup extends AjaxCommonTest  {
	public ViewContactGroup() {
		logger.info("New "+ ViewContactGroup.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;		

	}

   private ContactGroupItem _createContactGroup(String firstLetterOfGroupName) throws HarnessException {
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

   @Test(   description = "View a contact group created via soap",
         groups = { "functional" })
   public void DisplayContactGroupInfo_Emails() throws HarnessException {

      // Create a contact group via Soap then select
      ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);

      // Select the contact group
      DisplayContactGroup groupView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);

      ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Company), group.fileAs  , "Verify contact group email (" + group.fileAs + ") displayed");   

      for (int i=0; i<group.dlist.size(); i++) {
         ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Email), group.dlist.get(i).email, "Verify contact group email (" + group.dlist.get(i) + ") displayed");   
      }            
   }

   @Test(   description = "Click Alphabetbar button All: Verify contact groups started with digit and A-Z listed ",
         groups = { "functional" })
   public void ClickAllVerifyDisplayAllContactGroups() throws HarnessException {

      // Create  contact groups 
      ContactGroupItem group1 = _createContactGroup("B");    
      ContactGroupItem group2 = _createContactGroup("5");
      ContactGroupItem group3 = _createContactGroup("b");    

      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

      //click All       
      app.zPageAddressbook.zToolbarPressButton(Button.B_AB_ALL);

      //verify all contact groups name are listed
      List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
      int countGroup=0;
      for (ContactItem ci : contacts) {
         if (ci.fileAs.equals(group1.fileAs) ||
               ci.fileAs.equals(group2.fileAs) ||
               ci.fileAs.equals(group3.fileAs) ) {
            countGroup++;
         }        
      }

      ZAssert.assertTrue(countGroup==3, "Verify contact groups (" + group1.fileAs + "," + group2.fileAs + " and "+ group3.fileAs + ") displayed ");
   }

   @Test(   description = "Click Alphabetbar button 123: Verify contact groups started with digit listed and A-Z not-listed ",
         groups = { "functional" })
   public void Click123VerifyDisplayContactStartWithDigit() throws HarnessException {

      // Create  contact groups 
      ContactGroupItem group1 = _createContactGroup("B");    
      ContactGroupItem group2 = _createContactGroup("5");
      ContactGroupItem group3 = _createContactGroup("b");

      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

      //click 123      
      app.zPageAddressbook.zToolbarPressButton(Button.B_AB_123);

      //verify all group started with 123 listed, group started with alphabet are not listed
      List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
      int countGroup= 0;
      for (ContactItem ci : contacts) {
         if (ci.fileAs.equals(group2.fileAs)) {
            countGroup++;
         }
         if (ci.fileAs.equals(group1.fileAs) ||
            ci.fileAs.equals(group3.fileAs)  
             ) {
            countGroup--; 
         }
            
      }

      ZAssert.assertTrue(countGroup==1, "Verify contact groups " + group1.fileAs + "," + group3.fileAs + " not displayed, and "+ group2.fileAs + " displayed ");
   }

   @Test(   description = "Click Alphabetbar button B: Verify only contact groups started with B|b is listed ",
         groups = { "functional" })
   public void ClickBVerifyDisplayContactStartWithBb() throws HarnessException {

      // Create  contact groups 
      ContactGroupItem group0 = _createContactGroup("b");
      ContactGroupItem group1 = _createContactGroup("B");    
      ContactGroupItem group2 = _createContactGroup("5");
      ContactGroupItem group3 = _createContactGroup("V");

      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

      //click B      
      app.zPageAddressbook.zToolbarPressButton(Button.B_AB_B);

      //verify all group started with B listed, other groups not listed
      List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
      int countGroup= 0;
      for (ContactItem ci : contacts) {
         if (ci.fileAs.equals(group1.fileAs) || ci.fileAs.equals(group0.fileAs)) {
            countGroup++;
         }
         if (ci.fileAs.equals(group2.fileAs) || ci.fileAs.equals(group3.fileAs) )  {
            countGroup--; 
         }

      }

      ZAssert.assertTrue(countGroup==2, "Verify contact groups " + group1.fileAs + " " + group0.fileAs + " displayed, and " + group2.fileAs + "," + group3.fileAs + " not displayed ");
   }

   @Test(   description = "Click all Alphabetbar buttons: Verify only contact group started with the alphabet is listed ",
         groups = { "functional" })
   public void ClickAllAlphabetBarButtons() throws HarnessException {

      // Create  contact groups 
      ContactGroupItem[]    cgiArray = new ContactGroupItem[26];
      Button[]           buttonArray = 
      {Button.B_AB_A,Button.B_AB_B,Button.B_AB_C,Button.B_AB_D,Button.B_AB_E,Button.B_AB_F,Button.B_AB_G,
            Button.B_AB_H,Button.B_AB_I,Button.B_AB_J,Button.B_AB_K,Button.B_AB_L,Button.B_AB_M,Button.B_AB_N,
            Button.B_AB_O,Button.B_AB_P,Button.B_AB_Q,Button.B_AB_R,Button.B_AB_S,Button.B_AB_T,Button.B_AB_U,
            Button.B_AB_V,Button.B_AB_W,Button.B_AB_X,Button.B_AB_Y,Button.B_AB_Z};

      for (int i=0; i<26; i++) {
         cgiArray[i] = _createContactGroup(Character.toString((char)((int)'a' + i)));
      }     

      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

      for (int i=0; i<26; i++) {
         //click button      
         app.zPageAddressbook.zToolbarPressButton(buttonArray[i]);

        //verify group started with button name listed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
        int countGroup= 0;
        for (ContactItem ci : contacts) {
           if (ci.fileAs.equals(cgiArray[i].fileAs) || ci.fileAs.equals(cgiArray[i].fileAs)) {
              countGroup++;
           }
        }

        ZAssert.assertTrue(countGroup==1, "Verify contact groups " + cgiArray[i].fileAs +  " displayed");

        // Delete the verified contact to save time going through it in contact list
        // for subsequent test cases.
        app.zPageAddressbook.zListItem(Action.A_CHECKBOX, cgiArray[i].fileAs);

        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

        String expectedMsg = "1 contact group moved to Trash";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
              expectedMsg , "Verify toast message '" + expectedMsg + "'");

      }
   }   
}

