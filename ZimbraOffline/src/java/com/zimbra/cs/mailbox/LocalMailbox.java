package com.zimbra.cs.mailbox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.Provisioning.IdentityBy;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.mailbox.MailSender.SafeSendFailedException;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.mime.Mime.FixedMimeMessage;
import com.zimbra.cs.offline.LMailSender;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.YMailSender;
import com.zimbra.cs.offline.util.ymail.YMailException;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.util.JMSession;

public class LocalMailbox extends DesktopMailbox {
    
	private final boolean isImapMailbox;
    private final Flag mSyncFlag;
    private final Flag mSyncFolderFlag;
	
    LocalMailbox(MailboxData data) throws ServiceException {
        super(data);
        
        DataSource ds = OfflineProvisioning.getOfflineInstance().getDataSource(getAccount());
        isImapMailbox = ds != null && (ds.getType() == DataSource.Type.imap || ds.getType() == DataSource.Type.live);

        mSyncFlag = getFlagById(Flag.ID_FLAG_SYNC);
        mSyncFolderFlag = getFlagById(Flag.ID_FLAG_SYNCFOLDER);
    }

    @Override protected synchronized void initialize() throws ServiceException {
        super.initialize();
        if (isImapMailbox) {
	        List<Pair<Integer, String>> systemMailFolders = new ArrayList<Pair<Integer, String>>();
	        systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_INBOX, "/Inbox"));
	        systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_TRASH, "/Trash"));
	        systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_SPAM, "/Junk"));
	        systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_SENT, "/Sent"));
	        systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_DRAFTS, "/Drafts"));
        	for (Pair<Integer, String> pair : systemMailFolders) {
        		MailItem mi = getCachedItem(pair.getFirst());
        		DbMailItem.alterTag(mSyncFolderFlag, Arrays.asList(pair.getFirst()), true);
        		if (mi != null)
        			mi.mData.flags |= mSyncFolderFlag.getBitmask();
        		if (isSyncEnabledByDefault(pair.getSecond())) {
        			DbMailItem.alterTag(mSyncFlag, Arrays.asList(pair.getFirst()), true);
        			if (mi != null)
        				mi.mData.flags |= mSyncFlag.getBitmask();
        		}
        	}
        }
    }
    
    private boolean isSyncEnabledByDefault(String path) throws ServiceException {
    	OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.getOfflineInstance().getDataSource(getAccount()));
    	return ds != null && ds.isSyncEnabledByDefault(path);
    }
    
    private void alterSyncFolderFlag(Folder folder, boolean canSync) throws ServiceException {
    	DbMailItem.alterTag(mSyncFolderFlag, Arrays.asList(folder.getId()), canSync);
    	if (canSync) {
    		folder.mData.flags |= mSyncFolderFlag.getBitmask();
//    		if (isSyncEnabledByDefault(folder.getPath())) {
//    			DbMailItem.alterTag(mSyncFlag, Arrays.asList(folder.getId()), canSync);
//    			folder.mData.flags |= mSyncFlag.getBitmask();
//    		}
    	} else {
    		folder.mData.flags &= ~mSyncFolderFlag.getBitmask();
    		folder.mData.flags &= ~mSyncFlag.getBitmask();
    	}
    }
    
    @Override
    void archiveSingleItem(MailItem item, boolean toArchive) throws ServiceException {
    	super.archiveSingleItem(item, toArchive);
    	if (isImapMailbox && item instanceof Folder)
    		alterSyncFolderFlag((Folder)item, !toArchive);
    }
	
    @Override
	void itemCreated(MailItem item, boolean inArchive) throws ServiceException {
		if (isImapMailbox && !inArchive && item instanceof Folder)
			alterSyncFolderFlag((Folder)item, true);
	}
    
    @Override
    public MailSender getMailSender() {
        return new OfflineMailSender();
    }
    
    /*
     * Tracks messages that we've called SendMsg on but never got back a
     *  response.  This should help avoid duplicate sends when the connection
     *  goes away in the process of a SendMsg.<p>
     *  
     *  key: a String of the form <tt>account-id:message-id</tt><p>
     *  value: a Pair containing the content change ID and the "send UID"
     *         used when the message was previously sent.
     */
    private static final Map<Integer, Pair<Integer, String>> sSendUIDs = new HashMap<Integer, Pair<Integer, String>>();

    public int sendPendingMessages(boolean isOnRequest) throws ServiceException {
    	OperationContext context = new OperationContext(this);

    	int sentCount = 0;
        for (Iterator<Integer> iterator = OutboxTracker.iterator(this, isOnRequest ? 0 : 5 * Constants.MILLIS_PER_MINUTE); iterator.hasNext(); ) {
            int id = iterator.next();
        	
            Message msg;
            try {
            	msg = getMessageById(context, id);
            } catch (NoSuchItemException x) { //message deleted
                OutboxTracker.remove(this, id);
            	continue;
            }
            if (msg == null || msg.getFolderId() != ID_FOLDER_OUTBOX) {
            	OutboxTracker.remove(this, id);
            	continue;
            }
            
            Session session = null;
            //the client could send datasourceId as identityId
            OfflineDataSource ds = getDataSource(msg);
            if (!ds.isLive() && !ds.isYahoo()) {
                session = LocalJMSession.getSession(ds);
                if (session == null) {
                    OfflineLog.offline.info("SMTP configuration not valid: " + msg.getSubject());
                    bounceToInbox(context, id, msg, "SMTP configuration not valid");
                    OutboxTracker.remove(this, id);
                    continue;
                }
            }
            Identity identity = Provisioning.getInstance().get(getAccount(), IdentityBy.id, msg.getDraftIdentityId());
            // try to avoid repeated sends of the same message by tracking "send UIDs" on SendMsg requests
            Pair<Integer, String> sendRecord = sSendUIDs.get(id);
            String sendUID = sendRecord == null || sendRecord.getFirst() != msg.getSavedSequence() ?
                UUID.randomUUID().toString() : sendRecord.getSecond();
            sSendUIDs.put(id, new Pair<Integer, String>(msg.getSavedSequence(), sendUID));

            MimeMessage mm = ((FixedMimeMessage) msg.getMimeMessage()).setSession(session);
            ItemId origMsgId = getOrigMsgId(msg);

            if (ds.isLive()) {
                LMailSender ms = LMailSender.newInstance(ds);
                try {
                    ms.sendMimeMessage(context, this, false, mm, null, null, origMsgId,
                        msg.getDraftReplyType(), identity, false, false);
                } catch (ServiceException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof MessagingException) {
                        OfflineLog.offline.debug("smtp: failed to send mail (" + id + "): " + msg.getSubject(), e);
                        OfflineLog.offline.info("SMTP send failure: " + msg.getSubject());
                        OutboxTracker.recordFailure(this, id);
                        continue;
                    } else {
                        throw e;
                    }
                }
            } else if (ds.isYahoo()) {
                YMailSender ms = YMailSender.newInstance(ds);
                try {
                    ms.sendMimeMessage(context, this, false, mm, null, null, origMsgId,
                                       msg.getDraftReplyType(), identity, false, false);
                } catch (ServiceException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof YMailException) {
                        OfflineLog.offline.info("YMail request failed: " + msg.getSubject(), cause);
                        OutboxTracker.recordFailure(this, id);
                        continue;
                    } else {
                        throw e;
                    }
                }
            } else {
                MailSender ms = new MailSender();
                try {
                    ms.sendMimeMessage(context, this, ds.isSaveToSent(), mm, null, null,
                        origMsgId, msg.getDraftReplyType(), identity, false, false);
                } catch (ServiceException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof MessagingException) {
                        OfflineLog.offline.debug("smtp: failed to send mail (" + id + "): " + msg.getSubject(), e);
                        OfflineLog.offline.info("SMTP send failure: " + msg.getSubject());
                        if (cause instanceof SafeSendFailedException) {
                            bounceToInbox(context, id, msg, cause.getMessage());
                            OutboxTracker.remove(this, id);
                        } else {
                            OutboxTracker.recordFailure(this, id);
                        }
                        continue;
                    } else {
                        throw e;
                    }
                }
            }
            
            OfflineLog.offline.debug("sent pending mail (" + id + "): " + msg.getSubject());

            // remove the draft from the outbox
            delete(context, id, MailItem.TYPE_MESSAGE);
            OutboxTracker.remove(this, id);
            OfflineLog.offline.debug("deleted pending draft (" + id + ')');

            // the draft is now gone, so remove it from the "send UID" hash and the list of items to push
            sSendUIDs.remove(id);
            sentCount++;
        }
        
        return sentCount;
    }

    private ItemId getOrigMsgId(Message msg) throws ServiceException {
        String origId = msg.getDraftOrigId();
        return StringUtil.isNullOrEmpty(origId) ? null : new ItemId(origId, getAccountId());
    }

    private OfflineDataSource getDataSource(Message msg) throws ServiceException {
        //the client could send datasourceId as identityId
        Account acct = getAccount();
        DataSource ds = Provisioning.getInstance().get(
            acct, DataSourceBy.id, msg.getDraftIdentityId());
        if (ds == null) {
            ds = OfflineProvisioning.getOfflineInstance().getDataSource(acct);
        }
        return (OfflineDataSource) ds;
    }
    
    private void bounceToInbox(OperationContext context, int id, Message msg, String error) {
    	MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSession());
		try {
			mm.setFrom(new InternetAddress(getAccount().getName()));
    		mm.setRecipient(RecipientType.TO, new InternetAddress(getAccount().getName()));
    		mm.setSubject("Delivery failed: " + error);
    		
    		mm.saveChanges(); //must call this to update the headers
    		
    		MimeMultipart mmp = new MimeMultipart();
    		MimeBodyPart mbp = new MimeBodyPart();
    		mbp.setText(error == null ? "SEND FAILED. PLEASE CHECK RECIPIENT ADDRESSES AND SMTP SETTINGS." : error);
   			mmp.addBodyPart(mbp);
    		
    		mbp = new MimeBodyPart();
    		mbp.setContent(msg.getMimeMessage(), "message/rfc822");
    		mbp.setHeader("Content-Disposition", "attachment");
    		mmp.addBodyPart(mbp, mmp.getCount());
    		
    		mm.setContent(mmp);
    		mm.saveChanges();
		
    		//directly bounce to local inbox
    		ParsedMessage pm = new ParsedMessage(mm, true);
    		addMessage(context, pm, Mailbox.ID_FOLDER_INBOX, true, Flag.BITMASK_UNREAD, null);
    		delete(context, id, MailItem.TYPE_MESSAGE);
		} catch (Exception e) {
			OfflineLog.offline.warn("smtp: can't bounce failed send (" + id + ")" + msg.getSubject(), e);
		}
    }
    
    private boolean isAutoSyncDisabled(DataSource ds) {
        return ds.getSyncFrequency() <= 0;
    }
    
    @Override
    public boolean isAutoSyncDisabled() {
    	try {
			List<DataSource> dataSources = OfflineProvisioning.getOfflineInstance().getAllDataSources(getAccount());
			for (DataSource ds : dataSources) {
				if (!isAutoSyncDisabled(ds))
					return false;
			}
    	} catch (ServiceException x) {
    		OfflineLog.offline.error(x);
    	}
    	return true;
    }

    @Override
    protected void syncOnTimer() {
    	try {
    		sync(false, false);
    	} catch (ServiceException x) {
    		OfflineLog.offline.error(x);
    	}
    }

    private void syncAllLocalDataSources(boolean force, boolean isOnRequest) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        List<DataSource> dataSources = prov.getAllDataSources(getAccount());
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        for (DataSource ds : dataSources) {
            if (!force && !isOnRequest) {
                if (isAutoSyncDisabled(ds) || !syncMan.reauthOK(ds) || !syncMan.retryOK(ds))
                    continue;

	    		long freqLimit = syncMan.getSyncFrequencyLimit();
	    		long frequency = ds.getSyncFrequency() < freqLimit ? freqLimit : ds.getSyncFrequency();

                if (System.currentTimeMillis() - syncMan.getLastSyncTime(ds.getName()) < frequency)
                    continue;
            }
            try {
                syncMan.syncStart(ds.getName());
                importData(ds, isOnRequest);
                syncMan.syncComplete(ds.getName());
                OfflineProvisioning.getOfflineInstance().setDataSourceAttribute(ds, OfflineConstants.A_zimbraDataSourceLastSync, Long.toString(System.currentTimeMillis()));
            } catch (Exception x) {
                if (isDeleting())
                    OfflineLog.offline.info("Mailbox \"%s\" is being deleted", getAccountName());
                else
                    syncMan.processSyncException(ds, x);
            }
        }
    }

    private static void importData(DataSource ds, boolean isOnRequest)
        throws ServiceException {
        // Force a full sync if INBOX has not yet been successfully imported
        boolean inboxSynced = ds.hasSyncState(Mailbox.ID_FOLDER_INBOX);
        boolean fullSync = isOnRequest || !inboxSynced;
        List<Integer> folderIds = null;
        OfflineDataSource ods = (OfflineDataSource)ds;
        if (!fullSync && ods.isEmail()) {
        	// Import only INBOX and SENT (if not save-to-sent) folders
        	folderIds = new ArrayList<Integer>(2);
        	folderIds.add(Mailbox.ID_FOLDER_INBOX);
        	if (!ds.isSaveToSent()) {
        		folderIds.add(Mailbox.ID_FOLDER_SENT);
        	}
        } 
        DataSourceManager.importData(ds, folderIds, fullSync);
    }

    private boolean mSyncRunning;

    private boolean lockMailboxToSync() {
    	if (isDeleting())
    		return false;
    	
    	if (!mSyncRunning) {
	    	synchronized (this) {
	    		if (!mSyncRunning) {
	    			mSyncRunning = true;
	    			return true;
	    		}
	    	}
    	}
    	return false;
    }
    
    private void unlockMailbox() {
    	assert mSyncRunning == true;
    	mSyncRunning = false;
    }

    public void sync(boolean isOnRequest, boolean isDebugTraceOn) throws ServiceException {
        if (lockMailboxToSync()) {
        	if (isDebugTraceOn) {
        		OfflineLog.offline.debug("============================== SYNC DEBUG TRACE START ==============================");
        		getOfflineAccount().setRequestScopeDebugTraceOn(true);
        	}
        	
            try {
                int count = sendPendingMessages(isOnRequest);
                syncAllLocalDataSources(count > 0, isOnRequest);
            } catch (Exception x) {
                OfflineLog.offline.error("exception encountered during sync", x);
            } finally {
            	if (isDebugTraceOn) {
            		getOfflineAccount().setRequestScopeDebugTraceOn(false);
            		OfflineLog.offline.debug("============================== SYNC DEBUG TRACE END ================================");
            	}
                unlockMailbox();
            }
        } else if (isOnRequest) {
            OfflineLog.offline.debug("sync already in progress");
        }
    }
}
