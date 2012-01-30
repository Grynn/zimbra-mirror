package com.zimbra.qa.selenium.projects.ajax.tests.tasks.folders;



import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class DragAndDropTaskFolder extends AjaxCommonTest {
	@SuppressWarnings("serial")
	public DragAndDropTaskFolder() {
		logger.info("New " + DragAndDropTaskFolder.class.getCanonicalName());

		// test starts at the task tab
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
	}	

	
	@Bugs(ids="69661")
	@Test(description = "Drag one Task folder and Drop into other", groups = { "smoke" })
	public void DragAndDropTaskFolder_01() throws HarnessException {
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		ZAssert.assertNotNull(taskFolder, "Verify the task is available");
		
		// Create the subTaskList
		String name1 = "taskList1" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "taskList2" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name1 +"' l='"+ taskFolder.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subTaskList1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subTaskList1, "Verify the first subfolder is available");
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name2 +"' l='"+ taskFolder.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subTaskList2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);
		ZAssert.assertNotNull(subTaskList2, "Verify the second subfolder is available");	
		
		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
		
		app.zPageMail.zDragAndDrop(
				"//td[contains(@id, 'zti__main_Tasks__" + subTaskList1.getId() + "_textCell') and contains(text(), '"+ name1 + "')]",
				"//td[contains(@id, 'zti__main_Tasks__" + subTaskList2.getId() + "_textCell') and contains(text(),'"+ name2 + "')]");

		// Verify the folder is now in the other subfolder
		subTaskList1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);
		ZAssert.assertNotNull(subTaskList1, "Verify the subfolder is again available");
		ZAssert.assertEquals(subTaskList2.getId(), subTaskList1.getParentId(), "Verify the subfolder's parent is now the other subfolder");

	}

}
