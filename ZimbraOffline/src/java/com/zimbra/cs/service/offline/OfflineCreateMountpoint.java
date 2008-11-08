package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.redolog.op.CreateMountpoint;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineCreateMountpoint extends OfflineServiceProxy {

    public OfflineCreateMountpoint() {
        super("create mountpoint", false, false);
    }
    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof OfflineMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
                       
        Element response = super.handle(request, context);
        
        Element eMount = response.getElement(MailConstants.E_MOUNT);
        int parentId = (int) eMount.getAttributeLong(MailConstants.A_FOLDER);
        int id = (int) eMount.getAttributeLong(MailConstants.A_ID);
        String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : MailItem.normalizeItemName(eMount.getAttribute(MailConstants.A_NAME));
        int flags = Flag.flagsToBitmask(eMount.getAttribute(MailConstants.A_FLAGS, null));
        byte color = (byte) eMount.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        byte view = MailItem.getTypeForName(eMount.getAttribute(MailConstants.A_DEFAULT_VIEW, null));
        String ownerId = eMount.getAttribute(MailConstants.A_ZIMBRA_ID);
        String ownerName = eMount.getAttribute(MailConstants.A_OWNER_NAME);
        int remoteId = (int) eMount.getAttributeLong(MailConstants.A_REMOTE_ID);
        int mod_content = (int) eMount.getAttributeLong(MailConstants.A_REVISION, -1);
        
        CreateMountpoint redo = new CreateMountpoint(mbox.getId(), parentId, name, ownerId, remoteId, view, flags, color);
        redo.setId(id);
        redo.setChangeId(mod_content);
        try {
            mbox.createMountpoint(new OfflineContext(redo), parentId, name, ownerId, remoteId, view, flags, color);           
            OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
            if (prov.get(Provisioning.AccountBy.id, ownerId) != null)
                prov.deleteAccount(ownerId);
            OfflineAccount account = ((OfflineMailbox)mbox).getOfflineAccount();
            prov.createMountpointAccount(ownerName, ownerId, account); 
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
        }
        
        return response;
    }
}