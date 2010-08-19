/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.mailbox;


import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.session.SoapSession;

import java.util.List;

public class OfflineMailboxManager extends MailboxManager {
    public static OfflineMailboxManager getOfflineInstance() throws ServiceException {
        return (OfflineMailboxManager)MailboxManager.getInstance();
    }
	
    public OfflineMailboxManager() throws ServiceException  {
        super();
    }

    /** Returns a new {@link ZcsMailbox} object to wrap the given data. */
    @Override
    protected Mailbox instantiateMailbox(MailboxData data) throws ServiceException {
    	return DesktopMailbox.newMailbox(data);
    }
    
//    public void syncAllMailboxes(boolean isOnRequest) throws ServiceException {
//        for (Account account : OfflineProvisioning.getOfflineInstance().getAllSyncAccounts()) {
//            try {
//                Mailbox mbox = getMailboxByAccount(account);
//                if (!(mbox instanceof OfflineMailbox)) {
//                    OfflineLog.offline.warn("cannot sync: not an OfflineMailbox for account " + mbox.getAccount().getName());
//                    continue;
//                }
//                ((OfflineMailbox)mbox).sync(isOnRequest);
//            } catch (ServiceException e) {
//                OfflineLog.offline.warn("cannot sync: error fetching mailbox/account for acct id " + account.getId(), e);
//            } catch (Exception t) {
//            	OfflineLog.offline.error("unexpected exception syncing account " + account.getId(), t);
//            }
//        }
//    }
    
    public void notifyAllMailboxes() throws ServiceException {
        for (String acctId : getAccountIds()) {
            OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
            Account acct = prov.get(Provisioning.AccountBy.id, acctId);
            if (acct == null || prov.isGalAccount(acct) || prov.isMountpointAccount(acct))
                continue;
                
            try {
                Mailbox mbox = getMailboxByAccountId(acctId);
            	//loop through mailbox sessions and signal hanging NoOps
            	List<Session> sessions = mbox.getListeners(Session.Type.SOAP);
            	for (Session session : sessions) {
            		if (session instanceof SoapSession)
            			((SoapSession)session).forcePush();
            	}
            } catch (ServiceException e) {
                OfflineLog.offline.warn("failed to notify mailbox account_id=" + acctId, e);
            } catch (Exception t) {
            	OfflineLog.offline.error("unexpected exception notifying mailbox account_id" + acctId, t);
            }
        }
    }

    private final Object getMailboxMonitor = new Object();
    @Override
    protected Mailbox getMailboxById(long mailboxId, FetchMode fetchMode,
            boolean skipMailHostCheck) throws ServiceException {
        synchronized(getMailboxMonitor) {
            //have to fake MailboxMonitor out, it does not want us to hold it's lock since it will serialize mb instantiation
            //but w/ SQLite we need to do just that to avoid reading db when another thread might have progressed on to sync
            return super.getMailboxById(mailboxId, fetchMode, skipMailHostCheck);
        }
    }

    public synchronized void purgeBadMailboxByAccountId(String accountId) throws ServiceException {
        long mailboxId = lookupMailboxId(accountId);
        try {
            MailboxData mbData = new MailboxData();
            mbData.accountId = accountId;
            mbData.id = mailboxId;
            mbData.schemaGroupId = mailboxId;
            Mailbox tempMb = new Mailbox(mbData);
            LocalMailbox mbox = (LocalMailbox) OfflineMailboxManager.getInstance().getMailboxByAccount(
                    ((OfflineProvisioning)Provisioning.getInstance()).getLocalAccount());
            mbox.forceDeleteMailbox(tempMb);
        } catch (Exception e) {
            OfflineLog.offline.error("failed to purge bad mailbox due to exception",e);
        }
    }
    
    
}
