package projects.zcs.tests.tasks.folders;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access" })
public class TaskFolderTests extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createTaskFolder") || test.equals("deleteTaskFolder")
				|| test.equals("renameTaskFolder")) {
			return new Object[][] { {} };
		} else {
			return new Object[][] { { "" } };
		}
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		Thread.sleep(2000);
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Creates a task list folder Verifies that the folder is created
	 * successfully
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		String taskListBtn = getLocalizedData_NoSpecialChar();
		String taskListRtClick = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(taskListBtn);
		Thread.sleep(1000);
		page.zTaskApp.zTaskListCreateRtClick(taskListRtClick);
		obj.zTaskFolder.zExists(taskListBtn);
		obj.zTaskFolder.zExists(taskListRtClick);

		needReset = false;
	}

	/**
	 * Creates a task list folder Deletes the task list folder Verifies that the
	 * task list folder is deleted successfully
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		String taskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(taskList);
		obj.zTaskFolder.zExists(taskList);
		page.zTaskApp.zTaskListDelete(taskList);
		obj.zTaskFolder.zNotExists(taskList);

		needReset = false;
	}

	/**
	 * Creates a task list folder Renames the task list folder Verifies that the
	 * task list folder is renamed successfully
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameTaskFolder() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		String orgTaskList = getLocalizedData_NoSpecialChar();
		String renamedTaskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(orgTaskList);
		obj.zTaskFolder.zExists(orgTaskList);
		page.zTaskApp.zTaskListRename(orgTaskList, renamedTaskList);
		obj.zTaskFolder.zNotExists(orgTaskList);
		obj.zTaskFolder.zExists(renamedTaskList);

		needReset = false;
	}

	/**
	 * retry handler function
	 */
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
