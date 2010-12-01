package framework.items;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

public class RecipientItem extends ZimbraItem implements IItem {

	public enum RecipientType {
		To, Cc, Bcc, From
	}
	public String dEmailAddress;
	public String dDisplayName;
	public RecipientType dType;
	
	public RecipientItem() {
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

	/**
	 * Parse <e d="dislay" t="f" a="address@domain.com"/>
	 **/
	@Override
	public void importFromSOAP(Element eElement) throws HarnessException {
		try {

	
			Element element = ZimbraAccount.SoapClient.selectNode(eElement, "//mail:e");
			if ( element == null )
				throw new HarnessException("Element does not contain an e element");
			
			// Set the ID
			super.id = null;
			
			String type = element.getAttribute("t", null);
			if ( type.equals("t") )
				dType = RecipientType.To;
			else if ( type.equals("c") )
				dType = RecipientType.Cc;
			else if ( type.equals("b") )
				dType = RecipientType.Bcc;
			else if ( type.equals("f") )
				dType = RecipientType.From;
			else
				throw new HarnessException("Unkown <e t='?'/> attribute: "+ type);
			

			dDisplayName = element.getAttribute("d", null);
			
			dEmailAddress = element.getAttribute("a", null);

			
		} catch (Exception e) {
			throw new HarnessException("Could not parse </e>: "+ eElement.prettyPrint(), e);
		} finally {
			logger.info(this.prettyPrint());
		}
	}

	@Override
	public void importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("not supported");
	}

}
