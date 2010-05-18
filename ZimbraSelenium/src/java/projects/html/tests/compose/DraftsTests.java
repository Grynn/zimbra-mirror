package projects.html.tests.compose;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.cs.account.Provisioning;

import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.zcs.clients.ProvZCS;

/**
 * This class file contains drafts tests (saves draft, modify it, sending
 * updated draft mail & verifies all the field respectively
 * 
 * @author Jitesh Sojitra
 * 
 */

@SuppressWarnings("static-access")
public class DraftsTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) {
		// String test = method.getName();
		return new Object[][] { { "_selfAccountName_", "ccuser@testdomain.com",
				"bccuser@testdomain.com", getLocalizedData_NoSpecialChar(), "",
				"" } };
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
		zGoToApplication("Mail");
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

	/*
	 * This test saves draft by keeping to, cc, bcc, subject, body and priority
	 * field and verifies accordingly. It also updates all the draft data and
	 * verifies it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void saveAndUpdateDraftWithProrityAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// save draft with some priority set
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		selenium.select("name=priority", localize(locator.low));
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		zWaitTillObjectExist("folder", page.zMailApp.zDraftFldr);
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		// to overcome command time out issue
		zWaitTillObjectExist("message", subject);
		obj.zMessageItem.zClick(subject);
		zVerifyDraftFilledValues("save draft", to, cc, bcc, subject, body,
				attachments);
		to = ProvZCS.getRandomAccount();
		cc = ProvZCS.getRandomAccount();
		bcc = ProvZCS.getRandomAccount();
		subject = getLocalizedData_NoSpecialChar();
		body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		selenium.select("name=priority", localize(locator.high));
		Thread.sleep(SMALL_WAIT);// to avoid navigate away dialog
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		Thread.sleep(MEDIUM_WAIT);// to avoid navigate away dialog
		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		Thread.sleep(SMALL_WAIT); // to avoid navigate away dialog
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		obj.zMessageItem.zClick(subject);
		zVerifyDraftFilledValues("update draft", to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	/*
	 * This test saves & updates draft by keeping to, cc, bcc, subject, body and
	 * priority field and verfies accordingly. Sends updated mail to self and
	 * verifies all the stuff
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendUpdatedDraftedMailAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// save draft with some prority set
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		Thread.sleep(MEDIUM_WAIT);
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		Thread.sleep(MEDIUM_WAIT);
		
		/**
		 * Amit Jagtap : Following line solves the problem of pop-up window.
		 * But need to confirm whether this pop-up is expected or not.
		 * selenium.chooseOkOnNextConfirmation();
		 */
		
		obj.zMessageItem.zClick(subject);
		zVerifyDraftFilledValues("save draft", to, cc, bcc, subject, body,
				attachments);
		to = "_selfAccountName_";
		cc = ProvZCS.getRandomAccount();
		bcc = ProvZCS.getRandomAccount();
		subject = getLocalizedData_NoSpecialChar();
		body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		Thread.sleep(LONG_WAIT); // to overcome navigate away dialog
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		Thread.sleep(SMALL_WAIT);
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		obj.zMessageItem.zNotExists(subject);
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zComposeView.zVerifyMsgHeaders(to, cc, bcc, subject, body,
				attachments);

		needReset = false;
	}

	private void zVerifyDraftFilledValues(String action, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (to.equals("_selfAccountName_"))
			to = selfAccountName;
		if (cc.equals("_selfAccountName_"))
			cc = selfAccountName;
		if (bcc.equals("_selfAccountName_"))
			bcc = selfAccountName;
		String actualToVal = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zToField);
		String actualccVal = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zCcField);
		String actualbccVal = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zBccField);
		String actualSubjectVal = obj.zEditField
				.zGetInnerText(page.zComposeView.zSubjectField);
		String bodyVal = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actualToVal.indexOf(to) >= 0, "On " + action
				+ ", To-field isnt getting filled."
				+ page.zComposeView.formatExpActValues(to, actualToVal));
		Assert.assertTrue(actualccVal.indexOf(cc) >= 0, "On " + action
				+ ", CC-field isnt getting filled"
				+ page.zComposeView.formatExpActValues(cc, actualccVal));
		Assert.assertTrue(actualbccVal.indexOf(bcc) >= 0, "On " + action
				+ ", Bcc-field isnt getting filled"
				+ page.zComposeView.formatExpActValues(bcc, actualbccVal));
		Assert.assertTrue(actualSubjectVal.indexOf(subject) >= 0, "On "
				+ action
				+ ", Subject-field isnt getting filled"
				+ page.zComposeView.formatExpActValues(subject,
						actualSubjectVal));
		Assert.assertTrue(bodyVal.indexOf(body) >= 0, "On " + action
				+ ", Subject-field isnt getting filled"
				+ page.zComposeView.formatExpActValues(body, bodyVal));
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
