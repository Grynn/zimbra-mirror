package com.zimbra.qa.selenium.projects.octopus.core;

import java.util.ArrayList;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OctopusAccount;
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
	// Create a new folder, pass the required folder name
	protected FolderItem createFolderViaSoap(ZimbraAccount account, String folderName, FolderItem ...folderItemArray) throws HarnessException
	{
		FolderItem folderItem = FolderItem.importFromSOAP(account, SystemFolder.Briefcase);

		if ((folderItemArray != null) && folderItemArray.length >0) {
			folderItem = folderItemArray[0];
		}

		// send soap request
		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + folderName + "' l='"
				+ folderItem.getId()
				+ "' view='document'/>" + "</CreateFolderRequest>");

		// verify folder creation on the server
		return FolderItem.importFromSOAP(account, folderName);
	}
	// create a new zimbra account
	protected OctopusAccount getNewAccount() {
		OctopusAccount newAccount = new OctopusAccount();
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
	//Rename a file via Soap
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
	// Mark file favorite using soap
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
	// Unmark file favorite using soap
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

		// if file already upload, then delete and upload it again
		if (attachmentId == null) {
			account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
					+ "<action id='" + attachmentId + "' op='trash'/>"
					+ "</ItemActionRequest>");

			attachmentId = account.uploadFile(filePath);
		}

		// Save uploaded file to the root folder through SOAP
		account.soapSend(
				"<SaveDocumentRequest xmlns='urn:zimbraMail'>" + "<doc l='"
						+ folderItem.getId() + "'>" + "<upload id='"
						+ attachmentId + "'/>" + "</doc></SaveDocumentRequest>");

		//return id
		return account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");
	}
	// save document request
	protected void saveDocumentRequestViaSoap(ZimbraAccount account, FolderItem folder,String attachmentId) throws HarnessException
	{
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + folder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");
	}
	// get activity stream request
	protected void getActivityStreamRequest(ZimbraAccount account,FolderItem folder) throws HarnessException
	{
		account.soapSend(
				"<GetActivityStreamRequest xmlns='urn:zimbraMail' offset='0' limit='250' id='"
						+ folder.getId() + "'/>"
				);
	}
	// delete folder via Soap
	protected void deleteFolderViaSoap(ZimbraAccount account, FolderItem folder)throws HarnessException
	{
		account.soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>"
						+ "<action id='" + folder.getId() + "' op='delete'/>"
						+ "</ItemActionRequest>"
				);
	}
	//Function returns the array list containing folder Items. folder structure gets created is with hierarchy folder1>folder2>folder3.
	protected ArrayList<FolderItem> createMultipleSubfolders(ZimbraAccount act,String ParentFolder,int noOfSubFolders) throws HarnessException

	{
		ArrayList<FolderItem> folderNames = new ArrayList<FolderItem>();

		String _parent = ParentFolder;

		for(int i=0;i<noOfSubFolders;i++)
		{
			FolderItem newParentFolder = FolderItem.importFromSOAP(act, _parent);

			folderNames.add(newParentFolder);

			String subFolderName = "childFolder"+ZimbraSeleniumProperties.getUniqueString();
			// Create sub folder Using SOAP under a folder created

			act.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
							+"<folder name='" + subFolderName + "' l='" + newParentFolder.getId() + "' view='document'/>"
							+"</CreateFolderRequest>");

			_parent =subFolderName;


		}

		return folderNames;

	}
	// Function for checking if required document is present in destination folder or not.
	public boolean isDocumentPresentInFolder(ZimbraAccount acount,String folderName, String fileName)throws HarnessException
	{
		boolean docPresent= false;

		FolderItem FolderName = FolderItem.importFromSOAP(acount, folderName);

		acount.soapSend(
				"<SearchRequest xmlns=\"urn:zimbraMail\" types=\"document\">"
						+"<query>inid:"+FolderName.getId()+"</query>"
						+"</SearchRequest>"
				);

		docPresent= acount.soapMatch("//mail:SearchResponse/mail:doc", "name", fileName);

		return docPresent;
	}
	// create mountpoint request via soap
	protected void mountRequestViaSoap(ZimbraAccount account,ZimbraAccount grantee,FolderItem folder,FolderItem mountPointFolder,
			String mountPointName) throws HarnessException {
		grantee
		.soapSend("<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+ "<link l='" + mountPointFolder.getId()
				+ "' name='" + mountPointName
				+ "' view='document' rid='" + folder.getId()
				+ "' zid='" + account.ZimbraId + "'/>"
				+ "</CreateMountpointRequest>");
	}

}
