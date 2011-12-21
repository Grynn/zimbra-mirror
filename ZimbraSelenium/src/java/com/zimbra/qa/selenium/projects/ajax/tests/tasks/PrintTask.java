package com.zimbra.qa.selenium.projects.ajax.tests.tasks;



import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class PrintTask extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public PrintTask() {
		logger.info("New "+ PrintTask.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};

	}



	@Test(	
			description = "Print Task using RightClick -> Print and Verify Contents in Print view",
			groups = { "functional" }
			)
	public void PrintTask_01() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create a basic task 
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String bodyText = "text" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>"
			+		"<m >"
			+			"<inv>"
			+				"<comp name='"+ subject +"'>" 
			+					"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" 
			+				"</comp>" 
			+			"</inv>" 
			+			"<su>"+ subject +"</su>" 
			+			"<mp ct='text/plain'>" 
			+				"<content>"+ bodyText +"</content>" 
			+			"</mp>"
			+		"</m>" 
			+	"</CreateTaskRequest>");



		// Refresh the tasks view
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);


		SeparateWindowPrintPreview window = null;
		
		try {
				
			// Right click the item, select Show Original
			window = (SeparateWindowPrintPreview)app.zPageTasks.zListItem(Action.A_RIGHTCLICK, Button.O_PRINT_MENU, subject);
			window.zWaitForActive();		// Make sure the window is there			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");

			//Verify content in Print view.
			String Printcontent = window.sGetBodyText();
			ZAssert.assertStringContains(Printcontent, subject, "Verify subject in Print view");
			ZAssert.assertStringContains(Printcontent, bodyText, "Verify content in Print view");
		
		} finally {
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
		}
	}

	@Test(	
			description = "Print Task using shortcut 'p' and verify its content from GUI",
			groups = { "functional" }
			)
	public void PrintTask_02() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create a basic task 
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		String bodyText = "text" + ZimbraSeleniumProperties.getUniqueString();

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
				"<content>"+ bodyText +"</content>" +
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

		
		SeparateWindowPrintPreview window = null;
		
		try {
			
			//Press keyboard shortcut p
			window = (SeparateWindowPrintPreview)app.zPageTasks.zKeyboardShortcut(Shortcut.S_PRINTTASK);
			window.zWaitForActive();		// Make sure the window is there			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");

	
			//Verify content in Print view.
			String Printcontent = window.sGetBodyText();
			ZAssert.assertStringContains(Printcontent, subject, "Verify subject in Print view");
			ZAssert.assertStringContains(Printcontent, bodyText, "Verify content in Print view");

		} finally {
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
		}
	}
	
	
	@Test(	
			description = "Print multiple tasks using Print-> Print TaskFolder and  and verify its content from GUI",
			groups = { "functional" }
			)
	public void PrintTask_03() throws HarnessException {

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
		
		
		SeparateWindowPrintPreview window = null;

		try {
			
			//Pull down Print button and select Print Task folder.
			window = (SeparateWindowPrintPreview)app.zPageTasks.zToolbarPressPulldown(Button.B_PRINT, Button.O_PRINT_TASKFOLDER);
			window.zWaitForActive();		// Make sure the window is there			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");

			//Verify subjects in Print view.
			String Printcontent = window.sGetBodyText();
			ZAssert.assertStringContains(Printcontent, subject1, "Verify subject in Print view");
			ZAssert.assertStringContains(Printcontent, subject2, "Verify subject2 in Print view");
			ZAssert.assertStringContains(Printcontent, subject3, "Verify subject2 in Print view");
		
		} finally {
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
		}

	}
}

