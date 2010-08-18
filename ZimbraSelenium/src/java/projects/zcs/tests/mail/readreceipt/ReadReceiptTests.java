package projects.zcs.tests.mail.readreceipt;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

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
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void neverSendReadReceipt(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName.get();
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefMailSendReadReceipts", "never");

		resetSession();
		SelNGBase.selfAccountName.set(to);
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zComposeView.zNavigateToMailCompose();
		obj.zButtonMenu.zClick(page.zComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(page.zComposeView.zRequestReadReceiptMenuItem);
		page.zComposeView.zEnterComposeValues(currentloggedinuser, cc, bcc,
				subject, body, attachments);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);

		resetSession();
		SelNGBase.selfAccountName.set(currentloggedinuser);
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(3000);
		obj.zDialog.zNotExists(localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void alwaysSendReadReceipt(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName.get();
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefMailSendReadReceipts", "always");

		resetSession();
		SelNGBase.selfAccountName.set(to);
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zComposeView.zNavigateToMailCompose();
		obj.zButtonMenu.zClick(page.zComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(page.zComposeView.zRequestReadReceiptMenuItem);
		page.zComposeView.zEnterComposeValues(currentloggedinuser, cc, bcc,
				subject, body, attachments);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);

		resetSession();
		SelNGBase.selfAccountName.set(currentloggedinuser);
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		assertReport(localize(locator.readReceiptSent), obj.zToastAlertMessage
				.zGetMsg(), "Verifying toast message for sending read receipt");

		resetSession();
		SelNGBase.selfAccountName.set(to);
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp("Read-Receipt: " + subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void askMeForReadReceiptAndBug36344(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName.get();
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefMailSendReadReceipts", "prompt");

		resetSession();
		SelNGBase.selfAccountName.set(to);
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

		resetSession();
		SelNGBase.selfAccountName.set(currentloggedinuser);
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		zWaitTillObjectExist("dialog", localize(locator.warningMsg));
		if (!ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			assertReport(localize(locator.readReceiptSend).replaceAll("<br>",
					""), obj.zDialog.zGetMessage(localize(locator.warningMsg)),
					"Verifying dialog text for notifying read receipt");
		}
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		assertReport(localize(locator.readReceiptSent), obj.zToastAlertMessage
				.zGetMsg(), "Verifying toast message for sending read receipt");

		resetSession();
		SelNGBase.selfAccountName.set(to);
		page.zLoginpage.zLoginToZimbraAjax(to);
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp("Read-Receipt: " + subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void unwantedReadReceiptDlgWhileMarkRead_Bug41499(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Preferences");
		zGoToPreferences("Mail");
		obj.zRadioBtn.zClick(localize(locator.messageReadNone));
		obj.zRadioBtn.zClick(localize(locator.readReceiptAsk));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(1000);
		zGoToApplication("Mail");
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { SelNGBase.selfAccountName.get() };
		ProvZCS.injectMessage(SelNGBase.selfAccountName.get(), recipients, cc,
				subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(localize(locator.markAsRead));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.warningMsg));
		obj.zDialog.zNotExists(localize(locator.infoMsg));

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