/**
 * 
 */
package framework.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * This class represents a mail message
 * 
 * @author Matt Rhoades
 *
 */
public class MailItem extends ZimbraItem implements IItem {

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
	
	
	
	/**
	 * The folder that contains this mail
	 */
	public FolderItem dFolder;

	/**
	 * The read/unread status of this mail
	 */
	public boolean dRead;
	
	/**
	 * The flags associated with this mail (see soap.txt for details)
	 */
	public int dFlags;
	
	////
	// FINISH: SOAP Data
	////


	////
	// START: GUI Data
	////

	public boolean gIsChecked;
	
	public boolean gIsFlagged;

	public String gPriority; // TODO: how to represent the icon?
	
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
		dFlags = MessageFlags.None;	// Clear all flags
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
	public int addFlag(int flag) {
		int original = dFlags;
		dFlags |= flag;
		return (original);
	}
	
	/**
	 * Remove a flag from this message
	 * @param flag - A MessagePart flag to remove
	 * @return the previous value of the flag
	 */
	public int removeFlag(int flag) {
		int original = dFlags;
		dFlags &= ~flag;
		return (original);
	}
	
	/* (non-Javadoc)
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/* (non-Javadoc)
	 * @see framework.items.IItem#ImportSOAP(com.zimbra.common.soap.Element)
	 */
	@Override
	public void importFromSOAP(Element GetMsgResponse) throws HarnessException {
		
		try {

			// Make sure we only have the GetMsgResponse part
			Element getMsgResponse = ZimbraAccount.SoapClient.selectNode(GetMsgResponse, "//mail:GetMsgResponse");
			if ( getMsgResponse == null )
				throw new HarnessException("Element does not contain GetMsgResponse");
	
			Element m = ZimbraAccount.SoapClient.selectNode(getMsgResponse, "//mail:m");
			if ( m == null )
				throw new HarnessException("Element does not contain an m element");
			
			// Set the ID
			super.id = m.getAttribute("id", null);
			
			// If there is a subject, save it
			Element sElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:su");
			if ( sElement != null )
				dSubject = sElement.getText().trim();
			
			// Parse the recipients
			Element[] eElements = ZimbraAccount.SoapClient.selectNodes(m, "//mail:e");
			for (Element eElement : eElements) {
				
				RecipientItem r = new RecipientItem();
				r.importFromSOAP(eElement);
				
				if ( r.dType == RecipientItem.RecipientType.To ) {
					dToRecipients.add(r);
				} else if ( r.dType == RecipientItem.RecipientType.Cc ) {
					dCcRecipients.add(r);
				} else if ( r.dType == RecipientItem.RecipientType.Bcc ) {
					dBccRecipients.add(r);
				} else if ( r.dType == RecipientItem.RecipientType.From ) {
					dFromRecipient = r;
				} else {
					throw new HarnessException("Unable to parse recipient element "+ eElement.prettyPrint());
				}
				
			} 
			
			Element contentTextPlain = ZimbraAccount.SoapClient.selectNode(m, "//mail:mp[@ct='text/plain']//mail:content");
			if ( contentTextPlain != null ) {
				dBodyText = contentTextPlain.getText().trim();
			}
			
		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ GetMsgResponse.prettyPrint(), e);
		} finally {
			logger.info(this.prettyPrint());
		}
		
	}

	@Override
	public void importFromSOAP(ZimbraAccount account, String query) throws HarnessException {

		try {
			
			account.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>" +
						"<query>"+ query +"</query>" +
					"</SearchRequest>");
			
			Element[] results = account.soapSelectNodes("//mail:SearchResponse/mail:m");
			if (results.length != 1)
				throw new HarnessException("Query should return 1 result, not "+ results.length);
	
			String id = account.soapSelectValue("//mail:SearchResponse/mail:m", "id");
			
			account.soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail'>" +
	                	"<m id='"+ id +"' />" +
	                "</GetMsgRequest>");
			Element getMsgResponse = account.soapSelectNode("//mail:GetMsgResponse", 1);
			
			// Using the response, create this item
			importFromSOAP(getMsgResponse);
			
		} catch (Exception e) {
			throw new HarnessException("Unable to import using SOAP query("+ query +") and account("+ account.EmailAddress +")", e);
		}
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.prettyPrint());
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
	 * Sample MailItem Driver
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String envelopeString = 
			"<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'>" +
				"<soap:Header>" +
					"<context xmlns='urn:zimbra'>" +
						"<change token='5'></change>" +
					"</context>" +
				"</soap:Header>" +
				"<soap:Body>" +
					"<GetMsgResponse xmlns='urn:zimbraMail'>" +
						"<m rev='2' cid='352' id='352' sd='1281643943000' d='1281643943000' l='5' s='429' f='s'>" +
							"<e d='test1281644074332' t='f' a='test1281644074332.1@qa60.lab.zimbra.com' p='test1281644074332 1'></e>" +
							"<e p='test1281644074332 1' a='test1281644074332.1@qa60.lab.zimbra.com' d='test1281644074332' t='t'></e>" +
							"<su>Subject1281644074394.3</su>" +
							"<fr>content of the message1281644074394.4</fr>" +
							"<mid>&lt;19975588.6.1281643943936.JavaMail.root@qa60&gt;</mid>" +
							"<mp body='1' s='38' ct='text/plain' part='1'>" +
								"<content>content of the message1281644074394.4</content>" +
							"</mp>" +
						"</m>" +
					"</GetMsgResponse>" +
				"</soap:Body>" +
			"</soap:Envelope>";

		
		MailItem m = new MailItem();
		m.importFromSOAP(Element.parseXML(envelopeString));
		
		System.out.println("Imported mail item from SOAP");
		System.out.println(m.prettyPrint());
		
		ZimbraAccount.AccountA().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
					"<m l='1'>" +
						"<content>"+
"From: foo@foo.com \n"+
"To: foo@foo.com \n"+
"Subject: email01A \n"+
"MIME-Version: 1.0 \n"+
"Content-Type: text/plain; charset=utf-8 \n"+
"Content-Transfer-Encoding: 7bit \n"+
"\n"+
"simple text string in the body\n"+
"\n"+
						"</content>" +
					"</m>" +
				"</AddMsgRequest>");
		
		m = new MailItem();
		m.importFromSOAP(ZimbraAccount.AccountA(), "subject:(email01A)");
		
		System.out.println("Imported mail item from query");
		System.out.println(m.prettyPrint());

	}

	/**
	 * Set the HTML body of the message
	 * @param body
	 */
	public void setBodyHtml(String body) {
		gBodyHtml = StringEscapeUtils.escapeHtml(body);
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
