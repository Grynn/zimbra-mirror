/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;


/**
 * This class represents a mail message
 * 
 * @author Matt Rhoades
 *
 */
public class ConversationItem extends MailItem {


	////
	// START: GUI Data
	////

	/**
	 * Whether the conversation is expanded or not
	 */
	public boolean isExpanded = false;
	
	/**
	 * Whether this object is a mail that is part of an expanded conversation
	 */
	public boolean gIsConvExpanded = false;
	

	////
	// FINISH: GUI Data
	////
		
	
	/**
	 * Create a mail item
	 */
	public ConversationItem() {
	}

	@Override
	public String getName() {
		return (gSubject);
	}
	
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static ConversationItem importFromSOAP(Element GetMsgResponse) throws HarnessException {
		if ( GetMsgResponse == null )
			throw new HarnessException("Element cannot be null");

		throw new HarnessException("implement me");
	}

	public static ConversationItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(ConversationItem.class.getSimpleName()).append('\n');
		sb.append("isExpanded: ").append(isExpanded).append('\n');
		sb.append(super.prettyPrint());
		return (sb.toString());
	}




}
