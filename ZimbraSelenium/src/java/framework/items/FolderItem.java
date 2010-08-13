/**
 * 
 */
package framework.items;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * @author Matt Rhoades
 *
 */
public class FolderItem extends ZimbraItem implements IItem {
	
	public enum FolderView {
		Conversation, Message, Contact, Appointment, Task, Wiki, Document
	}
	
	/**
	 * The folder name
	 */
	public String name;
	
	/**
	 * The folder view
	 * 
	 * One of Conversation, Message, Contact, Appointment, Task, Wiki, Document
	 */
	public FolderView view;
	
	/**
	 * The parent folder
	 */
	public FolderItem parent;
	
	/**
	 * Create a new FolderItem object
	 */
	public FolderItem() {
		view = FolderView.Conversation;	// Default per soap.txt
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
	public void importFromSOAP(Element response) throws HarnessException {
		throw new HarnessException("implement me");
	}


	@Override
	public void importFromSOAP(ZimbraAccount account, String query)
			throws HarnessException {
		throw new HarnessException("implement me");
	}


}
