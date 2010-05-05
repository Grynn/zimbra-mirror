package com.zimbra.cs.service.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.service.mail.SendDeliveryReport;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author vmahajan
 */
public class OfflineSendDeliveryReport extends SendDeliveryReport {

    @Override
    protected Account getSenderAccount(ZimbraSoapContext zsc) throws ServiceException {
        return getRequestedAccount(zsc);
    }
}
