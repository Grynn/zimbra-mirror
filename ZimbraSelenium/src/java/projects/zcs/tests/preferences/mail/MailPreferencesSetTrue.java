package projects.zcs.tests.preferences.mail;

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

import framework.core.*;
import framework.util.HarnessException;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class MailPreferencesSetTrue extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailPreferencesDataProvider")
	public Object[][] createData(Method method) throws ServiceException, HarnessException {
		String test = method.getName();

		if (test.equals("mailPrefDisplaySnippets")
				|| (test.equals("mailPrefDoubleClickOpensInNewWindow"))
				|| (test.equals("mailPrefNewMailNotification"))) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("mailPrefMarkAsRead")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"0" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"5" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"-1" } };
		} else if (test.equals("mailPrefInitialMailSearch")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"in:sent", "sent" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"in:drafts", "drafts" } };
		} else if (test.equals("mailPrefMailForwarding")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							Stafzmprov.getRandomAccount(), "TRUE" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							Stafzmprov.getRandomAccount(), "FALSE" } };

		} else if (test.equals("mailPrefOutOfOfficeReply")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"20050101000000Z", "20500101000000Z",
							getLocalizedData(3), "true" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"20050101000000Z", "20060101000000Z",
							getLocalizedData(2), "false" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"20050101000000Z",
							getTodaysDateZimbraFormat() + "000000Z",
							getLocalizedData(2), "false" } };

		} else {

			return new Object[][] { { "localize(locator.GAL)" } };
		}

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();

		String accountName = ClientSessionFactory.session().currentUserName();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefShowFragments", "TRUE");
		Stafzmprov.modifyAccount(accountName, "zimbraPrefOpenMailInNewWindow",
				"TRUE");

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
	public void mailPrefDisplaySnippets() throws Exception {

		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = ClientSessionFactory.session().currentUserName();

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		obj.zMessageItem.zExists(body);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefDoubleClickOpensInNewWindow() throws Exception {
		
		checkForSkipException("na", "SF", "39446", "New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");


		zLogin();

		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);
		String browserWindowTitle;

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = ClientSessionFactory.session().currentUserName();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");
		SleepUtil.sleep(2000);
		obj.zMessageItem.zDblClick(subject);
		SleepUtil.sleep(4000); // test continuously fails here
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		zWaitTillObjectExist("button", "id=zb__MSG1__CLOSE_left_icon");
		obj.zButton.zClick("id=zb__MSG1__CLOSE_left_icon");
		ClientSessionFactory.session().selenium().selectWindow(null);
		ClientSessionFactory.session().selenium().refresh();
		SleepUtil.sleep(3000);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefMarkAsRead(String subject, String body, String readTime)
			throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "na", "provzcs method 'zimbraPrefMarkMsgRead' method doesn't sets corresponding value in database");

		String accountName = ClientSessionFactory.session().currentUserName();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefMarkMsgRead", readTime);

		// selenium.refresh();
		zReloginToAjax();

		SleepUtil.sleep(500);

		page.zComposeView.zNavigateToMailCompose();

		SleepUtil.sleep(500);

		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		obj.zButton.zClick(page.zMailApp.zMailViewIconBtn);

		obj.zMenuItem.zClick(localize(locator.byMessage));

		SleepUtil.sleep(500);

		obj.zMessageItem.zClick(subject);

		if (readTime.equals("0")) {
			SleepUtil.sleep(500);
			obj.zMessageItem.zVerifyIsRead(subject);
		} else if (readTime.equals("-1")) {
			SleepUtil.sleep(1000);
			obj.zMessageItem.zVerifyIsUnRead(subject);
		} else {
			int i = Integer.parseInt(readTime);
			i = (i * 1000) + 1000;

			obj.zMessageItem.zVerifyIsUnRead(subject);
			SleepUtil.sleep(i);

			obj.zMessageItem.zVerifyIsRead(subject);
		}

		obj.zButton.zClick(page.zMailApp.zMailViewIconBtn);

		obj.zMenuItem.zClick(localize(locator.byConversation));

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefInitialMailSearch() throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);
		String accountName = ClientSessionFactory.session().currentUserName();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefMailInitialSearch",
				"in:sent");

		// selenium.refresh();
		zReloginToAjax();

		SleepUtil.sleep(500);

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zSendMailToSelfAndVerify(Stafzmprov.getRandomAccount(),
				"", "", subject, body, "");

		obj.zMessageItem.zExists(subject);

		Stafzmprov.modifyAccount(accountName, "zimbraPrefMailInitialSearch",
				"in:inbox");

		// selenium.refresh();
		zReloginToAjax();

		SleepUtil.sleep(500);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefMailForwarding(String subject, String body,
			String forwardingAddress, String donotKeepLocalCopy)
			throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = Stafzmprov.getRandomAccount();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefMailForwardingAddress",
				forwardingAddress);

		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefMailLocalDeliveryDisabled", donotKeepLocalCopy);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(accountName, "", "", subject,
				body, "");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);

		SleepUtil.sleep(500);

		resetSession();

		SleepUtil.sleep(500);

		
		page.zLoginpage.zLoginToZimbraAjax(accountName);

		if (donotKeepLocalCopy.equals("TRUE")) {
			obj.zMessageItem.zNotExists(subject);
		} else {
			// Verify "Delete invite on reply" preference
			MailApp.ClickCheckMailUntilMailShowsUp(subject);
		}

		resetSession();

		SleepUtil.sleep(500);

		
		page.zLoginpage.zLoginToZimbraAjax(forwardingAddress);

		MailApp
				.ClickCheckMailUntilMailShowsUp(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefNewMailNotification(String subject, String body)
			throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = Stafzmprov.getRandomAccount();
		accountName = accountName.toLowerCase();

		String notificationAddress = Stafzmprov.getRandomAccount();

		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefNewMailNotificationAddress", notificationAddress);
		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefNewMailNotificationEnabled", "TRUE");

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(accountName, "", "", subject,
				body, "");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);

		SleepUtil.sleep(500);

		resetSession();

		SleepUtil.sleep(500);

		
		page.zLoginpage.zLoginToZimbraAjax(notificationAddress);

		MailApp.ClickCheckMailUntilMailShowsUp("Postmaster");

		obj.zMessageItem.zClick("Postmaster");

		String browser = ZimbraSeleniumProperties.getStringProperty("browser");

		if (browser.equals("IE"))
			SleepUtil.sleep(1000);

		page.zMailApp.zVerifyMailContentContains(subject);
		page.zMailApp.zVerifyMailContentContains("New message received at");
		page.zMailApp.zVerifyMailContentContains(accountName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefOutOfOfficeReply(String subject, String body,
			String startDate, String endDate, String oOOContent,
			String oOOReceived) throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = Stafzmprov.getRandomAccount();
		accountName = accountName.toLowerCase();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefOutOfOfficeFromDate",
				startDate);
		Stafzmprov.modifyAccount(accountName, "zimbraPrefOutOfOfficeReply",
				oOOContent);
		Stafzmprov.modifyAccount(accountName, "zimbraPrefOutOfOfficeReplyEnabled",
				"TRUE");
		Stafzmprov.modifyAccount(accountName, "zimbraPrefOutOfOfficeUntilDate",
				endDate);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(accountName, "", "", subject,
				body, "");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);

		if (oOOReceived.equals("true")) {
			MailApp.ClickCheckMailUntilMailShowsUp(subject);

			obj.zMessageItem.zClick(subject);

			String browser = ZimbraSeleniumProperties.getStringProperty("browser");
			if (browser.equals("IE"))
				SleepUtil.sleep(1000);

			page.zMailApp.zVerifyMailContentContains(oOOContent);
		} else {

			SleepUtil.sleep(5000);

			obj.zButton.zClick(page.zMailApp.zGetMailIconBtn);

			obj.zMessageItem.zNotExists(subject);
		}

		SelNGBase.needReset.set(false);
	}

}
