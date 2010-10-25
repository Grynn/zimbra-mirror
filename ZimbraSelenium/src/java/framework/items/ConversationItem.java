/**
 * 
 */
package framework.items;

import java.util.ArrayList;

import com.zimbra.common.soap.Element;

import framework.items.RecipientItem.RecipientType;
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
	 * The subject for this mail
	 */
	public String subject;
			
	/**
	 * The read/unread status of this mail
	 */
	public boolean read;
	
	/**
	 * The flags associated with this mail (see soap.txt for details)
	 */
	public int flags;
	
	
	/**
	 * The folder that contains this mail
	 */
	public FolderItem folder;
	
	
	/**
	 * Create a mail item
	 */
	public ConversationItem() {
		flags = MailItem.MessageFlags.None;	// Clear all flags
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
	public String printItem() {
		StringBuilder sb = new StringBuilder();
		sb.append(ConversationItem.class.getSimpleName()).append('\n');
		sb.append("ID: ").append(id).append('\n');
		sb.append("Subject: ").append(subject).append('\n');
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
