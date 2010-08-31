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
public class MoveMessage extends CommonTest {
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
	 * This test injects 3 mail, moves mails to Sent folder and verifies mail
	 * not exist into Inbox folder and check whether all properly moved to Sent
	 * folder or not.
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveMultipleMailsToSentFolder(String from, String to,
			String cc, String bcc) throws Exception {
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
		obj.zButton.zClick(page.zMailApp.zMoveIconBtn);
		String dlgName;
		dlgName = localize(locator.moveMessages);
		obj.zFolder.zClickInDlgByName(page.zMailApp.zSentFldrMoveDlg, dlgName);
		obj.zButton.zClickInDlgByName(localize(locator.ok), dlgName);
		// discard if unwanted dlg exists
		zPressBtnIfDlgExists(dlgName, localize(locator.ok),
				page.zMailApp.zSentFldrMoveDlg);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zSentFldr));
		verifyInjectedMailsExists();

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test injects 3 mail, moves mail to created new folder and verifies
	 * mail not exist into Inbox folder and check whether all properly moved to
	 * new folder or not.
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveMultipleMailsToNewFolder(String from, String to, String cc,
			String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String newFolder = getLocalizedData_NoSpecialChar();
		String subject[] = { "subject1", "subject2", "subject3" };
		String body[] = { "body1", "body2", "body3" };
		for (int i = 0; i <= 2; i++) {
			commonInjectMessage(from, to, cc, bcc, subject[i], body[i]);
		}
		SleepUtil.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		SleepUtil.sleep(1500);
		obj.zButton.zClick(page.zMailApp.zMoveIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator._new),
				localize(locator.moveMessages));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel), newFolder,
				localize(locator.createNewFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewFolder));
		obj.zFolder
				.zClickInDlgByName(newFolder, localize(locator.moveMessages));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessages));
		// discard if unwanted dlg exists
		zPressBtnIfDlgExists(localize(locator.moveMessages),
				localize(locator.ok), newFolder);
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		verifyInjectedMailsNotExists();
		obj.zFolder.zClick(newFolder);
		verifyInjectedMailsExists();

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