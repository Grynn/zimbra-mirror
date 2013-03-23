/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contactgroups;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.*;

public class TagContactGroup extends AjaxCommonTest  {
	public TagContactGroup() {
		logger.info("New "+ TagContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private void TagGroup(DialogTag dialogTag, ContactGroupItem group) throws HarnessException {
	
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();			
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
	
	@Test(	description = "Tag a contact group by click Tag button on toolbar",
			groups = { "smoke" })
	public void TagContactGroup_01() throws HarnessException {
	
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	           
		// Click new tag on Tag->New Tag on toolbar
		DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
		
		// Tag a contact group
        TagGroup(dialogTag, group);	  
   	}
	
	@Test(	description = "Tag a contact group by click Tag Group on Context Menu",
			groups = { "functional" })
	public void TagContactGroup_02() throws HarnessException {
	
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	           
		// Click Tag Group on context menu
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , group.fileAs);        
		
		// Tag a contact group
        TagGroup(dialogTag, group);	  
   	}
	
  	
}

