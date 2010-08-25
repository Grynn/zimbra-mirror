package projects.zcs.tests.tags.addressbook;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class AddressbookTags extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForContactAndVerify")
				|| test.equals("verifyTagFunctionalityFor2ContactAndRemoveTag")
				|| test.equals("applyMutlipleTagToContactAndVerify")
				|| test.equals("editContactAndVerifyAddRemoveTag")
				|| test.equals("editContactGroupAndVerifyAddRemoveTag")
				|| test
						.equals("verifyTagFunctionalityFor2ContactGroupAndRemoveTag")
				|| test.equals("applyTagByDnDTagToContactAndViceVersa")
				|| test.equals("tryToCreateDuplicateTagInAddressBook")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Address Book");
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
	//--------------------------------------------------------------------------


	/**
	 * Try to create duplicate tag and verify its not allowed
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTagInAddressBook(String firstName,
			String lastName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1;
		tag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zDuplicateTag(tag1);

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}