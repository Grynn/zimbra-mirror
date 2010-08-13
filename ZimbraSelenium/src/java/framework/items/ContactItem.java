package framework.items;

import java.util.HashMap;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * Used to define a Zimbra Contact
 * 
 * @author Matt Rhoades
 *
 */
public class ContactItem extends ZimbraItem implements IItem {

	public String firstName = null;
	public String middleName = null;
	public String lastName = null;
	public String email = null;
	public HashMap<String, String> ContactAttributes = new HashMap<String, String>();
	
	public FolderItem AddressBook = null;
	
	public ContactItem() {
	}

	public String getAttribute(String key, String defaultValue) {
		if ( !ContactAttributes.containsKey(key) )
			return (defaultValue);
		return (ContactAttributes.get(key));
	}
	
	public String getAttribute(String key) {
		return (getAttribute(key, null));
	}
	
	public String setAttribute(String key, String value) {
		
		// Process any special attributes here
		if ( key.equals("email") )
			email = value;
		if ( key.equals("firstName"))
			firstName = value;
		if ( key.equals("middleName"))
			middleName = value;
		if ( key.equals("lastName"))
			lastName = value;
		
		// Set the map
		ContactAttributes.put(key, value);
		
		return (ContactAttributes.get(key));
	}
	
	@Override
	public void importFromSOAP(Element GetContactsResponse) throws HarnessException {
		
		// Make sure we only have the GetMsgResponse part
		Element getContactsResponse = ZimbraAccount.SoapClient.selectNode(GetContactsResponse, "//mail:GetContactsResponse");
		if ( getContactsResponse == null )
			throw new HarnessException("Element does not contain GetContactsResponse: " + GetContactsResponse.prettyPrint());

		Element cn = ZimbraAccount.SoapClient.selectNode(getContactsResponse, "//mail:cn");
		if ( cn == null )
			throw new HarnessException("Element does not contain a cn element: "+ getContactsResponse.prettyPrint());
		
		// Set the ID
		super.id = cn.getAttribute("id", null);
		
		// Iterate the attributes
		Element[] attributes = ZimbraAccount.SoapClient.selectNodes(cn, "//mail:a");
		for (Element a : attributes) {
			String key = a.getAttribute("n", "foo");
			String value = a.getText();
			this.setAttribute(key, value);
		}

	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public void importFromSOAP(ZimbraAccount account, String query) throws HarnessException {

		try
		{

			account.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='contact'>" +
						"<query>"+ query +"</query>" +
					"</SearchRequest>");
			
			Element[] results = account.soapSelectNodes("//mail:SearchResponse/mail:cn");
			if (results.length != 1)
				throw new HarnessException("Query should return 1 result, not "+ results.length);
	
			String id = account.soapSelectValue("//mail:SearchResponse/mail:cn", "id");
			
			account.soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ id +"'/>" +
					"</GetContactsRequest>");
			Element getContactsResponse = account.soapSelectNode("//mail:GetContactsResponse", 1);
			
			// Using the response, create this item
			importFromSOAP(getContactsResponse);
			
		} catch (Exception e) {
			throw new HarnessException("Unable to import using SOAP query("+ query +") and account("+ account.EmailAddress +")", e);
		}
			
	}

	@Override
	public String printItem() {
		StringBuilder sb = new StringBuilder();
		sb.append(ContactItem.class.getSimpleName()).append('\n');
		sb.append("ID: ").append(id).append('\n');
		sb.append("Email: ").append(email).append('\n');
		sb.append(String.format("Name: first(%s) middle(%s) last(%s)\n", firstName, middleName, lastName)).append('\n');
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
		

		
		ContactItem c = new ContactItem();
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
		
		c = new ContactItem();
		c.importFromSOAP(ZimbraAccount.AccountA(), "email.1281656613301.21@domain.com");
		
		System.out.println("Imported contact item from query");
		System.out.println(c.printItem());

		
	}

}


