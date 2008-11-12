package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.service.mail.ModifyTask;

public class OfflineModifyTask extends ModifyTask {
    @Override
    public void preProxy(Element request, Map<String, Object> context) throws ServiceException {        
        OfflineCalendarProxy.uploadAttachments(request, getZimbraSoapContext(context).getRequestedAccountId());
    }
}
