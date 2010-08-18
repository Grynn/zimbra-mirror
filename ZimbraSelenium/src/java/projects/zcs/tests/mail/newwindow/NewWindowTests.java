package projects.zcs.tests.mail.newwindow;

import org.testng.Assert;
import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class NewWindowTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "lmtpDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("closeNewWindow")
				|| test.equals("deleteMsgFromNewWindow")
				|| test.equals("replyMsgFromNewWindow")
				|| test.equals("forwardMsgFromNewWindow")
				|| test.equals("tagUntagMsgFromNewWindow")
				|| test.equals("networkServiceErrorInNewWindow_Bug41205")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), "ccuser@testdomain.com",
					"bccuser@testdomain.com", getOnlyEnglishAlphabetCharAndNumber(), getOnlyEnglishAlphabetCharAndNumber(), "" } };
		} else if (test
				.equals("cancelingComposeInNewWindowSavesDraft_Bug43560")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), ProvZCS.getRandomAccount(),
					"bccuser@testdomain.com",
					"subject_cancelingComposeInNewWindowSavesDraft_Bug43560",
					"body_cancelingComposeInNewWindowSavesDraft_Bug43560", "" } };
		} else if (test.equals("cancelingComposeInNewWindowAndSavingDraft")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), ProvZCS.getRandomAccount(),
					"bccuser@testdomain.com",
					"subject_cancelingComposeInNewWindowSavingDraft",
					"body_cancelingComposeInNewWindowSavingDraft", "" } };
		} else if (test.equals("replyAllMsgFromNewWindow")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), ProvZCS.getRandomAccount(),
					"bccuser@testdomain.com",
					"subject_replyAllMsgFromNewWindow",
					"body_replyAllMsgFromNewWindow", "" } };
		} else {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					"commonbody", "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
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
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void closeNewWindow(String from, String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteMsgFromNewWindow(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(10000);
		SelNGBase.selenium.get().selectWindow("_blank");
		
		obj.zButton.zClick(page.zMailApp.zDeleteIconBtn_newWindow);
		Thread.sleep(8000);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyMsgFromNewWindow(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn_newWindow);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Re: " + subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyAllMsgFromNewWindow(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn_newWindow);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zExists("Re: " + subject);

		resetSession();
		SelNGBase.selfAccountName.set(cc);
		page.zLoginpage.zLoginToZimbraAjax(cc);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Re: " + subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardMsgFromNewWindow(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn_newWindow);
		Thread.sleep(1500);
		obj.zEditField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName.get());
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Fwd: " + subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tagUntagMsgFromNewWindow(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		page.zMailApp.zCreateTag("testTag");
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(1500);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zTagIconBtn_newWindow);
		obj.zMenuItem.zClick("testTag");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zMessageItem.zVerifyIsTagged(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(1500);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zTagIconBtn_newWindow);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zMessageItem.zVerifyIsNotTagged(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void networkServiceErrorInNewWindow_Bug41205(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		for (int i = 0; i <= 10; i++) {
			String dlgExists = obj.zDialog
					.zExistsDontWait(localize(locator.zimbraTitle));
			if (dlgExists.equals("true")) {
				Assert
						.assertFalse(
								localize(locator.errorService)
										.indexOf(
												obj.zDialog
														.zGetMessage(localize(locator.zimbraTitle))) >= 0,
								"Bug 41205 - 'system failure: Caught null' while opening message in new window");
				obj.zButton.zClickInDlgByName(localize(locator.ok),
						localize(locator.zimbraTitle));
			} else {
				Thread.sleep(1000);
			}
		}
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cancelingComposeInNewWindowSavesDraft_Bug43560(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyBtn);
		Thread.sleep(1000);
		obj.zButton.zClick("id=zb__COMPOSE1__DETACH_COMPOSE_left_icon");
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zEditor.zType("Modifying body");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		Thread.sleep(1000);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
		Thread.sleep(1000);
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject),
				"Verifying drafted message");

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cancelingComposeInNewWindowAndSavingDraft(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyBtn);
		Thread.sleep(1000);
		obj.zButton.zClick("id=zb__COMPOSE1__DETACH_COMPOSE_left_icon");
		Thread.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zEditor.zType("Modifying body");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		Thread.sleep(1000);
		SelNGBase.selenium.get().selectWindow(null);
		obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
		Thread.sleep(1000);
		obj.zMessageItem.zExists(subject);

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