package com.zimbra.qa.selenium.projects.octopus.core;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class CommonMethods {
	
	public CommonMethods() {}

	// return comment id
	protected String MakeACommentViaSoap(ZimbraAccount account, String fileId, String comment)
	throws HarnessException {
		// Add comments to the file using SOAP
		account.soapSend("<AddCommentRequest xmlns='urn:zimbraMail'> <comment parentId='"
			+ fileId + "' text='" + comment + "'/></AddCommentRequest>");

		// TODO: check if this call is redundant
		// Get file comments through SOAP
		account.soapSend("<GetCommentsRequest  xmlns='urn:zimbraMail'> <comment parentId='"
			+ fileId + "'/></GetCommentsRequest>");
		
		
		return account.soapSelectValue("//mail:comment", "id");

	}
	

	
	protected String RenameViaSoap(ZimbraAccount account, String fileId, String newName)
	throws HarnessException {
		// Add comments to the file using SOAP
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'> <action id='"
			+ fileId + "' name='" + newName + "' op='rename' /></ItemActionRequest>");

        //TODO: Check if the file is renamed on the server use GetActiviyStreamRequest?		
	    return newName;
	}
	

	
	protected void MarkFileFavoriteViaSoap(ZimbraAccount account, String fileId)
	throws HarnessException {
	 account.soapSend
       ("<DocumentActionRequest xmlns='urn:zimbraMail'>"
		+ "<action id='" + fileId + "'  op='watch' /></DocumentActionRequest>");
	} 

	protected void UnMarkFileFavoriteViaSoap(ZimbraAccount account, String fileId)
	throws HarnessException {
	 account.soapSend
       ("<DocumentActionRequest xmlns='urn:zimbraMail'>"
		+ "<action id='" + fileId + "'  op='!watch' /></DocumentActionRequest>");
	} 

	// upload file
	protected String uploadFileViaSoap(ZimbraAccount account, String fileName) 
    throws HarnessException {

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
		SystemFolder.Briefcase);

		// Create file item
        String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/" + fileName;

        // Upload file to server through RestUtil
        String attachmentId = account.uploadFile(filePath);

        // Save uploaded file to the root folder through SOAP
         account.soapSend(
         "<SaveDocumentRequest xmlns='urn:zimbraMail'>" + "<doc l='"
		+ briefcaseRootFolder.getId() + "'>" + "<upload id='"
		+ attachmentId + "'/>" + "</doc></SaveDocumentRequest>");

        //return id
        return account.soapSelectValue(
		  "//mail:SaveDocumentResponse//mail:doc", "id");
    }

}
