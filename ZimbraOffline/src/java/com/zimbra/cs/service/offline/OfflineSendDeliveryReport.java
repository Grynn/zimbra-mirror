package com.zimbra.cs.service.offline;

import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Message;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.mail.SendDeliveryReport;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.util.AccountUtil;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author vmahajan
 */
public class OfflineSendDeliveryReport extends SendDeliveryReport {

    @Override
    protected Account getSenderAccount(ZimbraSoapContext zsc) throws ServiceException {
        return getRequestedAccount(zsc);
    }
    
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);

        int msgid = new ItemId(request.getAttribute(MailConstants.A_MESSAGE_ID), zsc).getId();
        Message msg = mbox.getMessageById(octxt, msgid);

        // sending a read receipt requires write access to the message
        if ((mbox.getEffectivePermissions(octxt, msgid, MailItem.TYPE_MESSAGE) & ACL.RIGHT_WRITE) == 0)
            throw ServiceException.PERM_DENIED("you do not have sufficient permissions on the message");

        // first, send the notification
        // make new auth token so we are sure proxy is not set. 
        ZAuthToken authToken = new ZAuthToken(zsc.getRawAuthToken().getValue());
        sendReport(getSenderAccount(zsc), msg, false, zsc.getRequestIP(), zsc.getUserAgent(), authToken);

        // then mark the message as \Notified
        mbox.alterTag(octxt, msgid, MailItem.TYPE_MESSAGE, Flag.ID_FLAG_NOTIFIED, true);

        Element response = zsc.createElement(MailConstants.SEND_REPORT_RESPONSE);
        return response;
    }

    private static final String FROM_ADDRESS_TYPE = "f";
    private static final String TO_ADDRESS_TYPE = "t";
    private static final String READ_RECEIPT = "Read-Receipt: ";
    private static final String MIME_DISP_NOTIFICATION_TO = "Disposition-Notification-To";
    private static final String CONTENT_TYPE_TEXT = "text/plain; charset=utf-8";
    private static final String CONTENT_TYPE_NOTIFICATION = "message/disposition-notification; charset=utf-8";
    private static final String CONTENT_TYPE_REPORT = "multipart/report; report-type=disposition-notification";

    protected void sendReport(Account authAccount, Message msg, boolean automatic, String requestHost, String userAgent, ZAuthToken authToken)
    throws ServiceException {
        OfflineLog.offline.debug("sending report for msg ["+msg+"] in account ["+authAccount+"]");
        try {
        	MimeMessage mm = msg.getMimeMessage();
        	Account owner = msg.getMailbox().getAccount();

        	//should be ok with no duplicate tracking? if a request to self is lost or stuck the system is in pretty bad shape?
        	String sendUID = UUID.randomUUID().toString();
            Element request = new Element.XMLElement(MailConstants.SEND_MSG_REQUEST).addAttribute(MailConstants.A_SEND_UID, sendUID);
            Element m = request.addElement(MailConstants.E_MSG);
            
            InternetAddress[] recipients = Mime.parseAddressHeader(mm, MIME_DISP_NOTIFICATION_TO);
            if (recipients == null || recipients.length == 0)
                return;
            for (InternetAddress recipient : recipients) {
            	m.addElement(MailConstants.E_EMAIL).addAttribute(MailConstants.A_ADDRESS_TYPE, TO_ADDRESS_TYPE).
            	    addAttribute(MailConstants.A_ADDRESS, recipient.getAddress()).
            	    addAttribute(MailConstants.A_PERSONAL, recipient.getPersonal());
            }
            InternetAddress fromAddr = AccountUtil.getFromAddress(authAccount);
            m.addElement(MailConstants.E_EMAIL).addAttribute(MailConstants.A_ADDRESS_TYPE, FROM_ADDRESS_TYPE).
                addAttribute(MailConstants.A_ADDRESS, fromAddr.getAddress()).
                addAttribute(MailConstants.A_PERSONAL, fromAddr.getPersonal());
            m.addElement(MailConstants.E_SUBJECT).setText(READ_RECEIPT + msg.getSubject());

            Element multiPartReport = m.addElement(MailConstants.E_MIMEPART).
                addAttribute(MailConstants.A_CONTENT_TYPE, CONTENT_TYPE_REPORT);
            Element text = multiPartReport.addElement(MailConstants.E_MIMEPART).
                addAttribute(MailConstants.A_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            text.addElement(MailConstants.E_CONTENT).
                setText(generateTextPart(owner, mm, authAccount.getLocale()));
            Element mdn = multiPartReport.addElement(MailConstants.E_MIMEPART).
                addAttribute(MailConstants.A_CONTENT_TYPE, CONTENT_TYPE_NOTIFICATION);
            mdn.addElement(MailConstants.E_CONTENT).
                setText(generateReport(owner, mm, automatic, requestHost, userAgent));
            
            ZcsMailbox ombx = (ZcsMailbox)MailboxManager.getInstance().getMailboxByAccountId(owner.getId(), false);            
            ombx.sendRequest(request, true, true, OfflineLC.zdesktop_request_timeout.intValue(), null, null, getSelfUri(), authToken);
            OfflineLog.offline.debug("sent report (" + sendUID + ") " + msg.getSubject());
        } catch (MessagingException me) {
            throw ServiceException.FAILURE("error while sending read receipt", me);
        }
    }

    private static String selfUri = null;
    
    private synchronized String getSelfUri() {
    	if (selfUri == null) 
    	    selfUri = "http://127.0.0.1:" + LC.zimbra_admin_service_port.intValue() + "/service/soap";
    	return selfUri;
    }    
}
