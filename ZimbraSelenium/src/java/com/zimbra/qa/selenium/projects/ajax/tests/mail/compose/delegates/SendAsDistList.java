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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.delegates;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class SendAsDistList extends PrefGroupMailByMessageTest {


	public SendAsDistList() {
		logger.info("New "+ SendAsDistList.class.getCanonicalName());
		
		
		

		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	
	@Test(	description = "Send As Distribution List",
			groups = { "smoke" })
	public void SendAsDistList_01() throws HarnessException {
		
		//-- Data Setup
		
		// Mail data
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		// The grantor
		ZimbraDistributionList list = new ZimbraDistributionList();
		list.provision();

		// Add a member
		list.addMember(ZimbraAccount.AccountA());
		
		// Grant send rights
		list.grantRight(app.zGetActiveAccount(), "sendAsDistList");

				
		// Login to load the rights
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();
		
		
		//-- GUI Steps
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, "body" + ZimbraSeleniumProperties.getUniqueString());
		mailform.zFillField(Field.From, list.EmailAddress);	
		mailform.zSubmit();
	

		
		//-- Data verification
		
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");

		ZimbraAccount.AccountA().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");


		// Verify From: grantor
		String from = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='f']", "a");
		ZAssert.assertEquals(from, list.EmailAddress, "Verify From: grantor");
		
		// Verify no headers contain active account
		Element[] nodes = ZimbraAccount.AccountA().soapSelectNodes("//mail:e");
		for (Element e : nodes) {
			String attr = e.getAttribute("a", null);
			if ( attr != null ) {
				ZAssert.assertStringDoesNotContain(
						attr, 
						app.zGetActiveAccount().EmailAddress, 
						"Verify no headers contain the active account email address");
			}
		}
	}

	
}
