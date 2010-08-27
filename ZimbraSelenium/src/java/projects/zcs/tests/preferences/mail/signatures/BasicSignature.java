package projects.zcs.tests.preferences.mail.signatures;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 * 
 */

@SuppressWarnings("static-access")
public class BasicSignature extends CommonTest {
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
		SelNGBase.isExecutionARetry.set(false);
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	// Tests

	/**
	 * This test case is to create text signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTextSignature(String signatureName, String signatureBody)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test To create text Signature and then edit the signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editTextSignature(String signatureName, String signatureBody)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zEditSignature("",
				getLocalizedData_NoSpecialChar(), "TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Edited Signature should be saved");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);

		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"HTML");
		SleepUtil.sleep(1000);

		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test case is to create text signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void clearTextSignatureWithSave(String signatureName,
			String signatureBody) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		// obj.zMenuItem.zClick(signatureName);
		SelNGBase.selenium.get().clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zExists(signatureName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test case is to create text signature
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void clearHtmlSignatureWithSave(String signatureName,
			String signatureBody) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		SelNGBase.selenium.get().clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		obj.zButton.zClick(localize(locator.clear));
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zExists(signatureName);

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		SelNGBase.selenium.get().clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		SelNGBase.selenium.get()
				.clickAt(
						"xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");
		obj.zEditField.zNotExists(signatureName);

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"HTML");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"HTML");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		SelNGBase.selenium.get().clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		SelNGBase.selenium.get()
				.clickAt(
						"xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zNotExists(signatureName);

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		SleepUtil.sleep(500);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		SelNGBase.selenium.get().clickAt("xpath=//tr[contains(@id,'DWT')]/td[contains(text(),'"
				+ signatureName + "')]", "");
		SelNGBase.selenium.get()
				.clickAt(
						"xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");
		obj.zEditor.zExists(signatureBody);
		obj.zEditField.zNotExists(signatureName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:deleteTextSignatureWithSave
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "f" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteHtmlSignatureWithSave(String signatureName,
			String signatureBody) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String signatureName1 = getLocalizedData_NoSpecialChar();
		String signatureBody1 = getLocalizedData_NoSpecialChar();

		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"HTML");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.addSignature));
		page.zSignaturePref.zCreateSignature(signatureName1, signatureBody1,
				"HTML");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");
		SleepUtil.sleep(500);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		SelNGBase.selenium.get().clickAt("css=tr[id^=DWT]>td:contains("+ signatureName +")", "");
		SelNGBase.selenium.get().clickAt("xpath=//td[contains(@class,'ZOptionsField')]//table//tr/td[contains(@id,'_title') and contains(text(),'"
								+ localize(locator.del) + "')]", "");

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (!SelNGBase.selenium.get().isTextPresent(signatureName)) break; } catch (Exception e) {}
			SleepUtil.sleep(1000);
		}
		Assert.assertFalse(SelNGBase.selenium.get().isTextPresent(signatureBody));
		
		SelNGBase.needReset.set(false);
	}

	private void handleRetry() throws Exception {
		// TODO Auto-generated method stub
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}
}
