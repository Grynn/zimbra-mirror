package projects.html.tests.compose;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.zcs.clients.ProvZCS;

/**
 * This class file contains tests for compose To, Cc and Bcc buttons using GAL
 * search and verifies accordingly
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class ComposeToCcBccBtnTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) throws Exception {
		// String test = method.getName();
		return new Object[][] { { "_selfAccountName_",
				ProvZCS.getRandomAccount(), ProvZCS.getRandomAccount(),
				getLocalizedData_NoSpecialChar(),
				getLocalizedData_NoSpecialChar(), "" } };
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/*
	 * This test adds multiple receipients in To text area using To button (GAL
	 * search) and verifies whether all the receipients intact properly in To
	 * text area field
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addReceipientsThroughToButton(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String firstAcc = ProvZCS.getRandomAccount();
		String secondAcc = ProvZCS.getRandomAccount();
		String thirdAcc = ProvZCS.getRandomAccount();
		String addReceipAcc = firstAcc + "," + secondAcc + "," + thirdAcc;

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zAddReceipientsThroughToCcBccBtns("to", addReceipAcc,
				"", "", "");
		page.zComposeView.zVerifyToCcBccFields("to",
				"Verify To field value using To button", addReceipAcc, cc, bcc);
		//obj.zButton.zClick(page.zComposeView.zCancelBtn);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		Thread.sleep(LONG_WAIT);
		obj.zButton.zClick(page.zMailApp.zDraftFldr);


		needReset = false;
	}

	/*
	 * This test adds multiple receipients in Cc text area using Cc button (GAL
	 * search) and verifies whether all the receipients intact properly in Cc
	 * text area field
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addReceipientsThroughCcButton(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String firstAcc = ProvZCS.getRandomAccount();
		String secondAcc = ProvZCS.getRandomAccount();
		String thirdAcc = ProvZCS.getRandomAccount();
		String addReceipAcc = firstAcc + "," + secondAcc + "," + thirdAcc;

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zAddReceipientsThroughToCcBccBtns("cc", "",
				addReceipAcc, "", "");
		page.zComposeView.zVerifyToCcBccFields("cc",
				"Verify Cc field value using Cc button", to, addReceipAcc, bcc);
		//obj.zButton.zClick(page.zComposeView.zCancelBtn);
		obj.zButton.zClick(page.zComposeView.zSaveDraftsBtn);
		Thread.sleep(MEDIUM_WAIT);
		obj.zButton.zClick(page.zMailApp.zDraftFldr);


		needReset = false;
	}

	/*
	 * This test adds multiple receipients in Bcc text area using Bcc button
	 * (GAL search) and verifies whether all the receipients intact properly in
	 * Bcc text area field
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addReceipientsThroughBccButton(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String firstAcc = ProvZCS.getRandomAccount();
		String secondAcc = ProvZCS.getRandomAccount();
		String thirdAcc = ProvZCS.getRandomAccount();
		String addReceipAcc = firstAcc + "," + secondAcc + "," + thirdAcc;

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zAddReceipientsThroughToCcBccBtns("bcc", "", "",
				addReceipAcc, "");
		page.zComposeView
				.zVerifyToCcBccFields("bcc",
						"Verify Bcc field value using Bcc button", to, cc,
						addReceipAcc);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);

		needReset = false;
	}

	/*
	 * This test adds multiple receipients in To, Cc and Bcc text area using To
	 * button (GAL search) and verifies whether all the receipients intact
	 * properly in To, Cc and Bcc text area respectively
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addReceipientsThroughToCcBccButtons(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String firstAcc = ProvZCS.getRandomAccount();
		String secondAcc = ProvZCS.getRandomAccount();
		String thirdAcc = ProvZCS.getRandomAccount();
		String addReceipAcc = firstAcc + "," + secondAcc + "," + thirdAcc;

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zAddReceipientsThroughToCcBccBtns("to", addReceipAcc,
				addReceipAcc, addReceipAcc, "");
		page.zComposeView
				.zVerifyToCcBccFields(
						"toccbcc",
						"Verify To, Cc, Bcc field values using To, Cc, Bcc buttons respectively",
						addReceipAcc, addReceipAcc, addReceipAcc);
		obj.zButton.zClick(page.zComposeView.zCancelBtn);

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}