package projects.zcs.tests.mail.messages.attachments;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

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
				.zInjectMessage("createApptFromICSAttachment1_Bug27959");
		subject2 = page.zMailApp
				.zInjectMessage("createApptFromICSAttachment2_Bug27959");
		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject2);

		obj.zMessageItem.zClick(subject1);
		SleepUtil.sleep(1000);
		SelNGBase.selenium.get().click(
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
		SelNGBase.selenium.get().click(
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
		SelNGBase.selenium.get().selectWindow("_blank");
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().click(
				"link=" + localize(locator.addToCalendar));
		obj.zFolder.zClickInDlgByName(localize(locator.calendar),
				localize(locator.addToCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToCalendar));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SelNGBase.selenium.get().selectWindow(null);

		String startDate = "20060119";
		String calView = "workWeek";
		SelNGBase.selenium.get().open(
				ZimbraSeleniumProperties.getStringProperty("mode") + "://"
						+ ZimbraSeleniumProperties.getStringProperty("server")
						+ "/?app=calendar&view=" + calView + "&date="
						+ startDate);
		zNavigateAgainIfRequired(ZimbraSeleniumProperties
				.getStringProperty("mode")
				+ "://"
				+ ZimbraSeleniumProperties.getStringProperty("server")
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