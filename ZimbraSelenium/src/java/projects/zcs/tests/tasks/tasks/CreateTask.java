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
public class CreateTask extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createSimpleTask")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar()} 
					};
		} else if (test.equals("createSimpleTaskWithPriority")) {
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

		} else if (test.equals("createSimpleTaskInTaskList")) {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.low), getLocalizedData(3), "", } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SleepUtil.sleep(2000);
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Creates simple task with minimal required information
	 * 
	 */
	@Test(
			dataProvider = "taskCreateDataProvider", 
			groups = { "sanity", "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void createSimpleTask(String subject) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreateSimple(subject, "", "", "");
		obj.zTaskItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Creates simple task with high, normal, and low priority
	 * 
	 */
	@Test(
			dataProvider = "taskCreateDataProvider", 
			groups = { "sanity", "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void createSimpleTaskWithPriority(String subject, String location,
			String priority, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreateSimple(subject, location, priority, body);
		obj.zTaskItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Creates simple task with sujbect, location, priority and body in the
	 * specified task folder
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleTaskInTaskList() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
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

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		checkForSkipException("da,ar", "na", "na", "Percentage special character different in da, ar locale");

		page.zTaskApp.zNavigateToTasks();
		page.zTaskApp.zTaskCreate(subject, location, priority, body, taskList,
				progress, progressPercent, startDate, endDate);
		obj.zTaskItem.zExists(subject);
		page.zTaskApp.zTaskVerifyPercentProgress(subject, progressPercent,
				progress);

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
