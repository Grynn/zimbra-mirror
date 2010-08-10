package projects.html.tests.mail;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

import projects.html.tests.CommonTest;
import projects.zcs.clients.ProvZCS;

/**
 * Class file contains all mail top & bottom toolbar related 10*2 tests
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class MailTopBtmToolBarTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@SuppressWarnings("unused")
	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("doActionWithoutSelectingMail")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTdoActionWithoutSelectingMail", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTdoActionWithoutSelectingMail", "", "" } };
		} else if (test.equals("deleteMultipleMailsAndVerify")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTdeleteMultipleMailsAndVerify", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTdeleteMultipleMailsAndVerify", "", "" } };
		} else if (test.equals("moveSingleMailAndVerify")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTmoveSingleMailAndVerify", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTmoveSingleMailAndVerify", "", "" } };
		} else if (test.equals("moveMultipleMailsAndVerify")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTmoveMultipleMailsAndVerify", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTmoveMultipleMailsAndVerify", "", "" } };
		} else if (test.equals("markMailJunkNotJunkAndVerify")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTmarkMailJunkNotJunkAndVerify", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTmarkMailJunkNotJunkAndVerify", "", "" } };
		} else if (test.equals("verifyEmptyJunkMail")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTverifyEmptyJunkMail", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTverifyEmptyJunkMail", "", "" } };
		} else if (test.equals("verifyMarkAsReadUnreadToMail")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTverifyMarkAsReadUnreadToMail", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTverifyMarkAsReadUnreadToMail", "", "" } };
		} else if (test.equals("verifyAddRemoveFlagToMail")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTverifyAddRemoveFlagToMail", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTverifyAddRemoveFlagToMail", "", "" } };
		} else if (test.equals("verifyPermanentDeleteMail")) {
			return new Object[][] {
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"TTverifyPermanentDeleteMail", "", "" },
					{ ProvZCS.getRandomAccount(), "_selfAccountName_",
							"ccuser@testdomain.com", "bccuser@testdomain.com",
							"BTverifyPermanentDeleteMail", "", "" } };
		} else if (test.equals("verifyMultipleSelectedMailActions")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com",
					"verifyMultipleSelectedMailActions", "", "" } };
		} else {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "topbottomtoolbartestmail", "",
					"" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
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

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/**
	 * (-ve case) do mail actions without selecting message and verify
	 * corresponding toast messages
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void doActionWithoutSelectingMail(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentToastMessage = null;
		if (subject.equals("TTdoActionWithoutSelectingMail")) {
			System.out
					.println("--- Test (doActionWithoutSelectingMail) started for top toolbar ---");
		} else if (subject.equals("BTdoActionWithoutSelectingMail")) {
			System.out
					.println("--- Test (doActionWithoutSelectingMail) started for bottom toolbar ---");
		}
		// "Delete" without selecting message
		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		Thread.sleep(MEDIUM_WAIT); // selenium failure here in IE
		if (subject.equals("TTdoActionWithoutSelectingMail")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn);
		} else if (subject.equals("BTdoActionWithoutSelectingMail")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn, "2");
		}
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			currentToastMessage = obj.zToastAlertMessage.zGetMsg();
			assertReport(currentToastMessage, "No conversation selected",
					"Verifying message when try to click Delete button without selecting message");
		}

		// "Move To" without selecting message
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		Thread.sleep(MEDIUM_WAIT); // selenium failure here in IE
		zWaitTillObjectExist("htmlmenu", "name=actionOp");
		if (subject.equals("TTdoActionWithoutSelectingMail")) {
			page.zMailApp.zMoveTo(localize(locator.sent));
		} else if (subject.equals("BTdoActionWithoutSelectingMail")) {
			page.zMailApp.zMoveToBtmToolbar(localize(locator.sent));
		}
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			currentToastMessage = obj.zToastAlertMessage.zGetMsg();
			assertReport(currentToastMessage, "No conversation selected",
					"Verifying message when try to move message without selecting message");
		}

		// "More Actions" without selecting message
		if (!subject.equals("BTdoActionWithoutSelectingMail")) {
			obj.zFolder.zClick(page.zMailApp.zInboxFldr);
			Thread.sleep(MEDIUM_WAIT); // selenium failure here in IE
			zWaitTillObjectExist("htmlmenu", "name=actionOp");
			if (subject.equals("TTdoActionWithoutSelectingMail")) {
				page.zMailApp.zMoreActions(localize(locator.actionMarkRead));
			} else if (subject.equals("BTdoActionWithoutSelectingMail")) {
				page.zMailApp
						.zMoreActionsBtmToolbar(localize(locator.actionMarkRead));
			}
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
				currentToastMessage = obj.zToastAlertMessage.zGetMsg();
				assertReport(
						currentToastMessage,
						"No conversation selected",
						"Verifying message when try to do some mail related action without selecting message");
			}
		}

		needReset = false;
	}

	/**
	 * Delete multiple mails and verify
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteMultipleMailsAndVerify(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String newSubject = null;
		if (subject.equals("TTdeleteMultipleMailsAndVerify")) {
			System.out
					.println("--- Test (deleteMultipleMailsAndVerify) started for top toolbar ---");
			newSubject = "toptoolbarmailfordelete";
		} else if (subject.equals("BTdeleteMultipleMailsAndVerify")) {
			System.out
					.println("--- Test (deleteMultipleMailsAndVerify) started for bottom toolbar ---");
			newSubject = "bottomtlbrmailfordelete";
		}

		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject, body,
				attachments);
		obj.zCheckbox.zClick(subject);
		obj.zCheckbox.zClick(newSubject);
		if (subject.equals("TTdeleteMultipleMailsAndVerify")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn);
		} else if (subject.equals("BTdeleteMultipleMailsAndVerify")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn, "2");
		}
		obj.zMessageItem.zNotExists(subject);
		obj.zMessageItem.zNotExists(newSubject);
		obj.zFolder.zClick(page.zMailApp.zTrashFldr);
		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zExists(newSubject);

		needReset = false;
	}

	/**
	 * Move single mail and verify
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveSingleMailAndVerify(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (subject.equals("TTmoveSingleMailAndVerify")) {
			System.out
					.println("--- Test (moveSingleMailAndVerify) started for top toolbar ---");
		} else if (subject.equals("BTmoveSingleMailAndVerify")) {
			System.out
					.println("--- Test (moveSingleMailAndVerify) started for bottom toolbar ---");
		}

		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(MEDIUM_WAIT); // selenium failure in IE
		}
		obj.zCheckbox.zClick(subject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTmoveSingleMailAndVerify")) {
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("pl")) {
				obj.zHtmlMenu.zClick("name=folderId", localize(locator.sent)
						.substring(0, 5)
						+ ".*");
			} else if (ZimbraSeleniumProperties.getStringProperty("locale").equals("ar")) {
				obj.zHtmlMenu.zClick("name=folderId", localize(locator.sent)
						.substring(0, 2)
						+ ".*");
			} else {
				page.zMailApp.zMoveTo(localize(locator.sent));
			}
		} else if (subject.equals("BTmoveSingleMailAndVerify")) {
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("pl")) {
				obj.zHtmlMenu.zClickMenuByLocation("name=folderId", localize(
						locator.sent).substring(0, 5)
						+ ".*", "2", "2");
			} else if (ZimbraSeleniumProperties.getStringProperty("locale").equals("ar")) {
				obj.zHtmlMenu.zClickMenuByLocation("name=folderId", localize(
						locator.sent).substring(0, 2)
						+ ".*", "2", "2");
			} else {
				page.zMailApp.zMoveToBtmToolbar(localize(locator.sent));
			}
		}
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	/**
	 * Move multiple mails and verify
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveMultipleMailsAndVerify(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String newSubject = null;
		if (subject.equals("TTmoveMultipleMailsAndVerify")) {
			System.out
					.println("--- Test (moveMultipleMailsAndVerify) started for top toolbar ---");
			newSubject = "toptoolbarmailformove";
		} else if (subject.equals("BTmoveMultipleMailsAndVerify")) {
			System.out
					.println("--- Test (moveMultipleMailsAndVerify) started for bottom toolbar ---");
			newSubject = "bottomtblformove";
		}
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(MEDIUM_WAIT); // selenium failure in IE
		}
		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject, body,
				attachments);
		obj.zCheckbox.zClick(subject);
		obj.zCheckbox.zClick(newSubject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTmoveMultipleMailsAndVerify")) {
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("pl")) {
				obj.zHtmlMenu.zClick("name=folderId", localize(locator.sent)
						.substring(0, 5)
						+ ".*");
			} else if (ZimbraSeleniumProperties.getStringProperty("locale").equals("ar")) {
				obj.zHtmlMenu.zClick("name=folderId", localize(locator.sent)
						.substring(0, 2)
						+ ".*");
			} else {
				page.zMailApp.zMoveTo(localize(locator.sent));
			}
		} else if (subject.equals("BTmoveMultipleMailsAndVerify")) {
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("pl")) {
				obj.zHtmlMenu.zClickMenuByLocation("name=folderId", localize(
						locator.sent).substring(0, 5)
						+ ".*", "2", "2");
			} else if (ZimbraSeleniumProperties.getStringProperty("locale").equals("ar")) {
				obj.zHtmlMenu.zClickMenuByLocation("name=folderId", localize(
						locator.sent).substring(0, 2)
						+ ".*", "2", "2");
			} else {
				page.zMailApp.zMoveToBtmToolbar(localize(locator.sent));
			}
		}
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zNotExists(subject);
		obj.zMessageItem.zNotExists(newSubject);
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		Thread.sleep(SMALL_WAIT); // selenium failure in IE
		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zExists(newSubject);

		needReset = false;
	}

	/**
	 * Mark single mail to junk/not junk and verify
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void markMailJunkNotJunkAndVerify(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (subject.equals("TTmarkMailJunkNotJunkAndVerify")) {
			System.out
					.println("--- Test (markMailJunkNotJunkAndVerify) started for top toolbar ---");
		} else if (subject.equals("BTmarkMailJunkNotJunkAndVerify")) {
			System.out
					.println("--- Test (markMailJunkNotJunkAndVerify) started for bottom toolbar ---");
		}

		// verify junk - not junk mail functionality
		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(MEDIUM_WAIT); // selenium failure in IE
		}
		obj.zCheckbox.zClick(subject);
		if (subject.equals("TTmarkMailJunkNotJunkAndVerify")) {
			// previously it was junk instead of actionSpam
			page.zMailApp.zMoreActions(localize(locator.actionSpam));
		} else if (subject.equals("BTmarkMailJunkNotJunkAndVerify")) {
			page.zMailApp.zMoreActionsBtmToolbar(localize(locator.actionSpam));
		}
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zExists(subject);
		obj.zCheckbox.zClick(subject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTmarkMailJunkNotJunkAndVerify")) {
			// customized for pt_BR
			page.zMailApp.zMoreActions(localize(locator.actionNotSpam));
		} else if (subject.equals("BTmarkMailJunkNotJunkAndVerify")) {
			page.zMailApp
					.zMoreActionsBtmToolbar(localize(locator.actionNotSpam));
		}
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	/**
	 * Verify Empty Junk mail functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyEmptyJunkMail(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (subject.equals("TTverifyEmptyJunkMail")) {
			System.out
					.println("--- Test (verifyEmptyJunkMail) started for top toolbar ---");
		} else if (subject.equals("BTverifyEmptyJunkMail")) {
			System.out
					.println("--- Test (verifyEmptyJunkMail) started for bottom toolbar ---");
		}

		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(MEDIUM_WAIT); // selenium failure in IE
		}
		obj.zCheckbox.zClick(subject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTverifyEmptyJunkMail")) {
			page.zMailApp.zMoreActions(localize(locator.actionSpam));
		} else if (subject.equals("BTverifyEmptyJunkMail")) {
			page.zMailApp.zMoreActionsBtmToolbar(localize(locator.actionSpam));
		}
		Thread.sleep(SMALL_WAIT);
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zExists(subject);
		if (subject.equals("TTverifyEmptyJunkMail")) {
			obj.zButton.zClick(page.zMailApp.zEmptyJunkBtn);
		} else if (subject.equals("BTverifyEmptyJunkMail")) {
			obj.zButton.zClick(page.zMailApp.zEmptyJunkBtn, "2");
		}
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(page.zMailApp.zTrashFldr);
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zNotExists(subject);

		needReset = false;
	}

	/**
	 * Verify Mark as read/unread mail functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyMarkAsReadUnreadToMail(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (subject.equals("TTverifyMarkAsReadUnreadToMail")) {
			System.out
					.println("--- Test (verifyMarkAsReadUnreadToMail) started for top toolbar ---");
		} else if (subject.equals("BTverifyMarkAsReadUnreadToMail")) {
			System.out
					.println("--- Test (verifyMarkAsReadUnreadToMail) started for bottom toolbar ---");
		}

		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		obj.zCheckbox.zClick(subject);
		if (subject.equals("TTverifyMarkAsReadUnreadToMail")) {
			page.zMailApp.zMoreActions(localize(locator.actionMarkRead));
		} else if (subject.equals("BTverifyMarkAsReadUnreadToMail")) {
			page.zMailApp
					.zMoreActionsBtmToolbar(localize(locator.actionMarkRead));
		}
		obj.zMessageItem.zVerifyIsRead(subject);
		obj.zCheckbox.zClick(subject);
		if (subject.equals("TTverifyMarkAsReadUnreadToMail")) {
			page.zMailApp.zMoreActions(localize(locator.actionMarkUnread));
		} else if (subject.equals("BTverifyMarkAsReadUnreadToMail")) {
			page.zMailApp
					.zMoreActionsBtmToolbar(localize(locator.actionMarkUnread));
		}
		// zVerifyIsUnRead is not working
		// obj.zMessageItem.zVerifyIsUnRead(subject);

		needReset = false;
	}

	/**
	 * Verify Add/Remove flag functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAddRemoveFlagToMail(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (subject.equals("TTverifyAddRemoveFlagToMail")) {
			System.out
					.println("--- Test (verifyAddRemoveFlagToMail) started for top toolbar ---");
		} else if (subject.equals("BTverifyAddRemoveFlagToMail")) {
			System.out
					.println("--- Test (verifyAddRemoveFlagToMail) started for bottom toolbar ---");
		}

		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(MEDIUM_WAIT); // selenium failure in IE
		}
		obj.zCheckbox.zClick(subject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTverifyAddRemoveFlagToMail")) {
			page.zMailApp.zMoreActions(localize(locator.actionAddFlag));
		} else if (subject.equals("BTverifyAddRemoveFlagToMail")) {
			page.zMailApp
					.zMoreActionsBtmToolbar(localize(locator.actionAddFlag));
		}
		Thread.sleep(MEDIUM_WAIT);
		obj.zMessageItem.zVerifyIsFlagged(subject);
		obj.zCheckbox.zClick(subject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTverifyAddRemoveFlagToMail")) {
			page.zMailApp.zMoreActions(localize(locator.actionRemoveFlag));
		} else if (subject.equals("BTverifyAddRemoveFlagToMail")) {
			page.zMailApp
					.zMoreActionsBtmToolbar(localize(locator.actionRemoveFlag));
		}
		Thread.sleep(MEDIUM_WAIT);
		obj.zMessageItem.zVerifyIsNotFlagged(subject);

		needReset = false;
	}

	/**
	 * Verify permanent mail deletion functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyPermanentDeleteMail(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String newSubject = null;
		if (subject.equals("TTverifyPermanentDeleteMail")) {
			System.out
					.println("--- Test (verifyPermanentDeleteMail) started for top toolbar ---");
			newSubject = "toptlbrpermdeletemail";
		} else if (subject.equals("BTverifyPermanentDeleteMail")) {
			System.out
					.println("--- Test (verifyPermanentDeleteMail) started for bottom toolbar ---");
			newSubject = "bottomtoolbardeletemail";
		}

		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject, body,
				attachments);
		obj.zCheckbox.zClick(subject);
		Thread.sleep(SMALL_WAIT);
		obj.zCheckbox.zClick(newSubject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTverifyPermanentDeleteMail")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn);
		} else if (subject.equals("BTverifyPermanentDeleteMail")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn, "2");
		}
		Thread.sleep(SMALL_WAIT);
		obj.zFolder.zClick(page.zMailApp.zTrashFldr);
		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zExists(newSubject);
		obj.zCheckbox.zClick(subject);
		Thread.sleep(SMALL_WAIT);
		obj.zCheckbox.zClick(newSubject);
		Thread.sleep(SMALL_WAIT);
		if (subject.equals("TTverifyPermanentDeleteMail")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn);
		} else if (subject.equals("BTverifyPermanentDeleteMail")) {
			obj.zButton.zClick(page.zMailApp.zDeleteBtn, "2");
		}
		Thread.sleep(SMALL_WAIT);
		obj.zMessageItem.zNotExists(subject);
		obj.zMessageItem.zNotExists(newSubject);

		needReset = false;
	}

	/**
	 * Verify multiple mail selected checkbox functionality
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyMultipleSelectedMailActions(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// verify check box status by selecting multiple mail
		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		String newSubject = "verifyMultipleSelectedMailActions1";
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject, body,
				attachments);
		String newSubject1 = "verifyMultipleSelectedMailActions2";
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject1, body,
				attachments);
		obj.zCheckbox.zClick(page.zMailApp.zSelectAllMailChkBox);
		obj.zCheckbox.zVerifyIsChecked(subject);
		obj.zCheckbox.zVerifyIsChecked(newSubject);
		obj.zCheckbox.zVerifyIsChecked(newSubject1);
		obj.zCheckbox.zClick(page.zMailApp.zSelectAllMailChkBox);
		boolean subChkBoxStatus = obj.zCheckbox.zGetStatus(subject);
		boolean newSubChkBoxStatus = obj.zCheckbox.zGetStatus(newSubject);
		boolean newSub1ChkBoxStatus = obj.zCheckbox.zGetStatus(newSubject1);
		Assert.assertEquals(subChkBoxStatus, false);
		Assert.assertEquals(newSubChkBoxStatus, false);
		Assert.assertEquals(newSub1ChkBoxStatus, false);

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
