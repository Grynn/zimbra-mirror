/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.DomainItem;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;

public class GetDomain extends AdminCommonTest {
	public GetDomain() {
		logger.info("New" + GetDomain.class.getCanonicalName());
		
		//All tests starts from domain page
		this.startingPage=app.zPageManageDomains;
	}
	
	/**
	 * Testcase : Verify created domain is displayed in UI.
	 * Steps :
	 * 1. Create an domain using SOAP.
	 * 2. Verify domain is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created domain is present in the domain list view",
			groups = { "smoke" })
	public void GetDomain_01() throws HarnessException {

		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + domain.getName() + "</name>"
				+		"</CreateDomainRequest>");


		// Refresh the domain list
		app.zPageManageDomains.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Get the list of displayed domains
		List<DomainItem> accounts = app.zPageManageDomains.zListGetDomainList();
		ZAssert.assertNotNull(accounts, "Verify the account list is returned");
		
		DomainItem found = null;
		for (DomainItem a : accounts) {
			logger.info("Looking for domain "+ domain.getName() + " found: "+ a.getName());
			if ( domain.getName().equals(a.getName()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the domain is found");

	}

	/**
	 * Testcase : Verify get domain alias operation - Search list view.
	 * Steps :
	 * 1. Create domain alias using SOAP.
	 * 2. Search created domain alias.
	 * 3. Select the domain alias from gear box menu and select delete.
	 * 4. Verify domain alias is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete domain alias operation - Search list view",
			groups = { "functional" })
			public void GetDomain_02() throws HarnessException {
		
		
		String targetDomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + targetDomain + "</domain>"
				+	"</GetDomainRequest>");

		String targetDomainID=ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:GetDomainResponse/admin:domain", "id").toString();
		
		
		// Create a new domain alias in the Admin Console using SOAP
		DomainItem alias = new DomainItem();
		String domainAliasName=alias.getName();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+ "<name>"+domainAliasName+"</name>"
				+ "<a n='zimbraDomainType'>alias</a>"
				+ "<a n='zimbraDomainAliasTargetId'>"+targetDomainID+"</a>"
				+ "<a n='description'>"+"domain alias"+"</a>"
				+ "<a n='zimbraMailCatchAllAddress'>@"+domainAliasName+"</a>" 
				+ "<a  n='zimbraMailCatchAllForwardingAddress'>@"+targetDomain+"</a>"
				+ "</CreateDomainRequest>");
				
		// Refresh the domain list
		app.zPageManageDomains.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Get the list of displayed domains
		List<DomainItem> accounts = app.zPageManageDomains.zListGetDomainList();
		ZAssert.assertNotNull(accounts, "Verify the account list is returned");
		
		DomainItem found = null;
		for (DomainItem a : accounts) {
			logger.info("Looking for domain "+ domainAliasName + " found: "+ a.getName());
			if ( domainAliasName.equals(a.getName()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the domain is found");

	}


	
	/**
	 * Testcase : Verify created domain is displayed in UI.
	 * Steps :
	 * 1. Create an domain using SOAP.
	 * 2. Verify domain is present in the list.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify created domain is present in the domain list view",
			groups = { "smoke" })
	public void GetDomain_03() throws HarnessException {

		// Create a new domain in the Admin Console using SOAP
		DomainItem domain = new DomainItem();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
						"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + domain.getName() + "</name>"
				+		"</CreateDomainRequest>");

		
		
		// Enter the search string to find the domain
		app.zPageSearchResults.zAddSearchQuery(domain.getName());
		
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		
		
		// Get the list of displayed domains
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the account list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for domain "+ domain.getName() + " found: "+ a.getGEmailAddress());
			if ( domain.getName().equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the domain is found");

	}

	/**
	 * Testcase : Verify get domain alias operation - Search list view.
	 * Steps :
	 * 1. Create domain alias using SOAP.
	 * 2. Search created domain alias.
	 * 3. Select the domain alias from gear box menu and select delete.
	 * 4. Verify domain alias is deleted using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify delete domain alias operation - Search list view",
			groups = { "functional" })
			public void GetDomain_04() throws HarnessException {
		
		
		String targetDomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDomainRequest xmlns='urn:zimbraAdmin'>"
				+	"<domain by='name'>" + targetDomain + "</domain>"
				+	"</GetDomainRequest>");

		String targetDomainID=ZimbraAdminAccount.AdminConsoleAdmin().soapSelectValue("//admin:GetDomainResponse/admin:domain", "id").toString();
		
		
		// Create a new domain alias in the Admin Console using SOAP
		DomainItem alias = new DomainItem();
		String domainAliasName=alias.getName();
		
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"
				+ "<name>"+domainAliasName+"</name>"
				+ "<a n='zimbraDomainType'>alias</a>"
				+ "<a n='zimbraDomainAliasTargetId'>"+targetDomainID+"</a>"
				+ "<a n='description'>"+"domain alias"+"</a>"
				+ "<a n='zimbraMailCatchAllAddress'>@"+domainAliasName+"</a>" 
				+ "<a  n='zimbraMailCatchAllForwardingAddress'>@"+targetDomain+"</a>"
				+ "</CreateDomainRequest>");
				
		// Enter the search string to find the alias
		app.zPageSearchResults.zAddSearchQuery(domainAliasName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);

		
		
		// Get the list of displayed domains
		List<AccountItem> accounts = app.zPageSearchResults.zListGetAccounts();
		ZAssert.assertNotNull(accounts, "Verify the account list is returned");
		
		AccountItem found = null;
		for (AccountItem a : accounts) {
			logger.info("Looking for domain "+ domainAliasName + " found: "+ a.getGEmailAddress());
			if ( domainAliasName.equals(a.getGEmailAddress()) ) {
				found = a;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the domain is found");

	}

}
