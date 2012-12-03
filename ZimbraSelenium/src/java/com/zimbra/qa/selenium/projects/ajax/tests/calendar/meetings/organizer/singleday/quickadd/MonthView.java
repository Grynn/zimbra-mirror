package com.zimbra.qa.selenium.projects.ajax.tests.calendar.meetings.organizer.singleday.quickadd;

import java.util.HashMap;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.QuickAddAppointment;

public class MonthView extends CalendarWorkWeekTest {

	public MonthView() {
		logger.info("New "+ MonthView.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with month view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -2913827779459595178L;
		{
		    put("zimbraPrefCalendarInitialView", "month");
		}};
	}
	
	@DataProvider(name = "DataProviderQuickAdd")
	public Object[][] DataProviderQuickAdd() {
		return new Object[][] {
				new Object[] { Action.A_RIGHTCLICK },
				new Object[] { Action.A_DOUBLECLICK },
		};
	}
	
	@Test(	description = "Verify quick add dialog opens after double/right clicking to any date slot in month view",
			groups = { "sanity" },
			dataProvider = "DataProviderQuickAdd")
	
	public void MonthView_01(Action option) throws HarnessException {
		
		// Verify quick add dialog opened
		QuickAddAppointment quickAddAppt = new QuickAddAppointment(app) ;
		quickAddAppt.zNewAppointmentMonthView(option);
		quickAddAppt.zVerifyQuickAddDialog(true);
		
		/* Meeting invite full verification is already covered by meetings.organizer.singleday.minicalendar testcases so
		 not validating over here and avoiding duplication */
		
		/* If we find out something else in the future then will add verification accordingly */
	}

}
