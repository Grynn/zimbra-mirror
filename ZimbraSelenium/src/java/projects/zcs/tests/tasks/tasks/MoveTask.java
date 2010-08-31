package projects.zcs.tests.tasks.tasks;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access" })
public class MoveTask extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("moveTask")) {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.low), getLocalizedData(3), "", } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="tasks";
		super.zLogin();
	}



	/**
	 * Creates simple task with sujbect, location, priority and body only
	 * Creates two task folders Moves the task from one folder to another
	 * Verifies that the task is moved to the destination folder
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveTask() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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

		SelNGBase.needReset.set(false);
	}


}
