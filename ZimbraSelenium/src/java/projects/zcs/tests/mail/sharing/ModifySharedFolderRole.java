package projects.zcs.tests.mail.sharing;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.Stafzmprov;

import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
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
 *            - Email id to whom folder to be shared - as of now it is random
 *            account created by Stafzmprov.getRandomAccount() method
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
public class ModifySharedFolderRole extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "SharingDataProvider")
	protected Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("changingSharedFolderViewertoNone")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("changingSharedFolderManagertoNone")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.sent), "", Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleManager),
					localize(locator.sendStandardMailAboutSharePlusNote),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("changingSharedFolderAdmintoNone")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleAdmin), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("changingSharedFolderViewertoManager")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("changingSharedFolderManagertoViewer")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleManager), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("changingSharedFolderViewertoAdmin")) {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "_selfAccountName_", "", "",
					getLocalizedData(2), getLocalizedData(5), "", "Mail",
					localize(locator.inbox), "", Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleAdmin), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="mail";
		super.zLogin();
	}
	
	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * In this test user1 shares folder to user2 as viewer rights, after user1
	 * changes back to none rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * viewer rights 3.Login to user2 and accept share 4.Login to user1 and
	 * modify share as none rights 5.Login to user2 and verify share modified
	 * mail (by verifying message body) 6.Click to shared folder and verify no
	 * message exists because share has been modified as none rights
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void changingSharedFolderViewertoNone(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080",
				"'Share Accepted' & 'Share Modified' mail body missing sharing details");

		changeRoletoDiff(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername, localize(locator.shareRoleNone));

		SelNGBase.needReset.set(false);
	}

	/**
	 * In this test user1 shares folder to user2 as manager rights, after user1
	 * changes back to none rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * manager rights 3.Login to user2 and accept share 4.Login to user1 and
	 * modify share as none rights 5.Login to user2 and verify share modified
	 * mail (by verifying message body) 6.Click to shared folder and verify no
	 * message exists because share has been modified as none rights
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void changingSharedFolderManagertoNone(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080",
				"'Share Accepted' & 'Share Modified' mail body missing sharing details");

		changeRoletoDiff(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername, localize(locator.shareRoleNone));

		SelNGBase.needReset.set(false);
	}

	/**
	 * In this test user1 shares folder to user2 as admin rights, after user1
	 * changes back to none rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * admin rights 3.Login to user2 and accept share 4.Login to user1 and
	 * modify share as none rights 5.Login to user2 and verify share modified
	 * mail (by verifying message body) 6.Click to shared folder and verify no
	 * message exists because share has been modified as none rights 7.Right
	 * click to mounted folder and verify "Share Folder" menu item disabled for
	 * manager rights (now right as None)
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void changingSharedFolderAdmintoNone(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080",
				"'Share Accepted' & 'Share Modified' mail body missing sharing details");

		changeRoletoDiff(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername, localize(locator.shareRoleNone));

		SelNGBase.needReset.set(false);
	}

	/**
	 * In this test user1 shares folder to user2 as viewer rights, after user1
	 * changes to manager rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * viewer rights 3.Login to user2 and accept share 4.Login to user1 and
	 * modify share as manager rights 5.Login to user2 and verify share modified
	 * mail (by verifying message body) 6.Click to shared folder >> verify mail
	 * exists 7.Click to mail and verify Delete toolbar button remains enabled
	 * 8.Right click to mail and verify Delete menu item remains enabled 9.(-ve
	 * case) Right click to shared folder and verify "Share Folder" menu item
	 * remains disabled (this will enabled only for Admin rights)
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void changingSharedFolderViewertoManager(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080",
				"'Share Accepted' & 'Share Modified' mail body missing sharing details");

		changeRoletoDiff(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername, localize(locator.shareRoleManager));

		SelNGBase.needReset.set(false);
	}

	/**
	 * In this test user1 shares folder to user2 as manager rights, after user1
	 * changes to viewer rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * manager rights 3.Login to user2 and accept share 4.Login to user1 and
	 * modify share as viewer rights 5.Login to user2 and verify share modified
	 * mail (by verifying message body) 6.Click to shared folder >> verify mail
	 * exists 7.Click to mail and verify Delete toolbar button remains disabled
	 * 8.Right click to mail and verify Delete menu item remains disabled 9.(-ve
	 * case) Right click to shared folder and verify "Share Folder" menu item
	 * remains disabled (this will enabled only for Admin rights)
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void changingSharedFolderManagertoViewer(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080",
				"'Share Accepted' & 'Share Modified' mail body missing sharing details");

		changeRoletoDiff(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername, localize(locator.shareRoleViewer));

		SelNGBase.needReset.set(false);
	}

	/**
	 * In this test user1 shares folder to user2 as viewer rights, after user1
	 * changes to admin rights
	 * 
	 * 1.Login to user1 and send mail to himself 2.Share folder to user2 as
	 * viewer rights 3.Login to user2 and accept share 4.Login to user1 and
	 * modify share as admin rights 5.Login to user2 and verify share modified
	 * mail (by verifying message body) 6.Click to shared folder >> verify mail
	 * exists 7.Click to mail and verify Delete toolbar button remains enabled
	 * 8.Right click to mail and verify Delete menu item remains enabled 9.Right
	 * click to shared folder and verify "Share Folder" menu item remains
	 * enabled (this will enabled only for Admin rights)
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void changingSharedFolderViewertoAdmin(String to, String cc,
			String bcc, String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080",
				"'Share Accepted' & 'Share Modified' mail body missing sharing details");

		changeRoletoDiff(to, cc, bcc, subject, body, attachments,
				applicationtab, sharingfoldername, sharetype, invitedusers,
				role, message, sharingnoteifany, allowtoseeprivateappt,
				mountingfoldername, localize(locator.shareRoleAdmin));

		SelNGBase.needReset.set(false);
	}

	private void changeRoletoDiff(String to, String cc, String bcc,
			String subject, String body, String attachments,
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername, String newRole) throws Exception {

		String currentloggedinuser = SelNGBase.selfAccountName.get();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName.set(invitedusers);
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zAcceptShare(mountingfoldername);

		resetSession();
		SelNGBase.selfAccountName.set(currentloggedinuser);
		page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
		page.zSharing.zModifySharedFolder(applicationtab, sharingfoldername,
				newRole, message, sharingnoteifany, allowtoseeprivateappt);

		resetSession();
		SelNGBase.selfAccountName.set(invitedusers);
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zVerifyShareModifiedMail(currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, newRole,
				sharingnoteifany);

		if (newRole.equals(localize(locator.shareRoleAdmin))
				|| newRole.equals(localize(locator.shareRoleManager))) {
			obj.zFolder.zClick(mountingfoldername);
			obj.zMessageItem.zClick(subject);
			obj.zButton.zIsEnabled(page.zMailApp.zDeleteBtn);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zIsEnabled(localize(locator.del));
		} else if (newRole.equals(localize(locator.shareRoleViewer))) {
			obj.zFolder.zClick(mountingfoldername);
			obj.zMessageItem.zClick(subject);
			obj.zButton.zIsDisabled(page.zMailApp.zDeleteBtn);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zIsDisabled(page.zMailApp.zDeleteBtn);
		} else if (newRole.equals(localize(locator.shareRoleNone))) {
			obj.zMessageItem.zNotExists(subject);
		}

		if (newRole.equals(localize(locator.shareRoleAdmin))) {
			obj.zFolder.zRtClick(mountingfoldername);
			obj.zMenuItem.zIsEnabled(localize(locator.shareFolder));
		}
	}
}