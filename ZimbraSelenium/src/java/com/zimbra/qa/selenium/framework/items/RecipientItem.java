package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;


public class RecipientItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	public enum RecipientType {
		To, Cc, Bcc, From, Sender, ReplyTo, RedirectedFrom, ReadReceipt
	}
	public String dEmailAddress;
	public String dDisplayName;
	public RecipientType dType;
	
	public RecipientItem() {
	}
	
	public String getName() {
		return (dEmailAddress);
	}
	
	public RecipientItem(ZimbraAccount account) {
		this(account.EmailAddress);
	}
	
	public RecipientItem(ZimbraAccount account, RecipientType type) {
		this(account.EmailAddress, type);
	}
	
	public RecipientItem(String email) {
		this(email, RecipientType.To);
	}
	
	public RecipientItem(String email, RecipientType type) {
		this.dEmailAddress = email;
		this.dType = type;
	}
	
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(RecipientItem.class.getSimpleName()).append('\t');
		sb.append("Email: ").append(dEmailAddress).append('\t');
		sb.append("Name: ").append(dDisplayName).append('\t');
		sb.append("Type: ").append(dType).append('\n');
		return (sb.toString());
	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("not supported");
		
	}

	public static RecipientItem importFromSOAP(Element eElement) throws HarnessException {
		if ( eElement == null )
			throw new HarnessException("Element cannot be null");

		RecipientItem recipient = null;
		
		try {

	
			Element element = ZimbraAccount.SoapClient.selectNode(eElement, "//mail:e");
			if ( element == null )
				throw new HarnessException("Element does not contain an e element");
			
			recipient = new RecipientItem();
						
			String type = element.getAttribute("t", null);
			if ( type.equals("t") )
				recipient.dType = RecipientType.To;
			else if ( type.equals("c") )
				recipient.dType = RecipientType.Cc;
			else if ( type.equals("b") )
				recipient.dType = RecipientType.Bcc;
			else if ( type.equals("f") )
				recipient.dType = RecipientType.From;
			else if ( type.equals("s") )
				recipient.dType = RecipientType.Sender;
			else if ( type.equals("r") )
				recipient.dType = RecipientType.ReplyTo;
			else if ( type.equals("n") )
				recipient.dType = RecipientType.ReadReceipt;
			else if ( type.equals("rf") )
				recipient.dType = RecipientType.RedirectedFrom;
			else
				throw new HarnessException("Unkown <e t='?'/> attribute: "+ type);
			

			recipient.dDisplayName = element.getAttribute("d", null);
			
			recipient.dEmailAddress = element.getAttribute("a", null);
			
			return (recipient);

			
		} catch (Exception e) {
			throw new HarnessException("Could not parse </e>: "+ eElement.prettyPrint(), e);
		} finally {
			if ( recipient != null ) logger.info(recipient.prettyPrint());
		}
	}

	public static RecipientItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("not supported");
	}

}
