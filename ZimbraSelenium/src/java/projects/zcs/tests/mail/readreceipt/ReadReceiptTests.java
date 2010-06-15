package projects.zcs.tests.mail.readreceipt;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class ReadReceiptTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("neverSendReadReceipt")
				|| test.equals("alwaysSendReadReceipt")
				|| test.equals("askMeForReadReceipt")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5), "" } };
		} else if (test.equals("unwantedReadReceiptDlgWhileMarkRead_Bug41499")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(1), getLocalizedData(5), "" } };
		} else {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5), "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		page.zMailApp.zNavigateToMailApp();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void neverSendReadReceipt(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefMailSendReadReceipts", "never");

		zKillBrowsers();
		SelNGBase.selfAccountName = to;
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zComposeView.zNavigateToMailCompose();
		obj.zButtonMenu.zClick(page.zComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(page.zComposeView.zRequestReadReceiptMenuItem);
		page.zComposeView.zEnterComposeValues(currentloggedinuser, cc, bcc,
				subject, body, attachments);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);

		zKillBrowsers();
		SelNGBase.selfAccountName = currentloggedinuser;
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(3000);
		obj.zDialog.zNotExists(localize(locator.warningMsg));

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void alwaysSendReadReceipt(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefMailSendReadReceipts", "always");

		zKillBrowsers();
		SelNGBase.selfAccountName = to;
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zComposeView.zNavigateToMailCompose();
		obj.zButtonMenu.zClick(page.zComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(page.zComposeView.zRequestReadReceiptMenuItem);
		page.zComposeView.zEnterComposeValues(currentloggedinuser, cc, bcc,
				subject, body, attachments);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);

		zKillBrowsers();
		SelNGBase.selfAccountName = currentloggedinuser;
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		assertReport(localize(locator.readReceiptSent), obj.zToastAlertMessage
				.zGetMsg(), "Verifying toast message for sending read receipt");

		zKillBrowsers();
		SelNGBase.selfAccountName = to;
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp("Read-Receipt: " + subject);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void askMeForReadReceiptAndBug36344(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefMailSendReadReceipts", "prompt");

		zKillBrowsers();
		SelNGBase.selfAccountName = to;
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zComposeView.zNavigateToMailCompose();
		obj.zButtonMenu.zClick(page.zComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(page.zComposeView.zRequestReadReceiptMenuItem);
		page.zComposeView.zEnterComposeValues(currentloggedinuser, cc, bcc,
				subject, body, attachments);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zDialog.zNotExists(localize(locator.warningMsg));

		zKillBrowsers();
		SelNGBase.selfAccountName = currentloggedinuser;
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		zWaitTillObjectExist("dialog", localize(locator.warningMsg));
		if (!config.getString("browser").equals("IE")) {
			assertReport(localize(locator.readReceiptSend).replaceAll("<br>",
					""), obj.zDialog.zGetMessage(localize(locator.warningMsg)),
					"Verifying dialog text for notifying read receipt");
		}
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		assertReport(localize(locator.readReceiptSent), obj.zToastAlertMessage
				.zGetMsg(), "Verifying toast message for sending read receipt");

		zKillBrowsers();
		SelNGBase.selfAccountName = to;
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp("Read-Receipt: " + subject);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void unwantedReadReceiptDlgWhileMarkRead_Bug41499(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Preferences");
		zGoToPreferences("Mail");
		obj.zRadioBtn.zClick(localize(locator.messageReadNone));
		obj.zRadioBtn.zClick(localize(locator.readReceiptAsk));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(1000);
		zGoToApplication("Mail");
		to = SelNGBase.selfAccountName;
		String[] recipients = { SelNGBase.selfAccountName };
		ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients, cc,
				subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(localize(locator.markAsRead));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.warningMsg));
		obj.zDialog.zNotExists(localize(locator.infoMsg));

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}