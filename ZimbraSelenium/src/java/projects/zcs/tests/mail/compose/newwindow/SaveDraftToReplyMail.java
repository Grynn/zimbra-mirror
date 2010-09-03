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
import projects.zcs.ui.MailApp;

@SuppressWarnings("static-access")
public class SaveDraftToReplyMail extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("saveDraftToReplyHtmlMail_NewWindow")
				|| test.equals("saveDraftToReplyPlainTextMail_NewWindow")) {
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
	 * Test: Save draft to replied email in html-mode and in new-window and
	 * verify if the mail-compose and verify that cc and bcc is empty,to is
	 * filled, subject has Re appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void saveDraftToReplyHtmlMail_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
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
		obj.zButton.zClick(MailApp.zReplyIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zDetachBtn_ComposedMessage);
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zSaveDraftsBtn_newWindow);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCancelBtn_newWindow);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zFolder.zClick(page.zMailApp.zGetMailIconBtn);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));
		obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Edit Draft", "", "", "",
				"Re: " + subject, body, attachments);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test: Save draft to replied email in plain text mode and in new-window
	 * and verify if the mail-compose and verify that cc and bcc is empty,to is
	 * filled, subject has Re appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void saveDraftToReplyPlainTextMail_NewWindow(String to, String cc,
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
		obj.zButton.zClick(MailApp.zReplyIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zDetachBtn_ComposedMessage);
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zSaveDraftsBtn_newWindow);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCancelBtn_newWindow);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zFolder.zClick(page.zMailApp.zGetMailIconBtn);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));
		obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Edit Draft", "", "", "",
				"Re: " + subject, body, attachments);

		SelNGBase.needReset.set(false);
	}
}