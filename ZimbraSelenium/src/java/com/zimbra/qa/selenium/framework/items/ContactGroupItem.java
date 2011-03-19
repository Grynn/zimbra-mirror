package com.zimbra.qa.selenium.framework.items;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;


/**
 * The <code>ContactGroupItem</code> defines a Zimbra Contact Group
 */
public class ContactGroupItem extends ContactItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

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

	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
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


