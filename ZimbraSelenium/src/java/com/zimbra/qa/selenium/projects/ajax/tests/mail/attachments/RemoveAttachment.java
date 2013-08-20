/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class RemoveAttachment extends PrefGroupMailByMessageTest {

	
	public RemoveAttachment() throws HarnessException {
		logger.info("New "+ RemoveAttachment.class.getCanonicalName());
		
	}
	
	
	@Test(	description = "Remove an attachment from a mail",
			groups = { "functional" })
	public void RemoveAttachment_01() throws HarnessException {

		//-- Data Setup
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
		final String subject = "subject151615738";
		final String attachmentname = "file.txt";
		ZimbraAccount account = app.zGetActiveAccount();
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Double check that there is an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = account.soapSelectValue("//mail:m", "id");
		
		account.soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		Element[] nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message has the attachment");


		//-- GUI actions
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		// Click remove
		DialogWarning dialog = (DialogWarning)display.zListAttachmentItem(Button.B_REMOVE, item);
		dialog.zClickButton(Button.B_YES);

		
		//-- Verification

		// Verify the message no longer has an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		id = account.soapSelectValue("//mail:m", "id");
		
		
		try{
			
			int i = 0;
			do {
				SleepUtil.sleepSmall();

		    	account.soapSend(
		    			  "<GetMsgRequest xmlns='urn:zimbraMail' >"
		    			+   "<m id='"+ id +"'/>"
		    			+ "</GetMsgRequest>");
		    	nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");

			} while ( (i++ < 10) && (nodes.length > 0) );
			
		    
		}catch(Exception ex){
		    logger.error(ex);
		}
		
		ZAssert.assertEquals(nodes.length, 0, "Verify the message no longer has the attachment");
		

	}

	@Test(	description = "Remove all attachments (2 attachments) from a mail",
			groups = { "functional" })
	public void RemoveAttachment_02() throws HarnessException {

		//-- Data Setup
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email06/mime.txt";
		final String subject = "subject135219672356274";
		ZimbraAccount account = app.zGetActiveAccount();
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Double check that there is an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = account.soapSelectValue("//mail:m", "id");
		
		account.soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		Element[] nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message has the attachment");


		//-- GUI actions
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		ZAssert.assertNotNull(display, "Verify the message shows");
		
		// Click remove
		DialogWarning dialog = (DialogWarning)display.zPressButton(Button.B_REMOVE_ALL);
		dialog.zClickButton(Button.B_YES);

		
		//-- Verification

		// Verify the message no longer has an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		id = account.soapSelectValue("//mail:m", "id");
		
		
		try{
			
			int i = 0;
			do {
				SleepUtil.sleepSmall();

		    	account.soapSend(
		    			  "<GetMsgRequest xmlns='urn:zimbraMail' >"
		    			+   "<m id='"+ id +"'/>"
		    			+ "</GetMsgRequest>");
		    	nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");

			} while ( (i++ < 10) && (nodes.length > 0) );
			
		    
		}catch(Exception ex){
		    logger.error(ex);
		}
		
		ZAssert.assertEquals(nodes.length, 0, "Verify the message no longer has the attachment");
		

	}

	@Bugs(	ids = "81565")
	@Test(	description = "Remove an attachment from a meeting invite",
			groups = { "functional" })
	public void RemoveAttachment_03() throws HarnessException {

		//-- Data Setup
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug81565/mime1.txt";
		final String subject = "Bug81565";
		final String attachmentname = "Capture.PNG";
		ZimbraAccount account = app.zGetActiveAccount();
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Double check that there is an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = account.soapSelectValue("//mail:m", "id");
		
		account.soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		Element[] nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message has the attachment");


		//-- GUI actions
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		// Click remove
		DialogWarning dialog = (DialogWarning)display.zListAttachmentItem(Button.B_REMOVE, item);
		dialog.zClickButton(Button.B_YES);

		
		//-- Verification

		// Verify the message no longer has an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		id = account.soapSelectValue("//mail:m", "id");
		
		
		try{
			
			int i = 0;
			do {
				SleepUtil.sleepSmall();

		    	account.soapSend(
		    			  "<GetMsgRequest xmlns='urn:zimbraMail' >"
		    			+   "<m id='"+ id +"'/>"
		    			+ "</GetMsgRequest>");
		    	nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");

			} while ( (i++ < 10) && (nodes.length > 0) );
			
		    
		}catch(Exception ex){
		    logger.error(ex);
		}
		
		ZAssert.assertEquals(nodes.length, 0, "Verify the message no longer has the attachment");
		

	}

	@Bugs(	ids = "81565")
	@Test(	description = "Remove all attachments (2 attachments) from a meeting invite",
			groups = { "functional" })
	public void RemoveAttachment_04() throws HarnessException {

		//-- Data Setup
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug81565/mime2.txt";
		final String subject = "Bug81565B";
		ZimbraAccount account = app.zGetActiveAccount();
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Double check that there is an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = account.soapSelectValue("//mail:m", "id");
		
		account.soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		Element[] nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message has the attachment");


		//-- GUI actions
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		ZAssert.assertNotNull(display, "Verify the message shows");
		
		// Click remove
		DialogWarning dialog = (DialogWarning)display.zPressButton(Button.B_REMOVE_ALL);
		dialog.zClickButton(Button.B_YES);

		
		//-- Verification

		// Verify the message no longer has an attachment
		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		id = account.soapSelectValue("//mail:m", "id");
		
		
		try{
			
			int i = 0;
			do {
				SleepUtil.sleepSmall();

		    	account.soapSend(
		    			  "<GetMsgRequest xmlns='urn:zimbraMail' >"
		    			+   "<m id='"+ id +"'/>"
		    			+ "</GetMsgRequest>");
		    	nodes = account.soapSelectNodes("//mail:mp[@cd='attachment']");

			} while ( (i++ < 10) && (nodes.length > 0) );
			
		    
		}catch(Exception ex){
		    logger.error(ex);
		}
		
		ZAssert.assertEquals(nodes.length, 0, "Verify the message no longer has the attachment");
		

	}


}
