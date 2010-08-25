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
public class EditTask extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if ( test.equals("editTask")) {
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
	 * Creates a task with all details Edits majority of the details Verifies
	 * task is edited successfully
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editTask() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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
