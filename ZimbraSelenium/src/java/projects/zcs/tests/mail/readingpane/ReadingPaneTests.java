package projects.zcs.tests.mail.readingpane;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class ReadingPaneTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "lmtpDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("readingPaneOnRight")) {
			return new Object[][] { { SelNGBase.selfAccountName,
					SelNGBase.selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", "subject_readingPaneOnRight",
					"body_readingPaneOnRight", "" } };
		} else if (test.equals("readingPaneOnBottom")) {
			return new Object[][] { { SelNGBase.selfAccountName,
					SelNGBase.selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", "subject_readingPaneOnBottom",
					"body_readingPaneOnBottom", "" } };
		} else if (test.equals("readingPaneOff")) {
			return new Object[][] { { SelNGBase.selfAccountName,
					SelNGBase.selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", "subject_readingPaneOff",
					"body_readingPaneOff", "" } };
		} else {
			return new Object[][] { { SelNGBase.selfAccountName,
					SelNGBase.selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", "testsubject", "testbody", "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
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
	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "test" }, retryAnalyzer = RetryFailedTests.class)
	public void readingPaneOnRight(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		/**
		 * This step is added for bug 36935 : Multiple issues after setting reading pane on right.
  		 */
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneOnRight));
		
		
		/**
		 * Following redundant code is added because MailApp.ClickCheckMailUntilMailShowsUp(subject); is
		 * not working with Reading pane set to right.
		 */
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

		to = SelNGBase.selfAccountName;
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);

		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneOnRight));

		
		Boolean isVisible = selenium.isVisible("id=DWT6");
		Boolean isNotVisible = selenium.isVisible("id=DWT7");
		Boolean isSubDblRowClassExists = selenium
				.isElementPresent("class=SubjectDoubleRow");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");

		selenium.refresh();
		Thread.sleep(3000);
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"right",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName,
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");

		selenium.click("link=" + localize(locator.logOff));
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(to);
		Thread.sleep(3000);
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"right",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName,
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

		needReset = false;
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void readingPaneOff(String from, String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		to = SelNGBase.selfAccountName;
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneOff));
		obj.zMessageItem.zClick(subject);
		Thread.sleep(1500);
		obj.zMessageItem.zVerifyIsUnRead(subject);
		Boolean isNotVisible1 = selenium.isVisible("id=DWT6");
		Boolean isNotVisible2 = selenium.isVisible("id=DWT7");
		Boolean isSubDblRowClassExists = selenium
				.isElementPresent("class=SubjectDoubleRow");
		assertReport("false", isNotVisible1.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible2.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");

		selenium.refresh();
		Thread.sleep(3000);
		assertReport("false", isNotVisible1.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible2.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"off",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName,
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");

		selenium.click("link=" + localize(locator.logOff));
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(to);
		Thread.sleep(3000);
		assertReport("false", isNotVisible1.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible2.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"off",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName,
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

		needReset = false;
	}

	@Test(dataProvider = "lmtpDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void readingPaneOnBottom(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		to = SelNGBase.selfAccountName;
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));
		obj.zMessageItem.zClick(subject);
		Thread.sleep(1500);
		obj.zMessageItem.zVerifyIsRead(subject);
		Boolean isNotVisible = selenium.isVisible("id=DWT6");
		Boolean isVisible = selenium.isVisible("id=DWT7");
		Boolean isSubDblRowClassExists = selenium
				.isElementPresent("class=SubjectDoubleRow");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");

		selenium.refresh();
		Thread.sleep(3000);
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"bottom",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName,
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");

		selenium.click("link=" + localize(locator.logOff));
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(to);
		Thread.sleep(3000);
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"bottom",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName,
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

		needReset = false;
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