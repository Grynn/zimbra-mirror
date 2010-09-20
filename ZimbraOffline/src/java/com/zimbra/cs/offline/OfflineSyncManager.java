/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import javax.mail.AuthenticationFailedException;
import javax.security.auth.login.LoginException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.dom4j.QName;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.RemoteServiceException;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.service.ServiceException.Argument;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapHttpTransport;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ExceptionToString;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.offline.DirectorySync;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.ab.gab.GDataServiceException;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.common.OfflineConstants.SyncStatus;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;
import com.zimbra.cs.util.yauth.AuthenticationException;

public class OfflineSyncManager {

    private static final QName ZDSYNC_ZDSYNC = QName.get("zdsync", OfflineConstants.NAMESPACE);
    private static final QName ZDSYNC_ACCOUNT = QName.get("account", OfflineConstants.NAMESPACE);
    private static final QName ZDSYNC_ERROR = QName.get("error", OfflineConstants.NAMESPACE);
    private static final QName ZDSYNC_EXCEPTION = QName.get("exception", OfflineConstants.NAMESPACE);

    private static final String A_ZDSYNC_NAME = "name";
    private static final String A_ZDSYNC_ID = "id";
    private static final String A_ZDSYNC_STATUS = "status";
    private static final String A_ZDSYNC_LASTSYNC = "lastsync";
    private static final String A_ZDSYNC_ERRORCODE = "code";
    private static final String A_ZDSYNC_MESSAGE = "message";
    private static final String A_ZDSYNC_UNREAD = "unread";

    private static class SyncError {
        String message;
        Throwable t;

        SyncError(String message, Throwable t) {
            this.message = message;
            this.t = t;
        }

        void encode(Element e) {
            Element error = e.addElement(ZDSYNC_ERROR);
            if (message != null && message.length() > 0) {
                error.addAttribute(A_ZDSYNC_MESSAGE, message);
            }
            if (t != null) {
                error.addElement(ZDSYNC_EXCEPTION).setText(ExceptionToString.ToString(t));
            }
        }
    }

    private static class OfflineSyncStatus {
        String mStage;
        SyncStatus mStatus = SyncStatus.unknown;

        long mLastSyncTime = 0;
        long mLastFailTime = 0;
        int mRetryCount = 0;

        String mCode;
        SyncError mError;

        String authPassword;
        long lastAuthFail;
        ZAuthToken authToken; //null for data sources
        long authExpires; //0 for data sources

        boolean syncStart() {
            if (mStatus == SyncStatus.running)
                return false;
            mStatus = SyncStatus.running;
            mCode = null;
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
        
        void reset() {
            mCode = null;
            mError = null;
            mStage = null;
            mLastFailTime = mLastSyncTime = 0;
            mRetryCount = 0;
            mStatus = SyncStatus.unknown;
        }

        void resetLastSyncTime() {
            mLastSyncTime = System.currentTimeMillis();
        }

        void connectionDown(String code) {
            if (++mRetryCount >= OfflineLC.zdesktop_retry_limit.intValue()) {
                mLastFailTime = System.currentTimeMillis();
            }
            mCode = code;
            mStatus = SyncStatus.offline;
        }

        void syncFailed(String code, String message, Throwable t) {
            mLastFailTime = System.currentTimeMillis();
            mCode = code;
            mError = new SyncError(message, t);
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

        void clearAuthToken() {
            authToken = null;
            authExpires = 0;
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

        void authFailed(String code, String password) {
            authPassword = password;
            lastAuthFail = System.currentTimeMillis();
            authToken = null;
            authExpires = 0;
            mStatus = SyncStatus.authfail;
            mCode = code;
        }

        void encode(Element e) {
            e.addAttribute(A_ZDSYNC_STATUS, mStatus.toString());
            e.addAttribute(A_ZDSYNC_LASTSYNC, Long.toString(mLastSyncTime));
            if (mCode != null)
                e.addAttribute(A_ZDSYNC_ERRORCODE, mCode);
            if (mError != null) {
                mError.encode(e);
            }
        }

        SyncStatus getSyncStatus() {
            return mStatus;
        }

        void clearErrorCode() {
            mCode = null;
            mError = null;
            if (mStatus == SyncStatus.authfail || mStatus == SyncStatus.error || mStatus == SyncStatus.offline)
                mStatus = SyncStatus.unknown;
        }

        String getErrorMsg() {
            return mError == null ? null : mError.message;
        }

        String getException() {
            return mError == null || mError.t == null ? null : ExceptionToString.ToString(mError.t); 
        }
    }

    private final Map<String, OfflineSyncStatus> syncStatusTable = Collections.synchronizedMap(new HashMap<String, OfflineSyncStatus>());

    private OfflineSyncStatus getStatus(NamedEntry entry) {
        synchronized (syncStatusTable) {
            OfflineSyncStatus status = syncStatusTable.get(entry.getId());
            if (status == null) {
                status = new OfflineSyncStatus();
                syncStatusTable.put(entry.getId(), status);
            }
            return status;
        }
    }

    //
    // sync activity update
    //

    public String getErrorCode(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).mCode;
        }
    }

