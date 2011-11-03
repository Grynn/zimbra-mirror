package com.zimbra.qa.selenium.projects.ajax.tests.conversation.performance;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.PageLogin.Locators;



public class ZmMailApp extends AjaxCommonTest {
	
	public ZmMailApp() {
		logger.info("New "+ ZmMailApp.class.getCanonicalName());
		
		
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 7525760124523255182L;
		{
			put("zimbraPrefGroupMailBy", "conversation");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};
		
	}
	
	@Test(	description = "Measure the time to load the mail app, conversation view, initial load",
			groups = { "performance" })
	public void ZmMailApp_01() throws HarnessException {
		
		// Fill out the login page
		app.zPageLogin.zSetLoginName(ZimbraAccount.AccountZWC().EmailAddress);
		app.zPageLogin.zSetLoginPassword(ZimbraAccount.AccountZWC().Password);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailApp, "Load the mail app, conversation view, initial load");

		// Click the Login button
		app.zPageLogin.sClick(Locators.zBtnLogin);

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageMain.zWaitForActive();
		
		
	}

	@Test(	description = "Measure the time to load the mail app, conversation view, 1 conversation",
			groups = { "performance" })
	public void ZmMailApp_02() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email02/mime01.txt";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(mime));

		// Fill out the login page
		app.zPageLogin.zSetLoginName(ZimbraAccount.AccountZWC().EmailAddress);
		app.zPageLogin.zSetLoginPassword(ZimbraAccount.AccountZWC().Password);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailApp, "Load the mail app, conversation view, 1 conversations");

		// Click the Login button
		app.zPageLogin.sClick(Locators.zBtnLogin);

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageMain.zWaitForActive();
		
		
	}

	@Test(	description = "Measure the time to load the mail app, conversation view, 100 conversations",
			groups = { "performance" })
	public void ZmMailApp_03() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email03";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(mime));

		// Fill out the login page
		app.zPageLogin.zSetLoginName(ZimbraAccount.AccountZWC().EmailAddress);
		app.zPageLogin.zSetLoginPassword(ZimbraAccount.AccountZWC().Password);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailApp, "Load the mail app, conversation view, 100 conversations");

		// Click the Login button
		app.zPageLogin.sClick(Locators.zBtnLogin);

		PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageMain.zWaitForActive();
		
		
	}


}
