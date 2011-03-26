package com.zimbra.qa.selenium.framework.items;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;


/**
 * The <code>ContactGroupItem</code> defines a Zimbra Contact Group
 */
public class ContactGroupItem extends ContactItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	public static final String IMAGE_CLASS   = "ImgGroup";

	/**
	 * The name of the contact group
	 */
	public String groupName = null;
	
	/**
	 * The list of contacts within this group
	 */
	public ArrayList<String> dlist = null;
	
	/**
	 * Create a new contact group item+ 
	 */
	public ContactGroupItem(String groupName) {
		this.groupName=groupName;
		fileAs = groupName;
		type = "group";
		dlist = new ArrayList<String>();
	}
	
	public static ContactGroupItem generateContactItem(GenerateItemType type) throws HarnessException {
	
		ContactGroupItem group =  null;
		if ( type.equals(GenerateItemType.Default) || type.equals(GenerateItemType.Basic) ) {

			String domain = "@zimbra.com";
			String groupName =  "group_" + ZimbraSeleniumProperties.getUniqueString();
	        String emailAddress1 = "email_" + ZimbraSeleniumProperties.getUniqueString() + domain;
	        String emailAddress2 = "email_" +  ZimbraSeleniumProperties.getUniqueString() + domain;
	        String emailAddress3 = "email_" +  ZimbraSeleniumProperties.getUniqueString() + domain;
	        
	        // Create a contact group 
			group = new ContactGroupItem(groupName);
		    
			group.addDListMember(emailAddress1);
			group.addDListMember(emailAddress2);
			group.addDListMember(emailAddress3);
		
		}
		
		if ( type.equals(GenerateItemType.AllAttributes) ) {
			throw new HarnessException("Implement me!");
		}
		
		return group;
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
	
	
	
	public static ContactGroupItem importFromSOAP(Element GetContactsResponse) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public static ContactGroupItem createUsingSOAP(AppAjaxClient app, String ... tagIdArray ) throws HarnessException {
		
			String tagParam ="";
			if (tagIdArray.length == 1) {
				tagParam = " t='" + tagIdArray[0] + "'";
			}

	        // Create a contact group 
			ContactGroupItem group = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
		
	        app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                "<cn " + tagParam + " fileAsStr='" + group.groupName + "' >" +
	                "<a n='type'>group</a>" +
	                "<a n='nickname'>" + group.groupName +"</a>" +
	                "<a n='dlist'>" + group.getDList() + "</a>" +
	                "</cn>" +
	                "</CreateContactRequest>");

	        return group;
	}
	

	public static ContactGroupItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.prettyPrint());
		sb.append(ContactGroupItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(groupName).append('\n');
		sb.append("dlist: ").append(getDList()).append('\n');
		for (String key : ContactAttributes.keySet())
			sb.append(String.format("Attribute: key(%s) value(%s)", key, ContactAttributes.get(key))).append('\n');
		return (sb.toString());
	}
	

}


