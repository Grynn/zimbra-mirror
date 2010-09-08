package projects.zcs.tests.mail.compose;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.*;
import framework.util.LmtpUtil;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

import projects.zcs.tests.CommonTest;

@SuppressWarnings("static-access")
public class CancelCompose extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "dataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("cancelingComposeInNewWindowSavesDraft_Bug43560")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(),
					Stafzmprov.getRandomAccount(), "bccuser@testdomain.com",
					"subject_cancelingComposeInNewWindowSavesDraft_Bug43560",
					"body_cancelingComposeInNewWindowSavesDraft_Bug43560", "" } };
		} else if (test.equals("cancelingComposeInNewWindowAndSavingDraft")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					SelNGBase.selfAccountName.get(),
					Stafzmprov.getRandomAccount(), "bccuser@testdomain.com",
					"subject_cancelingComposeInNewWindowSavingDraft",
					"body_cancelingComposeInNewWindowSavingDraft", "" } };

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
	public void cancelingComposeInNewWindowSavesDraft_Bug43560(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		LmtpUtil.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick("id=zb__COMPOSE1__DETACH_COMPOSE_left_icon");
		SleepUtil.sleep(2000);
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		obj.zEditor.zType("Modifying body");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		SleepUtil.sleep(1000);
		ClientSessionFactory.session().selenium().selectWindow(null);
		obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
		SleepUtil.sleep(1000);
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject),
				"Verifying drafted message");

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cancelingComposeInNewWindowAndSavingDraft(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		LmtpUtil.injectMessage(from, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClick("id=zb__COMPOSE1__DETACH_COMPOSE_left_icon");
		SleepUtil.sleep(2000);
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		obj.zEditor.zType("Modifying body");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		SleepUtil.sleep(1000);
		ClientSessionFactory.session().selenium().selectWindow(null);
		obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}
}
