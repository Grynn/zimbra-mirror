package com.zimbra.qa.selenium.projects.zcs.tests.mail.messages.attachments;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.RetryFailedTests;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.zcs.tests.CommonTest;


/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class AddToCalendar extends CommonTest {
	protected int j = 0;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("addToCalendarFromICSAttachmentMail_Bug27959")
				|| test
						.equals("jsErrorOnClickingAddToCalendarInNewWindow_Bug49734")) {
			return new Object[][] { { "" } };
		} else {
			return new Object[][] { { "" } };
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
	 * bug 27959 - Create appointment from ics attachment
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addToCalendarFromICSAttachmentMail_Bug27959() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry1();

		String subject1, subject2;
		subject1 = page.zMailApp
				.zInjectMessage("data/public/mime/createApptFromICSAttachment1_Bug27959.txt");
		subject2 = page.zMailApp
				.zInjectMessage("data/public/mime/createApptFromICSAttachment2_Bug27959.txt");
		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject2);

		obj.zMessageItem.zClick(subject1);
		SleepUtil.sleep(1000);
		ClientSessionFactory.session().selenium().click(
				"link=" + localize(locator.addToCalendar));
		obj.zFolder.zClickInDlgByName(localize(locator.calendar),
				localize(locator.addToCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToCalendar));
		SleepUtil.sleep(1000);
		zGoToApplication("Calendar");
		obj.zAppointment.zDblClick("Critical");
		obj.zRadioBtn.zClickInDlgByName(localize(locator.openSeries),
				localize(locator.openRecurringItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.openRecurringItem));
		obj.zButton.zClick(localize(locator.close));

		zGoToApplication("Mail");
		obj.zMessageItem.zClick(subject2);
		SleepUtil.sleep(1000);
		ClientSessionFactory.session().selenium().click(
				"link=" + localize(locator.addToCalendar));
		obj.zFolder.zClickInDlgByName(localize(locator.calendar),
				localize(locator.addToCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToCalendar));
		SleepUtil.sleep(1000);
		zGoToApplication("Calendar");
		obj.zAppointment.zDblClick("every week");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.openRecurringItem));
		obj.zButton.zClick(localize(locator.close));

		SelNGBase.needReset.set(false);
	}

	/**
	 * bug 49734 - JS error (t is undefined) while click to 'Add to Calendar'
	 * when viewing in separate window
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void jsErrorOnClickingAddToCalendarInNewWindow_Bug49734()
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry1();

		checkForSkipException("all", "na", "50116",
				"Attachment links are not removed from same window if removed from new window");

		String subject;
		subject = page.zMailApp
				.zInjectMessage("jsErrorOnClickingAddToCalendarInNewWindow_Bug49734");

		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		SleepUtil.sleep(3000);
		ClientSessionFactory.session().selenium().click(
				"link=" + localize(locator.addToCalendar));
		obj.zFolder.zClickInDlgByName(localize(locator.calendar),
				localize(locator.addToCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToCalendar));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		ClientSessionFactory.session().selenium().selectWindow(null);

		String startDate = "20060119";
		String calView = "workWeek";
		ClientSessionFactory.session().selenium().open(
				ZimbraSeleniumProperties.getStringProperty("server.scheme") + "://"
						+ ZimbraSeleniumProperties.getStringProperty("server.host")
						+ "/?app=calendar&view=" + calView + "&date="
						+ startDate);
		zNavigateAgainIfRequired(ZimbraSeleniumProperties
				.getStringProperty("mode")
				+ "://"
				+ ZimbraSeleniumProperties.getStringProperty("server.host")
				+ "/?app=calendar&view=" + calView + "&date=" + startDate);
		Thread.sleep(2000);
		obj.zAppointment.zExists("iCalBasic");

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry1() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
		j = 0;
	}
}