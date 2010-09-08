package projects.zcs.tests.calendar.appointments;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.CalCompose;
import projects.zcs.ui.MailApp;

@SuppressWarnings({ "static-access", "unused" })
public class ForwardAppointment extends CommonTest {
	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("forwardAppt")
				|| test.equals("forwardApptInviteForMultivalue")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3) } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="calendar";
		super.zLogin();
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardAppt(String subject, String location, String attendees,
			String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String newBody = "ForwardAppt_BodyUpdated";
		String thirdUser = Stafzmprov.getRandomAccount();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName.set(attendees);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.accept));
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zRtClick(subject);
		obj.zMenuItem.zClick(localize(locator.forward));
		zWaitTillObjectExist("editfield", localize(locator.subjectLabel));
		// obj.zEditField.zIsDisabled(localize(locator.subjectLabel));
		// obj.zEditField.zIsDisabled(localize(locator.location));
		// obj.zButton.zIsDisabled(localize(locator.busy));
		// obj.zButton.zIsDisabled(localize(locator._public));
		// obj.zButton.zIsDisabled(localize(locator.calendarLabel));
		// obj.zCheckbox.zIsDisabled(localize(locator.allDayEvent));
		// obj.zEditField.zIsDisabled(localize(locator.start));
		// obj.zEditField.zIsDisabled(localize(locator.end));
		// obj.zButton.zIsDisabled(localize(locator.none));
		// obj.zButton.zIsDisabled(localize(locator.reminderLabel));
		// obj.zEditField.zIsDisabled(localize(locator.attendeesLabel));
		obj.zEditor.zType(newBody);
		obj.zTextAreaField
				.zType("xpath=//td[contains(@id,'_to_control')]//textarea[contains(@id,'DWT')]",
						thirdUser);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(thirdUser);
		SelNGBase.selfAccountName.set(thirdUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.accept));
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test case:zimbraPrefCalendarForwardInvitesTo doesn't work for multivalue
	 * Steps: 1. Go to Preferences -> Calendar -> Forward my invites to: and add
	 * 2 mail id here. 2. Invite above user to a meeting. and see all the
	 * invitation mails went to those 2 users also.
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardApptInviteForMultivalue(String subject, String location,
			String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentLoggedInUser = SelNGBase.selfAccountName.get();
		String user1 = Stafzmprov.getRandomAccount();
		String user2 = Stafzmprov.getRandomAccount();
		String user3 = Stafzmprov.getRandomAccount();
		String currentloggedInUser = currentLoggedInUser.toLowerCase();

		zGoToApplication("Preferences");
		zGoToPreferences("Calendar");
		obj.zTextAreaField.zType(
				"xpath=//input[contains(@id,'_CAL_INV_FORWARDING_ADDRESS')]",
				user2 + "," + user3);
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(2000);
		resetSession();
		// login to user1 and send invitaion currentLoggeduser
		String subject1 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user1);
		SelNGBase.selfAccountName.set(user1);
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject1, location,
				currentLoggedInUser, body);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
		// login to currentLoggeduser and accept invitation
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(currentLoggedInUser);
		SelNGBase.selfAccountName.set(currentLoggedInUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		obj.zMessageItem.zClick(subject1);
		obj.zButton.zClick(localize(locator.accept));
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zExists(subject1);
		resetSession();
		// login to user2 and check fwd'ed invitation
		page.zLoginpage.zLoginToZimbraAjax(user2);
		SelNGBase.selfAccountName.set(user2);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		obj.zMessageItem.zClick(subject1);
		obj.zButton.zExists(localize(locator.replyAccept));
		obj.zButton.zExists(localize(locator.replyDecline));
		obj.zButton.zExists(localize(locator.replyTentative));
		if (ZimbraSeleniumProperties.getStringProperty("locale")
				.equals("en_US")) {

			String onbehalfof = localize(locator.onBehalfOf).toLowerCase();
			Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent(
					"xpath=//td[contains(@id,'ztb__CLV__Inv_item') and contains(text(),'"
							+ onbehalfof + "') ]/b[contains(text(),'"
							+ currentloggedInUser + "')]"));
			obj.zButton.zClick(localize(locator.accept));
			SleepUtil.sleep(1000);
			obj.zDialog.zExists(localize(locator.zimbraTitle));

			obj.zDialog.zVerifyAlertMessage(localize(locator.zimbraTitle),
					localize(locator.errorPermission));
			obj.zButton.zClickInDlg(localize(locator.ok));
		}
		// Permission denied error dialog box and press ok

		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zNotExists(subject1);
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(user3);
		SelNGBase.selfAccountName.set(user3);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		obj.zMessageItem.zClick(subject1);

		obj.zButton.zExists(localize(locator.replyAccept));
		obj.zButton.zExists(localize(locator.replyDecline));
		obj.zButton.zExists(localize(locator.replyTentative));

		if (ZimbraSeleniumProperties.getStringProperty("locale")
				.equals("en_US")) {
			String onbehalfof = localize(locator.onBehalfOf).toLowerCase();
			Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent(
					"xpath=//td[contains(@id,'ztb__CLV__Inv_item') and contains(text(),'"
							+ onbehalfof + "') ]/b[contains(text(),'"
							+ currentloggedInUser + "')]"));
			obj.zButton.zClick(localize(locator.accept));
			SleepUtil.sleep(1000);
			obj.zDialog.zExists(localize(locator.zimbraTitle));

			obj.zDialog.zVerifyAlertMessage(localize(locator.zimbraTitle),
					localize(locator.errorPermission));
			obj.zButton.zClickInDlg(localize(locator.ok));
		}

		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zNotExists(subject1);

		SelNGBase.needReset.set(false);

	}
}
