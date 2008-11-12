package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.service.util.ItemId;

public class OfflineSaveDocument extends OfflineDocumentHandlers.SaveDocument {
    
    @Override
    protected Element proxyRequest(Element request, Map<String, Object> context, ItemId iidRequested, ItemId iidResolved)
        throws ServiceException {
        Element eUpload = request.getElement(MailConstants.E_DOC).getElement(MailConstants.E_UPLOAD);
        String id = eUpload.getAttribute(MailConstants.A_ID);
        String acctId = iidRequested.getAccountId();        
        eUpload.addAttribute(MailConstants.A_ID, OfflineDocumentHandlers.uploadOfflineDocument(id, acctId)); 
        
        return super.proxyRequest(request, context, iidRequested, iidResolved);
    }
}
