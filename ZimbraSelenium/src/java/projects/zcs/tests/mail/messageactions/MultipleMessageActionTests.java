package projects.zcs.tests.mail.messageactions;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access" })
public class MultipleMessageActionTests extends CommonTest {
	public static final String zMailListItemChkBox = "id=zlhi__CLV__se";

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "MailDataProvider")
	protected Object[][] createData(Method method) throws ServiceException {
		return new Object[][] { { ProvZCS.getRandomAccount(),
				"_selfAccountName_", "ccuser@testdomain.com",
				"bccuser@testdomain.com" } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zMailApp.zNavigateToMailApp();
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
	 * This test injects 3 mail, marks junk and verifies mail not exist into
	 * Inbox folder and check whether all properly moved to Junk folder or not.
	 * Again it marks Not Junk and check whether all moved back to Inbox folder
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void markMultipleMailsToJunkNotJunk(String from, String to,
			String cc, String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body3" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zJunkIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		verifyInjectedMailsExists();
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zJunkIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsExists();

		needReset = false;
	}

	/**
	 * This test injects 3 mail, delete it (trash) and verifies mail not exist
	 * into Inbox folder and check whether all properly moved to Trash folder or
	 * not. It also deletes from that and check for empty folder
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteMultipleMails(String from, String to, String cc,
			String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body3" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zDeleteIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		verifyInjectedMailsExists();
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zDeleteIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		verifyInjectedMailsNotExists();

		needReset = false;
	}

	/**
	 * This test injects 3 mail, moves mails to Sent folder and verifies mail
	 * not exist into Inbox folder and check whether all properly moved to Sent
	 * folder or not.
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveMultipleMailsToSentFolder(String from, String to,
			String cc, String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body3" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zMoveIconBtn);
		String dlgName;
		dlgName = localize(locator.moveMessages);
		obj.zFolder.zClickInDlgByName(page.zMailApp.zSentFldrMoveDlg, dlgName);
		obj.zButton.zClickInDlgByName(localize(locator.ok), dlgName);
		// discard if unwanted dlg exists
		zPressBtnIfDlgExists(dlgName, localize(locator.ok),
				page.zMailApp.zSentFldrMoveDlg);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zSentFldr));
		verifyInjectedMailsExists();

		needReset = false;
	}

	/**
	 * This test injects 3 mail, moves mail to created new folder and verifies
	 * mail not exist into Inbox folder and check whether all properly moved to
	 * new folder or not.
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveMultipleMailsToNewFolder(String from, String to, String cc,
			String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String newFolder = getLocalizedData_NoSpecialChar();
		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body3" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zMoveIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator._new),
				localize(locator.moveMessages));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel), newFolder,
				localize(locator.createNewFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewFolder));
		obj.zFolder
				.zClickInDlgByName(newFolder, localize(locator.moveMessages));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessages));
		// discard if unwanted dlg exists
		zPressBtnIfDlgExists(localize(locator.moveMessages),
				localize(locator.ok), newFolder);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(newFolder);
		verifyInjectedMailsExists();

		needReset = false;
	}

	/**
	 * This test creates 3 drafted mail, select all and verify enable disable
	 * menu items and also check for deletion UI
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteMultipleDraftedMails(String from, String to, String cc,
			String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String subject[] = { "subject1", "subject2", "subject3" };
		saveDrafts();
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zMailApp.zDraftsFldr));
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zExists(subject[i]);
		}
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zButton.zIsDisabled(page.zMailApp.zMoveBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zReplyBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zReplyAllBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zEditDraftBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zJunkBtn);
		obj.zButton.zClick(page.zMailApp.zDeleteIconBtn);
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zMailApp.zDraftsFldr));
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zNotExists(subject[i]);
		}
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zExists(subject[i]);
		}

		needReset = false;
	}

	/**
	 * This test injects 2 mail, select all > right clicks to it and verifies
	 * all enable disable menu items
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyRtClickMenusForMultipleMails(String from, String to,
			String cc, String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String subject[] = { "subject1", "subject2" };
		String body[] = { "body1", "body2" };
		for (int i = 0; i <= 1; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		obj.zMessageItem.zRtClick("subject1");
		String[] enabledMenuItemsArray = { page.zMailApp.zMarkReadMenuIconBtn,
				page.zMailApp.zForwardMenuIconBtn,
				page.zMailApp.zDeleteMenuIconBtn,
				page.zMailApp.zPrintMenuEnaDisaBtn,
				page.zMailApp.zMoveMenuIconBtn, page.zMailApp.zJunkMenuIconBtn };
		for (int i = 0; i <= 5; i++) {
			obj.zMenuItem.zIsEnabled(enabledMenuItemsArray[i]);
		}

		String[] disabledMenuItemsArray = { page.zMailApp.zReplyMenuEnaDisaBtn,
				page.zMailApp.zReplyAllMenuEnaDisaBtn,
				page.zMailApp.zEditAsNewMenuEnaDisaBtn,
				page.zMailApp.zShowOriginalMenuEnaDisaBtn,
				page.zMailApp.zNewFilterMenuEnaDisaBtn };
		for (int i = 0; i <= 4; i++) {
			obj.zMenuItem.zIsDisabled(disabledMenuItemsArray[i]);
		}

		needReset = false;
	}

	/**
	 * This test injects 2 mail, select all and verifies all toolbar enable
	 * disable buttons
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyToolbarBtnForMultipleMails(String from, String to,
			String cc, String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String subject[] = { "subject1", "subject2" };
		String body[] = { "body1", "body2" };
		for (int i = 0; i <= 1; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		Thread.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(1500);
		String[] enabledToolbarItemsArray = { page.zMailApp.zNewMenuBtn,
				page.zMailApp.zGetMailBtn, page.zMailApp.zDeleteBtn,
				page.zMailApp.zMoveBtn, page.zMailApp.zForwardBtn,
				page.zMailApp.zJunkBtn, page.zMailApp.zTagBtn,
				page.zMailApp.zViewBtn, page.zMailApp.zPrintBtn };
		for (int i = 0; i <= 8; i++) {
			obj.zMenuItem.zIsEnabled(enabledToolbarItemsArray[i]);
		}

		String[] disabledToolbarItemsArray = { page.zMailApp.zReplyBtn,
				page.zMailApp.zReplyAllBtn, page.zMailApp.zDetachBtn2 };
		for (int i = 0; i <= 2; i++) {
			obj.zButton.zIsDisabled(disabledToolbarItemsArray[i]);
		}

		needReset = false;
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
	 * @param from
	 * @param to
	 * @param cc
	 * @param bcc
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkStatusOfSelectCheckboxAfterSearch_Bug43116(String from,
			String to, String cc, String bcc) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body2" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		obj.zMessageItem.zExists(subject[0]);
		obj.zMessageItem.zExists(subject[1]);
		obj.zMessageItem.zExists(subject[2]);
		selenium.type("xpath=//input[@class='search_input']", "body2");
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		Thread.sleep(1000);
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
		Thread.sleep(1000);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'zlhi__CLV__se') and contains(@class,'ImgCheckboxChecked')]"),
						"Select All check box is unchecked");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowEven')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxChecked')]"),
						"1st list item shows unchecked after clicking Select All check box");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowOdd Row-selected')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxChecked')]"),
						"2nd list item shows unchecked after clicking Select All check box");
		// unchecked all
		obj.zCheckbox.zClick(zMailListItemChkBox);
		Thread.sleep(500);
		Assert
				.assertTrue(selenium
						.isElementPresent("xpath=//div[contains(@id,'zlhi__CLV__se') and contains(@class,'ImgCheckboxUnchecked')]"));
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowEven')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxUnchecked')]"),
						"1st list item shows checked after doing unchecked on 'Select All' check box");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowOdd')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxUnchecked')]"),
						"2nd list item shows checked after doing unchecked on 'Select All' check box");

		needReset = false;
	}

	/**
	 * Forward multiple messages and verify received message
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardMultipleMessages_Bug44236(String from, String to,
			String cc, String bcc) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject1, subject2, acc1, acc2, fwdSubject, fwdBody;
		acc1 = SelNGBase.selfAccountName;
		subject1 = "subject1";
		subject2 = "subject2";
		fwdSubject = "fwdSubject";
		fwdBody = "fwdBody";
		acc2 = ProvZCS.getRandomAccount();
		String subject[] = { subject1, subject2 };
		String body[] = { "body1", "body2" };
		for (int i = 0; i <= 1; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		Thread.sleep(1500);
		obj.zMessageItem.zCtrlClick(subject1);
		obj.zMessageItem.zCtrlClick(subject2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		obj.zTextAreaField.zType(page.zComposeView.zToField, acc2);
		obj.zEditField.zType(page.zComposeView.zSubjectField, fwdSubject);
		obj.zEditor.zType(fwdBody);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2500);

		resetSession();
		SelNGBase.selfAccountName = acc2;
		page.zLoginpage.zLoginToZimbraAjax(acc2);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(fwdSubject);
		obj.zMessageItem.zClick(fwdSubject);
		Thread.sleep(1500);
		selenium.click("link=subject1");
		Thread.sleep(5000);
		selenium.selectWindow("_blank");
		String msgBody = null;
		msgBody = selenium.getBodyText();
		assertReport(msgBody, localize(locator.from), "Verifying From header");
		assertReport(msgBody, localize(locator.to), "Verifying To header");
		assertReport(msgBody, localize(locator.cc), "Verifying Cc header");
		assertReport(msgBody.toLowerCase(), from.toLowerCase(),
				"Verifying From field value");
		assertReport(msgBody.toLowerCase(), acc1.toLowerCase(),
				"Verifying To field value");
		assertReport(msgBody.toLowerCase(), "ccuser@testdomain.com",
				"Verifying Cc field value");
		assertReport(msgBody, subject1, "Verifying Subject field value");
		Assert.assertTrue(selenium
				.isElementPresent("//html/body[contains(text(), 'body1')]"));
		Assert
				.assertFalse(msgBody.contains("HTTP ERROR: 404"),
						"Clicking to 'View entire message' link throws HTTP ERROR: 404");
		Assert
				.assertFalse(
						msgBody
								.contains("The page you were trying to access does not exist."),
						"Clicking to 'View entire message' link doesn't open message");
		Assert
				.assertFalse(msgBody.contains("Internal Server Error"),
						"Verifying message data after clicking to 'View entire message' link");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		selenium.selectWindow(null);

		selenium.click("link=subject2");
		Thread.sleep(5000);
		selenium.selectWindow("_blank");
		msgBody = selenium.getBodyText();
		assertReport(msgBody, localize(locator.from), "Verifying From header");
		assertReport(msgBody, localize(locator.to), "Verifying To header");
		assertReport(msgBody, localize(locator.cc), "Verifying Cc header");
		assertReport(msgBody.toLowerCase(), from.toLowerCase(),
				"Verifying From field value");
		assertReport(msgBody.toLowerCase(), acc1.toLowerCase(),
				"Verifying To field value");
		assertReport(msgBody.toLowerCase(), "ccuser@testdomain.com",
				"Verifying Cc field value");
		assertReport(msgBody, subject2, "Verifying Subject field value");
		Assert.assertTrue(selenium
				.isElementPresent("//html/body[contains(text(), 'body2')]"));
		Assert
				.assertFalse(msgBody.contains("HTTP ERROR: 404"),
						"Clicking to 'View entire message' link throws HTTP ERROR: 404");
		Assert
				.assertFalse(
						msgBody
								.contains("The page you were trying to access does not exist."),
						"Clicking to 'View entire message' link doesn't open message");
		Assert
				.assertFalse(msgBody.contains("Internal Server Error"),
						"Verifying message data after clicking to 'View entire message' link");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		selenium.selectWindow(null);

		needReset = false;
	}

	private void commonInjectMessage(String from, String to, String cc,
			String bcc, String subject, String body) throws Exception {
		to = SelNGBase.selfAccountName;
		String[] recipients = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
	}

	private void verifyInjectedMailsNotExists() throws Exception {
		String subject[] = { "subject1", "subject2", "subject3", "subject4",
				"subject5" };
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zNotExists(subject[i]);
		}
	}

	private void verifyInjectedMailsExists() throws Exception {
		String subject[] = { "subject1", "subject2", "subject3", "subject4",
				"subject5" };
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zExists(subject[i]);
		}
	}

	private void saveDrafts() throws Exception {
		String subject[] = { "subject1", "subject2", "subject3" };
		for (int i = 0; i <= 2; i++) {
			page.zComposeView.zNavigateToMailCompose();
			obj.zEditField.zType(page.zComposeView.zSubjectField, subject[i]);
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		}
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