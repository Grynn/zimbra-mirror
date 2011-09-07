package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.GalSync;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineResetGal extends AdminDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        String accountId = request.getAttribute(AdminConstants.E_ID);
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        OfflineAccount account = (OfflineAccount) prov.get(AccountBy.id, accountId);
        ZcsMailbox mbox = (ZcsMailbox) OfflineMailboxManager.getInstance().getMailboxByAccount(account);

        boolean isGalFullSynced = GalSync.isFullSynced(account);
        if (account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled, false)
                && account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled, false)) {
            if (isGalFullSynced) {
                String galAccountId = account.getAttr(OfflineConstants.A_offlineGalAccountId, false);
                Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAccountId, false);
                OfflineAccount galAccount = (OfflineAccount) prov.get(AccountBy.id, galAccountId);

                boolean isReset = GalSync.getInstance().resetGal(mbox, galMbox, account, galAccount);
                if (!isReset) {
                    OfflineLog.offline.debug("reseting gal for account %s -- Skipped because GAL is recently synced",
                            account.getName());
                }
            } else {
                OfflineLog.offline.debug(
                        "reseting gal for account %s -- Skipped because GAL has not finished initial sync",
                        account.getName());
            }
        } else {
            OfflineLog.offline.debug("Offline GAL sync is disabled for %s, resetting skipped." + account.getName());
        }

        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element resp = zsc.createElement(OfflineConstants.RESET_GAL_ACCOUNT_RESPONSE);
        return resp;
    }
}
