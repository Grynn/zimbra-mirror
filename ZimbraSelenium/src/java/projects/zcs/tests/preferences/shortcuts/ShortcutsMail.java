package projects.zcs.tests.preferences.shortcuts;

import java.awt.Robot;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.Selenium;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.service.admin.GetConfig;
import com.zimbra.cs.zclient.ZFolder;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ShortcutsMail extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "shortcutsDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		if (test.equals("shortcutsMailActions")) {

			return new Object[][] { { KeyEvent.VK_R, "reply" },
					{ KeyEvent.VK_A, "reply-all" },
					{ KeyEvent.VK_F, "forward" },
					{ KeyEvent.VK_DELETE, "delete" } };

		} else if (test.equals("shortcutsFlagMessage")) {

			return new Object[][] { { KeyEvent.VK_F, "flag" },
					{ KeyEvent.VK_F, "unflag" }, { KeyEvent.VK_R, "read" },
					{ KeyEvent.VK_U, "unread" } };

		} else if (test.equals("shortcutsGoToMailFolders")) {

			return new Object[][] { { KeyEvent.VK_J, localize(locator.junk) },
					{ KeyEvent.VK_S, localize(locator.sent) }, { KeyEvent.VK_T, localize(locator.trash) },
					{ KeyEvent.VK_D, localize(locator.draft) } };

		}else {

			return new Object[][] { { "test" } };
		}
	}

	// Before Class
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {

		zLoginIfRequired();
		SleepUtil.sleep(2000);

		String[] recipients = { SelNGBase.selfAccountName.get() };
		ProvZCS.injectMessage(ProvZCS.getRandomAccount(), recipients, ProvZCS
				.getRandomAccount(), "test mail", "test content");
		page.zMailApp.ClickCheckMailUntilMailShowsUp("test mail");
		ProvZCS.injectMessage(ProvZCS.getRandomAccount(), recipients, ProvZCS
				.getRandomAccount(), "flag message", "test content");

		page.zMailApp.ClickCheckMailUntilMailShowsUp("flag message");

		SelNGBase.isExecutionARetry.set(false);
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsMailActions(int keyToPress, String actionType)
			throws Exception {

		String verifyTo, verifyCc;

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "SF", "na", "shortcuts dont work with Selenium on Safari");

		SelNGBase.selenium.get().windowFocus();
		SleepUtil.sleep(2000);
		obj.zMessageItem.zClick("test mail");

		SleepUtil.sleep(3000);		

		Robot zRobot = new Robot();

		zRobot.keyPress(keyToPress);

		zRobot.keyRelease(keyToPress);

		SleepUtil.sleep(2000);

		if (actionType.equals("reply")) {
			verifyTo = obj.zTextAreaField
					.zGetInnerText(page.zComposeView.zToField);
			verifyCc = obj.zTextAreaField
					.zGetInnerHTML(page.zComposeView.zCcField);

			Assert.assertTrue(!(verifyTo.equals("")),
					"Shortcut R doesn't work for replying a message");
			Assert.assertTrue(verifyCc.equals(""),
					"Shortcut R doesn't work for replying a message");
		} else if (actionType.equals("replyall")) {
			verifyTo = obj.zTextAreaField
					.zGetInnerText(page.zComposeView.zToField);
			verifyCc = obj.zTextAreaField
					.zGetInnerText(page.zComposeView.zCcField);

			Assert.assertTrue(!(verifyTo.equals("")),
					"Shortcut A doesn't work for reply all to a message");
			Assert.assertTrue(!(verifyCc.equals("")),
					"Shortcut A doesn't work for reply all to a message");
		} else if (actionType.equals("forward")) {
			verifyTo = obj.zTextAreaField
					.zGetInnerHTML(page.zComposeView.zToField);
			verifyCc = obj.zTextAreaField
					.zGetInnerHTML(page.zComposeView.zCcField);

			Assert.assertTrue(verifyTo.equals(""),
					"Shortcut F doesn't work for forwarding a message");
			Assert.assertTrue(verifyCc.equals(""),
					"Shortcut F doesn't work for forwarding a message");
		} else if (actionType.equals("delete")) {
			obj.zMessageItem.zNotExists("test mail");
		}

		zRobot.keyPress(KeyEvent.VK_ESCAPE);
		zRobot.keyRelease(KeyEvent.VK_ESCAPE);
		SleepUtil.sleep(2000);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsJunkMessage() throws Exception {

		String[] recipients = { SelNGBase.selfAccountName.get() };
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "SF", "na", "shortcuts dont work with Selenium on Safari");

		ProvZCS.injectMessage(ProvZCS.getRandomAccount(), recipients, ProvZCS
				.getRandomAccount(), "mark as junk", "test content");

		page.zMailApp.ClickCheckMailUntilMailShowsUp("mark as junk");

		obj.zMessageItem.zClick("mark as junk");

		SleepUtil.sleep(1000);

		Robot zRobot = new Robot();

		zRobot.keyPress(KeyEvent.VK_M);
		zRobot.keyPress(KeyEvent.VK_J);
		zRobot.keyRelease(KeyEvent.VK_M);
		zRobot.keyRelease(KeyEvent.VK_J);

		SleepUtil.sleep(1000);
		obj.zMessageItem.zNotExists("mark as junk");

		obj.zFolder.zClick(localize(locator.junk));

		obj.zMessageItem.zExists("mark as junk");

		obj.zFolder.zClick(localize(locator.inbox));

		SelNGBase.needReset.set(false);

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsMoveMessage() throws Exception {

		// String[] recipients = { selfAccountName };
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "SF", "na", "shortcuts dont work with Selenium on Safari");

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName.get(), "", "",
				"move message", "test content", "");

		page.zMailApp.ClickCheckMailUntilMailShowsUp("move message");
		obj.zMessageItem.zClick("move message");
		SleepUtil.sleep(3000);
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_M);
		zRobot.keyPress(KeyEvent.VK_M);
		zRobot.keyRelease(KeyEvent.VK_M);
		zRobot.keyRelease(KeyEvent.VK_M);
		SleepUtil.sleep(2000);
		obj.zFolder.zClickInDlgByName(localize(locator.trash),
				localize(locator.moveMessage));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessage));
		obj.zMessageItem.zNotExists("move message");
		obj.zFolder.zClick(localize(locator.trash));
		obj.zMessageItem.zExists("move message");
		obj.zMessageItem.zClick("move message");

		SleepUtil.sleep(2000);
		zRobot.keyPress(KeyEvent.VK_M);
		zRobot.keyPress(KeyEvent.VK_M);
		zRobot.keyRelease(KeyEvent.VK_M);
		zRobot.keyRelease(KeyEvent.VK_M);
		SleepUtil.sleep(2000);
		obj.zFolder.zClickInDlgByName(localize(locator.inbox),
				localize(locator.moveMessage));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessage));
		SleepUtil.sleep(1000);
		obj.zMessageItem.zNotExists("move message");
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zExists("move message");

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsFlagMessage(int keyToPress, String actionType)
			throws Exception {

		String[] recipients = { SelNGBase.selfAccountName.get() };
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "SF", "na", "shortcuts dont work with Selenium on Safari");

		obj.zMessageItem.zClick("flag message");

		SleepUtil.sleep(1000);

		Robot zRobot = new Robot();

		zRobot.keyPress(KeyEvent.VK_M);
		zRobot.keyPress(keyToPress);
		zRobot.keyRelease(KeyEvent.VK_M);
		zRobot.keyRelease(keyToPress);

		SleepUtil.sleep(1000);

		if (actionType.equals("read"))
			obj.zMessageItem.zVerifyIsRead("flag message");
		else if (actionType.equals("unread"))
			obj.zMessageItem.zVerifyIsUnRead("flag message");
		else if (actionType.equals("flag"))
			obj.zMessageItem.zVerifyIsFlagged("flag message");
		else if (actionType.equals("unflag"))
			obj.zMessageItem.zVerifyIsNotFlagged("flag message");

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsGoToMailFolders(int keyToPress, String folderName)
			throws Exception {

		String[] recipients = { SelNGBase.selfAccountName.get() };
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		String subject="This is test for GoTo Folder Shortcuts";

		ProvZCS.injectMessage(ProvZCS.getRandomAccount(), recipients, ProvZCS
				.getRandomAccount(), subject, "test content");

		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		
		
		if(folderName.equals(localize(locator.draft))) {
			obj.zButton.zClick(localize(locator.reply));
			obj.zEditField.zType(localize(locator.subject), subject);
			obj.zButton.zClick(localize(locator.saveDraft));
			obj.zButton.zClick(localize(locator.cancel));
		} 	else {
			obj.zButton.zClick(page.zMailApp.zMoveBtn);
			obj.zFolder.zClickInDlgByName(folderName, localize(locator.moveMessage));
			obj.zButton.zClickInDlgByName(localize(locator.ok), localize(locator.moveMessage));
		}
		
		
		SleepUtil.sleep(2000);
		
		Robot zRobot = new Robot();
		
		zRobot.keyPress(KeyEvent.VK_V);
		zRobot.keyRelease(KeyEvent.VK_V);
		zRobot.keyPress(keyToPress);
		zRobot.keyRelease(keyToPress);
		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}
	
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsMoveToTrash_And_MoveToInbox() throws Exception {

		String[] recipients = { SelNGBase.selfAccountName.get() };
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		String subject = "Move to Trash";

		ProvZCS.injectMessage(ProvZCS.getRandomAccount(), recipients, ProvZCS
				.getRandomAccount(), subject, "test content");

		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);

		obj.zMessageItem.zClick(subject);

		SleepUtil.sleep(1000);

		Robot zRobot = new Robot();

		zRobot.keyPress(KeyEvent.VK_PERIOD);
		zRobot.keyPress(KeyEvent.VK_T);
		zRobot.keyRelease(KeyEvent.VK_PERIOD);
		zRobot.keyRelease(KeyEvent.VK_T);

		SleepUtil.sleep(1000);
		obj.zMessageItem.zNotExists(subject);

		obj.zFolder.zClick(localize(locator.trash));

		obj.zMessageItem.zExists(subject);
		
		
		
		obj.zMessageItem.zClick(subject);
		
		SleepUtil.sleep(1000);

		Robot zRobot1 = new Robot();

		zRobot1.keyPress(KeyEvent.VK_PERIOD);
		zRobot1.keyPress(KeyEvent.VK_I);
		zRobot1.keyRelease(KeyEvent.VK_PERIOD);
		zRobot1.keyRelease(KeyEvent.VK_I);

		SleepUtil.sleep(1000);
		obj.zMessageItem.zNotExists(subject);

		obj.zFolder.zClick(localize(locator.inbox));

		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);

	}

	
	
}
