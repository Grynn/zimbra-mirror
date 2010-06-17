package projects.html.tests.compose;

import java.io.File;
import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;

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
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		Thread.sleep(SMALL_WAIT);
		zWaitTillObjectExist("message", subject); // timing issue
		obj.zMessageItem.zVerifyHasAttachment(subject);

		needReset = false;
	}

	/**
	 * Test adds attachment one by one and verifies accordingly
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addAttachmentOneByOneAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zCheckbox.zVerifyIsChecked(attachments);
		String newAttachment = "testsoundfile.wav";
		page.zComposeView.zAddAttachments(newAttachment, false);
		obj.zCheckbox.zVerifyIsChecked(attachments);
		obj.zCheckbox.zVerifyIsChecked(newAttachment);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		Thread.sleep(MEDIUM_WAIT);

		needReset = false;
	}

	/**
	 * Test unchecks first attachment, adds second attachment and verifies
	 * present of first attachment and absense of second of attachment
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void uncheckAttachmentAndVerify(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
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
		Thread.sleep(MEDIUM_WAIT);
		obj.zButton.zClick(page.zMailApp.zDraftFldr);
		obj.zMessageItem.zClick(subject);
		obj.zCheckbox.zNotExists(attachments);
		obj.zCheckbox.zVerifyIsChecked(newAttachment);
		obj.zButton.zClick(page.zMailApp.zDraftFldr);

		needReset = false;
	}

	/**
	 * Test verifies for big attachment to the mail (should throw error message
	 * and attachment should not attach to the mail)
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void bigAttachmentThrowErrorTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		if (to.equals("_selfAccountName_"))
			to = selfAccountName;
		obj.zTextAreaField.zType(page.zComposeView.zToField, to);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		page.zComposeView.zAddAttachments(attachments, false);
		String isSendBtnExists;
		for (int i = 0; i <= 150; i++) {
			Thread.sleep(MEDIUM_WAIT); // big attachment takes too much time
			isSendBtnExists = obj.zButton
					.zExistsDontWait(page.zComposeView.zSendBtn);
			if (isSendBtnExists.equals("true"))
				break;
		}
		String bigAttToastMessage = obj.zToastAlertMessage.zGetMsg();
		if (config.getString("locale").equals("en_US")) {
			assertReport(
					bigAttToastMessage,
					"This file cannot be attached because it has exceeded the maximum allowed size",
					"Verifying big attachment toast message");
		}
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		Thread.sleep(MEDIUM_WAIT);

		needReset = false;
	}

	/**
	 * Cancel adding attachment to the mail and verify attachment not present
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cancelAddingAttachmentTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		if (to.equals("_selfAccountName_"))
			to = selfAccountName;
		obj.zTextAreaField.zType(page.zComposeView.zToField, to);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		obj.zButton.zClick(ComposeView.zAddAttachmentBtn);
		Thread.sleep(LONG_WAIT); // please don't remove this
		String[] attList = attachments.split(",");
		for (int i = 0; i < attList.length; i++) {
			File f = new File("src/java/projects/html/data/" + attList[i]);
			String path = f.getAbsolutePath();
			obj.zBrowseField.zTypeInDlgWithKeyboard((i + 1) + ".", path, "");
		}
		obj.zButton.zClick(page.zComposeView.zAddAttachCancelBtn);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		Thread.sleep(MEDIUM_WAIT);

		needReset = false;
	}

	/**
	 * Test adds two attachment to the mail, while sending mail it unchecks one
	 * attachment
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void unCheckOneAttachmentAndSendMailTest(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		Thread.sleep(SMALL_WAIT);
		String[] attList = attachments.split(",");
		obj.zCheckbox.zClick(attList[0]);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
		Thread.sleep(MEDIUM_WAIT);
		MailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		// verify one attachment not present

		needReset = false;
	}

	// --------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	// --------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}