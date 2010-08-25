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
public class MoveTaskFolder extends CommonTest {
	@DataProvider(name = "taskCreateDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("moveTaskFolder")) {
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


	@Test(dataProvider = "taskCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveTaskFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String orgTaskList = getLocalizedData_NoSpecialChar();
		page.zTaskApp.zTaskListCreateNewBtn(orgTaskList);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Tasks') and contains(text(), '"
						+ orgTaskList + "')]", page.zTaskApp.zTasksFolder);
		Assert
				.assertTrue(SelNGBase.selenium.get()
						.isElementPresent("//div[@id='zti__main_Tasks__15']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
								+ orgTaskList + "')]"));

		SelNGBase.needReset.set(false);
	}


	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}
