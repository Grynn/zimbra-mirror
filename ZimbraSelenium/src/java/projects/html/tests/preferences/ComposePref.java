/**Tests to verify all the Preference Compose options
 * @Author:Prashant Jaiswal
 */

package projects.html.tests.preferences;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.html.tests.CommonTest;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

@SuppressWarnings( { "static-access", "unused" })
public class ComposePref extends CommonTest {

	//--------------------------------------------------------------------------
	@DataProvider(name = "ComposePrefDataProvider")
	private Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		return new Object[][] { {} };

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();

		SelNGBase.isExecutionARetry.set(false);
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Test to verify compose mail as HTML/Text
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ComposePrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composeAsHTMLAndAsText() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		page.zComposePrefUI.zNavigateToPrefCompose();
		page.zComposePrefUI.zSetComposeAsAndVerifyInMailCompose("AsHTML");
		page.zComposePrefUI.zNavigateToPrefCompose();
		page.zComposePrefUI.zSetComposeAsAndVerifyInMailCompose("AsText");
		SelNGBase.needReset.set(false);
	}

	/**
	 * To verify Reply/Forward using format of the original message
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ComposePrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyReplyFwdUsingOrignalMailFormat() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentUser = SelNGBase.selfAccountName.get();
		String randomAcc = Stafzmprov.getRandomAccount();
		String textSubject = getLocalizedData_NoSpecialChar();
		String htmlSubject = getLocalizedData_NoSpecialChar();

		page.zComposePrefUI.zNavigateToPrefCompose();
		obj.zCheckbox
				.zClick(page.zComposePrefUI.zReplyFwdUsingOrignalMailFormatChckBox);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);

		resetSession();
		// to login to random acc and send text and html mails to req
		// account
		page.zLoginpage.zLoginToZimbraHTML(randomAcc);
		page.zComposeView.zSendMail(currentUser, "", "", textSubject, "", "");
		page.zComposePrefUI.zNavigateToPrefComposeAndSelectComposeAse("AsHTML");
		SleepUtil.sleepMedium();
		page.zComposeView.zSendMail(currentUser, "", "", htmlSubject, "", "");
		SleepUtil.sleepMedium();

		resetSession();
		// to login back to acc where the reply fwd using orignal mail format
		// setting is made and verify

		page.zLoginpage.zLoginToZimbraHTML(currentUser);
		page.zComposePrefUI
				.zClickCheckMailUntilMailShowsUpAndClickReply(textSubject);
		SleepUtil.sleepSmall();
		obj.zButton.zNotExists(page.zComposePrefUI.zHtmlComposeBoldBtn);
		obj.zTab.zClick(localize(locator.mail));
		page.zComposePrefUI
				.zClickCheckMailUntilMailShowsUpAndClickReply(htmlSubject);
		SleepUtil.sleepSmall();
		// obj.zButton.zExists(page.zComposePrefUI.zHtmlComposeBoldBtn);//have
		// to ask to raja about verification

		SelNGBase.needReset.set(false);
	}

	/**
	 * To verify
	 * "When replying to an email, include the original email message as" This
	 * test verifies all the four options
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ComposePrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void replyReplyAllIncludeOriginalMsgAs() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zSendMail(SelNGBase.selfAccountName.get(), "", "", subject,
				body, "");
		page.zMailApp.zClickCheckMailUntilMailShowsUp(page.zMailApp.zInboxFldr,
				subject);
		// page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		page.zComposePrefUI
				.zNavigateToPrefComposeAndSetRplyRplyAllIncludeMsgAs(
						"FullTextInline", "");
		page.zComposePrefUI.zVerifyReplyReplyAllIncludeMsgAs("FullTextInline",
				"", subject, body);

		page.zComposePrefUI
				.zNavigateToPrefComposeAndSetRplyRplyAllIncludeMsgAs(
						"FullTextInlineWithPrefix", ">");
		page.zComposePrefUI.zVerifyReplyReplyAllIncludeMsgAs(
				"FullTextInlineWithPrefix", ">", subject, body);

		page.zComposePrefUI
				.zNavigateToPrefComposeAndSetRplyRplyAllIncludeMsgAs(
						"FullTextInlineWithPrefix", "|");
		page.zComposePrefUI.zVerifyReplyReplyAllIncludeMsgAs(
				"FullTextInlineWithPrefix", "|", subject, body);

		page.zComposePrefUI
				.zNavigateToPrefComposeAndSetRplyRplyAllIncludeMsgAs(
						"AnAttachment", "");
		page.zComposePrefUI.zVerifyReplyReplyAllIncludeMsgAs("AnAttachment",
				"", subject, body);

		page.zComposePrefUI
				.zNavigateToPrefComposeAndSetRplyRplyAllIncludeMsgAs(
						"DoNotInclude", "");
		page.zComposePrefUI.zVerifyReplyReplyAllIncludeMsgAs("DoNotInclude",
				"", subject, body);

		SelNGBase.needReset.set(false);
	}

	/**
	 * To verify
	 * "When forwarding an email, include the original email message as" This
	 * test verifies all three options
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ComposePrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void fwdIncludeOriginalMsgAs() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zSendMail(SelNGBase.selfAccountName.get(), "", "", subject,
				body, "");
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);

		page.zComposePrefUI.zNavigateToPrefComposeAndSetFwdIncludeMsgAs(
				"FullTextInline", "");
		page.zComposePrefUI.zVerifyFwdIncludeMsgAs("FullTextInline", "",
				subject, body);

		page.zComposePrefUI.zNavigateToPrefComposeAndSetFwdIncludeMsgAs(
				"FullTextInlineWithPrefix", ">");
		page.zComposePrefUI.zVerifyFwdIncludeMsgAs("FullTextInlineWithPrefix",
				">", subject, body);

		page.zComposePrefUI.zNavigateToPrefComposeAndSetFwdIncludeMsgAs(
				"FullTextInlineWithPrefix", "|");
		page.zComposePrefUI.zVerifyFwdIncludeMsgAs("FullTextInlineWithPrefix",
				"|", subject, body);

		page.zComposePrefUI.zNavigateToPrefComposeAndSetFwdIncludeMsgAs(
				"AnAttachment", "");
		page.zComposePrefUI.zVerifyFwdIncludeMsgAs("AnAttachment", "", subject,
				body);

		SelNGBase.needReset.set(false);
	}

	/**
	 * To verify sent message is a saved or not saved in sent folder
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ComposePrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sentMsgCopyInSentFolder() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		String subject1 = getLocalizedData_NoSpecialChar();
		String subject2 = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData_NoSpecialChar();

		// to verify save in sent folder
		page.zComposePrefUI
				.zNavigateToPrefComposeAndSelectSentMsgOption("SaveInSent");

		page.zComposeView.zSendMail(SelNGBase.selfAccountName.get(), "", "",
				subject1, body, "");

		zWaitTillObjectExist("folder", page.zMailApp.zSentFldr);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);

		obj.zMessageItem.zExists(subject1);

		// to verify do not save in sent folder
		page.zComposePrefUI
		.zNavigateToPrefComposeAndSelectSentMsgOption("DoNotSaveInSent");

		
		page.zComposeView.zSendMail(SelNGBase.selfAccountName.get(), "", "",
				subject2, body, "");

		obj.zFolder.zClick(page.zMailApp.zSentFldr);

		obj.zMessageItem.zNotExists(subject2);
		
		SelNGBase.needReset.set(false);
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}

}
