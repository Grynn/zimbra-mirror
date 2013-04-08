package com.zimbra.qa.selenium.projects.ajax.tests.mail.performance.compose;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.performance.PerfKey;
import com.zimbra.qa.selenium.framework.util.performance.PerfMetrics;
import com.zimbra.qa.selenium.framework.util.performance.PerfToken;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;

public class ZmMailAppReplyCompose extends AjaxCommonTest {

	public ZmMailAppReplyCompose() {
		logger.info("New " + ZmMailAppReplyCompose.class.getCanonicalName());

		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 7525760124523255182L;
			{
				put("zimbraPrefComposeFormat", "text");
			}
		};

	}

	@Test(description = "Measure the time to load reply-compose  window for simple message", groups = { "performance" })
	public void ZmMailAppReplyCompose_01() throws HarnessException {

		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email02/mime01.txt";
		String subject = "Subject13155016716713";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailAppCompose, "Load Reply-Compose window for simple conversation");

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		//Click Reply from tool bar
		app.zPageMail.zToolbarPressButton(Button.B_REPLY);

		PerfMetrics.waitTimestamp(token);

	}
	
	
	@Test(description = "Measure the time to load reply-compose  window for large conversation", groups = { "performance" })
	public void ZmMailAppReplyCompose_02() throws HarnessException {

		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/largeconversation_mime.txt";
		String subject = "RESOLVED BUGS";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailAppCompose, "Load Reply-Compose window for large conversation");
		
		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		//Click Reply from tool bar
		app.zPageMail.zToolbarPressButton(Button.B_REPLY);

		PerfMetrics.waitTimestamp(token);
	}

	@Test(description = "Measure the time to load reply-compose window for invite conversation", groups = { "performance" })
	public void ZmMailAppReplyCompose_03() throws HarnessException {

		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Invite_Message.txt";
		String subject = "Test Edit Reply";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailAppCompose, "Load Reply-Compose window for invite conversation");

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Click Accept -> Edit Reply , which will open a new reply compose
		@SuppressWarnings("unused")
		FormMailNew editReply = (FormMailNew)display.zPressButtonPulldown(Button.B_ACCEPT, Button.O_ACCEPT_EDIT_REPLY);
		
		PerfMetrics.waitTimestamp(token);
	}
	
}

