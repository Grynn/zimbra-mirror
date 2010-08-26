package projects.zcs.tests.briefcase.files;

import java.lang.reflect.Method;
import org.testng.Assert;
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
 */
@SuppressWarnings("static-access")
public class BriefcaseFileContextMenu extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "BriefcaseFileUpload")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("verifyDownloadMenuDisabledForNewDoc"))
			return new Object[][] { { "CreateNewDoc",
					getLocalizedData_NoSpecialChar() } };
		else
			return new Object[][] { { "" } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		page.zBriefcaseApp.zGoToBriefcaseApp();
		SleepUtil.sleep(2000);
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * 1. Login to web client 2. Go to Briefcase > create any document Or
	 * presentation Or spreadsheet 3. Right click to Saved doc. 4. Verify
	 * 'Download' menu remains disabled 5. Right side column 'Download' link as
	 * disabled.
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "BriefcaseFileUpload", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyDownloadMenuDisabledForNewDoc(String filename,
			String newBFFolder) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		if (ZimbraSeleniumProperties.getStringProperty("locale")
				.equals("en_US")) {
			obj.zButton.zClick(localize(locator.newDocument));
			SleepUtil.sleep(1500);
			SelNGBase.selenium.get().selectWindow(
					SelNGBase.selenium.get().getAllWindowTitles()[1]);
			SelNGBase.selenium.get().windowFocus();
			zWaitTillObjectExist("button", localize(locator.save));
			SelNGBase.selenium.get().type("xpath=//input[@type='text']",
					filename);
			obj.zButton.zClick(localize(locator.save));
			SleepUtil.sleep(1000);
			SelNGBase.selenium.get().close();
			SelNGBase.selenium.get().selectWindow(null);
			obj.zFolder.zClick(page.zBriefcaseApp.zBriefcaseFolder);
			SleepUtil.sleep(1000);
			obj.zBriefcaseItem.zClick(filename);
			obj.zBriefcaseItem.zRtClick(filename);
			SleepUtil.sleep(500);
			String download = SelNGBase.selenium
					.get()
					.getEval(
							"selenium.browserbot.getCurrentWindow().document.getElementById('zmi__Briefcase__SAVE_FILE').className");
			Assert.assertTrue(download.contains("ZDisabled"),
					"Download is in enable state");

			Boolean downloadLink = SelNGBase.selenium.get().isElementPresent(
					"Link=" + localize(locator.saveFile));
			assertReport("false", downloadLink.toString(),
					"Verifying Download link exist");
		}

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}