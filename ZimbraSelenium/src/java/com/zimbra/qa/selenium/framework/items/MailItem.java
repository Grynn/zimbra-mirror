/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;


/**
 * This class represents a mail message
 * 
 * @author Matt Rhoades
 *
 */
public class MailItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	public class MessageFlags {
		public static final int None = 				0x0000;
		public static final int Unread = 			0x0001;
		public static final int Flagged = 			0x0002;
		public static final int HasAttachment = 	0x0004;
		public static final int Replied = 			0x0008;
		public static final int SentByMe = 			0x0010;
		public static final int Forwarded = 		0x0020;
		public static final int CalendarInvite = 	0x0040;
		public static final int Draft = 			0x0080;
		public static final int ImapDeleted = 		0x0100;
		public static final int NotificationSent = 	0x0200;
		public static final int Urgent = 			0x0400;
		public static final int LowPriority = 		0x0800;
	}
	
	////
	// START: SOAP Data
	////
	
	/**
	 * The subject for this mail
	 */
	public String dSubject;
	
	
	/**
	 * The plain text content
	 */
	public String dBodyText;
	
	/**
	 * The html text content
	 */
	public String dBodyHtml;
	
	
	/**
	 * A list of recipients from the "From:", "To:", "Cc:", and "Bcc:" fields
	 */
	public List<RecipientItem> dToRecipients = new ArrayList<RecipientItem>();
	public List<RecipientItem> dCcRecipients = new ArrayList<RecipientItem>();
	public List<RecipientItem> dBccRecipients = new ArrayList<RecipientItem>();
	public RecipientItem dFromRecipient;
	public RecipientItem dSenderRecipient;
	public RecipientItem dReplyToRecipient;
	public RecipientItem dRedirectedFromRecipient;
	
	
	/**
	 * The folder that contains this mail
	 */
	public String dFolderId;

	/**
	 * The read/unread status of this mail
	 */
	public boolean dRead;
	
	/**
	 * The flags associated with this mail (see soap.txt for details)
	 */
	public String dFlags;
	
	/**
	 * The tag names (tn attribute) associated with this mail (see soap.txt for details)
	 */
	public List<String> dTagNames;
	
	/**
	 * The autoSaveTime associated with this draft) (see soap.txt for details)
	 */
	public String dAutoSendTime = null;

	////
	// FINISH: SOAP Data
	////


	////
	// START: GUI Data
	////

	public boolean gIsChecked;
	
	public boolean gIsFlagged;

	public enum Priority {
		High,
		Normal,
		Low
	}
	public Priority gPriority; // TODO: how to represent the icon?
	
	public String gTags; // TODO: how to represent the icon?

	public String gStatusIcon; // TODO: how to represent these status icons?
	
	public String gFrom;

	public boolean gHasAttachments;

	public String gSubject;

	public String gFragment;

	public String gFolder;

	public String gSize;

	public String gReceived;

	
	/**
	 * Is the message currently highlighted in the list view?
	 */
	public boolean gIsSelected;


	/**
	 * The text body of the message
	 */
	public String gBodyText;
	
	/**
	 * The HTML body of the message
	 */
	private String gBodyHtml;
	
	////
	// FINISH: GUI Data
	////

	
	
	/**
	 * Create a mail item
	 */
	public MailItem() {
	}
	
	@Override
	public String getName() {
		return (getSubject());
	}
	
	public String getSubject() {
		return (dSubject);
	}
	
	// TODO: eventually, replace this with the com.zimbra.soap.types.Contact method
	private String myId;
	public String getId() {
		return (myId);
	}
	public void setId(String id) {
		myId=id;
	}


	public List<RecipientItem> dAllRecipients() {
		List<RecipientItem> list = new ArrayList<RecipientItem>();
		
		if ( dFromRecipient != null )
			list.add(dFromRecipient);
		
		for ( RecipientItem r : dToRecipients )
			list.add(r);
		
		for ( RecipientItem r : dCcRecipients )
			list.add(r);

		for ( RecipientItem r : dBccRecipients )
			list.add(r);

		return (list);
	}

	/**
	 * Add a flag to this message
	 * @param flag - A MessagePart flag to add
	 * @return the previous value of the flag
	 */
	public String setFlags(String flags) {
		dFlags = flags;
		return (dFlags);
	}
	
	/**
	 * Remove a flag from this message
	 * @param flag - A MessagePart flag to remove
	 * @return the previous value of the flag
	 */
	public String getFlags() {
		return (dFlags);
	}
	
	/**
	 * Add a list of tags to this message
	 * @param tn - A list of tag names to add
	 * @return the list of tag names
	 */
	public List<String> setTagNames(List<String> tn) {
		dTagNames = tn;
		return (dTagNames);
	}
	
	/**
	 * Get the list of tags on this message
	 * @return the list of tag names
	 */
	public List<String> getTagNames() {
		return (dTagNames);
	}
	
	public String getAutoSendTime() {
		return (dAutoSendTime);
	}

	private String setAutoSendTime(String autoSaveTime) {
		dAutoSendTime = autoSaveTime;
		return (dAutoSendTime);
	}


	/* (non-Javadoc)
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public static MailItem importFromSOAP(Element GetMsgResponse) throws HarnessException {
		if ( GetMsgResponse == null )
			throw new HarnessException("Element cannot be null");

		MailItem mail = null;
		
		try {

			// Make sure we only have the GetMsgResponse part
			Element getMsgResponse = ZimbraAccount.SoapClient.selectNode(GetMsgResponse, "//mail:GetMsgResponse");
			if ( getMsgResponse == null )
				throw new HarnessException("Element does not contain GetMsgResponse");
	
			Element m = ZimbraAccount.SoapClient.selectNode(getMsgResponse, "//mail:m");
			if ( m == null )
				throw new HarnessException("Element does not contain an m element");
			
			// Create the object
			mail = new MailItem();
			
			// Set the ID
			mail.setId(m.getAttribute("id", null));
			mail.setFlags(m.getAttribute("f", ""));
			mail.setTagNames(Arrays.asList(m.getAttribute("tn", "").split(",")));
			mail.setAutoSendTime(m.getAttribute("autoSendTime", null));

			mail.dFolderId = m.getAttribute("l", null);
			
			// If there is a subject, save it
			Element sElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:su");
			if ( sElement != null )
				mail.dSubject = sElement.getText().trim();
			
			// Parse the recipients
			Element[] eElements = ZimbraAccount.SoapClient.selectNodes(m, "mail:e");
			for (Element eElement : eElements) {
				
				RecipientItem r = new RecipientItem();
				r = RecipientItem.importFromSOAP(eElement);
				
				if ( r.dType == RecipientItem.RecipientType.To ) {
					mail.dToRecipients.add(r);
				} else if ( r.dType == RecipientItem.RecipientType.Cc ) {
					mail.dCcRecipients.add(r);
				} else if ( r.dType == RecipientItem.RecipientType.Bcc ) {
					mail.dBccRecipients.add(r);
				} else if ( r.dType == RecipientItem.RecipientType.From ) {
					mail.dFromRecipient = r;
				} else if ( r.dType == RecipientItem.RecipientType.Sender ) {
					mail.dSenderRecipient = r;
				} else if ( r.dType == RecipientItem.RecipientType.ReplyTo ) {
					mail.dReplyToRecipient = r;
				} else if ( r.dType == RecipientItem.RecipientType.ReadReceipt ) {
					// Nothing to do for this case
				} else if ( r.dType == RecipientItem.RecipientType.RedirectedFrom ) {
					mail.dRedirectedFromRecipient = r;
				} else {
					throw new HarnessException("Unable to parse recipient element "+ eElement.prettyPrint());
				}
				
			} 
			
			Element contentTextPlain = ZimbraAccount.SoapClient.selectNode(m, "//mail:mp[@ct='text/plain']//mail:content");
			Element contentBodyHtml = ZimbraAccount.SoapClient.selectNode(m, "//mail:mp[@ct='text/html']//mail:content");
			if ( contentTextPlain != null ) {
				mail.dBodyText = contentTextPlain.getText().trim();
			}
			else if ( contentBodyHtml != null ) {
				mail.dBodyHtml= contentBodyHtml.getText().trim();
			}

			return (mail);

		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ GetMsgResponse.prettyPrint(), e);
		} finally {
			if ( mail != null )	logger.info(mail.prettyPrint());
		}
		
	}


	public static MailItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		
		return importFromSOAP(account, query, SOAP_DESTINATION_HOST_TYPE.SERVER, null);

	}

	public static MailItem importFromSOAP(ZimbraAccount account, String query,
	      SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException {

	   try {

         account.soapSend(
               "<SearchRequest xmlns='urn:zimbraMail' types='message'>" +
                  "<query>"+ query +"</query>" +
               "</SearchRequest>",
               destType,
               accountName);
         
         Element[] results = account.soapSelectNodes("//mail:SearchResponse/mail:m");
         if (results.length != 1)
            //throw new HarnessException("Query should return 1 result, not "+ results.length);
        	return null;
   
         String id = account.soapSelectValue("//mail:SearchResponse/mail:m", "id");
         
         account.soapSend(
               "<GetMsgRequest xmlns='urn:zimbraMail'>" +
                     "<m id='"+ id +"' />" +
                   "</GetMsgRequest>",
                   destType,
                   accountName);
         Element getMsgResponse = account.soapSelectNode("//mail:GetMsgResponse", 1);
         
         // Using the response, create this item
         return (importFromSOAP(getMsgResponse));
         
      } catch (Exception e) {
         throw new HarnessException("Unable to import using SOAP query("+ query +") and account("+ account.EmailAddress +")", e);
      }
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(MailItem.class.getSimpleName()).append('\n');
		sb.append('\n').append(prettyPrintSOAP());
		sb.append('\n').append(prettyPrintGUI());
		return (sb.toString());
	}
	
	public String prettyPrintSOAP() {
		StringBuilder sb = new StringBuilder();
		sb.append("SOAP Data:\n");
		sb.append("Subject: ").append(dSubject).append('\n');
		for (RecipientItem r : dToRecipients) {
			sb.append(r.prettyPrint());
		}
		for (RecipientItem r : dCcRecipients) {
			sb.append(r.prettyPrint());
		}
		for (RecipientItem r : dBccRecipients) {
			sb.append(r.prettyPrint());
		}
		if ( dFromRecipient != null ) {
			sb.append(dFromRecipient.prettyPrint());
		}
		sb.append("tn: ");
		if ( getTagNames() != null ) {
			for ( String tn : getTagNames() ) {
				sb.append(tn).append(", ");
			}
		}
		sb.append("\n");
		if ( (dAutoSendTime != null) && (dAutoSendTime.trim().length() != 0) ) {
			sb.append("autoSaveTime: ");
			sb.append(dAutoSendTime);
			try {
				// Print a 'friendly' version of the time, too
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				sb.append( '(').append(formatter.format(new Date(Long.parseLong(dAutoSendTime)))).append(')');
			} catch (NumberFormatException e) {
				logger.warn("Unable to parse autoSaveTime attribute.  Skip logging the value.", e);
			}
			sb.append('\n');
		}
		sb.append("Content(text):").append('\n').append(dBodyText).append('\n');
		sb.append("Content(html):").append('\n').append(dBodyHtml).append('\n');
		return (sb.toString());
	}

	public String prettyPrintGUI() {
		StringBuilder sb = new StringBuilder();
		sb.append("GUI Data:\n");
		sb.append("IsChecked: ").append(gIsChecked).append('\n');
		sb.append("IsFlagged: ").append(gIsFlagged).append('\n');
		sb.append("Priority: ").append(gPriority).append('\n');
		sb.append("Tagged: ").append(gTags).append('\n');
		sb.append("Status Icon: ").append(gStatusIcon).append('\n');
		sb.append("From: ").append(gFrom).append('\n');
		sb.append("Has Attachments: ").append(gHasAttachments).append('\n');
		sb.append("Subject: ").append(gSubject).append('\n');
		sb.append("Fragment: ").append(gFragment).append('\n');
		sb.append("Folder: ").append(gFolder).append('\n');
		sb.append("Size: ").append(gSize).append('\n');
		sb.append("Received: ").append(gReceived).append('\n');
		return (sb.toString());
	}




	/**
	 * Set the HTML body of the message
	 * @param body
	 */
	public void setBodyHtml(String body) {
		gBodyHtml = XmlStringUtil.escapeXml(body);
	}

	/**
	 * Get the HTML body of the message
	 * @param body
	 */
	public String getBodyHtml() {
		return (gBodyHtml);
	}


	/**
	 * Generate a sample mime from this object
	 * This String can be used in an <AddMsgRequest/>
	 */
	public String generateMimeString() {
		StringBuilder sb = new StringBuilder();
		
		if ( this.dFromRecipient != null ) {
			sb.append("From: ").append(dFromRecipient.dEmailAddress).append('\n');
		}
		for (RecipientItem r : this.dToRecipients) {
			sb.append("To: ").append(r.dEmailAddress).append('\n');
		}
		for (RecipientItem r : this.dCcRecipients) {
			sb.append("Cc: ").append(r.dEmailAddress).append('\n');
		}
		for (RecipientItem r : this.dBccRecipients) {
			sb.append("Bcc: ").append(r.dEmailAddress).append('\n');
		}
		
		if ( this.dSubject != null ) {
			sb.append("Subject: ").append(this.dSubject).append('\n');
		}
		
		sb.append("MIME-Version: 1.0\n");
		sb.append("Content-Type: text/plain; charset=utf-8\n");
		sb.append("Content-Transfer-Encoding: 7bit\n");
		
		if ( this.dBodyText == null ) {
			sb.append("\n\n\n");
		} else {
			sb.append("\n\n");
			sb.append(this.dBodyText);
			sb.append("\n\n\n");
		}

		return (sb.toString());
	}


}
