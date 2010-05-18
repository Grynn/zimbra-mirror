/**
 *Test cases related to preferences general
 * 
 * @author Prashant Jaiswal
 * 
 */

package projects.html.tests.preferences;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.html.clients.ProvZCS;

/**
 * @author VICKY JAISWAL
 * 
 */
@SuppressWarnings( { "static-access", "unused" })
public class GeneralPref extends CommonTest {
	private String constantSubjectForJunk = getLocalizedData_NoSpecialChar();
	private String constantSubjectForTrash = getLocalizedData_NoSpecialChar();

	@DataProvider(name = "GeneralPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("verifyIncludeJunkFolderInSearch")
				|| test.equals("verifyIncludeTrashFolderInSearch")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { {} };
		}
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {

		zLoginIfRequired();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to verify the change password functionality
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ChangePwdRelogin() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zGeneralPrefUI.zNavigateToChangePasswordWindow();

		page.zGeneralPrefUI.zEnterChangePWData("test123", "test321", "test321");
		obj.zButton.zClick("class=zLoginButton");
		Thread.sleep(MEDIUM_WAIT);

		zKillBrowsers();
		page.zLoginpage
				.zLoginToZimbraHTML(SelNGBase.selfAccountName, "test321");

		zKillBrowsers();

		String accountName = ProvZCS.getRandomAccount();
		SelNGBase.selfAccountName = accountName;
		page.zLoginpage.zLoginToZimbraHTML(accountName);

		needReset = false;

	}

	/**
	 * To verify the junk folder is included in search
	 * 
	 * @param body
	 * @throws Exception
	 */
	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyIncludeJunkFolderInSearch(String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zGeneralPrefUI.zNavigateToPrefGenralAndSelectSearchFolder("Junk");

		// to have a mail in Junk folder
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(ProvZCS.selfAccountName,
				"", "", constantSubjectForJunk, body, "");
		obj.zCheckbox.zClick(constantSubjectForJunk);
		Thread.sleep(SMALL_WAIT);
		obj.zHtmlMenu.zClick("name=actionOp", localize(locator.actionSpam));

		// To verify the message moved in junk folder
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zExists(constantSubjectForJunk);

		page.zGeneralPrefUI
				.zSearchUsingMainSearchField(ProvZCS.selfAccountName);

		obj.zMessageItem.zExists(constantSubjectForJunk);

		needReset = false;
	}

	/**
	 * Negative test to include junk folder in search
	 */
	@Test(dataProvider = "GeneralPrefDataProvider", dependsOnMethods = "verifyIncludeJunkFolderInSearch", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void NegativeTestIncludeJunkFolderInSearch() throws Exception {
		if (isExecutionARetry)
			IncludeJunkFolderInSearch();
		// This test works on the message which is in junk folder from test
		// verifyIncludeJunkFolderInSearch
		page.zGeneralPrefUI.zNavigateToPrefGenralAndSelectSearchFolder("Junk");

		page.zGeneralPrefUI
				.zSearchUsingMainSearchField(ProvZCS.selfAccountName);
		obj.zMessageItem.zNotExists(constantSubjectForJunk);

		needReset = false;
	}

	/**
	 * To verify trash folder is included in search
	 * 
	 * @param body
	 * @throws Exception
	 */
	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyIncludeTrashFolderInSearch(String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zGeneralPrefUI.zNavigateToPrefGenralAndSelectSearchFolder("Trash");

		// to have a mail in Junk folder
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(ProvZCS.selfAccountName,
				"", "", constantSubjectForTrash, body, "");
		obj.zCheckbox.zClick(constantSubjectForTrash);
		obj.zButton.zClick(localize(locator.del));

		// To verify the message moved in junk folder
		obj.zFolder.zClick(page.zMailApp.zTrashFldr);
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zExists(constantSubjectForTrash);

		page.zGeneralPrefUI
				.zSearchUsingMainSearchField(ProvZCS.selfAccountName);

		obj.zMessageItem.zExists(constantSubjectForTrash);

		needReset = false;
	}

	/**
	 * Negative test to test include trash folder in search
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "GeneralPrefDataProvider", dependsOnMethods = "verifyIncludeTrashFolderInSearch", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void NegativeTestIncludeTrashFolderInSearch() throws Exception {
		if (isExecutionARetry)
			IncludeTrashFolderInSearch();
		// This test works on the message which is in junk folder from test
		// verifyIncludeJunkFolderInSearch
		page.zGeneralPrefUI.zNavigateToPrefGenralAndSelectSearchFolder("Trash");

		page.zGeneralPrefUI
				.zSearchUsingMainSearchField(ProvZCS.selfAccountName);
		obj.zMessageItem.zNotExists(constantSubjectForTrash);

		needReset = false;
	}

	/**
	 * To verify search string is displayed in in the search bar
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAlwaysShowSrchString() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zGeneralPrefUI
				.zNavigateToPrefGenralAndSelectAlwaysShowSrchString();
		obj.zTab.zClick(localize(locator.mail));
		Thread.sleep(SMALL_WAIT);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		Thread.sleep(SMALL_WAIT);
		String actualValueDisplayed = obj.zEditField
				.zGetInnerText(page.zGeneralPrefUI.zFindEditFiled);
		Assert.assertTrue(actualValueDisplayed.equals("in:\"Inbox\""),
				"The string in:\"Inbox\" is not displayed in find edit field");

		needReset = false;
	}

	/**
	 * Negative test to include search string in the search string
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "GeneralPrefDataProvider", dependsOnMethods = "verifyAlwaysShowSrchString", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void NegativeTestAlwaysShowSrchString() throws Exception {
		if (isExecutionARetry)
			AlwaysShowSrchString();

		page.zGeneralPrefUI
				.zNavigateToPrefGenralAndSelectAlwaysShowSrchString();
		obj.zTab.zClick(localize(locator.mail));
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		String actualValueDisplayed = obj.zEditField
				.zGetInnerText(page.zGeneralPrefUI.zFindEditFiled);
		Assert.assertTrue(actualValueDisplayed.equals("<blank>"),
				"The Find edit field is not blank ");
		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

	private void IncludeJunkFolderInSearch() throws Exception {
		handleRetry();
		verifyIncludeJunkFolderInSearch(getLocalizedData_NoSpecialChar());
	}

	private void IncludeTrashFolderInSearch() throws Exception {
		handleRetry();
		verifyIncludeTrashFolderInSearch(getLocalizedData_NoSpecialChar());
	}

	private void AlwaysShowSrchString() throws Exception {
		handleRetry();
		verifyAlwaysShowSrchString();
	}
}
