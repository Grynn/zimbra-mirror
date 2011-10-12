/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;



/**
 * This class represents a mail message
 * 
 * @author Matt Rhoades
 *
 */
public class TagItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	
	//just a pseudo object for the option Remove Tag-> All Tags with multi-tagged items 
	public static TagItem Remove_All_Tags = new TagItem();

	
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
	 * Create a tag item
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

	//Create a new tag via soap
	//return TagItem object
	public static TagItem CreateUsingSoap(AbsApplication app) throws HarnessException{
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();	
		// Create the object
		TagItem tagItem = new TagItem();

	
		//TODO color attribute 
	   // Create a tag via soap
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
               	"<tag name='"+ tagName +"' color='1' />" +
               "</CreateTagRequest>");
		String tagId = app.zGetActiveAccount().soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");

		// Set the ID
		tagItem.setId(tagId);
		//Set tag name
		tagItem.setName(tagName);			

		// Refresh addressbook
		((AppAjaxClient)app).zPageMain.zToolbarPressButton(Button.B_REFRESH);

		return tagItem;
	}

	
	//Create a new tag via soap
	//return TagItem object
	@Deprecated()
	public static TagItem CreateTagViaSoap(ZimbraAccount account) throws HarnessException{
		throw new HarnessException("deprecated - using CreateUsingSoap instead");		  
	}
	
	/* (non-Javadoc)
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	//TODO: ~ CreateViaSoap?
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static TagItem importFromSOAP(Element tag) throws HarnessException {

		if ( tag == null )
			throw new HarnessException("Element cannot be null");

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
			item.setName(t.getAttribute("name",null));			

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
