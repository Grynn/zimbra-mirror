package com.zimbra.qa.selenium.projects.ajax.tests.mail.performance;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class ZmMailItemHTML extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public ZmMailItemHTML() throws HarnessException {
		logger.info("New "+ ZmMailItemHTML.class.getCanonicalName());
		
		super.startingPage = app.zPageMail;

		
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				    put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
				}};


	}
	
	
	@Test(	description = "Measure the performance for preview pane, html message, initial load",
			groups = { "performance" })
	public void ZmMailItem_01() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email02/mime01.txt";
		String subject = "Subject13155016716713";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailItem, "Load preview pane, html message, initial load");

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		PerfMetrics.waitTimestamp(token);

	}

	@Test(	description = "Measure the performance for preview pane, html message, 1 message",
			groups = { "performance" })
	public void ZmMailItem_02() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email02/mime01.txt";
		String subject = "Subject13155016716713";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailItem, "Load preview pane, html message, 1 message");

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		PerfMetrics.waitTimestamp(token);

	}



}
