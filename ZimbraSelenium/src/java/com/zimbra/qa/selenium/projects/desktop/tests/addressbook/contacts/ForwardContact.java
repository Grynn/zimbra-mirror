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
package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contacts;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.*;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;

public class ForwardContact extends AjaxCommonTest  {
	public ForwardContact() {
		logger.info("New "+ ForwardContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Forward a contact by click Forward on the toolbar",
			groups = { "functional" })
	public void InDisplayViewClickForwardOnToolbar() throws HarnessException {
			
	   // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
		//click Forward icon on toolbar
		FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zToolbarPressButton(Button.B_FORWARD);
        
		Assert.assertTrue(formMail.zHasAttachment(contactItem.fileAs + ".vcf"), "Verify there is  attachment named: " + contactItem.fileAs );

		//TODO: verify attachment file content
        
		//click Cancel
		DialogWarning dialogWarning= (DialogWarning) formMail.zToolbarPressButton(Button.B_CANCEL);
        
		//close the dialog
		dialogWarning.zClickButton(Button.B_NO);
	}

	@Test(   description = "Forward an editing contact by click Forward on the toolbar",
	      groups = { "functional" })
	public void InEditViewClickForwardOnToolbar() throws HarnessException {

	   // Create a contact via Soap then select
	   ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);

	   // click Edit button
	   app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);

	   //click Forward icon on toolbar
	   FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zToolbarPressButton(Button.B_FORWARD);

	   Assert.assertTrue(formMail.zHasAttachment(contactItem.fileAs + ".vcf"), "Verify there is  attachment named: " + contactItem.fileAs );

	   //click Cancel
	   DialogWarning dialogWarning= (DialogWarning) formMail.zToolbarPressButton(Button.B_CANCEL);

	   //close the dialog
	   dialogWarning.zClickButton(Button.B_NO);
	}

	  @Test(	description = "Forward a contact by click Forward on the context menu",
			groups = { "functional" })
	public void ClickForwardOnContextmenu() throws HarnessException {
		  // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
        //click Forward icon on context menu
        FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_FORWARD, contactItem.fileAs);        
                
        Assert.assertTrue(formMail.zHasAttachment(contactItem.fileAs + ".vcf"), "Verify there is  attachment named: " + contactItem.fileAs );
        
        //TODO: verify attachment file content

        //click Cancel
        DialogWarning dialogWarning= (DialogWarning) formMail.zToolbarPressButton(Button.B_CANCEL);
        
        //close the dialog
        dialogWarning.zClickButton(Button.B_NO);
	}

	@Test(   description = "Forward a local Folders' contact by click Forward on the toolbar",
	      groups = { "functional" })
   public void LocalInDisplayViewClickForwardOnToolbar() throws HarnessException {

	   // Create a contact via Soap then select
	   ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(app, Action.A_LEFTCLICK);

	   //click Forward icon on toolbar
	   FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zToolbarPressButton(Button.B_FORWARD);

	   Assert.assertTrue(formMail.zHasAttachment(contactItem.fileAs + ".vcf"), "Verify there is  attachment named: " + contactItem.fileAs );

	   //TODO: verify attachment file content

	   //click Cancel
	   DialogWarning dialogWarning= (DialogWarning) formMail.zToolbarPressButton(Button.B_CANCEL);

	   //close the dialog
	   dialogWarning.zClickButton(Button.B_NO);
	}

	@Test(   description = "Forward an editing Local Folders contact by click Forward on the toolbar",
	      groups = { "functional" })
   public void LocalInEditViewClickForwardOnToolbar() throws HarnessException {

	   // Create a contact via Soap then select
	   ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(app, Action.A_LEFTCLICK);

	   // click Edit button
	   app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);

	   //click Forward icon on toolbar
	   FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zToolbarPressButton(Button.B_FORWARD);

	   Assert.assertTrue(formMail.zHasAttachment(contactItem.fileAs + ".vcf"), "Verify there is  attachment named: " + contactItem.fileAs );

	   //click Cancel
	   DialogWarning dialogWarning= (DialogWarning) formMail.zToolbarPressButton(Button.B_CANCEL);

	   //close the dialog
	   dialogWarning.zClickButton(Button.B_NO);
	}

	@Test( description = "Forward a Local Folders contact by click Forward on the context menu",
	      groups = { "functional" })
	public void LocalClickForwardOnContextmenu() throws HarnessException {
	   // Create a contact via Soap then select
	   ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(app, Action.A_LEFTCLICK);

	   //click Forward icon on context menu
	   FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_FORWARD, contactItem.fileAs);        

	   Assert.assertTrue(formMail.zHasAttachment(contactItem.fileAs + ".vcf"), "Verify there is  attachment named: " + contactItem.fileAs );

	   //TODO: verify attachment file content

	   //click Cancel
	   DialogWarning dialogWarning= (DialogWarning) formMail.zToolbarPressButton(Button.B_CANCEL);

	   //close the dialog
	   dialogWarning.zClickButton(Button.B_NO);
	}

}

