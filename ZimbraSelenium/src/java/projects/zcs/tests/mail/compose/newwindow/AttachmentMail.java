package projects.zcs.tests.mail.compose.newwindow;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;

@SuppressWarnings("static-access")
public class AttachmentMail extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("sendPlainTextMailWithAttach_NewWindow")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(2), getLocalizedData(5),
					"testwordfile.doc" } };
		} else if (test.equals("sendHtmlMailWithAttach_NewWindow")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(2), getLocalizedData(5),
					"testexcelfile.xls" } };

		} else {
			return new Object[][] { { "" }, };
		}

	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * Test: Send an email(to self) in html-mode and in-newwindow in several
	 * ways(to-only,cc-only, attachment etc) and verify if the received mail has
	 * all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendHtmlMailWithAttach_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefComposeFormat", "html");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(2500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		obj.zButton.zClick(MailApp.zReplyIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zDetachBtn_ComposedMessage);
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zAddAttachmentBtn_newWindow);
		page.zComposeView.zAddAttachments(attachments, false);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		SelNGBase.selenium.get().selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Re: " + subject);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test: Send an email(to self) in plain text-mode and in-newwindow in
	 * several ways(to-only,cc-only, attachment etc) and verify if the received
	 * mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendPlainTextMailWithAttach_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefComposeFormat", "text");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(2500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}

}
