/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * @author zimbra
 *
 */
public class DisplayConversation extends DisplayMail {

	protected DisplayConversation(AbsApplication application) {
		super(application);

		logger.info("new " + DisplayConversation.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	private DisplayConversationMessage parseMessageRow(String locator) throws HarnessException {
		DisplayConversationMessage item = new DisplayConversationMessage(this.MyApplication);

		String id = this.sGetAttribute(locator + "@id");
		item.setItemLocator("css=div#"+ id);

		return (item);
	}


	public List<DisplayConversationMessage> zListGetMessages() throws HarnessException {
		List<DisplayConversationMessage> items = new ArrayList<DisplayConversationMessage>();
		
		String listLocator = "css=div#zv__CLV-main__CV div[id$='_messages']";
		String rowLocator = "div.ZmMailMsgCapsuleView";

		// Make sure the button exists
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("Message List View Rows is not present: " + listLocator);

		String tableLocator = listLocator + " " + rowLocator;
		
		// How many items are in the table?
		int count = this.sGetCssCount(tableLocator);
		logger.debug(myPageName() + " zListGetMessages: number of messages: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			// Add the new item to the list
			DisplayConversationMessage item = parseMessageRow(listLocator + " " + rowLocator + ":nth-of-type("+ i +")");
			items.add(item);
			logger.info(item.prettyPrint());
		}

		return (items);
	}

	public List<DisplayConversationMessage> zListItem() throws HarnessException {
		List<DisplayConversationMessage> items = new ArrayList<DisplayConversationMessage>();
		return (items);
	}

}
