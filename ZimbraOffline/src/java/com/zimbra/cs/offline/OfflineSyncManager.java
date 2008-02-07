package com.zimbra.cs.offline;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.dom4j.QName;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.ExceptionToString;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.offline.common.OfflineConstants.SyncStatus;
import com.zimbra.cs.service.offline.OfflineService;

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
        long mLastTryTime = 0;
        int mRetryCount = 0;
        
        SyncError mError;
        
		String authPassword;
	    long lastAuthFail;
	    String authToken; //null for data sources
	    long authExpires; //0 for data sources
        
        void syncStart() {
        	mStatus = SyncStatus.running;
        	mError = null;
        }
        
        void syncComplete() {
        	mLastSyncTime = mLastTryTime = System.currentTimeMillis();
        	mStatus = SyncStatus.online;
        }
        
        void connecitonDown() {
        	if (++mRetryCount >= OfflineLC.zdesktop_retry_limit.intValue()) {
        		mRetryCount = 0;
        		mLastTryTime = System.currentTimeMillis();
        	}
        	mStatus = SyncStatus.offline;
        }
        
        void syncFailed(String message, Exception exception) {
        	mLastTryTime = System.currentTimeMillis();
        	mError = new SyncError(message, exception);
        	mStatus = SyncStatus.error;
        }
        
    	String lookupAuthToken(String password) {
    		if (authToken != null && System.currentTimeMillis() < authExpires && password.equals(authPassword))
    			return authToken;
    		authToken = null;
    		authExpires = 0;
    		return null;
    	}
    	
    	boolean reauthOK(String password) {
    		if (!password.equals(authPassword) || System.currentTimeMillis() - lastAuthFail > OfflineLC.zdesktop_reauth_delay.longValue())
    			return true;
    		return false;
    	}
    	
    	void authSuccess(String password, String token, long expires) {
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
    
    private Map<String, OfflineSyncStatus> syncStatusTable = Collections.synchronizedMap(new HashMap<String, OfflineSyncStatus>());
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
	
	public long getLastTryTime(String targetName) {
		synchronized (syncStatusTable) {
			return getStatus(targetName).mLastTryTime;
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
    	synchronized (syncStatusTable) {
    		getStatus(targetName).syncStart();
    	}
    	notifyStateChange();
    }
    
    public void syncComplete(String targetName) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).syncComplete();
    	}
    	notifyStateChange();
    }
    
    public void connecitonDown(String targetName) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).connecitonDown();
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
    
    public void authSuccess(String targetName, String password, String token, long expires) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).authSuccess(password, token, expires);
    	}
    }
    
    //
    // account auth
    //
    
	public String lookupAuthToken(Account account) {
		synchronized (syncStatusTable) {
			return getStatus(account.getName()).lookupAuthToken(((OfflineAccount)account).getRemotePassword());
		}
	}
	
	public boolean reauthOK(Account account) {
		synchronized (syncStatusTable) {
			return getStatus(account.getName()).reauthOK(((OfflineAccount)account).getRemotePassword());
		}
	}
	
	public void authSuccess(Account account, String token, long expires) {
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
	
	private boolean isAuthEerror(Exception exception) {
		return exception instanceof SoapFaultException &&
					((SoapFaultException)exception).getCode().equals(AccountServiceException.AUTH_FAILED) ||
			   exception instanceof ServiceException && exception.getCause() instanceof AuthenticationFailedException;
	}
	
    public void processSyncException(Account account, Exception exception) {
    	if (isAuthEerror(exception)) {
    		authFailed(account);
    	}
    	processSyncException(account.getName(), ((OfflineAccount)account).getRemotePassword(), exception);
    }
    
    public void processSyncException(DataSource dataSource, Exception exception) throws ServiceException {
    	if (isAuthEerror(exception)) {
    		authFailed(dataSource);
    	}
    	processSyncException(dataSource.getName(), dataSource.getDecryptedPassword(), exception);
    }
    
	private void processSyncException(String targetName, String password, Exception exception) {
		if (exception instanceof ServiceException) {
	        Throwable cause = exception.getCause();
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
	        if (cause instanceof java.net.UnknownHostException ||
		            cause instanceof java.net.NoRouteToHostException ||
		            cause instanceof java.net.SocketException ||
		        	cause instanceof java.net.SocketTimeoutException ||
		        	cause instanceof java.net.ConnectException ||
		        	cause instanceof org.apache.commons.httpclient.ConnectTimeoutException) {
	        	connecitonDown(targetName);
	        	OfflineLog.offline.info("sync connection down: " + targetName);
	        } else if (isAuthEerror(exception)) {
	        	authFailed(targetName, password);
	    		OfflineLog.offline.warn("sync remote auth failure: " + targetName);
	        } else {
	        	syncFailed(targetName, exception);
	        	OfflineLog.offline.error("sync failure: " + targetName, exception);
	        	if (exception instanceof SoapFaultException) {
	        		SoapFaultException x = (SoapFaultException)exception;
	        	    OfflineLog.offline.warn("SoapFaultException: " + x.getReason() + "\nFaultRequest:\n" + x.getFaultRequest() + "\nFaultResponse:\n" + x.getFaultResponse());
	        	}
	        }
		} else {
	        syncFailed(targetName, exception);
	        OfflineLog.offline.error("sync exception: " + targetName, exception);
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
	}
	
	/*
		<zdsync xmlns="urn:zimbraOffline">
		  <account name="foo@domain1.com" id="1234-5678">
			  <status>{STATUS}</status>
			  <lastsync>{LASTSYNC}</lastsync>
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
    		}
    	}
    }
}
