package projects.zcs.tests.zcscommon;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class TagActionTestsForAllAppTab extends CommonTest {
	public static String name = "tagaction";
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagAndVerifyInAllTab")) {
			return new Object[][] { {} };
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
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagAndVerifyInAllTab() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "48696", "Js error (appCtxt.getById(this.tagId) is undefined) while navigate Mail tab");
		

		String tag = getLocalizedData_NoSpecialChar();
		String renameTag = getLocalizedData_NoSpecialChar();
		zCreateTag(tag);
		obj.zFolder.zClick(tag);
		obj.zFolder.zExists(page.zMailApp.zInboxFldr);
		zGoToApplication("Address Book");
		obj.zFolder.zClick(tag);
		obj.zFolder.zExists(page.zABCompose.zContactsFolder);
		zGoToApplication("Calendar");
		obj.zFolder.zClick(tag);
		obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
		zGoToApplication("Tasks");
		obj.zFolder.zClick(tag);
		obj.zFolder.zExists(page.zTaskApp.zTasksFolder);
		zGoToApplication("Documents");
		obj.zFolder.zClick(tag);
		obj.zFolder.zExists(page.zDocumentApp.zNotebookFolder);
		zGoToApplication("Briefcase");
		obj.zFolder.zClick(tag);
		obj.zFolder.zExists(page.zBriefcaseApp.zBriefcaseFolder);

		zRenameTag(tag, renameTag);
		obj.zFolder.zClick(renameTag);
		obj.zFolder.zExists(page.zBriefcaseApp.zBriefcaseFolder);
		zGoToApplication("Mail");
		obj.zFolder.zClick(renameTag);
		obj.zFolder.zExists(page.zMailApp.zInboxFldr);
		zGoToApplication("Address Book");
		obj.zFolder.zClick(renameTag);
		obj.zFolder.zExists(page.zABCompose.zContactsFolder);
		zGoToApplication("Calendar");
		obj.zFolder.zClick(renameTag);
		obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
		zGoToApplication("Tasks");
		obj.zFolder.zClick(renameTag);
		obj.zFolder.zExists(page.zTaskApp.zTasksFolder);
		zGoToApplication("Documents");
		obj.zFolder.zClick(renameTag);
		obj.zFolder.zExists(page.zDocumentApp.zNotebookFolder);

		zDeleteTag(renameTag);
		obj.zFolder.zNotExists(renameTag);
		zGoToApplication("Mail");
		obj.zFolder.zNotExists(renameTag);
		zGoToApplication("Address Book");
		obj.zFolder.zNotExists(renameTag);
		zGoToApplication("Calendar");
		obj.zFolder.zNotExists(renameTag);
		zGoToApplication("Tasks");
		obj.zFolder.zNotExists(renameTag);
		zGoToApplication("Briefcase");
		obj.zFolder.zNotExists(renameTag);

		SelNGBase.needReset.set(false);
	}
}