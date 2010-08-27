package projects.zcs.tests.mail.compose;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.cs.account.Provisioning;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

import projects.html.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;

@SuppressWarnings("static-access")
public class ComposeReplyFwdInHTMLTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("sendMailToSelfAndVerify")
				|| test.equals("sendMailToSelfAndVerify_NewWindow")) {
			return new Object[][] {
					{ "_selfAccountName_", "ccuser@testdomain.com",
							"bccuser@testdomain.com", getLocalizedData(2),
							getLocalizedData(5), "" },
					{ "", "_selfAccountName_", "bccuser@testdomain.com",
							getLocalizedData(1), getLocalizedData(5), "" },
					{ "", "", "_selfAccountName_", getLocalizedData(1),
							getLocalizedData(5), "" } };
		} else {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(2), getLocalizedData(5), "" }, };
		}

	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		// set Compose in html-mode ON
		Map<String, Object> accntAttrs = new HashMap<String, Object>();
		accntAttrs.put(Provisioning.A_zimbraPrefComposeFormat,
				Provisioning.MAIL_FORMAT_HTML);
		zLoginIfRequired(accntAttrs);
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
	// ------------------------------Compose
	// Tests..------------------------------------------------------
	/**
	 * Test: Send an email(to self) in html-mode in several
	 * ways(to-only,cc-only, etc) and verify if the received mail has all the
	 * information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailToSelfAndVerify_htmlMode(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		SelNGBase.needReset.set(false);
	}

	/**
	 * Test: Send an email(to self) in html-mode and in-newwindow in several
	 * ways(to-only,cc-only, etc) and verify if the received mail has all the
	 * information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailToSelfAndVerify_NewWindowHtmlMode(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		checkForSkipException("na", "SF", "39446", "New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		SelNGBase.needReset.set(false);
	}

	// ----------------------------------Reply
	// tests...----------------------------------------
	/**
	 * Test: Reply to an email in html-mode and verify if the mail-compose and
	 * verify that cc and bcc is empty,to is filled, subject has Re appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyTest_HtmlMode(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(MailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Reply",
				"_selfAccountName_", "", "", "Re: " + subject, body,
				attachments);
		SelNGBase.needReset.set(false);
	}

	/**
	 * Test: Reply to an email in html-mode and in new-window and verify if the
	 * mail-compose and verify that cc and bcc is empty,to is filled, subject
	 * has Re appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyTest_NewWindowHtml(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		checkForSkipException("na", "SF", "39446", "New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(MailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Reply",
				"_selfAccountName_", "", "", "Re: " + subject, body,
				attachments);
		SelNGBase.needReset.set(false);

	}

	// -------------------Forward tests...----------------------------
	/**
	 * Test: Hit "Forward" to an email in html-mode and verify if the
	 * mail-compose and verify that cc, bcc and to are empty, but subject and
	 * body are filled. Also: subject has Fwd prepended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full",
			"enabled" }, retryAnalyzer = RetryFailedTests.class)
	public void fwdMailTest_HtmlMode(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(MailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Forward", "", "", "",
				"Fwd: " + subject, body, attachments);
		SelNGBase.needReset.set(false);
	}

	/**
	 * Test: Hit "Forward" to an email in html-mode and in-newwindow and verify
	 * if the mail-compose and verify that cc, bcc and to are empty, but subject
	 * and body are filled. Also: subject has Fwd prepended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void fwdMailTest_NewWindowHtml(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		checkForSkipException("na", "SF", "39446", "New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(MailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Forward", "", "", "",
				"Fwd: " + subject, body, attachments);
		SelNGBase.needReset.set(false);
	}

	// ----------------------Reply All tests...------------------
	/**
	 * Test: "Reply-all" to an email in html-mode and verify if the mail-compose
	 * and verify that cc, bcc,to,subject and body are filled, subject has Re
	 * appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyAllTest_HtmlMode(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(MailApp.zReplyAllIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("ReplyAll",
				"_selfAccountName_", cc, "", "Re: " + subject, body,
				attachments);
		SelNGBase.needReset.set(false);
	}

	/**
	 * Test: "Reply-all" to an email in html-mode and in-newwindow and verify if
	 * the mail-compose and verify that cc, bcc,to,subject and body are filled,
	 * subject has Re appended
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyAllTest_NewWindowHtml(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		checkForSkipException("na", "SF", "39446", "New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", cc,
				bcc, subject, body, attachments);
		obj.zButton.zClick(MailApp.zReplyAllIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("ReplyAll",
				"_selfAccountName_", cc, "", "Re: " + subject, body,
				attachments);
		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:No tab created after canceling compose
	 * 
	 * @step: 1. Go to Mail 2. Click on New MAil button 3. Verify Compose Tab
	 *        gets open 3.Click on Cancel and again clikc 2nd time on New Mail
	 *        button 4. Verify Compose Tab should get open again in 2nd attempt.
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkComposeTabForTheSecondComposeView_41755(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		Assert.assertTrue(
				SelNGBase.selenium.get().isElementPresent("id=zb__App__tab_COMPOSE1"),
				"Compose Tab is not present");
		obj.zButton.zClick(localize(locator.cancel));
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			obj.zDialog.zVerifyAlertMessage(localize(locator.warningMsg),
					localize(locator.askSaveDraft));
			obj.zButton.zClickInDlg(localize(locator.no));
		}
		Assert.assertFalse(SelNGBase.selenium.get()
				.isElementPresent("id=zb__App__tab_COMPOSE1"),
				"Compose Tab is present");
		page.zComposeView.zNavigateToMailCompose();
		Assert.assertTrue(
				SelNGBase.selenium.get().isElementPresent("id=zb__App__tab_COMPOSE1"),
				" 2nd Attempt Compose Tab is not present");
		obj.zButton.zClick(localize(locator.cancel));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void differentCasePrefFromAddress_Bug40068(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = SelNGBase.selfAccountName.get();
		ProvZCS.modifyAccount(accountName, "zimbraPrefFromAddress", accountName
				.toUpperCase());
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void lossOfDataOnChangingFormat_Bug44545(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(localize(locator.reply));
		obj.zButton.zClick(ComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(localize(locator.formatAsText));
		page.zComposeView.zVerifyComposeFilledValues("Reply",
				"_selfAccountName_", "", "", "Re: " + subject, body,
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
