package com.zimbra.qa.selenium.projects.ajax.tests.conversation.performance;

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


public class ZmMailItem extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public ZmMailItem() throws HarnessException {
		logger.info("New "+ ZmMailItem.class.getCanonicalName());
		
		super.startingPage = app.zPageMail;

		
		super.startingAccountPreferences = new HashMap<String, String>() {{
					put("zimbraPrefGroupMailBy", "conversation");
				    put("zimbraPrefMessageViewHtmlPreferred", "FALSE");
				}};


	}
	
	
	@Test(	description = "Measure the performance for conversation view, preview pane, text message, initial load",
			groups = { "performance" })
	public void ZmMailItem_01() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/conversation02";
		String subject = "Conversation13155016716714";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailItem, "Load preview pane, text message, initial load");

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		PerfMetrics.waitTimestamp(token);

	}


}
