package projects.zcs.tests.calendar.tags;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class TagAppointmentTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForApptAndVerify_ListView")
				|| test.equals("createRenameDeleteTagForApptInAll6View")
				|| test
						.equals("verifyTagFunctionalityFor2ApptAndRemoveTag_ListView")
				|| test.equals("applyMutlipleTagToApptAndVerify_ListView")
				|| test.equals("applyTagByDnDTagToApptAndViceVersa_ListView")
				|| test.equals("tryToCreateDuplicateTagInCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Calendar");
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
	 * Verify create, rename & delete functionality for tag for appointments
	 * (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForApptAndVerify_ListView(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, newTag1;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		page.zCalCompose.zCreateSimpleAppt(subject);
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zListItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		Thread.sleep(1000);

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);

		zDeleteTag(newTag1);
		obj.zListItem.zClick(subject);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Verify create, rename & delete functionality for tag for appointments
	 * (all 6 view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForApptInAll6View(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, newTag1;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.month));
		page.zCalCompose.zCreateSimpleAppt(subject);
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zClickApptInMonthView(subject);

		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		// verify tagged appt. in month view

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		Thread.sleep(1000);
		// verify new tagged appt. in month view

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.week));
		Thread.sleep(1000);
		// verify new tagged appt. in week view

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		Thread.sleep(1000);
		// verify new tagged appt. in list view

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.workWeek));
		Thread.sleep(1000);
		// verify new tagged appt. in work week view

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.day));
		Thread.sleep(1000);
		// verify new tagged appt. in day view

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.schedule));
		Thread.sleep(1000);
		// verify new tagged appt. in schedule view

		zDeleteTag(newTag1);

		// verify appt. is not tagged in schedule view
		// click to appt. in schedule view
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.day));
		// verify appt. is not tagged in day view
		// click to appt. in day view
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.workWeek));
		// verify appt. is not tagged in work week view
		// click to appt. in work week view
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.week));
		// verify appt. is not tagged in week view
		// click to appt. in week view
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.month));
		// verify appt. is not tagged in month view
		// click to appt. in month view
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.month));
		// verify appt. is not tagged in list view
		// click to appt. in list view
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Create 2 tag, apply 1 tag to each appointment and verify appointment
	 * exist / not exist by clicking to tag (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2ApptAndRemoveTag_ListView(
			String subject) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject2, tag1, tag2;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		subject2 = getLocalizedData_NoSpecialChar();
		page.zCalCompose.zCreateSimpleAppt(subject);
		page.zCalCompose.zCreateSimpleAppt(subject2);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zListItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		Thread.sleep(1000);
		obj.zListItem.zClick(subject2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);
		assertReport("false", obj.zListItem.zExistsDontWait(subject2),
				"Verify contact2 not exists");
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject2);
		assertReport("false", obj.zListItem.zExistsDontWait(subject),
				"Verify contact1 not exists");
		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		Thread.sleep(1000);
		assertReport("true", obj.zListItem.zExistsDontWait(subject),
				"Verify appointment1 not exists");
		assertReport("true", obj.zListItem.zExistsDontWait(subject2),
				"Verify appointment2 not exists");
		obj.zListItem.zClick(subject2);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zListItem.zClick(subject2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Create 2 tag, apply both tag to appointment and verify both appointment
	 * after clicking to tag exists (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyMutlipleTagToApptAndVerify_ListView(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, tag2;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		page.zCalCompose.zCreateSimpleAppt(subject);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		obj.zListItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag1);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		obj.zListItem.zClick(subject);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(subject);
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);

		needReset = false;
	}

	/**
	 * Verify drag n drop functionality for tag and appointment. Drag
	 * appointment to tag and verify tag applied & same way drag tag to
	 * appointment and verify tag applied (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyTagByDnDTagToApptAndViceVersa_ListView(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject2, tag1, tag2;
		obj.zButton.zClick(page.zCalApp.zViewBtn);
		obj.zMenuItem.zClick(localize(locator.list));
		subject2 = getLocalizedData_NoSpecialChar();
		page.zCalCompose.zCreateSimpleAppt(subject);
		page.zCalCompose.zCreateSimpleAppt(subject2);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zCreateTag(tag2);
		zDragAndDrop("//div[contains(@id, 'zli__CLL')]//td[contains(text(), '"
				+ subject + "')]",
				"//td[contains(@id, 'zti__main_Calendar') and contains(text(), '"
						+ tag1 + "')]");
		obj.zListItem.zVerifyIsTagged(subject);

		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject);
		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Calendar') and contains(text(), '"
						+ tag2 + "')]",
				"//div[contains(@id, 'zli__CLL')]//td[contains(text(), '"
						+ subject2 + "')]");
		obj.zListItem.zVerifyIsTagged(subject2);
		Thread.sleep(1000);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(subject2);
		assertReport("false", obj.zListItem.zExistsDontWait(subject),
				"Verify appointment1 not exists");

		needReset = false;
	}

	/**
	 * Try to create duplicate tag and verify its not allowed (list view)
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTagInCalendar(String subject)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1;
		tag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zDuplicateTag(tag1);

		needReset = false;
	}

	public static void zClickApptInMonthView(String subject) throws Exception {
		selenium.clickAt(
				"xpath=//td[contains(@class, 'calendar_month_day_item') and contains(text(), "
						+ subject + ")]", "");
	}

	public static void zDblClickApptInMonthView(String subject)
			throws Exception {
		selenium.doubleClickAt(
				"xpath=//td[contains(@class, 'calendar_month_day_item') and contains(text(), "
						+ subject + ")]", "");
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