package projects.zcs.tests.tasks.tags;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class TagTaskTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForTaskAndVerify")
				|| test.equals("verifyTagFunctionalityFor2TaskAndRemoveTag")
				|| test.equals("applyMutlipleTagToTaskAndVerify")
				|| test.equals("applyTagByDnDTagToTaskAndViceVersa")
				|| test.equals("tryToCreateDuplicateTagInTasks")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Tasks");
		isExecutionARetry = false;
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

	/**
	 * Verify create, rename & delete functionality for tag for tasks
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForTaskAndVerify(String taskName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, newTag1;
		page.zTaskApp.zTaskCreateSimple(taskName, "", "", "");
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zTaskItem.zClick(taskName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zTaskItem.zVerifyIsTagged(taskName);
		Thread.sleep(1000);

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		Thread.sleep(1000);
		obj.zTaskItem.zVerifyIsTagged(taskName);

		zDeleteTag(newTag1);
		obj.zTaskItem.zClick(taskName);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Create 2 tag, apply 1 tag to each task and verify task exist / not exist
	 * by clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2TaskAndRemoveTag(String taskName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String taskName2, tag1, tag2;
		taskName2 = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskCreateSimple(taskName, "", "", "");
		page.zTaskApp.zTaskCreateSimple(taskName2, "", "", "");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zTaskItem.zClick(taskName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zTaskItem.zVerifyIsTagged(taskName);
		Thread.sleep(1000);
		obj.zTaskItem.zClick(taskName2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zTaskItem.zVerifyIsTagged(taskName2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zTaskItem.zExists(taskName);
		assertReport("false", obj.zTaskItem.zExistsDontWait(taskName2),
				"Verify task2 not exists");
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zTaskItem.zExists(taskName2);
		assertReport("false", obj.zTaskItem.zExistsDontWait(taskName),
				"Verify task1 not exists");
		obj.zFolder.zClick(localize(locator.tasks));
		Thread.sleep(1000);
		assertReport("true", obj.zTaskItem.zExistsDontWait(taskName),
				"Verify task1 not exists");
		assertReport("true", obj.zTaskItem.zExistsDontWait(taskName2),
				"Verify task2 not exists");

		obj.zFolder.zClick(localize(locator.tasks));
		obj.zTaskItem.zClick(taskName2);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		needReset = false;
	}

	/**
	 * Create 2 tag, apply both tag to task and verify both task exists after
	 * clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyMutlipleTagToTaskAndVerify(String taskName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1, tag2;
		page.zTaskApp.zTaskCreateSimple(taskName, "", "", "");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		obj.zTaskItem.zClick(taskName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag1);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zTaskItem.zVerifyIsTagged(taskName);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zTaskItem.zVerifyIsTagged(taskName);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zTaskItem.zExists(taskName);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zTaskItem.zExists(taskName);

		needReset = false;
	}

	/**
	 * Verify drag n drop functionality for tag and task. Drag task to tag and
	 * verify tag applied & same way drag tag to task and verify tag applied
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyTagByDnDTagToTaskAndViceVersa(String taskName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String taskName2, tag1, tag2;
		taskName2 = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskCreateSimple(taskName, "", "", "");
		page.zTaskApp.zTaskCreateSimple(taskName2, "", "", "");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zCreateTag(tag2);

		zDragAndDrop("//tr[contains(@id, 'zlif__TKL')]//td[contains(text(), '"
				+ taskName + "')]",
				"//td[contains(@id, 'zti__main_Tasks') and contains(text(), '"
						+ tag1 + "')]");
		obj.zTaskItem.zVerifyIsTagged(taskName);
		Thread.sleep(1000);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zTaskItem.zExists(taskName);

		obj.zFolder.zClick(localize(locator.tasks));
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Tasks') and contains(text(), '"
						+ tag2 + "')]",
				"//tr[contains(@id, 'zlif__TKL')]//td[contains(text(), '"
						+ taskName2 + "')]");
		obj.zTaskItem.zVerifyIsTagged(taskName2);
		Thread.sleep(1000);
		obj.zFolder.zClick(localize(locator.tasks));
		Thread.sleep(1000);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zTaskItem.zExists(taskName2);
		assertReport("false", obj.zTaskItem.zExistsDontWait(taskName),
				"Verify task1 not exists");

		needReset = false;
	}

	/**
	 * Try to create duplicate tag and verify its not allowed
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTagInTasks(String taskName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String tag1;
		tag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zDuplicateTag(tag1);

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