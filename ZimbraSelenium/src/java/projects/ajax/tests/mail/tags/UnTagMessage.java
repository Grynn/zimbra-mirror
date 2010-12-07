package projects.ajax.tests.mail.tags;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import framework.items.MailItem;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraSeleniumProperties;

public class UnTagMessage extends AjaxCommonTest {

	public UnTagMessage() {
		logger.info("New "+ UnTagMessage.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
			
		super.startingAccount = null;		
		
	}

	
	@Test(	description = "Remove a tag from a message using Toolbar -> Tag -> New Tag",
			groups = { "smoke" })
	public void UnTagMessage_01() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		String tagname = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Create a tag
		app.getActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
                	"<tag name='"+ tagname +"' color='1' />" +
                "</CreateTagRequest>");
		String tagid = app.getActiveAccount().soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");
	

		// Add a message to the mailbox
		app.getActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                		"<m l='"+ app.getActiveAccount().getFolderIdByName("Inbox") +"' t='"+ tagid +"'>" +
                    		"<content>From: foo@foo.com\n" +
"To: foo@foo.com \n" +
"Subject: "+ subject +"\n" +
"MIME-Version: 1.0 \n" +
"Content-Type: text/plain; charset=utf-8 \n" +
"Content-Transfer-Encoding: 7bit\n" +
"\n" +
"simple text string in the body\n" +
"</content>" +
                    	"</m>" +
                	"</AddMsgRequest>");
		
		// Get the message data from SOAP
		MailItem mail = new MailItem();
		mail.importFromSOAP(app.getActiveAccount(), "subject:("+ subject +")");
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
	
		// Untag it
		app.zPageMail.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);
		

		app.getActiveAccount().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail'>" +
						"<m id='"+ mail.id +"'/>" +
					"</GetMsgRequest>");
		String mailTags = app.getActiveAccount().soapSelectValue("//mail:GetMsggResponse//mail:m", "t");
		
		ZAssert.assertNull(mailTags, "Verify that the tag is removed from the message");
		

		
	}

}
