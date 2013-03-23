/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;




import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class ForwardContact extends AjaxCommonTest  {
	public ForwardContact() {
		logger.info("New "+ ForwardContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Bugs(ids = "77708")
	@Test(	description = "Forward a contact by click Forward on the toolbar",
			groups = { "deprecated" })
	public void InDisplayViewClickForwardOnToolbar() throws HarnessException {
		
		
		//-- Data
	
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// Mail subject
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI
	
		// Refresh
		app.zPageAddressbook.zRefresh();
	
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

        // Forward
        FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zToolbarPressButton(Button.B_FORWARD);
                
        //wait for attachment link present
        for (int i=0; (i<20) && !app.zPageAddressbook.sIsElementPresent("css=div[id$=_attachments_div] div[class='ImgAttachment']") ; i++ , SleepUtil.sleepVerySmall());
        	
        // since the contact.fileAs length probably large, it is usually trim in the middle and replace with ...
        ZAssert.assertTrue( formMail.zHasAttachment("vcf"), "Verify the VCF attachment is there");

        formMail.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
        formMail.zFillField(Field.Subject, subject);
        formMail.zSubmit();
        
        
        
        // Verification
        ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
        String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
        
        
        /*
        <mp part="TEXT" ct="multipart/mixed">
          <mp body="1" s="0" part="1" ct="text/plain">
            <content/>
          </mp>
          <mp s="250" filename="lastname, firstname.vcf" part="2" ct="text/directory" cd="attachment"/>
        </mp>
         */

        ZimbraAccount.AccountA().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
        
        // Make sure we have a "mixed" content
        ZimbraAccount.AccountA().soapSelectNode("//mail:mp[@ct='multipart/mixed']", 1);
        
        // Make sure ct = text/directory 
        String ct = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@cd='attachment']", "ct");
        ZAssert.assertEquals(ct, "text/directory", "Make sure ct = text/directory");
        
        // Make sure filename contains .vcf
        String filename = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@cd='attachment']", "filename");
        ZAssert.assertStringContains(filename, ".vcf", "Make sure filename contains .vcf");

   	}

	@Test(	description = "Forward an editing contact by click Forward on the toolbar",
			groups = { "deprecated" })
	public void InEditViewClickForwardOnToolbar() throws HarnessException {
		
		
		//-- Data
	
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// Mail subject
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI
	
		// Refresh
		app.zPageAddressbook.zRefresh();
	
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

        // Edit
		app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
		
        // Forward
        FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zToolbarPressButton(Button.B_FORWARD);
                
        //wait for attachment link present
        for (int i=0; (i<20) && !app.zPageAddressbook.sIsElementPresent("css=div[id$=_attachments_div] div[class='ImgAttachment']") ; i++ , SleepUtil.sleepVerySmall());
        	
        // since the contact.fileAs length probably large, it is usually trim in the middle and replace with ...
        ZAssert.assertTrue( formMail.zHasAttachment("vcf"), "Verify the VCF attachment is there");

        formMail.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
        formMail.zFillField(Field.Subject, subject);
        formMail.zSubmit();
        
        
        
        // Verification
        ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
        String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
        
        
        /*
        <mp part="TEXT" ct="multipart/mixed">
          <mp body="1" s="0" part="1" ct="text/plain">
            <content/>
          </mp>
          <mp s="250" filename="lastname, firstname.vcf" part="2" ct="text/directory" cd="attachment"/>
        </mp>
         */

        ZimbraAccount.AccountA().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
        
        // Make sure we have a "mixed" content
        ZimbraAccount.AccountA().soapSelectNode("//mail:mp[@ct='multipart/mixed']", 1);
        
        // Make sure ct = text/directory 
        String ct = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@cd='attachment']", "ct");
        ZAssert.assertEquals(ct, "text/directory", "Make sure ct = text/directory");
        
        // Make sure filename contains .vcf
        String filename = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@cd='attachment']", "filename");
        ZAssert.assertStringContains(filename, ".vcf", "Make sure filename contains .vcf");

			
   	}

	@Test(	description = "Forward a contact by click Forward on the context menu",
			groups = { "smoke" })
	public void ClickForwardOnContextmenu() throws HarnessException {
		
		
		//-- Data
	
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// Mail subject
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI
	
		// Refresh
		app.zPageAddressbook.zRefresh();
	
        // Right Click -> Forward
        FormMailNew formMail = (FormMailNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_FORWARD, contact.fileAs);        
                
        //wait for attachment link present
        for (int i=0; (i<20) && !app.zPageAddressbook.sIsElementPresent("css=div[id$=_attachments_div] div[class='ImgAttachment']") ; i++ , SleepUtil.sleepVerySmall());
        	
        // since the contact.fileAs length probably large, it is usually trim in the middle and replace with ...
        ZAssert.assertTrue( formMail.zHasAttachment("vcf"), "Verify the VCF attachment is there");

        formMail.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
        formMail.zFillField(Field.Subject, subject);
        formMail.zSubmit();
        
        
        
        // Verification
        ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
        String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
        
        
        /*
        <mp part="TEXT" ct="multipart/mixed">
          <mp body="1" s="0" part="1" ct="text/plain">
            <content/>
          </mp>
          <mp s="250" filename="lastname, firstname.vcf" part="2" ct="text/directory" cd="attachment"/>
        </mp>
         */

        ZimbraAccount.AccountA().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
        
        // Make sure we have a "mixed" content
        ZimbraAccount.AccountA().soapSelectNode("//mail:mp[@ct='multipart/mixed']", 1);
        
        // Make sure ct = text/directory 
        String ct = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@cd='attachment']", "ct");
        ZAssert.assertEquals(ct, "text/directory", "Make sure ct = text/directory");
        
        // Make sure filename contains .vcf
        String filename = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@cd='attachment']", "filename");
        ZAssert.assertStringContains(filename, ".vcf", "Make sure filename contains .vcf");

	}

}

