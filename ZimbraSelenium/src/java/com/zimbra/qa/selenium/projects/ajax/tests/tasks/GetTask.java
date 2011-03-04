package com.zimbra.qa.selenium.projects.ajax.tests.tasks;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.DisplayTask;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.DisplayTask.Field;


public class GetTask extends AjaxCommonTest {

	/**
	 * Example SOAP:
	 * <CreateTaskRequest xmlns="urn:zimbraMail">
	 * 	<m l="15">
	 * 		<inv>
	 * 			<comp priority="1" status="INPR" percentComplete="50" allDay="1" name="subject" loc="location">
	 * 				<s d="20110214"/>
	 * 				<e d="20110216"/>
	 * 				<or a="enus12974607211563@testdomain.com"/>
	 * 				<alarm action="DISPLAY">
	 * 					<trigger>
	 * 						<abs d="20110215T223000Z"/>
	 * 					</trigger>
	 * 				</alarm>
	 * 			</comp>
	 * 		</inv>
	 * 		<su>subject</su>
	 * 		<mp ct="text/plain">
	 * 			<content>content</content>
	 * 		</mp>
	 * 	</m>
	 * </CreateTaskRequest>
	 * 
	 */
	
	public GetTask() {
		logger.info("New "+ GetTask.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	
	@Test(	description = "View a simple task",
			groups = { "smoke" })
	public void GetTask_01() throws HarnessException {
		
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

	@Test(	description = "Get a task with text content - verify task contents",
			groups = { "smoke" })
	public void GetTask_02() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String content = "content"+ ZimbraSeleniumProperties.getUniqueString();
				
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
			        		"<content>"+ content +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(task, "Verify the task is created");
		
		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Select the message so that it shows in the reading pane
		DisplayTask actual = (DisplayTask) app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Subject), subject, "Verify the subject matches");
		
		// The body could contain HTML, even though it is only displaying text (e.g. <br> may be present)
		// do a contains, rather than equals.
		ZAssert.assertStringContains(	actual.zGetTaskProperty(Field.Body), content, "Verify the body matches");

		
	}

	@Test(	description = "Get a task with html content - verify task contents",
			groups = { "smoke" })
	public void GetTask_03() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String bodyText = "text" + ZimbraSeleniumProperties.getUniqueString();
		String bodyHTML = "text <strong>bold"+ ZimbraSeleniumProperties.getUniqueString() +"</strong> text";
		String contentHTML = XmlStringUtil.escapeXml(
			"<html>" +
				"<head></head>" +
				"<body>"+ bodyHTML +"</body>" +
			"</html>");
		
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
							"<content>" + bodyText +"</content>" +
						"</mp>" +
						"<mp ct='text/html'>" +
							"<content>"+ contentHTML +"</content>" +
						"</mp>" +
					"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(task, "Verify the task is created");
		
		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Select the message so that it shows in the reading pane
		DisplayTask actual = (DisplayTask) app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Subject), subject, "Verify the subject matches");
		

		// Verify the To, From, Subject, Body		
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Body), contentHTML, "Verify the content matches");
		
	}

	@Test(	description = "Get a task with all fields - verify task contents",
			groups = { "smoke" })
	public void GetTask_04() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String location = "location"+ ZimbraSeleniumProperties.getUniqueString();
		ZDate startDate    = new ZDate(2015, 1, 15, 12, 0, 0);
		ZDate dueDate      = new ZDate(2015, 1, 17, 12, 0, 0);
		ZDate reminderDate = new ZDate(2015, 1, 16, 12, 0, 0);
		String content = "content"+ ZimbraSeleniumProperties.getUniqueString();
				
		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
			        	"<inv>" +
			        		"<comp priority='1' status='INPR' percentComplete='50' allDay='1' name='"+ subject +"' location='"+ location +"'>" +
		        				"<s d='"+ startDate.toYYYYMMDD() +"'/>" +
		        				"<s d='"+ dueDate.toYYYYMMDD() +"'/>" +
								"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
								"<alarm action='DISPLAY'>" +
									"<trigger>" +
										"<abs d='"+ reminderDate.toYYYYMMDDHHMMSSZ() +"'/>" +
									"</trigger>" +
								"</alarm>" +
			        		"</comp>" +
			        	"</inv>" +
			        	"<su>"+ subject +"</su>" +
			        	"<mp ct='text/plain'>" +
			        		"<content>"+ content +"</content>" +
			        	"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(task, "Verify the task is created");
		
		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Select the message so that it shows in the reading pane
		DisplayTask actual = (DisplayTask) app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Subject), subject, "Verify the subject matches");
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Location), location, "Verify the location matches");
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.StartDate), startDate.toMMM_dC_yyyy(), "Verify the start date matches");
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.DueDate), dueDate.toMMM_dC_yyyy(), "Verify the due date matches");
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Priority), "High", "Verify the priority matches");
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Status), "In Progress", "Verify the status matches");
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Percentage), "50", "Verify the percentage matches");
		ZAssert.assertEquals(	actual.zGetTaskProperty(Field.Reminder), dueDate.toMMM_dd_yyyy_A_hCmm_a(), "Verify the percentage matches");
		
		// The body could contain HTML, even though it is only displaying text (e.g. <br> may be present)
		// do a contains, rather than equals.
		ZAssert.assertStringContains(	actual.zGetTaskProperty(Field.Body), content, "Verify the body matches");

		
	}


	@Test(	description = "Click on task folder to receive any new tasks",
			groups = { "functional" })
	public void GetTask_05() throws HarnessException {
		
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		
		// Create a basic task to delete
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String content = "content"+ ZimbraSeleniumProperties.getUniqueString();
				
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
			        		"<content>"+ content +"</content>" +
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
