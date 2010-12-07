package projects.ajax.tests.mail.tags;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.tests.mail.mail.DeleteMail;
import projects.ajax.ui.DialogTag;
import framework.items.MailItem;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraSeleniumProperties;

public class TagMessage extends AjaxCommonTest {

	public TagMessage() {
		logger.info("New "+ DeleteMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
			
		super.startingAccount = null;		
		
	}

	
	@Test(	description = "Tag a message using Toolbar -> Tag -> New Tag",
			groups = { "smoke" })
	public void TagMessage_01() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		

		// Add a message to the mailbox
		app.getActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                		"<m l='"+ app.getActiveAccount().getFolderIdByName("Inbox") +"'>" +
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
		
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Click new tag
		DialogTag dialogTag = (DialogTag) app.zPageMail.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);
		
		app.getActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.getActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tagName +"']", "id");


		app.getActiveAccount().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail'>" +
						"<m id='"+ mail.id +"'/>" +
					"</GetMsgRequest>");
		String mailTags = app.getActiveAccount().soapSelectValue("//mail:GetMsggResponse//mail:m", "t");
		 
		ZAssert.assertEquals(tagID, mailTags, "Verify the tag appears on the message");
		

		
	}

}
