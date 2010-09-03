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

import framework.core.SelNGBase;
import framework.util.SleepUtil;
import framework.util.RetryFailedTests;
import framework.util.Stafzmprov;

import projects.html.tests.CommonTest;


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

	/*
	 * This test saves draft by keeping to, cc, bcc, subject, body and priority
	 * field and verifies accordingly. It also updates all the draft data and
	 * verifies it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void saveAndUpdateDraftWithProrityAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// save draft with some priority set
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		SelNGBase.selenium.get().select("name=priority", localize(locator.low));
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		zWaitTillObjectExist("folder", page.zMailApp.zDraftFldr);
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		// to overcome command time out issue
		zWaitTillObjectExist("message", subject);
		obj.zMessageItem.zClick(subject);
		zVerifyDraftFilledValues("save draft", to, cc, bcc, subject, body,
				attachments);
		to = Stafzmprov.getRandomAccount();
		cc = Stafzmprov.getRandomAccount();
		bcc = Stafzmprov.getRandomAccount();
		subject = getLocalizedData_NoSpecialChar();
		body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		SelNGBase.selenium.get().select("name=priority", localize(locator.high));
		SleepUtil.sleepSmall();// to avoid navigate away dialog
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		SleepUtil.sleepMedium();// to avoid navigate away dialog
		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		SleepUtil.sleepSmall(); // to avoid navigate away dialog
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		obj.zMessageItem.zClick(subject);
		zVerifyDraftFilledValues("update draft", to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);
		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// save draft with some prority set
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		SleepUtil.sleepMedium();
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		SleepUtil.sleepMedium();
		
		/**
		 * Amit Jagtap : Following line solves the problem of pop-up window.
		 * But need to confirm whether this pop-up is expected or not.
		 * selenium.chooseOkOnNextConfirmation();
		 */
		
		obj.zMessageItem.zClick(subject);
		zVerifyDraftFilledValues("save draft", to, cc, bcc, subject, body,
				attachments);
		to = "_selfAccountName_";
		cc = Stafzmprov.getRandomAccount();
		bcc = Stafzmprov.getRandomAccount();
		subject = getLocalizedData_NoSpecialChar();
		body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		SleepUtil.sleepLong(); // to overcome navigate away dialog
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		SleepUtil.sleepSmall();
		obj.zFolder.zClick(page.zMailApp.zDraftFldr);
		obj.zMessageItem.zNotExists(subject);
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zComposeView.zVerifyMsgHeaders(to, cc, bcc, subject, body,
				attachments);

		SelNGBase.needReset.set(false);
	}

	private void zVerifyDraftFilledValues(String action, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (to.equals("_selfAccountName_"))
			to = SelNGBase.selfAccountName.get();
		if (cc.equals("_selfAccountName_"))
			cc = SelNGBase.selfAccountName.get();
		if (bcc.equals("_selfAccountName_"))
			bcc = SelNGBase.selfAccountName.get();
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
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}
