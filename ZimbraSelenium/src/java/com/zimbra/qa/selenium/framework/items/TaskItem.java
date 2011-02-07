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
public class TaskItem implements IItem {

	protected static Logger logger = LogManager.getLogger(IItem.class);

	
	////
	// START: GUI Data
	////

	public boolean gIsChecked;
	
	public String gTags; // TODO: how to represent the icon?

	public String gPriority; // TODO: how to represent the icon?
	
	public boolean gHasAttachments;

	public String gSubject;

	public String gStatus;

	public String gPercentComplete;

	public String gDueDate;

	
	////
	// FINISH: GUI Data
	////

	
	/**
	 * Create a mail item
	 */
	public TaskItem() {
	}
	


	public static TaskItem importFromSOAP(Element GetMsgResponse) throws HarnessException {
		
		TaskItem task = null;
		
		try {

			// Make sure we only have the GetMsgResponse part
			Element getMsgResponse = ZimbraAccount.SoapClient.selectNode(GetMsgResponse, "//mail:GetMsgResponse");
			if ( getMsgResponse == null )
				throw new HarnessException("Element does not contain GetMsgResponse");
	
			Element m = ZimbraAccount.SoapClient.selectNode(getMsgResponse, "//mail:m");
			if ( m == null )
				throw new HarnessException("Element does not contain an m element");
			
			// Create the object
			task = new TaskItem();
			
			// TODO: parse the <m/> element
			
			return (task);
			
		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ GetMsgResponse.prettyPrint(), e);
		} finally {
			if ( task != null )	logger.info(task.prettyPrint());
		}
		
	}

	public static TaskItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		
		try {
			
			account.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='task' >" +
						"<query>"+ query +"</query>" +
					"</SearchRequest>");
			
			Element[] results = account.soapSelectNodes("//mail:SearchResponse/mail:task");
			if (results.length != 1)
				throw new HarnessException("Query should return 1 result, not "+ results.length);
	
			String invId = account.soapSelectValue("//mail:SearchResponse/mail:task", "invId");
			
			account.soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail'>" +
	                	"<m id='"+ invId +"' />" +
	                "</GetMsgRequest>");
			Element getMsgResponse = account.soapSelectNode("//mail:GetMsgResponse", 1);
			
			// Using the response, create this item
			return (importFromSOAP(getMsgResponse));
			
		} catch (Exception e) {
			throw new HarnessException("Unable to import using SOAP query("+ query +") and account("+ account.EmailAddress +")", e);
		}
	}

	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me!");
	}


	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(TaskItem.class.getSimpleName()).append('\n');
		sb.append("Checked: ").append(gIsChecked).append('\n');
		sb.append("Tags: ").append(gTags).append('\n');
		sb.append("Priority: ").append(gPriority).append('\n');
		sb.append("Attachments: ").append(gHasAttachments).append('\n');
		sb.append("Subject: ").append(gSubject).append('\n');
		sb.append("Status: ").append(gStatus).append('\n');
		sb.append("% Complete: ").append(gPercentComplete).append('\n');
		sb.append("Due Date: ").append(gDueDate).append('\n');
		return (sb.toString());
	}
	

}
