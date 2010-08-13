/**
 * 
 */
package framework.items;

import java.util.ArrayList;

import com.zimbra.common.soap.Element;

import framework.items.RecipientItem.RecipientType;
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
	
	/**
	 * The subject for this mail
	 */
	public String subject;
	
	/**
	 * The message body parts for this mail
	 */
	public MessagePart part;
	
	/**
	 * The sender of this mail
	 */
	public RecipientItem sender;
	
	/**
	 * A list of recipients from the "From:", "To:", "Cc:", and "Bcc:" fields
	 */
	public ArrayList<RecipientItem> recipients = new ArrayList<RecipientItem>();
	
	
	/**
	 * The read/unread status of this mail
	 */
	public boolean read;
	
	/**
	 * The flags associated with this mail (see soap.txt for details)
	 */
	public int flags;
	
	/**
	 * The text body of the message
	 */
	public String bodyText;
	
	/**
	 * The folder that contains this mail
	 */
	public FolderItem folder;
	
	
	/**
	 * Create a mail item
	 */
	public MailItem() {
		flags = MessageFlags.None;	// Clear all flags
	}

	/**
	 * Add a flag to this message
	 * @param flag - A MessagePart flag to add
	 * @return the previous value of the flag
	 */
	public int addFlag(int flag) {
		int original = flags;
		flags |= flag;
		return (original);
	}
	
	/**
	 * Remove a flag from this message
	 * @param flag - A MessagePart flag to remove
	 * @return the previous value of the flag
	 */
	public int removeFlag(int flag) {
		int original = flags;
		flags &= ~flag;
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
				subject = sElement.getText();
			
			// Parse the recipients
			Element[] recipientElements = ZimbraAccount.SoapClient.selectNodes(m, "//mail:e");
			for (Element rElement : recipientElements) {
				RecipientItem r = new RecipientItem();
				String type = rElement.getAttribute("t", "t");
				if ( type.equals("t"))
					r.type = RecipientType.To;
				if ( type.equals("c"))
					r.type = RecipientType.Cc;
				if ( type.equals("b"))
					r.type = RecipientType.Bcc;
				if ( type.equals("f")) {
					r.type = RecipientType.From;
					sender = new RecipientItem();
					sender.emailAddress = rElement.getAttribute("a", null);
					sender.name = rElement.getAttribute("p", null);
							}
				r.emailAddress = rElement.getAttribute("a", null);
				r.name = rElement.getAttribute("p", null);
				recipients.add(r);
			} 
		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ GetMsgResponse.prettyPrint(), e);
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
	public String printItem() {
		StringBuilder sb = new StringBuilder();
		sb.append(MailItem.class.getSimpleName()).append('\n');
		sb.append("ID: ").append(id).append('\n');
		sb.append("Subject: ").append(subject).append('\n');
		for (RecipientItem r : recipients) {
			sb.append(r.type).append(": ").append(r.emailAddress).append('\n');
		}
		sb.append("TextBody: \n\n").append(bodyText).append("\n\n");
		return (sb.toString());
	}


	/**
	 * MessagePart defines a part of a mail message
	 *
	 */
	public static class MessagePart {
		
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
		System.out.println(m.printItem());
		
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
		System.out.println(m.printItem());

	}

}
