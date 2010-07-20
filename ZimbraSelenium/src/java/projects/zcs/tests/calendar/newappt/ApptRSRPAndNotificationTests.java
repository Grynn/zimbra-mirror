package projects.zcs.tests.calendar.newappt;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access", "unused" })
public class ApptRSRPAndNotificationTests extends CommonTest {

	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("apptRSRPNo_NotifictionNo")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3), "0", "0" } };
		} else if (test.equals("apptRSRPNo_NotifictionYes")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else if (test.equals("apptRSRPNoWithNotifyingOrg")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else if (test.equals("apptRSRPNoWithTentativeEdit")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else if (test.equals("apptRSRPNoWithDecline")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		} else {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3), "0", "1" } };
		}

	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		Thread.sleep(2000);
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNo_NotifictionNo(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName = attendees;
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject);
		zGoToApplication("Calendar");
		obj.zAppointment.zNotExists(subject);

		needReset = false;

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNo_NotifictionYes(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = SelNGBase.selfAccountName;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName = attendees;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(localize(locator.accept));
		Thread.sleep(1000);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zNotExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		SelNGBase.selfAccountName = organizer;
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject);

		needReset = false;

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNoWithNotifyingOrg(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = SelNGBase.selfAccountName;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName = attendees;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButtonMenu.zClick(localize(locator.accept));
		Thread.sleep(1500);
		obj.zMenuItem.zClick(localize(locator.notifyOrganizerLabel));
		Thread.sleep(2000);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zExists(localize(locator.accept) + ": " + subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		SelNGBase.selfAccountName = organizer;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(localize(locator.accept)
				+ ": " + subject);

		needReset = false;

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNoWithTentativeEdit(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = SelNGBase.selfAccountName;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName = attendees;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButtonMenu.zClick(localize(locator.replyTentative));
		
		Thread.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.editReply));
		Thread.sleep(1500);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zExists(localize(locator.replyTentative) + ": " + subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		SelNGBase.selfAccountName = organizer;
		page.zMailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.replyTentative)
						+ ": " + subject);

		needReset = false;

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRSRPNoWithDecline(String subject, String location,
			String attendees, String body, String RSRP, String Notifcation)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = SelNGBase.selfAccountName;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleApptWithRSRPNotification(subject,
				location, attendees, body, RSRP, Notifcation);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName = attendees;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButtonMenu.zClick(localize(locator.replyDecline));
		Thread.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.dontNotifyOrganizerLabel));
		Thread.sleep(1500);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		obj.zMessageItem.zNotExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		SelNGBase.selfAccountName = organizer;
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		obj.zMessageItem.zNotExists(subject);

		needReset = false;

	}

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
