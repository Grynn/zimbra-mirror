package com.zimbra.qa.selenium.framework.items;

import java.util.*;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;

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

	// TODO: Remove dlist in favor of the new "members" list
	/**
	 * The list of contacts within this group
	 */
	public ArrayList<ContactItem> dlist = null;

	/**
	 * The list of members within this group
	 */
	public ArrayList<MemberItem> groupMembers = new ArrayList<MemberItem>();

	/**
	 * Create a new contact group item+
	 */
	public ContactGroupItem() {
		dlist = new ArrayList<ContactItem>();
	}
	
	/**
	 * Create a new contact group item+
	 */
	public ContactGroupItem(String groupName) {
		this.groupName=groupName;
		fileAs = groupName;
		type = "group";
		dlist = new ArrayList<ContactItem>();
	}

	/**
	 * Get the dlist member emails as a comma separated String
	 * @deprecated Use getMemberList() instead
	 * @return
	 */
	public String getDList() {
		StringBuilder sb = null;
		for (ContactItem contactItem : dlist) {
			String s = contactItem.email;
			if ( sb==null ) {
				sb = new StringBuilder(s);
			} else {
				sb.append(',').append(s);
			}
		}
		return (sb == null ? "" : sb.toString());
	}

	public List<MemberItem> getMemberList() {
		return(groupMembers);
	}
	
	
	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.items.ContactItem#getName()
	 */
	@Override
	public String getName() {
		return (groupName);
	}
	
	/**
	 * Add a contact item to the dlist
	 * @param ContactItem
	 * @return the current dlist members
	 */
	public ArrayList<ContactItem> addDListMember(ContactItem contactItem) {
		if ( dlist.contains(contactItem) ) {
			// Nothing to add
			return (dlist);
		}

		dlist.add(contactItem);

		return (dlist);
	}


	// no longer used
	@Deprecated()
	public ArrayList<ContactItem> addDListMember(String contactStr) {
		 return null;
	}

	/**
	 * Remove all instances of an emailaddress from the dlist
	 * @param emailaddress
	 * @return the current dlist members
	 */
	public ArrayList<ContactItem> removeDListMember(String emailaddress) {
	    for (ContactItem contactItem : dlist) {
		    if (contactItem.email.contains(emailaddress)) {
			    dlist.remove(contactItem);
		  }
	    }
		return (dlist);
	}


    // key=dlist; value="email1, email2,..."
	public String setAttribute(String key, String value) {

		// Process any special attributes here
		// FIXME:
		//if ( key.equals("dlist") ) {
		//	dlist = new ArrayList<String>(Arrays.asList(value.split(",")));
		//}
		if (!key.equals("dlist")) {
			super.setAttribute(key, value);
		}
		return (ContactAttributes.get(key));
	}



	public static ContactGroupItem importFromSOAP(Element GetContactsResponse) throws HarnessException {
		if ( GetContactsResponse == null )
			throw new HarnessException("GetContactsResponse cannot be null");

		ContactGroupItem group = null;

		try {

			// Make sure we only have the GetMsgResponse part
			Element getContactsResponse = ZimbraAccount.SoapClient.selectNode(GetContactsResponse, "//mail:GetContactsResponse");
			if ( getContactsResponse == null )
				throw new HarnessException("Element does not contain GetContactsResponse: " + GetContactsResponse.prettyPrint());

			Element cn = ZimbraAccount.SoapClient.selectNode(getContactsResponse, "//mail:cn");
			if ( cn == null )
				throw new HarnessException("Element does not contain a cn element: "+ getContactsResponse.prettyPrint());

			// Create the object
			group = new ContactGroupItem();

			// Set the ID
			group.setId(cn.getAttribute("id", null));
			group.fileAs = cn.getAttribute("fileAsStr", null);
			group.setFolderId(cn.getAttribute("l", null));


			// Iterate the attributes
			Element[] attributes = ZimbraAccount.SoapClient.selectNodes(cn, "//mail:a");
			for (Element a : attributes) {
				String key = a.getAttribute("n", "foo");
				String value = a.getText();
				
				if ( key.equalsIgnoreCase("nickname") ) {
					group.groupName = value;
				}

				group.setAttribute(key, value);
			}

			// Iterate the members
			Element[] members = ZimbraAccount.SoapClient.selectNodes(cn, "//mail:m");
			for (Element m : members) {
				String value = m.getAttribute("value", null);
				String type = m.getAttribute("type", null);
				
				if ( type.equalsIgnoreCase(MemberItemGAL.MyType) ) {
					group.groupMembers.add(new MemberItemGAL(value, type));
				} else if ( type.equalsIgnoreCase(MemberItemContact.MyType) ) {
					group.groupMembers.add(new MemberItemContact(value, type));
				} else if ( type.equalsIgnoreCase(MemberItemAddress.MyType) ) {
					group.groupMembers.add(new MemberItemAddress(value, type));
				} else {
					group.groupMembers.add(new MemberItem(value, type));
				}

			}

			return (group);

		} finally {
			if ( group != null )	logger.info(group.prettyPrint());
		}
	}


	
	/**
	 * Create a contact group with 2 email address members
	 * @param account
	 * @return
	 * @throws HarnessException
	 */
	public static ContactGroupItem createContactGroupItem(ZimbraAccount account) throws HarnessException {

		// Create a contact group
		String unique = ZimbraSeleniumProperties.getUniqueString(); // group name is max 20 chars
		String groupname = "group"+ unique.substring(unique.length() - 10);

		// Create 2 members
		String member1 = "member"+ ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
		String member2 = "member"+ ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";

		StringBuilder sb = new StringBuilder();
		sb.append("<m type='I' value='").append(member1).append("'/>");
		sb.append("<m type='I' value='").append(member2).append("'/>");

		account.soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='type'>group</a>" +
						"<a n='nickname'>" + groupname +"</a>" +
						"<a n='fileAs'>8:" +  groupname +"</a>" +
						sb.toString() +
						"</cn>" +
				"</CreateContactRequest>");
		String id = account.soapSelectValue("//mail:CreateContactResponse/mail:cn", "id");

		return (ContactGroupItem.importFromSOAP(account, "item:"+ id));

	}

	public static ContactGroupItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		return ContactGroupItem.importFromSOAP(
				account,
				query,
				SOAP_DESTINATION_HOST_TYPE.SERVER,
				null);
	}

	public static ContactGroupItem importFromSOAP(ZimbraAccount account,
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


	public static String getId(ZimbraAccount account) {
		return account.soapSelectValue("//mail:CreateContactResponse/mail:cn", "id");
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
		sb.append("members:\n");
		for (MemberItem m : groupMembers) {
			sb.append(m.toString());
		}
		for (String key : ContactAttributes.keySet())
			sb.append(String.format("Attribute: key(%s) value(%s)", key, ContactAttributes.get(key))).append('\n');
		return (sb.toString());
	}

	public static class MemberItemAddress extends MemberItem {
		public static final String MyType = "I";
		
		public MemberItemAddress(String value, String type) {
			super(value, type);
		}
		
		public MemberItemAddress(String address) {
			super(address, MyType);
		}
		
	}
	

	public static class MemberItemContact extends MemberItem {
		public static final String MyType = "C";
		
		protected String id = null;

		public MemberItemContact(String value, String type) {
			super(value, type);
			id = value;
		}
		
		public MemberItemContact(ContactItem c) {
			super(c.email, MyType);
			id = c.getId();
		}
		
		protected String getNormalized() {
			return (id);
		}
	}
	
	public static class MemberItemGroup extends MemberItem {
		public static final String MyType = "C";
		
		protected String id = null;

		public MemberItemGroup(String value, String type) {
			super(value, type);
			id = value;
		}
		
		public MemberItemGroup(ContactGroupItem g) {
			super(g.email, MyType);
			id = g.getId();
		}
		
		protected String getNormalized() {
			return (id);
		}
	}
	
	public static class MemberItemGAL extends MemberItem {
		public static final String MyType = "G";

		public MemberItemGAL(String value, String type) {
			super(value, type);
		}
		
		public MemberItemGAL(ZimbraAccount a) {
			super(a.EmailAddress, MyType);
		}
		
		protected String getNormalized() {
		
			// Member values may look like:
			// <m value="uid=address,ou=people,dc=testdomain,dc=com" type="G"/>
			// convert those to an 'email address' format
			//
			
			if ( !value.contains("uid") ) {
				// Not an LDAP format value
				return (value);
			}
			
			String email = null;
			StringBuilder domain = null;
			for (String pair : value.split(",")) {
				
				if ( !pair.contains("=") ) {
					return (value); // Error?
				}
				
				String key = pair.split("=")[0];
				String value = pair.split("=")[1];
				
				if ( key.equals("uid") ) {
					email = value;
					continue;
				}
				
				if ( key.equals("dc") ) {
					if ( domain == null ) {
						domain = new StringBuilder(value);
					} else {
						domain.append(".").append(value);
					}
				}
			}

			return (email + "@" + domain.toString());

		}
	}
	
	public static class MemberItem {
		protected String value;
		protected String type;
		
		public MemberItem() {
			
		}
		
		public MemberItem(String value, String type) {
			this.value = value;
			this.type = type;
		}
		
		public String getValue() {
			return (value);
		}
		
		public String getType() {
			return (type);
		}
		
		/**
		 * This method is used to compare two MemberItems.
		 * All classes should return a String such as "email@domain.com".
		 * @return
		 */
		protected String getNormalized() {
			return (getValue());
		}
		
		public String prettyPrint() {
			StringBuilder sb = new StringBuilder();
			sb.append("value: ").append(value).append("\n");
			sb.append("type: ").append(type).append("\n");
			return (sb.toString());
		}
		
		public String toString() {
			return (String.format("value(%s) type(%s)", value, type));
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return (getNormalized().hashCode());
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals (Object o) {
			
			if ( o == null ) {
				return (false);
			}
			
			if ( o == this ) {
				return (true);
			}
			
			if( !(o instanceof MemberItem) ) {
				return (false);
			}
			
			MemberItem other = (MemberItem) o;
			
			if ( !(other.getType().equals(getType())) ) {
				return (false);
			}
			
			/**
			Sometimes, the server returns contacts with the Account ID, such as:
			        <m value="f7042cb6-9fed-477c-ade1-20da53822ce3:257" type="C"/>
			Eventually, it would be good to verify the Account ID part, but it
			is difficult to track that information at the moment.
			
			For now, just do a String Contains using the ID.
			 */
			return (
					other.getNormalized().contains(getNormalized()) || 
					getNormalized().contains((other.getNormalized())));

		}
		

	}
}


