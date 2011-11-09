package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;


import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.DisplayTask;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Field;

public class EditTask extends AjaxCommonTest{

	@SuppressWarnings("serial")
	public EditTask() {
		logger.info("Edit " + EditTask.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
	}

	@Test(	description = "Create task through SOAP - edit subject and verify through GUI",groups = { "smoke" })
	public void EditTask_01() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);


		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String Newsubject = "Edittask"+ ZimbraSeleniumProperties.getUniqueString();

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

		// Select the item
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Click edit

		FormTaskNew taskedit = (FormTaskNew) app.zPageTasks.zToolbarPressButton(Button.B_EDIT);

		//Fill new subject in subject field
		taskedit.zFillField(Field.Subject, Newsubject);
		taskedit.zSubmit();

		// Get the list of tasks in the view
		List<TaskItem> tasks = app.zPageTasks.zGetTasks();
		ZAssert.assertNotNull(tasks, "Verify the list of edited tasks exists");

		// Iterate over the task list, looking for the new task
		TaskItem found = null;
		for (TaskItem t : tasks ) {
			logger.info("Task: looking for "+ Newsubject +" found: "+ t.gSubject);
			if ( Newsubject.equals(t.gSubject) ) {
				// Found it!
				found = t;
			}
		}
		ZAssert.assertNotNull(found, "Verify the Edited task present in the task list");

		// Iterate over the task list, looking for the old task
		TaskItem foundoldtask = null;
		for (TaskItem t : tasks ) {
			logger.info("Task: looking for "+ subject +" foundeditedtask: "+ t.gSubject);
			if ( subject.equals(t.gSubject) ) {
				// Found it!
				foundoldtask = t;
				break;
			}
		}

		ZAssert.assertNull(foundoldtask, "Verify the old task no longer  present in the task list");

	}
	
	/**
	 * 	1. Go to Tasks
	 * 	2. Create a new task with no due date
	 * 	3. Refresh list view to see new task 
	 * 	4. Edit task and add due date
	 * 	5. Refresh list view again
	 * 	   Expected result:Task should show due date
	 * @throws HarnessException
	 */
	@Bugs(ids="64647")
	@Test(	description = "Create task through SOAP - edit duedate >> Refresh task >>verify Due Date in list view through GUI",groups = { "functional" })
	public void EditTask_02() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		FolderItem trashFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		ZDate dueDate      = new ZDate(2015, 11, 17, 12, 0, 0);
		
		//Create task
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

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(task, "Verify the task is created");

		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
		// Select the item
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);
		// Click edit
		FormTaskNew taskedit = (FormTaskNew) app.zPageTasks.zToolbarPressButton(Button.B_EDIT);

		//Fill due date field
		taskedit.zFillField(Field.DueDate, dueDate.toMM_DD_YYYY());
		taskedit.zSubmit();

		DisplayTask actual = (DisplayTask) app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);
		
		//Verify Due Date before refresh
		ZAssert.assertEquals(actual.zGetTaskListViewProperty(com.zimbra.qa.selenium.projects.ajax.ui.tasks.DisplayTask.Field.DueDate), dueDate.toMM_DD_YYYY(), "Verify the due date matches");
		
		// click on Trash folder
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, trashFolder);		
		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
		
		//Verify the due date matches after refresh
		ZAssert.assertEquals(actual.zGetTaskListViewProperty(com.zimbra.qa.selenium.projects.ajax.ui.tasks.DisplayTask.Field.DueDate), dueDate.toMM_DD_YYYY(), "Verify the due date matches after refresh");
			
	}
	@Test(	description = "Create task through SOAP - Edit task using Right Click Context Menu & verify through GUI",groups = { "functional" })
	public void EditTask_03() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);


		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String Newsubject = "Edittask"+ ZimbraSeleniumProperties.getUniqueString();

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

		// Select the item
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Right click subject and select edit context menu
		FormTaskNew taskedit = (FormTaskNew) app.zPageTasks.zListItem(Action.A_RIGHTCLICK, Button.O_EDIT, subject);

		//Fill new subject in subject field
		taskedit.zFillField(Field.Subject, Newsubject);
		taskedit.zSubmit();

		// Get the list of tasks in the view
		List<TaskItem> tasks = app.zPageTasks.zGetTasks();
		ZAssert.assertNotNull(tasks, "Verify the list of edited tasks exists");

		// Iterate over the task list, looking for the new task
		TaskItem found = null;
		for (TaskItem t : tasks ) {
			logger.info("Task: looking for "+ Newsubject +" found: "+ t.gSubject);
			if ( Newsubject.equals(t.gSubject) ) {
				// Found it!
				found = t;
			}
		}
		ZAssert.assertNotNull(found, "Verify the Edited task present in the task list");

		// Iterate over the task list, looking for the old task
		TaskItem foundoldtask = null;
		for (TaskItem t : tasks ) {
			logger.info("Task: looking for "+ subject +" foundeditedtask: "+ t.gSubject);
			if ( subject.equals(t.gSubject) ) {
				// Found it!
				foundoldtask = t;
				break;
			}
		}

		ZAssert.assertNull(foundoldtask, "Verify the old task no longer  present in the task list");

	}

}
