package projects.zcs.tests.calendar.meetingrequests;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings({ "static-access", "unused" })
public class ApptRSRPAndNotification extends CommonTest {

	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("apptRSRPNo_NotifictionNo")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3), "0", "0" } };
		} else if (test.equals("apptRSRPNo_NotifictionYes")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else if (test.equals("apptRSRPNoWithNotifyingOrg")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else if (test.equals("apptRSRPNoWithTentativeEdit")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else if (test.equals("apptRSRPNoWithDecline")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
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
	public void apptRSRPNo_NotifictionNo(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject);
		zGoToApplication("Calendar");
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNo_NotifictionYes(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = ClientSessionFactory.session().currentUserName();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.accept));
		SleepUtil.sleep(1000);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zNotExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNoWithNotifyingOrg(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = ClientSessionFactory.session().currentUserName();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButtonMenu.zClick(localize(locator.accept));
		SleepUtil.sleep(1500);
		obj.zMenuItem.zClick(localize(locator.notifyOrganizerLabel));
		SleepUtil.sleep(2000);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zExists(localize(locator.accept) + ": " + subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		
		page.zMailApp.ClickCheckMailUntilMailShowsUp(localize(locator.accept)
				+ ": " + subject);

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNoWithTentativeEdit(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = ClientSessionFactory.session().currentUserName();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButtonMenu.zClick(localize(locator.replyTentative));

		SleepUtil.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.editReply));
		SleepUtil.sleep(1500);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(2000);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zExists(localize(locator.replyTentative) + ": "
				+ subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.replyTentative)
						+ ": " + subject);

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNoWithDecline(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = ClientSessionFactory.session().currentUserName();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButtonMenu.zClick(localize(locator.replyDecline));
		SleepUtil.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.dontNotifyOrganizerLabel));
		SleepUtil.sleep(1500);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zNotExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);

	}
}
