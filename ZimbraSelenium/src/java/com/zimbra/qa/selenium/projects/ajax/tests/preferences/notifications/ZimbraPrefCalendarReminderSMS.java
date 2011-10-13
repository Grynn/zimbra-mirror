package com.zimbra.qa.selenium.projects.ajax.tests.preferences.notifications;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning.DialogWarningID;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefCalendarReminderSMS extends AjaxCommonTest {

	protected String sms = null;
	protected String code = null;
	
	
	public ZimbraPrefCalendarReminderSMS() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 8123430160111682678L;
			{
				put("zimbraFeatureCalendarReminderDeviceEmailEnabled", "TRUE");
			}
		};
		
		// Determine the SMS number
		sms = ZimbraSeleniumProperties.getStringProperty("sms.default.number", "6505551212");
		code = ZimbraSeleniumProperties.getStringProperty("sms.default.code", "654321");
		
	}


	@Test(
			description = "Send SendVerificationCodeRequest to an SMS address",
			groups = { "functional" }
			)
	public void ZimbraPrefCalendarReminderSMS_01() throws HarnessException {
		
		String locator;
		boolean visible;
		
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Notifications);

		// Wait for the page to  be drawn
		SleepUtil.sleep(5000);

		// Set the SMS address
		locator = "css=input[id='ZmNotificationsPage_DEVICE_EMAIL_PHONE_input']";
		
		visible = app.zPagePreferences.zIsVisiblePerPosition(locator, 0, 0);
		ZAssert.assertTrue(visible, "Verify the SMS number field is present");
		
		app.zPagePreferences.sFocus(locator);
		app.zPagePreferences.zClick(locator);
		app.zPagePreferences.zKeyboardTypeString(sms);

		// Click "Send Code"
		locator = "css=td[id='ZmNotificationsPage_DEVICE_EMAIL_PHONE_SEND_CODE_title']";
		app.zPagePreferences.zClick(locator);
		
		
		// Verify the popup is displayed
		DialogWarning dialog = app.zPageMain.zGetWarningDialog(DialogWarningID.SmsVerificationCodeSent);
		dialog.zWaitForActive();
		
		ZAssert.assertTrue(dialog.zIsActive(), "Verify the confirmation dialog appears");
		dialog.zClickButton(Button.B_OK);

		
	}
	
	@Test(
			description = "Send VerifyCodeRequest to an SMS address",
			groups = { "functional" }
			)
	public void ZimbraPrefCalendarReminderSMS_02() throws HarnessException {
		
		String locator;		
		boolean visible;

		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Notifications);

		// Wait for the page to  be drawn
		SleepUtil.sleep(5000);

		
		// Set the SMS address
		locator = "css=input[id='ZmNotificationsPage_DEVICE_EMAIL_PHONE_input']";
		
		visible = app.zPagePreferences.zIsVisiblePerPosition(locator, 0, 0);
		ZAssert.assertTrue(visible, "Verify the SMS number field is present");
		
		app.zPagePreferences.sFocus(locator);
		app.zPagePreferences.zClick(locator);
		app.zPagePreferences.zKeyboardTypeString(sms);
		
		// Set the code
		locator = "css=input[id='ZmNotificationsPage_DEVICE_EMAIL_CODE_input']";

		visible = app.zPagePreferences.zIsVisiblePerPosition(locator, 0, 0);
		ZAssert.assertTrue(visible, "Verify the Code field is present");

		app.zPagePreferences.sFocus(locator);
		app.zPagePreferences.zClick(locator);
		app.zPagePreferences.zKeyboardTypeString(code);

		
		// Click "Validate Code"
		locator = "css=td[id='ZmNotificationsPage_DEVICE_EMAIL_CODE_VALIDATE_title']";
		app.zPagePreferences.zClick(locator);

		
	}

}
