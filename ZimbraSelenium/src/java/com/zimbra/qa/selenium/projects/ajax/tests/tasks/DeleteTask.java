package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class DeleteTask extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public DeleteTask() {
		logger.info("New "+ DeleteTask.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
		
	}
	
	@Test(	description = "Delete a task using toolbar delete button",
			groups = { "smoke" })
	public void DeleteTask_01() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
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

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Refresh the tasks view
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click delete
		app.zPageTasks.zToolbarPressButton(Button.B_DELETE);
		
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
		ZAssert.assertNull(found, "Verify the task is no longer present");
	
	}

	@Test(	description = "Delete a task using checkbox and toolbar delete button",
			groups = { "smoke" })
	public void DeleteTask_02() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
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
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);
		
		// Click delete
		app.zPageTasks.zToolbarPressButton(Button.B_DELETE);
		
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
		ZAssert.assertNull(found, "Verify the task is no longer present");
	
	}

	@DataProvider(name = "DataProviderDeleteKeys")
	public Object[][] DataProviderDeleteKeys() {
	  return new Object[][] {
	    new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
	    new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
	  };
	}
	
	@Test(	description = "Delete a task by selecting and typing 'delete' keyboard",
			groups = { "smoke" },
			dataProvider = "DataProviderDeleteKeys")
	public void DeleteTask_03(String name, int keyEvent) throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
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
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);

		
		// Click delete keyboard
		logger.info("Typing shortcut key "+ name + " KeyEvent: "+ keyEvent);
		app.zPageMail.zKeyboardKeyEvent(keyEvent);
		
		
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
		ZAssert.assertNull(found, "Verify the task is no longer present");
	
	}
	@Bugs(ids="56467")
	@Test(	description = "Delete a task by selecting and typing '.t' shortcut",
			groups = { "functional" } )
	public void DeleteTask_04() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
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
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);

		
		// Use Delete Keyboard Shortcut
		app.zPageTasks.zKeyboardShortcut(Shortcut.S_MAIL_MOVETOTRASH);
		
		
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
		ZAssert.assertNull(found, "Verify the task is no longer present");
	
	}

	@Test(	description = "Delete multiple tasks (3) by select and toolbar delete",
			groups = { "functional" })
	public void DeleteTask_05() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create the message data to be sent
		String subject1 = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String subject2 = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String subject3 = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp name='"+ subject1 +"'>" +
			        			"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject1 +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp name='"+ subject2 +"'>" +
			        			"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject2 +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp name='"+ subject3 +"'>" +
			        			"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject3 +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Import each message into MailItem objects
		TaskItem task1 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject1);
		ZAssert.assertNotNull(task1, "Verify the task is created");

		TaskItem task2 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject2);
		ZAssert.assertNotNull(task2, "Verify the task is created");

		TaskItem task3 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject3);
		ZAssert.assertNotNull(task3, "Verify the task is created");

		// Refresh the tasks view
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the items
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject1);
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject2);
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject3);
				
		// Click toolbar delete button
		app.zPageTasks.zToolbarPressButton(Button.B_DELETE);
				
		List<TaskItem> tasks = app.zPageTasks.zGetTasks();
		ZAssert.assertNotNull(tasks, "Verify the message list exists");

		TaskItem found1 = null;
		TaskItem found2 = null;
		TaskItem found3 = null;
		for (TaskItem t : tasks) {
			logger.info("Subject: looking at: "+ t.gSubject);
			if ( subject1.equals(t.gSubject) ) {
				found1 = t;
			}
			if ( subject2.equals(t.gSubject) ) {
				found2 = t;
			}
			if ( subject3.equals(t.gSubject) ) {
				found3 = t;
			}
		}
		ZAssert.assertNull(found1, "Verify the task "+ subject1 +" is no longer in the mailbox");
		ZAssert.assertNull(found2, "Verify the task "+ subject2 +" is no longer in the mailbox");
		ZAssert.assertNull(found3, "Verify the task "+ subject3 +" is no longer in the mailbox");	

	}


	@Test(	description = "Delete a task using context menu delete button",
			groups = { "smoke" })
	public void DeleteTask_06() throws HarnessException {

		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
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
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);

		
		// Right click the item, select delete
		app.zPageTasks.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, subject);
		
		
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
		ZAssert.assertNull(found, "Verify the task is no longer present");
	
	}
	
	@Bugs(ids="61625")
	@Test(	description = "Hard-delete a task by selecting and typing 'shift-del' shortcut",
			groups = { "functional" })
	public void HardDeleteTask_07() throws HarnessException {

		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
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
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);
		
		// Click shift-delete
		app.zPageTasks.zKeyboardShortcut(Shortcut.S_TASK_HARDELETE);
		
		//Verify the task is no longer present in tasks folder
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
		ZAssert.assertNull(found, "Verify the task is no longer present");
	
		
		// Verify the task is not in the  trash
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='task'>"
				+		"<query>is:anywhere "+ subject +"</query>"
				+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:task");
		ZAssert.assertEquals(nodes.length, 0, "Verify the task is not in the  trash");

	}

	@Bugs(ids="61625")
	@Test(	description = "Hard-delete multiple tasks(3) by selecting and typing 'shift-del' shortcut",
			groups = { "functional" })
	public void HardDeleteTask_08() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);		
		// Create a basic task to delete
		String subject1 = "task1"+ ZimbraSeleniumProperties.getUniqueString();
		String subject2 = "task2"+ ZimbraSeleniumProperties.getUniqueString();
		String subject3 = "task3"+ ZimbraSeleniumProperties.getUniqueString();
				
		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp name='"+ subject1 +"'>" +
			        			"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject1 +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");
		
		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp name='"+ subject2 +"'>" +
			        			"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject2 +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");
		
		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp name='"+ subject3 +"'>" +
			        			"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject3 +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		

		TaskItem task1 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject1);
		TaskItem task2 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject2);
		TaskItem task3 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject3);
		
		ZAssert.assertNotNull(task1, "Verify the task1 is created");
		ZAssert.assertNotNull(task2, "Verify the task2 is created");
		ZAssert.assertNotNull(task3, "Verify the task3 is created");
		
		// Refresh the tasks view
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject1);
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject2);
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject3);
		
		// Click shift-delete
		app.zPageTasks.zKeyboardShortcut(Shortcut.S_TASK_HARDELETE);
		
		//Verify the task is no longer present in tasks/trash folder
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='task'>"
			+		"<query>is:anywhere "+ subject1 +"</query>"
			+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:task");
		ZAssert.assertEquals(nodes.length, 0, "Verify the task1 is not in the  tasks/trash");
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='task'>"
			+		"<query>is:anywhere "+ subject2 +"</query>"
			+	"</SearchRequest>");

		nodes = app.zGetActiveAccount().soapSelectNodes("//mail:task");
		ZAssert.assertEquals(nodes.length, 0, "Verify the task2 is not in the  tasks/trash");
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='task'>"
			+		"<query>is:anywhere "+ subject3 +"</query>"
			+	"</SearchRequest>");

		nodes = app.zGetActiveAccount().soapSelectNodes("//mail:task");
		ZAssert.assertEquals(nodes.length, 0, "Verify the task3 is not in the  tasks/trash");

	}
	@Test(	description = "Create task through SOAP - delete using Backspace Key & verify through GUI",
			groups = { "functional" } )
	public void DeleteTask_09() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
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
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);

		
		// Use Backspace Keyboard Shortcut
		app.zPageTasks.zKeyboardShortcut(Shortcut.S_BACKSPACE);
		
		
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
		ZAssert.assertNull(found, "Verify the task is no longer present");
	
	}


}
