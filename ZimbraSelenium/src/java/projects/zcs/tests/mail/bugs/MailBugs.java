package projects.zcs.tests.mail.bugs;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;
import com.zimbra.common.service.ServiceException;
import framework.core.*;
import framework.util.LmtpUtil;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class MailBugs extends CommonTest {
	public static final String zMailListItemChkBox = "id=zlhi__CLV__se";

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("continouslyPromptingReadReceipt_Bug44128")) {
			return new Object[][] { {} };
		} else if (test.equals("senderAndFromInShowOriginal_Bug30438")) {
			return new Object[][] { {
					"admin@"
							+ ZimbraSeleniumProperties
									.getStringProperty("server"),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar() + "Bug30438_Subject",
					getLocalizedData_NoSpecialChar() + "Bug30438_Body", "" } };
		} else if (test.equals("msgBodyLostWhileChooseNotToAttachSig_Bug40559")
				|| test.equals("dontSelectFirstMsgByDefault_Bug39908_Bug43335")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5), "" } };
		} else if (test.equals("jsErrorOnClickingDetachIcon_Bug35948")
				|| test
						.equals("UserCanAbleToSavePrefWithEmptySignatureField_Bug44607")
				|| test.equals("emptyTrash_Bug41209")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5), "" } };
		} else if (test.equals("ajaxCrashesIfMailBodyContainsScript_Bug36391")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"<script ></script >", "<script ></script >", "" } };
		} else if (test.equals("networkServiceErrorInNewWindow_Bug41205")
				|| test
						.equals("checkStatusOfSelectCheckboxAfterSearch_Bug43116")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), "ccuser@testdomain.com",
					"bccuser@testdomain.com",
					getOnlyEnglishAlphabetCharAndNumber(),
					getOnlyEnglishAlphabetCharAndNumber(), "" } };
		} else {
			return new Object[][] { { "" }, };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.zLogin();
	}

	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void continouslyPromptingReadReceipt_Bug44128() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		zGoToApplication("Preferences");
		zGoToPreferences("Mail");
		obj.zRadioBtn.zClick(localize(locator.readReceiptAsk));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(1000);
		zGoToApplication("Mail");
		subject = page.zMailApp
				.zInjectMessage("continouslyPromptingReadReceipt_Bug44128");
		obj.zMessageItem.zClick(subject);
		zWaitTillObjectExist("dialog", localize(locator.warningMsg));
		if (!ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			assertReport(localize(locator.readReceiptSend).replaceAll("<br>",
					""), obj.zDialog.zGetMessage(localize(locator.warningMsg)),
					"Verifying dialog text for notifying read receipt");
		}
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zDialog.zNotExists(localize(locator.warningMsg));
		obj.zMessageItem.zClick(subject);
		obj.zDialog.zNotExists(localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void senderAndFromInShowOriginal_Bug30438(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		if (ZimbraSeleniumProperties.getStringProperty("locale")
				.equals("en_US")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_AU")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_GB")) {
			// Show original
			resetSession();
			page.zLoginpage.zLoginToZimbraAjax("admin@"
					+ ZimbraSeleniumProperties.getStringProperty("server"));
			zGoToApplication("Preferences");
			zGoToPreferences("Accounts");
			SleepUtil.sleep(1500);
			obj.zButton.zClick("admin@"
					+ ZimbraSeleniumProperties.getStringProperty("server"));
			obj.zMenuItem.zClick("root@"
					+ ZimbraSeleniumProperties.getStringProperty("server"));
			obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
			SleepUtil.sleep(2000);

			zGoToApplication("Mail");
			obj.zButton.zClick(localize(locator.view));
			obj.zMenuItem.zClick(localize(locator.byConversation));
			page.zComposeView.zNavigateToMailCompose();
			obj.zTextAreaField.zType(page.zComposeView.zToField, to);
			obj.zTextAreaField.zType(page.zComposeView.zCcField, cc);
			obj.zTextAreaField.zType(page.zComposeView.zBccField, bcc);
			obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
			obj.zEditor.zType(body);
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(2000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			SleepUtil.sleep(1000);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zClick("id=zmi__CLV__Dra__SHOW_ORIG_left_icon");
			SleepUtil.sleep(4000);
			ClientSessionFactory.session().selenium().selectWindow("_blank");
			String showOrigText = ClientSessionFactory.session().selenium().getBodyText();
			SleepUtil.sleep(1000);
			Assert
					.assertFalse(showOrigText.contains("Sender: root@"
							+ ZimbraSeleniumProperties
									.getStringProperty("server")),
							"Show original contains Sender if sender is alias from - Bug 30438");
			verifyShowOriginalMsgBody(showOrigText, "From: root@"
					+ ZimbraSeleniumProperties.getStringProperty("server"),
					"To: admin@"
							+ ZimbraSeleniumProperties
									.getStringProperty("server"),
					"Cc: ccuser@testdomain.com", "Bcc: bcc@"
							+ ZimbraSeleniumProperties
									.getStringProperty("server"), subject, body);
			System.out.println(ClientSessionFactory.session().selenium().getAllWindowTitles());
			if (!ZimbraSeleniumProperties.getStringProperty("browser").equals(
					"IE")) {
				ClientSessionFactory.session().selenium().close();
			}
			ClientSessionFactory.session().selenium().selectWindow(null);
			obj.zMessageItem.zClick(subject);
			obj.zButton.zClick("id=zb__CLV__EDIT_left_icon");
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zSendIconBtn);
			page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
			obj.zButton.zClick(page.zMailApp.zViewBtn);
			obj.zMenuItem.zClick(localize(locator.byMessage));
			SleepUtil.sleep(1000);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zClick(localize(locator.showOrig));
			SleepUtil.sleep(1000);
			ClientSessionFactory.session().selenium().selectWindow("_blank");
			showOrigText = ClientSessionFactory.session().selenium().getBodyText();
			SleepUtil.sleep(1000);
			Assert
					.assertFalse(showOrigText.contains("Sender: root@"
							+ ZimbraSeleniumProperties
									.getStringProperty("server")),
							"Show original contains Sender if sender is alias from - Bug 30438");
			verifyShowOriginalMsgBody(showOrigText, "From: root@"
					+ ZimbraSeleniumProperties.getStringProperty("server"),
					"To: admin@"
							+ ZimbraSeleniumProperties
									.getStringProperty("server"),
					"Cc: ccuser@testdomain.com", "Bcc: bcc@"
							+ ZimbraSeleniumProperties
									.getStringProperty("server"), subject, body);
			if (!ZimbraSeleniumProperties.getStringProperty("browser").equals(
					"IE")) {
				ClientSessionFactory.session().selenium().close();
			}
			ClientSessionFactory.session().selenium().selectWindow(null);
			obj.zButton.zClick(localize(locator.view));
			obj.zMenuItem.zClick(localize(locator.byConversation));

			resetSession();
			String newUser = Stafzmprov.getRandomAccount();
			SelNGBase.selfAccountName.set(newUser);
			page.zLoginpage.zLoginToZimbraAjax(newUser);
		}

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void msgBodyLostWhileChooseNotToAttachSig_Bug40559(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Preferences");
		zGoToPreferences("Signatures");
		page.zSignaturePref.zCreateSignature("testSignature", "signatureBody",
				"text");
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(2000);
		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick("testSignature");
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(1000);
		verifySignatureWithMsgbody(subject);

		zGoToApplication("Preferences");
		zGoToPreferences("Composing");
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(2000);
		verifySignatureWithMsgbody(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:-Message is automatically
	 * selected(checkMsgStatusOfSelectedMsgWhileMovingOneFolderToOther)
	 * 1.Compose Mail to self 2.Go To Inbox 3.Click on Get Mail until it show up
	 * in Inbox. 4.Verify Msg In Reading Pane. 5.It should disply
	 * "To view a message, click on it" 6.Go To another folder and check for the
	 * same msg "To view a message, click on it" 7. Means in short It should not
	 * display the content of the mail while going from one folder to another.OR
	 * Clicking on folder should NOT open the first message at any time
	 * 
	 * @author Girish
	 */

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void dontSelectFirstMsgByDefault_Bug39908_Bug43335(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zFolder.zClick(localize(locator.inbox));
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent(
				"xpath=//div[@id='zv__CLV__MSG']/table/tbody/tr/td"),
				"To view a message, click on it.Msg does not present");
		String BodyText = ClientSessionFactory.session().selenium().getText(
				"xpath=//div[@id='zv__CLV__MSG']/table/tbody/tr/td");
		Assert.assertTrue(BodyText.contains(localize(locator.viewMessage)));
		obj.zFolder.zClick(localize(locator.sent));
		Assert.assertTrue(ClientSessionFactory.session().selenium().getText(
				"xpath=//div[@id='zv__CLV__MSG']/table/tbody/tr/td").contains(
				localize(locator.viewMessage)));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void jsErrorOnClickingDetachIcon_Bug35948(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		obj.zButton.zClick("id=zb__COMPOSE1__DETACH_COMPOSE_left_icon");
		SleepUtil.sleep(2000);
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		obj.zTextAreaField.zType(page.zComposeView.zToField, to);
		obj.zTextAreaField.zType(page.zComposeView.zCcField, cc);
		obj.zTextAreaField.zType(page.zComposeView.zBccField, bcc);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		obj.zEditor.zType(body);
		obj.zButton.zClick(localize(locator.send));
		ClientSessionFactory.session().selenium().selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:User cannot save preferences due to empty signature field Steps
	 * to replicate Login as user to Ajax webclient (as user with no existing
	 * sigs) Click Preferences Click Signatures Click another pref section, eg
	 * Instant messaging Change something and click save
	 * "Signature value is empty. It's required." appears at the top.
	 * Validation(Expected) above msg should not appear while saving empty
	 * signature field
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void UserCanAbleToSavePrefWithEmptySignatureField_Bug44607(
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Preferences");
		zGoToPreferences("Signatures");
		Assert
				.assertTrue(
						ClientSessionFactory.session().selenium()
								.isElementPresent(
										"xpath=//td[contains(@class,'ZOptionsHeader ImgPrefsHeader') and contains(text(),'"
												+ localize(locator.signatures)
												+ "')]"),
						"Signatures label is not present");
		zGoToPreferences("Composing");
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Pref Save Message shows correctly");
		String[] signatureValueMissingRequired = localize(
				locator.signatureValueMissingRequired).split("'");
		Assert
				.assertFalse(ClientSessionFactory.session().selenium().isElementPresent(
						"xpath=//div[contains(@id,'z_toast_text') and contains(text(),'"
								+ signatureValueMissingRequired[0] + "')]"),
						"Signature value is empty. It's required. this msg still present");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void emptyTrash_Bug41209() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject = "Empty Trash Test";
		String body = "This message will be deleted.";

		/**
		 * 1. Send Mail to self. 2. Select it. 3. Delete it. Move to Trash. *
		 */
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName
				.get(), "", "", subject, body, "");

		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDeleteBtn);
		SleepUtil.sleep(1000);

		/**
		 * 1. Check deleted message is present in Trash. 2. Right click on trash
		 * and select Empty Trash.
		 */
		obj.zFolder.zClick(localize(locator.trash));
		obj.zMessageItem.zExists(subject);
		obj.zFolder.zRtClick(localize(locator.trash));
		obj.zMenuItem.zClick(localize(locator.emptyTrash));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.warningMsg));

		/**
		 * 1. Go To Trash. 2. Check message is deleted from Trash.
		 */
		obj.zFolder.zClick(localize(locator.trash));
		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * bug 36391 - Ajax Client crashes if mail body contains <script ></script >
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ajaxCrashesIfMailBodyContainsScript_Bug36391(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				"");
		ClientSessionFactory.session().selenium().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		obj.zMessageItem.zClick(subject);

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
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent(
				"id=zb__App__tab_COMPOSE1"), "Compose Tab is not present");
		obj.zButton.zClick(localize(locator.cancel));
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			obj.zDialog.zVerifyAlertMessage(localize(locator.warningMsg),
					localize(locator.askSaveDraft));
			obj.zButton.zClickInDlg(localize(locator.no));
		}
		Assert.assertFalse(ClientSessionFactory.session().selenium().isElementPresent(
				"id=zb__App__tab_COMPOSE1"), "Compose Tab is present");
		page.zComposeView.zNavigateToMailCompose();
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent(
				"id=zb__App__tab_COMPOSE1"),
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
		Stafzmprov.modifyAccount(accountName, "zimbraPrefFromAddress", accountName
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

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void networkServiceErrorInNewWindow_Bug41205(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"na",
				"SF",
				"39446",
				"New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		LmtpUtil.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		SleepUtil.sleep(2000);
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		for (int i = 0; i <= 10; i++) {
			String dlgExists = obj.zDialog
					.zExistsDontWait(localize(locator.zimbraTitle));
			if (dlgExists.equals("true")) {
				Assert
						.assertFalse(
								localize(locator.errorService)
										.indexOf(
												obj.zDialog
														.zGetMessage(localize(locator.zimbraTitle))) >= 0,
								"Bug 41205 - 'system failure: Caught null' while opening message in new window");
				obj.zButton.zClickInDlgByName(localize(locator.ok),
						localize(locator.zimbraTitle));
			} else {
				SleepUtil.sleep(1000);
			}
		}
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SleepUtil.sleep(2000);
		ClientSessionFactory.session().selenium().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test case:Checkbox stays checked after a "search action"
	 * 
	 * @steps 1.Run a search with more than a screen-full of results. 2.Click on
	 *        select all checkbox to select all results. 3.Apply a tag to all
	 *        the results. Expected: The select all checkbox should be in the
	 *        same state as the rest of the list items. I think checked would be
	 *        better than unchecked. Verified both the condition as when Select
	 *        All checked then other item should checked and when it is
	 *        unchecked then other list item shouldunchecked
	 * @author Girish
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkStatusOfSelectCheckboxAfterSearch_Bug43116(String from,
			String to, String cc, String bcc) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body2" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		obj.zMessageItem.zExists(subject[0]);
		obj.zMessageItem.zExists(subject[1]);
		obj.zMessageItem.zExists(subject[2]);
		ClientSessionFactory.session().selenium().type("xpath=//input[@class='search_input']",
				"body2");
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zNotExists(subject[0]);
		obj.zMessageItem.zExists(subject[1]);
		obj.zMessageItem.zExists(subject[2]);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zDialog.zExists(localize(locator.createNewTag));
		obj.zEditField.zTypeInDlgByName(localize(locator.tagName),
				"TagFromMail", localize(locator.createNewTag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewTag));
		SleepUtil.sleep(1000);
		Assert
				.assertTrue(
						ClientSessionFactory.session().selenium()
								.isElementPresent(
										"xpath=//div[contains(@id,'zlhi__CLV__se') and contains(@class,'ImgCheckboxChecked')]"),
						"Select All check box is unchecked");
		Assert
				.assertTrue(
						ClientSessionFactory.session().selenium()
								.isElementPresent(
										"xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowEven')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxChecked')]"),
						"1st list item shows unchecked after clicking Select All check box");
		Assert
				.assertTrue(
						ClientSessionFactory.session().selenium()
								.isElementPresent(
										"xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowOdd Row-selected')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxChecked')]"),
						"2nd list item shows unchecked after clicking Select All check box");
		// unchecked all
		obj.zCheckbox.zClick(zMailListItemChkBox);
		SleepUtil.sleep(500);
		Assert
				.assertTrue(ClientSessionFactory.session().selenium()
								.isElementPresent(
								"xpath=//div[contains(@id,'zlhi__CLV__se') and contains(@class,'ImgCheckboxUnchecked')]"));
		Assert
				.assertTrue(
						ClientSessionFactory.session().selenium()
								.isElementPresent(
										"xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowEven')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxUnchecked')]"),
						"1st list item shows checked after doing unchecked on 'Select All' check box");
		Assert
				.assertTrue(
						ClientSessionFactory.session().selenium()
								.isElementPresent(
										"xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowOdd')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxUnchecked')]"),
						"2nd list item shows checked after doing unchecked on 'Select All' check box");

		SelNGBase.needReset.set(false);
	}

	// --------------------------- internal wrappers ---------------------------
	private void verifySignatureWithMsgbody(String subject) throws Exception {
		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		Boolean signatureContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		assertReport("true", signatureContains.toString(),
				"Message body doesn't contain signature if set from account preferences");
		obj.zEditor.zType(subject);
		obj.zButton.zClick(localize(locator.signature));
		obj.zMenuItem.zClick(localize(locator.signatureDoNotAttach));
		signatureContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		assertReport(subject, obj.zEditor.zGetInnerText(""),
				"Message body contain lost after choosing not to attach signature");
		assertReport("false", signatureContains.toString(),
				"Message body signature is not removed after choose not to attach signature");
		obj.zButton.zClick(localize(locator.signature));
		obj.zMenuItem.zClick("testSignature");
		Boolean subjectContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		signatureContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		assertReport("true", signatureContains.toString(),
				"Message body signature lost after choose to attach signature");
		assertReport("true", subjectContains.toString(),
				"Message body signature lost after choose to attach signature");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
	}

	private void commonInjectMessage(String from, String to, String cc,
			String bcc, String subject, String body) throws Exception {
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { to };
		LmtpUtil.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
	}
}