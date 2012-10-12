package com.zimbra.qa.selenium.framework.items;

import java.util.HashMap;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;

/**
 * Used to define a Zimbra Contact
 *
 * @author Matt Rhoades
 *
 */
public class ContactItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	public static final String IMAGE_CLASS       = "ImgContact";
	public static final String GAL_IMAGE_CLASS   = "ImgGALContact";

	public String fileAs = null;
	public String type = null;
	public String firstName = null;
	public String lastName = null;
	public String email = null;
//	public String middleName = null;
//	public String homePostalCode = null;
//	public String nameSuffix = null;
//	public String birthday = null;
//	public String homeStreet = null;
//	public String nickname = null;
//	public String department = null;
//	public String homeCountry = null;
//	public String homeCity = null;
//	public String company = null;
//	public String homeState = null;
//	public String notes = null;
//	public String jobTitle = null;
//	public String maidenName = null;
//	public String imAddress1 = null;
//	public String mobilePhone = null;
//	public String namePrefix = null;
//	public String homeURL = null;

	public HashMap<String, String> ContactAttributes = new HashMap<String, String>();

	public FolderItem AddressBook = null;

	/**
	 * The GUI displayed email
	 */
	public String gEmail;

	/**
	 * Whether the contact is checked or not in the contact list view
	 */
	public boolean gListIsChecked;

	/**
	 * The List contact icon
	 */
	public String gListContactIcon;

	/**
	 *  The List display name
	 */
	public String gListFileAsDisplay;


	public ContactItem() {
	}


	public ContactItem(String fileAs) {
		this.fileAs=fileAs;
	}

	@Override
	public String getName() {
		return (fileAs);
	}


	// TODO: eventually, replace this with the com.zimbra.soap.types.Contact method
	private String myId;
	public String getId() {
		return (myId);
	}
	public void setId(String id) {
		myId=id;
	}

	private String myFolderId;
	public void setFolderId(String id) {
		this.myFolderId = id;
	}

	public String getFolderId() {
		return (this.myFolderId);
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
		else if ( key.equals("firstName"))
			firstName = value;
		else if ( key.equals("lastName"))
			lastName = value;
		else if ( key.equals("fileAs"))
			fileAs = value;

		// add to the map
		ContactAttributes.put(key, value);
		
		return value;
	}

	/**
	 * Get the CN for this contact (cn@domain.com)
	 * @return the CN, or null if email is not set
	 */
	public String getCN() {
		String address = null;

		// Determine the current contact email address
		// either from the "email" property or from
		// the ContactAttributes
		//
		if ( email != null ) {
			address = email;
		} else if ( getAttribute("email", null) != null) {
			address = getAttribute("email", null);
		} else {
			return (null); // No email set
		}

		// If the email contains an '@', use the first part
		//
		if ( address.contains("@")) {
			return (address.split("@")[0]);
		}

		// If the email does not contain the '@', return the entire part
		return (address);
	}



	
	/**
	 * Create a basic contact in the account's default addressbook (Contacts)
	 * @param account
	 * @param Type
	 * @return
	 * @throws HarnessException
	 */
	public static ContactItem createContactItem(ZimbraAccount account) throws HarnessException {
		
		// Create a contact item
		String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();
		String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
		String email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
		String company = "company" + ZimbraSeleniumProperties.getUniqueString();

		StringBuilder attrs = new StringBuilder();
		
		attrs.append("<a n='firstName'>").append(firstName).append("</a>");
		attrs.append("<a n='lastName'>").append(lastName).append("</a>");
		attrs.append("<a n='email'>").append(email).append("</a>");
		attrs.append("<a n='company'>").append(company).append("</a>");
		
				
		account.soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							attrs.toString() +
						"</cn>" +
				"</CreateContactRequest>");
		String id = account.soapSelectValue("//mail:cn", "id");
		
		return (ContactItem.importFromSOAP(account, "item:"+ id));

	}

	public static ContactItem importFromSOAP(Element GetContactsResponse) throws HarnessException {
		if ( GetContactsResponse == null )
			throw new HarnessException("Element cannot be null");

		ContactItem contact = null;

		try {

			// Make sure we only have the GetMsgResponse part
			Element getContactsResponse = ZimbraAccount.SoapClient.selectNode(GetContactsResponse, "//mail:GetContactsResponse");
			if ( getContactsResponse == null )
				throw new HarnessException("Element does not contain GetContactsResponse: " + GetContactsResponse.prettyPrint());

			Element cn = ZimbraAccount.SoapClient.selectNode(getContactsResponse, "//mail:cn");
			if ( cn == null )
				throw new HarnessException("Element does not contain a cn element: "+ getContactsResponse.prettyPrint());

			// Create the object
			contact = new ContactItem();

			// Set the ID
			contact.setId(cn.getAttribute("id", null));
			contact.fileAs=cn.getAttribute("fileAsStr",null);
			contact.setFolderId(cn.getAttribute("l", null));


			// Iterate the attributes
			Element[] attributes = ZimbraAccount.SoapClient.selectNodes(cn, "//mail:a");
			for (Element a : attributes) {
				String key = a.getAttribute("n", "foo");
				String value = a.getText();
				contact.setAttribute(key, value);
			}

			return (contact);

		} finally {
			if ( contact != null )	logger.info(contact.prettyPrint());
		}

	}

	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static ContactItem importFromSOAP(ZimbraAccount account, String query)
			throws HarnessException {
		return importFromSOAP(
				account,
				query,
				SOAP_DESTINATION_HOST_TYPE.SERVER,
				null);
	}

	public static ContactItem importFromSOAP(ZimbraAccount account,
			String query, SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException {

		try
		{

			account.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='contact'>" +
							"<query>"+ query +"</query>" +
							"</SearchRequest>",
							destType,
							accountName);

			Element[] results = account.soapSelectNodes("//mail:SearchResponse/mail:cn");
			if (results.length == 0) {
				return null;
			} else if (results.length != 1) {
				throw new HarnessException("Query should return 1 result, not "+ results.length);
			}

			String id = account.soapSelectValue("//mail:SearchResponse/mail:cn", "id");

			account.soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail' >" +
							"<cn id='"+ id +"'/>" +
							"</GetContactsRequest>",
							destType,
							accountName);
			Element getContactsResponse = account.soapSelectNode("//mail:GetContactsResponse", 1);

			// Using the response, create this item
			return (importFromSOAP(getContactsResponse));

		} catch (Exception e) {
			throw new HarnessException("Unable to import using SOAP query("+ query +") and account("+ account.EmailAddress +")", e);
		}

	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(ContactItem.class.getSimpleName()).append('\n');
		sb.append("Email: ").append(getAttribute("email", "")).append('\n');
		sb.append(String.format("Name: first(%s) middle(%s) last(%s)\n", firstName, getAttribute("middleName", ""), lastName)).append('\n');
		for (String key : ContactAttributes.keySet())
			sb.append(String.format("Attribute: key(%s) value(%s)", key, ContactAttributes.get(key))).append('\n');
		return (sb.toString());
	}



}


