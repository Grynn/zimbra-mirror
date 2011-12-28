package com.zimbra.qa.selenium.projects.ajax.tests.calendar.folders.external;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.DialogAddExternalCalendar;

public class CreateExternalCalendarOther extends AjaxCommonTest {


	public CreateExternalCalendarOther() {
		logger.info("New " + CreateExternalCalendarOther.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageCalendar;
		super.startingAccountPreferences = null;
	}


	@Test(	description = "Create a new external calendar (type=other) by clicking 'Gear' -> 'new folder' on folder tree", 
			groups = { "functional" })
	public void CreateExternalCalendarOther_01() throws HarnessException {

		
		// Set the new calendar name
		String calendarname = "calendar" + ZimbraSeleniumProperties.getUniqueString();

		
		// Click on the "New Calendar" button in the calendar tree
		DialogAddExternalCalendar dialog = (DialogAddExternalCalendar) app.zTreeCalendar.zPressPulldown(Button.B_TREE_FOLDERS_OPTIONS, Button.B_TREE_NEW_EXTERNAL_CALENDAR);

		// Fill out the dialog
		dialog.zSetSourceType(DialogAddExternalCalendar.SourceType.Other);
		dialog.zClickButton(Button.B_NEXT);

		
		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), calendarname);
		ZAssert.assertNotNull(folder, "Verify the new folder is found");
		ZAssert.assertEquals(folder.getName(), calendarname, "Verify the server and client folder names match");
		
	}

//	@Test(	description = "Create a new external calendar (type=other) using keyboard shortcuts", 
//			groups = { "functional" })
//	public void CreateExternalCalendarOther_02() throws HarnessException {
//
//		
//		// Set the new calendar name
//		String calendarname = "calendar" + ZimbraSeleniumProperties.getUniqueString();
//
//
//		// Click on the "New Calendar" button in the calendar tree
//		DialogCreateFolder dialog = (DialogCreateFolder) app.zPageCalendar.zKeyboardShortcut(Shortcut.S_NEWCALENDAR);
//
//		// Fill out the dialog
//		dialog.zEnterFolderName(calendarname);
//		dialog.zClickButton(Button.B_OK);
//
//		
//		// Make sure the folder was created on the ZCS server
//		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), calendarname);
//		ZAssert.assertNotNull(folder, "Verify the new folder is found");
//		ZAssert.assertEquals(folder.getName(), calendarname, "Verify the server and client folder names match");
//
//	}
//
//	@Test(	description = "Create a new external calendar (type=other)  using context menu from root folder", 
//			groups = { "functional" })
//	public void CreateCalendar_03() throws HarnessException {
//
//		// Set the new calendar name
//		String calendarname = "calendar" + ZimbraSeleniumProperties.getUniqueString();
//
//		// Determine the calendar folder
//		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
//		
//		
//		// Click on the "New Calendar" button in the calendar tree
//		DialogCreateFolder dialog = (DialogCreateFolder) app.zTreeCalendar.zTreeItem(Action.A_RIGHTCLICK, Button.O_NEW_CALENDAR, root);
//
//		// Fill out the dialog
//		dialog.zEnterFolderName(calendarname);
//		dialog.zClickButton(Button.B_OK);
//
//		
//		// Make sure the folder was created on the ZCS server
//		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), calendarname);
//		ZAssert.assertNotNull(folder, "Verify the new folder is found");
//		ZAssert.assertEquals(folder.getName(), calendarname, "Verify the server and client folder names match");
//		
//	}
//
//	@Test(	description = "Create a new external calendar (type=other)  using  mail app New -> New Folder", 
//			groups = { "functional" })
//	public void CreateCalendar_04() throws HarnessException {
//
//		// Set the new calendar name
//		String calendarname = "calendar" + ZimbraSeleniumProperties.getUniqueString();
//
//		// Create a new folder in the inbox
//		// using the context menu + New Folder
//		DialogCreateFolder dialog = (DialogCreateFolder) app.zPageCalendar.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CALENDAR);
//		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");
//
//		// Fill out the form with the basic details
//		dialog.zEnterFolderName(calendarname);
//		dialog.zClickButton(Button.B_OK);
//
//		
//		// Make sure the folder was created on the ZCS server
//		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), calendarname);
//		ZAssert.assertNotNull(folder, "Verify the new folder is found");
//		ZAssert.assertEquals(folder.getName(), calendarname, "Verify the server and client folder names match");
//
//	}


}