    public String getErrorMsg(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).getErrorMsg();
        }
    }

    public String getException(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).getException();
        }
    }

    public long getLastSyncTime(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).mLastSyncTime;
        }
    }

    public boolean isOnLine(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).mStatus == SyncStatus.online;
        }
    }

    public String getStage(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).mStage;
        }
    }

    public void setStage(NamedEntry entry, String stage) {
        synchronized (syncStatusTable) {
            getStatus(entry).mStage = stage;
        }
    }

    public void syncStart(NamedEntry entry) {
        boolean b;
        synchronized (syncStatusTable) {
            b = getStatus(entry).syncStart();
        }
        if (b)
            notifyStateChange();
    }

    public void syncComplete(NamedEntry entry) {
        boolean b;
        synchronized (syncStatusTable) {
            b = getStatus(entry).syncComplete();
        }
        if (b)
            notifyStateChange();
    }

    public void resetLastSyncTime(NamedEntry entry) {
        synchronized (syncStatusTable) {
            getStatus(entry).resetLastSyncTime();
        }        
    }

    private void connectionDown(NamedEntry entry, String code) {
        synchronized (syncStatusTable) {
            getStatus(entry).connectionDown(code);
        }
        notifyStateChange();
    }

    private void authFailed(NamedEntry entry, String code, String password) {
        synchronized (syncStatusTable) {
            getStatus(entry).authFailed(code, password);
        }
        notifyStateChange();
    }

    private void syncFailed(NamedEntry entry, String code, String message, Throwable t) {
        synchronized (syncStatusTable) {
            getStatus(entry).syncFailed(code, message, t);
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

    public void authSuccess(NamedEntry entry, String password, ZAuthToken token, long expires) {
        synchronized (syncStatusTable) {
            getStatus(entry).authSuccess(password, token, expires);
        }
    }

    //
    // account auth
    //

    public ZAuthToken lookupAuthToken(Account account) {
        synchronized (syncStatusTable) {
            return getStatus(account).lookupAuthToken(((OfflineAccount)account).getRemotePassword());
        }
    }

    public void clearAuthToken(Account account) {
        synchronized (syncStatusTable) {
            getStatus(account).clearAuthToken();
        }
    }

    public boolean reauthOK(Account account) {
        synchronized (syncStatusTable) {
            return getStatus(account).reauthOK(((OfflineAccount)account).getRemotePassword());
        }
    }

    public boolean retryOK(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).retryOK();
        }	    
    }

    public void authSuccess(Account account, ZAuthToken token, long expires) {
        authSuccess(account, ((OfflineAccount)account).getRemotePassword(), token, expires);
    }

    //
    // data source auth
    //

    public boolean reauthOK(DataSource dataSource) throws ServiceException {
        synchronized (syncStatusTable) {
            return getStatus(dataSource).reauthOK(dataSource.getDecryptedPassword());
        }
    }

    public void authSuccess(DataSource dataSource) throws ServiceException {
        authSuccess(dataSource, dataSource.getDecryptedPassword(), null, 0);
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
        Throwable cause = SystemUtil.getInnermostException(exception);
        return cause instanceof AuthenticationFailedException ||
            cause instanceof AuthenticationException ||
            cause instanceof com.google.gdata.util.AuthenticationException ||
            cause instanceof LoginException;
    }

    public static boolean isConnectionDown(Exception exception) {
        if (exception instanceof SoapFaultException && ((SoapFaultException)exception).getCode().equals(MailServiceException.MAINTENANCE))
            return true;

        if (exception instanceof ServiceException) {
            ServiceException e = (ServiceException)exception;
            if (e.getCode().equals(ServiceException.RESOURCE_UNREACHABLE)) {
                if (e.getArgs() != null)
                    for (Argument arg : e.getArgs())
                        if (UserServlet.HTTP_STATUS_CODE.equals(arg.mName) && arg.mValue.startsWith("5"))
                            return false;
                return true;
            }
        }
        if (getInstance().isConnectionDown())
            return true;

        Throwable cause = SystemUtil.getInnermostException(exception);
        return cause instanceof java.net.UnknownHostException ||
            cause instanceof java.net.NoRouteToHostException ||
            cause instanceof java.net.SocketException ||
            cause instanceof java.net.SocketTimeoutException ||
            cause instanceof java.net.ConnectException ||
            cause instanceof org.apache.commons.httpclient.ConnectTimeoutException ||
            cause instanceof org.apache.commons.httpclient.NoHttpResponseException;
    }

    public static boolean isIOException(Exception exception) {
        Throwable cause = SystemUtil.getInnermostException(exception);
        return cause instanceof IOException;
    }

    public static boolean isMailboxInMaintenance(Exception exception) {
        return exception instanceof ServiceException && ((ServiceException)exception).getCode().equals(MailServiceException.MAINTENANCE);
    }

    public static boolean isDbShutdown(Exception exception) {
        Throwable e = SystemUtil.getInnermostException(exception);
        if (e instanceof RuntimeException) {
            String msg = e.getMessage();
            return msg != null && msg.equals("DbPool permanently shutdown");
        }
        return false;
    }

    public void processSyncException(Account account, Exception exception) {
        processSyncException(account, ((OfflineAccount)account).getRemotePassword(), exception, ((OfflineAccount)account).isDebugTraceEnabled());
    }

    public void processSyncException(Account account, Exception exception, boolean markSyncFail) {
        processSyncException(account, ((OfflineAccount)account).getRemotePassword(), exception, ((OfflineAccount)account).isDebugTraceEnabled(), markSyncFail);
    }

    public void processSyncException(DataSource dataSource, Exception exception) throws ServiceException {
        processSyncException(dataSource, dataSource.getDecryptedPassword(), exception, dataSource.isDebugTraceEnabled());
    }

    public void processSyncException(NamedEntry entry, String password, Exception exception, boolean isDebugTraceOn) {
        processSyncException(entry, password, exception, isDebugTraceOn, true);
    }

    private void processSyncException(NamedEntry entry, String password, Exception exception, boolean isDebugTraceOn, boolean markSyncFail) {
        Throwable cause = SystemUtil.getInnermostException(exception);
        String code = null;
        
        if (cause instanceof ServiceException)
            code = ((ServiceException)cause).getCode();
        else if (cause instanceof com.google.gdata.util.ServiceException)
            GDataServiceException.getErrorCode((com.google.gdata.util.ServiceException)cause);
        else if (cause instanceof LoginException)
            code = RemoteServiceException.AUTH_FAILURE;
        else
            code = RemoteServiceException.getErrorCode(cause);

        if (ZimbraApplication.getInstance().isShutdown()) {
            OfflineLog.offline.info("sync aborted by shutdown: " + entry.getName());
        } else if (!isServiceActive()) {
            OfflineLog.offline.info("sync aborted by network: " + entry.getName());
        } else if (isConnectionDown(exception)) {
            connectionDown(entry, null);
            if (isDebugTraceOn)
                OfflineLog.offline.debug("sync connection down: " + entry.getName(), exception);
            else
                OfflineLog.offline.info("sync connection down: " + entry.getName());
        } else if (isAuthError(exception)) {
            authFailed(entry, code, password);
            if (isDebugTraceOn)
                OfflineLog.offline.debug("sync remote auth failure: " + entry.getName(), exception);
            else
                OfflineLog.offline.warn("sync remote auth failure: " + entry.getName());
        } else {
            code = code == null ? OfflineServiceException.UNEXPECTED : code;
            if (markSyncFail)
                syncFailed(entry, code, cause.getMessage(), exception);
            OfflineLog.offline.error("sync failure: " + entry.getName(), exception);
            if (exception instanceof SoapFaultException) {
                SoapFaultException x = (SoapFaultException)exception;
                OfflineLog.offline.warn("SoapFaultException: " + x.getReason() + "\nFaultRequest:\n" + x.getFaultRequest() + "\nFaultResponse:\n" + x.getFaultResponse());
            }
        }
    }
    
    public void processSyncError(NamedEntry entry, Error error) {
        syncFailed(entry, OfflineServiceException.UNEXPECTED, error.getMessage(), error);
        OfflineLog.offline.error("sync failure: " + entry.getName(), error);
    }

    public SyncStatus getSyncStatus(NamedEntry entry) {
        synchronized (syncStatusTable) {
            return getStatus(entry).getSyncStatus();
        }
    }

    public void resetStatus(NamedEntry entry) {
        synchronized (syncStatusTable) {
            syncStatusTable.remove(entry.getId());
        }
    }

    public void clearErrorCode(NamedEntry entry) {
        synchronized (syncStatusTable) {
            getStatus(entry).clearErrorCode();
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

    private boolean isConnectionDown = false, isServiceUp = false, isUiLoading = false;
    private Lock lock = new ReentrantLock();
    private Condition waiting  = lock.newCondition(); 

    public synchronized boolean isConnectionDown() {
        return isConnectionDown;
    }

    public synchronized boolean isServiceActive() {
        return isServiceActive(false);
    }
    
    public synchronized boolean isServiceActive(boolean isOnRequest) {
        return isServiceUp && (isOnRequest || !isConnectionDown) &&
            !ZimbraApplication.getInstance().isShutdown() && !isUiLoading;
    }

    public synchronized void setConnectionDown(boolean b) {
        isConnectionDown = b;
        OfflineLog.offline.info("setting connection status to " + (b ? "down" : "up"));
        for (OfflineSyncStatus ss : syncStatusTable.values()) {
            if (ss.getSyncStatus() != SyncStatus.authfail &&
                ss.getSyncStatus() != SyncStatus.error) {
                if (b)
                    ss.connectionDown(null);
                else
                    ss.reset();
            }
        }
        notifyStateChange();
        lock.lock();
        if (!isConnectionDown)
            waiting.signalAll();
        lock.unlock();
    }

    public synchronized void setUILoading(boolean b) {
        lock.lock();
        isUiLoading = b;
        if (!isUiLoading)
            waiting.signalAll();
        lock.unlock();
    }

    public synchronized void shutdown() {
        lock.lock();
        waiting.signalAll();
        lock.unlock();
        try {
            Thread.sleep(250);
        } catch (Exception e) {
        }
    }

    public void init() throws ServiceException {
        new Thread(new Runnable() {
            public void run() {
                backgroundInit();
            }
        }, "sync-manager-init").start();
    }

    public void continueOK() throws ServiceException {
        try {
            lock.lock();
            if (isUiLoading) {
                OfflineLog.offline.info("ui loading - sync paused");
                if (!waiting.await(30, TimeUnit.SECONDS)) {
                    OfflineLog.offline.warn("ui loading in progress for 30 seconds - sync resuming");
                    isUiLoading = false;
                    waiting.signalAll();
                }
            }
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
        if (isConnectionDown)
            throw ServiceException.INTERRUPTED("network down - sync cancelled");
        else if (ZimbraApplication.getInstance().isShutdown())
            throw ServiceException.INTERRUPTED("system shutting down");
    }

    private synchronized void backgroundInit() {
        String uri = LC.zimbra_admin_service_scheme.value() + "127.0.0.1"+ ":" + LC.zimbra_admin_service_port.value() +
        AdminConstants.ADMIN_SERVICE_URI;
        final int LOOP_COUNT = 24 * 10;
        int loop = 0;

        while (loop++ < LOOP_COUNT) {
            try {
                SoapHttpTransport transport = new SoapHttpTransport(uri);
                transport.setUserAgent(OfflineLC.zdesktop_name.value(), OfflineLC.getFullVersion());
                transport.setTimeout(3000);
                transport.setRetryCount(1);
                transport.setRequestProtocol(SoapProtocol.Soap12);
                transport.setResponseProtocol(SoapProtocol.Soap12);

                Element request = new Element.XMLElement(AdminConstants.PING_REQUEST);
                transport.invokeWithoutSession(request.detach());
                OfflineLog.offline.info("service port is ready.");
                isServiceUp = true;
                break;
            } catch (Exception x) {
                if (x instanceof ConnectException || x instanceof SocketTimeoutException || x instanceof ConnectTimeoutException) {
                    if (loop % 10 == 1)
                        OfflineLog.offline.info("waiting for service port");
                } else if (x instanceof NoRouteToHostException || x instanceof PortUnreachableException) {
                    OfflineLog.offline.warn("service host or port unreachable - retrying...", x);
                } else {
                    OfflineLog.offline.warn("service port check failed - retrying...", x);
                }
            }
            try {
                Thread.sleep(250); // avoid potential tight loop
            } catch (InterruptedException e) {}
        }
        if (loop == LOOP_COUNT)
            Zimbra.halt("Zimbra Desktop Service failed to initialize. Shutting down...");

        //load all mailboxes so that timers are kicked off
        String[] toSkip = OfflineLC.zdesktop_sync_skip_idlist.value().split("\\s*,\\s*");
        for (String s : toSkip) {
            try {
                toSkipList.add(Integer.parseInt(s));
            } catch (NumberFormatException x) {
                if (s.length() > 0)
                    OfflineLog.offline.warn("invaid item id %s in zdesktop_sync_skip_idlist", s);
            }
        }

        try {
            OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
            List<Account> dsAccounts = prov.getAllDataSourceAccounts();
            for (Account dsAccount : dsAccounts) {
                try {
                    MailboxManager.getInstance().getMailboxByAccount(dsAccount);
                }
                catch (Exception e) {
                    OfflineLog.offline.error("Failed to initialize account ["+dsAccount+"] due to exception",e);
                    markAccountSyncDisabled(dsAccount, e);
                }
            }
            List<Account> syncAccounts = prov.getAllZcsAccounts();
            for (Account syncAccount : syncAccounts) {
                try {
                    MailboxManager.getInstance().getMailboxByAccount(syncAccount);
                }
                catch (Exception e) {
                    OfflineLog.offline.error("Failed to initialize account ["+syncAccount+"] due to exception",e);
                    markAccountSyncDisabled(syncAccount, e);
                }
            }
            DirectorySync.getInstance();

            //deal with left over mailboxes from interrupted delete/reset
            long[] mids = MailboxManager.getInstance().getMailboxIds();
            for (long mid : mids) {
                try {
                    MailboxManager.getInstance().getMailboxById(mid, true);
                } catch (ServiceException x) {
                    OfflineLog.offline.warn("failed to load mailbox id=%d", mid, x);
                }
            }
        } catch (Exception e) {
            Zimbra.halt("Zimbra Desktop failed to initialize accounts. Shutting down...", e);
        }
    }

    /*
		<zdsync xmlns="urn:zimbraOffline">
		  <account name="foo@domain1.com" id="1234-5678" status="online" [code="{CODE}"] lastsync="1234567" unread="32">
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
            Mailbox mb = null;
            try {
                mb = MailboxManager.getInstance().getMailboxByAccount(account);
            } catch (Exception e) {
                OfflineLog.offline.error("exception fetching mailbox for account ["+account+"]",e);
                markAccountSyncDisabled(account, e);
                continue;
            }
            if (!(account instanceof OfflineAccount) || prov.isLocalAccount(account))
                continue;

            Element e = zdsync.addElement(ZDSYNC_ACCOUNT).addAttribute(A_ZDSYNC_NAME, account.getName()).addAttribute(A_ZDSYNC_ID, account.getId());
            if (prov.isZcsAccount(account))
                getStatus(account).encode(e);
            else if (OfflineProvisioning.isDataSourceAccount(account))
                getStatus(prov.getDataSource(account)).encode(e);
            else {
                e.detach();
                OfflineLog.offline.warn("Invalid account: " + account.getName());
                continue;
            }
            e.addAttribute(A_ZDSYNC_UNREAD, mb.getFolderById(null, Mailbox.ID_FOLDER_INBOX).getUnreadCount());
        }
    }

    private void markAccountSyncDisabled(Account account, Exception e) {
        if (account instanceof OfflineAccount) {
            ((OfflineAccount) account).setDisabledDueToError(true);    
            processSyncException(account, ((OfflineAccount)account).getRemotePassword(), e, ((OfflineAccount)account).isDebugTraceEnabled(), true);
        } else {
            OfflineLog.offline.warn("cannot mark non-offline account as disabled sync.");
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
