package projects.zcs.tests.tasks.newtask;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access" })
public class BasicTaskTests extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createSimpleTask")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(1),
							localize(locator.low), getLocalizedData(3) },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(1),
							localize(locator.normal), getLocalizedData(3) },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(1),
							localize(locator.high), getLocalizedData(3) } };
		} else if (test.equals("createTask")) {
			return new Object[][] {
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.low), getLocalizedData(3), "",
							localize(locator.completed), "", "", "" },
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.normal), getLocalizedData(3), "",
							localize(locator.inProgress), "50", "", "" },
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.high), getLocalizedData(3), "",
							localize(locator.waitingOn), "70", "", "" },
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.low), getLocalizedData(3), "",
							localize(locator.deferred), "10", "", "" } };

		} else if (test.equals("createSimpleTaskInTaskList")
				|| test.equals("editTask") || test.equals("deleteTask")
				|| test.equals("moveTask")) {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.low), getLocalizedData(3), "", } };
		} else if (test.equals("taskView")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "", getLocalizedData(3) } };
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
	 * Creates simple task with sujbect, location, priority and body only
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleTask(String subject, String location,
			String priority, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);
		obj.zTaskItem.zExists(subject);

		needReset = false;
	}

	/**
	 * Creates simple task with sujbect, location, priority and body in the
	 * specified task folder
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleTaskInTaskList() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String taskList = getLocalizedData_NoSpecialChar();
		String subject = getLocalizedData(2);
		String location = getLocalizedData(1);
		String body = getLocalizedData(3);
		String priority = localize(locator.low);
		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskListCreateNewBtn(taskList);
		page.zTaskApp.zTaskCreateSimpleInTaskList(subject, location, priority,
				body, taskList);
		obj.zTaskFolder.zClick(taskList);
		obj.zTaskItem.zExists(subject);

		needReset = false;
	}

	/**
	 * Creates tasks with all details
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTask(String subject, String location, String priority,
			String body, String taskList, String progress,
			String progressPercent, String startDate, String endDate)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreate(subject, location, priority, body, taskList,
				progress, progressPercent, startDate, endDate);
		obj.zTaskItem.zExists(subject);
		page.zTaskApp.zTaskVerifyPercentProgress(subject, progressPercent,
				progress);

		needReset = false;
	}

	/**
	 * Creates a task with all details Edits majority of the details Verifies
	 * task is edited successfully
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editTask() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject = getLocalizedData_NoSpecialChar();
		String newSubject = getLocalizedData_NoSpecialChar();
		String location = getLocalizedData(2);
		String priority = localize(locator.high);
		String body = getLocalizedData(3);
		String progress = localize(locator.deferred);
		String progressPercent = "60";
		String newLocation = getLocalizedData(1);
		String newBody = getLocalizedData(3);
		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreate(subject, location, priority, body, "",
				progress, progressPercent, "", "");
		obj.zTaskItem.zExists(subject);
		page.zTaskApp.zTaskEdit(subject, newSubject, newLocation, "", newBody,
				"", "", "", "", "");
		obj.zTaskItem.zExists(newSubject);

		needReset = false;
	}

	/**
	 * Creates simple task with sujbect, location, priority and body only
	 * Deletes the task Verifies that the task is deleted
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTask() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject = getLocalizedData(1);
		String location = getLocalizedData(2);
		String priority = localize(locator.normal);
		String body = getLocalizedData(3);

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);
		obj.zTaskItem.zExists(subject);
		page.zTaskApp.zTaskDeleteToolbarBtn(subject);
		obj.zTaskItem.zNotExists(subject);

		needReset = false;
	}

	/**
	 * Creates simple task with sujbect, location, priority and body only
	 * Creates two task folders Moves the task from one folder to another
	 * Verifies that the task is moved to the destination folder
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveTask() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject = getLocalizedData(1);
		String location = getLocalizedData(2);
		String priority = localize(locator.normal);
		String body = getLocalizedData(3);
		String taskList = getLocalizedData_NoSpecialChar();
		String newTaskList = getLocalizedData_NoSpecialChar();

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskListCreateNewBtn(taskList);
		page.zTaskApp.zTaskCreateSimpleInTaskList(subject, location, priority,
				body, taskList);
		page.zTaskApp.zTaskListCreateNewBtn(newTaskList);
		page.zTaskApp.zTaskMoveToolbar(subject, taskList, newTaskList);
		obj.zTaskFolder.zClick(newTaskList);
		obj.zTaskItem.zExists(subject);
		obj.zFolder.zClick(localize(locator.tasks));

		needReset = false;
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void taskView(String subject, String location, String priority,
			String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);

		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.notStarted));
		obj.zTaskItem.zExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.completed));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.inProgress));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.waitingOn));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.deferred));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.notStarted));
		obj.zTaskItem.zClick(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksEditBtn);
		obj.zButton.zClick(localize(locator.notStarted));
		obj.zMenuItem.zClick(localize(locator.completed));
		obj.zButton.zClick(page.zTaskApp.zTasksSaveBtn);

		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.notStarted));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.completed));
		obj.zTaskItem.zExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.inProgress));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.waitingOn));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.deferred));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.completed));
		obj.zTaskItem.zClick(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksEditBtn);
		obj.zButton.zClick(localize(locator.completed));
		obj.zMenuItem.zClick(localize(locator.inProgress));
		obj.zButton.zClick(page.zTaskApp.zTasksSaveBtn);

		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.notStarted));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.completed));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.inProgress));
		obj.zTaskItem.zExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.waitingOn));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.deferred));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.inProgress));
		obj.zTaskItem.zClick(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksEditBtn);
		obj.zButton.zClick(localize(locator.inProgress));
		obj.zMenuItem.zClick(localize(locator.waitingOn));
		obj.zButton.zClick(page.zTaskApp.zTasksSaveBtn);

		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.notStarted));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.completed));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.inProgress));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.waitingOn));
		obj.zTaskItem.zExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.deferred));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.waitingOn));
		obj.zTaskItem.zClick(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksEditBtn);
		obj.zButton.zClick(localize(locator.waitingOn));
		obj.zMenuItem.zClick(localize(locator.deferred));
		obj.zButton.zClick(page.zTaskApp.zTasksSaveBtn);

		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.notStarted));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.completed));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.inProgress));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.waitingOn));
		obj.zTaskItem.zNotExists(subject);
		obj.zButton.zClick(page.zTaskApp.zTasksViewBtn);
		obj.zMenuItem.zClick(localize(locator.deferred));
		obj.zTaskItem.zExists(subject);

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
