package projects.zcs.tests.tasks.folders;

import java.lang.reflect.Method;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access" })
public class DeleteTaskFolder extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("deleteTaskFolder")) {
			return new Object[][] { {} };
		} else {
			return new Object[][] { { "" } };
		}
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SleepUtil.sleep(2000);
		page.zTaskApp.zNavigateToTasks();
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
	 * Creates a task list folder Deletes the task list folder Verifies that the
	 * task list folder is deleted successfully
	 * 
	 */
	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTaskFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String taskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(taskList);
		obj.zTaskFolder.zExists(taskList);
		page.zTaskApp.zTaskListDelete(taskList);
		obj.zTaskFolder.zNotExists(taskList);

		SelNGBase.needReset.set(false);
	}

		private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}
