package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
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

	//verify a contact group is untaged via UI & soap
	private void VerifyContactGroupUntag(ContactGroupItem group, String tagName) throws HarnessException {
		String expectedMsg = "Tag \"" + tagName + "\" removed from 1 contact group";
		
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

	//get tag id via soap
	private String GetTagid(String tagName) throws HarnessException{
			
	   // Create a tag via soap
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
               	"<tag name='"+ tagName +"' color='1' />" +
               "</CreateTagRequest>");
		String tagid = app.zGetActiveAccount().soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");

		return tagid;
	}
	
	@Test(	description = "Untag a contact group by click Tag->Remove tag on toolbar ",
			groups = { "smoke" })
	public void UnTagContactGroup_01() throws HarnessException {
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();	
		String tagid = GetTagid(tagName);
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, tagid);
	  
    	// Untag it by click Tag->Remove Tag on toolbar 
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);
    
        // Verify contact group untagged
		VerifyContactGroupUntag(group, tagName);
	 
   	}
	

	@Test(	description = "Untag a contact group by click Tag->Remove tag on Context Menu",
			groups = { "functional" })
	public void UnTagContactGroup_02() throws HarnessException {
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();	
		String tagid = GetTagid(tagName);
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, tagid);
	  
    	// Untag it by click Tag->Remove Tag on context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , group.fileAs);

	     
        // Verify contact group untagged
		VerifyContactGroupUntag(group,tagName);
	 
   	}

}

