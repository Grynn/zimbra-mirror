package projects.zcs.tests.mail.compose.newwindow;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;

@SuppressWarnings("static-access")
public class ReplyAllMail extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("replyAllHtmlMail_NewWindow")
				|| test.equals("replyAllPlainTextMail_NewWindow")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(2), getLocalizedData(5), "" } };
		} else {
			return new Object[][] { { "" }, };
		}

	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.zLogin();
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * Test: Reply all to an email in html-mode and in new-window and verify if
	 * the mail-compose and verify that cc and bcc is empty,to is filled,
	 * subject has Re appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyAllHtmlMail_NewWindow(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefComposeFormat", "html");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(2500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		obj.zButton.zClick(MailApp.zReplyAllIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zDetachBtn_ComposedMessage);
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Reply All", "", "", "",
				"Re: " + subject, body, attachments);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test: Reply all to an email in plain text mode and in new-window and
	 * verify if the mail-compose and verify that cc and bcc is empty,to is
	 * filled, subject has Re appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyAllPlainTextMail_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefComposeFormat", "text");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(2500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		obj.zButton.zClick(MailApp.zReplyAllIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zDetachBtn_ComposedMessage);
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Reply All", "", "", "",
				"Re: " + subject, body, attachments);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));

		SelNGBase.needReset.set(false);
	}
}