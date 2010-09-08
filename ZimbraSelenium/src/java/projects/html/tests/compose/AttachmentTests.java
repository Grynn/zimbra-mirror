package projects.html.tests.compose;

import java.io.File;
import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.SleepUtil;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

import projects.html.tests.CommonTest;
import projects.html.ui.ComposeView;
import projects.html.ui.MailApp;

/**
 * 
 * This class file contains +ve and -ve attachment tests
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class AttachmentTests extends CommonTest {

	// --------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	// --------------------------------------------------------------------------
	@SuppressWarnings("unused")
	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("sendMailWith3AttachmentsAndVerify")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData_NoSpecialChar(), "",
					"putty.log,samplejpg.jpg,testtextfile.txt" } };
		} else if (test.equals("addAttachmentOneByOneAndVerify")
				|| (test.equals("uncheckAttachmentAndVerify") || (test
						.equals("cancelAddingAttachmentTest")))) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData_NoSpecialChar(), "", "testpptfile.ppt" } };
		} else if (test.equals("bigAttachmentThrowErrorTest")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData_NoSpecialChar(), "", "mail700.pst" } };
		} else if (test.equals("unCheckOneAttachmentAndSendMailTest")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData_NoSpecialChar(), "",
					"testbitmapfile.bmp,testexcelfile.xls" } };
		} else {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com", "",
					getLocalizedData_NoSpecialChar(), "testwordfile.doc" } };
		}
	}

	// --------------------------------------------------------------------------
	// SECTION 2: SETUP
	// --------------------------------------------------------------------------
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

	// --------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	// --------------------------------------------------------------------------

	/**
	 * Test sends mail to self with 3 attachments and verifies accordingly
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendMailWith3AttachmentsAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		SleepUtil.sleepSmall();
		zWaitTillObjectExist("message", subject); // timing issue
		obj.zMessageItem.zVerifyHasAttachment(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test adds attachment one by one and verifies accordingly
	 */
	@Test(dataProvider = "composeDataProvider", groups = {"d", "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addAttachmentOneByOneAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		String newAttachment = "testsoundfile.wav";
		page.zComposeView.zAddAttachments(newAttachment, false);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		SleepUtil.sleepMedium();

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test unchecks first attachment, adds second attachment and verifies
	 * present of first attachment and absense of second of attachment
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void uncheckAttachmentAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zCheckbox.zClick(attachments);
		String newAttachment = "testsoundfile.wav";
		page.zComposeView.zAddAttachments(newAttachment, false);
		obj.zCheckbox.zNotExists(attachments);
		obj.zCheckbox.zVerifyIsChecked(newAttachment);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		SleepUtil.sleepMedium();
		obj.zButton.zClick(page.zMailApp.zDraftFldr);
		obj.zMessageItem.zClick(subject);
		obj.zCheckbox.zNotExists(attachments);
		obj.zCheckbox.zVerifyIsChecked(newAttachment);
		obj.zButton.zClick(page.zMailApp.zDraftFldr);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test verifies for big attachment to the mail (should throw error message
	 * and attachment should not attach to the mail)
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void bigAttachmentThrowErrorTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		if (to.equals("_selfAccountName_"))
			to = SelNGBase.selfAccountName.get();
		obj.zTextAreaField.zType(page.zComposeView.zToField, to);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		page.zComposeView.zAddAttachments(attachments, false);
		String isSendBtnExists;
		for (int i = 0; i <= 150; i++) {
			SleepUtil.sleepMedium(); // big attachment takes too much time
			isSendBtnExists = obj.zButton
					.zExistsDontWait(page.zComposeView.zSendBtn);
			if (isSendBtnExists.equals("true"))
				break;
		}
		String bigAttToastMessage = obj.zToastAlertMessage.zGetMsg();
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			assertReport(
					bigAttToastMessage,
					"This file cannot be attached because it has exceeded the maximum allowed size",
					"Verifying big attachment toast message");
		}
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		SleepUtil.sleepMedium();

		SelNGBase.needReset.set(false);
	}

	/**
	 * Cancel adding attachment to the mail and verify attachment not present
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cancelAddingAttachmentTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		if (to.equals("_selfAccountName_"))
			to = SelNGBase.selfAccountName.get();
		obj.zTextAreaField.zType(page.zComposeView.zToField, to);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		obj.zButton.zClick(ComposeView.zAddAttachmentBtn);
		SleepUtil.sleepLong(); // please don't remove this
		String[] attList = attachments.split(",");
		for (int i = 0; i < attList.length; i++) {
			File f = new File("src/java/projects/html/data/" + attList[i]);
			String path = f.getAbsolutePath();
			obj.zBrowseField.zTypeInDlgWithKeyboard((i + 1) + ".", path, "");
		}
		obj.zButton.zClick(page.zComposeView.zAddAttachCancelBtn);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		SleepUtil.sleepMedium();

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test adds two attachment to the mail, while sending mail it unchecks one
	 * attachment
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void unCheckOneAttachmentAndSendMailTest(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		SleepUtil.sleepSmall();
		String[] attList = attachments.split(",");
		obj.zCheckbox.zClick(attList[0]);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		SleepUtil.sleepMedium();
		MailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		// verify one attachment not present

		SelNGBase.needReset.set(false);
	}

	// --------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	// --------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}