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
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFileComments;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class ViewComment extends OctopusCommonTest {


	public ViewComment() {
		logger.info("New " + ViewComment.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
	}

	@Test(
			description = "View a comment on a file",
			groups = { "smoke" })
	public void ViewComment_01() throws HarnessException {

		String filename = "filename"+ ZimbraSeleniumProperties.getUniqueString() +".txt";
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/documents/doc01/plaintext.txt";
	
		String commentText = "Comment" + ZimbraSeleniumProperties.getUniqueString();


		// Upload file to server through RestUtil
		String attachmentId = app.zGetActiveAccount().uploadFile(filePath);

		// Save uploaded file through SOAP
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);

		app.zGetActiveAccount().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+		"<doc name='"+ filename +"' l='" + briefcaseRootFolder.getId() + "'>"
				+			"<upload id='" + attachmentId + "'/>"
				+		"</doc>"
				+	"</SaveDocumentRequest>");
		String documentId = app.zGetActiveAccount().soapSelectValue("//mail:doc", "id");


		
		// Sync up
//		app.zPageOctopus.zToolbarPressButton(Button.B_GETMAIL);


		// Add comments to the file using SOAP
		app.zGetActiveAccount().soapSend(
					"<AddCommentRequest xmlns='urn:zimbraMail'>"
				+		"<comment parentId='"+ documentId + "' text='" + commentText + "'/>"
				+	"</AddCommentRequest>");

		// Get file comments through SOAP
		app.zGetActiveAccount().soapSend(
					"<GetCommentsRequest  xmlns='urn:zimbraMail'>"
				+		"<comment parentId='"+ documentId + "'/>"
				+	"</GetCommentsRequest>");


		
		
		// Click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

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
			ZAssert.assertEquals(found.getCommentEmail(), app.zGetActiveAccount().getPref("displayName"), "Verify the comment email matches");

		
		} finally {
			if ( fileComments != null ) {
				
				// close Comments view
				fileComments.zPressButton(Button.B_CLOSE);
				fileComments = null;

			}
		}

	}

}
