package com.zimbra.qa.selenium.projects.ajax.tests.calendar.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class DragAndDropCalendar extends AjaxCommonTest{

	public DragAndDropCalendar(){
		logger.info("New "+ DragAndDropCalendar.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageCalendar;
		super.startingAccountPreferences = null;

	}

	@Test(	description = "Drag one calendar and Drop into other",
			groups = { "smoke" })
	public void DragDropCalendar_01() throws HarnessException {

		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		ZAssert.assertNotNull(root, "Verify the inbox is available");


		// Create two subfolders in the inbox
		// One folder to Drag
		// Another folder to drop into
		String name1 = "calendar" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "calendar" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ name1 +"' l='"+ root.getId() +"' view='appointment'/>"
				+	"</CreateFolderRequest>");


		FolderItem subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the first subfolder is available");

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ name2 +"' l='"+ root.getId() +"' view='appointment'/>"
				+	"</CreateFolderRequest>");

		FolderItem subfolder2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(subfolder2, "Verify the second subfolder is available");


		// Click on Get Mail to refresh the folder list
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

//		app.zPageMail.zDragAndDrop(
//				"//td[contains(@id, 'zti__main_Calendar__" + subfolder1.getId() + "_textCell') and contains(text(), '"+ name1 + "')]",
//				"//td[contains(@id, 'zti__main_Calendar__" + subfolder2.getId() + "_textCell') and contains(text(),'"+ name2 + "')]");

		app.zPageCalendar.zDragAndDrop(
				String.format("css=td[id='zti__main_Calendar__%s_textCell']", subfolder1.getId()),
				String.format("css=td[id='zti__main_Calendar__%s_textCell']", subfolder2.getId()) );
		
		// Verify the folder is now in the other subfolder
		subfolder1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subfolder1, "Verify the subfolder is again available");
		ZAssert.assertEquals(subfolder2.getId(), subfolder1.getParentId(), "Verify the subfolder's parent is now the other subfolder");


	}

}
