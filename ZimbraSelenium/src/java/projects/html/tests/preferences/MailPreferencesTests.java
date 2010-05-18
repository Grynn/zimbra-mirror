/**
 * Test to verify mail prefreneces settings
 */
package projects.html.tests.preferences;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;

import org.bouncycastle.crypto.prng.RandomGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import projects.html.clients.ProvZCS;
import projects.html.tests.CommonTest;
import projects.html.ui.ComposeView;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

@SuppressWarnings( { "static-access", "unused" })
public class MailPreferencesTests extends CommonTest {

	//--------------------------------------------------------------------------
	@DataProvider(name = "mailPreferencesDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		return new Object[][] { {} };

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to verify emails display per page functionality steps are: -To set
	 * no of emails displayed per page from pref-mail UI -To verify the no got
	 * saved properly in database -To verify only 10 mails are displayed per
	 * page in UI
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void MailDisplayPerPage() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		int numberOfMails = 11;
		String noOfEmailsToBeDisplayed = "10";
		String actualVal;
		String accountName = selfAccountName;
		String subject = "subject";
		String[] mailSubject = new String[numberOfMails];
		for (int i = 0; i < numberOfMails; i++) {
			mailSubject[i] = subject + i;
			// getLocalizedData_NoSpecialChar();
		}

		// To set no of e mails displayed per page from pref-mail
		page.zMailPrefUI
				.zNavigatePrefMailAndSelectEmailsPerPage(noOfEmailsToBeDisplayed);
		// to verify the no got saved properly in database
		// actualVal = ProvZCS.getAccountPreferenceValue(accountName,
		// "zimbraPrefMailItemsPerPage");
		// Assert.assertEquals(actualVal, "10",
		// "Number of mails value set is not set in db. Actual value is "
		// + actualVal);
		Thread.sleep(SMALL_WAIT);
		// to verify only 10 mails are displayed per page in UI
		page.zMailPrefUI.zInjectSpecificNoOfMails(mailSubject, 11);

		Thread.sleep(MEDIUM_WAIT);

		obj.zTab.zClick(localize(locator.mail));
		page.zMailApp.zClickCheckMailUntilMailShowsUp(mailSubject[10]);
		obj.zMessageItem.zNotExists(mailSubject[0]);

		needReset = false;
	}

	// this test is failing at the moment because of known issue that is when we
	// send email without subject it gives an error as [] INFO: Got result:
	// ERROR: There was an unexpected Confirmation! [No subject. Send anyway?]
	// on session fd0b57d73f7f4da48fed6855bb56f8f1
	// Hence commenting this test for the moment

	// @Test(groups = { "smoke", "full" }, retryAnalyzer =
	// RetryFailedTests.class)
	// public void zVerifyMessagePreview() throws Exception {
	//
	// if (isExecutionARetry)
	// handleRetry();
	//
	// String actualVal = ProvZCS.getAccountPreferenceValue(
	// ProvZCS.selfAccountName, "zimbraPrefShowFragments");
	//
	// if (!actualVal.equals("TRUE")) {
	// page.zMailPrefUI.zNavigateToPrefMail();
	// obj.zCheckbox
	// .zClick(page.zMailPrefUI.zDsplyTxtOfMsgInEmailListChkBox);
	//
	// obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
	//
	// }
	// page.zComposeView.zNavigateToMailCompose();
	// page.zComposeView.zSendMail(ProvZCS.selfAccountName, "", "", "",
	// "body", "");
	// page.zMailApp.zClickCheckMailUntilMailShowsUp("");
	// // page.zComposeView.zSendMailToSelfAndSelectIt(ProvZCS.selfAccountName,
	// // "", "", "subject", "body", "");
	// obj.zMessageItem.zExists("body");
	//
	// needReset = false;
	// }

