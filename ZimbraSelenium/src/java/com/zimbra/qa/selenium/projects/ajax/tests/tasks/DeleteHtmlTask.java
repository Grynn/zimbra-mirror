package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class DeleteHtmlTask extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public DeleteHtmlTask() {
		logger.info("New "+ DeleteHtmlTask.class.getCanonicalName());
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
		
	}
	
	@Test(	description = "Delete a Html task using toolbar delete button",
			groups = { "smoke" })
	public void DeleteHtmlTask_01() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String taskHtmlbody = "task<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String contentHTML = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody+"</div>"+"</body>"+"</html>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m >" +
				"<inv>" +
				"<comp name='"+ subject +"'>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML+"</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
				"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertStringContains(task.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody.trim(), "Verify the html content of task body");
		
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
		ZAssert.assertNull(found, "Verify the html task is no longer present");
	
	}

	@Test(	description = "Delete a Html task using checkbox and toolbar delete button",
			groups = { "smoke" })
	public void DeleteHtmlTask_02() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
		String taskHtmlbody = "task<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String contentHTML = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody+"</div>"+"</body>"+"</html>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m >" +
				"<inv>" +
				"<comp name='"+ subject +"'>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML+"</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
				"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertStringContains(task.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody.trim(), "Verify the html content of task body");
				
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
		ZAssert.assertNull(found, "Verify the html task is no longer present");
	
	}

	@DataProvider(name = "DataProviderDeleteKeys")
	public Object[][] DataProviderDeleteKeys() {
	  return new Object[][] {
	    new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
	    new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
	  };
	}
	
	@Test(	description = "Delete a html task by selecting and typing 'delete' keyboard",
			groups = { "smoke" },
			dataProvider = "DataProviderDeleteKeys")
	public void DeleteHtmlTask_03(String name, int keyEvent) throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
		String taskHtmlbody = "task<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String contentHTML = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody+"</div>"+"</body>"+"</html>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m >" +
				"<inv>" +
				"<comp name='"+ subject +"'>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML+"</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
				"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertStringContains(task.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody.trim(), "Verify the html content of task body");

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
	@Test(	description = "Delete a Html task by selecting and typing '.t' shortcut",
			groups = { "functional" } )
	public void DeleteHtmlTask_04() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
		String taskHtmlbody = "task<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String contentHTML = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody+"</div>"+"</body>"+"</html>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m >" +
				"<inv>" +
				"<comp name='"+ subject +"'>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML+"</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
				"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertStringContains(task.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody.trim(), "Verify the html content of task body");

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

	@Test(	description = "Delete multiple Html tasks (3) by select and toolbar delete",
			groups = { "functional" })
	public void DeleteHtmlTask_05() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create the message data to be sent
		String subject1 = "task1"+ ZimbraSeleniumProperties.getUniqueString();
		String subject2 = "task2"+ ZimbraSeleniumProperties.getUniqueString();
		String subject3 = "task3"+ ZimbraSeleniumProperties.getUniqueString();
				
		String taskHtmlbody1 = "task1<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String taskHtmlbody2 = "task2<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String taskHtmlbody3 = "task3<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String contentHTML1 = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody1+"</div>"+"</body>"+"</html>");
		String contentHTML2 = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody2+"</div>"+"</body>"+"</html>");
		String contentHTML3 = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody3+"</div>"+"</body>"+"</html>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m >" +
				"<inv>" +
				"<comp name='"+ subject1 +"'>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject1 +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML1+"</content>" +
				"</mp>" +
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
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML2+"</content>" +
				"</mp>" +
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
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML3+"</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
				"</CreateTaskRequest>");
		

		// Import each message into MailItem objects
		TaskItem task1 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject1);
		ZAssert.assertStringContains(task1.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody1.trim(), "Verify the html content of task body");

		TaskItem task2 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject2);
		ZAssert.assertStringContains(task2.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody2.trim(), "Verify the html content of task body");

		TaskItem task3 = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject3);
		ZAssert.assertStringContains(task3.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody3.trim(), "Verify the html content of task body");

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
		ZAssert.assertNull(found1, "Verify the Html task "+ subject1 +" is no longer in the mailbox");
		ZAssert.assertNull(found2, "Verify the Html task "+ subject2 +" is no longer in the mailbox");
		ZAssert.assertNull(found3, "Verify the Html task "+ subject3 +" is no longer in the mailbox");	

	}


	@Test(	description = "Delete a html task using context menu delete button",
			groups = { "smoke" })
	public void DeleteHtmlTask_06() throws HarnessException {

		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
		String taskHtmlbody = "task<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String contentHTML = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody+"</div>"+"</body>"+"</html>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m >" +
				"<inv>" +
				"<comp name='"+ subject +"'>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML+"</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
				"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertStringContains(task.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody.trim(), "Verify the html content of task body");

		// Refresh the tasks view
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
						
		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);

		
		// Right click the item, select delete
		app.zPageTasks.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, subject);
		
		
		List<TaskItem> tasks = app.zPageTasks.zGetTasks();
		ZAssert.assertNotNull(tasks, "Verify the  task list exists");

		TaskItem found = null;
		for (TaskItem t : tasks) {
			logger.info("Subject: looking for "+ subject +" found: "+ t.gSubject);
			if ( subject.equals(t.gSubject) ) {
				found = t;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the html task is no longer present");
	
	}
	
	
	@Test(	description = "Create Html task through SOAP - delete using Backspace Key & verify through GUI",
			groups = { "functional" } )
	public void DeleteHtmlTask_07() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
				
		String taskHtmlbody = "task<b>bold"+ ZimbraSeleniumProperties.getUniqueString()+"</b>task";
		String contentHTML = XmlStringUtil.escapeXml("<html>"+"<body>"+"<div>"+taskHtmlbody+"</div>"+"</body>"+"</html>");

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
				"<m >" +
				"<inv>" +
				"<comp name='"+ subject +"'>" +
				"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
				"</comp>" +
				"</inv>" +
				"<su>"+ subject +"</su>" +
				"<mp ct='multipart/alternative'>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
				"</mp>" +
				"<mp ct='text/html'>" +
				"<content>"+contentHTML+"</content>" +
				"</mp>" +
				"</mp>" +
				"</m>" +
				"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertStringContains(task.getHtmlTaskBody().trim().toLowerCase(), taskHtmlbody.trim(), "Verify the html content of task body");

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
		ZAssert.assertNull(found, "Verify the html task is no longer present");
	
	}


}
