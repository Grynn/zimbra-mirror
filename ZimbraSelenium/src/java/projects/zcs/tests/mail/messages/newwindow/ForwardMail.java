package projects.zcs.tests.mail.messages.newwindow;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.SelNGBase;
import framework.util.LmtpUtil;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class ForwardMail extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "dataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("forwardMsgFromNewWindow")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(), "ccuser@testdomain.com",
					"bccuser@testdomain.com",
					getOnlyEnglishAlphabetCharAndNumber(),
					getOnlyEnglishAlphabetCharAndNumber(), "" } };
		} else {
			return new Object[][] { { "" } };
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
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardMsgFromNewWindow(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"na",
				"SF",
				"39446",
				"New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		LmtpUtil.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		SleepUtil.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn_newWindow);
		SleepUtil.sleep(1500);
		obj.zEditField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName.get());
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SleepUtil.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Fwd: " + subject);

		SelNGBase.needReset.set(false);
	}
}