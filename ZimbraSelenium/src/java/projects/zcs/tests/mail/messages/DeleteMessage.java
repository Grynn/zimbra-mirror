package projects.zcs.tests.mail.messages;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access" })
public class DeleteMessage extends CommonTest {
	public static final String zMailListItemChkBox = "id=zlhi__CLV__se";
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "MailDataProvider")
	protected Object[][] createData(Method method) throws ServiceException {
		return new Object[][] { { ProvZCS.getRandomAccount(),
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
	 * This test injects 3 mail, delete it (trash) and verifies mail not exist
	 * into Inbox folder and check whether all properly moved to Trash folder or
	 * not. It also deletes from that and check for empty folder
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteMultipleMails(String from, String to, String cc,
			String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body3" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		SleepUtil.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		SleepUtil.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zDeleteIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		verifyInjectedMailsExists();
		SleepUtil.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		SleepUtil.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zDeleteIconBtn);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		verifyInjectedMailsNotExists();

		SelNGBase.needReset.set(false);
	}

	private void commonInjectMessage(String from, String to, String cc,
			String bcc, String subject, String body) throws Exception {
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { to };
		ProvZCS.injectMessage(from, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
	}

	private void verifyInjectedMailsNotExists() throws Exception {
		String subject[] = { "subject1", "subject2", "subject3", "subject4",
				"subject5" };
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zNotExists(subject[i]);
		}
	}

	private void verifyInjectedMailsExists() throws Exception {
		String subject[] = { "subject1", "subject2", "subject3", "subject4",
				"subject5" };
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zExists(subject[i]);
		}
	}
}