package projects.zcs.tests.mail.sharing;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

/**
 * @author Jitesh Sojitra
 * 
 *         Class contains 9 methods regarding 1.decline share 2.share as viewer
 *         and verify mail deletion 3.share as manager and verify mail deletion
 *         4.share as admin and verify mail deletion 5.share as admin and admin
 *         shares folder to 3rd user 6.share as viewer and revoke it 7.resend
 *         share invite 8.share as viewer to multiple users 9.share as manager
 *         to multiple users and one user declines share
 * 
 *         Below parameter used to pass values from data provider
 * 
 * @param to
 *            - to user
 * @param cc
 *            - cc user
 * @param bcc
 *            - bcc user
 * @param subject
 *            - subject of mail
 * @param body
 *            - body of mail
 * @param attachments
 *            - attachments if any
 * @param applicationtab
 *            - Mail, Address Book or any other application tab from which you
 *            want to share folder
 * @param sharingfoldername
 *            - Folder to be shared
 * @param sharetype
 *            - Either Internal, External or public
 * @param invitedusers
 *            - Email ids to whom folder to be shared
 * @param role
 *            - Either None, Viewer, Manager or Admin
 * @param message
 *            - Either Send message, No message, Add note or composing mail
 *            regarding shares
 * @param sharingnoteifany
 *            - Applicable only if Add note selected for previous message
 *            parameter
 * @param allowtoseeprivateappt
 *            - Applicable only for calendar folder sharing
 * @param mountingfoldername
 *            - While other user mount the share, he can specify his own name
 *            using this parameter
 * 
 */

