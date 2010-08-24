package projects.html.ui;

import java.io.File;

import org.testng.Assert;

import framework.core.SelNGBase;
import framework.util.SleepUtil;

import projects.html.tests.CommonTest;

/**
 *This class has UI level static id's and methods for html contact related
 * tests Prashant Jaiswal
 */
/**
 * @author KK Sure
 * 
 */
@SuppressWarnings( { "static-access", "unused" })
public class CalFolderApp extends CommonTest {

	// IDs in Calendar Folder's Page
	public static final String calFoldersToolbarCloseBtn = "id=OPCLOSE";
	public static final String calFoldersNewCalendarBtn = "id=SOPNEWCAL";
	public static final String calFoldersSubscribeBtn = "id=SOPNEWSUB";
	public static final String calFoldersLinkToSharedBtn = "id=SOPNEWLINK";
	public static final String calFoldersSendFreeBusyLinkBtn = "id=SOPFREEBUSYLINK";

	// IDs in Calendar Folder properties page
	public static final String calNameField = "id=name";
	public static final String calColorDropdown = "id=folderColor";
	public static final String calExcludeFreeBusyCheckbox = "id=exclude";
	public static final String calCheckedInUICheckbox = "id=checked";
	public static final String calSaveChangesBtn = "id=OPSAVE";
	public static final String calImportEditField = "id=import";
	public static final String calImportBtn = "name=actionImport";
	public static final String calDeleteAllApptsCheckbox = "id=emptyConfirm";
	public static final String calDeleteAllApptsBtn = "name=actionEmptyFolderConfirm";
	public static final String calDeleteCalendarCheckbox = "id=deleteConfirm";
	public static final String calDeleteCalendarBtn = "name=actionDelete";
	public static final String calImportFromICSEditField = "id=import";

	// IDs in New Calendar Folder Creation page
	public static final String calNewNameField = "id=newName";
	public static final String calNewColorDropdown = "id=color";
	public static final String calNewCreateCalendarBtn = "id=OPSAVE";
	public static final String calNewCancelBtn = "name=actionCancel";

	// IDs in Subscribe to calendar page
	public static final String calSubscribeURLField = "id=url";

	// IDs in Link to shared calendar page
	public static final String calLinkOwnersEmailField = "id=ownersEmail";
	public static final String calLinkOwnersCalNameField = "id=ownersCalName";

	/**
	 * Navigates to calendars folder page
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToCalendarFoldersPage() throws Exception {
		page.zCalendarApp.zNavigateToCalendar();
		SleepUtil.sleep(2000);
		obj.zFolder.zEdit(localize(locator.calendars));
		SleepUtil.sleep(3000);
	}

	/**
	 *Opens the calendar edit page
	 * 
	 * @param calName
	 * @throws Exception
	 */
	public static void zNavigateToCalendarProperties(String calName)
			throws Exception {
		page.zCalFolderApp.zNavigateToCalendarFoldersPage();
		obj.zCalendarFolder.zClick(calName);
		SleepUtil.sleep(4000);
	}

	/**
	 * Creates a new calendar
	 * 
	 * @param calName
	 * @param color
	 * @throws Exception
	 */
	public static void zCreateNewCalendar(String calName, String color)
			throws Exception {
		zNavigateToCalendarFoldersPage();
		obj.zButton.zClick(calFoldersNewCalendarBtn);
		SleepUtil.sleep(3000);
		obj.zEditField.zType(calNewNameField, calName);
		obj.zHtmlMenu.zClick(calNewColorDropdown, color);
		obj.zButton.zClick(calNewCreateCalendarBtn);
		SleepUtil.sleep(3000);
	}

	/**
	 * Subscribes to a calendar
	 * 
	 * @param calName
	 * @param url
	 * @throws Exception
	 */
	public static void zSubscribeToCalendar(String calName, String url)
			throws Exception {

		zNavigateToCalendarFoldersPage();

		obj.zButton.zClick(calFoldersSubscribeBtn);

		obj.zEditField.zType(calNewNameField, calName);

		obj.zEditField.zType(calSubscribeURLField, url);

		obj.zButton.zClick(calNewCreateCalendarBtn);

	}

	/**
	 * Links to a shared calendar
	 * 
	 * @param calName
	 * @param ownersEmail
	 * @param ownersCalName
	 * @throws Exception
	 */
	public static void zLinkSharedCalendar(String calName, String ownersEmail,
			String ownersCalName) throws Exception {

		zNavigateToCalendarFoldersPage();

		obj.zButton.zClick(calFoldersLinkToSharedBtn);

		obj.zEditField.zType(calNewNameField, calName);

		obj.zEditField.zType(calLinkOwnersEmailField, ownersEmail);

		obj.zEditField.zType(calLinkOwnersCalNameField, ownersCalName);

		obj.zButton.zClick(calNewCreateCalendarBtn);

	}

	/**
	 * Deletes all appointments of a given calendar
	 * 
	 * @param calName
	 * @throws Exception
	 */
	public static void zDeleteAllApptsOfCalendar(String calName)
			throws Exception {

		zNavigateToCalendarProperties(calName);

		obj.zCheckbox.zClick(calDeleteAllApptsCheckbox);

		obj.zButton.zClick(calDeleteAllApptsBtn);

	}

	/**
	 * Deletes the given calendar
	 * 
	 * @param calName
	 * @throws Exception
	 */
	public static void zDeleteCalendar(String calName) throws Exception {

		zNavigateToCalendarProperties(calName);

		obj.zCheckbox.zClick(calDeleteCalendarCheckbox);

		obj.zButton.zClick(calDeleteCalendarBtn);

		SleepUtil.sleep(3000);
	}

	/**
	 * Imports appointments from the specified ICS file to the given calendar
	 * 
	 * @param calName
	 * @param fileName
	 * @throws Exception
	 */
	public static void zImportFromICSToCalendar(String calName, String fileName)
			throws Exception {

		File f = new File("src/java/projects/html/data/" + fileName);
		String path = f.getAbsolutePath();

		zNavigateToCalendarProperties(calName);

		obj.zBrowseField.zTypeWithKeyboard(calImportFromICSEditField, path);

		obj.zButton.zClick(calImportBtn);

		SleepUtil.sleep(5000);

	}

	/**
	 * Check or un-check calendar in UI
	 * 
	 * @param calName
	 * @throws Exception
	 */
	public static void zCheckUncheckCalendarInUI(String calName)
			throws Exception {

		zNavigateToCalendarProperties(calName);

		obj.zCheckbox.zClick(calCheckedInUICheckbox);

		obj.zButton.zClick(calSaveChangesBtn);
	}

	public static void zRenameCalendar(String calName, String newCalName)
			throws Exception {
		zNavigateToCalendarProperties(calName);
		// below sleep required
		SleepUtil.sleep(3000);
		obj.zFolder.zClick(calName);
		SleepUtil.sleep(3000);
		obj.zEditField.zType(calNameField, newCalName);
		obj.zButton.zClick(calSaveChangesBtn);
		SleepUtil.sleep(3000);
	}

}
