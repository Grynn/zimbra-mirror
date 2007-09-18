package com.zimbra.cs.mailbox;

import java.util.List;

import org.dom4j.QName;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.ExceptionToString;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.offline.OfflineService;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.session.SoapSession;

public class MailboxSync {
	
    private static final QName ZDSYNC_ZDSYNC = QName.get("zdsync", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_STAGE = QName.get("stage", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_STATE = QName.get("state", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_LASTSYNC = QName.get("lastsync", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_LASTTRY = QName.get("lasttry", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_ERROR = QName.get("error", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_CODE = QName.get("code", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_MESSAGE = QName.get("message", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_EXCEPTION = QName.get("exception", OfflineService.NAMESPACE);
    
    private static final String SN_OFFLINE  = "offline";
    private static final String FN_PROGRESS = "state";
    private static final String FN_TOKEN    = "token";
    private static final String FN_INITIAL  = "initial";
    private static final String FN_LAST_ID  = "last";
    
    private enum SyncStage {
        BLANK, INITIAL, SYNC, RESET
    }

    private enum SyncState {
        OFFLINE, ONLINE, ERROR, RUNNING
    }
    
    private enum ErrorCode {
    	UNKNOWN, REMOTEAUTH
    }
    
    private static class SyncError {
    	ErrorCode code = ErrorCode.UNKNOWN;
    	String message;
    	Exception exception;
    	
    	SyncError(ErrorCode code, String message, Exception exception) {
    		this.code = code;
    		this.message = message;
    		this.exception = exception;
    	}
    	
    	void encode(Element zdsync) {
    		Element error = zdsync.addElement(ZDSYNC_ERROR);
    		error.addElement(ZDSYNC_CODE).setText(code.toString());
    		if (message != null && message.length() > 0) {
    			error.addElement(ZDSYNC_MESSAGE).setText(message);
    		}
    		if (exception != null) {
    			error.addElement(ZDSYNC_EXCEPTION).setText(ExceptionToString.ToString(exception));
    		}
    	}
    }
	
    private SyncStage mStage = SyncStage.BLANK;
    private SyncState mState = SyncState.OFFLINE;
    private boolean mSyncRunning = false;
    
    private long mLastSyncTime = 0;
    private long mLastTryTime = 0;
    private int mRetryCount = 0;
    
    private SyncError mError;
	
    private String mSyncToken;
    private Element mInitialSync;
    private int mLastSyncedItem;
    
    private OfflineMailbox ombx;

    MailboxSync(OfflineMailbox ombx) throws ServiceException {
    	this.ombx = ombx;
    	
        Metadata config = ombx.getConfig(null, SN_OFFLINE);
        if (config != null && config.containsKey(FN_PROGRESS)) {
            try {
            	mStage = SyncStage.valueOf(config.get(FN_PROGRESS));
                switch (mStage) {
                    case INITIAL:  mInitialSync = Element.parseXML(config.get(FN_INITIAL, null));
                                   mLastSyncedItem = (int) config.getLong(FN_LAST_ID, 0);          break;
                    case SYNC:     mSyncToken = config.get(FN_TOKEN, null);                        break;
                }
            } catch (Exception e) {
                ZimbraLog.mailbox.warn("invalid persisted sync data; will force reset");
                mStage = SyncStage.RESET;
            }
        }
    }
    
    private boolean lockMailboxToSync() {
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
    
    private void syncStart() {
    	mState = SyncState.RUNNING;
    	mError = null;
    	notifyStateChange();
    }
    
    private void syncComplete() {
    	mLastSyncTime = mLastTryTime = System.currentTimeMillis();
    	mState = SyncState.ONLINE;
    	notifyStateChange();
    }
    
    void connecitonDown() {
    	if (++mRetryCount >= OfflineLC.zdesktop_retry_limit.intValue()) {
    		mRetryCount = 0;
    		mLastTryTime = System.currentTimeMillis();
    	}
    	mState = SyncState.OFFLINE;
    	notifyStateChange();
    }
    
    private void syncFailed(Exception exception) {
    	syncFailed(null, exception);
    }
    
    private void syncFailed(String message, Exception exception) {
    	syncFailed(ErrorCode.UNKNOWN, message, exception);
    }
    
    private void syncFailed(ErrorCode code, String message, Exception exception) {
    	mLastTryTime = System.currentTimeMillis();
    	mError = new SyncError(code, message, exception);
    	mState = SyncState.ERROR;
    	notifyStateChange();
    }

    private void notifyStateChange() {
    	//loop through mailbox sessions and signal hanging NoOps
    	List<Session> sessions = ombx.getListeners(Session.Type.SOAP);
    	for (Session session : sessions) {
    		((SoapSession)session).forcePush();
    	}
    }
    
    void runSyncOnSchedule() throws ServiceException {
    	final boolean pushEnabled = OfflineLC.zdesktop_enable_push.booleanValue();
    	
        // do we need to sync this mailbox yet?
        if (mState == SyncState.ONLINE && pushEnabled && ombx.getRemoteServerVersion().getMajor() >= 5) {
        	if (mStage != SyncStage.SYNC || OfflinePoller.getInstance().isSyncCandidate(ombx)) {
        		sync();
        	}
        } else if (getSyncFrequency() + mLastTryTime <= System.currentTimeMillis()) {
            sync();
        }
    }
    
    void sync() {
    	String username = null;
        if (lockMailboxToSync()) { //don't want to start another sync when one is already in progress
            try {
                username = ombx.getRemoteUser();
                if (mStage == SyncStage.RESET) {
                    String acctId = ombx.getAccountId();
                    ombx.deleteMailbox();
                    Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(acctId);
                    if (!(mbox instanceof OfflineMailbox)) {
                        OfflineLog.offline.debug("cannot sync: not an OfflineMailbox for account " + username);
                        return;
                    }
                    ombx = (OfflineMailbox) mbox;
                }
                
                syncStart();

                if (mStage == SyncStage.BLANK)
                    InitialSync.sync(ombx);
                else if (mStage == SyncStage.INITIAL)
                    InitialSync.resume(ombx);

                //Send pending messages before delta sync
                PushChanges.sendPendingMessages(ombx);
                
                DeltaSync.sync(ombx);
                if (PushChanges.sync(ombx))
                    DeltaSync.sync(ombx);

                syncComplete();
            } catch (ServiceException e) {
                Throwable cause = e.getCause();
                if (cause instanceof java.net.UnknownHostException ||
                    cause instanceof java.net.NoRouteToHostException ||
                    cause instanceof java.net.SocketException ||
                	cause instanceof java.net.SocketTimeoutException ||
                	cause instanceof java.net.ConnectException ||
                	cause instanceof org.apache.commons.httpclient.ConnectTimeoutException) {
                	connecitonDown();
                	OfflineLog.offline.info("mailbox sync connection down: " + username);
                } else if (e instanceof SoapFaultException && e.getCode().equals(AccountServiceException.AUTH_FAILED)) {
            		syncFailed(ErrorCode.REMOTEAUTH, "remote auth failure", e);
            		OfflineLog.offline.warn("mailbox sync remote auth failure: " + username);
                } else {
                	syncFailed(e);
                    OfflineLog.offline.error("mailbox sync failure: " + username, e);
                }
            } catch (Exception e) {
                syncFailed(e);
                OfflineLog.offline.error("mailbox sync exception: " + username, e);
            } finally {
            	unlockMailbox();
            }
        } else {
        	OfflineLog.offline.debug("sync already in progress");
        }
    }

    /** Returns the minimum frequency (in milliseconds) between syncs with the
     *  remote server.  Defaults to 2 minutes. */
    private long getSyncFrequency() throws ServiceException {
        return ombx.getAccount().getTimeInterval(OfflineProvisioning.A_offlineSyncInterval, OfflineMailboxManager.DEFAULT_SYNC_INTERVAL);
    }
    
    /** Returns the sync token from the last completed initial or delta sync,
     *  or <tt>null</tt> if initial sync has not yet been completed. */
    String getSyncToken() {
        return mSyncToken;
    }

    /** Returns the <tt>SyncResponse</tt> content from the pending initial
     *  sync, or <tt>null</tt> if initial sync is not currently in progress. */
    Element getInitialSyncResponse() {
        return mInitialSync;
    }

    /** Returns the id of the last item initial synced from the current folder
     *  during the pending initial sync, or <tt>0</tt> if initial sync is not
     *  currently in progress or if the initial sync of the previous folder
     *  completed. */
    int getLastSyncedItem() {
        return mLastSyncedItem;
    }

    /** Stores the <tt>SyncResponse</tt> content from the pending initial
     *  sync.  As a side effect, sets the mailbox's {@link SyncStage}
     *  to <tt>INITIAL</tt>. */
    void updateInitialSync(Element initial) throws ServiceException {
        updateInitialSync(initial, -1);
    }

    /** Stores the <tt>SyncResponse</tt> content from the pending initial
     *  sync.  As a side effect, sets the mailbox's {@link SyncStage}
     *  to <tt>INITIAL</tt>. */
    void updateInitialSync(Element initial, int lastId) throws ServiceException {
        if (initial == null)
            throw ServiceException.FAILURE("null Element passed to updateInitialSync", null);

        Metadata config = new Metadata().put(FN_PROGRESS, SyncStage.INITIAL).put(FN_INITIAL, initial).put(FN_LAST_ID, lastId);
        ombx.setConfig(null, SN_OFFLINE, config);

        mStage = SyncStage.INITIAL;
        mInitialSync = initial;
        mLastSyncedItem = lastId;
        mSyncToken = null;
    }

    /** Stores the sync token from the last completed sync (initial or
     *  delta).  As a side effect, sets the mailbox's {@link SyncStage}
     *  to <tt>SYNC</tt>. */
    void recordSyncComplete(String token) throws ServiceException {
        if (token == null)
            throw ServiceException.FAILURE("null sync token passed to recordSyncComplete", null);

        Metadata config = new Metadata().put(FN_PROGRESS, SyncStage.SYNC).put(FN_TOKEN, token);
        ombx.setConfig(null, SN_OFFLINE, config);

        mStage = SyncStage.SYNC;
        mSyncToken = token;
        mInitialSync = null;
    }
    
    public void encode(Element context) {
    	Element zdsync = context.addUniqueElement(ZDSYNC_ZDSYNC);
    	zdsync.addElement(ZDSYNC_STAGE).setText(mStage.toString());
    	zdsync.addElement(ZDSYNC_STATE).setText(mState.toString());
    	zdsync.addElement(ZDSYNC_LASTSYNC).setText(Long.toString(mLastSyncTime));
    	zdsync.addElement(ZDSYNC_LASTTRY).setText(Long.toString(mLastTryTime));
    	if (mError != null) {
    		mError.encode(zdsync);
    	}
    }
}
