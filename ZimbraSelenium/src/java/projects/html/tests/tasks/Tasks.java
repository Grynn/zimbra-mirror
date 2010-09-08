package projects.html.tests.tasks;

import java.lang.reflect.Method;

import org.apache.tools.ant.taskdefs.WaitFor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.SleepUtil;
import framework.util.RetryFailedTests;
import framework.util.Stafzmprov;
import projects.html.tests.CommonTest;

@SuppressWarnings( { "static-access", "unused" })
public class Tasks extends CommonTest {

	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {

		String test = method.getName();

		if (test.equals("createSimpleTask") || test.equals("deleteTask")) {

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
							localize(locator.inProgress), "50%", "", "" },
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.high), getLocalizedData(3), "",
							localize(locator.waitingOn), "70%", "", "" },
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.low), getLocalizedData(3), "",
							localize(locator.deferred), "10%", "", "" } };

		} else if (test.equals("createTaskInTaskList")
				|| test.equals("tagTask")
				|| test.equals("deleteTasksInTaskList")) {

			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), localize(locator.low),
					getLocalizedData(3), getLocalizedData_NoSpecialChar() } };

		} else if (test.equals("editTask")) {

			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), localize(locator.high),
					getLocalizedData(3), getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), localize(locator.low),
					getLocalizedData(3) } };

		} else if (test.equals("moveTask")) {

			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), localize(locator.high),
					getLocalizedData(3), getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };

		} else {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.low), getLocalizedData(3), "", } };
		}

	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "parallel", "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleTask(String subject, String location,
			String priority, String body) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTasks();

		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);

		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTask(String subject, String location, String priority,
			String body, String taskList, String progress,
			String progressPercent, String startDate, String endDate)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTasks();

		page.zTaskApp.zTaskCreate(subject, location, priority, body, taskList,
				progress, progressPercent, startDate, endDate);

		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTaskInTaskList(String subject, String location,
			String priority, String body, String taskListName) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		SleepUtil.sleepSmall();
		page.zTaskApp.zTaskListCreate(taskListName);
		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreateSimpleInTaskList(subject, location, priority,
				body, taskListName);
		page.zTaskApp.zTaskVerifyExistsInTaskList(subject, taskListName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTask(String subject, String location, String priority,
			String body) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTasks();

		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);

		obj.zMessageItem.zExists(subject);

		page.zTaskApp.zTaskDeleteToolbarBtn(subject);

		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editTask(String subject, String location, String priority,
			String body, String newSub, String newLoc, String newPriority,
			String newBody) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTasks();

		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);

		obj.zMessageItem.zExists(subject);

		page.zTaskApp.zTaskEdit(subject, newSub, newLoc, newPriority, newBody,
				"", "", "", "", "");

		obj.zMessageItem.zNotExists(subject);

		obj.zMessageItem.zExists(newSub);

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tagTask(String subject, String location, String priority,
			String body, String tagName) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zMailApp.zCreateTag(tagName);
		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);
		page.zTaskApp.zTaskTagToolbar(subject, tagName, "");
		obj.zMessageItem.zVerifyIsTagged(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveTask(String subject, String location, String priority,
			String body, String folder1, String folder2) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zTaskListCreate(folder1);

		page.zTaskApp.zTaskListCreate(folder2);

		page.zTaskApp.zNavigateToTasks();

		page.zTaskApp.zTaskCreateSimpleInTaskList(subject, location, priority,
				body, folder1);

		page.zTaskApp.zTaskMoveToolbar(subject, folder1, folder2);

		page.zTaskApp.zTaskVerifyExistsInTaskList(subject, folder2);

		page.zTaskApp.zTaskVeriyNotExistsInTaskList(subject, folder1);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTaskList() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String taskListName = getLocalizedData_NoSpecialChar();

		page.zTaskApp.zNavigateToTasks();

		page.zTaskApp.zTaskListCreate(taskListName);

		obj.zFolder.zExists(taskListName);

		SelNGBase.needReset.set(false);

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTaskList() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String taskListName = getLocalizedData_NoSpecialChar();

		page.zTaskApp.zTaskListCreate(taskListName);

		obj.zFolder.zExists(taskListName);
		// obj.zFolder.zExists("deletetasklist");

		page.zTaskApp.zNavigateToTasks();

		page.zTaskApp.zTaskListDelete(taskListName);

		obj.zFolder.zNotExists(taskListName);

		SelNGBase.needReset.set(false);

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameTaskList() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String taskList = getLocalizedData_NoSpecialChar();
		String newTaskList = getLocalizedData_NoSpecialChar();

		page.zTaskApp.zTaskListCreate(taskList);

		obj.zFolder.zExists(taskList);

		page.zTaskApp.zTaskListRename(taskList, newTaskList);

		SleepUtil.sleepSmall();

		obj.zFolder.zNotExists(taskList);
		obj.zFolder.zExists(newTaskList);

		SelNGBase.needReset.set(false);

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTasksInTaskList(String subject, String location,
			String priority, String body, String taskList) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zTaskListCreate(taskList);

		obj.zFolder.zExists(taskList);

		page.zTaskApp.zTaskCreateSimpleInTaskList(subject, location, priority,
				body, taskList);

		page.zTaskApp.zTaskVerifyExistsInTaskList(subject, taskList);

		page.zTaskApp.zTaskListDeleteAllTasks(taskList);

		page.zTaskApp.zTaskVeriyNotExistsInTaskList(subject, taskList);

		SelNGBase.needReset.set(false);

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAllTasksNegativeTest() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTaskListPage();

		obj.zButton.zClick(page.zTaskApp.zTaskListDeleteAllTasksBtn);

		obj.zToastAlertMessage.zAlertMsgExists(
				localize(locator.actionDeleteCheckConfirm),
				"Toast message to check the checkbox is incorrect");

		SelNGBase.needReset.set(false);

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTaskListNegativeTest() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String taskListName = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreate(taskListName);
		obj.zFolder.zClick(taskListName);
		SleepUtil.sleepSmall();
		obj.zButton.zClick(page.zTaskApp.zTaskListDeleteListBtn);
		SleepUtil.sleepSmall();
		obj.zToastAlertMessage.zAlertMsgExists(
				localize(locator.actionTaskListCheckConfirm),
				"Toast message to check the checkbox is incorrect");

		SelNGBase.needReset.set(false);
	}

	/**
	 * retry handler function
	 */
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}

}
