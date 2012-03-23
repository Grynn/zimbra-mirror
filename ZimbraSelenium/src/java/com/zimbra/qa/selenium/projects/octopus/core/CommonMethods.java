package com.zimbra.qa.selenium.projects.octopus.core;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class CommonMethods {
	
	public CommonMethods() {}

	// revoke sharing a folder via soap
	protected void revokeShareFolderViaSoap(ZimbraAccount account, ZimbraAccount grantee, FolderItem folder) throws HarnessException {

		account.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
			+ "<action id='" + folder.getId()
			+ "' op='!grant' zid='" +  grantee.ZimbraId +"'" + ">"  
			+ "</FolderActionRequest>");
	}

	// share a folder via soap
	protected void shareFolderViaSoap(ZimbraAccount account, ZimbraAccount grantee, FolderItem folder,
			  String permission) throws HarnessException {

		account.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
			+ "<action id='" + folder.getId()
			+ "' op='grant'>" + "<grant d='"
			+ grantee.EmailAddress + "' gt='usr' perm='" + permission + "'/>"
			+ "</action>" + "</FolderActionRequest>");

		account.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
			+ "<item id='"
			+ folder.getId()
			+ "'/>"
			+ "<e a='"
			+ grantee.EmailAddress
			+ "'/>"
			+ "<notes _content='share folder invitation'/>"
			+ "</SendShareNotificationRequest>");
	}
	// create a new folder via soap
	protected FolderItem createFolderViaSoap(ZimbraAccount account) throws HarnessException {

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account, SystemFolder.Briefcase);

		// generate folder name
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// send soap request
	    account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
					+ "<folder name='" + foldername + "' l='"
					+ briefcaseFolder.getId()
					+ "' view='document'/>" + "</CreateFolderRequest>");

	    // get the folder Item
	    FolderItem folderItem = FolderItem.importFromSOAP(account, foldername);

	    return folderItem;
	}
	
	// create a new zimbra account
	protected ZimbraAccount getNewAccount() {
		ZimbraAccount newAccount = new ZimbraAccount();
		newAccount.provision();
		newAccount.authenticate();
		return newAccount;
	}
	
	// return comment id
	protected String makeCommentViaSoap(ZimbraAccount account, String fileId, String comment)
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
	

	
	protected String renameViaSoap(ZimbraAccount account, String fileId, String newName)
	throws HarnessException {
		// Add comments to the file using SOAP
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'> <action id='"
			+ fileId + "' name='" + newName + "' op='rename' /></ItemActionRequest>");

        //TODO: Check if the file is renamed on the server use GetActiviyStreamRequest?		
	    return newName;
	}
	

	
	protected void markFileFavoriteViaSoap(ZimbraAccount account, String fileId)
	throws HarnessException {
	 account.soapSend
       ("<DocumentActionRequest xmlns='urn:zimbraMail'>"
		+ "<action id='" + fileId + "'  op='watch' /></DocumentActionRequest>");
	} 

	protected void unMarkFileFavoriteViaSoap(ZimbraAccount account, String fileId)
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

	
	// upload file
	protected String uploadFileViaSoap(ZimbraAccount account, String fileName, FolderItem folderItem) 
    throws HarnessException {

	
		// Create file item
        String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/" + fileName;

        // Upload file to server through RestUtil
        String attachmentId = account.uploadFile(filePath);

        // Save uploaded file to the root folder through SOAP
         account.soapSend(
         "<SaveDocumentRequest xmlns='urn:zimbraMail'>" + "<doc l='"
		+ folderItem.getId() + "'>" + "<upload id='"
		+ attachmentId + "'/>" + "</doc></SaveDocumentRequest>");

        //return id
        return account.soapSelectValue(
		  "//mail:SaveDocumentResponse//mail:doc", "id");
    }

}
