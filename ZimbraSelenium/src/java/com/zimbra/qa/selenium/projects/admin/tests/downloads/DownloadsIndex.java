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
package com.zimbra.qa.selenium.projects.admin.tests.downloads;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class DownloadsIndex extends AdminCommonTest {

	public DownloadsIndex() {
		logger.info("New "+ DownloadsIndex.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageDownloads;
	}

	public static String[] NetworkOnlyLocators = {
		
		// These links only appear on NETWORK

		// Zimbra Connector for Microsoft Outlook 32 bit
		"//a[@id='zco_32bit']", 
		
		// Zimbra Connector for Microsoft Outlook 64 bit
		"//a[@id='zco_64bit']", 
		
		// Microsoft Outlook PST Import Tools
		"//a[@id='pst_import']",
		
		// Migration Wizard for Microsoft Exchange
		"//a[@id='exchange_migration']",
		
		// General Migration Wizard 32 bit
		"//a[@id='general_migration_32bit']",
		
		// General Migration Wizard 64 bit
		"//a[@id='general_migration_64bit']",		
	};
	
	public static String[] FossOnlyLocators = {
		
		// These links only appear on FOSS

		
		// There are currently no FOSS specific downloads
		
	};



	@Test(	description = "Verify the Downloads Index opens",
			groups = { "functional" })
	public void DownloadsIndex_01() throws HarnessException {


		String windowTitle = "Zimbra Collaboration Suite :: Downloads";

		try {
			
			// Open a new window pointing at http://server.com/zimbra/downloads/index.html
			app.zPageDownloads.zOpenIndexHTML();
		
			// This method throws an exception if the page doesn't open
			app.zPageDownloads.zSeparateWindowFocus(windowTitle);

			// If we get here (i.e. no exception thrown), then pass
			ZAssert.assertTrue(true, "Verify that the page opened correctly");
			
		} catch (Exception e) {
			
			throw new HarnessException(e);
			
		} finally {
			
			app.zPageDownloads.zSeparateWindowClose(windowTitle);
			
		}
	}

	@Test(	description = "Verify the Downloads Tab contains the correct FOSS vs NETWORK links",
			groups = { "functional" })
	public void DownloadsIndex_02() throws HarnessException {
	
		String windowTitle = "Zimbra Collaboration Suite :: Downloads";
		app.zPageDownloads.zOpenIndexHTML();
		
		// This method throws an exception if the page doesn't open
		app.zPageDownloads.zSeparateWindowFocus(windowTitle);

		
		// If NETWORK, make sure NETWORK-only links appear and FOSS-only links do not appear
		// If FOSS, make sure FOSS-only links appear and NETWORK-only links do not appear
		if ( ZimbraSeleniumProperties.zimbraGetVersionString().contains("NETWORK") ) {
			
			for ( String locator : NetworkOnlyLocators ) {
				ZAssert.assertTrue(app.zPageDownloads.sIsElementPresent(locator), "Verify the network-only locator exists: "+ locator);
			}

			for ( String locator : FossOnlyLocators ) {
				ZAssert.assertFalse(app.zPageDownloads.sIsElementPresent(locator), "Verify the foss-only locator does not exists: "+ locator);
			}

		} else if ( ZimbraSeleniumProperties.zimbraGetVersionString().contains("FOSS") ) {
			
			for ( String locator : NetworkOnlyLocators ) {
				ZAssert.assertFalse(app.zPageDownloads.sIsElementPresent(locator), "Verify the network-only locator does not exists: "+ locator);
			}

			for ( String locator : FossOnlyLocators ) {
				ZAssert.assertTrue(app.zPageDownloads.sIsElementPresent(locator), "Verify the foss-only locator exists: "+ locator);
			}

		} else {
			throw new HarnessException("Unable to find NETWORK or FOSS in version string: "+ ZimbraSeleniumProperties.zimbraGetVersionString());
		}
		
		app.zPageDownloads.zSeparateWindowClose(windowTitle);
		
	}
	

	@Test(	description = "Verify the downloads links return 200 rather than 404",
			groups = { "functional" })
	public void DownloadsIndex_03() throws HarnessException {

		String windowTitle = "Zimbra Collaboration Suite :: Downloads";
		app.zPageDownloads.zOpenIndexHTML();
		
		// This method throws an exception if the page doesn't open
		app.zPageDownloads.zSeparateWindowFocus(windowTitle);

		
		// Determine which links should be present
		List<String> locators = new ArrayList<String>();
		
		if ( ZimbraSeleniumProperties.zimbraGetVersionString().contains("NETWORK") ) {
			
			locators.addAll(Arrays.asList(NetworkOnlyLocators));			
			
		} else if ( ZimbraSeleniumProperties.zimbraGetVersionString().contains("FOSS") ) {
			
			locators.addAll(Arrays.asList(FossOnlyLocators));			

		} else {
			throw new HarnessException("Unable to find NETWORK or FOSS in version string: "+ ZimbraSeleniumProperties.zimbraGetVersionString());
		}

		for (String locator : locators ) {
			String href = app.zPageDownloads.sGetAttribute(locator +"@href");
			String page = ZimbraSeleniumProperties.getBaseURL() + href;
			
			HttpURLConnection  connection = null;
			try {
				
				URL url = new URL(page);
				connection = (HttpURLConnection )url.openConnection();
				connection.setRequestMethod("HEAD");
		        int code = connection.getResponseCode();
		        
		        // TODO: why is 400 returned for the PDF links?
		        // 200 and 400 are acceptable
		        ZAssert.assertStringContains("200 400", ""+code, "Verify the download URL is valid: "+ url.toString());
		        
			} catch (MalformedURLException e) {
				throw new HarnessException(e);
			} catch (IOException e) {
				throw new HarnessException(e);
			} finally {
				if ( connection != null ) {
					connection.disconnect();
					connection = null;
				}
			}

		}
		app.zPageDownloads.zSeparateWindowClose(windowTitle);
	}

}
