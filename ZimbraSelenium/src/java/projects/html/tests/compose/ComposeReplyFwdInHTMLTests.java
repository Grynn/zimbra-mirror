package projects.html.tests.compose;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.cs.account.Provisioning;

import framework.util.SleepUtil;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;

/**
 * This class file contains html compose related tests - compose mail, reply,
 * reply all and forward
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class ComposeReplyFwdInHTMLTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@SuppressWarnings("unused")
	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("sendMailOnlyWithToAndVerify")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "" } };
		} else if (test.equals("sendMailOnlyWithCcAndVerify")) {
			return new Object[][] { { "", "_selfAccountName_", "",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "" } };
		} else if (test.equals("sendMailOnlyWithBccAndVerify")) {
			return new Object[][] { { "", "", "_selfAccountName_",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "" } };
		} else if (test.equals("sendMailWithToCcBccAndVerify")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "" } };
		} else if (test.equals("replyMailAndVerify")
				|| test.equals("replyAllMailAndVerify")
				|| test.equals("forwardMailAndVerify")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "samplejpg.jpg" } };
		} else {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		// set Compose in html-mode ON
		Map<String, Object> accntAttrs = new HashMap<String, Object>();
		accntAttrs.put(Provisioning.A_zimbraPrefComposeFormat,
				Provisioning.MAIL_FORMAT_HTML);
		zLoginIfRequired(accntAttrs);
		zGoToApplication("Mail");
		isExecutionARetry = false;
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/**
	 * Send an email(to self) in html-mode using To and verify if the received
	 * mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailOnlyWithToAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		needReset = false;
	}

	/**
	 * Send an email(to self) in html-mode using Cc and verify if the received
	 * mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailOnlyWithCcAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		needReset = false;
	}

	/**
	 * Send an email(to self) in html-mode using Bcc and verify if the received
	 * mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailOnlyWithBccAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		needReset = false;
	}

	/**
	 * Send an email(to self) in html-mode using To, Cc and Bcc and verify if
	 * the received mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailWithToCcBccAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		needReset = false;
	}

	/**
	 * Send an email(to self) in html-mode, reply it and verify compose filled
	 * values
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyMailAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, bcc, subject,
				body, attachments);
		obj.zButton.zClick(page.zComposeView.zReplyBtn);
		obj.zButton.zExists(page.zComposeView.zSendBtn);
		/* zVerifyComposeFilledValues doesn't work */
		// page.zComposeView.zVerifyComposeFilledValues("Reply",
		// "_selfAccountName_", "", "", "Re: " + subject, body,
		// attachments);
		// verify attachment checkbox uncheck status
		obj.zCheckbox.zExists(attachments); /*
											 * need to update by
											 * zVerifyNotChecked
											 */
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		SleepUtil.sleepLong();
		SleepUtil.sleepLong();
		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		

		needReset = false;
	}

	/**
	 * Send an email(to self) in html-mode, reply all and verify compose filled
	 * values
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyAllMailAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();


		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, bcc, subject,
				body, attachments);
		obj.zButton.zClick(page.zComposeView.zReplyBtn);
		obj.zButton.zExists(page.zComposeView.zSendBtn);
		/* zVerifyComposeFilledValues doesn't work */
		// page.zComposeView.zVerifyComposeFilledValues("Reply All",
		// "_selfAccountName_", "", "", "Re: " + subject, body,
		// attachments);
		// verify attachment checkbox uncheck status
		obj.zCheckbox.zExists(attachments); /*
											 * need to update by
											 * zVerifyNotChecked
											 */
//		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		SleepUtil.sleepLong();
		SleepUtil.sleepLong();
		obj.zButton.zClick(page.zMailApp.zDraftFldr);

		needReset = false;
	}

	/**
	 * Send an email(to self) in html-mode, forward it and verify compose filled
	 * values
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardMailAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, bcc, subject,
				body, attachments);
		obj.zButton.zClick(page.zComposeView.zForwardBtn);
		obj.zButton.zExists(page.zComposeView.zSendBtn);
		/* zVerifyComposeFilledValues doesn't work */
		// page.zComposeView.zVerifyComposeFilledValues("Forward",
		// "_selfAccountName_", "", "", "Fwd: " + subject, body,
		// attachments);
		// verify attachment checkbox uncheck status
		obj.zCheckbox.zVerifyIsChecked(attachments);
		//obj.zButton.zClick(page.zComposeView.zCancelBtn);
		//obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		//DelayUtil.sleepLong();
		//obj.zButton.zClick(page.zMailApp.zDraftFldr);


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
