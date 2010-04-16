package com.zimbra.cs.service.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.service.mail.SendInviteReply;

/**
 * @author vmahajan
 */
public class OfflineSendInviteReply extends SendInviteReply {

    @Override
    protected boolean deleteInviteOnReply(Account acct) throws ServiceException {
        return OfflineProvisioning.getOfflineInstance().getLocalAccount().getBooleanAttr(Provisioning.A_zimbraPrefDeleteInviteOnReply, true);
    }
}
