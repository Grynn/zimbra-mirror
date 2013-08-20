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
package com.zimbra.qa.selenium.projects.admin.tests.domains;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DomainItem;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateDomainAlias;


public class CreateDomainAlias extends AdminCommonTest {
	
	public CreateDomainAlias() {
		logger.info("New " + CreateDomainAlias.class.getCanonicalName());
		
		// All tests start at the "Alias" page
		super.startingPage=app.zPageManageDomains;
	}
	
	
	/**
	 * Testcase : Create a basic domain alias
	 * Steps :
	 * 1. Create a domain alias from GUI i.e. New -> Alias.
	 * 2. Verify domain alias is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a domain alias",
			groups = { "sanity" })
			public void CreateAlias_01() throws HarnessException {

		// Create a new account in the Admin Console
		DomainItem domainalias = new DomainItem();		

		// Click "New"
		WizardCreateDomainAlias wizard = 
			(WizardCreateDomainAlias)app.zPageManageDomains.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.O_ADD_DOMAIN_ALIAS);
		
		// Fill out the wizard	
		wizard.zCompleteWizard(domainalias);

		// Verify the domain exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
			+	"<domain by='name'>" + domainalias.getName() + "</domain>"
			+	"</GetDomainRequest>");


		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDomainResponse/admin:domain", 1);
		ZAssert.assertNotNull(response, "Verify the domain is created successfully");
	}

}
