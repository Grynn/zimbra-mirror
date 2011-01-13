/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;


/**
 * This class represents a mail message
 * 
 * @author Matt Rhoades
 *
 */
public class ConversationItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	/**
	 * Whether the checkbox is checked or not
	 */
	public boolean isSelected = false;
	
	/**
	 * Whether the conversation is expanded or not
	 */
	public boolean isExpanded = false;
	
	/**
	 * Whether the conversation is flagged or not
	 */
	public boolean isFlagged = false;
	
	/**
	 * The priority for this conversation, high, none, low
	 * TODO: change to enum
	 */
	public String priority = "none";
	
	/**
	 * The "From" contents
	 */
	public String from;
	
	/**
	 * Whether or not the conversation has attachments
	 */
	public boolean hasAttachments = false;
	
	/**
	 * The subject for this mail
	 */
	public String subject;
			
	/**
	 * The fragment for this mail
	 */
	public String fragment;
	
	/**
	 * The folder for this conversation (string format)
	 */
	public String folder;
	
	/**
	 * The count of messages in this conversation (string format)
	 */
	public String size;
	
	/**
	 * The received field for this conversation (string format)
	 */
	public String received;
	
	
	/**
	 * The read/unread status of this conversation
	 */
	public boolean read;

	////
	// START: GUI Data
	////

	public boolean gIsChecked;
	
	public boolean gIsFlagged;

	public String gPriority; // TODO: how to represent the icon?
	
	public String gTags; // TODO: how to represent the icon?

	public String gStatusIcon; // TODO: how to represent these status icons?
	
	public String gFrom;

	public boolean gHasAttachments;

	public String gSubject;

	public String gFragment;

	public String gFolder;

	public String gSize;

	public String gReceived;

	
	/**
	 * Is the message currently highlighted in the list view?
	 */
	public boolean gIsSelected;


	/**
	 * The text body of the message
	 */
	public String gBodyText;
	
	
	////
	// FINISH: GUI Data
	////
		
	
	/**
	 * Create a mail item
	 */
	public ConversationItem() {
	}

	
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static ConversationItem importFromSOAP(Element GetMsgResponse) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static ConversationItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(ConversationItem.class.getSimpleName()).append('\n');
		sb.append("isSelected: ").append(isSelected).append('\n');
		sb.append("isExpanded: ").append(isExpanded).append('\n');
		sb.append("isFlagged: ").append(isFlagged).append('\n');
		sb.append("priority: ").append(priority).append('\n');
		sb.append("from: ").append(from).append('\n');
		sb.append("hasAttachments: ").append(hasAttachments).append('\n');
		sb.append("subject: ").append(subject).append('\n');
		sb.append("fragment: ").append(fragment).append('\n');
		sb.append("folder: ").append(folder).append('\n');
		sb.append("size: ").append(size).append('\n');
		sb.append("received: ").append(received).append('\n');
		return (sb.toString());
	}



	/**
	 * Sample MailItem Driver
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	}

}
