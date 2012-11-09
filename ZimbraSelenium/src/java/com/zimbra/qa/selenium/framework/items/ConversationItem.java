/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import java.util.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;


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
		
	////
	// START: SOAP Data
	////
	
	/**
	 * A list of messages in the conversation
	 */
	protected List<MailItem> dMessageList = new ArrayList<MailItem>();
	
	////
	// FINISH: SOAP Data
	////
	
	/**
	 * Create a mail item
	 */
	public ConversationItem() {
	}

	@Override
	public String getName() {
		return (getSubject());
	}
	
	public String getSubject() {
		return (gSubject);
	}
	
	/**
	 * Return a list of messages contained in this conversation
	 * @return
	 */
	public List<MailItem> getMessageList() {
		return (dMessageList);
	}
	
	/**
	 * Create a basic conversation in the account's mailbox with 3 messages
	 * @param account
	 * @return
	 * @throws HarnessException 
	 */
	public static ConversationItem createConversationItem(ZimbraAccount account) throws HarnessException {
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		String body1 = "body" + ZimbraSeleniumProperties.getUniqueString();
		String body2 = "reply" + ZimbraSeleniumProperties.getUniqueString();
		String body3 = "forward" + ZimbraSeleniumProperties.getUniqueString();
		
		// Send a message to the test account and AccountB
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ account.EmailAddress +"'/>" +
						"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ body1 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		// AccountB replies to the message.
		MailItem bMessage = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "in:inbox subject:("+ subject +")");
		ZimbraAccount.AccountB().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ bMessage.getId() +"' rt='r'>" +
						"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
						"<e t='c' a='"+ account.EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ body2 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		// AccountB forwards the message to test account.
		ZimbraAccount.AccountB().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m origid='"+ bMessage.getId() +"' rt='w'>" +
						"<e t='t' a='"+ account.EmailAddress +"'/>" +
						"<su>FWD: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ body3 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		ConversationItem c = new ConversationItem();
		c.gSubject = subject;
		c.dMessageList.add(MailItem.importFromSOAP(account, "content:("+ body1 +")"));
		c.dMessageList.add(MailItem.importFromSOAP(account, "content:("+ body2 +")"));
		c.dMessageList.add(MailItem.importFromSOAP(account, "content:("+ body3 +")"));
		
		return (c);

	}

	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {		
		throw new HarnessException("implement me");
	}

	public static ConversationItem importFromSOAP(ZimbraAccount account, Element GetConvResponse) throws HarnessException {
		if ( GetConvResponse == null )
			throw new HarnessException("Element cannot be null");

		ConversationItem conversation = null;
		
		try {

			// Make sure we only have the GetMsgResponse part
			Element getConvResponse = ZimbraAccount.SoapClient.selectNode(GetConvResponse, "//mail:GetConvResponse");
			if ( getConvResponse == null )
				throw new HarnessException("Element does not contain GetConvResponse");
	
			Element c = ZimbraAccount.SoapClient.selectNode(getConvResponse, "//mail:c");
			if ( c == null )
				throw new HarnessException("Element does not contain an c element");
			
			// Create the object
			conversation = new ConversationItem();
			
			// Set the ID
			conversation.setId(c.getAttribute("id", null));
			
			// If there is a subject, save it
			Element sElement = ZimbraAccount.SoapClient.selectNode(c, "//mail:su");
			if ( sElement != null )
				conversation.gSubject = sElement.getText().trim();
			
			// Parse the conversation messages
			Element[] mElements = ZimbraAccount.SoapClient.selectNodes(c, "//mail:m");
			for (Element m : mElements) {
				
				String id = m.getAttribute("id", null);
				
				// Add each message to the conversation item
				account.soapSend(
						"<GetMsgRequest xmlns='urn:zimbraMail'>" +
							"<m id='"+ id +"' />" +
						"</GetMsgRequest>");
				Element getMsgResponse = account.soapSelectNode("//mail:GetMsgResponse", 1);

				MailItem message = MailItem.importFromSOAP(getMsgResponse);
				conversation.getMessageList().add(message);
				
			} 
			
			return (conversation);

		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ GetConvResponse.prettyPrint(), e);
		} finally {
			if ( conversation != null )	logger.info(conversation.prettyPrint());
		}
	}

	public static ConversationItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {

		account.soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='conversation'>" +
				"<query>"+ query +"</query>" +
		"</SearchRequest>");

		Element[] results = account.soapSelectNodes("//mail:SearchResponse/mail:c");
		if (results.length == 0) {
			return null;
		}
		if (results.length != 1) {
			throw new HarnessException("Search result should return 1 converation, not "+ results.length);
		}

		String id = account.soapSelectValue("//mail:SearchResponse/mail:c", "id");

		account.soapSend(
				"<GetConvRequest xmlns='urn:zimbraMail'>" +
					"<c id='"+ id +"'/>" +
				"</GetConvRequest>");

		Element getConvRequest = account.soapSelectNode("//mail:GetConvResponse", 1);

		// Using the response, create this item
		return (importFromSOAP(account, getConvRequest));

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
