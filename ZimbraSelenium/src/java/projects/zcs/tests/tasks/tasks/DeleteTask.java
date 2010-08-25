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
public class DeleteTask extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("deleteTask")) {
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
	 * Creates simple task with sujbect, location, priority and body only
	 * Deletes the task Verifies that the task is deleted
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTask() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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
