package com.zimbra.qa.selenium.framework.items;

import java.util.ArrayList;


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
	public ArrayList<ContactItem> dlist = null;

	/**
	 * Create a new contact group item+
	 */
	public ContactGroupItem(String groupName) {
		this.groupName=groupName;
		fileAs = groupName;
		type = "group";
		dlist = new ArrayList<ContactItem>();
	}

	public static ContactGroupItem generateContactItem(GenerateItemType type) throws HarnessException {

		ContactGroupItem group =  null;
		if ( type.equals(GenerateItemType.Default) || type.equals(GenerateItemType.Basic) ) {

			String groupName =  "group_" + ZimbraSeleniumProperties.getUniqueString();
			//group name with length > 20 is automatically shorten
			groupName = groupName.substring(0,20);

	        // Create a contact group
			group = new ContactGroupItem(groupName);

			group.addDListMember(ContactItem.generateContactItem(GenerateItemType.Basic));
			group.addDListMember(ContactItem.generateContactItem(GenerateItemType.Basic));
			group.addDListMember(ContactItem.generateContactItem(GenerateItemType.Basic));

		}

		if ( type.equals(GenerateItemType.AllAttributes) ) {
			throw new HarnessException("Implement me!");
		}

		return group;
	}

	/**
	 * Get the dlist member emails as a comma separated String
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
		return (sb.toString());
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

		throw new HarnessException("implement me!");
	}

	/**
	 * Create contact group item using SOAP
	 * @param app
	 * @param tagIdArray
	 * @return
	 * @throws HarnessException
	 */
	public static ContactGroupItem createUsingSOAP(AbsApplication app, String ... tagIdArray ) throws HarnessException {

	   String tagParam ="";
	   if (tagIdArray.length == 1) {
	      tagParam = " t='" + tagIdArray[0] + "'";
	   }

       // Create a contact group
 	   ContactGroupItem group = ContactGroupItem.generateContactItem(GenerateItemType.Basic);

       StringBuilder sb= new StringBuilder("");
       for (ContactItem contactItem: group.dlist) {
          String e= contactItem.email;
          sb.append("<m type='I' value='" + e + "' />");
       }

	   app.zGetActiveAccount().soapSend(
	         "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	         "<cn " + tagParam + " >" +
	         "<a n='type'>group</a>" +
	         "<a n='nickname'>" + group.groupName +"</a>" +
	         "<a n='fileAs'>8:" +  group.fileAs +"</a>" +
             sb.toString() +
             //"<a n='dlist'>" + group.getDList() + "</a>" +
	         "</cn>" +
	   "</CreateContactRequest>");

	   group.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));

	   // Refresh addressbook
       ((AppAjaxClient)app).zPageMain.zToolbarPressButton(Button.B_REFRESH);

	   return group;
	}

	/**
	 * Create local contact group item using SOAP - for ZD
	 * @param app
	 * @param accountName
	 * @param tagIdArray
	 * @return
	 * @throws HarnessException
	 */
	public static ContactGroupItem createLocalUsingSOAP(AbsApplication app, String accountName, String ... tagIdArray ) throws HarnessException {

      String tagParam ="";
      if (tagIdArray.length == 1) {
         tagParam = " t='" + tagIdArray[0] + "'";
      }

      // Create a contact group
      ContactGroupItem group = ContactGroupItem.generateContactItem(GenerateItemType.Basic);

      app.zGetActiveAccount().soapSend(
            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
            "<cn " + tagParam + " >" +
            "<a n='type'>group</a>" +
            "<a n='nickname'>" + group.groupName +"</a>" +
            "<a n='dlist'>" + group.getDList() + "</a>" +
            "<a n='fileAs'>8:" +  group.fileAs +"</a>" +
            "</cn>" +
            "</CreateContactRequest>",
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            accountName);

      return group;
   }

	public static ContactGroupItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("implement me!");
	}


	public static String getId(ZimbraAccount account) {
		return account.soapSelectValue("//mail:CreateContactResponse/mail:cn", "id");
	}

	public static String[] getDList(ZimbraAccount account) {
		String[] dlist = null; //account.so .soapSelectNodes("//mail:CreateContactResponse/mail:cn/mail:m");
		return dlist;
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


