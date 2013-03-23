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
package com.zimbra.qa.selenium.projects.admin.tests.domains;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DomainItem;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateDomain;

public class CreateDomain extends AdminCommonTest {
	
	
	public CreateDomain() {
		logger.info("New " + CreateDomain.class.getName());
		
		super.startingPage=app.zPageManageDomains;
	}
	
	/**
	 * Testcase : Create a simple domain
	 * Steps :
	 * 1. Create a domain from GUI
	 * 2. Verify domain is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a simple domain",
			groups = { "sanity" })
			public void CreateDomain_01() throws HarnessException {

		// Create a new domain in the Admin Console
		DomainItem domain = new DomainItem();



		// Click "New" -> "Domain"
		WizardCreateDomain wizard = 
			(WizardCreateDomain)app.zPageManageDomains.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_NEW);

		// Fill out the wizard and click Finish
		wizard.zCompleteWizard(domain);

		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
			+	"<domain by='name'>" + domain.getName() + "</domain>"
			+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain", 1);
		ZAssert.assertNotNull(response, "Verify the domain is created successfully");
	}
}
