package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class TagContactGroup extends AjaxCommonTest  {
	public TagContactGroup() {
		logger.info("New "+ TagContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Tag a contact group",
			groups = { "smoke" })
	public void TagContact_01() throws HarnessException {
	
        // Create a contact group 
		ContactGroupItem group = CreateContactGroup.CreateContactGroupViaSoap(app);
		
		group.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
                 
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs); 
        
	    String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Click new tag
		DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
				
	
		// Make sure the tag was created on the server (get the tag ID)
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tagName +"']", "id");

		// Make sure the tag was applied to the contact
		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>" +
						"<cn id='"+ group.getId() +"'/>" +
					"</GetContactsRequest>");
		
		String contactTags = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
		 
		ZAssert.assertEquals(contactTags, tagID, "Verify the tag appears on the contact id=" +  group.getId());
		
		//verify toasted message '1 contact tagged ...'
        String expectedMsg = "1 contact group tagged \"" + tagName + "\"";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
		        expectedMsg , "Verify toast message '" + expectedMsg + "'");

  
   	}
	
	
  	
}

