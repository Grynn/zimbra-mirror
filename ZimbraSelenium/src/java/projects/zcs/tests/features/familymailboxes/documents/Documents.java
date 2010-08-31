package projects.zcs.tests.features.familymailboxes.documents;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.HarnessException;
import framework.util.RetryFailedTests;
public class Documents extends CommonTest {

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		//super.NAVIGATION_TAB="documents";
		super.zLogin();
	}
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void familyDocuments_1() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// dummy test

		SelNGBase.needReset.set(false);
		
		throw new HarnessException("implement me!");
	}
}