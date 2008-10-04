package com.zimbra.cs.offline;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.security.auth.login.LoginException;

import org.dom4j.QName;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ExceptionToString;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.DirectorySync;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.offline.common.OfflineConstants.SyncStatus;
import com.zimbra.cs.service.offline.OfflineService;
import com.zimbra.cs.util.yauth.AuthenticationException;

public class OfflineSyncManager {
	
    private static final QName ZDSYNC_ZDSYNC = QName.get("zdsync", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_ACCOUNT = QName.get("account", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_ERROR = QName.get("error", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_EXCEPTION = QName.get("exception", OfflineService.NAMESPACE);
    
    private static final String A_ZDSYNC_NAME = "name";
    private static final String A_ZDSYNC_ID = "id";
    private static final String A_ZDSYNC_STATUS = "status";
    private static final String A_ZDSYNC_LASTSYNC = "lastsync";
    private static final String A_ZDSYNC_MESSAGE = "message";
    private static final String A_ZDSYNC_UNREAD = "unread";
    
    
    private static class SyncError {
    	String message;
    	Exception exception;
    	
    	SyncError(String message, Exception exception) {
    		this.message = message;
    		this.exception = exception;
    	}
    	
    	void encode(Element e) {
    		Element error = e.addElement(ZDSYNC_ERROR);
    		if (message != null && message.length() > 0) {
    			error.addAttribute(A_ZDSYNC_MESSAGE, message);
    		}
    		if (exception != null) {
    			error.addElement(ZDSYNC_EXCEPTION).setText(ExceptionToString.ToString(exception));
    		}
    	}
    }
    

    private static class OfflineSyncStatus {
        String mStage;
        SyncStatus mStatus = SyncStatus.unknown;
        boolean mSyncRunning = false;
        
        long mLastSyncTime = 0;
        long mLastFailTime = 0;
        int mRetryCount = 0;
        
        SyncError mError;
        
		String authPassword;
	    long lastAuthFail;
	    ZAuthToken authToken; //null for data sources
	    long authExpires; //0 for data sources
	    
        boolean syncStart() {
        	if (mStatus == SyncStatus.running)
        		return false;
        	mStatus = SyncStatus.running;
        	mError = null;
        	return true;
        }
        
        boolean syncComplete() {
        	if (mStatus != SyncStatus.running)
        		return false;
        	mLastSyncTime = System.currentTimeMillis();
        	mLastFailTime = 0;
        	mStatus = SyncStatus.online;
        	mRetryCount = 0;
        	return true;
        }
        
        void connectionDown() {
        	if (++mRetryCount >= OfflineLC.zdesktop_retry_limit.intValue()) {
        		mLastFailTime = System.currentTimeMillis();
        	}
        	mStatus = SyncStatus.offline;
        }
        
        void syncFailed(String message, Exception exception) {
        	mLastFailTime = System.currentTimeMillis();
        	mError = new SyncError(message, exception);
        	mStatus = SyncStatus.error;
        	++mRetryCount;
        }
        
        boolean retryOK() {
        	int clicks = mRetryCount - OfflineLC.zdesktop_retry_limit.intValue();
        	clicks = clicks < 0 ? 0 : clicks;
        	clicks = clicks > 15 ? 15 : clicks;
        	long delay = OfflineLC.zdesktop_retry_delay_min.longValue() * (1 << clicks);
        	delay = delay > OfflineLC.zdesktop_retry_delay_max.longValue() ? OfflineLC.zdesktop_retry_delay_max.longValue() : delay;
        	return System.currentTimeMillis() - mLastFailTime > delay;
        }
        
    	ZAuthToken lookupAuthToken(String password) {
    		if (authToken != null && System.currentTimeMillis() < authExpires && password.equals(authPassword))
    			return authToken;
    		authToken = null;
    		authExpires = 0;
    		return null;
    	}
    	
    	boolean reauthOK(String password) {
    		return !password.equals(authPassword) || System.currentTimeMillis() - lastAuthFail > OfflineLC.zdesktop_reauth_delay.longValue();
    	}
    	
    	void authSuccess(String password, ZAuthToken token, long expires) {
    		authPassword = password;
    		authToken = token;
    		authExpires = expires;
    		mStatus = mStatus == SyncStatus.authfail ? SyncStatus.online : mStatus;
    	}
    	
    	void authFailed(String password) {
    		authPassword = password;
    		lastAuthFail = System.currentTimeMillis();
    		authToken = null;
    		authExpires = 0;
    		mStatus = SyncStatus.authfail;
    	}
    	
    	void encode(Element e) {
    		e.addAttribute(A_ZDSYNC_STATUS, mStatus.toString());
        	e.addAttribute(A_ZDSYNC_LASTSYNC, Long.toString(mLastSyncTime));
        	if (mError != null) {
        		mError.encode(e);
        	}
    	}
    	
    	SyncStatus getSyncStatus() {
    		return mStatus;
    	}
    }
    
    private final Map<String, OfflineSyncStatus> syncStatusTable = Collections.synchronizedMap(new HashMap<String, OfflineSyncStatus>());

    private OfflineSyncStatus getStatus(String targetName) {
		synchronized (syncStatusTable) {
			OfflineSyncStatus status = syncStatusTable.get(targetName);
			if (status == null) {
				status = new OfflineSyncStatus();
				syncStatusTable.put(targetName, status);
			}
			return status;
		}
	}
	
	//
	// sync activity update
	//
	
	public long getLastSyncTime(String targetName) {
		synchronized (syncStatusTable) {
			return getStatus(targetName).mLastSyncTime;
		}
	}
	
	public boolean isOnLine(String targetName) {
		synchronized (syncStatusTable) {
			return getStatus(targetName).mStatus == SyncStatus.online;
		}
	}
	
	public void setStage(String targetName, String stage) {
		synchronized (syncStatusTable) {
			getStatus(targetName).mStage = stage;
		}
	}
	
    public void syncStart(String targetName) {
    	boolean b;
    	synchronized (syncStatusTable) {
    		b = getStatus(targetName).syncStart();
    	}
    	if (b)
    		notifyStateChange();
    }
    
    public void syncComplete(String targetName) {
    	boolean b;
    	synchronized (syncStatusTable) {
    		b = getStatus(targetName).syncComplete();
    	}
    	if (b)
    		notifyStateChange();
    }
    
    public void connectionDown(String targetName) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).connectionDown();
    	}
    	notifyStateChange();
    }
    
