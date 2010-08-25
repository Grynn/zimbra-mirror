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
public class ViewTask extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
        if (test.equals("taskView")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "", getLocalizedData(3) } };
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


	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void taskView(String subject, String location, String priority,
			String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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
