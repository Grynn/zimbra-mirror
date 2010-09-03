package projects.zcs.tests.addressbook.sharing;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.util.RetryFailedTests;
import framework.util.Stafzmprov;

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
					localize(locator.contacts), Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleManager),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareABViewerRghtsAndVerifyRtClckContactMenu")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					localize(locator.contacts), Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleViewer),
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("shareABAdminRightsAndMoveSharedContact")
				|| test.equals("modifyABShare")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					localize(locator.contacts), Stafzmprov.getRandomAccount(),
					localize(locator.shareRoleAdmin),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("sharingABToExternalGuest")
				|| test.equals("publicShareAB")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					localize(locator.contacts), Stafzmprov.getRandomAccount(), "",
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
		//super.NAVIGATION_TAB="address book";
		super.zLogin();
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
		
		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(mountFolderName);
		obj.zContactListItem.zExists(cnLastName);
		page.zABApp.zDeleteContactAndVerify(cnLastName, "ToolbarDelete");
		SelNGBase.needReset.set(false);
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

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
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

		SelNGBase.needReset.set(false);
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

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zAcceptShare(mountFolderName);
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(mountFolderName);
		obj.zContactListItem.zExists(cnLastName);

		obj.zContactListItem.zClick(cnLastName);
		page.zABApp.zMoveContactAndVerify(cnLastName,
				localize(locator.emailedContacts), "ToolbarMove");

		SelNGBase.needReset.set(false);
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

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName, "", attendee,
				role, "", "", "");

		page.zSharing.zRevokeShare(folderName, "", "");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		checkForSkipException("hi", "na", "34080", "Share Accepted & Share Modified mail body missing sharing details");
		
		
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
		SelNGBase.selfAccountName.set(attendee);
		String currentloggedinuser = SelNGBase.selfAccountName.get();
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing
				.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
						folderName, "", attendee,
						localize(locator.shareRoleViewer), "");
		page.zSharing.zVerifyShareModifiedMail(currentloggedinuser, folderName,
				"", attendee, localize(locator.shareRoleManager), "");

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("hi", "na", "34084", "Share Created mail body missing external guest url informations");
		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		String currentloggedinuser = SelNGBase.selfAccountName.get();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName,
				localize(locator.shareWithGuest), attendee, role, "", "", "");

		resetSession();
		SelNGBase.selfAccountName.set(attendee);
		page.zLoginpage.zLoginToZimbraAjax(attendee);
		page.zSharing.zVerifyShareCreatedMailInInboxFolder(currentloggedinuser,
				folderName, localize(locator.shareWithGuest), attendee, role,
				"");

		page.zSharing.zAcceptShare(mountFolderName);

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(mountFolderName);
		obj.zContactListItem.zExists(cnLastName);

		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstName;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		page.zSharing.zShareFolder("Address Book", folderName,
				localize(locator.shareWithPublicLong), "", "", "", "", "");

		SelNGBase.needReset.set(false);
	}
}