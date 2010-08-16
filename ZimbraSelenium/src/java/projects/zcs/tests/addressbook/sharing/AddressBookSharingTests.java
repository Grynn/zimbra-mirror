package projects.zcs.tests.addressbook.sharing;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.util.RetryFailedTests;

//written by Prashant Jaiswal

@SuppressWarnings( { "static-access" })
public class AddressBookSharingTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABSharingDataProvider")
	protected Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("shareABManagerRightsAndDeleteSharedContact")
				|| test.equals("revokeShareAB")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					localize(locator.contacts), ProvZCS.getRandomAccount(),
					localize(locator.shareRoleManager),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareABViewerRghtsAndVerifyRtClckContactMenu")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					localize(locator.contacts), ProvZCS.getRandomAccount(),
					localize(locator.shareRoleViewer),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareABAdminRightsAndMoveSharedContact")
				|| test.equals("modifyABShare")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					localize(locator.contacts), ProvZCS.getRandomAccount(),
					localize(locator.shareRoleAdmin),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("sharingABToExternalGuest")
				|| test.equals("publicShareAB")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					localize(locator.contacts), ProvZCS.getRandomAccount(), "",
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
		// page.zABCompose.navigateTo(ActionMethod.DEFAULT);
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

	/**
	 * 
	 * Test to Share Address Book with Manager Rights and to verify shared
	 * contact delete
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareABManagerRightsAndDeleteSharedContact(String cnLastName,
			String cnMiddleName, String cnFirstName, String folderName,
			String attendee, String role, String mountFolderName)
			throws Exception {
		
		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(mountFolderName);
		obj.zContactListItem.zExists(cnLastName);
		page.zABApp.zDeleteContactAndVerify(cnLastName, "ToolbarDelete");
		needReset = false;
	}

	/**
	 * 
	 * Test to share AddressBook with Viewer Rights and to verify right click
	 * menu's for shared contact
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareABViewerRghtsAndVerifyRtClckContactMenu(String cnLastName,
			String cnMiddleName, String cnFirstName, String folderName,
			String attendee, String role, String mountFolderName)
			throws Exception {

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(mountFolderName);
		obj.zContactListItem.zExists(cnLastName);
		obj.zButton.zIsDisabled(page.zABApp.zEditContactIcon);
		obj.zButton.zIsDisabled(page.zABApp.zDeleteContactIcon);

		obj.zContactListItem.zRtClick(cnLastName);
		obj.zMenuItem.zIsDisabled(localize(locator.editContact));
		obj.zMenuItem.zIsDisabled(localize(locator.del));
		obj.zMenuItem.zIsDisabled(localize(locator.move));
		obj.zMenuItem.zIsEnabled(localize(locator.print));
		obj.zMenuItem.zIsEnabled(localize(locator.newEmail));

		needReset = false;
	}

	/**
	 * 
	 * Test to Share contact with Admin Rights and Verify shared contact move
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shareABAdminRightsAndMoveSharedContact(String cnLastName,
			String cnMiddleName, String cnFirstName, String folderName,
			String attendee, String role, String mountFolderName)
			throws Exception {

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(mountFolderName);
		obj.zContactListItem.zExists(cnLastName);

		obj.zContactListItem.zClick(cnLastName);
		page.zABApp.zMoveContactAndVerify(cnLastName,
				localize(locator.emailedContacts), "ToolbarMove");

		needReset = false;
	}

	/**
	 * Test to check the revoke share functionality for Contacts folder
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void revokeShareAB(String cnLastName, String cnMiddleName,
			String cnFirstName, String folderName, String attendee,
			String role, String mountFolderName) throws Exception {

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		page.zSharing.zRevokeShare(folderName, "", "");

		needReset = false;
	}

	/**
	 * 
	 * Test to modify the sharing rights and verify
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void modifyABShare(String cnLastName, String cnMiddleName,
			String cnFirstName, String folderName, String attendee,
			String role, String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				localize(locator.shareRoleViewer), "", "", "");
		page.zSharing.zModifySharedFolder("Address Book", folderName,
				localize(locator.shareRoleManager), "", "", "");

		resetSession();
		SelNGBase.selfAccountName = attendee;
		String currentloggedinuser = SelNGBase.selfAccountName;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing
				.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
						folderName, "", attendee,
						localize(locator.shareRoleViewer), "");
		page.zSharing.zVerifyShareModifiedMail(currentloggedinuser, folderName,
				"", attendee, localize(locator.shareRoleManager), "");

		needReset = false;
	}

	/**
	 * Test to share the Address Book to external guest.Verify the sharing
	 * message to the attendee and shared contacts folder under Address Book tab
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sharingABToExternalGuest(String cnLastName,
			String cnMiddleName, String cnFirstName, String folderName,
			String attendee, String role, String mountFolderName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentloggedinuser = SelNGBase.selfAccountName;

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName,
				localize(locator.shareWithGuest), attendee, role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName = attendee;
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				folderName, localize(locator.shareWithGuest), attendee, role,
				"");

		page.zSharing.zAcceptShare(mountFolderName);

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(mountFolderName);
		obj.zContactListItem.zExists(cnLastName);

		needReset = false;
	}

	/**
	 * Test to share the Address Book publicly
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param folderName
	 * @param attendee
	 * @param role
	 * @param mountFolderName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABSharingDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void publicShareAB(String cnLastName, String cnMiddleName,
			String cnFirstName, String folderName, String attendee,
			String role, String mountFolderName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName,
				localize(locator.shareWithPublicLong), "", "", "", "", "");

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