package projects.zcs.tests.preferences.importexport;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import projects.zcs.ui.MailApp;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.items.FolderItem;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

/**
 * 
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class AccountImportExportTest extends CommonTest {
	protected static String aChar = new Character((char) 92).toString();

	static String tgzFileName;
	static String newTag = "newTag";

	static String inboxMsg = "inboxTestMessage";
	static String newFolderMsg = "newMailFolderTestMessage";
	static String taggedMsg = "taggedTestMessage",
			newMailFolder = "newMailFolder";
	static String newSearchFolder = "newSearchFolder";

	static String lastName = "lastName";
	static String firstName = "firstName";
	static String lastNameTagged = "lastNameTagged";
	static String firstNameTagged = "firstNameTagged";
	static String lastNameNewFolder = "lastNameNewFolder";
	static String firstNameNewFolder = "firstNameNewFolder";
	static String newABFolder = "newABFolder";

	static String subjectAppt = "subjectAppt";
	static String subjectTaggedAppt = "subjectTaggedAppt";
	static String subjectNewCalFolder = "subjectNewCalFolder";
	static String newCalFolder = "newCalFolder";

	static String subjectTask = "subjectTask";
	static String subjectTaggedTask = "subjectTaggedTask";
	static String subjectNewTaskFolder = "subjectNewTaskFolder";
	static String newTaskFolder = "newTaskFolder";

	static String subjectPage = "subjectPage";
	static String subjectTaggedPage = "subjectTaggedPage";
	static String subjectNewNotebookFolder = "subjectNewNotebookFolder";
	static String newNotebookFolder = "newNotebookFolder";

	static String fileName = "samlejpg.jpg";
	static String fileTagged = "testexcelfile.xls";
	static String fileNewBriefcaseFolder = "testwordfile.doc";
	static String newBriefcaseFolder = "newBriefcaseFolder";

	//--------------------------------------------------------------------------
	// Section 1 : BeforeClass
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		resetSession();
		String acc1 = ProvZCS.getRandomAccount();
		SelNGBase.selfAccountName = acc1;
		page.zLoginpage.zLoginToZimbraAjax(acc1);
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
	// Section 2 : Test
	//--------------------------------------------------------------------------
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void exportImportEntireMailboxAndVerify() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (ZimbraSeleniumProperties.getStringProperty("browser").substring(0, 2).equals("FF")) {
			clearDirectory();
			createTestData();
			exportAccount();

			resetSession();
			String acc2 = ProvZCS.getRandomAccount();
			SelNGBase.selfAccountName = acc2;
			page.zLoginpage.zLoginToZimbraAjax(acc2);
			importAccount();
			verifyTestData();
		}
		needReset = false;
	}

	public static void clearDirectory() throws Exception {
		String myNewDir = "C:\\temp";
		new File(myNewDir).mkdirs();

		File dir = new File("c:/temp");
		String[] children = dir.list();
		for (int i = 0; i < children.length; i++) {
			String filename = children[i];
			System.out.println(filename);
			File f1 = new File(dir + "/" + filename);
			f1.delete();
		}
	}

	private static void createTestData() throws Exception {
		// Common data across all application
		zGoToApplication("Mail");
		// String newTag = "newTag";
		page.zMailApp.zCreateTag(newTag);

		// ------------------------- Mail -------------------------
		String[] recipients = { SelNGBase.selfAccountName };
		ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
				"ccuser@testdomain.com", inboxMsg, inboxMsg);
		MailApp.ClickCheckMailUntilMailShowsUp(inboxMsg);

		// create folder and keep one mail
		page.zMailApp.zCreateFolder(newMailFolder);
		ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
				"ccuser@testdomain.com", newFolderMsg, newFolderMsg);
		MailApp.ClickCheckMailUntilMailShowsUp(newFolderMsg);
		obj.zMessageItem.zClick(newFolderMsg);
		obj.zButton.zClick(page.zMailApp.zMoveIconBtn);
		Thread.sleep(1000);
		obj.zFolder.zClickInDlgByName(newMailFolder,
				localize(locator.moveMessage));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessage));
		Thread.sleep(1000);

		// apply tag to mail
		ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
				"ccuser@testdomain.com", taggedMsg, taggedMsg);
		MailApp.ClickCheckMailUntilMailShowsUp(taggedMsg);
		obj.zMessageItem.zClick(taggedMsg);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zClick(newTag);

		// create search folder
		selenium.type("xpath=//input[@class='search_input']", newFolderMsg);
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zButton.zClick("id=zb__Search__SAVE_left_icon");
		obj.zEditField.zTypeInDlgByName("id=*nameField", newSearchFolder,
				localize(locator.saveSearch));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.saveSearch));

		// ------------------------- Address Book -------------------------
		zGoToApplication("Address Book");
		ContactItem contact1 = new ContactItem();
		contact1.firstName=firstName;
		contact1.lastName = lastName;
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact1);

		// apply tag to contact
		ContactItem contact2 = new ContactItem();
		contact2.firstName = lastNameTagged;
		contact2.firstName = firstNameTagged;
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact2);
		obj.zFolder.zClick(page.zABCompose.zContactsFolder);
		Thread.sleep(2000);
		obj.zContactListItem.zClick(lastNameTagged);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(newTag);

		// keep one contact in new AB folder
		FolderItem folder = new FolderItem();
		folder.name = newABFolder;
		ContactItem contact = new ContactItem();
		contact.firstName = firstNameNewFolder;
		contact.middleName = "";
		contact.lastName = lastNameNewFolder;
		contact.AddressBook = folder;
		
		page.zABCompose.zCreateNewAddBook(folder.name);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		// ------------------------- Calendar -------------------------
		zGoToApplication("Calendar");
		page.zCalCompose.zCreateSimpleAppt(subjectAppt, "", "", "");

		// apply tag to the appointment
		page.zCalCompose.zCreateSimpleAppt(subjectTaggedAppt, "", "", "");
		obj.zAppointment.zClick(subjectTaggedAppt.substring(0, 7));
		obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		obj.zMenuItem.zClick(newTag);

		// keep one appointment in new calendar folder
		page.zCalApp.zCreateNewCalendarFolder(newCalFolder);
		page.zCalCompose.zCreateSimpleApptInCalendar(subjectNewCalFolder, "",
				"", "", newCalFolder);

		// ------------------------- Tasks -------------------------
		zGoToApplication("Tasks");
		page.zTaskApp.zTaskCreateSimple(subjectTask, "", "", "");

		// apply tag to the task item
		page.zTaskApp.zTaskCreateSimple(subjectTaggedTask, "", "", "");
		obj.zTaskItem.zClick(subjectTaggedTask);
		obj.zButton.zClick(page.zTaskApp.zTasksTagBtn);
		obj.zMenuItem.zClick(newTag);

		// keep one appointment in new task folder
		page.zTaskApp.zTaskListCreateNewBtn(newTaskFolder);
		Thread.sleep(2000);
		page.zTaskApp.zTaskCreateSimpleInTaskList(subjectNewTaskFolder, "", "",
				"", newTaskFolder);

		// ------------------------- Documents -------------------------
		zGoToApplication("Documents");
		page.zDocumentCompose.zCreateBasicPage(subjectPage, subjectPage);

		// apply tag to the page
		page.zDocumentCompose.zCreateNewNotebook(newNotebookFolder, "", "");
		obj.zFolder.zClick(newNotebookFolder);
		Thread.sleep(1000);
		// page.zDocumentCompose.zCreateBasicPage(subjectTaggedPage,
		// subjectTaggedPage);
		// obj.zButton.zClick(page.zDocumentApp.zEditPageIconBtn);
		// obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		// obj.zMenuItem.zClick(newTag);

		// keep one page in new notebook folder
		page.zDocumentCompose.zCreatePageInSpecificNotebook(newNotebookFolder,
				subjectNewNotebookFolder, subjectNewNotebookFolder);

		// ------------------------- Briefcase -------------------------
		zGoToApplication("Briefcase");
		page.zBriefcaseApp.zBriefcaseFileUpload(fileName, "");

		// apply tag to the file
		page.zBriefcaseApp.zBriefcaseFileUpload(fileTagged, "");
		Thread.sleep(1000);
		obj.zBriefcaseItem.zClick(fileTagged);
		obj.zButton.zClick(page.zBriefcaseApp.zTagItemIconBtn);
		obj.zMenuItem.zClick(newTag);

		// keep one file in new briefcase folder
		Thread.sleep(2000);
		page.zBriefcaseApp.zCreateNewBriefcaseFolder(newBriefcaseFolder);
		page.zBriefcaseApp.zBriefcaseFileUpload(fileNewBriefcaseFolder,
				newBriefcaseFolder);
	}

	private static void exportAccount() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Import/Export");
		obj.zButton.zMouseOver(localize(locator._export));
		obj.zButton.zClick(localize(locator._export));
		Thread.sleep(4000);
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_ALT);
		zRobot.keyPress(KeyEvent.VK_S);
		zRobot.keyRelease(KeyEvent.VK_ALT);
		zRobot.keyRelease(KeyEvent.VK_S);
		zRobot.keyPress(KeyEvent.VK_ENTER);
		zRobot.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(5000);

		boolean foundFlag = false;
		String currentDateTime, oneMinLessCurrentDateTime, twoMinLessCurrentDateTime, threeMinLessCurrentDateTime, currentMin, subFilename;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmm");
		java.util.Date date = new java.util.Date();
		currentDateTime = "All-" + dateFormat.format(date);
		currentMin = String.valueOf(dateFormat.format(date).substring(13, 15));
		oneMinLessCurrentDateTime = "All-"
				+ dateFormat.format(date).substring(0, 13)
				+ (Integer.parseInt(currentMin.trim()) - 1);
		twoMinLessCurrentDateTime = "All-"
				+ dateFormat.format(date).substring(0, 13)
				+ (Integer.parseInt(currentMin.trim()) - 2);
		threeMinLessCurrentDateTime = "All-"
				+ dateFormat.format(date).substring(0, 13)
				+ (Integer.parseInt(currentMin.trim()) - 3);

		File dir = new File("c:/temp");
		String[] children = dir.list();
		for (int i = 0; i < children.length; i++) {
			String filename = children[i];
			if (filename.length() >= 4
					&& filename.substring(0, 4).equals("All-")) {
				subFilename = filename.substring(0, 19);
				if (subFilename.equals(currentDateTime)
						|| subFilename.equals(oneMinLessCurrentDateTime)
						|| subFilename.equals(twoMinLessCurrentDateTime)
						|| subFilename.equals(threeMinLessCurrentDateTime)) {
					foundFlag = true;
					tgzFileName = "c:" + aChar + "temp" + aChar + filename;
					System.out.println(tgzFileName);
					break;
				}
			}
		}

		Assert.assertEquals(foundFlag, true,
				"Account exported tgz file not found");
	}

	private static void importAccount() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Import/Export");
		obj.zBrowseField.zTypeWithKeyboard(localize(locator.fileLabel),
				tgzFileName);
		obj.zButton.zClick(localize(locator._import));

		zWaitTillObjectExist("dialog", localize(locator.infoMsg));
		String expectedMsg = localize(locator.importSuccess);
		String actualMsg = obj.zDialog.zGetMessage(localize(locator.infoMsg));

		Assert.assertTrue(actualMsg.equals(expectedMsg),
				"Actual message of import account " + actualMsg
						+ " is not same as expected message " + expectedMsg);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.infoMsg));
	}

	private static void verifyTestData() throws Exception {
		zReloginToAjax();

		// ------------------------- Mail -------------------------
		zGoToApplication("Mail");
		obj.zMessageItem.zExists(inboxMsg);
		// verify tagged message
		obj.zMessageItem.zVerifyIsTagged(taggedMsg);
		// verify mail in new folder
		obj.zFolder.zClick(newMailFolder);
		obj.zMessageItem.zExists(newFolderMsg);

		// verify search folder
		obj.zFolder.zClick(newSearchFolder);
		obj.zMessageItem.zExists(taggedMsg);

		// ------------------------- Address Book -------------------------
		zGoToApplication("Address Book");
		obj.zContactListItem.zExists(lastName);
		// verify tagged contact
		obj.zContactListItem.zVerifyIsTagged(lastNameTagged);
		// verify contact in new AB folder
		obj.zFolder.zClick(newABFolder);
		obj.zContactListItem.zExists(lastNameNewFolder);

		// ------------------------- Calendar -------------------------
		zGoToApplication("Calendar");
		obj.zAppointment.zExists(subjectAppt.substring(0, 8));
		obj.zCalendarFolder.zExists(newCalFolder);
		obj.zAppointment.zExists(subjectNewCalFolder.substring(0, 8));
		// verify tagged appointment
		// obj.zAppointment.zDblClick(subjectTaggedAppt.substring(0, 8));
		// obj.zButton.zClick(page.zCalApp.zCalTagBtn);
		// obj.zMenuItem.zNotExists(newTag);

		// ------------------------- Tasks -------------------------
		zGoToApplication("Tasks");
		obj.zTaskItem.zExists(subjectTask);
		// verify tagged task item
		obj.zTaskItem.zVerifyIsTagged(subjectTaggedTask);
		// verify task item in new folder
		obj.zFolder.zClick(newTaskFolder);
		obj.zTaskItem.zExists(subjectNewTaskFolder);

		// ------------------------- Documents -------------------------
		zGoToApplication("Documents");
		obj.zDocumentPage.zExists(subjectPage);
		// verify tagged page
		// obj.zDocumentPage.zClick(subjectTaggedPage);
		// obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		// obj.zMenuItem.zNotExists(newTag);
		// verify page in new notebook folder
		obj.zFolder.zClick(newNotebookFolder);
		obj.zDocumentPage.zExists(subjectNewNotebookFolder);

		// ------------------------- Briefcase -------------------------
		zGoToApplication("Briefcase");
		obj.zBriefcaseItem.zExists(fileName);
		// verify tagged file
		obj.zBriefcaseItem.zVerifyIsTagged(fileTagged);
		// verify file in new briefcase folder
		obj.zFolder.zClick(newBriefcaseFolder);
		obj.zBriefcaseItem.zExists(fileNewBriefcaseFolder);
	}

	//--------------------------------------------------------------------------
	// Section 3: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs re login..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}
}