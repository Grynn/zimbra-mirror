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

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access" })
public class DeleteDrafts extends CommonTest {
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
	 * This test creates 3 drafted mail, select all and verify enable disable
	 * menu items and also check for deletion UI
	 */
	@Test(dataProvider = "MailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteMultipleDraftedMails(String from, String to, String cc,
			String bcc) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"all",
				"na",
				"34080",
				"not able to create multiple drafts - first drafted message just hides its UI so while creating new draft it updates value in hidden draft message (selenium bug)");

		String subject[] = { "subject1", "subject2", "subject3" };
		saveDrafts();
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zMailApp.zDraftsFldr));
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zExists(subject[i]);
		}
		SleepUtil.sleep(1500);
		obj.zCheckbox.zClick(zMailListItemChkBox);
		SleepUtil.sleep(1500);
		obj.zButton.zIsDisabled(page.zMailApp.zMoveBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zReplyBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zReplyAllBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zEditDraftBtn);
		obj.zButton.zIsDisabled(page.zMailApp.zJunkBtn);
		obj.zButton.zClick(page.zMailApp.zDeleteIconBtn);
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zMailApp.zDraftsFldr));
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zNotExists(subject[i]);
		}
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		for (int i = 0; i <= 2; i++) {
			obj.zMessageItem.zExists(subject[i]);
		}

		SelNGBase.needReset.set(false);
	}

	private void saveDrafts() throws Exception {
		String subject[] = { "subject1", "subject2", "subject3" };
		for (int i = 0; i <= 2; i++) {
			page.zComposeView.zNavigateToMailCompose();
			obj.zEditField.zType(page.zComposeView.zSubjectField, subject[i]);
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		}
	}
}