    private void authFailed(String targetName, String password) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).authFailed(password);
    	}
    	notifyStateChange();
    }
    
    private void syncFailed(String targetName, Exception exception) {
    	syncFailed(targetName, null, exception);
    }
    
    private void syncFailed(String targetName, String message, Exception exception) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).syncFailed(message, exception);
    	}
    	notifyStateChange();
    }

    private void notifyStateChange() {
    	try {
    		OfflineMailboxManager.getOfflineInstance().notifyAllMailboxes();
    	} catch (Exception x) {
    		OfflineLog.offline.error("unexpected exception", x);
    	}
    }
    
    public void authSuccess(String targetName, String password, ZAuthToken token, long expires) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).authSuccess(password, token, expires);
    	}
    }
    
    //
    // account auth
    //
    
	public ZAuthToken lookupAuthToken(Account account) {
		synchronized (syncStatusTable) {
			return getStatus(account.getName()).lookupAuthToken(((OfflineAccount)account).getRemotePassword());
		}
	}
	
	public boolean reauthOK(Account account) {
		synchronized (syncStatusTable) {
			return getStatus(account.getName()).reauthOK(((OfflineAccount)account).getRemotePassword());
		}
	}
	
	public boolean retryOK(Account account) {
	    return retryOK(account.getName());
	}
	
	public boolean retryOK(String targetName) {
        synchronized (syncStatusTable) {
            return getStatus(targetName).retryOK();
        }	    
	}
	
	public void authSuccess(Account account, ZAuthToken token, long expires) {
		authSuccess(account.getName(), ((OfflineAccount)account).getRemotePassword(), token, expires);
	}
	
	private void authFailed(Account account) {
		synchronized (syncStatusTable) {
			getStatus(account.getName()).authFailed(((OfflineAccount)account).getRemotePassword());
		}
	}
	
	//
	// data source auth
	//
	
	public boolean reauthOK(DataSource dataSource) throws ServiceException {
		synchronized (syncStatusTable) {
			return getStatus(dataSource.getName()).reauthOK(dataSource.getDecryptedPassword());
		}
	}
	
	public boolean retryOK(DataSource dataSource) {
		synchronized (syncStatusTable) {
			return getStatus(dataSource.getName()).retryOK();
		}
	}
	
	public void authSuccess(DataSource dataSource) throws ServiceException {
		authSuccess(dataSource.getName(), dataSource.getDecryptedPassword(), null, 0);
	}
	
	private void authFailed(DataSource dataSource) throws ServiceException {
        synchronized (syncStatusTable) {
			getStatus(dataSource.getName()).authFailed(dataSource.getDecryptedPassword());
		}
	}
    
	//
	// process failure
	//
	
	public static boolean isReceiversFault(Exception exception) {
		SoapFaultException fault = null;
		if (exception instanceof SoapFaultException) {
			fault = (SoapFaultException)exception;
		} else if (exception.getCause() instanceof SoapFaultException) {
			fault = (SoapFaultException)(exception.getCause());
		}
		return fault != null && fault.isReceiversFault();
	}
	
	public static boolean isAuthError(Exception exception) {
        if (exception instanceof SoapFaultException) {
		    return ((SoapFaultException)exception).getCode().equals(AccountServiceException.AUTH_FAILED);
        }
        if (exception instanceof ServiceException) {
            Throwable cause = exception.getCause();
            return cause instanceof AuthenticationFailedException ||
                   cause instanceof AuthenticationException ||
                   cause instanceof LoginException;
        }
        return false;
    }
	
    public static boolean isConnectionDown(Exception exception) {
        if (exception instanceof ServiceException) {
        	if (((ServiceException)exception).getCode().equals(ServiceException.RESOURCE_UNREACHABLE))
        		return true;
        	Throwable cause = findCause(exception);
	        if (cause instanceof java.net.UnknownHostException ||
		            cause instanceof java.net.NoRouteToHostException ||
		            cause instanceof java.net.SocketException ||
		        	cause instanceof java.net.SocketTimeoutException ||
		        	cause instanceof java.net.ConnectException ||
		        	cause instanceof org.apache.commons.httpclient.ConnectTimeoutException ||
		        	cause instanceof org.apache.commons.httpclient.NoHttpResponseException)
	        	return true;
        }
        return false;
	}
	
	public static boolean isIOException(Exception exception) {
		Throwable cause = findCause(exception);
		return cause instanceof IOException;
	}
	
	private static Throwable findCause(Exception exception) {
		Throwable cause = exception instanceof ServiceException ? exception.getCause() : exception;
        for (int i = 0; i < 10; ++i) {
        	if (cause instanceof MessagingException) {
        		MessagingException me = (MessagingException)cause;
        		if (me.getNextException() != null)
        			cause = me.getNextException();
        		else
        			break;
        	} else
        		break;
        }
		return cause;
	}
	
    public void processSyncException(Account account, Exception exception) {
    	if (isAuthError(exception)) {
    		authFailed(account);
    	}
    	processSyncException(account.getName(), ((OfflineAccount)account).getRemotePassword(), exception, ((OfflineAccount)account).isDebugTraceEnabled());
    }
    
    public void processSyncException(DataSource dataSource, Exception exception) throws ServiceException {
    	if (isAuthError(exception)) {
    		authFailed(dataSource);
    	}
    	processSyncException(dataSource.getName(), dataSource.getDecryptedPassword(), exception, dataSource.isDebugTraceEnabled());
    }
    
	public void processSyncException(String targetName, String password, Exception exception, boolean isDebugTraceOn) {
		if (isConnectionDown(exception)) {
        	connectionDown(targetName);
        	OfflineLog.offline.info("sync connection down: " + targetName);
        	if (isDebugTraceOn)
        		OfflineLog.offline.debug("sync conneciton down: " + targetName, exception);
        } else if (isAuthError(exception)) {
        	authFailed(targetName, password);
    		OfflineLog.offline.warn("sync remote auth failure: " + targetName);
        	if (isDebugTraceOn)
        		OfflineLog.offline.debug("sync remote auth failure: " + targetName, exception);
        } else {
        	syncFailed(targetName, exception);
        	OfflineLog.offline.error("sync failure: " + targetName, exception);
        	if (exception instanceof SoapFaultException) {
        		SoapFaultException x = (SoapFaultException)exception;
        	    OfflineLog.offline.warn("SoapFaultException: " + x.getReason() + "\nFaultRequest:\n" + x.getFaultRequest() + "\nFaultResponse:\n" + x.getFaultResponse());
        	}
        }
	}
	
	public SyncStatus getSyncStatus(String targetName) {
    	synchronized (syncStatusTable) {
    		return getStatus(targetName).getSyncStatus();
    	}
	}
	
	public void resetStatus(String targetName) {
		synchronized (syncStatusTable) {
			syncStatusTable.remove(targetName);
		}
	}
	
	private static OfflineSyncManager instance = new OfflineSyncManager();
	public static OfflineSyncManager getInstance() {
		return instance;
	}
	
	private Set<Integer> toSkipList = new HashSet<Integer>();
	public boolean isInSkipList(int itemId) {
		return toSkipList.contains(itemId);
	}
	
	public void init() throws ServiceException {
		String[] toSkip = OfflineLC.zdesktop_sync_skip_idlist.value().split("\\s*,\\s*");
		for (String s : toSkip) {
			try {
				toSkipList.add(Integer.parseInt(s));
			} catch (NumberFormatException x) {
				if (s.length() > 0)
					OfflineLog.offline.warn("Invaid item id %s in zdesktop_sync_skip_idlist", s);
			}
		}
		
		//load all mailboxes so that timers are kicked off
    	OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
    	List<Account> dsAccounts = prov.getAllDataSourceAccounts();
		for (Account dsAccount : dsAccounts) {
		    MailboxManager.getInstance().getMailboxByAccount(dsAccount);
		}
		List<Account> syncAccounts = prov.getAllSyncAccounts();
		for (Account syncAccount : syncAccounts) {
			MailboxManager.getInstance().getMailboxByAccount(syncAccount);
		}
		DirectorySync.getInstance();
	}
	
	/*
		<zdsync xmlns="urn:zimbraOffline">
		  <account name="foo@domain1.com" id="1234-5678" status="online" lastsync="1234567" unread="32">
			  [<error [message="{MESSAGE}"]>
			    [<exception>{EXCEPTION}</exception>]
			  </error>]
		  </account>
		  [(<account>...</account>)*]
		</zdsync>
	 */
    public void encode(Element context, String requestedAccountId) throws ServiceException {
    	OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
    	
    	Element zdsync = context.addUniqueElement(ZDSYNC_ZDSYNC);
    	List<Account> accounts = prov.getAllAccounts();
    	for (Account account : accounts) {
        	if (!(account instanceof OfflineAccount) || prov.isLocalAccount(account))
        		continue;
        	
        	String user = account.getName();
    		Element e = zdsync.addElement(ZDSYNC_ACCOUNT).addAttribute(A_ZDSYNC_NAME, user).addAttribute(A_ZDSYNC_ID, account.getId());
    		if (prov.isSyncAccount(account))
    			getStatus(user).encode(e);
    		else if (prov.isDataSourceAccount(account))
    			getStatus(OfflineProvisioning.getOfflineInstance().getDataSourceName(account)).encode(e);
    		else {
        		e.detach();
        		OfflineLog.offline.warn("Invalid account: " + user);
        		continue;
    		}
    		e.addAttribute(A_ZDSYNC_UNREAD, MailboxManager.getInstance().getMailboxByAccount(account).getFolderById(null, Mailbox.ID_FOLDER_INBOX).getUnreadCount());
    	}
    }
    
    private long lastClientPing;
    
    public synchronized void clientPing() {
    	lastClientPing = System.currentTimeMillis();
    }
    
    public synchronized long getSyncFrequencyLimit() {
		long quietTime = System.currentTimeMillis() - lastClientPing;
		
		long freqLimit = 0;
		if (quietTime > Constants.MILLIS_PER_HOUR)
			freqLimit = Constants.MILLIS_PER_HOUR;
		else if (quietTime > 5 * Constants.MILLIS_PER_MINUTE)
			freqLimit = 15 * Constants.MILLIS_PER_MINUTE;
		
		return freqLimit;
    }
}
