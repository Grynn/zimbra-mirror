package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.comments;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.CommentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFileComments;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class ViewSharedComment extends OctopusCommonTest {

	private ZimbraAccount destination = null;
	private String destinationDisplayName = null;
	
	public ViewSharedComment() throws HarnessException {
		logger.info("New " + ViewSharedComment.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
		
		destination = new ZimbraAccount();
		destinationDisplayName = "DisplayName"+ ZimbraSeleniumProperties.getUniqueString();
		destination.setPref("displayName", destinationDisplayName);
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
			ZAssert.assertEquals(found.getCommentEmail(), destinationDisplayName, "Verify the comment email matches");

		
		} finally {
			if ( fileComments != null ) {
				
				// close Comments view
				fileComments.zPressButton(Button.B_CLOSE);
				fileComments = null;

			}
		}

	}

}
