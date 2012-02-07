package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.comments;

import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.CommentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraCharsets;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraCharsets.ZCharset;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFileComments;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

/**
 * @author zimbra
 *
 */
public class ViewSharedComment extends OctopusCommonTest {

	private ZimbraAccount destination = null;
	
	public ViewSharedComment() throws HarnessException {
		logger.info("New " + ViewSharedComment.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
		
		destination = new ZimbraAccount();		
		destination.setPref("displayName", "DisplayName"+ ZimbraSeleniumProperties.getUniqueString());
		destination.provision();
		destination.authenticate();
		
	}

	@Test(
			description = "View another user's comment on a file",
			groups = { "smoke" })
	public void ViewSharedComment_01() throws HarnessException {

		String filename = "filename"+ ZimbraSeleniumProperties.getUniqueString() +".txt";
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/documents/doc01/plaintext.txt";
		String subFolderName = "subFolder" + ZimbraSeleniumProperties.getUniqueString();

		
		String commentText = "Comment" + ZimbraSeleniumProperties.getUniqueString();



		// Save uploaded file through SOAP
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);

		// Create a shared folder
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder name='" + subFolderName + "' l='" + briefcaseRootFolder.getId() + "' view='document'/>"
			+	"</CreateFolderRequest>");
		FolderItem subFolderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), subFolderName);
		ZAssert.assertNotNull(subFolderItem, "Verify the subfolder is available");

		app.zGetActiveAccount().soapSend(
				"<FolderActionRequest xmlns='urn:zimbraMail'>"
			+		"<action op='grant' id='" + subFolderItem.getId() + "'>"
			+			"<grant gt='guest' perm='rwidxa' d='"+ destination.EmailAddress +"'/>"
			+		"</action>"
			+	"</FolderActionRequest>");

		
		// Upload file to server through RestUtil
		String attachmentId = app.zGetActiveAccount().uploadFile(filePath);

		app.zGetActiveAccount().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+		"<doc name='"+ filename +"' l='" + subFolderItem.getId() + "'>"
				+			"<upload id='" + attachmentId + "'/>"
				+		"</doc>"
				+	"</SaveDocumentRequest>");
		String documentId = app.zGetActiveAccount().soapSelectValue("//mail:doc", "id");




		// Add comments to the file using SOAP
		destination.soapSend(
					"<AddCommentRequest xmlns='urn:zimbraMail'>"
				+		"<comment parentId='"+ app.zGetActiveAccount().ZimbraId + ":" + documentId + "' text='" + commentText + "'/>"
				+	"</AddCommentRequest>");

		// Get file comments through SOAP
		destination.soapSend(
					"<GetCommentsRequest  xmlns='urn:zimbraMail'>"
				+		"<comment parentId='"+ app.zGetActiveAccount().ZimbraId + ":" + documentId + "'/>"
				+	"</GetCommentsRequest>");


		

		// Sync up
//		app.zPageOctopus.zToolbarPressButton(Button.B_GETMAIL);

		// Click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		
		// Since we are going to a subfolder, always back out to My Files at the end
		try {
			
			// Go to the subfolder
			app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName);
	
			// Verify file exists in My Files view
			ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
					PageMyFiles.Locators.zMyFilesListViewItems.locator
							+ ":contains(" + filename + ")", "3000"),
					"Verify file appears in My Files view");
	
			// Select file in the list view
			DisplayFilePreview filePreview = (DisplayFilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, filename);
	
			DisplayFileComments fileComments = null;
			
			try {
	
				// Click on Comments button
				fileComments = (DisplayFileComments) filePreview.zPressButton(Button.B_COMMENTS);
				
				CommentItem found = null;
				List<CommentItem> comments = fileComments.zGetCommentsList();
				for ( CommentItem comment : comments ) {
					
					// Verify the comment is found
					if (comment.getCommentText().equals(commentText)) {
						found = comment;
						break;
					}
					
				}
				
				ZAssert.assertNotNull(found, "Verify the commment is found");
				
				ZAssert.assertEquals(found.getCommentText(), commentText, "Verify the comment text matches");
				ZAssert.assertEquals(found.getCommentEmail(), destination.getPref("displayName"), "Verify the comment email matches");
	
			
			} finally {
				if ( fileComments != null ) {
					
					// close Comments view
					fileComments.zPressButton(Button.B_CLOSE);
					fileComments = null;
	
				}
			}

		} finally {
			app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);
			app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
		}

	}
	
	/**
	 * Since the following tests all have the same steps, just different
	 * values being used, this method will run the test steps for:
	 * 
	 * 1. Given a different account and a comment
	 * 2. The test account shares a file
	 * 2. The different account comments on a file
	 * 3. Test account verifies the displayed name (displayName) and comment text
	 * 
	 * @param commentor the other account that creates the comment
	 * @param comment the comment text
	 * @throws HarnessException
	 */
	private void verifyCommentDetails(ZimbraAccount commentor, String comment) throws HarnessException {
			
		String filename = "filename"+ ZimbraSeleniumProperties.getUniqueString() +".txt";
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/documents/doc01/plaintext.txt";
		String subFolderName = "subFolder" + ZimbraSeleniumProperties.getUniqueString();

		



		// Save uploaded file through SOAP
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);

		// Create a shared folder
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder name='" + subFolderName + "' l='" + briefcaseRootFolder.getId() + "' view='document'/>"
			+	"</CreateFolderRequest>");
		FolderItem subFolderItem = FolderItem.importFromSOAP(app.zGetActiveAccount(), subFolderName);
		ZAssert.assertNotNull(subFolderItem, "Verify the subfolder is available");

		app.zGetActiveAccount().soapSend(
				"<FolderActionRequest xmlns='urn:zimbraMail'>"
			+		"<action op='grant' id='" + subFolderItem.getId() + "'>"
			+			"<grant gt='guest' perm='rwidxa' d='"+ commentor.EmailAddress +"'/>"
			+		"</action>"
			+	"</FolderActionRequest>");

		
		// Upload file to server through RestUtil
		String attachmentId = app.zGetActiveAccount().uploadFile(filePath);

		app.zGetActiveAccount().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+		"<doc name='"+ filename +"' l='" + subFolderItem.getId() + "'>"
				+			"<upload id='" + attachmentId + "'/>"
				+		"</doc>"
				+	"</SaveDocumentRequest>");
		String documentId = app.zGetActiveAccount().soapSelectValue("//mail:doc", "id");




		// Add comments to the file using SOAP
		commentor.soapSend(
					"<AddCommentRequest xmlns='urn:zimbraMail'>"
				+		"<comment parentId='"+ app.zGetActiveAccount().ZimbraId + ":" + documentId + "' text='" + XmlStringUtil.escapeXml(comment) + "'/>"
				+	"</AddCommentRequest>");

		// Get file comments through SOAP
		commentor.soapSend(
					"<GetCommentsRequest  xmlns='urn:zimbraMail'>"
				+		"<comment parentId='"+ app.zGetActiveAccount().ZimbraId + ":" + documentId + "'/>"
				+	"</GetCommentsRequest>");


		

		// Sync up
