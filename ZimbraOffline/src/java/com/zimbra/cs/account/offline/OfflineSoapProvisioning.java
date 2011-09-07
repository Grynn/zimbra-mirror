package com.zimbra.cs.account.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.offline.common.OfflineConstants;

public class OfflineSoapProvisioning extends SoapProvisioning {

    public void resetGal(String accountId) throws ServiceException {
        XMLElement req = new XMLElement(OfflineConstants.RESET_GAL_ACCOUNT_REQUEST);
        req.addElement(AdminConstants.E_ID).setText(accountId);
        invoke(req);
    }
}
