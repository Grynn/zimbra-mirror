package com.zimbra.qa.selenium.projects.mobile.tests.main;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.mobile.core.MobileCommonTest;
import com.zimbra.qa.selenium.projects.mobile.ui.PageMain;


public class MainScreen extends MobileCommonTest {
	
	public MainScreen() {
		logger.info("New "+ MainScreen.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMain;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Verify basic elements on the Main Screen",
			groups = { "sanity" })
	public void MainScreen_01() throws HarnessException {
				
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zAppbarMail),		"Verify that the appbar Mail icon is present");
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zAppbarContact),	"Verify that the appbar Contact icon is present");
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zAppbarCal),		"Verify that the appbar Cal icon is present");
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zAppbarDocs),		"Verify that the appbar Docs icon is present");
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zAppbarSearch),	"Verify that the appbar Search icon is present");
		
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zBtnCompose),		"Verify that the New Compose link is present");

		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zPreferences),	"Verify that the Preferences link is present");

	}

	@Test(	description = "Verify Copyright on the Main Screen",
			groups = { "smoke" })
	public void MainScreen_02() throws HarnessException {
				
		// The copyright doesn't seem to be translated
		//use "\u00a9" (char)169 for Copyright ©
		
		String copyright = "Copyright " + "\u00a9" + " 2005-2011 VMware, Inc. VMware and Zimbra are registered trademarks or trademarks of VMware, Inc.";
		
				
		ZAssert.assertTrue(app.zPageMain.sIsElementPresent(PageMain.Locators.zMainCopyright),	"Verify that the copyright notice is present");
		
		String text = app.zPageMain.sGetText(PageMain.Locators.zMainCopyright);
		ZAssert.assertEquals(text, copyright, "Verify the copyright text is correct");
			
		Calendar calendar = new GregorianCalendar();
		String thisYear = "" + calendar.get(Calendar.YEAR);

		ZAssert.assertStringContains(text, thisYear, "Verify the copyright text is correct");

	}


}
