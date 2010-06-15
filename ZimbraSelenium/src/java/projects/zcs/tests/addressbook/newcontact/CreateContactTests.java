package projects.zcs.tests.addressbook.newcontact;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * This covers some Misc test cases related to address book
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class CreateContactTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABMiscDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("addressPicker")
				|| test.equals("addressPickerBug20969")) {
			return new Object[][] { { localize(locator.toLabel) } };
		} else if (test.equals("contextAddToContacts")) {
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
		// page.zABCompose.zNavigateToContact();
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
	 * test to select To/CC/BCC values in Select Address dlg box and verify them
	 * on mail compose
	 * 
	 * @param to
	 * @throws Exception
	 */
	@Test(dataProvider = "ABMiscDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addressPicker(String to) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zButton.zClick(page.zABCompose.zMailTabIconBtn);
		page.zComposeView.zNavigateToMailCompose();

		obj.zButton.zClick(to);

		String infoDlgExist;
		infoDlgExist = obj.zDialog.zExistsDontWait(localize(locator.infoMsg));
		if (infoDlgExist.equals("true")) {
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.infoMsg), "2");
		}

		obj.zDialog.zExists(localize(locator.selectAddresses));
		obj.zEditField.zActivateInDlg(localize(locator.search), "");
		for (int i = 1; i <= 3; i++) {
			ProvZCS.createAccount("ac" + i + "@testdomain.com");

			obj.zEditField.zTypeInDlg(localize(locator.search), "ac" + i
					+ "@testdomain.com");

			obj.zButton.zClickInDlg(localize(locator.search));

			if (i == 1) {
				obj.zButton.zClickInDlg(localize(locator.to));
			} else if (i == 2) {
				obj.zButton.zClickInDlg(localize(locator.cc));
			} else {
				obj.zButton.zClickInDlg(localize(locator.bcc));
			}
		}

		obj.zButton.zClickInDlg(localize(locator.ok));

		String toValue = obj.zTextAreaField
				.zGetInnerText(localize(locator.toLabel));
		String ccValue = obj.zTextAreaField
				.zGetInnerText(localize(locator.ccLabel));
		String bccValue = obj.zTextAreaField
				.zGetInnerText(localize(locator.bccLabel));

		Assert.assertTrue(toValue.contains("ac1" + "@testdomain.com")
				|| ccValue.contains("ac2" + "@testdomain.com")
				|| bccValue.contains("ac3" + "@testdomain.com"),
				"to/cc/bcc field not picked properly from address picker");
		needReset = false;

	}

	/**
	 * Test related to address picker w.r.t. bug 20969
	 * 
	 * @param to
	 * @throws Exception
	 */
	@Test(dataProvider = "ABMiscDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addressPickerBug20969(String to) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zButton.zClick(page.zABCompose.zMailTabIconBtn);
		page.zComposeView.zNavigateToMailCompose();

		obj.zButton.zClick(to);

		String infoDlgExist;
		Thread.sleep(3000);
		infoDlgExist = obj.zDialog.zExistsDontWait(localize(locator.infoMsg));
		if (infoDlgExist.equals("true")) {
			obj.zButton.zClickInDlg(localize(locator.ok), "2");
			Thread.sleep(1000);
		}

		obj.zDialog.zExists(localize(locator.selectAddresses));
		obj.zEditField.zActivateInDlg(localize(locator.search), "");

		for (int i = 1; i <= 3; i++) {
			ProvZCS.createAccount("ab" + i + "@testdomain.com");

			obj.zEditField.zTypeInDlg(localize(locator.search), "ab" + i
					+ "@testdomain.com");

			obj.zButton.zClickInDlg(localize(locator.search));
			if (currentBrowserName.contains("Safari")) {
				obj.zButton.zClickInDlg(localize(locator.search));
			}
			if (i == 1) {
				obj.zButton.zClickInDlg(localize(locator.to));
			} else if (i == 2) {
				obj.zButton.zClickInDlg(localize(locator.cc));
			} else if (i == 3) {
				obj.zButton.zClickInDlg(localize(locator.search));
				obj.zButton.zClickInDlg(localize(locator.bcc));
			}
		}

		obj.zButton.zClickInDlg(localize(locator.ok));

		obj.zTextAreaField.zType(localize(locator.toLabel),
				"add@testdomain.com");

		obj.zButton.zClick(to);

		Thread.sleep(3000);
		infoDlgExist = obj.zDialog.zExistsDontWait(localize(locator.infoMsg));
		if (infoDlgExist.equals("true")) {
			obj.zButton.zClickInDlg(localize(locator.ok), "2");
			Thread.sleep(1000);
		}

		obj.zListItem.zClickItemInSpecificList(localize(locator.bcc), "2");

		obj.zButton.zClickInDlg(localize(locator.remove));
		obj.zListItem.zVerifyItemInSpecificListInDlgNotExist(
				localize(locator.bcc), "", "2");
		Thread.sleep(2000);
		infoDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.selectAddresses));
		if (infoDlgExist.equals("true")) {
			obj.zButton.zClickInDlg(localize(locator.ok));
			Thread.sleep(1000);
		}
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));

		needReset = false;

	}

	/**
	 * Test to check the new mail right click "Add to Contacts"
	 * functionality.This test performed within the same account login in order
	 * avoid login to a another account.So this test sends the mail to self
	 * account and adds it to contacts
	 * 
	 * @param subject
	 *            - subject of the mail
	 * @param mailBody
	 *            -body of the mail
	 * @throws Exception
	 */
	@Test(dataProvider = "ABMiscDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void contextAddToContacts(String subject, String mailBody)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String contactName = null;
		String[] fromAccount = SelNGBase.selfAccountName.split("@");

		String[] firstAndLastName = fromAccount[0].split("_");

		String contactFirstName = firstAndLastName[0];
		String contactLastName = firstAndLastName[firstAndLastName.length - 1];
		contactName = contactLastName + ", " + contactFirstName;

		fromAccount[0] = fromAccount[0].replace("_", " ");
		fromAccount[0] = fromAccount[0].toLowerCase();

		obj.zButton.zClick(page.zABCompose.zMailTabIconBtn);
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(SelNGBase.selfAccountName,
				"", "", subject, mailBody, "");

		MailApp
				.ClickCheckMailUntilMailShowsUp("", subject);

		obj.zMessageItem.zClick(subject);

		SelNGBase.actOnLabel = true;
		obj.zMessageItem.zRtClick(fromAccount[0]);
		SelNGBase.actOnLabel = false;// reset the flag

		obj.zMenuItem.zClick(localize(locator.AB_ADD_CONTACT));

		obj.zButton.zClick(localize(locator.save), "2");

		obj.zToastAlertMessage.zAlertMsgExists(
				localize(locator.contactCreated),
				"Contact should be created in Address Book");

		page.zABCompose.zNavigateToContact();

		obj.zFolder.zClick(localize(locator.contacts));
		if (config.getString("locale").equals("ar")) {
			obj.zContactListItem.zExists(contactName.substring(0, 5));
		} else if (config.getString("locale").equals("zh_HK")
				|| config.getString("locale").equals("ja")
				|| config.getString("locale").equals("ko")) {
			obj.zContactListItem.zExists(contactLastName);
		} else {
			obj.zContactListItem.zExists(contactName);
		}

		needReset = false;

	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

}