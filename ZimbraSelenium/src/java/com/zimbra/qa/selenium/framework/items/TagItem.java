/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;


/**
 * This class represents a mail message
 * 
 * @author Matt Rhoades
 *
 */
public class TagItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	
	////
	// START: SOAP Data
	////
	
	/**
	 * The ID for this tag
	 */
	protected String dId;
		
	/**
	 * The Name for this tag
	 */
	protected String dName;
		
	////
	// FINISH: SOAP Data
	////


	////
	// START: GUI Data
	////

	/**
	 * The icon image for this tag
	 */
	public String gIconImage;
	
	/**
	 * The name for this tag
	 */
	public String gName;
	
	////
	// FINISH: GUI Data
	////

	
	
	/**
	 * Create a mail item
	 */
	public TagItem() {
	}
	

	public void setId(String id) {
		dId = id;		
	}

	public String getId() {
		return (dId);		
	}
	
	public void setName(String name) {
		dName = name;
	}
	
	public String getName() {
		return (dName);
	}


	
	/* (non-Javadoc)
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static TagItem importFromSOAP(Element tag) throws HarnessException {
		
		TagItem item = null;
		
		try {

			// Make sure we only have the <tag/> part
			Element t = ZimbraAccount.SoapClient.selectNode(tag, "//mail:tag");
			if ( t == null )
				throw new HarnessException("Element does not contain an <tag/> element");
			
			// Create the object
			item = new TagItem();
			
			// Set the ID
			item.setId(t.getAttribute("id", null));
			//Set tag name
			Element sElement = ZimbraAccount.SoapClient.selectNode(tag, "//mail:tag");
			if ( sElement != null )
			item.dName = sElement.getAttribute("name");

			
			return (item);
			
		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ tag.prettyPrint(), e);
		} finally {
			if ( item != null )	logger.info(item.prettyPrint());
		}
		
	}




	public static TagItem importFromSOAP(ZimbraAccount account, String name) throws HarnessException {
		
		if ( account == null )
			throw new HarnessException("account cannot be null");
		if ( name == null )
			throw new HarnessException("name cannot be null");
		if ( name.trim().length() == 0 )
			throw new HarnessException("name cannot be empty: ("+ name +")");

		try {
			
			account.soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");
			
			Element[] results = account.soapSelectNodes("//mail:GetTagResponse//mail:tag[@name='"+name+"']");
			
			if (results.length != 1)
				throw new HarnessException("Query should return 1 result, not "+ results.length);
				
			// Using the response, create this item
			return (importFromSOAP(results[0]));
			
		} catch (Exception e) {
			throw new HarnessException("Unable to import using SOAP name("+ name +") and account("+ account.EmailAddress +")", e);
		}
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(TagItem.class.getSimpleName()).append('\n');
		sb.append('\n').append(prettyPrintSOAP());
		sb.append('\n').append(prettyPrintGUI());
		return (sb.toString());
	}
	
	public String prettyPrintSOAP() {
		StringBuilder sb = new StringBuilder();
		sb.append("SOAP Data:\n");
		sb.append("Name: ").append(dName).append('\n');
		return (sb.toString());
	}

	public String prettyPrintGUI() {
		StringBuilder sb = new StringBuilder();
		sb.append("GUI Data:\n");
		sb.append("gIconImage: ").append(gIconImage).append('\n');
		sb.append("gName: ").append(gName).append('\n');
		return (sb.toString());
	}




}
