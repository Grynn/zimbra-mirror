package projects.zcs.tests.mail.gui;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import framework.core.*;
import framework.util.HarnessException;
import framework.util.LmtpUtil;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access" })
public class MessageGUI extends CommonTest {
	public static final String zMailListItemChkBox = "id=zlhi__CLV__se";

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "MailDataProvider")
	protected Object[][] createData(Method method) throws ServiceException, HarnessException {
		return new Object[][] { { Stafzmprov.getRandomAccount(),
				"_selfAccountName_", "ccuser@testdomain.com",
				"bccuser@testdomain.com" } };
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
	/**
	 * This test injects 2 mail, select all > right clicks to it and verifies
	 * all enable disable menu items
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyRtClickMenusForMultipleMails(String from, String to,
			String cc, String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject[] = { "subject1", "subject2" };
		String body[] = { "body1", "body2" };
		for (int i = 0; i <= 1; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		SleepUtil.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		SleepUtil.sleep(1500);
		obj.zMessageItem.zRtClick("subject1");
		String[] enabledMenuItemsArray = { page.zMailApp.zMarkReadMenuIconBtn,
				page.zMailApp.zForwardMenuIconBtn,
				page.zMailApp.zDeleteMenuIconBtn,
				page.zMailApp.zPrintMenuEnaDisaBtn,
				page.zMailApp.zMoveMenuIconBtn, page.zMailApp.zJunkMenuIconBtn };
		for (int i = 0; i <= 5; i++) {
			obj.zMenuItem.zIsEnabled(enabledMenuItemsArray[i]);
		}

		String[] disabledMenuItemsArray = { page.zMailApp.zReplyMenuEnaDisaBtn,
				page.zMailApp.zReplyAllMenuEnaDisaBtn,
				page.zMailApp.zEditAsNewMenuEnaDisaBtn,
				page.zMailApp.zShowOriginalMenuEnaDisaBtn,
				page.zMailApp.zNewFilterMenuEnaDisaBtn };
		for (int i = 0; i <= 4; i++) {
			obj.zMenuItem.zIsDisabled(disabledMenuItemsArray[i]);
		}

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test injects 2 mail, select all and verifies all toolbar enable
	 * disable buttons
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyToolbarBtnForMultipleMails(String from, String to,
			String cc, String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject[] = { "subject1", "subject2" };
		String body[] = { "body1", "body2" };
		for (int i = 0; i <= 1; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		SleepUtil.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		SleepUtil.sleep(1500);
		String[] enabledToolbarItemsArray = { page.zMailApp.zNewMenuBtn,
				page.zMailApp.zGetMailBtn, page.zMailApp.zDeleteBtn,
				page.zMailApp.zMoveBtn, page.zMailApp.zForwardBtn,
				page.zMailApp.zJunkBtn, page.zMailApp.zTagBtn,
				page.zMailApp.zViewBtn, page.zMailApp.zPrintBtn };
		for (int i = 0; i <= 8; i++) {
			obj.zMenuItem.zIsEnabled(enabledToolbarItemsArray[i]);
		}

		String[] disabledToolbarItemsArray = { page.zMailApp.zReplyBtn,
				page.zMailApp.zReplyAllBtn, page.zMailApp.zDetachBtn2 };
		for (int i = 0; i <= 2; i++) {
			obj.zButton.zIsDisabled(disabledToolbarItemsArray[i]);
		}

		SelNGBase.needReset.set(false);
	}

	private void commonInjectMessage(String from, String to, String cc,
			String bcc, String subject, String body) throws Exception {
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { to };
		LmtpUtil.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
	}
}