@SuppressWarnings( { "static-access" })
public class MailSharingTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "SharingDataProvider")
	protected Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("declineShare")) {
			return new Object[][] { { "Mail", localize(locator.inbox), "",
					ProvZCS.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsViewerAndVerifyMailDeletion")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsManagerAndVerifyMailDeletion_Bug40954")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleManager), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsAdminAndVerifyMailDeletion_Bug40954")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleAdmin), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsAdminAndAdminSharesFolderto3rduser")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleAdmin),
					localize(locator.sendStandardMailAboutSharePlusNote),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsViewerAndRevoke")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleAdmin), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("resendShareInvite")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleAdmin), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsViewerToMultipleUsers")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", "",
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test
				.equals("shareAsManagerToMultipleUsersAndOneUserDeclinesShare")
				|| test
						.equals("CheckOwnerOfMessageDoesGetACopyInSentFolder_30451")) {
			return new Object[][] { { "_selfAccountName_", "", "", "verify",
					getLocalizedData(5), "", "Mail", localize(locator.inbox),
					"", "", localize(locator.shareRoleManager), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test
				.equals("checkGranteesAdminAndManagerPermissionOnTheUI_31242")) {
			return new Object[][] { { "_selfAccountName_", "", "", "verify",
					getLocalizedData(5), "", "calendar",
					localize(locator.calendar), "", "",
					localize(locator.shareRoleAdmin), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test
				.equals("checkWarningMsgForDeletingMountPtFromTrash_bug42082")) {
			return new Object[][] { { "_selfAccountName_", "", "", "verify",
					getLocalizedData(5), "", "Mail", localize(locator.inbox),
					"", "", localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test
				.equals("ExceptionWhileMovingMailsFromSharedToLocalFolder_Bug45034")) {
			return new Object[][] { { "_selfAccountName_", "", "", "verify",
					getLocalizedData(5), "", "Mail", localize(locator.inbox),
					"", "", localize(locator.shareRoleManager), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "", "testOBOSubject",
					"testOBOBody", "putty.log", "Mail",
					localize(locator.inbox), "", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		obj.zButton.zClick(page.zMailApp.zMailTabIconBtn);
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
	/**
	 * This test creates share and invited user declines share
	 * 
	 * 1.Login to user1 and share folder to user2 2.Login to user2, decline
	 * share and send decline related mail to user1 3.Login to user1 and verify
	 * share declined mail in Inbox
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void declineShare(String applicationtab, String sharingfoldername,
			String sharetype, String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zDeclineShare();
		obj.zButton.zClick(page.zMailApp.zGetMailIconBtn);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zNotExists(localize(locator.shareCreatedSubject));
		page.zSharing.zVerifyShareDeclinedMailInSentFolder(currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);

		resetSession();
		SelNGBase.selfAccountName = currentloggedinuser;
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zSharing.zVerifyShareDeclinedMailInInboxFolder(
				currentloggedinuser, sharingfoldername, sharetype,
				currentloggedinuser, role, sharingnoteifany);
		page.zSharing.zRevokeShare(sharingfoldername, "", "");

		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 as viewer rights and user2
	 * verifies mail deletion (-ve case)
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * viewer rights 3.Login to user2 and accept share 4.Click to mounted folder
	 * >> verify mail exists 5.Click to mail and verify Delete toolbar button
	 * remains disabled 6.Right click to mail and verify Delete menu item
	 * remains disabled
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsViewerAndVerifyMailDeletion(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		verifyMailDeletionOnDiffRole(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername);

		needReset = false;
	}

	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composer_OBO_Bug36225(String to, String cc, String bcc,
			String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		to = SelNGBase.selfAccountName;
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zAcceptShare(mountingfoldername);
		obj.zFolder.zClick(mountingfoldername);

		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		zWaitTillObjectExist("element", "zv__COMPOSE1_obo_checkbox");
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);
		Thread.sleep(1000);

		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		zWaitTillObjectExist("element", "zv__COMPOSE1_obo_checkbox");
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);
		Thread.sleep(1000);

		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		zWaitTillObjectExist("element", "zv__COMPOSE1_obo_checkbox");
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);
		Thread.sleep(1000);

		needReset = false;
	}

	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachmentBreaksPersona_Bug42346(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		String User1 = selfAccountName;
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				"");
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareCreatedSubject));
		page.zSharing.zAcceptShare(mountingfoldername);

		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addPersona));

		obj.zEditField
				.zType(localize(locator.accountPersonaLabel), "MyPersona");
		selenium.check("//input[contains(@id, '_PERSONA_WHEN_SENT_TO')]");
		selenium.type("//input[contains(@id, '_PERSONA_WHEN_SENT_TO_LIST')]",
				User1);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);

		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences should be saved");

		obj.zFolder.zClick(mountingfoldername);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.reply));
		selenium.isElementPresent("zv__COMPOSE1_obo_checkbox");
		page.zComposeView.zAddAttachments(attachments, false);
		obj.zButton.zClick(localize(locator.send));

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(User1);
		subject = "Re: " + subject;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		selenium.isTextPresent(User1);
		selenium.isTextPresent(invitedusers);

		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 as manager rights and user2
	 * verifies mail deletion (+ve case)
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * manager rights 3.Login to user2 and accept share 4.Click to mounted
	 * folder >> verify mail exists 5.Click to mail and verify Delete toolbar
	 * button remains enabled 6.Right click to mail and verify Delete menu item
	 * remains enabled 7.Delete this mail and verify mail has been deleted
	 * properly
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsManagerAndVerifyMailDeletion_Bug40954(String to,
			String cc, String bcc, String subject, String body,
			String attachments, String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		verifyMailDeletionOnDiffRole(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername);

		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 as admin rights and user2
	 * verifies mail deletion (+ve case)
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * admin rights 3.Login to user2 and accept share 4.Click to mounted folder
	 * >> verify mail exists 5.Click to mail and verify Delete toolbar button
	 * remains enabled 6.Right click to mail and verify Delete menu item remains
	 * enabled 7.Delete this mail and verify mail has been deleted properly
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsAdminAndVerifyMailDeletion_Bug40954(String to,
			String cc, String bcc, String subject, String body,
			String attachments, String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		verifyMailDeletionOnDiffRole(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername);

		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 as admin rights, user2 mounts
	 * this share and shares this folder to user3 as viewer rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * admin rights 3.Login to user2 and accept share 4.Share this folder to
	 * user3 as viewer rights (user3 can also share because it has admin rights)
	 * 5. Login to user3, accepts share folder >> verify mail exists 5.Click to
	 * mail and verify Delete toolbar button remains disabled 6.Right click to
	 * mail and verify Delete menu item remains disabled
	 * 
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsAdminAndAdminSharesFolderto3rduser(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
		page.zSharing.zAcceptShare(mountingfoldername);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zNotExists(localize(locator.shareCreatedSubject));

		String thirdUser = ProvZCS.getRandomAccount();
		page.zSharing.zShareFolder(applicationtab, mountingfoldername,
				sharetype, thirdUser, localize(locator.shareRoleViewer),
				message, sharingnoteifany, allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = thirdUser;
		page.zLoginpage.zLoginToZimbraAjax(thirdUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		page.zSharing.zAcceptShare(mountingfoldername);
		obj.zFolder.zClick(mountingfoldername);
		obj.zButton.zIsDisabled(page.zMailApp.zDeleteBtn);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zIsDisabled(localize(locator.del));
		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 as viewer rights, user2 mounts
	 * this share and shares this folder to user3 as viewer rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * viewer rights 3.Login to user2 and accept share 4.Login to user1 and
	 * revoke share 5.Login to user1 and verify share revoked mail 6.Verify
	 * message not exists in mounted folder because share has been revoked
	 * 
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsViewerAndRevoke(String to, String cc, String bcc,
			String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);
		page.zSharing.zVerifyShareCreatedMailInSentFolder(currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zAcceptShare(mountingfoldername, localize(locator.green),
				localize(locator.sendStandardMailAboutShare), sharingnoteifany);

		resetSession();
		SelNGBase.selfAccountName = currentloggedinuser;
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zSharing.zVerifyShareAcceptedMail(currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
		page.zSharing
				.zRevokeShare(sharingfoldername, message, sharingnoteifany);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zVerifyShareRevokedMail(currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
		obj.zFolder.zClick(mountingfoldername);
		obj.zMessageItem.zNotExists(subject);
		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 as viewer rights, user2 mounts
	 * this share and shares this folder to user3 as viewer rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * viewer rights 3.Login to user2 and accept share 4.Login to user1 and
	 * revoke share 5.Login to user1 and verify share revoked mail 6.Verify
	 * message not exists in mounted folder because share has been revoked
	 * 
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void resendShareInvite(String to, String cc, String bcc,
			String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zAcceptShare(mountingfoldername);

		resetSession();
		SelNGBase.selfAccountName = currentloggedinuser;
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zSharing.zResendShare(sharingfoldername);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		String samemountedfolder = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
		page.zSharing.zAcceptShare(samemountedfolder);
		obj.zFolder.zClick(mountingfoldername);
		obj.zMessageItem.zExists(subject);
		obj.zFolder.zClick(samemountedfolder);
		obj.zMessageItem.zExists(subject);
		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 & user3 as viewer rights and
	 * both user accepts share verifies mail deletion (-ve case)
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 & user3
	 * as viewer rights 3.Login to user2 and accept share 4.Click to mounted
	 * folder >> verify mail exists 5.Click to mail and verify Delete toolbar
	 * button remains disabled 6.Right click to mail and verify Delete menu item
	 * remains disabled. 7.Login to user3, decline share 8.Login to user1 and
	 * verify share declined mail from user3
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsViewerToMultipleUsers(String to, String cc, String bcc,
			String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		String inviteduser1 = ProvZCS.getRandomAccount();
		String inviteduser2 = ProvZCS.getRandomAccount();
		invitedusers = inviteduser1 + ";" + inviteduser2;
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		String[] invitedarray = { inviteduser1, inviteduser2 };
		for (int i = 0; i <= 1; i++) {
			resetSession();
			SelNGBase.selfAccountName = invitedarray[i];
			page.zLoginpage.zLoginToZimbraAjax(invitedarray[i]);
			page.zSharing.zVerifyShareCreatedMailInInboxFolder(
					currentloggedinuser, sharingfoldername, sharetype,
					invitedarray[i], role, sharingnoteifany);
			page.zSharing.zAcceptShare(mountingfoldername);
			obj.zFolder.zClick(mountingfoldername);
			obj.zMessageItem.zClick(subject);
			obj.zButton.zIsDisabled(page.zMailApp.zDeleteBtn);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zIsDisabled(localize(locator.del));
		}
		needReset = false;
	}

	/**
	 * In this test user1 shares folder to user2 & user3 as manager rights and
	 * user2 accepts share and user3 declines share
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 & user3
	 * as manager rights 3.Login to user2 and accept share 4.Click to mounted
	 * folder >> verify mail exists 5.Click to mail and verify Delete toolbar
	 * button remains disabled 6.Right click to mail and verify Delete menu item
	 * remains disabled. 7. Same verification for user3 as well
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsManagerToMultipleUsersAndOneUserDeclinesShare(String to,
			String cc, String bcc, String subject, String body,
			String attachments, String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		String inviteduser1 = ProvZCS.getRandomAccount();
		String inviteduser2 = ProvZCS.getRandomAccount();
		invitedusers = inviteduser1 + ";" + inviteduser2;
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		String[] invitedarray = { inviteduser1, inviteduser2 };
		for (int i = 0; i <= 1; i++) {
			if (i == 0) {
				resetSession();
				SelNGBase.selfAccountName = invitedarray[i];
				page.zLoginpage.zLoginToZimbraAjax(invitedarray[i]);
				page.zSharing.zVerifyShareCreatedMailInInboxFolder(
						currentloggedinuser, sharingfoldername, sharetype,
						invitedarray[i], role, sharingnoteifany);
				page.zSharing.zAcceptShare(mountingfoldername);
				obj.zFolder.zClick(mountingfoldername);
				obj.zMessageItem.zClick(subject);
				obj.zButton.zIsEnabled(page.zMailApp.zDeleteBtn);
				obj.zMessageItem.zRtClick(subject);
				obj.zMenuItem.zIsEnabled(localize(locator.del));
			} else if (i == 1) {
				resetSession();
				SelNGBase.selfAccountName = invitedarray[i];
				page.zLoginpage.zLoginToZimbraAjax(invitedarray[i]);
				page.zSharing.zVerifyShareCreatedMailInInboxFolder(
						currentloggedinuser, sharingfoldername, sharetype,
						invitedarray[i], role, sharingnoteifany);
				page.zSharing.zDeclineShare();

				resetSession();
				SelNGBase.selfAccountName = currentloggedinuser;
				page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
				page.zSharing.zVerifyShareDeclinedMailInInboxFolder(
						currentloggedinuser, sharingfoldername, sharetype,
						invitedarray[i], role, sharingnoteifany);
			}
		}
		needReset = false;
	}

	private void verifyMailDeletionOnDiffRole(String to, String cc, String bcc,
			String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareCreatedSubject));
		page.zSharing.zAcceptShare(mountingfoldername);
		obj.zFolder.zClick(mountingfoldername);

		if (role.equals(localize(locator.shareRoleAdmin))
				|| role.equals(localize(locator.shareRoleManager))) {
			obj.zMessageItem.zClick(subject);
			obj.zButton.zIsEnabled(page.zMailApp.zDeleteBtn);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zIsEnabled(localize(locator.del));

			// moving message from shared folder to personal folder
			obj.zMessageItem.zClick(subject);
			obj.zButton.zClick(page.zMailApp.zMoveIconBtn);
			obj.zFolder.zClickInDlgByName(localize(locator.inbox),
					localize(locator.moveMessage));
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.moveMessage));
			Thread.sleep(1500);
			obj.zFolder.zClick(mountingfoldername);
			String msgExists = obj.zMessageItem.zExistsDontWait(subject);
			assertReport("false", msgExists, "Moved message still exists");
			obj.zFolder.zClick(page.zMailApp.zInboxFldr);
			Thread.sleep(1000);
			msgExists = obj.zMessageItem.zExistsDontWait(subject);
			assertReport("true", msgExists, "Moved message not exists");

			// moving message from shared folder to personal folder
			obj.zMessageItem.zClick(subject);
			obj.zButton.zClick(page.zMailApp.zMoveIconBtn);
			obj.zFolder.zClickInDlgByName(mountingfoldername,
					localize(locator.moveMessage));
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.moveMessage));
			Thread.sleep(1500);
			obj.zFolder.zClick(page.zMailApp.zInboxFldr);
			msgExists = obj.zMessageItem.zExistsDontWait(subject);
			assertReport("false", msgExists, "Moved message still exists");
			obj.zFolder.zClick(mountingfoldername);
			Thread.sleep(1000);
			msgExists = obj.zMessageItem.zExistsDontWait(subject);
			assertReport("true", msgExists, "Moved message not exists");
		} else if (role.equals(localize(locator.shareRoleViewer))) {
			obj.zButton.zIsDisabled(page.zMailApp.zDeleteBtn);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zIsDisabled(localize(locator.del));
		}
	}

	/**
	 * Test Case:-When send message as a delegated sender, if the message is
	 * saved within the draft folder the owner of the message does not get a
	 * copy Steps: User A shares inbox to User B User C sends message to User A
	 * User B reply to User C from User A share folder (During the reply, the
	 * message is saved to the draft folder) Verify User C receives a reply and
	 * User A does receive a copy within the sent folder.
	 * 
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @param applicationtab
	 * @param sharingfoldername
	 * @param sharetype
	 * @param invitedusers
	 * @param role
	 * @param message
	 * @param sharingnoteifany
	 * @param allowtoseeprivateappt
	 * @param mountingfoldername
	 * @throws Exception
	 * @author Girish
	 */

	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void CheckOwnerOfMessageDoesGetACopyInSentFolder_30451(String to,
			String cc, String bcc, String subject, String body,
			String attachments, String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		String inviteduser2 = ProvZCS.getRandomAccount();
		String inviteduser3 = ProvZCS.getRandomAccount();

		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, inviteduser2, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		// user3 send mail to user1
		resetSession();
		SelNGBase.selfAccountName = inviteduser3;
		page.zLoginpage.zLoginToZimbraAjax(inviteduser3);
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(currentloggedinuser, cc, bcc,
				subject, body, attachments);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		Thread.sleep(5000);

		resetSession();
		SelNGBase.selfAccountName = inviteduser2;
		page.zLoginpage.zLoginToZimbraAjax(inviteduser2);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				sharingfoldername, sharetype, inviteduser2, role,
				sharingnoteifany);
		page.zSharing.zAcceptShare(mountingfoldername);
		obj.zFolder.zClick(mountingfoldername);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		obj.zButton.zClick(ComposeView.zSendIconBtn);

		resetSession();
		SelNGBase.selfAccountName = inviteduser3;
		page.zLoginpage.zLoginToZimbraAjax(inviteduser3);
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
						subject);
		Thread.sleep(5000);

		resetSession();
		SelNGBase.selfAccountName = currentloggedinuser;
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);

		MailApp.ClickCheckMailUntilMailShowsUp(page.zMailApp.zSentFldr, "Re: "
				+ subject);

		needReset = false;
	}

	/**
	 * The order below must be follwed. Log in to 3 accounts: user1, user2, and
	 * user3. user3: share Calendar to user2 with Manager rights user3: share
	 * Calendar to user1 with Admin rights user2: accept share user1: accept
	 * share user1: go to Calendar app, right-click mountpoint and select "Edit
	 * Properties" user1: log out, log in, and try step 6
	 * 
	 *The dialog does show Admin in the list of Permissions along with list the
	 * shares. The shares are listed, since user1 has Admin rights.
	 * 
	 *Then logged in with the user2: accept share user2: go to Calendar app,
	 * right-click mountpoint and select "Edit Properties"
	 * 
	 *The dialog does NOT show the list of Permissions, nor does it list the
	 * shares. The shares should be listed, since user2 has Manager rights.
	 * 
	 *In short For the Manager rights ,It should display Persmission:-View,
	 * Edit, Add, Remove, Accept, Decline
	 * 
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @param applicationtab
	 * @param sharingfoldername
	 * @param sharetype
	 * @param invitedusers
	 * @param role
	 * @param message
	 * @param sharingnoteifany
	 * @param allowtoseeprivateappt
	 * @param mountingfoldername
	 * @throws Exception
	 * @author Girish
	 */

	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkGranteesAdminAndManagerPermissionOnTheUI_31242(String to,
			String cc, String bcc, String subject, String body,
			String attachments, String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;

		page.zCalApp.zNavigateToCalendar();
		obj.zCalendarFolder.zExists(localize(locator.calendar));
		String inviteduser2 = ProvZCS.getRandomAccount();
		String inviteduser3 = ProvZCS.getRandomAccount();

		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, inviteduser2, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		Thread.sleep(5000);

		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, inviteduser3, localize(locator.shareRoleManager),
				message, sharingnoteifany, allowtoseeprivateappt);
		Thread.sleep(5000);

		resetSession();
		SelNGBase.selfAccountName = inviteduser2;
		page.zLoginpage.zLoginToZimbraAjax(inviteduser2);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				sharingfoldername, sharetype, inviteduser2, role,
				sharingnoteifany);
		page.zSharing.zAcceptShare(mountingfoldername);
		zGoToApplication("Calendar");
		obj.zFolder.zRtClick(mountingfoldername);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		obj.zDialog.zExists(localize(locator.folderProperties));
		String admin = localize(locator.shareActionWorkflow);
		String admins[] = admin.split(",");
		// use tokinize here to seperate accept and ecline
		Assert
				.assertTrue(selenium
						.isElementPresent("xpath=//td[contains(@class,'Label') and contains(text(),'"
								+ localize(locator.permissions) + "')]"));

		Assert
				.assertTrue(selenium
						.isElementPresent("xpath=//td[contains(@class,'Field')]/div[contains(text(),'"
								+ localize(locator.shareActionRead)
								+ ", "
								+ localize(locator.shareActionWrite)
								+ ", "
								+ localize(locator.shareActionInsert)
								+ ", "
								+ localize(locator.shareActionDelete)
								+ ", "
								+ admins[0]
								+ ", "
								+ admins[1].trim()
								+ ", "
								+ localize(locator.shareActionAdmin) + "')]"));

		resetSession();
		SelNGBase.selfAccountName = inviteduser3;
		page.zLoginpage.zLoginToZimbraAjax(inviteduser3);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				sharingfoldername, sharetype, inviteduser3,
				localize(locator.shareRoleManager), sharingnoteifany);
		page.zSharing.zAcceptShare(mountingfoldername);
		zGoToApplication("Calendar");
		obj.zFolder.zRtClick(mountingfoldername);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		obj.zDialog.zExists(localize(locator.folderProperties));
		String mgr = localize(locator.shareActionWorkflow);
		String mangr[] = mgr.split(",");
		// use tokinize here to seperate accept and decline
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@class,'Label') and contains(text(),'"
										+ localize(locator.permissions) + "')]"),
						"Permission Label doesn't present for Admin Rights");
		// Assert.assertTrue(selenium.isElementPresent(
		// "xpath=//td[contains(@class,'Label') and contains(text(),'"
		// +localize(locator.permissions)+"')]"));
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@class,'Field')]/div[contains(text(),'"
										+ localize(locator.shareActionRead)
										+ ", "
										+ localize(locator.shareActionWrite)
										+ ", "
										+ localize(locator.shareActionInsert)
										+ ", "
										+ localize(locator.shareActionDelete)
										+ ", "
										+ mangr[0]
										+ ", "
										+ mangr[1].trim() + "')]"),
						"View, Edit, Add, Remove, Accept, Decline not showing for Admin Rights");

		needReset = false;
	}

	/**
	 *Test Case:- Warning for deleting mount point from trash incorrect Steps:-
	 * Delete mountpoint Right click on mountpoint in Trash Delete You get a
	 * warning "Do you want to permanently delete everything inside this
	 * folder?" that's wrong. Deleting mountpoints just deletes the mountpoint,
	 * not the content in the folder. It should show the msg as
	 * "Are you sure you want to permanently delete the "Namexxx" folder?
	 * 
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @param applicationtab
	 * @param sharingfoldername
	 * @param sharetype
	 * @param invitedusers
	 * @param role
	 * @param message
	 * @param sharingnoteifany
	 * @param allowtoseeprivateappt
	 * @param mountingfoldername
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkWarningMsgForDeletingMountPtFromTrash_bug42082(String to,
			String cc, String bcc, String subject, String body,
			String attachments, String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(currentloggedinuser, cc,
				bcc, subject, body, attachments);

		String inviteduser2 = ProvZCS.getRandomAccount();

		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, inviteduser2, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName = inviteduser2;
		page.zLoginpage.zLoginToZimbraAjax(inviteduser2);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				sharingfoldername, sharetype, inviteduser2, role,
				sharingnoteifany);
		page.zSharing.zAcceptShare(mountingfoldername);
		obj.zFolder.zRtClick(mountingfoldername);
		obj.zMenuItem.zClick(localize(locator.del));
		Thread.sleep(1000);
		obj.zFolder.zRtClick(mountingfoldername);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zDialog.zExists(localize(locator.warningMsg));
		String msg = obj.zDialog.zGetMessage(localize(locator.warningMsg));
		Assert.assertTrue(msg.contains(localize(locator.confirmDeleteFolder,
				mountingfoldername, "")), "Showing wrong warning msg");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.warningMsg));
		Thread.sleep(1500);
		obj.zFolder.zNotExists(mountingfoldername);

		needReset = false;
	}

	/**
	 * Test case :SearchConvRequest:mail.NO_SUCH_MSG' exception while moving
	 * messages from remote/share folder to local & vice versa
	 * 
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @param applicationtab
	 * @param sharingfoldername
	 * @param sharetype
	 * @param invitedusers
	 * @param role
	 * @param message
	 * @param sharingnoteifany
	 * @param allowtoseeprivateappt
	 * @param mountingfoldername
	 * @throws Exception
	 * @author Girish
	 */

	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ExceptionWhileMovingMailsFromSharedToLocalFolder_Bug45034(
			String to, String cc, String bcc, String subject, String body,
			String attachments, String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(currentloggedinuser, cc,
				bcc, subject, body, attachments);
		Thread.sleep(1000);
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(currentloggedinuser, cc,
				bcc, "subj2", body, attachments);
		String inviteduser2 = ProvZCS.getRandomAccount();
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, inviteduser2, role, message, sharingnoteifany,
				allowtoseeprivateappt);
		resetSession();
		SelNGBase.selfAccountName = inviteduser2;
		page.zLoginpage.zLoginToZimbraAjax(inviteduser2);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				sharingfoldername, sharetype, inviteduser2, role,
				sharingnoteifany);
		page.zSharing.zAcceptShare(mountingfoldername);
		obj.zFolder.zClick(mountingfoldername);
		obj.zMessageItem.zClick(subject);
		obj.zCheckbox.zClick("id=zlhi__CLV__se");
		zDragAndDrop(
				"xpath=//div[contains(@id,'zl__CLV__rows')]/div[contains(@class,'Row RowEven')]/table//tr/td/div[contains(@id,'zlif__CLV') and contains (@class,'ImgCheckboxChecked')]",
				"class=ImgInbox");
		String toastmsg = localize(locator.actionMove, "2", localize(
				locator.conversations).toLowerCase(), localize(locator.inbox));
		Thread.sleep(900);
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			obj.zToastAlertMessage
					.zAlertMsgExists(toastmsg,
							"conversations does not moved or draged into required folder");
		}
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zExists(subject);

		needReset = false;
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