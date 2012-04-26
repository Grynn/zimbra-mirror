/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.util.*;

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
		
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("can't find that message row: "+ locator);
		}
		
		DisplayConversationMessage item = new DisplayConversationMessage(this.MyApplication);

		String id = this.sGetAttribute(locator + "@id");
		item.setItemId(id);

		return (item);
	}


	public List<DisplayConversationMessage> zListGetMessages() throws HarnessException {
		List<DisplayConversationMessage> items = new ArrayList<DisplayConversationMessage>();
		
		String listLocator = "css=div#zv__CLV-main__CV div[id$='_messages']";
		String rowLocator = "div.ZmMailMsgCapsuleView";

		// Make sure the button exists
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("Message List View Rows is not present: " + listLocator);

		
		// How many items are in the table?
		int count = this.sGetCssCount(listLocator + " " + rowLocator);
		logger.debug(myPageName() + " zListGetMessages: number of messages: "+ count);

		this.zGetHtml(listLocator);
		
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

	@Override
	public boolean zIsActive() throws HarnessException {
		SleepUtil.sleep(5000);
		return (true);
	}
}
