package projects.zcs.tests.sharing;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

//written by Prashant Jaiswal

@SuppressWarnings( { "static-access" })
public class DocumentSharing extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "DocSharingDataProvider")
	protected Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("shareDocManagerRightsAndDeleteSharedPage")
				|| test.equals("revokeShareDoc")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					localize(locator.notebook), ProvZCS.getRandomAccount(),
					localize(locator.shareRoleManager),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareDocViewerRghtsAndVerifyEditLink")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					localize(locator.notebook), ProvZCS.getRandomAccount(),
					localize(locator.shareRoleViewer),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareDocAdminRghtsAndVerifyDeletePage")
				|| test.equals("modifyDocShare")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					localize(locator.notebook), ProvZCS.getRandomAccount(),
					localize(locator.shareRoleAdmin),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("sharingDocToExternalGuest")
				|| test.equals("publicShareDoc")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					localize(locator.notebook), ProvZCS.getRandomAccount(), "",
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
		page.zDocumentCompose.zNavigateToDocument();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to share notbook with manager rights and then verify the shared page
	 * can be deleted from the sharee
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "DocSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareDocManagerRightsAndDeleteSharedPage(String pageName,
			String bodyContent, String folderName, String attendee,
			String role, String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);
		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		zKillBrowsers();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		zGoToApplication("Documents");
		obj.zFolder.zClick(mountFolderName);
		zWaitTillObjectExist("button", localize(locator.send));
		Assert.assertTrue(selenium.isElementPresent("link=" + pageName),
				"The shared page is not displayed");
		page.zDocumentApp.zDeleteNotebookPage(mountFolderName, pageName,
				"ToolbarDelete");

		needReset = false;
	}

	/**
	 * 
	 * Test to share notebook with viewer rights and verify Edit link does not
	 * exist
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "DocSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareDocViewerRghtsAndVerifyEditLink(String pageName,
			String bodyContent, String folderName, String attendee,
			String role, String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		zKillBrowsers();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zDocumentCompose.zNavigateToDocument();
		Thread.sleep(1000);
		obj.zFolder.zDblClick(mountFolderName);
		obj.zFolder.zClick(mountFolderName);
		zWaitTillObjectExist("button", localize(locator.send));

		boolean bisExists = false;
		String sIsExists = new Boolean(bisExists).toString();
		bisExists = selenium.isElementPresent("link=" + localize(locator.edit));
		assertReport("false", sIsExists, "Edit link exists for viewer rights");

		needReset = false;
	}

	/**
	 * To share the document with admin rights an then deleting the shared page
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "DocSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareDocAdminRghtsAndVerifyDeletePage(String pageName,
			String bodyContent, String folderName, String attendee,
			String role, String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		zKillBrowsers();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zDocumentCompose.zNavigateToDocument();
		Thread.sleep(1000);
		obj.zFolder.zDblClick(mountFolderName);
		obj.zFolder.zClick(mountFolderName);
		zWaitTillObjectExist("button", localize(locator.send));
		Assert.assertTrue(selenium.isElementPresent("link="
				+ localize(locator.del)),
				"The shared page's delete link should be displayed");

		needReset = false;
	}

	/**
	 * Test to verify revoke of Document folders
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "DocSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void revokeShareDoc(String pageName, String bodyContent,
			String folderName, String attendee, String role,
			String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		page.zSharing.zRevokeShare(folderName, "", "");

		needReset = false;
	}

	/**
	 * Test to modify the share and verify
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @param folderName
	 * @param attendee8
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "DocSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void modifyDocShare(String pageName, String bodyContent,
			String folderName, String attendee, String role,
			String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		page.zSharing.zModifySharedFolder(localize(locator.documents),
				folderName, localize(locator.shareRoleViewer), "", "", "");

		String currentloggedinuser = SelNGBase.selfAccountName;

		zKillBrowsers();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);

		page.zSharing.zVerifyShareModifiedMail(currentloggedinuser, folderName,
				"", attendee, localize(locator.shareRoleViewer), "");
		page.zDocumentCompose.zNavigateToDocument();
		Thread.sleep(1000);
		needReset = false;
	}

	/**
	 * Test to verify sharing of documents to external guests
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "DocSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sharingDocToExternalGuest(String pageName, String bodyContent,
			String folderName, String attendee, String role,
			String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;
		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName,
				localize(locator.shareWithGuest), attendee, role, "", "", "");

		zKillBrowsers();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				folderName, localize(locator.shareWithGuest), attendee, role,
				"");

		page.zSharing.zAcceptShare(mountFolderName);

		page.zDocumentCompose.zNavigateToDocument();
		obj.zFolder.zClick(mountFolderName);
		zWaitTillObjectExist("button", localize(locator.send));

		boolean bisExists = false;
		String sIsExists = new Boolean(bisExists).toString();
		bisExists = selenium.isElementPresent("link=" + localize(locator.del));
		assertReport("false", sIsExists, "Edit link exists for viewer rights");

		needReset = false;
	}

	/**
	 * Test to verify public sharing of documents
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "DocSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void publicShareDoc(String pageName, String bodyContent,
			String folderName, String attendee, String role,
			String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zSharing.zShareFolder("Documents", folderName,
				localize(locator.shareWithPublic), "", role, "", "", "");
		obj.zButton.zClickInDlg(localize(locator.ok));
		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}