package com.zimbra.qa.selenium.projects.ajax.tests.tasks.folders;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder.FolderColor;


public class ChangeColorTaskFolder extends AjaxCommonTest {
	@SuppressWarnings("serial")
	public ChangeColorTaskFolder() {
		logger.info("New " + ChangeColorTaskFolder.class.getCanonicalName());

		// test starts at the task tab
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
	}	

	
	
	@Test(description = "Edit a folder, change the color (Context menu -> Edit)", groups = { "functional" })
	public void ChangeColorTaskFolder_01() throws HarnessException {
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		ZAssert.assertNotNull(taskFolder, "Verify the task is available");
		
		// Create the subTaskList
		String name = "taskList" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ name +"' l='"+ taskFolder.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem subTaskList = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(subTaskList, "Verify the subfolder is available");
		
		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Edit the folder using context menu		
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeTasks.zTreeItem(
				Action.A_RIGHTCLICK, Button.B_TREE_EDIT, subTaskList);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");
		
		// Change the color, click OK
		dialog.zSetNewColor(FolderColor.Blue);
		dialog.zClickButton(Button.B_OK);

		// Check the color
		app.zGetActiveAccount().soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder id='" + subTaskList.getId() + "'/>"
			+	"</GetFolderRequest>");

		String color = app.zGetActiveAccount().soapSelectValue("//mail:folder[@name='" + subTaskList.getName() + "']", "color");
		ZAssert.assertEquals(color, "1", "Verify the color of the folder is set to blue (1)");
	}
	
}


