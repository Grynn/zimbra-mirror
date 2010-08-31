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

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

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


	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="mail";
		super.zLogin();
		String[] recipients = { SelNGBase.selfAccountName.get() };
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsCalendarActions(int keyToPress, String actionType, String className)
	throws Exception {

		String subject = getLocalizedData_NoSpecialChar();
		String location = getLocalizedData(1);
		String attendees = ProvZCS.getRandomAccount();
		String body = getLocalizedData(3);

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		SelNGBase.selenium.get().windowFocus();
		SleepUtil.sleep(2000);
		page.zCalApp.zNavigateToCalendar();

		SleepUtil.sleep(3000);		
		Robot zRobot = new Robot();

		if (actionType.equals("Day") ) {
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			SleepUtil.sleep(2000);

			Assert.assertTrue(getElementStatus(className,""));
		} else if(actionType.equals("Today")){
			page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
			obj.zAppointment.zExists(subject);
			SelNGBase.selenium.get().clickAt("//*[contains(@class,'ImgRightArrow')]", "");
			SelNGBase.selenium.get().clickAt("//*[contains(@class,'ImgRightArrow')]", "");
			SelNGBase.selenium.get().clickAt("//*[contains(@class,'ImgRightArrow')]", "");
			obj.zAppointment.zNotExists(subject);
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			SleepUtil.sleep(2000);
			obj.zAppointment.zExists(subject);
		} else if(actionType.equals("Week")){
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			SleepUtil.sleep(2000);


			if(ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_AU") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_GB")) {
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
			SleepUtil.sleep(2000);

			if(ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_AU") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_GB")) {
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
			SleepUtil.sleep(2000);

			if(ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_AU") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_GB")) {
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
			SleepUtil.sleep(2000);

			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhi__CLL__se"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhi__CLL__tg"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhi__CLL__at"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhl__CLL__su"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhl__CLL__lo"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhl__CLL__st"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhl__CLL__fo"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhi__CLL__re"));
			Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("zlhl__CLL__dt"));
		}else if(actionType.equals("Schedule")){
			zRobot.keyPress(keyToPress);
			zRobot.keyRelease(keyToPress);
			SleepUtil.sleep(2000);

			if(ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_AU") || ZimbraSeleniumProperties.getStringProperty("locale").equals("en_GB")) {
				/**
				 * Following code makes test case english locale specific.
				 */
				Assert.assertTrue(getElementStatus(className,"Calendar"));
			}
		}

		zRobot.keyPress(KeyEvent.VK_ESCAPE);
		zRobot.keyRelease(KeyEvent.VK_ESCAPE);
		SleepUtil.sleep(2000);
		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "shortcutsDataProvider",groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void quickAddAppointment_Q(String testArg)
	throws Exception {


		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		SelNGBase.selenium.get().windowFocus();
		SleepUtil.sleep(2000);
		page.zCalApp.zNavigateToCalendar();

		SleepUtil.sleep(3000);		
		Robot zRobot = new Robot();

		zRobot.keyPress(KeyEvent.VK_Q);
		zRobot.keyRelease(KeyEvent.VK_Q);
		SleepUtil.sleep(2000);
		
		obj.zDialog.zExists(localize(locator.quickAddAppt));
		SelNGBase.needReset.set(false);
	}


	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editAppt_E(String testArg)
	throws Exception {

		String subject = getLocalizedData_NoSpecialChar();
		String location = getLocalizedData(1);
		String attendees = ProvZCS.getRandomAccount();
		String body = getLocalizedData(3);

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		SelNGBase.selenium.get().windowFocus();
		SleepUtil.sleep(2000);
		page.zCalApp.zNavigateToCalendar();

		SleepUtil.sleep(3000);		
		Robot zRobot = new Robot();

		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zClick(subject);

		zRobot.keyPress(KeyEvent.VK_E);
		zRobot.keyRelease(KeyEvent.VK_E);
		SleepUtil.sleep(2000);
		obj.zEditField.zExists(localize(locator.subject));
		SelNGBase.needReset.set(false);
	}
	
	
	public Boolean getElementStatus(String className, String day) throws Exception{
		return(SelNGBase.selenium.get().isElementPresent("//*[contains(@class,'"+className+"') and contains(text(),'"+day+"')]"));
	}

}
