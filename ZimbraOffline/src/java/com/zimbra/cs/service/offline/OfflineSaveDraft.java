package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.service.mail.SaveDraft;

public class OfflineSaveDraft extends SaveDraft {
    @Override
    public void preProxy(Element request, Map<String, Object> context) throws ServiceException {        
        OfflineProxyHelper.uploadAttachments(request, getZimbraSoapContext(context).getRequestedAccountId());
    }
}

