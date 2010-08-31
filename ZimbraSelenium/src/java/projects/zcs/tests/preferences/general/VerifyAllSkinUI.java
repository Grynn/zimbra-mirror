package projects.zcs.tests.preferences.general;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 * 
 */
public class VerifyAllSkinUI extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "SkinDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("verifyAllSkinsBasicUI")) {
			return new Object[][] { { "Bare, Beach, Bones, HotRod, Lake, Lavender, Lomongrass, Oasis, Pebble, Sand, Sky, Smoke, Steel, Tree, Twilight, Waves, Yahoo, Zmail" } };
		} else {
			return new Object[][] { { "Bare, Beach, Bones, Goldrush, HotRod, Lake, Lavender, Lomongrass, Oasis, Pebble, Sand, Sky, Smoke, Steel, Tree, Twilight, Waves, Yahoo, Zmail" } };
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
	@SuppressWarnings("static-access")
	@Test(dataProvider = "SkinDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAllSkinsBasicUI(String skinName) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String skin[];
		skin = skinName.split(", ");

		for (int i = 0; i <= skin.length - 1; i++) {
			System.out.println("---------- Verifying skin UI(" + skin[i]
					+ ") ----------");
			SelNGBase.selenium.get().open(ZimbraSeleniumProperties.getStringProperty("mode") + "://"
					+ ZimbraSeleniumProperties.getStringProperty("server") + "/?skin="
					+ skin[i].toLowerCase());
			zNavigateAgainIfRequired(ZimbraSeleniumProperties.getStringProperty("mode") + "://"
					+ ZimbraSeleniumProperties.getStringProperty("server") + "/?skin="
					+ skin[i].toLowerCase());

			// mail
			if (skin[i].toLowerCase().equals("zmail")) {
				obj.zButton.zClick("id=zb__App__Mail_title");
			} else {
				zGoToApplication("Mail");
			}
			obj.zFolder.zExists(page.zMailApp.zInboxFldr);
			obj.zFolder.zExists(page.zMailApp.zSentFldr);
			obj.zFolder.zExists(page.zMailApp.zTrashFldr);
			verifyOverviewPane();
			obj.zButton.zExists(page.zMailApp.zNewMenuIconBtn);
			obj.zButton.zExists(page.zMailApp.zViewIconBtn);

			// address book
			if (skin[i].toLowerCase().equals("goldrush")) {
				obj.zButton.zClick(localize(locator.contacts));
			} else if (skin[i].toLowerCase().equals("yahoo")
					|| skin[i].toLowerCase().equals("zmail")) {
				obj.zButton.zClick("id=zb__App__Contacts_title");
			} else {
				zGoToApplication("Address Book");
			}
			obj.zFolder.zExists(page.zABCompose.zContactsFolder);
			obj.zFolder.zExists(page.zABCompose.zEmailedContactsFolder);
			obj.zButton.zExists(page.zABCompose.zNewABOverviewPaneIcon);
			verifyOverviewPane();

			// calendar
			if (skin[i].toLowerCase().equals("yahoo")
					|| skin[i].toLowerCase().equals("zmail")) {
				obj.zButton.zClick("id=zb__App__Calendar_title");
			} else {
				zGoToApplication("Calendar");
			}
			obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
			obj.zButton.zExists(page.zCalApp.zCalNewApptBtn);
			obj.zButton.zExists(page.zCalApp.zCalTodayBtn);
			verifyOverviewPane();

			// tasks
			if (skin[i].toLowerCase().equals("yahoo")
					|| skin[i].toLowerCase().equals("zmail")) {
				obj.zButton.zClick("id=zb__App__Tasks_title");
			} else {
				zGoToApplication("Tasks");
			}
			if (skin[i].toLowerCase().equals("zmail")) {
				obj.zFolder.zExists("id=zti__main_Tasks__15_textCell");
				obj.zButton.zExists(page.zTaskApp.zTasksNewBtn);
				obj.zButton.zExists(page.zTaskApp.zTasksViewBtn);
			} else {
				obj.zFolder.zExists(page.zTaskApp.zTasksFolder);
				obj.zButton.zExists(page.zTaskApp.zTasksNewBtn);
				obj.zButton.zExists(page.zTaskApp.zTasksViewBtn);
			}
			verifyOverviewPane();

			// documents
			if (skin[i].toLowerCase().equals("yahoo")
					|| skin[i].toLowerCase().equals("zmail")) {
				obj.zButton.zClick("id=zb__App__Notebook_title");
			} else {
				zGoToApplication("Documents");
			}
			obj.zFolder.zExists(page.zDocumentApp.zNotebookFolder);
			obj.zButton.zExists(page.zDocumentApp.zNewNotebookOverviewPaneIcon);
			verifyOverviewPane();

			// briefcase
			if (skin[i].toLowerCase().equals("yahoo")
					|| skin[i].toLowerCase().equals("zmail")) {
				obj.zButton.zClick("id=zb__App__Briefcase_title");
			} else {
				zGoToApplication("Briefcase");
			}
			obj.zFolder.zExists(page.zBriefcaseApp.zBriefcaseFolder);
			obj.zButton.zExists(page.zBriefcaseApp.zNewMenuIconBtn);
			obj.zButton.zExists(page.zBriefcaseApp.zViewIconBtn);
			verifyOverviewPane();

			// preferences
			if (skin[i].toLowerCase().equals("yahoo")
					|| skin[i].toLowerCase().equals("zmail")
					|| skin[i].toLowerCase().equals("goldrush")) {
				obj.zButton.zClick("id=zb__App__Options_title");
			} else {
				zGoToApplication("Preferences");
			}
			obj.zButton.zExists(localize(locator.changePassword));
			zGoToPreferences("Mail");
			obj.zButton.zExists(page.zMailApp.zMailViewIconBtn);
			zGoToPreferences("Calendar");
			obj.zFeatureMenu.zExists(localize(locator.defaultViewLabel));
			zGoToPreferences("Sharing");
			obj.zRadioBtn.zExists("id=*_user");
			obj.zButton.zExists(page.zMailApp.zZimletsPrefFolder);
			SleepUtil.sleep(2000);
		}

		SelNGBase.needReset.set(false);
	}

	@SuppressWarnings("static-access")
	private static void verifyOverviewPane() throws Exception {
		obj.zButton.zExists("id=ztih__main_Mail__FOLDER_textCell");
		obj.zButton.zExists("id=ztih__main_Mail__SEARCH_textCell");
		obj.zButton.zExists("id=ztih__main_Mail__TAG_textCell");
		obj.zButton.zExists("id=ztih__main_Mail__ZIMLET_textCell");
	}

}