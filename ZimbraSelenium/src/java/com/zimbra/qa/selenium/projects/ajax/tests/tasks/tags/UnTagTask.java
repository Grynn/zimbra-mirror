package com.zimbra.qa.selenium.projects.ajax.tests.tasks.tags;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;

public class UnTagTask extends AjaxCommonTest {

	public UnTagTask() {
		logger.info("New " + UnTagTask.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Remove a tag from a Document using Toolbar -> Tag -> Remove Tag", groups = { "smoke" })
	public void UnTagTask_01() throws HarnessException {
		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>"
				+		"<m >"
				+			"<inv>"
				+				"<comp name='" + subject + "'>"
				+					"<or a='"+ app.zGetActiveAccount().EmailAddress + "'/>"
				+				"</comp>" 
				+			"</inv>"
				+			"<su>" + subject + "</su>"
				+			"<mp ct='text/plain'>"
				+				"<content>content" + ZimbraSeleniumProperties.getUniqueString() + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateTaskRequest>");

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(),subject);
		ZAssert.assertNotNull(task, "Verify the task is created");

		// Refresh the tasks view
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Select the item
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Create a tag using GUI
		String tagName = "tag" + ZimbraSeleniumProperties.getUniqueString();

		// Click on New Tag and check for active
		DialogTag dialogtag = (DialogTag)app.zPageTasks.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
		ZAssert.assertNotNull(dialogtag, "Verify that the Create New Tag dialog is active");
		ZAssert.assertTrue(dialogtag.zIsActive(), "Verify that the Create New Tag dialog is active");

		//Fill Name  and Press OK button
		dialogtag.zSetTagName(tagName);
		dialogtag.zClickButton(Button.B_OK);

		// Make sure the tag was created on the server (get the tag ID)
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tagName +"']", "id");

		// Verify tagged task name
		app.zGetActiveAccount()
		.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='task'>"
				+ "<query>tag:"
				+ tagName
				+ "</query>"
				+ "</SearchRequest>");

		String name = app.zGetActiveAccount().soapSelectValue(
				"//mail:SearchResponse//mail:task", "name");

		ZAssert.assertEquals(name, subject,	"Verify tagged task name");

		// Make sure the tag was applied to the task
		app.zGetActiveAccount()
		.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='task'>"
				+ "<query>" + subject + "</query>" + "</SearchRequest>");

		String id = app.zGetActiveAccount().soapSelectValue(
				"//mail:SearchResponse//mail:task", "t");

		ZAssert.assertEquals(id, tagID,"Verify the tag was attached to the task");

		// refresh briefcase page
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Click on tagged document
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);

		// Click Remove Tag
		app.zPageTasks.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);

		app.zGetActiveAccount()
		.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='task'>"
				+ "<query>" + subject + "</query>" + "</SearchRequest>");

		id = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:task", "t");

		ZAssert.assertStringDoesNotContain(id,tagID, "Verify that the tag is removed from the message");		
	}
}
