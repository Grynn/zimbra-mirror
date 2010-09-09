package projects.html.tests.mail;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.SleepUtil;
import framework.util.RetryFailedTests;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

import projects.html.tests.CommonTest;


/**
 * Class file contains all miscellaneous compose related tests
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class MiscMailTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("verifyShowOriginal")) {
			return new Object[][] { { Stafzmprov.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData_NoSpecialChar(),
					"", "" } };
		} else {
			return new Object[][] { { Stafzmprov.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData_NoSpecialChar(),
					"", "" } };
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
	 * Compose an email in html-mode using compose button top toolbar and verify
	 * it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composeMailWithTopToolbar(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Mail"); /*
								 * in suite, some time it doesn't go to mail tab
								 */
		page.zComposeView.zNavigateToMailComposeTopToolBar();
		SleepUtil.sleepSmall();
		obj.zButton.zExists(page.zComposeView.zSendBtn);
		obj.zTextAreaField.zExists(page.zComposeView.zToField);
		obj.zTextAreaField.zExists(page.zComposeView.zCcField);
		ClientSessionFactory.session().selenium().click("link=" + localize(locator.showBcc));
		SleepUtil.sleepSmall();
		obj.zTextAreaField.zExists(page.zComposeView.zBccField);
		obj.zEditField.zExists(page.zComposeView.zSubjectField);
		// obj.zEditor.zExists(body);

		SelNGBase.needReset.set(false);
	}

	/*
	 * This test verifies Show-Hide Bcc link functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyShowHideBccLink(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		SleepUtil.sleepLong(); // required because composing takes some time
		ClientSessionFactory.session().selenium().click("link=" + localize(locator.showBcc));
		obj.zButton.zExists(page.zComposeView.zBccBtn);
		obj.zTextAreaField.zExists(page.zComposeView.zBccField);

		ClientSessionFactory.session().selenium().click("link=" + localize(locator.hideBcc));
		obj.zButton.zNotExists(page.zComposeView.zBccBtn);
		obj.zTextAreaField.zNotExists(page.zComposeView.zBccField);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);

		SelNGBase.needReset.set(false);
	}

	/*
	 * This test enters compose values and sets high priority and verifies
	 * received mail with high priority
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyMessageHighPriority(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, "", subject, body,
				attachments);
		SleepUtil.sleepSmall();
		obj.zHtmlMenu.zClick(page.zComposeView.zPriorityListBox,
				localize(locator.high));
		SleepUtil.sleepSmall();
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zVerifyHasHighPriority(subject);

		SelNGBase.needReset.set(false);
	}

	/*
	 * This test enters compose values and sets high priority and verifies
	 * received mail with low priority
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyMessageLowPriority(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, "", subject, body,
				attachments);
		SleepUtil.sleepSmall();
		obj.zHtmlMenu.zClick(page.zComposeView.zPriorityListBox,
				localize(locator.low));
		SleepUtil.sleepSmall();
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		SleepUtil.sleepSmall();
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zVerifyHasLowPriority(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Add receipients using top toolbar and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "test" }, retryAnalyzer = RetryFailedTests.class)
	public void addRecepientsWithTopToolbar(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, "", subject, "",
				attachments);
		obj.zButton.zClick(page.zComposeView.zAddReceipientsBtn);
		SleepUtil.sleepSmall();
		zWaitTillObjectExist("button", page.zComposeView.zAddReceipCancelBtn);
		obj.zButton.zExists(page.zComposeView.zAddReceipAddSelectedBtn);
		obj.zButton.zExists(page.zComposeView.zAddReceipCancelBtn);
		obj.zButton.zClick(page.zComposeView.zAddReceipDoneBtn);
		SleepUtil.sleepSmall();
		//obj.zButton.zClick(page.zComposeView.zCancelBtn);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		SleepUtil.sleepMedium();
		obj.zButton.zClick(page.zMailApp.zDraftFldr);
		SelNGBase.needReset.set(false);
	}

	/**
	 * verify show original functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyShowOriginal(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, "", subject, body,
				attachments);
		/*
		 * test fails while getting body, so waiting for long time and clicking
		 * 'Show Original' button 2 time (increasing time again)
		 */
		SleepUtil.sleepMedium();
		obj.zButton.zClick(page.zMailApp.zShowOrigIconBtn);
		SleepUtil.sleepSmall();
		obj.zButton.zClick(page.zMailApp.zShowOrigIconBtn);
		SleepUtil.sleepVeryLong();
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		SleepUtil.sleepMedium();
		String bodyValue = ClientSessionFactory.session().selenium().getBodyText();
		String[] columnToVerify = { "Return-Path:", "Received:", "Date:",
				"From:", "To:", "Cc:", "Message-ID:", "Subject:",
				"MIME-Version:", "Content-Type:", "Content-Transfer-Encoding:",
				"X-Originating-IP:", "X-Mailer:" };
		String[] dataToVerify = { to, cc, body, attachments };
		String[] localizedToVerify = { subject };
		for (int i = 0; i < columnToVerify.length - 1; i++) {
			assertReport(bodyValue, columnToVerify[i], "Verifying - "
					+ columnToVerify[i] + " in show original body");
		}
		for (int i = 0; i < dataToVerify.length - 1; i++) {
			assertReport(bodyValue, dataToVerify[i], "Verifying - "
					+ dataToVerify[i] + " in show original body");
		}
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			for (int i = 0; i < localizedToVerify.length - 1; i++) {
				assertReport(bodyValue, localizedToVerify[i], "Verifying - "
						+ localizedToVerify[i] + " in show original body");
			}
		}
		ClientSessionFactory.session().selenium().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	/**
	 * verify detach mail functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyOpenMailInNewWindow(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, "", subject, body,
				attachments);
		obj.zButton.zClick(page.zMailApp.zOpenInNewWindowIconBtn);
		SleepUtil.sleepLong(); // test fails here
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		String bodyValue = ClientSessionFactory.session().selenium().getBodyText();
		String[] columnToVerify = { localize(locator.from),
				localize(locator.to), localize(locator.cc),
				localize(locator.subject) };
		String[] dataToVerify = { to, cc, body, attachments };
		for (int i = 0; i < columnToVerify.length - 1; i++) {
			assertReport(bodyValue, columnToVerify[i], "Verifying - "
					+ columnToVerify[i] + " in show original body");
		}
		for (int i = 0; i < dataToVerify.length - 1; i++) {
			assertReport(bodyValue, dataToVerify[i], "Verifying - "
					+ dataToVerify[i] + " in show original body");
		}
		ClientSessionFactory.session().selenium().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Set 10 mail items per page and inject 12 mails, check next/previous page
	 * link appears and respected 2 mails shows in next page
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void nextPrevPageTestFor12Mail(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// set mail items per page to 10
		Stafzmprov.modifyAccount(ClientSessionFactory.session().currentUserName(),
				"zimbraPrefMailItemsPerPage", "10");
		resetSession();
		page.zLoginpage.zLoginToZimbraHTML(ClientSessionFactory.session().currentUserName());
		String[] subjectArray = { "subject1", "subject2", "subject3",
				"subject4", "subject5", "subject6", "subject7", "subject8",
				"subject9", "subject10", "subject11", "subject12" };
		for (int i = 0; i <= subjectArray.length - 1; i++) {
			page.zMailApp.zInjectMessage(from, ClientSessionFactory.session().currentUserName(), cc,
					bcc, subjectArray[i], subjectArray[i] + "body", "");
		}
		for (int i = 2; i <= 11; i++) {
			obj.zMessageItem.zExists(subjectArray[i]);
		}
		for (int i = 0; i <= 1; i++) {
			obj.zMessageItem.zNotExists(subjectArray[i]);
		}
		obj.zButton.zClick(page.zMailApp.zNextPageIconBtn);
		for (int i = 2; i <= 11; i++) {
			obj.zMessageItem.zNotExists(subjectArray[i]);
		}
		for (int i = 0; i <= 1; i++) {
			obj.zMessageItem.zExists(subjectArray[i]);
		}
		obj.zButton.zClick(page.zMailApp.zPreviousPageIconBtn);
		for (int i = 2; i <= 11; i++) {
			obj.zMessageItem.zExists(subjectArray[i]);
		}
		for (int i = 0; i <= 1; i++) {
			obj.zMessageItem.zNotExists(subjectArray[i]);
		}

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verify existing conversion using next/previous button
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyNextPrevPageConversionFor3Mail(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "na", "As we put reading pane feature in html client, so button remains disabled : Still not working.");


		String[] subjectArray = { "verifyNextPrevPage1", "verifyNextPrevPage2",
				"verifyNextPrevPage3" };
		for (int i = 0; i <= subjectArray.length - 1; i++) {
			page.zMailApp.zInjectMessage(from, ClientSessionFactory.session().currentUserName(), cc,
					bcc, subjectArray[i], subjectArray[i] + "body", "");
		}
		obj.zMessageItem.zClick(subjectArray[2]);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zExists(subjectArray[2]);
		obj.zMessageItem.zNotExists(subjectArray[1]);
		obj.zMessageItem.zNotExists(subjectArray[0]);
		obj.zButton.zClick(page.zMailApp.zNextPageIconBtn);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zNotExists(subjectArray[2]);
		obj.zMessageItem.zExists(subjectArray[1]);
		obj.zMessageItem.zNotExists(subjectArray[0]);
		obj.zButton.zClick(page.zMailApp.zNextPageIconBtn);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zNotExists(subjectArray[2]);
		obj.zMessageItem.zNotExists(subjectArray[1]);
		obj.zMessageItem.zExists(subjectArray[0]);

		obj.zButton.zClick(page.zMailApp.zPreviousPageIconBtn);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zNotExists(subjectArray[2]);
		obj.zMessageItem.zExists(subjectArray[1]);
		obj.zMessageItem.zNotExists(subjectArray[0]);
		obj.zButton.zClick(page.zMailApp.zPreviousPageIconBtn);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zClick(subjectArray[2]);
		obj.zMessageItem.zExists(subjectArray[2]);
		obj.zMessageItem.zNotExists(subjectArray[1]);
		obj.zMessageItem.zNotExists(subjectArray[0]);

		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zVerifyIsRead(subjectArray[0]);
		obj.zMessageItem.zVerifyIsRead(subjectArray[1]);
		obj.zMessageItem.zVerifyIsRead(subjectArray[2]);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Reply mail 2 time, apply flag to all and verify all mail has flagged
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rplyMail2TimeApplyFlagToAllAndVerify(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("ar", "na", "34114", "Searched keyword replaced by question marks");

		page.zMailApp.zInjectMessage(from, ClientSessionFactory.session().currentUserName(), cc, bcc,
				subject, subject + "body", "");
		SleepUtil.sleepSmall();
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zComposeView.zReplyBtn);
		SleepUtil.sleepSmall();
		obj.zTextAreaField.zType(page.zComposeView.zToField,
				ClientSessionFactory.session().currentUserName());
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		page.zMailApp.zClickCheckMailUntilMailShowsUp("Re: " + subject);
		obj.zMessageItem.zClick("Re: " + subject);
		obj.zButton.zClick(page.zComposeView.zReplyBtn);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		SleepUtil.sleepSmall();

		zGoToApplication("Preferences");
		obj.zTab.zClick(localize(locator.mail), "2");
		obj.zHtmlMenu.zClick("id=groupMailBy", localize(locator.message));
		obj.zButton.zClick("name=actionSave");

		zGoToApplication("Mail");
		obj.zCheckbox.zClick(page.zMailApp.zSelectAllMailChkBox);
		page.zMailApp.zMoreActions(localize(locator.actionAddFlag));

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
