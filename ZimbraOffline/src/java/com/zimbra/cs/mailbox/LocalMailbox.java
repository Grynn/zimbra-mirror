package com.zimbra.cs.mailbox;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ArrayUtil;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.IdentityBy;
import com.zimbra.cs.mime.Mime.FixedMimeMessage;
import com.zimbra.cs.offline.OfflineLog;

public class LocalMailbox extends Mailbox {

	public static final String OUTBOX_PATH = "Outbox";
    public static final int ID_FOLDER_OUTBOX = 254;
    public static final String IMPORT_ROOT_PATH = "IMPORT_ROOT";
    public static final int ID_FOLDER_IMPORT_ROOT = 253;
    
    LocalMailbox(MailboxData data) throws ServiceException {
        super(data);
    }
    
    @Override
    public MailSender getMailSender() {
        return new OfflineMailSender();
    }
    
    @Override
    synchronized void initialize() throws ServiceException {
        super.initialize();

        // create a system outbox folder
        Folder userRoot = getFolderById(ID_FOLDER_USER_ROOT);
        Folder.create(ID_FOLDER_OUTBOX, this, userRoot, OUTBOX_PATH, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null);
        Folder.create(ID_FOLDER_IMPORT_ROOT, this, userRoot, IMPORT_ROOT_PATH, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_UNKNOWN, 0, MailItem.DEFAULT_COLOR, null); //root for all data sources
    }
    
    
    private static Map<Integer, Long> sDelaySendMessageMap = Collections.synchronizedMap(new HashMap<Integer, Long>());
	
    /** Tracks messages that we've called SendMsg on but never got back a
     *  response.  This should help avoid duplicate sends when the connection
     *  goes away in the process of a SendMsg.<p>
     *  
     *  key: a String of the form <tt>account-id:message-id</tt><p>
     *  value: a Pair containing the content change ID and the "send UID"
     *         used when the message was previously sent. */
    private static final Map<Integer, Pair<Integer, String>> sSendUIDs = new HashMap<Integer, Pair<Integer, String>>();

    public synchronized void sendPendingMessages(boolean isOnRequest) throws ServiceException {
    	OperationContext context = new OperationContext(this);
    	
        int[] pendingSends = listItemIds(context, MailItem.TYPE_MESSAGE, ID_FOLDER_OUTBOX);
        if (pendingSends == null || pendingSends.length == 0)
            return;

        Session session = LocalJMSession.getSession();
        
        // ids are returned in descending order of date, so we reverse the order to send the oldest first
        for (int id : ArrayUtil.reverse(pendingSends)) {
        	if (!isOnRequest) {
	    		synchronized (sDelaySendMessageMap) {
	    			Long lastTry = sDelaySendMessageMap.get(id);
	    			if (lastTry != null) {
	    				if (System.currentTimeMillis() - lastTry.longValue() > 1 * Constants.MILLIS_PER_MINUTE) { //TODO: change to something else
	    					sDelaySendMessageMap.remove(id);
	    				} else {
	    					continue;
	    				}
	    			}
	    		}
        	}
        	
            Message msg = getMessageById(context, id);
            try {
                // try to avoid repeated sends of the same message by tracking "send UIDs" on SendMsg requests
                Pair<Integer, String> sendRecord = sSendUIDs.get(id);
                String sendUID = sendRecord == null || sendRecord.getFirst() != msg.getSavedSequence() ? UUID.randomUUID().toString() : sendRecord.getSecond();
                sSendUIDs.put(id, new Pair<Integer, String>(msg.getSavedSequence(), sendUID));

                MimeMessage mm = msg.getMimeMessage();
                ((FixedMimeMessage)mm).setSession(session);
                Identity identity = Provisioning.getInstance().get(getAccount(), IdentityBy.id, msg.getDraftIdentityId());
                new MailSender().sendMimeMessage(context, this, true, mm, null, null, msg.getDraftOrigId(), msg.getDraftReplyType(), identity, false, false);
              	OfflineLog.offline.debug("smtp: sent pending mail (" + id + "): " + msg.getSubject());
                
                // remove the draft from the outbox
                delete(context, id, MailItem.TYPE_MESSAGE);
                OfflineLog.offline.debug("smtp: deleted pending draft (" + id + ')');

                // the draft is now gone, so remove it from the "send UID" hash and the list of items to push
                sSendUIDs.remove(id);
            } catch (ServiceException x) {
            	if (x.getCause() instanceof MessagingException) {
	        		OfflineLog.offline.info("SMTP send failure: " + msg.getSubject());
	        		synchronized (sDelaySendMessageMap) {
	        			sDelaySendMessageMap.put(id, System.currentTimeMillis());
	        		}
            	} else {
            		throw x;
            	}
            }
        }
    }
	
}
