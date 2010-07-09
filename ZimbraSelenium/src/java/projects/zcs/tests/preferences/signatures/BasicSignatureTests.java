package projects.zcs.tests.preferences.signatures;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 * 
 */

@SuppressWarnings("static-access")
public class BasicSignatureTests extends CommonTest {
	@DataProvider(name = "SigPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createTextSignature")
				|| test.equals("editTextSignature")
				|| test.equals("clearTextSignatureWoSave")
				|| test.equals("clearHtmlSignatureWoSave")
				|| test.equals("clearTextSignatureWithSave")
				|| test.equals("clearHtmlSignatureWithSave")
				|| test.equals("deleteTextSignatureWoSave")
				|| test.equals("deleteHtmlSignatureWoSave")
				|| test.equals("deleteTextSignatureWithSave")
				|| test.equals("deleteHtmlSignatureWithSave")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { {} };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	// Tests

	/**
	 * This test case is to create text signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTextSignature(String signatureName, String signatureBody)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");

		needReset = false;
	}

	/**
	 * Test To create text Signature and then edit the signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editTextSignature(String signatureName, String signatureBody)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zEditSignature("",
				getLocalizedData_NoSpecialChar(), "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Edited Signature should be saved");

		needReset = false;
	}

	/**
	 * This test case is to create text signature ClearTextSignatureWithoutClick
	 * on Save
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void clearTextSignatureWoSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);

		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);

		needReset = false;
	}

	/**
	 * This test case is to create text signature
	 * ClearHtmlSignatureWithoutClickonSave
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void clearHtmlSignatureWoSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"HTML");
		Thread.sleep(1000);

		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);

		needReset = false;
	}

	/**
	 * This test case is to create text signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void clearTextSignatureWithSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		// obj.zMenuItem.zClick(signatureName);
		selenium.clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zExists(signatureName);

		needReset = false;
	}

	/**
	 * This test case is to create text signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void clearHtmlSignatureWithSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		selenium.clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zExists(signatureName);

		needReset = false;
	}

	/**
	 * Test Case: Delete Text signature without saving.
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTextSignatureWoSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		selenium.clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		selenium
				.clickAt(
						"xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");
		obj.zEditField.zNotExists(signatureName);

		needReset = false;
	}

	/**
	 * Test Case: Delete Text signature without saving.
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteHtmlSignatureWoSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"HTML");
		Thread.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"HTML");
		Thread.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		selenium.clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		selenium
				.clickAt(
						"xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zNotExists(signatureName);

		needReset = false;
	}

	/**
	 * Test Case:deleteTextSignatureWithSave
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTextSignatureWithSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		Thread.sleep(500);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		selenium.clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		selenium
				.clickAt(
						"xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zNotExists(signatureName);

		needReset = false;
	}

	/**
	 * Test Case:deleteTextSignatureWithSave
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteHtmlSignatureWithSave(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"HTML");
		Thread.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"HTML");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		Thread.sleep(500);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		selenium.clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		selenium
				.clickAt(
						"xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zNotExists(signatureName);

		needReset = false;
	}

	private void handleRetry() throws Exception {
		// TODO Auto-generated method stub
		isExecutionARetry = false;// reset this to false
		zLogin();
	}
}
