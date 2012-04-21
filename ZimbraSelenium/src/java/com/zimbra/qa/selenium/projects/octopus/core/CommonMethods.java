package com.zimbra.qa.selenium.projects.octopus.core;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
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
			+ "</action>"
			+ "</FolderActionRequest>");
		
		
	}

	
	// create mountpoint via soap
	protected void mountFolderViaSoap(ZimbraAccount account, ZimbraAccount grantee, FolderItem folder,
			  String permission, FolderItem mountPointFolder, String mountPointName) throws HarnessException {

		account.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + folder.getId()
				+ "' op='grant'>" + "<grant d='"
				+ grantee.EmailAddress + "' gt='usr' perm='" + permission + "'/>"
				+ "</action>" + "</FolderActionRequest>");



		grantee.soapSend("<CreateMountpointRequest xmlns='urn:zimbraMail'>"
						+ "<link l='" + mountPointFolder.getId()
						+ "' name='" + mountPointName
						+ "' view='document' rid='" + folder.getId()
						+ "' zid='" + account.ZimbraId + "'/>"
						+ "</CreateMountpointRequest>");

		
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
			+ "<notes _content='You are invited to view my shared folder " + folder.getName() + " '/>"
			+ "</SendShareNotificationRequest>");
	}

	// create a new folder via soap
	protected FolderItem createFolderViaSoap(ZimbraAccount account, FolderItem ...folderItemArray) throws HarnessException {
		
		FolderItem folderItem = FolderItem.importFromSOAP(account, SystemFolder.Briefcase);

		if ((folderItemArray != null) && folderItemArray.length >0) {
			folderItem = folderItemArray[0];
		}
		
		// generate folder name
		String foldername = "Folder " + ZimbraSeleniumProperties.getUniqueString();

		// send soap request
	    account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
					+ "<folder name='" + foldername + "' l='"
					+ folderItem.getId()
					+ "' view='document'/>" + "</CreateFolderRequest>");

	    // verify folder creation on the server 
	    return FolderItem.importFromSOAP(account, foldername); 
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

		SleepUtil.sleepVerySmall();
       
		//TODO: verify valid id?
		return account.soapSelectValue("//mail:AddCommentResponse//mail:comment", "id");		
	}
	

	
	protected String renameViaSoap(ZimbraAccount account, String fileId, String newName)
	throws HarnessException {
		// Rename file using SOAP
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'> <action id='"
			+ fileId + "' name='" + newName + "' op='rename' /></ItemActionRequest>");

		SleepUtil.sleepVerySmall();

		//verification
		ZAssert.assertTrue(account.soapMatch(
				"//mail:ItemActionResponse//mail:action", "op", "rename"),
				"Verify file is renamed to " + newName);

		return newName;
	}
	

	
	
	protected void markFileFavoriteViaSoap(ZimbraAccount account, String fileId)
	throws HarnessException {
		account.soapSend("<DocumentActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + fileId + "'  op='watch' /></DocumentActionRequest>");

		SleepUtil.sleepVerySmall();

		//verification
		ZAssert.assertTrue(account.soapMatch(
				"//mail:DocumentActionResponse//mail:action", "op", "watch"),
				"Verify file is marked as favorite");
		
	} 

	protected void unMarkFileFavoriteViaSoap(ZimbraAccount account, String fileId)
	throws HarnessException {
		account.soapSend("<DocumentActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + fileId + "'  op='!watch' /></DocumentActionRequest>");

		SleepUtil.sleepVerySmall();

		//verification
		ZAssert.assertTrue(account.soapMatch(
				"//mail:DocumentActionResponse//mail:action", "op", "!watch"),
				"Verify file is inmarked favorite");
		
	} 

	
	// upload file
	protected String uploadFileViaSoap(ZimbraAccount account, String fileName, FolderItem ...folderItemArray) 
    throws HarnessException {
		FolderItem folderItem = FolderItem.importFromSOAP(account, SystemFolder.Briefcase);

		if ((folderItemArray != null) && folderItemArray.length >0) {
			folderItem = folderItemArray[0];
		}
		
	    
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
