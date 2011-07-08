package com.zimbra.qa.selenium.projects.ajax.tests.calendar.appointments;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew;

public class ModifyAppointment extends AjaxCommonTest {

	public ModifyAppointment() {
		logger.info("New " + ModifyAppointment.class.getCanonicalName());

		// All tests start at the Calendar page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}

	@Test(description = "Modify appointment with subject & body and verify it", groups = { "smoke" })
	public void ModifyAppointment_01() throws HarnessException {

		// Creating object for appointment data
		AppointmentItem appt = new AppointmentItem();
		String apptSubject, apptBody, editApptSubject, editApptBody;
		apptSubject = ZimbraSeleniumProperties.getUniqueString();
		apptBody = ZimbraSeleniumProperties.getUniqueString();

		editApptSubject = ZimbraSeleniumProperties.getUniqueString();
		editApptBody = ZimbraSeleniumProperties.getUniqueString();

		// Compose appointment, enter data and save it
		FormApptNew apptForm = (FormApptNew) app.zPageCalendar
				.zToolbarPressButton(Button.B_NEW);
		appt.setSubject(apptSubject);
		appt.setContent(apptBody);
		apptForm.zFill(appt);
		apptForm.zToolbarPressButton(Button.B_SAVEANDCLOSE);

		// Switch to work week view and open appointment again to modify it
		app.zPageCalendar.zToolbarPressPulldown(Button.B_LISTVIEW,
				Button.O_LISTVIEW_WORKWEEK);
		app.zPageCalendar.zDblClickAppointment(apptSubject);

		// Modify subject, body and re-verify
		appt.setSubject(editApptSubject);
		appt.setContent(editApptBody);
		apptForm.zFill(appt);
		apptForm.zToolbarPressButton(Button.B_SAVEANDCLOSE);
		SleepUtil.sleepMedium();

		// Verify modified appointment
		ZAssert.assertEquals(app.zPageCalendar.zVerifyAppointmentExists(
				editApptSubject).toString(), "true",
				"Verify updated appointment exists in work week view");
		ZAssert.assertEquals(editApptSubject, appt.getSubject(),
				"Subject: Verify updated appointment data");
		ZAssert.assertEquals(editApptBody, appt.getContent(),
				"Body: Verify updated appointment data");
	}
}
