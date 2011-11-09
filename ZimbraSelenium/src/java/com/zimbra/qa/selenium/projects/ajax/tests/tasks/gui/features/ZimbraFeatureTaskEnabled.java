package com.zimbra.qa.selenium.projects.ajax.tests.tasks.gui.features;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class ZimbraFeatureTaskEnabled extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraFeatureTaskEnabled() {
		logger.info("New " + ZimbraFeatureTaskEnabled.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String, String>() {
			{

				// Only task is enabled
				put("zimbraFeatureTasksEnabled", "TRUE");
				put("zimbraFeatureMailEnabled", "FALSE");
				put("zimbraFeatureContactsEnabled", "FALSE");
				put("zimbraFeatureCalendarEnabled", "FALSE");
				put("zimbraFeatureBriefcasesEnabled", "FALSE");

			    // https://bugzilla.zimbra.com/show_bug.cgi?id=62161#c3
			    // put("zimbraFeatureOptionsEnabled", "FALSE");
				
				put("zimbraPrefTasksReadingPaneLocation", "bottom");
				

			}
		};

	}
	
	@Test(	description = "Load the Task tab with just Tasks enabled",
			groups = { "functional" })
	public void ZimbraFeatureTaskEnabled_01() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create a basic task
		String subject = "task" + ZimbraSeleniumProperties.getUniqueString();
				
		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp name='"+ subject +"'>" +
			        			"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(task, "Verify the task is created");
		
		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Get all the tasks
		List<TaskItem> tasks = app.zPageTasks.zGetTasks();
		ZAssert.assertNotNull(tasks, "Verify the task list exists");

		TaskItem found = null;
		for (TaskItem t : tasks) {
			logger.info("Subject: looking for "+ subject +" found: "+ t.gSubject);
			if ( subject.equals(t.gSubject) ) {
				found = t;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the task is present");
		
	}
}
