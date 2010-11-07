package projects.ajax.tests.mail.bugs;

import java.io.IOException;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Actions;
import projects.ajax.ui.Buttons;
import projects.ajax.ui.DisplayMail;

import com.zimbra.cs.lmtpserver.LmtpProtocolException;

import framework.util.HarnessException;
import framework.util.LmtpUtil;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

public class Bug39246 extends AjaxCommonTest {

	public Bug39246() {
		logger.info("New "+ Bug39246.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Verify bug 39246",
			groups = { "sanity" })
	public void Bug39246_01() throws IOException, LmtpProtocolException, HarnessException  {
		
		final String subject = "Bug39246";
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/viewEntireMessage_Bug39246.txt";
		
		
		// Inject the example message
		LmtpUtil.injectFile(app.getActiveAccount().EmailAddress, mime);

		// Get Mail
		app.zPageMail.zToolbarPressButton(Buttons.B_GETMAIL);
		
		// Select the message
		app.zPageMail.zListItem(Actions.A_LEFTCLICK, subject);
		SleepUtil.sleepLong();	// Message is large, let it load

		// In the preview pane, click "View Entire Message"
		DisplayMail pane = app.zPageMail.zGetReadingPane();
		pane.zClickViewEntireMessage();
		SleepUtil.sleepLong();	// Message is large, let it load

		throw new HarnessException("Convert remaining code from LmtpInject.viewEntireMessage_Bug39246()");

		
	}

}
