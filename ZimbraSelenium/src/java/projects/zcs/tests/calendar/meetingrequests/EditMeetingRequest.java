package projects.zcs.tests.calendar.meetingrequests;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.tools.ant.taskdefs.WaitFor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.service.admin.GetConfig;

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings({ "static-access", "unused" })
public class EditMeetingRequest extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("editReplyAppt_Bug37186")
				|| test.equals("editReplyApptAsAnAlias_Bug12301")) {
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

	/**
	 * Sends meeting invite to attendees and verifies that attendee can
	 * Accept/Decline/Tentative the appointment
	 */
	
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zAppointmentsRespondWithEditReply() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String acceptSubject = getLocalizedData_NoSpecialChar();
		String declineSubject = getLocalizedData_NoSpecialChar();
		String tentativeSubject = getLocalizedData_NoSpecialChar();

		String body = getLocalizedData_NoSpecialChar();

		String acceptLocation = getLocalizedData_NoSpecialChar();
		String declineLocation = getLocalizedData_NoSpecialChar();
		String tentativeLocation = getLocalizedData_NoSpecialChar();

		String acceptEditedReplyContent = getLocalizedData_NoSpecialChar();
		String declineEditedReplyContent = getLocalizedData_NoSpecialChar();
		String tentativeEditedReplyContent = getLocalizedData_NoSpecialChar();
		String attendees = Stafzmprov.getRandomAccount();
		String organizer = SelNGBase.selfAccountName.get();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(acceptSubject, acceptLocation,
				attendees, body);
		SleepUtil.sleep(1000);
		waitForIE();
		page.zCalCompose.zCreateSimpleAppt(declineSubject, declineLocation,
				attendees, body);
		SleepUtil.sleep(1000);
		waitForIE();
		page.zCalCompose.zCreateSimpleAppt(tentativeSubject, tentativeLocation,
				attendees, body);
		SleepUtil.sleep(1000);
		waitForIE();

		resetSession();
		SleepUtil.sleep(500);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		// obj.zButton.zClick(page.zMailApp.zMailViewIconBtn);
		// obj.zMenuItem.zClick(localize(locator.byMessage));
		MailApp.ClickCheckMailUntilMailShowsUp(acceptSubject);
		page.zCalApp.zRespondApptEditReply(acceptSubject, "accept",
				acceptEditedReplyContent);
		MailApp.ClickCheckMailUntilMailShowsUp(declineSubject);
		page.zCalApp.zRespondApptEditReply(declineSubject, "decline",
				declineEditedReplyContent);
		MailApp.ClickCheckMailUntilMailShowsUp(tentativeSubject);
		page.zCalApp.zRespondApptEditReply(tentativeSubject, "tentative",
				tentativeEditedReplyContent);
		SleepUtil.sleep(500);

		resetSession();
		SleepUtil.sleep(500);
		SelNGBase.selfAccountName.set(organizer);
		page.zLoginpage.zLoginToZimbraAjax(organizer);
		obj.zButton.zClick(page.zMailApp.zMailViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		String itemsToVerify[] = { acceptEditedReplyContent };
		MailApp.ClickCheckMailUntilMailShowsUp(acceptSubject);
		obj.zMessageItem.zClick(acceptSubject);
		page.zCalApp.zVerifyInviteContent("", itemsToVerify);
		String itemsToVerify1[] = { declineEditedReplyContent };
		MailApp.ClickCheckMailUntilMailShowsUp(declineSubject);
		obj.zMessageItem.zClick(declineSubject);
		page.zCalApp.zVerifyInviteContent("", itemsToVerify1);
		String itemsToVerify2[] = { tentativeEditedReplyContent };
		MailApp.ClickCheckMailUntilMailShowsUp(tentativeSubject);
		obj.zMessageItem.zClick(tentativeSubject);
		page.zCalApp.zVerifyInviteContent("", itemsToVerify2);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:- editReplyAccept Appointment
	 * 
	 * @steps 1.user1 creates an appointment and send to user2 2. user2 logs in
	 *        and goes to calendar 3. rt. clicks the appointment in the calendar
	 *        grid 4. selects 'Edit Reply', Accept. 5. Compose page opens 6.
	 *        Verify 'To' and 'Subject' fields they should not remains empty.
	 *        7.'user1' should be filled in To, and 'appointment subject' should
	 *        be filled in the 'Subject' field
	 * @author Girish
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void editReplyAppt_Bug37186(String subject, String location,
			String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String loggeduser = SelNGBase.selfAccountName.get();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName.set(attendees);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zRtClick(subject);
		obj.zMenuItem.zMouseOver(localize(locator.editReply));
		ClientSessionFactory.session().selenium().clickAt(
				"xpath=//td[contains(@id,'DW') and contains(text(),'"
						+ localize(locator.accept) + "')][1]", "");
		zWaitTillObjectExist("editfield", localize(locator.subjectLabel));
		Assert.assertTrue(
				obj.zTextAreaField.zGetInnerText(page.zComposeView.zToField)
						.equalsIgnoreCase(loggeduser),
				"Replied and logged user does not Match");
		Assert.assertTrue(
				obj.zTextAreaField.zGetInnerText(
						page.zComposeView.zSubjectField).contains(subject),
				"Subject does not matched");

		SelNGBase.needReset.set(false);

	}

	/**
	 * Test case users can't reply to appt invitations as an alias
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void editReplyApptAsAnAlias_Bug12301(String subject,
			String location, String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String acc1 = Stafzmprov.getRandomAccount();
		GregorianCalendar thisday = new GregorianCalendar();
		Date d = thisday.getTime();
		DateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		String s = df.format(d);
		String alias = s + "@testdomain.com";

		Stafzmprov.addAccountAlias(acc1, alias);
		Stafzmprov.modifyAccount(acc1, "zimbraPrefFromAddress", alias);
		String loggeduser = SelNGBase.selfAccountName.get();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, alias, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(acc1);
		SelNGBase.selfAccountName.set(acc1);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zRtClick(subject);
		obj.zMenuItem.zMouseOver(localize(locator.editReply));
		ClientSessionFactory.session().selenium().clickAt(
				"xpath=//td[contains(@id,'DW') and contains(text(),'"
						+ localize(locator.accept) + "')][1]", "");
		zWaitTillObjectExist("editfield", localize(locator.subjectLabel));
		Assert.assertTrue(
				obj.zTextAreaField.zGetInnerText(page.zComposeView.zToField)
						.equalsIgnoreCase(loggeduser),
				"Replied and logged user does not Match");
		Assert.assertTrue(
				obj.zTextAreaField.zGetInnerText(
						page.zComposeView.zSubjectField).contains(subject),
				"Subject does not matched");

		obj.zButton.zClick(localize(locator.send));

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(loggeduser);
		SelNGBase.selfAccountName.set(loggeduser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		SleepUtil.sleep(1000);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(localize(locator.showOrig));
		SleepUtil.sleep(4000);
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		String showOrigText = ClientSessionFactory.session().selenium().getBodyText();
		SleepUtil.sleep(1000);
		Assert.assertTrue(showOrigText.contains("From: " + alias));
		Assert.assertFalse(showOrigText.contains("From: " + acc1));
		ClientSessionFactory.session().selenium().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	private void waitForIE() throws Exception {
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		if (browser.equals("IE"))
			SleepUtil.sleep(2000);

	}

	private void waitForSF() throws Exception {
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		if (browser.equals("SF"))
			SleepUtil.sleep(2000);
	}
}
