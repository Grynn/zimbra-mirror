/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;


import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.session.SoapSession;

public class OfflineMailboxManager extends MailboxManager {
	
	public static OfflineMailboxManager getOfflineInstance() throws ServiceException {
		return (OfflineMailboxManager)MailboxManager.getInstance();
	}
	
    public OfflineMailboxManager() throws ServiceException  {
        super();
    }

    /** Returns a new {@link OfflineMailbox} object to wrap the given data. */
    @Override
    Mailbox instantiateMailbox(MailboxData data) throws ServiceException {
    	OfflineAccount account = (OfflineAccount)Provisioning.getInstance().get(AccountBy.id, data.accountId);
    	if (account.isLocal()) {
    		return new LocalMailbox(data);
    	}
        return new OfflineMailbox(data);
    }
    
    public void syncAllMailboxes(boolean isOnRequest) {
        for (String acctId : getAccountIds()) {
        	if (acctId.equals(OfflineProvisioning.LOCAL_ACCOUNT_ID))
        		continue;
            try {
                Mailbox mbox = getMailboxByAccountId(acctId);
                if (!(mbox instanceof OfflineMailbox)) {
                    OfflineLog.offline.warn("cannot sync: not an OfflineMailbox for account " + mbox.getAccount().getName());
                    continue;
                }
                ((OfflineMailbox)mbox).sync(isOnRequest);
            } catch (ServiceException e) {
                OfflineLog.offline.warn("cannot sync: error fetching mailbox/account for acct id " + acctId, e);
            } catch (Throwable t) {
            	OfflineLog.offline.error("unexpected exception syncing account " + acctId, t);
            }
        }
    }
    
    public void notifyAllMailboxes() {
        for (String acctId : getAccountIds()) {
            try {
                Mailbox mbox = getMailboxByAccountId(acctId);
            	//loop through mailbox sessions and signal hanging NoOps
            	List<Session> sessions = mbox.getListeners(Session.Type.SOAP);
            	for (Session session : sessions) {
            		((SoapSession)session).forcePush();
            	}
            } catch (ServiceException e) {
                OfflineLog.offline.warn("failed to notify mailbox account_id=" + acctId, e);
            } catch (Throwable t) {
            	OfflineLog.offline.error("unexpected exception notifying mailbox account_id" + acctId, t);
            }
        }
    }
}
