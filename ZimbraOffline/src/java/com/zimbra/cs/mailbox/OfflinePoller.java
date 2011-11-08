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
package com.zimbra.cs.mailbox;

import java.net.SocketTimeoutException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.service.admin.AdminServiceException;

public final class OfflinePoller implements Runnable {

    private ZcsMailbox ombx;

    private String setId;
    private String lastSequence;
    private String lastKnownToken;

    private boolean isPolling;
    private boolean hasChanges;

    OfflinePoller(ZcsMailbox ombx) {
        this.ombx = ombx;
        Thread t = new Thread(this, "offline-poller-" + ombx.getAccountName());
        t.setDaemon(true); // no obvious shutdown hook point
        t.start();
    }

    public synchronized boolean hasChanges(String lastKnownToken) {
        if (hasChanges) {
            hasChanges = false;
            return true;
        }
        if (!isPolling) {
            this.lastKnownToken = lastKnownToken;
            isPolling = true;
            notify();
        }
        return false;
    }

    public void run() {
        while (true) {
            synchronized (this) {
                isPolling = false;
                while (!isPolling)
                    try {
                        wait();
                    } catch (InterruptedException x) {
                    }
            }
            runPoll();
        }
    }

    private void runPoll() {
        try {
            if (setId == null)
                createWaitset();
            waitsetRequest();
        } catch (Exception x) {
            try {
                if (!SyncExceptionHandler.isCausedBy(x, SocketTimeoutException.class)) { // workaround for bug #55186, see bug #56686
                    OfflineSyncManager.getInstance().processSyncException(ombx.getAccount(), x);
                }
            } catch (Exception e) {
                OfflineLog.offline.error("unexpected exception", e);
            }
        }
    }

    private void createWaitset() throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.CREATE_WAIT_SET_REQUEST);
        request.addAttribute(MailConstants.A_DEFTYPES, "all");
        Element account = request.addElement(MailConstants.E_WAITSET_ADD).addElement(MailConstants.E_A);
        account.addAttribute(MailConstants.A_ID, ombx.getAccountId());
        synchronized (this) {
            account.addAttribute(MailConstants.A_TOKEN, "" + lastKnownToken);
        }

        Element response = ombx.sendRequest(request, true);

        synchronized (this) {
            if (hasError(response)) {
                hasChanges = true; // when there's error force a sync
                return;
            }
        }

        setId = response.getAttribute(MailConstants.A_WAITSET_ID);
        lastSequence = response.getAttribute(MailConstants.A_SEQ);
    }

    private void waitsetRequest() throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.WAIT_SET_REQUEST);
        request.addAttribute(MailConstants.A_WAITSET_ID, setId);
        request.addAttribute(MailConstants.A_DEFTYPES, "all");
        request.addAttribute(MailConstants.A_SEQ, lastSequence);
        request.addAttribute(MailConstants.A_BLOCK, "1");
        request.addAttribute(MailConstants.A_TIMEOUT, "300");

        Element account = request.addElement(MailConstants.E_WAITSET_UPDATE).addElement(MailConstants.E_A);
        account.addAttribute(MailConstants.A_ID, ombx.getAccountId());
        synchronized (this) {
            account.addAttribute(MailConstants.A_TOKEN, "" + lastKnownToken);
        }

        Element response = null;
        try {
            response = ombx.sendRequest(request, true, true, 1 * Constants.SECONDS_PER_MINUTE * 1000); // will block
        } catch (ServiceException x) {
            if (AdminServiceException.NO_SUCH_WAITSET.equals(x.getCode())
                    || ServiceException.PERM_DENIED.equals(x.getCode())
                    || ServiceException.PROXY_ERROR.equals(x.getCode())) {
                if (!SyncExceptionHandler.isCausedBy(x, SocketTimeoutException.class)) { // we only care about those not caused by socket-time-out
                    OfflineLog.offline.debug("failed sending request in waitsetRequest(), error code: %s", x.getCode(), x);    
                }
                setId = null;
                lastSequence = null;
                return;
            } else
                throw x;
        }

        lastSequence = response.getAttribute(MailConstants.A_SEQ, lastSequence);

        synchronized (this) {
            if (hasError(response)) {
                hasChanges = true; // when there's error force a sync
                return;
            }

            account = response.getOptionalElement(MailConstants.E_A);
            if (account != null && account.getAttribute(MailConstants.A_ID).equals(ombx.getAccountId()))
                hasChanges = true;
        }
    }

    private boolean hasError(Element response) throws ServiceException {
        Element error = response.getOptionalElement(MailConstants.E_ERROR);
        if (error != null) {
            OfflineLog.offline.warn("waitset error account=%s type=%s", error.getAttribute(MailConstants.A_ID),
                    error.getAttribute(MailConstants.A_TYPE));
            return true;
        }
        return false;
    }
}
