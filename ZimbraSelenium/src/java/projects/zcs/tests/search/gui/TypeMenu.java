package projects.zcs.tests.search.gui;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;

import projects.zcs.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.HarnessException;
import framework.util.RetryFailedTests;

public class TypeMenu extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "dataProvider")
	public Object[][] createData(Method method) throws ServiceException{
		String test = method.getName();
		if (test.equals("test1")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("search");
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}
	
	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void test1(String name) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// dummy test

		SelNGBase.needReset.set(false);
		
		throw new HarnessException("implement me!");
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}