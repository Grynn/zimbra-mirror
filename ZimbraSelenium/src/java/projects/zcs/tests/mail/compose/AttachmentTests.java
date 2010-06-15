package projects.zcs.tests.mail.compose;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;

@SuppressWarnings("static-access")
public class AttachmentTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) {

		String test = method.getName();
		if (test.equals("simpleAttachTest")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), "", "putty.log" }, };
		}else if (test.equals("rplyFwdInlineImageAttchMailInPlainText_Bug30335")) {
			return new Object[][] { { "_selfAccountName_", "", "",
				getLocalizedData(2), "", "" }, };
	}else {// default
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), "", "putty.log,testexcelfile.xls" }, };
		}

	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

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
	 * Test: Send an email(to self) with an attachment and verify if the
	 * attachment is attached when we get received
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void simpleAttachTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		needReset = false;
	}

	/**
	 * Test: Attach multiple-attachments and verify if all of them are attached
	 * Also verify if "download all attachments"-link is displayed
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachMultipleTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		// "_selfAccountName_" = page.zComposeView.zLoginAndNavigateToCompose();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		needReset = false;
	}

	/**
	 * Test: Hitting Forward on an email with attachment should open compose
	 * with attachments-checkboxes checked/selected by-default dependsOn:
	 * attachMultipleTest
	 */
	@Test(dataProvider = "composeDataProvider", dependsOnMethods = "attachMultipleTest", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void fwdAMailWithAttachment(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)// relogin and call all the dependsOnmethods
			fwdAMailWithAttachment_Retry();
		obj.zButton.zClick(MailApp.zForwardIconBtn);
		obj.zTextAreaField.zWait(localize(locator.toLabel));
		String[] att = attachments.split(",");
		for (int i = 0; i < att.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(att[i].toLowerCase());
		}
		needReset = false;
	}

	/**
	 * Test: Select a mail with attachment. Hit Forward, Change the
	 * compose-format to HTML, attach another attachment-inline Verify if the
	 * inlined-attachment is displayed. dependsOn: fwdAMailWithAttachment
	 */
	@Test(dependsOnMethods = "fwdAMailWithAttachment", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void changeToHTMLAndAttachInline() throws Exception {
		if (isExecutionARetry)// relogin and call all the dependsOnmethods
			changeToHTMLAndAttachInline_Retry();
		obj.zButton.zClick(ComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		page.zComposeView.zAddAttachments("structure.jpg", true);
		// Thread.sleep(1000);
		String html = obj.zEditor.zGetInnerHTML("");
		boolean b = (html.toLowerCase().indexOf("dfsrc=") > 0);
		Assert.assertTrue(b,
				"html content of the message body didnt contain <img string");
		needReset = false;
	}

	/**
	 * Test: Select a mail with 2-attachment and 3rd one attached inline. Hit
	 * detach button Verify if all the attachments are intact in new-window
	 * dependsOn: changeToHTMLAndAttachInline
	 */
	@Test(dataProvider = "composeDataProvider", dependsOnMethods = "changeToHTMLAndAttachInline", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void detachRetainsAttachments(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)// relogin and call all the dependsOnmethods
			detachRetainsAttachments_Retry();
		Thread.sleep(2000);
		obj.zButton.zClick("id=zb__COMPOSE1__DETACH_COMPOSE_left_icon");
		Thread.sleep(4000);
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zCancelIconBtn);
		obj.zButton.zWait(ComposeView.zSendIconBtn);
		String[] att = attachments.split(",");
		for (int i = 0; i < att.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(att[i].toLowerCase());
		}
		// verify if editor has image...
		String html = obj.zEditor.zGetInnerHTML("");
		boolean b = (html.toLowerCase().indexOf("dfsrc=") > 0);
		Assert.assertTrue(b,
				"html content of the message body didnt contain <img string");
		needReset = false;
	}

	/**
	 * Test: Hit Send to an email that has 2-attachments and 1-inlined
	 * attachment Verify if the mail has all these attachment intact dependsOn:
	 * detachRetainsAttachments
	 */
	@Test(dependsOnMethods = "detachRetainsAttachments", groups = { "smoke",
			"full" })
	public void sendAttachmentsFromNewWindow() throws Exception {
		if (isExecutionARetry)// relogin and call all the dependsOnmethods
			sendAttachmentsFromNewWindow_Retry();
		page.zComposeView.zSendMailToSelfAndVerify("_selfAccountName_", "", "",
				"somesubject", "", "");
		// verify if messagebody has image...
		obj.zMessageItem.zVerifyCurrentMsgBodyHasImage();
		needReset = false;
	}

	/**
	 * Test Case :replying to mail with inline attachment doesn't show it until saved as draft
	 * Expected:The attachment checkbox should be included in the compose for reply/forward.
	 * At that point, the user can choose whether or not to remove the attachment.
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
	public void rplyFwdInlineImageAttchMailInPlainText_Bug30335(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		
		page.zComposeView.zNavigateToMailCompose();
		obj.zButton.zClick(ComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		page.zComposeView.zAddAttachments("structure.jpg", true);
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		String attachment = "structure.jpg";
		obj.zButton.zClick(MailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);

		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[@id='zv__COMPOSE1_attachments_div']/table/tbody/tr/td/div[contains(@class,'ImgAttachment')]"),
						"orignal msg doesn't contains attachment");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[@id='zv__COMPOSE1_attachments_div']/table/tbody/tr/td[3]/a[contains(text(),'"
										+ attachment + "')]"),
						"orignal msg doesn't contains attachment with subject");

		obj.zTextAreaField.zWait(localize(locator.toLabel));
		obj.zCheckbox.zVerifyIsChecked(attachment);
		obj.zButton.zClick(localize(locator.cancel));
		obj.zButton.zClick(MailApp.zReplyBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);

		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[@id='zv__COMPOSE1_attachments_div']/table/tbody/tr/td/div[contains(@class,'ImgAttachment')]"),
						"orignal msg doesn't contains attachment");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[@id='zv__COMPOSE1_attachments_div']/table/tbody/tr/td[3]/a[contains(text(),'"
										+ attachment + "')]"),
						"orignal msg doesn't contains attachment with subject");

		obj.zTextAreaField.zWait(localize(locator.toLabel));
		obj.zCheckbox.zVerifyIsNotChecked(attachment);

		needReset = false;
	}
	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those that needs just relogin
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

	private void fwdAMailWithAttachment_Retry() throws Exception {
		handleRetry();// relogin
		attachMultipleTest("_selfAccountName_", "", "", getLocalizedData(2),
				"", "putty.log,testexcelfile.xls");
	}

	private void changeToHTMLAndAttachInline_Retry() throws Exception {
		handleRetry();// relogin
		attachMultipleTest("_selfAccountName_", "", "", getLocalizedData(2),
				"", "putty.log,testexcelfile.xls");
		fwdAMailWithAttachment("_selfAccountName_", "", "",
				getLocalizedData(2), "", "putty.log,testexcelfile.xls");
	}

	private void detachRetainsAttachments_Retry() throws Exception {
		handleRetry();// relogin
		attachMultipleTest("_selfAccountName_", "", "", getLocalizedData(2),
				"", "putty.log,testexcelfile.xls");
		fwdAMailWithAttachment("_selfAccountName_", "", "",
				getLocalizedData(2), "", "putty.log,testexcelfile.xls");
		changeToHTMLAndAttachInline();
	}

	private void sendAttachmentsFromNewWindow_Retry() throws Exception {
		handleRetry();// relogin
		attachMultipleTest("_selfAccountName_", "", "", getLocalizedData(2),
				"", "putty.log,testExcelFile.xls");
		fwdAMailWithAttachment("_selfAccountName_", "", "",
				getLocalizedData(2), "", "putty.log,testexcelfile.xls");
		changeToHTMLAndAttachInline();
		detachRetainsAttachments("_selfAccountName_", "", "",
				getLocalizedData(2), "", "putty.log,testexcelfile.xls");
	}
}
