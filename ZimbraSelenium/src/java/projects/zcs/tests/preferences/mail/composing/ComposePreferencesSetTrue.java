package projects.zcs.tests.preferences.mail.composing;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.Selenium;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZInvite.ZRole;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ComposePreferencesSetTrue extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composePreferencesDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		if (test.equals("composePrefReplyFwdFormat")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"TRUE", "html" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"TRUE", "text" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"FALSE", "html" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"FALSE", "text" } };

		} else if (test.equals("composePrefReplyIncludeMsg")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBody" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "|",
							"includeBodyWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBodyAndHeadersWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeAsAttachment" } };

		} else if (test.equals("composeFwdIncludeMsg")) {

			return new Object[][] {

					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBody" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "|",
							"includeBodyWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBodyAndHeadersWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeAsAttachment" } };

		} else {

			return new Object[][] { { "test" } };
		}

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();

		// Setup

		String accountName = SelNGBase.selfAccountName.get();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefComposeFormat", "html");
		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefHtmlEditorDefaultFontFamily", "Times New Roman");

		Stafzmprov.modifyAccount(accountName, "zimbraPrefAutoSaveDraftInterval",
				"10");

		Stafzmprov.modifyAccount(accountName, "zimbraPrefSaveToSent", "TRUE");

		// selenium.refresh();
		super.zLogin();
		}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Imports a ics file and verifies that all the appointments are imported
	 * correctly
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefComposeAs() throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// zReloginToAjax();
		zLogin();

		page.zComposeView.zNavigateToMailCompose();

		obj.zButton.zExists("Times New Roman");

		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefOpensInNewWindow() throws Exception {

		String browserWindowTitle;

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		checkForSkipException("na", "SF", "39446", "New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");


		String accountName = SelNGBase.selfAccountName.get();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefComposeInNewWindow",
				"TRUE");

		zReloginToAjax();

		SleepUtil.sleep(1000);

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		SleepUtil.sleep(1500);
		SelNGBase.selenium.get().selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zSendBtn_newWindow);
		obj.zTextAreaField.zType(page.zComposeView.zToField, "test@test.com");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		// obj.zButton.zClickInDlgByName(localize(locator.no),
		// localize(locator.warningMsg));
		SelNGBase.selenium.get().selectWindow(null);

		Stafzmprov.modifyAccount(accountName, "zimbraPrefComposeInNewWindow",
				"FALSE");

		zReloginToAjax();

		SleepUtil.sleep(1000);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefAutoSaveDraft() throws Exception {

		checkForSkipException("fr", "na", "na", "Expected and actual value are same though throwing error (because of some UTF-8 character)");

		zReloginToAjax();

		String subject = getLocalizedData_NoSpecialChar();

		String body = getLocalizedData(3);

		int autoSaveDraftTime = 10;

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zEnterComposeValues("", "", "", subject, body, "");

		int i = (autoSaveDraftTime * 1000) + 1000;

		SleepUtil.sleep(i);

		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		obj.zDialog.zExists(localize(locator.warningMsg));

		obj.zDialog.zVerifyAlertMessage(localize(locator.warningMsg),
				localize(locator.askSaveAutosavedDraft));

		obj.zButton.zClickInDlg(localize(locator.yes));

		obj.zFolder.zClick(localize(locator.drafts));

		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefSaveToSent() throws Exception {

		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String recepient = Stafzmprov.getRandomAccount();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(recepient, "", "", subject, body,
				"");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);

		SleepUtil.sleep(500);

		obj.zFolder.zClick(localize(locator.sent));

		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "composePreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefReplyFwdFormat(String subject, String body,
			String replyFwdInOriginalFormat, String composeFormat)
			throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentAccount = SelNGBase.selfAccountName.get();
		String accountName = Stafzmprov.getRandomAccount();

		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefForwardReplyInOriginalFormat",
				replyFwdInOriginalFormat);
		Stafzmprov.modifyAccount(accountName, "zimbraPrefComposeFormat", "text");
		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefHtmlEditorDefaultFontFamily", "Times New Roman");

		Stafzmprov.modifyAccount(currentAccount, "zimbraPrefComposeFormat",
				composeFormat);

		// selenium.refresh();
		zReloginToAjax();

		SleepUtil.sleep(1000);

		page.zComposeView.zComposeAndSendMail(accountName, "", "", subject, body, "");

		SleepUtil.sleep(500);

		resetSession();

		SleepUtil.sleep(500);

		SelNGBase.selfAccountName.set(accountName);
		page.zLoginpage.zLoginToZimbraAjax(accountName);

		MailApp
				.ClickCheckMailUntilMailShowsUp(subject);

		SleepUtil.sleep(500);

		obj.zMessageItem.zClick(subject);

		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);

		SleepUtil.sleep(500);

		if (replyFwdInOriginalFormat.equals("TRUE")) {

			if (composeFormat.equals("html")) {
				obj.zButton.zExists("Times New Roman");
			} else {
				obj.zButton.zNotExists("Times New Roman");
			}
		} else {

			obj.zButton.zNotExists("Times New Roman");

		}

		String browser = ZimbraSeleniumProperties.getStringProperty("browser");

		if ((browser.equals("IE"))
				&& (composeFormat.equals("html") && replyFwdInOriginalFormat
						.equals("TRUE"))) {
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

			obj.zButton.zClickInDlg(localize(locator.no));

		}

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "composePreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefReplyIncludeMsg(String subject, String body,
			String replyPrefix, String includeMsg) throws Exception {

		String actualReplyMsg;
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "na", "UI change for prefix while reply/forward message");

		String accountName = SelNGBase.selfAccountName.get();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefForwardReplyPrefixChar",
				replyPrefix);
		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefReplyIncludeOriginalText", includeMsg);

		// Stafzmprov.modifyAccount(accountName, "zimbraPrefGroupMailBy",
		// "message");
		// selenium.refresh();
		zReloginToAjax();

		SleepUtil.sleep(500);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		MailApp
				.ClickCheckMailUntilMailShowsUp(subject);

		SleepUtil.sleep(500);

		obj.zMessageItem.zClick(subject);

		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);

		SleepUtil.sleep(500);

		actualReplyMsg = obj.zEditor.zGetInnerText("");

		SleepUtil.sleep(500);

		if (includeMsg.equals("includeNone")) {
			Assert
					.assertTrue(
							actualReplyMsg.equals("\n"),
							"reply body is not empty when 'zimbraPrefReplyIncludeOriginalText' set to includeNone. Reply body contains:__"
									+ actualReplyMsg);

		} else if (includeMsg.equals("includeBody")) {

			Assert
					.assertTrue(
							actualReplyMsg.indexOf(body) >= 0,
							"reply body is not present when 'zimbraPrefReplyIncludeOriginalText' is set to includeBody");

		} else if (includeMsg.equals("includeBodyWithPrefix")) {

			Assert
					.assertTrue(
							actualReplyMsg.indexOf(body) >= 0,
							"reply body is not present when 'zimbraPrefReplyIncludeOriginalText' is set to includeBodyWithPrefix");

			Assert
					.assertTrue(
							actualReplyMsg.indexOf(replyPrefix) >= 0,
							"reply prefix is not present when 'zimbraPrefReplyIncludeOriginalText' is set to includeBodyWithPrefix");

		} else if (includeMsg.equals("includeBodyAndHeadersWithPrefix")) {

			Assert
					.assertTrue(
							actualReplyMsg.indexOf(body) >= 0,
							"reply body is not present when 'zimbraPrefReplyIncludeOriginalText' is set to includeBodyAndHeadersWithPrefix");

			Assert
					.assertTrue(
							actualReplyMsg.indexOf(replyPrefix) >= 0,
							"reply prefix is not present when 'zimbraPrefReplyIncludeOriginalText' is set to includeBodyAndHeadersWithPrefix");

			Assert
					.assertTrue(
							actualReplyMsg.indexOf(accountName) >= 0,
							"message header is not present when 'zimbraPrefReplyIncludeOriginalText' is set to includeBodyAndHeadersWithPrefix");

		} else if (includeMsg.equals("includeAsAttachment")) {

			obj.zButton.zClick(page.zComposeView.zSendIconBtn);

			SleepUtil.sleep(500);

			obj.zFolder.zClick(localize(locator.sent));

			obj.zMessageItem.zClick(subject);

			SelNGBase.selenium.get().isElementPresent("link=" + subject);

		}

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "composePreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void composeFwdIncludeMsg(String subject, String body,
			String fwdPrefix, String includeMsg) throws Exception {

		String actualFwdMsg;
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		checkForSkipException("all", "na", "na", "UI change for prefix while reply/forward message");

		String accountName = SelNGBase.selfAccountName.get();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefForwardReplyPrefixChar",
				fwdPrefix);
		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefForwardIncludeOriginalText", includeMsg);

		// Stafzmprov.modifyAccount(accountName, "zimbraPrefGroupMailBy",
		// "message");
		// selenium.refresh();
		zReloginToAjax();

		SleepUtil.sleep(500);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		SleepUtil.sleep(500);

		MailApp
				.ClickCheckMailUntilMailShowsUp(subject);

		obj.zMessageItem.zClick(subject);

		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);

		SleepUtil.sleep(500);

		actualFwdMsg = obj.zEditor.zGetInnerText("");

		if (includeMsg.equals("includeBody")) {

			Assert
					.assertTrue(
							actualFwdMsg.indexOf(body) >= 0,
							"forward body is not present when 'zimbraPrefForwardIncludeOriginalText' is set to includeBody");

		} else if (includeMsg.equals("includeBodyWithPrefix")) {

			Assert
					.assertTrue(
							actualFwdMsg.indexOf(body) >= 0,
							"forward body is not present when 'zimbraPrefForwardIncludeOriginalText' is set to includeBodyWithPrefix");

			Assert
					.assertTrue(
							actualFwdMsg.indexOf(fwdPrefix) >= 0,
							"fwd prefix is not present when 'zimbraPrefForwardIncludeOriginalText' is set to includeBodyWithPrefix");

		} else if (includeMsg.equals("includeBodyAndHeadersWithPrefix")) {

			Assert
					.assertTrue(
							actualFwdMsg.indexOf(body) >= 0,
							"forward body is not present when 'zimbraPrefForwardIncludeOriginalText' is set to includeBodyAndHeadersWithPrefix");

			Assert
					.assertTrue(
							actualFwdMsg.indexOf(fwdPrefix) >= 0,
							"reply prefix is not present when 'zimbraPrefForwardIncludeOriginalText' is set to includeBodyAndHeadersWithPrefix");

			Assert
					.assertTrue(
							actualFwdMsg.indexOf(accountName) >= 0,
							"message header is not present when 'zimbraPrefForwardIncludeOriginalText' is set to includeBodyAndHeadersWithPrefix");

		} else if (includeMsg.equals("includeAsAttachment")) {

			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);

			obj.zFolder.zClick(localize(locator.drafts));

			obj.zMessageItem.zClick(subject);

			SelNGBase.selenium.get().isElementPresent("link=" + subject);

		}

		SelNGBase.needReset.set(false);
	}
}
