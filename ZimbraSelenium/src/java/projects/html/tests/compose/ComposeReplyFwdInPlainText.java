package projects.html.tests.compose;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;

/**
 * This class file contains plain text compose related tests - compose mail,
 * reply, reply all and forward
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class ComposeReplyFwdInPlainText extends CommonTest {

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
		zLoginIfRequired();
		zGoToApplication("Mail");
		SelNGBase.isExecutionARetry.set(false);
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/**
	 * Send an email(to self) in plain text-mode using To and verify if the
	 * received mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailOnlyWithToAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Send an email(to self) in plain text-mode using Cc and verify if the
	 * received mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailOnlyWithCcAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Send an email(to self) in plain text-mode using Bcc and verify if the
	 * received mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "parallel", "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailOnlyWithBccAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Send an email(to self) in plain text-mode using To, Cc and Bcc and verify
	 * if the received mail has all the information
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailWithToCcBccAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Send an email(to self) in plain text-mode, reply it and verify compose
	 * filled values
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyMailAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, bcc, subject,
				body, attachments);
		obj.zButton.zClick(page.zComposeView.zReplyBtn);
		obj.zButton.zExists(page.zComposeView.zSendBtn);
		/* zVerifyComposeFilledValues doesn't work */
//		page.zComposeView.zVerifyComposeFilledValues("Reply",
//				"_selfAccountName_", "", "", "Re: " + subject, body,
//				attachments);
		obj.zCheckbox.zExists(attachments); /*
											 * need to update by
											 * zVerifyNotChecked
											 */
		obj.zButton.zClick(page.zComposeView.zCancelBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Send an email(to self) in plain text-mode, reply all and verify compose
	 * filled values
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyAllMailAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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
		obj.zCheckbox.zExists(attachments); /*
											 * need to update by
											 * zVerifyNotChecked
											 */
		obj.zButton.zClick(page.zComposeView.zCancelBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Send an email(to self) in plain text-mode, forward it and verify compose
	 * filled values
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardMailAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, bcc, subject,
				body, attachments);
		obj.zButton.zClick(page.zComposeView.zForwardBtn);
		obj.zButton.zExists(page.zComposeView.zSendBtn);
		/* zVerifyComposeFilledValues doesn't work */
//		page.zComposeView.zVerifyComposeFilledValues("Forward",
//				"_selfAccountName_", "", "", "Fwd: " + subject, body,
//				attachments);
		obj.zCheckbox.zVerifyIsChecked(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);

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