//		app.zPageOctopus.zToolbarPressButton(Button.B_GETMAIL);

		// Click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Since we are going to a subfolder, always back out to My Files at the end
		try {
			

			// Go to the subfolder
			app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName);
	
			// Verify file exists in My Files view
			ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
					PageMyFiles.Locators.zMyFilesListViewItems.locator
							+ ":contains(" + filename + ")", "3000"),
					"Verify file appears in My Files view");
	
			// Select file in the list view
			DisplayFilePreview filePreview = (DisplayFilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, filename);
	
			DisplayFileComments fileComments = null;
			
			try {
	
				// Click on Comments button
				fileComments = (DisplayFileComments) filePreview.zPressButton(Button.B_COMMENTS);
				
				CommentItem found = null;
				List<CommentItem> comments = fileComments.zGetCommentsList();
				for ( CommentItem item : comments ) {
					
					// Verify the comment is found
					if (item.getCommentText().equals(comment)) {
						found = item;
						break;
					}
					
				}
				
				ZAssert.assertNotNull(found, "Verify the commment is found");
				
				ZAssert.assertEquals(
						found.getCommentEmail(),
						// If there is no displayName set, the app should fall back to the EmailAddress
						commentor.getPref("displayName") == null ? commentor.EmailAddress : commentor.getPref("displayName"), 
						"Verify that in absense of a display name, the email is shown");

				ZAssert.assertEquals(
						found.getCommentText(), 
						comment, 
						"Verify that in absense of a display name, the email is shown");

			
			} finally {
				if ( fileComments != null ) {
					
					// close Comments view
					fileComments.zPressButton(Button.B_CLOSE);
					fileComments = null;
	
				}
			}
		
		} finally {
			app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);
			app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);			
		}


	}


	@Test(
			description = "View comment from another user that does not contain a display name",
			groups = { "functional" })
	public void ViewSharedComment_DisplayName_01() throws HarnessException {

		ZimbraAccount commentor = new ZimbraAccount();
		commentor.clearPref("displayName"); // unset the displayName
		commentor.provision();
		commentor.authenticate();
		
		String comment = "Comment"+ ZimbraSeleniumProperties.getUniqueString();
		
		verifyCommentDetails(commentor, comment);

	}
	
	
	@DataProvider(name = "DataProviderDisplayNames")
	public Object[][] DataProviderDeleteKeys() throws HarnessException {
		return (ZimbraCharsets.getInstance().getSampleTable());
	}

	@Test(
			description = "View comment from another user where displayName contains special characters",
			groups = { "functional" },
			dataProvider = "DataProviderDisplayNames")
	public void ViewSharedComment_DisplayName_02(ZCharset charset, String displayName) throws HarnessException {

		ZimbraAccount commentor = new ZimbraAccount();
		commentor.setPref("displayName", displayName);
		commentor.provision();
		commentor.authenticate();
		
		String comment = "Comment"+ ZimbraSeleniumProperties.getUniqueString();
		
		verifyCommentDetails(commentor, comment);
	}

	@Test(
			description = "View comment from another user where comment contains special characters",
			groups = { "functional" },
			dataProvider = "DataProviderDisplayNames")
	public void ViewSharedComment_Comment_01(ZCharset charset, String comment) throws HarnessException {
		
		verifyCommentDetails(destination, comment);
	}




}
