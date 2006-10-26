/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;
import com.zimbra.cs.service.ServiceException;

public class OfflineMailboxManager extends MailboxManager {

    public OfflineMailboxManager() throws ServiceException  {
        super();
    }


    @Override
    Mailbox instantiateMailbox(MailboxData data) throws ServiceException {
        Account local = Provisioning.getInstance().get(AccountBy.id, data.accountId);
        if (local == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(data.accountId);
        String passwd = local.getAttr(OfflineProvisioning.A_offlineRemotePassword);
        String uri = local.getAttr(OfflineProvisioning.A_offlineRemoteServerUri);
        return new OfflineMailbox(data, data.accountId, passwd, uri);

//        Account local = Provisioning.getInstance().get(AccountBy.id, data.accountId);
//        if (local == null || !local.getName().startsWith("user4"))
//            return super.instantiateMailbox(data);
//
//        Account remote = Provisioning.getInstance().get(AccountBy.name, "user1");
//        String remoteId = remote.getId();
//        String password = "test123";
//        String url = getLocalServerUri();
//        return new OfflineMailbox(data, remoteId, password, url);
    }

//      private String getLocalServerUri() throws ServiceException {
//          Server server = Provisioning.getInstance().getLocalServer();
//          String scheme = "http";
//          String hostname = server.getAttr(Provisioning.A_zimbraServiceHostname);
//          int port = server.getIntAttr(Provisioning.A_zimbraMailPort, 0);
//          if (port <= 0) {
//              port = server.getIntAttr(Provisioning.A_zimbraMailSSLPort, 0);
//              if (port <= 0)
//                  throw ServiceException.FAILURE("remote server " + server.getName() + " has neither http nor https port enabled", null);
//              scheme = "https";
//          }
// 
//          return scheme + "://" + hostname + ':' + port;
//      }
}
