package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class UnTagContactGroup extends AjaxCommonTest  {
	public UnTagContactGroup() {
		logger.info("New "+ UnTagContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Untag a contact group",
			groups = { "smoke" })
	public void UnTagContact_01() throws HarnessException {


	    String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
			// Create a tag via soap
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
                	"<tag name='"+ tagName +"' color='1' />" +
                "</CreateTagRequest>");
		String tagid = app.zGetActiveAccount().soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");

		  // Create a contact group 
		ContactGroupItem group = CreateContactGroup.CreateContactGroupViaSoap(app, tagid);
		group.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
	      
             
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
               
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);


    	// Untag it
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);


		//verify toasted message 'contact created'
		String expectedMsg = "All tags removed from 1 contact group";
	    ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
			        expectedMsg , "Verify toast message '" + expectedMsg + "'");

	 

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>" +
						"<cn id='"+ group.getId() +"'/>" +
					"</GetContactsRequest>");
		String contactTag = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");

		ZAssert.assertNull(contactTag, "Verify that the tag is removed from the contact");

	 
   	}
	
	
}

