package com.zimbra.qa.selenium.projects.desktop.tests.briefcase.file;

import org.testng.annotations.AfterMethod;
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
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.DialogCheckInFile;

public class CheckInFile extends AjaxCommonTest {

   public CheckInFile() {
      logger.info("New " + CheckInFile.class.getCanonicalName());

      super.startingPage = app.zPageBriefcase;

      super.startingAccountPreferences = null;
   }

   @Test(description = "Check Out File through SOAP - right click 'Check In' - click 'Cancel'", groups = { "functional" })
   public void CheckInFile_01() throws HarnessException {
      ZimbraAccount account = app.zGetActiveAccount();

      FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
            SystemFolder.Briefcase);

      // Create file item
      String filePath = ZimbraSeleniumProperties.getBaseDirectory()
            + "/data/public/other/putty.log";

      FileItem file = new FileItem(filePath);

      String fileName = file.getName();

      // Upload file to server through RestUtil
      String attachmentId = account.uploadFile(filePath);

      // Save uploaded file to briefcase through SOAP
      account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
            + "<doc l='" + briefcaseFolder.getId() + "'>" + "<upload id='"
            + attachmentId + "'/>" + "</doc>" + "</SaveDocumentRequest>");

      // account.soapSelectNode("//mail:SaveDocumentResponse", 1);

      // search the uploaded file
      account
            .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
                  + "<query>"
                  + fileName
                  + "</query>"
                  + "</SearchRequest>");

      // Verify file name through SOAP
      String name = account.soapSelectValue("//mail:doc", "name");
      ZAssert.assertEquals(name, fileName, "Verify file name through SOAP");

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

      // refresh briefcase page
      app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

      SleepUtil.sleepVerySmall();
      
      //Verify Lock icon is not present for the uploaded file
      ZAssert.assertFalse(app.zPageBriefcase.isLockIconPresent(file),"Verify Lock icon is not present for the uploaded file");
      
      // Check Out file through SOAP
      String id = account.soapSelectValue("//mail:doc", "id");
      account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
            + "<action id='" + id + "' op='lock'/>"
            + "</ItemActionRequest>");

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

      // refresh briefcase page
      app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

      SleepUtil.sleepVerySmall();

      //Verify Lock icon is present for the checked out file
      ZAssert.assertTrue(app.zPageBriefcase.isLockIconPresent(file),"Verify Lock icon is present for the checked out file");

      // Right click on the locked file and select Check In File context menu
      DialogCheckInFile dlg = (DialogCheckInFile) app.zPageBriefcase
            .zListItem(Action.A_RIGHTCLICK, Button.O_CHECK_IN_FILE, file);

      // Verify the 'Check In File to Briefcase' dialog is displayed
      ZAssert.assertTrue(dlg.zIsActive(),
            "Verify the 'Check In File to Briefcase' dialog is displayed");

      // Dismiss dialog by clicking on Cancel button
      dlg.zClickButton(Button.B_CANCEL);

      // delete file upon test completion
      app.zPageBriefcase.deleteFileById(id);
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);
   }
   
   @Test(description = "Check Out File through SOAP - right click 'Discard Check Out'", groups = { "functional" })
   public void CheckInFile_02() throws HarnessException {
      ZimbraAccount account = app.zGetActiveAccount();

      FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
            SystemFolder.Briefcase);

      // Create file item
      String filePath = ZimbraSeleniumProperties.getBaseDirectory()
            + "/data/public/other/testtextfile.txt";

      FileItem file = new FileItem(filePath);

      String fileName = file.getName();

      // Upload file to server through RestUtil
      String attachmentId = account.uploadFile(filePath);

      // Save uploaded file to briefcase through SOAP
      account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
            + "<doc l='" + briefcaseFolder.getId() + "'>" + "<upload id='"
            + attachmentId + "'/>" + "</doc>" + "</SaveDocumentRequest>");

      // search the uploaded file
      account
            .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
                  + "<query>"
                  + fileName
                  + "</query>"
                  + "</SearchRequest>");

      // Verify file name through SOAP
      String name = account.soapSelectValue("//mail:doc", "name");
      ZAssert.assertEquals(name, fileName, "Verify file name through SOAP");

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

      // refresh briefcase page
      app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

      SleepUtil.sleepVerySmall();
      
      //Verify Lock icon is not present for the uploaded file
      ZAssert.assertFalse(app.zPageBriefcase.isLockIconPresent(file),"Verify Lock icon is not present for the uploaded file");
      
      // Check Out file through SOAP
      String id = account.soapSelectValue("//mail:doc", "id");
      account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
            + "<action id='" + id + "' op='lock'/>"
            + "</ItemActionRequest>");

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

      // refresh briefcase page
      app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

      SleepUtil.sleepVerySmall();

      //Verify Lock icon is present for the checked out file
      ZAssert.assertTrue(app.zPageBriefcase.isLockIconPresent(file),"Verify Lock icon is present for the checked out file");

      // Right click on the locked file and select Discard Check Out context menu
      app.zPageBriefcase
            .zListItem(Action.A_RIGHTCLICK, Button.O_DISCARD_CHECK_OUT, file);

      // refresh briefcase page
      app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

      SleepUtil.sleepVerySmall();
      
      //Verify Lock icon is removed for the corresponding file
      ZAssert.assertFalse(app.zPageBriefcase.isLockIconPresent(file),"Verify Lock icon is not present for the uploaded file");
            
      // delete file upon test completion
      app.zPageBriefcase.deleteFileById(id);
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);
   }

   @AfterMethod(alwaysRun=true)
   public void cleanup() {
      // This is needed because the next test might upload the same file as these tests do
      // and it will fail if there is a duplicate file in briefcase
      ZimbraAccount.ResetAccountZDC();
   }
}

