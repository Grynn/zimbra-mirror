package com.zimbra.qa.selenium.framework.items;

import java.util.ArrayList;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;

/**
 * The <code>DistributionListItem</code> defines a Zimbra dl item
 */
public class DistributionListItem extends ContactItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	public static final String IMAGE_CLASS   = "ImgDistributionList";

	/**
	 * The name of the list
	 */
	private String email = null;
	
	private ZimbraAccount owner=null;
	
	/**
	 * The list of emails belong to this list
	 */
	private ArrayList<String> emailList = null;

	/**
	 * Create a new dlist item 
	 */
	public DistributionListItem(String displayName) {
		fileAs = displayName;
		type = "dlist";
		emailList = new ArrayList<String>();
	}

	/**
	 * Create a new dlist item+ 
	 */
	public DistributionListItem(String email,String displayName) {
		this.email=email;
		fileAs = displayName;
		type = "dlist";
		emailList = new ArrayList<String>();
	}
	
	public static DistributionListItem generateDListItem(GenerateItemType type) throws HarnessException {
	
		DistributionListItem list =  null;
		if ( type.equals(GenerateItemType.Default) || type.equals(GenerateItemType.Basic) ) {

			String email =  "dlist_" + ZimbraSeleniumProperties.getUniqueString();
			// name with length > 20 is automatically shorten
			email = email.substring(0,20);
			
	        // Create a new dlist
			list = new DistributionListItem(email, "name " + email);
		    
			list.addDListMember(ZimbraSeleniumProperties.getUniqueString());
			list.addDListMember(ZimbraSeleniumProperties.getUniqueString());
			list.addDListMember(ZimbraSeleniumProperties.getUniqueString());
		
		}
		
		if ( type.equals(GenerateItemType.AllAttributes) ) {
			throw new HarnessException("Implement me!");
		}
		
		return list;
	}

	public ArrayList<String> getEmailList() {
		return emailList;
	}
	
	/**
	 * Get the dlist member emails as a comma separated String
	 * @return
	 */
	public String getDList() {
		StringBuilder sb = null;
		for (String email : emailList) {
			if ( sb==null ) {
				sb = new StringBuilder(email);
			} else {
				sb.append(',').append(email);
			}
		}
		return (sb.toString());
	}
	
	public ArrayList<String> addDListMember(String ... memberList) {	     		
        for (String member:memberList) {
        	emailList.add(member);
        }
		return emailList;
	}

	/**
	 * Remove all instances of an emailaddress from the dlist
	 * @param emailaddress
	 * @return the current dlist members
	 */
	public ArrayList<String> removeDListMember(String member) {
	    emailList.remove(member);
		return (emailList);
	}
	
	
	//TODO:????
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
	
	
	
	public static DistributionListItem importFromSOAP(Element GetContactsResponse) throws HarnessException {
		if ( GetContactsResponse == null )
			throw new HarnessException("GetContactsResponse cannot be null");
		
		throw new HarnessException("implement me!");
	}

	public static void grantRightRequest(ZimbraAccount account)throws HarnessException {

	    String accountName   = account.EmailAddress.split("@")[0];
	    String accountDomain = account.EmailAddress.split("@")[1];

		// grant dlist creation request
		
		ZimbraAdminAccount.GlobalAdmin().soapSend(
	     "<GrantRightRequest xmlns='urn:zimbraAdmin'>" 
			+	"<target xmlns='' by='name' type='domain'>" + accountDomain + "</target>"
			+	"<grantee xmlns='' by='name' type='usr'>" + account.EmailAddress + "</grantee>" 
			+	"<right xmlns=''>createDistList</right>" 
		+ "</GrantRightRequest>");
		
		
		//TODO: verification
		//Element response = ZimbraAdminAccount.GlobalAdmin().soapSelectNode("//admin:GrantRightResponse/?????", 1);
	}
	
	public static DistributionListItem createUsingSOAP(ZimbraAccount owner, AppAjaxClient app, ZimbraAccount ... memberList) throws HarnessException {
				    
	        // Create a dlist
			DistributionListItem dlist = null; 
		    String name = "dl_";
		   
		    
		    String soapStr = "";
		    
		    for (ZimbraAccount member:memberList) {
		    	soapStr += 	"<a n='mail'>" + member.EmailAddress + "</a> ";
		    }
	        owner.soapSend(
	        		"<CreateDistributionListRequest xmlns='urn:zimbraAccount'>" +
	                "<name>" + name + memberList[0].EmailAddress + "</name>" +
	                soapStr +
	              	"</CreateDistributionListRequest>"
	                       );

	    	//TODO: check CreateDistribution
	    	dlist = new DistributionListItem(name);
			dlist.owner = owner;
			for (ZimbraAccount member:memberList) {			    
				dlist.addDListMember(member.EmailAddress);
			}
			
		 	dlist.setId(owner.soapSelectValue("//acct:CreateDistributionListResponse/acct:dl" , "id"));
			
			   
	    	// Refresh addressbook
	    	((AppAjaxClient)app).zPageMain.zToolbarPressButton(Button.B_REFRESH);
		
        return dlist;
	}
	

	public static DistributionListItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
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
		sb.append(DistributionListItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(email).append('\n');
		sb.append("dlist: ").append(getDList()).append('\n');
		for (String key : ContactAttributes.keySet())
			sb.append(String.format("Attribute: key(%s) value(%s)", key, ContactAttributes.get(key))).append('\n');
		return (sb.toString());
	}
	

}


