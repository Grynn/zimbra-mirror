package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.file;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.core.FeatureBriefcaseTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogUploadFile;

public class UploadFile extends FeatureBriefcaseTest {

	public UploadFile() throws HarnessException {
		logger.info("New " + UploadFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		if(ZimbraSeleniumProperties.zimbraGetVersionString().contains("FOSS")){
		    super.startingAccountPreferences.put("zimbraPrefShowSelectionCheckbox","TRUE");
		}			    
	}

	@Test(description = "Upload file through RestUtil - verify through SOAP", groups = { "smoke" })
	public void UploadFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/testsoundfile.wav";
		
		FileItem file = new FileItem(filePath);

		String fileName = file.getName();
		
		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);
	
		// Save uploaded file to briefcase through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +

		"<doc l='" + briefcaseFolder.getId() + "'>" +

		"<upload id='" + attachmentId + "'/>" +

		"</doc>" +

		"</SaveDocumentRequest>");

		account.soapSelectNode("//mail:SaveDocumentResponse", 1);

		// search the uploaded file
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + fileName + "</query>"
						+ "</SearchRequest>");

		// Verify file name through SOAP
		String name = account.soapSelectValue("//mail:doc", "name");
		ZAssert.assertEquals(name, fileName, "Verify file name through SOAP");
		
		//delete file upon test completion
		String id = account.soapSelectValue("//mail:doc", "id");
		app.zPageBriefcase.deleteFileById(id);
	}

	@Test(description = "Upload file through RestUtil - verify through GUI", groups = { "sanity" })
	public void UploadFile_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/putty.log";
		
		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Verify document is created
		String name = app.zPageBriefcase.getItemNameFromListView(fileName);
		ZAssert.assertStringContains(name, fileName, "Verify file name through GUI");
		
		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(fileItem.getName());
	}
	
	@Test(description = "Upload file through GUI - verify through GUI", groups = { "webdriver" })
	public void UploadFile_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();
		
		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);
		
		// Create file item
		final String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "\\data\\public\\other\\testtextfile.txt";
		
		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);
		
		// Click on Upload File button in the Toolbar
		DialogUploadFile dlg = (DialogUploadFile) app.zPageBriefcase.zToolbarPressButton(Button.B_UPLOAD_FILE, fileItem);
		
		String upload = "upload.vbs";
		
		if(new File(upload).isFile()){
		    dlg.zClickButton(Button.B_BROWSE);	
		    
		    Process p = null;
		    try{
			p = Runtime.getRuntime().exec("wscript.exe upload.vbs " + filePath);			
			for(int i = 0; ; i++){
			    SleepUtil.sleepSmall(); 
			    if (!isAlive(p) || i > 20) {
			        break;
			    }
			} 
		    } catch(Exception ex){
			logger.error(ex);			
		    } finally{
			p.destroy();
		    }
		}else{
		    throw new HarnessException(upload + " not found");
		}		
		
		dlg.zClickButton(Button.B_OK);
		
		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);
		
		SleepUtil.sleepSmall();
		
		// Click on created File
		if(ZimbraSeleniumProperties.zimbraGetVersionString().contains(
    			"FOSS")){
		    app.zPageBriefcase.zListItem(Action.A_BRIEFCASE_CHECKBOX, fileItem);

		}else{
		    app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);
		}
		
		// Verify file is uploaded
		String name = app.zPageBriefcase.getItemNameFromListView(fileName);
		ZAssert.assertStringContains(name, fileName, "Verify file name through GUI");
		
		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(fileItem.getName());
	}
	
	public static boolean isAlive(Process p){
	    try
	    {
		if(p.exitValue()==0){
		    return false;
		}else{
		    return true;
		}
	    } catch (IllegalThreadStateException itex) {
		logger.error(itex);
		return true;
	    } catch (Exception ex) {
		logger.error(ex);
		return false;
	    }
	}
	
}
