/**
 * 
 */
package framework.items;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * This class represents a mail message
 * 
 * @author Matt Rhoades
 *
 */
public class ConversationItem extends ZimbraItem implements IItem {

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
	
		
	
	/**
	 * Create a mail item
	 */
	public ConversationItem() {
	}

	
	/* (non-Javadoc)
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/* (non-Javadoc)
	 * @see framework.items.IItem#ImportSOAP(com.zimbra.common.soap.Element)
	 */
	@Override
	public void importFromSOAP(Element GetMsgResponse) throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public void importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.prettyPrint());
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
