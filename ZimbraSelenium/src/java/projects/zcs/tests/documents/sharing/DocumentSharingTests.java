package projects.zcs.tests.documents.sharing;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

import projects.zcs.tests.CommonTest;

//written by Prashant Jaiswal

@SuppressWarnings( { "static-access" })
public class DocumentSharingTests extends CommonTest {

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
					localize(locator.notebook), Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleManager),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareDocViewerRghtsAndVerifyEditLink")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					localize(locator.notebook), Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleViewer),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareDocAdminRghtsAndVerifyDeletePage")
				|| test.equals("modifyDocShare")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					localize(locator.notebook), Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleAdmin),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("sharingDocToExternalGuest")
				|| test.equals("publicShareDoc")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					localize(locator.notebook), Stafzmprov.getRandomAccount(), "",
					getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { {} };
		}
	}


	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="documents";
		super.zLogin();
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "47183", "'permission denied: you do not have sufficient permissions' exception while accessing shared notebook folder");

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);
		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		zGoToApplication("Documents");
		obj.zFolder.zClick(mountFolderName);
		zWaitTillObjectExist("button", localize(locator.send));
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent("link=" + pageName),
				"The shared page is not displayed");
		page.zDocumentApp.zDeleteNotebookPage(mountFolderName, pageName,
				"ToolbarDelete");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zDocumentCompose.zNavigateToDocument();
		SleepUtil.sleep(1000);
		obj.zFolder.zDblClick(mountFolderName);
		obj.zFolder.zClick(mountFolderName);
		zWaitTillObjectExist("button", localize(locator.send));

		boolean bisExists = false;
		String sIsExists = new Boolean(bisExists).toString();
		bisExists = ClientSessionFactory.session().selenium().isElementPresent("link=" + localize(locator.edit));
		assertReport("false", sIsExists, "Edit link exists for viewer rights");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "47183", "'permission denied: you do not have sufficient permissions' exception while accessing shared notebook folder");

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zDocumentCompose.zNavigateToDocument();
		SleepUtil.sleep(1000);
		obj.zFolder.zDblClick(mountFolderName);
		obj.zFolder.zClick(mountFolderName);
		zWaitTillObjectExist("button", localize(locator.send));
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent("link="
				+ localize(locator.del)),
				"The shared page's delete link should be displayed");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		page.zSharing.zRevokeShare(folderName, "", "");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080", "'Share Accepted' & 'Share Modified' mail body missing sharing details");

		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName, "", attendee, role,
				"", "", "");

		page.zSharing.zModifySharedFolder(localize(locator.documents),
				folderName, localize(locator.shareRoleViewer), "", "", "");

		String currentloggedinuser = SelNGBase.selfAccountName.get();

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
		page.zLoginpage.zLoginToZimbraAjax(attendee);

		page.zSharing.zVerifyShareModifiedMail(currentloggedinuser, folderName,
				"", attendee, localize(locator.shareRoleViewer), "");
		page.zDocumentCompose.zNavigateToDocument();
		SleepUtil.sleep(1000);
		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34080", "'Share Accepted' & 'Share Modified' mail body missing sharing details");

		String currentloggedinuser = SelNGBase.selfAccountName.get();
		obj.zFolder.zClick(localize(locator.notebook));
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);

		page.zSharing.zShareFolder("Documents", folderName,
				localize(locator.shareWithGuest), attendee, role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
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
		bisExists = ClientSessionFactory.session().selenium().isElementPresent("link=" + localize(locator.del));
		assertReport("false", sIsExists, "Edit link exists for viewer rights");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zSharing.zShareFolder("Documents", folderName,
				localize(locator.shareWithPublic), "", role, "", "", "");
		obj.zButton.zClickInDlg(localize(locator.ok));
		SelNGBase.needReset.set(false);
	}
}