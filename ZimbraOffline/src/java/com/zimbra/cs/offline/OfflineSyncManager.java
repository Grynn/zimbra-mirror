package com.zimbra.cs.offline;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.dom4j.QName;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ExceptionToString;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.LocalMailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.service.offline.OfflineService;
import com.zimbra.cs.util.Zimbra;

public class OfflineSyncManager {
	
    private static final QName ZDSYNC_ZDSYNC = QName.get("zdsync", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_ACCOUNT = QName.get("account", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_DATASOURCE = QName.get("datasource", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_ERROR = QName.get("error", OfflineService.NAMESPACE);
    private static final QName ZDSYNC_EXCEPTION = QName.get("exception", OfflineService.NAMESPACE);
    
    private static final String A_ZDSYNC_NAME = "name";
    private static final String A_ZDSYNC_STAGE = "stage";
    private static final String A_ZDSYNC_STATE = "state";
    private static final String A_ZDSYNC_LASTSYNC = "lastsync";
    private static final String A_ZDSYNC_LASTTRY = "lasttry";
    private static final String A_ZDSYNC_CODE = "code";
    private static final String A_ZDSYNC_MESSAGE = "message";
    
    
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
    	
    	void encode(Element e) {
    		Element error = e.addElement(ZDSYNC_ERROR);
    		error.addAttribute(A_ZDSYNC_CODE, code.toString());
    		if (message != null && message.length() > 0) {
    			error.addAttribute(A_ZDSYNC_MESSAGE, message);
    		}
    		if (exception != null) {
    			error.addElement(ZDSYNC_EXCEPTION).setText(ExceptionToString.ToString(exception));
    		}
    	}
    }
    

    private static class SyncStatus {
        String mStage;
        SyncState mState = SyncState.OFFLINE;
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
        	mState = SyncState.RUNNING;
        	mError = null;
        }
        
        void syncComplete() {
        	mLastSyncTime = mLastTryTime = System.currentTimeMillis();
        	mState = SyncState.ONLINE;
        }
        
        void connecitonDown() {
        	if (++mRetryCount >= OfflineLC.zdesktop_retry_limit.intValue()) {
        		mRetryCount = 0;
        		mLastTryTime = System.currentTimeMillis();
        	}
        	mState = SyncState.OFFLINE;
        }
        
        void syncFailed(ErrorCode code, String message, Exception exception) {
        	mLastTryTime = System.currentTimeMillis();
        	mError = new SyncError(code, message, exception);
        	mState = SyncState.ERROR;
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
    	}
    	
    	void authFailed(String password) {
    		authPassword = password;
    		lastAuthFail = System.currentTimeMillis();
    		authToken = null;
    		authExpires = 0;
    	}
    	
    	void encode(Element e) {
    		if (mStage != null) {
    			e.addAttribute(A_ZDSYNC_STAGE, mStage);
    		}
    		e.addAttribute(A_ZDSYNC_STATE, mState.toString());
        	e.addAttribute(A_ZDSYNC_LASTSYNC, Long.toString(mLastSyncTime));
        	e.addAttribute(A_ZDSYNC_LASTTRY, Long.toString(mLastTryTime));
        	if (mError != null) {
        		mError.encode(e);
        	}
    	}
    }
    
    private Map<String, SyncStatus> syncStatusTable = Collections.synchronizedMap(new HashMap<String, SyncStatus>());
	private SyncStatus getStatus(String targetName) {
		synchronized (syncStatusTable) {
			SyncStatus status = syncStatusTable.get(targetName);
			if (status == null) {
				status = new SyncStatus();
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
			return getStatus(targetName).mState == SyncState.ONLINE;
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
    
    private void syncFailed(String targetName, Exception exception) {
    	syncFailed(targetName, null, exception);
    }
    
    private void syncFailed(String targetName, String message, Exception exception) {
    	syncFailed(targetName, ErrorCode.UNKNOWN, message, exception);
    }
    
    private void syncFailed(String targetName, ErrorCode code, String message, Exception exception) {
    	synchronized (syncStatusTable) {
    		getStatus(targetName).syncFailed(code, message, exception);
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
	
	public void authFailed(Account account) {
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
    	processSyncException(account.getName(), exception);
    }
    
    public void processSyncException(DataSource dataSource, Exception exception) throws ServiceException {
    	if (isAuthEerror(exception)) {
    		authFailed(dataSource);
    	}
    	processSyncException(dataSource.getName(), exception);
    }
    
	private void processSyncException(String targetName, Exception exception) {
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
	        	syncFailed(targetName, ErrorCode.REMOTEAUTH, "remote auth failure", null);
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
	
	private static OfflineSyncManager instance = new OfflineSyncManager();
	public static OfflineSyncManager getInstance() {
		return instance;
	}
	
	private Set<Integer> toSkipList = new HashSet<Integer>();
	public boolean isInSkipList(int itemId) {
		return toSkipList.contains(itemId);
	}
	
	private Timer sTimer = new Timer("OfflineSyncManager-Timer", true);
	public void init() {
		String[] toSkip = OfflineLC.zdesktop_sync_skip_idlist.value().split("\\s*,\\s*");
		for (String s : toSkip) {
			try {
				toSkipList.add(Integer.parseInt(s));
			} catch (NumberFormatException x) {
				if (s.length() > 0)
					OfflineLog.offline.warn("Invaid item id %s in zdesktop_sync_skip_idlist", s);
			}
		}
		
		sTimer.schedule(
				new TimerTask() {
					@Override
					public void run() {
						try {
							syncAllOnTimer();
						} catch (Throwable e) { //don't let exceptions kill the timer
							if (e instanceof OutOfMemoryError)
								Zimbra.halt("Caught out of memory error", e);
							OfflineLog.offline.warn("Caught exception in timer ", e);
						}
					}
				},
				5 * Constants.MILLIS_PER_SECOND,
				OfflineLC.zdesktop_sync_timer_frequency.longValue());
	}

	//sync all accounts and data sources
	private void syncAllOnTimer() {
		try {
	    	OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
            List<Account> dsAccounts = prov.getAllDataSourceAccounts();
            for (Account dsAccount : dsAccounts) {
                LocalMailbox dsMbox = (LocalMailbox)MailboxManager.getInstance().getMailboxByAccount(dsAccount);
	    	    dsMbox.sync(false);
            }
			
			OfflineProvisioning.getOfflineInstance().syncAllAccounts(false);
			
			OfflineMailboxManager.getOfflineInstance().syncAllMailboxes(false);
		} catch (Exception x) {
			OfflineLog.offline.error("exception encountered during sync", x);
		}
	}
	
	/*
		<zdsync xmlns="urn:zimbraOffline">
		  <account name="foo@domain1.com">
			  <stage>{STAGE}</stage>
			  <state>{STATE}</state>
			  <lastsync>{LASTSYNC}</lastsync>
			  <lasttry>{LASTTRY}</lasttry>
			  [<error>
			    <code>{CODE}</code>
			    [<message>{MESSAGE}</message>]
			    [<exception>{EXCEPTION}</exception>]
			  </error>]
		  </account>
		  <account name="bar@domain2.com">
		     ...
		  </account>
		  <datasource name="GmailPop">
		     ...
		  </datasource>
		  <datasource name="YahooMail">
		     ...
		  </datasource>
		</zdsync>
	 */
    public void encode(Element context) {
    	Element zdsync = context.addUniqueElement(ZDSYNC_ZDSYNC);
    	try {
    		OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
	        for (Account acct : prov.getAllSyncAccounts()) {
	        	String user = acct.getName();
	        	Element e = zdsync.addElement(ZDSYNC_ACCOUNT).addAttribute(A_ZDSYNC_NAME, user);
	        	getStatus(user).encode(e);
	        }
			List<DataSource> dataSources = prov.getAllDataSources();
			for (DataSource ds : dataSources) {
				String user = ds.getName();
	        	Element e = zdsync.addElement(ZDSYNC_DATASOURCE).addAttribute(A_ZDSYNC_NAME, user);
	        	getStatus(user).encode(e);
			}
    	} catch (ServiceException x) {
    		zdsync.detach();
    		OfflineLog.offline.warn(x);
    	}
    }
}
