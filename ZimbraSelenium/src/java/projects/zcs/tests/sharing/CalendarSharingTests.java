package projects.zcs.tests.sharing;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 * 
 *         Class contains 4 methods (12-13 test internally) regarding 1.share as
 *         viewer and verify appt & also covers change role to none 2.share as
 *         manager and verify appt & also covers delete shared appt from parent
 *         folder 3.share as admin and verify appt & also covers delete shared
 *         appt from parent folder 4.share as manager, verifying private
 *         appointment functionality & also covers delete shared appt from
 *         parent folder
 * 
 *         Below parameter used to pass values from data provider
 * 
 * @param applicationtab
 *            - Mail, Address Book or any other application tab from which you
 *            want to share folder
 * @param sharingfoldername
 *            - Folder to be shared
 * @param sharetype
 *            - Either Internal, External or public
 * @param invitedusers
 *            - Email id to whom folder to be shared - as of now it is random
 *            account created by ProvZCS.getRandomAccount() method
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
public class CalendarSharingTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "SharingDataProvider")
	protected Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("shareAsViewerVerifyApptAndChangeRoleToNone")) {
			return new Object[][] { { "Calendar", localize(locator.calendar),
					"", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsManagerAndCreateUpdateDelApptVerify")) {
			return new Object[][] { { "Calendar", localize(locator.calendar),
					"", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleManager), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("shareAsAdminAndCreateUpdateDelApptVerify")) {
			return new Object[][] { { "Calendar", localize(locator.calendar),
					"", ProvZCS.getRandomAccount(),
					localize(locator.shareRoleAdmin), "", "", "",
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("allowToSeePrivateApptWithManagerRights")) {
			return new Object[][] { { "Calendar",
					getLocalizedData_NoSpecialChar(), "",
					ProvZCS.getRandomAccount(),
					localize(locator.shareRoleViewer), "", "",
					localize(locator.privatePermission),
					getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "Calendar", localize(locator.calendar),
					"", ProvZCS.getRandomAccount(),
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
		page.zCalApp.zNavigateToCalendar();
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
	 * In this test user1 shares calendar folder to user2 as viewer rights and
	 * user2 verifies appt deletion (-ve case). Also covers changing role to
	 * none
	 * 
	 * 1.Login to user1 and create appt 2.Share folder to user2 as viewer rights
	 * 3.Login to user2 and accept share 4.Click to mounted folder >> verify
	 * calendar exists 5.Click to appt and verify Delete toolbar button remains
	 * disabled 6.Right click to appt and verify Delete menu item &
	 * corresponding item remains disabled 7.Change role to none and check no
	 * appointment exists
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsViewerVerifyApptAndChangeRoleToNone(
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		verifyApptOnDiffRole(applicationtab, sharingfoldername, sharetype,
				invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt, mountingfoldername);

		needReset = false;
	}

	/**
	 * In this test user1 shares calendar folder to user2 as manager rights and
	 * user2 verifies appt related validations. Also covers updating subject for
	 * the appointment for manager right & deleting appointment.
	 * 
	 * 1.Login to user1 and create appt 2.Share folder to user2 as manager
	 * rights 3.Login to user2 and accept share 4.Click to mounted folder >>
	 * verify appt exists 5.Click to appt and verify Delete toolbar button
	 * remains enabled 6.Right click to appt and verify Delete menu item &
	 * corresponding item remains enabled 7.Open appt and update it 8.Verify its
	 * new subject 9.Create new appt in shared calendar 10.Delete appointment
	 * (because it has manager rights)
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsManagerAndCreateUpdateDelApptVerify(
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		verifyApptOnDiffRole(applicationtab, sharingfoldername, sharetype,
				invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt, mountingfoldername);

		needReset = false;
	}

	/**
	 * In this test user1 shares calendar folder to user2 as admin rights and
	 * user2 verifies appt related validations. Also covers updating subject for
	 * the appointment for manager right & deleting appointment.
	 * 
	 * 1.Login to user1 and create appt 2.Share folder to user2 as admin rights
	 * 3.Login to user2 and accept share 4.Click to mounted folder >> verify
	 * appt exists 5.Click to appt and verify Delete toolbar button remains
	 * enabled 6.Right click to appt and verify Delete menu item & corresponding
	 * item remains enabled 7.Open appt and update it 8.Verify its new subject
	 * 9.Create new appt in shared calendar 10.Delete appointment (because it
	 * has admin rights)
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareAsAdminAndCreateUpdateDelApptVerify(String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		verifyApptOnDiffRole(applicationtab, sharingfoldername, sharetype,
				invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt, mountingfoldername);

		needReset = false;
	}

	/**
	 * In this test user1 shares calendar folder to user2 as admin rights with
	 * private appt access also and user2 verifies appt related validations.
	 * Also covers updating subject for the appointment for manager right &
	 * deleting appointment.
	 * 
	 * 1.Login to user1 and create appt (with private appt access) 2.Share
	 * folder to user2 as admin rights 3.Login to user2 and accept share 4.Click
	 * to mounted folder >> verify appt exists 5.Click to appt and verify Delete
	 * toolbar button remains enabled 6.Right click to appt and verify Delete
	 * menu item & corresponding item remains enabled 7.Open appt and update it
	 * 8.Verify its new subject 9.Create new appt in shared calendar 10.Delete
	 * appointment (because it has admin rights)
	 */
	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "test" }, retryAnalyzer = RetryFailedTests.class)
	public void allowToSeePrivateApptWithManagerRights(String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		verifyApptOnDiffRole(applicationtab, sharingfoldername, sharetype,
				invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt, mountingfoldername);

		needReset = false;
	}

	@Test(dataProvider = "SharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void readOnlyCalenar_shoulnotBeVisible_inCreateAppt_Bug37103(
			String applicationtab, String sharingfoldername, String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt,
			String mountingfoldername) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String account = ProvZCS.getRandomAccount();
		String calendarName = getLocalizedData_NoSpecialChar();

		page.zCalApp.zCreateNewCalendarFolder(calendarName);
		page.zSharing.zShareFolder(applicationtab, calendarName, "", account,
				localize(locator.shareRoleViewer), "", "", "");

		zKillBrowsers();

		SelNGBase.selfAccountName = account;
		page.zLoginpage.zLoginToZimbraAjax(account);
		page.zSharing.zAcceptShare(calendarName);

		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		obj.zFeatureMenu.zNotExists(localize(locator.calendar));
		page.zCalCompose.zCreateSimpleAppt("full", "", "", "");

		needReset = false;
	}

	private void verifyApptOnDiffRole(String applicationtab,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String message, String sharingnoteifany,
			String allowtoseeprivateappt, String mountingfoldername)
			throws Exception {

		String apptSubject = getLocalizedData_NoSpecialChar();
		String apptLocation = getLocalizedData_NoSpecialChar();
		String apptAttendees = "";
		String apptBody = getLocalizedData_NoSpecialChar();
		String newSubject = getLocalizedData_NoSpecialChar();

		String currentloggedinuser = SelNGBase.selfAccountName;
		if (allowtoseeprivateappt.equals(localize(locator.privatePermission))) {
			page.zCalApp.zCreateNewCalendarFolder(sharingfoldername);
			zWaitTillObjectExist("folder", sharingfoldername);
			page.zCalCompose.zCreateAppt(apptSubject, apptLocation, "",
					localize(locator._private), sharingfoldername, "", "", "",
					"", "", "", "", apptAttendees, apptBody);

		} else {
			page.zCalCompose.zCreateSimpleAppt(apptSubject, apptLocation,
					apptAttendees, apptBody);
		}

		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		obj.zAppointment.zExists(apptSubject);

		page.zSharing.zShareFolder(applicationtab, sharingfoldername,
				sharetype, invitedusers, role, message, sharingnoteifany,
				allowtoseeprivateappt);
		/*
		 * test retries for manager rights in some locales (this method is
		 * common for all role though retries only for manager rights - not
		 * getting exact reason, putting little sleep after sharing folder only
		 * for manager right test
		 */

		if (role.equals(localize(locator.shareRoleManager))) {
			Thread.sleep(2000);
		}

		zKillBrowsers();
		SelNGBase.selfAccountName = invitedusers;
		page.zLoginpage.zLoginToZimbraAjax(invitedusers);
		page.zSharing.zAcceptShare(mountingfoldername);
		zGoToApplication(applicationtab);
		Thread.sleep(1000);
		obj.zFolderCheckbox.zActivate(localize(locator.calendar));
		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		Thread.sleep(1000);
		obj.zAppointment.zExists(apptSubject);
		if (role.equals(localize(locator.shareRoleAdmin))
				|| role.equals(localize(locator.shareRoleManager))) {
			obj.zAppointment.zClick(apptSubject);
			obj.zButton.zIsEnabled(page.zCalApp.zCalDelete);
			obj.zAppointment.zRtClick(apptSubject);
			obj.zMenuItem.zIsEnabled(localize(locator.viewAppointment));
			obj.zMenuItem.zIsDisabled(localize(locator.accept));
			if (!config.getString("locale").equals("pt_BR")
					&& !config.getString("locale").equals("pl")
					&& !config.getString("locale").equals("hi")) {
				obj.zMenuItem.zIsDisabled(localize(locator.tentative));
			}
			obj.zMenuItem.zIsDisabled(localize(locator.replyDecline));
			obj.zMenuItem.zIsDisabled(localize(locator.editReply));
			obj.zMenuItem.zIsEnabled(localize(locator.del));
			obj.zMenuItem.zIsDisabled(localize(locator.tagAppt));
			obj.zMenuItem.zClick(localize(locator.viewAppointment));
			zWaitTillObjectExist("editfield",
					getNameWithoutSpace(localize(locator.subjectLabel)));
			obj.zEditField.zType(
					getNameWithoutSpace(localize(locator.subjectLabel)),
					newSubject);
			Thread.sleep(500);
			obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
			zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
			obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
			obj.zAppointment.zExists(newSubject);
			/*
			 * Below is additional test & added for manager and admin role >> it
			 * deletes appointment and check appointment doesn't exist by login
			 * to parent user
			 */
			obj.zAppointment.zClick(newSubject);
			obj.zButton.zClick(page.zCalApp.zCalDelete);
			obj.zDialog.zExists(localize(locator.confirmTitle));
			obj.zButton.zClickInDlgByName(localize(locator.yes),
					localize(locator.confirmTitle));
			obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
			obj.zAppointment.zNotExists(newSubject);
			Thread.sleep(1000);
			page.zCalCompose.zCreateSimpleApptInCalendar(apptSubject,
					apptLocation, apptAttendees, apptBody, mountingfoldername);

			obj.zAppointment.zExists(apptSubject);

			zKillBrowsers();
			SelNGBase.selfAccountName = currentloggedinuser;
			page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
			zGoToApplication(applicationtab);
			obj.zAppointment.zNotExists(newSubject);
			obj.zAppointment.zExists(apptSubject);
		} else if (role.equals(localize(locator.shareRoleViewer))) {
			if (allowtoseeprivateappt
					.equals(localize(locator.privatePermission))) {
				/*
				 * Below is additional test & added for manager >> it tries to
				 * delete private appointment and check permission denied
				 * message box exist
				 */
				obj.zAppointment.zClick(apptSubject);
				obj.zButton.zIsDisabled(page.zCalApp.zCalDelete);
				obj.zAppointment.zRtClick(apptSubject);
				obj.zMenuItem.zIsEnabled(localize(locator.viewAppointment));
				obj.zMenuItem.zIsDisabled(localize(locator.accept));
				if (!config.getString("locale").equals("pt_BR")
						&& !config.getString("locale").equals("pl")
						&& !config.getString("locale").equals("hi")) {
					obj.zMenuItem.zIsDisabled(localize(locator.tentative));
				}
				obj.zMenuItem.zIsDisabled(localize(locator.replyDecline));
				obj.zMenuItem.zIsDisabled(localize(locator.editReply));
				obj.zMenuItem.zIsDisabled(localize(locator.del));
				obj.zMenuItem.zIsDisabled(localize(locator.tagAppt));

				/*
				 * (-ve case) don't allow to see private appointment and verify
				 * its corresponding UI
				 */
				zKillBrowsers();
				SelNGBase.selfAccountName = currentloggedinuser;
				page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
				zGoToApplication(applicationtab);
				page.zSharing.zModifySharedFolder(applicationtab,
						sharingfoldername, localize(locator.shareRoleManager),
						message, sharingnoteifany, "");

				zKillBrowsers();
				SelNGBase.selfAccountName = invitedusers;
				page.zLoginpage.zLoginToZimbraAjax(invitedusers);
				zGoToApplication(applicationtab);
				obj.zAppointment.zClick("11");
				obj.zButton.zClick(page.zCalApp.zCalDelete);
				obj.zDialog.zExists(localize(locator.confirmTitle));
				obj.zButton.zClickInDlgByName(localize(locator.yes),
						localize(locator.confirmTitle));
				obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
				obj.zAppointment.zNotExists(apptSubject);
			} else {
				obj.zAppointment.zClick(apptSubject);
				obj.zButton.zIsDisabled(page.zCalApp.zCalDelete);
				obj.zAppointment.zRtClick(apptSubject);
				obj.zMenuItem.zIsEnabled(localize(locator.viewAppointment));
				obj.zMenuItem.zIsDisabled(localize(locator.accept));
				if (!config.getString("locale").equals("pt_BR")
						&& !config.getString("locale").equals("pl")
						&& !config.getString("locale").equals("hi")) {
					obj.zMenuItem.zIsDisabled(localize(locator.tentative));
				}
				obj.zMenuItem.zIsDisabled(localize(locator.replyDecline));
				obj.zMenuItem.zIsDisabled(localize(locator.editReply));
				obj.zMenuItem.zIsDisabled(localize(locator.del));
				obj.zMenuItem.zIsDisabled(localize(locator.tagAppt));
				obj.zMenuItem.zClick(localize(locator.viewAppointment));
				obj.zButton.zClick(localize(locator.close));

				/*
				 * Below is additional test & added into
				 * "shareAsViewerAndVerifyAppt" test itself. >> it modifies
				 * share to none and check appointment doesn't exist
				 */
				zKillBrowsers();
				SelNGBase.selfAccountName = currentloggedinuser;
				page.zLoginpage.zLoginToZimbraAjax(currentloggedinuser);
				zGoToApplication(applicationtab);
				page.zSharing.zModifySharedFolder(applicationtab,
						sharingfoldername, localize(locator.shareRoleNone),
						message, sharingnoteifany, allowtoseeprivateappt);

				zKillBrowsers();
				SelNGBase.selfAccountName = invitedusers;
				page.zLoginpage.zLoginToZimbraAjax(invitedusers);
				zGoToApplication(applicationtab);
				obj.zAppointment.zNotExists(apptSubject);
			}
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