package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contacts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;

public class UnTagContact extends AjaxCommonTest  {
	public UnTagContact() {
		logger.info("New "+ UnTagContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}

	private ContactItem createContactWithTag(String tagName) throws HarnessException {
	   return createContactWithTag(tagName, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	private ContactItem createContactWithTag(String tagName,
	      SOAP_DESTINATION_HOST_TYPE destType,
	      String accountName) throws HarnessException{
      
      // Create a tag via soap
       app.zGetActiveAccount().soapSend(
         "<CreateTagRequest xmlns='urn:zimbraMail'>" +
               "<tag name='"+ tagName +"' color='1' />" +
            "</CreateTagRequest>",
            destType,
            accountName);
       String tagid = app.zGetActiveAccount().soapSelectValue(
             "//mail:CreateTagResponse/mail:tag", "id");

       String tagParam = " t='" + tagid + "'";;
       String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();     
       String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
       String email = "email" +  ZimbraSeleniumProperties.getUniqueString() +
             "@zimbra.com";
   
        //default value for file as is last, first
       String fileAs = lastName + ", " + firstName;

        app.zGetActiveAccount().soapSend(
            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
            "<cn " + tagParam + " fileAsStr='" + fileAs + "' >" +
            "<a n='firstName'>" + firstName +"</a>" +
            "<a n='lastName'>" + lastName +"</a>" +
            "<a n='email'>" + email + "</a>" +               
            "</cn>" +            
            "</CreateContactRequest>",
            destType,
            accountName);

                 
        ContactItem contactItem = ContactItem.importFromSOAP(
              app.zGetActiveAccount(),
              "FIELD[lastname]:" + lastName,
              destType,
              accountName);
    
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(
              app.zGetActiveAccount(),
              "Contacts",
              destType,
              accountName);

        if (accountName == null ||
              !accountName.equals(ZimbraAccount.clientAccountName)) {
           GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
           app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);
        }

        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
           
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);

        return contactItem;
   }

	private void VerifyTagRemove(String tagName, ContactItem contactItem) throws HarnessException{
	   VerifyTagRemove(tagName, contactItem, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	private void VerifyTagRemove(String tagName, ContactItem contactItem,
	      SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException{
      
	   //verify toasted message Tag xxxxxx removed from 1 contact
	   String expectedMsg = "Tag \"" + tagName + "\" removed from 1 contact";      
	   ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
	         expectedMsg , "Verify toast message '" + expectedMsg + "'");


	   GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
	   app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

	   app.zGetActiveAccount().soapSend(
	         "<GetContactsRequest xmlns='urn:zimbraMail'>" +
	         "<cn id='"+ contactItem.getId() +"'/>" +
	         "</GetContactsRequest>",
            destType,
            accountName);
       String contactTag = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");

       ZAssert.assertNull(contactTag, "Verify that the tag is removed from the contact");
   }

	@Test(	description = "Untag a contact by click Toolbar Tag, then select Remove Tag",
			groups = { "smoke" })
	public void clickToolbarTagRemoveTag() throws HarnessException {
      String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
      
      ContactItem contactItem = createContactWithTag(tagName);

      // Untag it
      app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);

      VerifyTagRemove(tagName, contactItem);
   }

	@Test(  description = "Untag a contact by click Tag->Remove Tag on context menu",
         groups = { "smoke" })
   public void clickContextMenuTagRemoveTag() throws HarnessException {
       String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
      
        ContactItem contactItem = createContactWithTag(tagName);

        // Untag it
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , contactItem.fileAs);
      
        VerifyTagRemove(tagName, contactItem);   
	}

   @Test(   description = "Untag a Local Folders' contact by click Toolbar Tag, then select Remove Tag",
         groups = { "smoke" })
   public void LocalClickToolbarTagRemoveTag() throws HarnessException {
      String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
      
      ContactItem contactItem = createContactWithTag(
            tagName,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      // Untag it
      app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);

      VerifyTagRemove(tagName,
            contactItem,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);
   }

   @Test(  description = "Untag a Local Folders' contact by click Tag->Remove Tag on context menu",
         groups = { "smoke" })
   public void LocalClickContextMenuTagRemoveTag() throws HarnessException {
       String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
      
        ContactItem contactItem = createContactWithTag(
              tagName,
              SOAP_DESTINATION_HOST_TYPE.CLIENT,
              ZimbraAccount.clientAccountName);

        // Untag it
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , contactItem.fileAs);
      
        VerifyTagRemove(tagName,
              contactItem,
              SOAP_DESTINATION_HOST_TYPE.CLIENT,
              ZimbraAccount.clientAccountName);   
   }
}

