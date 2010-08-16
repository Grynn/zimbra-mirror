package framework.items;

import java.util.ArrayList;
import java.util.Arrays;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * Used to define a Zimbra Contact
 * 
 * @author Matt Rhoades
 *
 */
public class ContactGroupItem extends ContactItem implements IItem {

	public String nickname = null;
	public ArrayList<String> dlist = null;
	public String fileas = null;
	
	public ContactGroupItem() {
		type = "group";
		dlist = new ArrayList<String>();
	}
	
	/**
	 * Get the dlist attribute as a comma separated String
	 * @return
	 */
	public String getDList() {
		StringBuilder sb = null;
		for (String s : dlist) {
			if ( sb==null ) {
				sb = new StringBuilder(s);
			} else {
				sb.append(',').append(s);
			}
		}
		return (sb.toString());
	}
	
	/**
	 * Add an email address to the dlist
	 * @param emailaddress
	 * @return the current dlist members
	 */
	public ArrayList<String> addDListMember(String emailaddress) {
		if ( dlist.contains(emailaddress) ) {
			// Nothing to add
			return (dlist);
		}
		
		dlist.add(emailaddress);
		
		return (dlist);
	}
	
	/**
	 * Remove all instances of an emailaddress from the dlist
	 * @param emailaddress
	 * @return the current dlist members
	 */
	public ArrayList<String> removeDListMember(String emailaddress) {
		while (dlist.contains(emailaddress)) {
			dlist.remove(emailaddress);
		}
		return (dlist);
	}
	
	

	public String setAttribute(String key, String value) {
		
		// Process any special attributes here
		if ( key.equals("dlist") )
			dlist = new ArrayList<String>(Arrays.asList(value.split(",")));

		super.setAttribute(key, value);
		
		return (ContactAttributes.get(key));
	}
	
	
	@Override
	public void importFromSOAP(Element GetContactsResponse) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public void importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public String printItem() {
		StringBuilder sb = new StringBuilder();
		sb.append(ContactGroupItem.class.getSimpleName()).append('\n');
		sb.append("ID: ").append(id).append('\n');
		sb.append("Name: ").append(nickname).append('\n');
		sb.append("dlist: ").append(getDList()).append('\n');
		for (String key : ContactAttributes.keySet())
			sb.append(String.format("Attribute: key(%s) value(%s)", key, ContactAttributes.get(key))).append('\n');
		return (sb.toString());
	}
	
	/**
	 * Sample ContactItem Driver
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		String envelopeString = 
			"<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'>" +
				"<soap:Header>" +
					"<context xmlns='urn:zimbra'>" +
						"<change token='2'></change>" +
					"</context>" +
				"</soap:Header>" +
				"<soap:Body>" +
					"<GetContactsResponse xmlns='urn:zimbraMail'>" +
						"<cn d='1281656466000' fileAsStr='Last.1281656613238.17, First.1281656613238.16' id='425' l='7' rev='2'>" +
							"<a n='lastName'>Last.1281656613238.17</a>" +
							"<a n='email'>email.1281656613238.18@domain.com</a>" +
							"<a n='firstName'>First.1281656613238.16</a>" +
						"</cn>" +
					"</GetContactsResponse>" +
				"</soap:Body>" +
			"</soap:Envelope>";
		

		
		ContactGroupItem c = new ContactGroupItem();
		c.importFromSOAP(Element.parseXML(envelopeString));
		
		System.out.println("Imported contact item from SOAP");
		System.out.println(c.printItem());
		
		ZimbraAccount.AccountA().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
					"<cn>" +
						"<a n='firstName'>First.1281656613301.19</a>" +
						"<a n='lastName'>Last.1281656613301.20</a>" +
						"<a n='email'>email.1281656613301.21@domain.com</a>" +
					"</cn>" +
				"</CreateContactRequest>");
		
		c = new ContactGroupItem();
		c.importFromSOAP(ZimbraAccount.AccountA(), "email.1281656613301.21@domain.com");
		
		System.out.println("Imported contact item from query");
		System.out.println(c.printItem());

		
	}

}


