package projects.zcs.tests.addressbook.contacts;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.HarnessException;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */

public class TabOrder extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("test1")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(), "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "testexcelfile.xls" } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
	}
	
	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(description = "verify the tab order within the contacts app", dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tabOrderContacts01(String to, String cc, String bcc, String subject,
			String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		SelNGBase.needReset.set(false);
		
		
		throw new HarnessException("implement me!");
	}
}