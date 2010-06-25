package projects.zcs.tests.preferences.shortcuts;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

import com.zimbra.common.service.ServiceException;

import framework.util.RetryFailedTests;

@SuppressWarnings( { "static-access", "unused" })
public class ShortcutsCalendar extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "shortcutsDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		if (test.equals("shortcutsCalendarActions")) {

			return new Object[][] { { KeyEvent.VK_W, "Week", "calendar_heading_day"},
					{ KeyEvent.VK_W, "WorkWeek", "calendar_heading_day" },
					{ KeyEvent.VK_M, "Month", "calendar_month_header_cells_text"},
					{ KeyEvent.VK_L, "List","" },
					{ KeyEvent.VK_S, "Schedule","calendar_heading_day" },
					{ KeyEvent.VK_D, "Day","calendar_heading_day_today" },
					{ KeyEvent.VK_Y, "Today","" } 
			};

		}else {

			return new Object[][] { { "test" } };
		}
	}

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {

		zLoginIfRequired();
		Thread.sleep(2000);

		String[] recipients = { selfAccountName };
		isExecutionARetry = false;
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsCalendarActions(int keyToPress, String actionType, String className)
	throws Exception {

		String subject = getLocalizedData_NoSpecialChar();
		String location = getLocalizedData(1);
		String attendees = ProvZCS.getRandomAccount();
		String body = getLocalizedData(3);

		if (isExecutionARetry)
			handleRetry();

		selenium.windowFocus();
		Thread.sleep(2000);
		page.zCalApp.zNavigateToCalendar();

		Thread.sleep(3000);		
		Robot zRobot = new Robot();

		if (actionType.equals("Day") ) {
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			Thread.sleep(2000);

			Assert.assertTrue(getElementStatus(className,""));
		} else if(actionType.equals("Today")){
			page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
			obj.zAppointment.zExists(subject);
			selenium.clickAt("//*[contains(@class,'ImgRightArrow')]", "");
			selenium.clickAt("//*[contains(@class,'ImgRightArrow')]", "");
			selenium.clickAt("//*[contains(@class,'ImgRightArrow')]", "");
			obj.zAppointment.zNotExists(subject);
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			Thread.sleep(2000);
			obj.zAppointment.zExists(subject);
		} else if(actionType.equals("Week")){
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			Thread.sleep(2000);


			if(config.getString("locale").equals("en_US") || config.getString("locale").equals("en_AU") || config.getString("locale").equals("en_GB")) {
				/**
				 * Following code makes test case english locale specific.
				 */
				Assert.assertTrue(getElementStatus(className,"Mon"));
				Assert.assertTrue(getElementStatus(className,"Tue"));
				Assert.assertTrue(getElementStatus(className,"Wed"));
				Assert.assertTrue(getElementStatus(className,"Thu"));
				Assert.assertTrue(getElementStatus(className,"Fri"));
				Assert.assertTrue(getElementStatus(className,"Sat"));
				Assert.assertTrue(getElementStatus(className,"Sun"));
			}
		}else if(actionType.equals("WorkWeek")){
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			Thread.sleep(2000);

			if(config.getString("locale").equals("en_US") || config.getString("locale").equals("en_AU") || config.getString("locale").equals("en_GB")) {
				/**
				 * Following code makes test case english locale specific.
				 */
				Assert.assertTrue(getElementStatus(className,"Mon"));
				Assert.assertTrue(getElementStatus(className,"Tue"));
				Assert.assertTrue(getElementStatus(className,"Wed"));
				Assert.assertTrue(getElementStatus(className,"Thu"));
				Assert.assertTrue(getElementStatus(className,"Fri"));
				//Assert.assertFalse(getElementStatus(className,"Sat"));
				//Assert.assertFalse(getElementStatus(className,"Sun"));
			}
		}else if(actionType.equals("Month")){
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			Thread.sleep(2000);

			if(config.getString("locale").equals("en_US") || config.getString("locale").equals("en_AU") || config.getString("locale").equals("en_GB")) {
				/**
				 * Following code makes test case english locale specific.
				 */
				Assert.assertTrue(getElementStatus(className,"Monday"));
				Assert.assertTrue(getElementStatus(className,"Tuesday"));
				Assert.assertTrue(getElementStatus(className,"Wednesday"));
				Assert.assertTrue(getElementStatus(className,"Thursday"));
				Assert.assertTrue(getElementStatus(className,"Friday"));
				Assert.assertTrue(getElementStatus(className,"Saturday"));
				Assert.assertTrue(getElementStatus(className,"Sunday"));
			}
		}else if(actionType.equals("List")){
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			Thread.sleep(2000);

			Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__se"));
			Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__tg"));
			Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__at"));
			Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__su"));
			Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__lo"));
			Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__st"));
			Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__fo"));
			Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__re"));
			Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__dt"));
		}else if(actionType.equals("Schedule")){
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			Thread.sleep(2000);

			if(config.getString("locale").equals("en_US") || config.getString("locale").equals("en_AU") || config.getString("locale").equals("en_GB")) {
				/**
				 * Following code makes test case english locale specific.
				 */
				Assert.assertTrue(getElementStatus(className,"Calendar"));
			}
		}

		zRobot.keyPress(KeyEvent.VK_ESCAPE);
		zRobot.keyRelease(KeyEvent.VK_ESCAPE);
		Thread.sleep(2000);
		needReset = false;
	}


	public Boolean getElementStatus(String className, String day) throws Exception{
		return(selenium.isElementPresent("//*[contains(@class,'"+className+"') and contains(text(),'"+day+"')]"));
	}


	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
