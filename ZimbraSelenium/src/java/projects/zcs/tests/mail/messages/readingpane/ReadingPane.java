package projects.zcs.tests.mail.messages.readingpane;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class ReadingPane extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "dataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("readingPaneOnRight")) {
			return new Object[][] {
					{ SelNGBase.selfAccountName.get(), SelNGBase.selfAccountName.get(),
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"subject_readingPaneOnRight",
							"body_readingPaneOnRight", "byNormalMethod" },
					{ SelNGBase.selfAccountName.get(), SelNGBase.selfAccountName.get(),
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"subject_readingPaneOnRight",
							"body_readingPaneOnRight", "byKeyboardShortCut" } };
		} else if (test.equals("readingPaneOnBottom")) {
			return new Object[][] {
					{ SelNGBase.selfAccountName.get(), SelNGBase.selfAccountName.get(),
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"subject_readingPaneOnBottom",
							"body_readingPaneOnBottom", "byNormalMethod" },
					{ SelNGBase.selfAccountName.get(), SelNGBase.selfAccountName.get(),
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"subject_readingPaneOnBottom",
							"body_readingPaneOnBottom", "byKeyboardShortCut" } };
		} else if (test.equals("readingPaneOff")) {
			return new Object[][] {
					{ SelNGBase.selfAccountName.get(), SelNGBase.selfAccountName.get(),
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"subject_readingPaneOff", "body_readingPaneOff",
							"byNormalMethod" },
					{ SelNGBase.selfAccountName.get(), SelNGBase.selfAccountName.get(),
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"subject_readingPaneOff", "body_readingPaneOff",
							"byKeyboardShortCut" } };
		} else {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), "ccuser@testdomain.com",
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
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void readingPaneOnRight(String from, String to, String cc,
			String bcc, String subject, String body, String method)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		/**
		 * This step is added for bug 36935 : Multiple issues after setting
		 * reading pane on right.
		 */
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneOnRight));

		/**
		 * Following redundant code is added because
		 * MailApp.ClickCheckMailUntilMailShowsUp(subject); is not working with
		 * Reading pane set to right.
		 */
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);

		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneOff));

		if (method.equals("byNormalMethod")) {
			obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
			obj.zMenuItem.zClick(localize(locator.readingPaneOnRight));
		}

		if (method.equals("byKeyboardShortCut")) {
			SelNGBase.selenium.get().windowFocus();
			SleepUtil.sleep(3000);
			Robot zRobot = new Robot();
			zRobot.keyPress(KeyEvent.VK_M);
			zRobot.keyRelease(KeyEvent.VK_M);
			zRobot.keyPress(KeyEvent.VK_P);
			zRobot.keyRelease(KeyEvent.VK_P);
			zRobot.keyPress(KeyEvent.VK_R);
			zRobot.keyRelease(KeyEvent.VK_R);
			SleepUtil.sleep(2000);
		}

		Boolean isVisible = SelNGBase.selenium.get().isVisible("id=DWT6");
		Boolean isNotVisible = SelNGBase.selenium.get().isVisible("id=DWT7");
		Boolean isSubDblRowClassExists = SelNGBase.selenium.get()
				.isElementPresent("class=SubjectDoubleRow");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");

		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3000);
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"right",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName.get(),
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");

		SelNGBase.selenium.get().click("link=" + localize(locator.logOff));
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(to);
		SleepUtil.sleep(3000);
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"right",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName.get(),
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void readingPaneOff(String from, String to, String cc, String bcc,
			String subject, String body, String method) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);

		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneOnRight));

		if (method.equals("byNormalMethod")) {
			obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
			obj.zMenuItem.zClick(localize(locator.readingPaneOff));
		}

		if (method.equals("byKeyboardShortCut")) {
			SelNGBase.selenium.get().windowFocus();
			SleepUtil.sleep(3000);
			Robot zRobot = new Robot();
			zRobot.keyPress(KeyEvent.VK_M);
			zRobot.keyRelease(KeyEvent.VK_M);
			zRobot.keyPress(KeyEvent.VK_P);
			zRobot.keyRelease(KeyEvent.VK_P);
			zRobot.keyPress(KeyEvent.VK_O);
			zRobot.keyRelease(KeyEvent.VK_O);
			SleepUtil.sleep(2000);
		}

		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(1500);
		obj.zMessageItem.zVerifyIsUnRead(subject);
		Boolean isNotVisible1 = SelNGBase.selenium.get().isVisible("id=DWT6");
		Boolean isNotVisible2 = SelNGBase.selenium.get().isVisible("id=DWT7");
		Boolean isSubDblRowClassExists = SelNGBase.selenium.get()
				.isElementPresent("class=SubjectDoubleRow");
		assertReport("false", isNotVisible1.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible2.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");

		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3000);
		assertReport("false", isNotVisible1.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible2.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"off",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName.get(),
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");

		SelNGBase.selenium.get().click("link=" + localize(locator.logOff));
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(to);
		SleepUtil.sleep(3000);
		assertReport("false", isNotVisible1.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isNotVisible2.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"off",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName.get(),
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void readingPaneOnBottom(String from, String to, String cc,
			String bcc, String subject, String body, String method)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);

		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneOff));

		if (method.equals("byNormalMethod")) {
			obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
			obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));
		}

		if (method.equals("byKeyboardShortCut")) {
			SelNGBase.selenium.get().windowFocus();
			SleepUtil.sleep(3000);
			Robot zRobot = new Robot();
			zRobot.keyPress(KeyEvent.VK_M);
			zRobot.keyRelease(KeyEvent.VK_M);
			zRobot.keyPress(KeyEvent.VK_P);
			zRobot.keyRelease(KeyEvent.VK_P);
			zRobot.keyPress(KeyEvent.VK_B);
			zRobot.keyRelease(KeyEvent.VK_B);
			SleepUtil.sleep(2000);
		}

		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(1500);
		obj.zMessageItem.zVerifyIsRead(subject);
		Boolean isNotVisible = SelNGBase.selenium.get().isVisible("id=DWT6");
		Boolean isVisible = SelNGBase.selenium.get().isVisible("id=DWT7");
		Boolean isSubDblRowClassExists = SelNGBase.selenium.get()
				.isElementPresent("class=SubjectDoubleRow");
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");

		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3000);
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"bottom",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName.get(),
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");

		SelNGBase.selenium.get().click("link=" + localize(locator.logOff));
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(to);
		SleepUtil.sleep(3000);
		assertReport("false", isNotVisible.toString(),
				"Verifying whether horizontal scrollbar exists or not");
		assertReport("true", isVisible.toString(),
				"Verifying whether vertical scrollbar exists or not");
		assertReport("false", isSubDblRowClassExists.toString(),
				"Verifying subject double row view for reading pane on right");
		assertReport(
				"bottom",
				ProvZCS.getAccountPreferenceValue(SelNGBase.selfAccountName.get(),
						"zimbraPrefReadingPaneLocation"),
				"Verifying whether db value set properly or not for this account (zimbraPrefReadingPaneLocation)");
		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.readingPaneAtBottom));

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