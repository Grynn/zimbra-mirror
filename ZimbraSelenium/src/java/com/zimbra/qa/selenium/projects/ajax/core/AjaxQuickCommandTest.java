package com.zimbra.qa.selenium.projects.ajax.core;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.QuickCommand;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.items.QuickCommand.QCAction;
import com.zimbra.qa.selenium.framework.items.QuickCommand.QCItemTypeId;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

/**
 * A base class that creates some basic Quick Command shortcuts in the mailbox.  This
 * class should only be used when testing Quick Commands.
 * 
 * 
 * @author Matt Rhoades
 *
 */
public class AjaxQuickCommandTest extends AjaxCommonTest {
	protected static Logger logger = LogManager.getLogger(AjaxQuickCommandTest.class);

	private QuickCommand command1;
	private QuickCommand command2;
	private QuickCommand command3;


	public AjaxQuickCommandTest() {
		logger.info("New "+ AjaxQuickCommandTest.class.getCanonicalName());
		
		command1 = null;
		command2 = null;
		command3 = null;
		
	}

	/**
	 * Quick Command:
	 * Item Type: Mail
	 * Action 1: Tag
	 * Action 2: Mark Read
	 * Action 3: Mark Flagged
	 * Action 4: Move To Subfolder
	 * @throws HarnessException
	 */
	protected QuickCommand getQuickCommand01() throws HarnessException {
		
		if ( command1 != null ) {
			// Command already exists, just return it
			return (command1);
		}
		
		
		// Create a tag
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
					"<CreateTagRequest xmlns='urn:zimbraMail'>"
				+		"<tag name='"+ tagname +"' color='1' />"
				+	"</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(ZimbraAccount.AccountZWC(), tagname);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		// Create a subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ foldername +"' l='1' view='message'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// Create the list of actions
		ArrayList<QCAction> actions = new ArrayList<QCAction>();
		actions.add(new QCAction(QCAction.QCTypeId.actionTag, tag.getId(), true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFlag, "read", true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFlag, "flagged", true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFileInto, folder.getId(), true));


		String name = "name" + ZimbraSeleniumProperties.getUniqueString();
		String description = "description" + ZimbraSeleniumProperties.getUniqueString();
		
		command1 = new QuickCommand(name, description, QCItemTypeId.MSG, true);
		command1.addActions(actions);

		return (command1);
	}
	
	/**
	 * Quick Command:
	 * Item Type: Contact
	 * Action 1: Tag
	 * Action 2: Move To Subfolder
	 * @throws HarnessException
	 */
	protected QuickCommand getQuickCommand02() throws HarnessException {

		
		if ( command2 != null ) {
			// Command already exists, just return it
			return (command2);
		}
		
		
		// Create a tag
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
					"<CreateTagRequest xmlns='urn:zimbraMail'>"
				+		"<tag name='"+ tagname +"' color='1' />"
				+	"</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(ZimbraAccount.AccountZWC(), tagname);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		// Create a subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ foldername +"' l='1' view='contact'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// Create the list of actions
		ArrayList<QCAction> actions = new ArrayList<QCAction>();
		actions.add(new QCAction(QCAction.QCTypeId.actionTag, tag.getId(), true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFileInto, folder.getId(), true));


		String name = "name" + ZimbraSeleniumProperties.getUniqueString();
		String description = "description" + ZimbraSeleniumProperties.getUniqueString();
		
		command2 = new QuickCommand(name, description, QCItemTypeId.CONTACT, true);
		command2.addActions(actions);

		return (command2);
	}
	
	/**
	 * Quick Command:
	 * Item Type: Appointment
	 * Action 1: Tag
	 * Action 2: Mark Read
	 * Action 3: Mark Flagged
	 * Action 4: Move To Subfolder
	 * @throws HarnessException
	 */
	protected QuickCommand getQuickCommand03() throws HarnessException {
		
		if ( command3 != null ) {
			// Command already exists, just return it
			return (command3);
		}
		
		
		// Create a tag
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
					"<CreateTagRequest xmlns='urn:zimbraMail'>"
				+		"<tag name='"+ tagname +"' color='1' />"
				+	"</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(ZimbraAccount.AccountZWC(), tagname);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		// Create a subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountZWC().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='"+ foldername +"' l='1' view='appointment'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// Create the list of actions
		ArrayList<QCAction> actions = new ArrayList<QCAction>();
		actions.add(new QCAction(QCAction.QCTypeId.actionTag, tag.getId(), true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFlag, "read", true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFlag, "flagged", true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFileInto, folder.getId(), true));


		String name = "name" + ZimbraSeleniumProperties.getUniqueString();
		String description = "description" + ZimbraSeleniumProperties.getUniqueString();
		
		command3 = new QuickCommand(name, description, QCItemTypeId.APPT, true);
		command3.addActions(actions);

		return (command3);
	}
	

	/**
	 * Set up basic quick commands for all combinations 
	 * @throws HarnessException
	 */
	@BeforeClass( groups = { "always" } )
	public void addQuickCommands() throws HarnessException {
		logger.info("addQuickCommands: start");
		

		// Create a quick command in the user preferences
		ZimbraAccount.AccountZWC().soapSend(
				"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
				+		"<pref name='zimbraPrefQuickCommand'>"+ this.getQuickCommand01().toString() +"</pref>"
				+		"<pref name='zimbraPrefQuickCommand'>"+ this.getQuickCommand02().toString() +"</pref>"
				+		"<pref name='zimbraPrefQuickCommand'>"+ this.getQuickCommand03().toString() +"</pref>"
				+	"</ModifyPrefsRequest>");

		
		// Re-login to pick up the new preferences
		super.startingPage.zRefresh();

		// The AjaxCommonTest.commonTestBeforeMethod() method will log into the client
		
		logger.info("addQuickCommands: finish");


	}


}
