package projects.zcs.tests.preferences;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;

@SuppressWarnings("static-access")
public class SignatureTests extends CommonTest {

	@DataProvider(name = "SigPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("verifyTextSignatureAboveIncludedMsgInReply_Bug45880")
				|| test.equals("verifyTextSignatureBelowIncludedMsgInReply_Bug45880")
				||test.equals("verifyHtmlSignatureAboveIncludedMsgInReply_Bug45880")
				||test.equals("verifyHtmlSignatureBelowIncludedMsgInReply_Bug45880")
				||test.equals("verifyTextSignatureAboveIncludedMsgInFwd_Bug45880")
				||test.equals("verifyHtmlSignatureAboveIncludedMsgInFwd_Bug45880")
				||test.equals("verifyTextSignatureBelowIncludedMsgInFwd_Bug45880")
				||test.equals("verifyHtmlSignatureBelowIncludedMsgInFwd_Bug45880")
				||test.equals("verifyTextSignatureAboveIncludedMsgInReplyAll_Bug45880")
				||test.equals("verifyTextSignatureBelowIncludedMsgInReplyAll_Bug45880")
				||test.equals("verifyHtmlSignatureAboveIncludedMsgInReplyAll_Bug45880")
				||test.equals("verifyHtmlSignatureBelowIncludedMsgInReplyAll_Bug45880")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
				getLocalizedData_NoSpecialChar(),"_selfAccountName_", "", "", getLocalizedData(2),getLocalizedData(5), "" } };
		} else
			return new Object[][] { {} };
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}
	/**
	 * Test Case:Reply-Text:-verifyTextSignatureAboveIncludedMsgInReply
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Text mode
	 * 4.select or check "Above included messages" check box
	 * 5.Save
	 * 6.Compose a mail to self and verify it
	 * 7.Click on Reply and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place above included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */


	//Reply:-Text: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureAboveIncludedMsgInReply_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInText(
				localize(locator.aboveQuotedText), signatureBody, "Reply");

		needReset = false;
	}

	/**
	 * Test Case:Reply-Html Mode:verifyHtmlSignatureAboveIncludedMsgInReply
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Html mode
	 * 4.select or check "Above included messages" check box
	 * 5.Save
	 * 6.Compose a mail in html mode  and send it to self and verify it
	 * 7.Click on Reply and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place above included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//Reply:-html: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureAboveIncludedMsgInReply_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		
		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "HTML");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInHTML(
				localize(locator.aboveQuotedText), signatureBody, "Reply");

		needReset = false;
	}


	/**
	 * Test Case:Reply-Text:-verifyTextSignatureBelowIncludedMsgInReply
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Text mode
	 * 4.select or check "Below included messages" check box
	 * 5.Save
	 * 6.Compose a mail to self and verify it
	 * 7.Click on Reply and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place Below included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//Reply:-Text: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureBelowIncludedMsgInReply_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInText(
				localize(locator.atBottomOfMessage), signatureBody, "Reply");	
		
		needReset = false;
	}

	/**
	 * Test Case:Reply-Html Mode:BelowIncludedMsg:verifyHtmlSignatureBelowIncludedMsgInReply
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Html mode
	 * 4.select or check "below included messages" check box
	 * 5.Save
	 * 6.Compose a mail in html mode  and send it to self and verify it
	 * 7.Click on Reply and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place below included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//Reply:-html: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureBelowIncludedMsgInReply_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "HTML");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInHTML(
				localize(locator.atBottomOfMessage), signatureBody, "Reply");		


		needReset = false;
	}
	
	/**
	 * Test Case:Forward-Text:-verifyTextSignatureAboveIncludedMsgInFwd
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Text mode
	 * 4.select or check "Above included messages" check box
	 * 5.Save
	 * 6.Compose a mail to self and verify it
	 * 7.Click on Forward and verify subject field with Fwd: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place above included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */

	//FWD:-Text: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureAboveIncludedMsgInFwd_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains Fwd:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInText(
				localize(locator.aboveQuotedText), signatureBody, "Forward");

		needReset = false;
	}


	/**
	 * Test Case:Forward-Html Mode:verifyHtmlSignatureAboveIncludedMsgInFwd
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Html mode
	 * 4.select or check "Above included messages" check box
	 * 5.Save
	 * 6.Compose a mail in html mode  and send it to self and verify it
	 * 7.Click on Forward and verify subject field with Fwd: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place above included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */

	//Fwd:-html: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureAboveIncludedMsgInFwd_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		
		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "HTML");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains Fwd:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInHTML(
				localize(locator.aboveQuotedText), signatureBody, "Forward");

		needReset = false;
	}


	/**
	 * Test Case:Forward-Text:-verifyTextSignatureBelowIncludedMsgInFwd
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Text mode
	 * 4.select or check "Below included messages" check box
	 * 5.Save
	 * 6.Compose a mail to self and verify it
	 * 7.Click on Forward and verify subject field with Fwd: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place Below included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//Fwd:-Text: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureBelowIncludedMsgInFwd_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains Fwd:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInText(
				localize(locator.atBottomOfMessage), signatureBody, "Forward");	

		needReset = false;
	}

	/**
	 * Test Case:Forward-Html Mode:BelowIncludedMsg:verifyHtmlSignatureBelowIncludedMsgInFwd
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Html mode
	 * 4.select or check "Below included messages" check box
	 * 5.Save
	 * 6.Compose a mail in html mode  and send it to self and verify it
	 * 7.Click on Forward and verify subject field with Fwd: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place below included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//Fwd:-html: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureBelowIncludedMsgInFwd_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "HTML");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Fwd: " + subject),
				"Subject doesnot Contains FWD:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInHTML(
				localize(locator.atBottomOfMessage), signatureBody, "Forward");		


		needReset = false;
	}

	/**
	 * Test Case:ReplyAll-Text:-verifyTextSignatureAboveIncludedMsgInReplyAll
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Text mode
	 * 4.select or check "Above included messages" check box
	 * 5.Save
	 * 6.Compose a mail to self and verify it
	 * 7.Click on ReplyAll and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place above included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//ReplyAll:-Text: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureAboveIncludedMsgInReplyAll_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInText(
				localize(locator.aboveQuotedText), signatureBody, "ReplyAll");

		needReset = false;
	}


	/**
	 * Test Case:ReplyAll-Html Mode:verifyHtmlSignatureAboveIncludedMsgInReplyAll
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Html mode
	 * 4.select or check "Above included messages" check box
	 * 5.Save
	 * 6.Compose a mail in html mode  and send it to self and verify it
	 * 7.Click on ReplyAll and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place above included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//ReplyAll:-html: aboveinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureAboveIncludedMsgInReplyAll_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		
		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "HTML");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.aboveQuotedText));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInHTML(
				localize(locator.aboveQuotedText), signatureBody, "ReplyAll");

		needReset = false;
	}


	/**
	 * Test Case:ReplyAll-Text:-verifyTextSignatureBelowIncludedMsgInReplyAll
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Text mode
	 * 4.select or check "Below included messages" check box
	 * 5.Save
	 * 6.Compose a mail to self and verify it
	 * 7.Click on ReplyAll and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place Below included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */

	
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTextSignatureBelowIncludedMsgInReplyAll_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInText(
				localize(locator.atBottomOfMessage), signatureBody, "ReplyAll");	

		needReset = false;
	}

	/**
	 * Test Case:ReplyAll-Html Mode:BelowIncludedMsg:verifyHtmlSignatureBelowIncludedMsgInReplyAll
	 * 1.Login to ZCS
	 * 2.Go to Preference
	 * 3.Create Signature in Html mode
	 * 4.select or check "below included messages" check box
	 * 5.Save
	 * 6.Compose a mail in html mode  and send it to self and verify it
	 * 7.Click on ReplyAll and verify subject field with RE: key word
	 * 8.click on Signature Icon button and select signature name
	 * 9.Verify whether signature name place above or below included message
	 * 10.It should place below included message
	 * @param signatureName
	 * @param signatureBody
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	//ReplyAll:-html: belowinclmsg
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyHtmlSignatureBelowIncludedMsgInReplyAll_Bug45880(String signatureName,
			String signatureBody,String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(3000);
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "HTML");
		Thread.sleep(1000);
		page.zAccPref.usingSignature(localize(locator.atBottomOfMessage));
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_", "",
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(500);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				ComposeView.zSubjectField).contains("Re: " + subject),
				"Subject doesnot Contains Re:");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		page.zAccPref.zVerifySignaturePlaceInHTML(
				localize(locator.atBottomOfMessage), signatureBody, "ReplyAll");		


		needReset = false;
	}


	////end
	private void waitForIE() throws Exception {
		String browser = config.getString("browser");
		if (browser.equals("IE"))
			Thread.sleep(1000);

	}

	private void handleRetry() throws Exception  {
		// TODO Auto-generated method stub
		isExecutionARetry = false;// reset this to false
		zLogin();
	}


}
