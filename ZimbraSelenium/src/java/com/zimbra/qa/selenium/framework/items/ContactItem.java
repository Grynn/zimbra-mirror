package com.zimbra.qa.selenium.framework.items;

import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew;

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
	public String middleName = null;
	public String lastName = null;
	public String email = null;
	public String homePostalCode = null;
	public String nameSuffix = null;
	public String birthday = null;
	public String homeStreet = null;
	public String nickname = null;
	public String department = null;
	public String homeCountry = null;
	public String homeCity = null;
	public String company = null;
	public String homeState = null;
	public String notes = null;
	public String jobTitle = null;
	public String maidenName = null;
	public String imAddress1 = null;
	public String mobilePhone = null;
	public String namePrefix = null;
	public String homeURL = null;

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
		else if ( key.equals("middleName"))
			middleName = value;
		else if ( key.equals("lastName"))
			lastName = value;
		else if ( key.equals("homePostalCode"))
		   homePostalCode = value;
		else if ( key.equals("nameSuffix"))
		   nameSuffix = value;
		else if ( key.equals("birthday"))
		   birthday = value;
		else if ( key.equals("homeStreet"))
		   homeStreet = value;
		else if ( key.equals("nickname"))
		   nickname = value;
		else if ( key.equals("department"))
		   department = value;
		else if ( key.equals("homeCountry"))
		   homeCountry = value;
		else if ( key.equals("homeCity"))
		   homeCity = value;
		else if ( key.equals("company"))
		   company = value;
		else if ( key.equals("homeState"))
		   homeState = value;
		else if ( key.equals("notes"))
		   notes = value;
		else if ( key.equals("jobTitle"))
		   jobTitle = value;
		else if ( key.equals("imAddress1"))
		   imAddress1 = value;
		else if ( key.equals("namePrefix"))
		   namePrefix = value;
		else if ( key.equals("mobilePhone"))
		   mobilePhone = value;
		else if ( key.equals("maidenName"))
		   maidenName = value;
		else if ( key.equals("homeURL"))
		   homeURL = value;
		else {
		    // add to the map
		    ContactAttributes.put(key, value);
		}
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

	public static ContactItem createUsingSOAP(AbsApplication app, String ... tagIdArray ) throws HarnessException {

		String tagParam ="";
		if (tagIdArray.length == 1) {
			tagParam = " t='" + tagIdArray[0] + "'";
		}

        // Create a contact item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                "<cn " + tagParam + " >" +
	                "<a n='firstName'>" + contactItem.firstName +"</a>" +
	                "<a n='lastName'>" + contactItem.lastName +"</a>" +
	                "<a n='email'>" + contactItem.email + "</a>" +
	                "<a n='company'>" + contactItem.company + "</a>" +
	                "</cn>" +
	                "</CreateContactRequest>");

		contactItem.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));

		// Refresh addressbook
    	((AppAjaxClient)app).zPageMain.zToolbarPressButton(Button.B_REFRESH);

        return contactItem;
    }

	public enum GenerateItemType {
		Default, Basic, AllAttributes
	}

	/**
	 * Create a ContactItem with basic properties
	 *
	 * For type = Default or Basic, create a contact with first, middle, last and email set
	 * For type = AllAttributes, create a contact with all attributes set
	 *
	 * @param type The type of ContactItem to create
	 * @return the new ContactItem
	 * @throws HarnessException
	 */
	public static ContactItem generateContactItem(GenerateItemType type) throws HarnessException {
		ContactItem c = new ContactItem();
		c.firstName = "first" + ZimbraSeleniumProperties.getUniqueString();
		c.lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
	    c.email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
		//default value for file as is  last , first
		c.fileAs = c.lastName + ", " + c.firstName;
		c.company = "company" + ZimbraSeleniumProperties.getUniqueString();

		if ( type.equals(GenerateItemType.Default) || type.equals(GenerateItemType.Basic) ) {
			return (c);
		}
		else if ( type.equals(GenerateItemType.AllAttributes) ) {
			c.middleName = "middle" + ZimbraSeleniumProperties.getUniqueString();
			c.homePostalCode = "95123";
			c.nameSuffix = "Su";
			c.nickname = "Nick";
			c.department = "dep" + ZimbraSeleniumProperties.getUniqueString();
			c.jobTitle = "jobTitle" + ZimbraSeleniumProperties.getUniqueString();
			c.company = "company" + ZimbraSeleniumProperties.getUniqueString();
			c.homeStreet = "street " + ZimbraSeleniumProperties.getUniqueString();
			c.homeCity = "city " + ZimbraSeleniumProperties.getUniqueString();
			c.homeState = "CA";
			c.homeCountry = "USA";
			c.birthday = "1985-05-24";
			c.notes = "notes " + ZimbraSeleniumProperties.getUniqueString();
			c.maidenName = "M";
			c.namePrefix = "p";
			c.mobilePhone = ZimbraSeleniumProperties.getUniqueString();
			c.imAddress1 = ZimbraSeleniumProperties.getUniqueString();
			c.homeURL = "http://www.zimbra.com";

			c.ContactAttributes.put(FormContactNew.Locators.zPrefixEditField, c.namePrefix);
			c.ContactAttributes.put(FormContactNew.Locators.zMaidenEditField, c.maidenName);
			c.ContactAttributes.put(FormContactNew.Locators.zSuffixEditField, c.nameSuffix);
			c.ContactAttributes.put(FormContactNew.Locators.zNicknameEditField, c.nickname);
			c.ContactAttributes.put(FormContactNew.Locators.zDepartmentEditField, c.department);
			c.ContactAttributes.put(FormContactNew.Locators.zJobTitleEditField, c.jobTitle);
			c.ContactAttributes.put(FormContactNew.Locators.zCompanyEditField, c.company);
			c.ContactAttributes.put(FormContactNew.Locators.zPhone1EditField, c.mobilePhone);
			c.ContactAttributes.put(FormContactNew.Locators.zIM1EditField,  c.imAddress1);
			c.ContactAttributes.put(FormContactNew.Locators.zStreet1TextArea, c.homeStreet);
			c.ContactAttributes.put(FormContactNew.Locators.zCity1EditField, c.homeCity);
			c.ContactAttributes.put(FormContactNew.Locators.zState1EditField, c.homeState);
			c.ContactAttributes.put(FormContactNew.Locators.zPostalCode1EditField, c.homePostalCode);
			c.ContactAttributes.put(FormContactNew.Locators.zCountry1EditField, c.homeCountry);
			c.ContactAttributes.put(FormContactNew.Locators.zURL1EditField, c.homeURL);
			c.ContactAttributes.put(FormContactNew.Locators.zOther1EditField, c.birthday);
			c.ContactAttributes.put(FormContactNew.Locators.zNotesEditField,  c.notes);

			return c;
		}

		// Default:
		// Return empty Item
		return (new ContactItem());
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
			if (results.length != 1)
				throw new HarnessException("Query should return 1 result, not "+ results.length);

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
		sb.append("Email: ").append(email).append('\n');
		sb.append(String.format("Name: first(%s) middle(%s) last(%s)\n", firstName, middleName, lastName)).append('\n');
		for (String key : ContactAttributes.keySet())
			sb.append(String.format("Attribute: key(%s) value(%s)", key, ContactAttributes.get(key))).append('\n');
		return (sb.toString());
	}



}


