package com.zimbra.qa.selenium.projects.ajax.tests.calendar.folders;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class GetCalendar extends AjaxCommonTest {

	public GetCalendar() {
		logger.info("New "+ GetCalendar.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageCalendar;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Get a calendar (under USER_ROOT)",
			groups = { "smoke" })
	public void GetCalendar_01() throws HarnessException {
		
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(root, "Verify the inbox is available");
		
		// Create the subfolder
		String name = "calendar" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name +"' l='"+ root.getId() +"' view='appointment'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(subfolder, "Verify the subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		
		// Verify the folder appears in the list
		List<FolderItem> folders = app.zTreeCalendar.zListGetFolders();
		
		FolderItem found = null;
		for (FolderItem f : folders) {
			if ( name.equals(f.getName()) ) {
				found = f;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the calendar was in the tree");

		
	}	

	@Test(	description = "Get a calendar (under subcalendar)",
			groups = { "functional" })
	public void GetCalendar_02() throws HarnessException {
		
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(root, "Verify the inbox is available");
		
		// Create a subfolder
		String name1 = "calendar" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name1 +"' l='"+ root.getId() +"' view='appointment'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is available");
		
		// Create a subfolder of the subfolder
		String name2 = "calendar" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name2 +"' l='"+ subfolder1.getId() +"' view='appointment'/>" +
                "</CreateFolderRequest>");

		FolderItem subfolder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(subfolder2, "Verify the subfolder is available");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

		
		// Verify the folder appears in the list
		List<FolderItem> folders = app.zTreeCalendar.zListGetFolders();
		
		FolderItem found1 = null;
		FolderItem found2 = null;
		for (FolderItem f : folders) {
			if ( name1.equals(f.getName()) ) {
				found1 = f;
			}
			if ( name2.equals(f.getName()) ) {
				found2 = f;
			}
		}
		ZAssert.assertNotNull(found1, "Verify the calendar was in the tree");
		ZAssert.assertNotNull(found2, "Verify the sub-calendar was in the tree");

		
	}	


}