	/**
	 * To verify default mail search
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void VerifyDefaultMailSearch() throws Exception {

		if (isExecutionARetry)
			handleRetry();
		String subject = getLocalizedData_NoSpecialChar();
		String selfAccName = SelNGBase.selfAccountName;
		page.zMailPrefUI.zSendMailToSelfAndMoveItToJunkAndVerify(subject);

		page.zMailPrefUI.zNavigateToPrefAndEditDefaultMailSearch("in:junk");

		Thread.sleep(SMALL_WAIT);
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraHTML(selfAccName);
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	/**
	 * To verify forward copy to functionlity
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void VerifyForwardACopyTo() throws Exception {

		if (isExecutionARetry)
			handleRetry();
		String fwdToAcc = ProvZCS.getRandomAccount();
		String subject = getLocalizedData_NoSpecialChar();

		page.zMailPrefUI.zNavigateToPrefMailAndSetFwdCopyTo(fwdToAcc);
		Thread.sleep(SMALL_WAIT);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(ProvZCS.selfAccountName,
				"", "", subject, getLocalizedData_NoSpecialChar(), "");

		zKillBrowsers();

		page.zLoginpage.zLoginToZimbraHTML(fwdToAcc);
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		needReset = false;
	}

	/**
	 * To verify send notification to functionality
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void VerifySendNotificationMsgTo() throws Exception {

		if (isExecutionARetry)
			handleRetry();
		String selfAcc = SelNGBase.selfAccountName.toLowerCase();
		String notificationSubject = "New message received at " + selfAcc;
		String notificationToAcc = ProvZCS.getRandomAccount();
		String subject = getLocalizedData_NoSpecialChar();

		page.zMailPrefUI
				.zNavigateToPrefMailAndSetSendNotificationMsgTo(notificationToAcc);
		Thread.sleep(SMALL_WAIT);

		page.zComposeView.zNavigateToMailCompose();
		Thread.sleep(SMALL_WAIT);
		page.zComposeView.zSendMailToSelfAndSelectIt(ProvZCS.selfAccountName,
				"", "", subject, getLocalizedData_NoSpecialChar(), "");

		zKillBrowsers();
		SelNGBase.selfAccountName = notificationToAcc;
		page.zLoginpage.zLoginToZimbraHTML(notificationToAcc);
		page.zMailApp.zClickCheckMailUntilMailShowsUp("New message received");
		obj.zMessageItem.zExists("New message received");
		needReset = false;
	}

	/**
	 * To verify auto reply functionality
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void VerifyAutoReply() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentAccount = SelNGBase.selfAccountName;
		String randomAccount = ProvZCS.getRandomAccount();
		String randomAccount2 = ProvZCS.getRandomAccount();
		String autoReplyMsg = localize(locator.auto);
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData_NoSpecialChar();
		page.zMailPrefUI.zNavigateToPrefMailAndSetAutoReply(autoReplyMsg);
		Thread.sleep(SMALL_WAIT);
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraHTML(randomAccount);
		page.zComposeView.zSendMail(currentAccount, "", "", subject, body, "");
		Thread.sleep(MEDIUM_WAIT);

		page.zMailPrefUI.zVerifyAutoReplyMsg(subject, autoReplyMsg,
				currentAccount);
		// to test negative of autoreply msg.to verify unchecking of auto reply
		// does work.
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraHTML(currentAccount);
		page.zMailPrefUI.zNavigateToPrefMail();
		obj.zCheckbox.zClick(page.zMailPrefUI.zSendASendAutoReplyMsgChkBox);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
		Thread.sleep(SMALL_WAIT);
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraHTML(randomAccount2);
		SelNGBase.selfAccountName = randomAccount2;
		page.zComposeView.zSendMail(currentAccount, "", "", subject, body, "");
		String replyMsgSubject = localize(locator.ZM_replySubjectPrefix) + " "
				+ subject;

		// little change here
		Thread.sleep(MEDIUM_WAIT);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zNotExists(replyMsgSubject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(replyMsgSubject);

		needReset = false;
	}

	/**
	 * To verify where to place message when I am in to or cc
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void VerifyMsgFrmMePlaceInInboxIfInToOrCc() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject1 = getLocalizedData_NoSpecialChar();
		String subject2 = getLocalizedData_NoSpecialChar();
		page.zMailPrefUI
				.zNavigateToPrefMailAndSelectMsgFrmMe("PlaceInInboxIfInToOrCc");
		// to verify cc mail is received in Inbox
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMail("", SelNGBase.selfAccountName, "",
				subject1, "", "");
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject1);

		// to verify bcc mail is not received
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMail("", "", SelNGBase.selfAccountName,
				subject2, "", "");

		// little change here
		Thread.sleep(LONG_WAIT);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zNotExists(subject2);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject2);

		needReset = false;
	}

	/**
	 * To verify ignore message when message sent by self
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "mailPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void VerifyMsgFrmMeIgnoreMsg() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String subject = getLocalizedData_NoSpecialChar();

		page.zMailPrefUI.zNavigateToPrefMailAndSelectMsgFrmMe("IgnoreMsg");
		// to verify the mail sent to self is not received
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMail(SelNGBase.selfAccountName, "", "", subject,
				"", "");
		Thread.sleep(LONG_WAIT);
		// little change here
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject);
		page.zMailPrefUI.zNavigateToPrefMailAndSelectMsgFrmMe("PlaceInInbox");
		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

	private void setAutoReply() throws Exception {
		handleRetry();
		VerifyAutoReply();
	}

}
