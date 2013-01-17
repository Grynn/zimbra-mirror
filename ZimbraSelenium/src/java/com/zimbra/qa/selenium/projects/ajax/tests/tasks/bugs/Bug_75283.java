package com.zimbra.qa.selenium.projects.ajax.tests.tasks.bugs;

import java.util.HashMap;
import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class Bug_75283 extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public Bug_75283() {
		logger.info("New " + Bug_75283.class.getCanonicalName());
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefShowSelectionCheckbox", "TRUE");
			put("zimbraPrefComposeFormat", "text");
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};
	}
	/**
	 * 1.Login to web client
	 * 2.Go to Tasks
	 * 3.Create one task with one attachment
	 * 4 [details].It shows in list view
	 * 5.select same task and do double click
	 * 6.Task gets open in same tab
	 * 7.Close it.
	 * 8.Again do double click and close.
	 * 9.Try to open same task in 3rd attempt .
	 * 10.Task gets open in same tab
	 * Expected:Even though user opens same attachment task multiple times, 
	 * it should show attachment only once


	 * @throws HarnessException
	 */

	@Test(description = "Same attachment keeps on adding while opening same task multiple times", groups = { "smoke" })
	public void Bug__75283() throws HarnessException {

		String subject = "task" + ZimbraSeleniumProperties.getUniqueString();


		ZimbraAccount account = app.zGetActiveAccount();
		FolderItem taskFolder = FolderItem.importFromSOAP(account,SystemFolder.Tasks);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/Files/Basic01/BasicExcel2007.xlsx";
		FileItem file = new FileItem(filePath);
		String fileName = file.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);		


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
				"<attach aid='"+attachmentId+"'>"+
				"</attach>"+
				"</m>" +
		"</CreateTaskRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		TaskItem task = TaskItem.importFromSOAP(app.zGetActiveAccount(), subject);
		ZAssert.assertNotNull(task, "Verify the task is created");
		
		//Verify attachment through soap
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);		
		account.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='task' >"
				+ "<query>" + subject + "</query>" + "</SearchRequest>");

		String invId = account.soapSelectValue("//mail:SearchResponse/mail:task", "invId");

		account.soapSend("<GetMsgRequest xmlns='urn:zimbraMail'>"
				+ "<m id='" + invId + "' />" + "</GetMsgRequest>");

		Element getMsgResponse = account.soapSelectNode("//mail:GetMsgResponse", 1);
		Element m = ZimbraAccount.SoapClient.selectNode(getMsgResponse,"//mail:mp[@s='9055']");
		ZAssert.assertEquals(m.getAttribute("filename", null), fileName, "Verify file name through SOAP");

		//1st attempt
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageTasks.zListItem(Action.A_DOUBLECLICK, subject);

		SleepUtil.sleepMedium();		
		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent(com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Locators.zCloseButton), "Verify Close button is there");

		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent("css=div[class='ZmTaskEditView'] tr[id$='_attachment_container'] div td a[class='AttLink']:contains('"+fileName+"')")," Verify only one Attachment present");
		ZAssert.assertFalse(app.zPageTasks.sIsElementPresent("xpath=//div[contains(@class,'ZmTaskEditView')]//tr[contains(@id,'_attachment_container')]/td/fieldset/form/div/div[2]"),"Verify Duplicate attachment is not present");

		app.zPageTasks.zClickAt(com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Locators.zCloseButton, "0,0");
		//After closing Task list view should show.
		ZAssert.assertTrue(app.zPageTasks.sGetEval("window.appCtxt.getCurrentViewType()").equalsIgnoreCase("TKL"),"Verify List view is open");

		//2nd Attempt

		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageTasks.zListItem(Action.A_DOUBLECLICK, subject);

		SleepUtil.sleepMedium();		
		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent(com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Locators.zCloseButton), "Verify Close button is there");

		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent("css=div[class='ZmTaskEditView'] tr[id$='_attachment_container'] div td a[class='AttLink']:contains('"+fileName+"')")," Verify only one Attachment present");
		ZAssert.assertFalse(app.zPageTasks.sIsElementPresent("xpath=//div[contains(@class,'ZmTaskEditView')]//tr[contains(@id,'_attachment_container')]/td/fieldset/form/div/div[2]"),"Verify Duplicate attachment is not present");

		//Close Edit window
		app.zPageTasks.zClickAt(com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Locators.zCloseButton, "0,0");

		//After closing Task list view should show.
		ZAssert.assertTrue(app.zPageTasks.sGetEval("window.appCtxt.getCurrentViewType()").equalsIgnoreCase("TKL"),"Verify List view is open");

		//3rd Attempt		
		app.zPageTasks.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageTasks.zListItem(Action.A_DOUBLECLICK, subject);

		SleepUtil.sleepMedium();		
		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent(com.zimbra.qa.selenium.projects.ajax.ui.tasks.FormTaskNew.Locators.zCloseButton), "Verify Close button is there");

		ZAssert.assertTrue(app.zPageTasks.sIsElementPresent("css=div[class='ZmTaskEditView'] tr[id$='_attachment_container'] div td a[class='AttLink']:contains('"+fileName+"')")," Verify only one Attachment present");
		ZAssert.assertFalse(app.zPageTasks.sIsElementPresent("xpath=//div[contains(@class,'ZmTaskEditView')]//tr[contains(@id,'_attachment_container')]/td/fieldset/form/div/div[2]"),"Verify Duplicate attachment is not present");


	}
}
