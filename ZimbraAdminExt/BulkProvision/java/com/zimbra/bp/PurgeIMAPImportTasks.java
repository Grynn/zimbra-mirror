package com.zimbra.bp;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class PurgeIMAPImportTasks extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context)
    throws ServiceException {
         
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(ZimbraBulkProvisionService.PURGE_BULK_IMAP_IMPORT_TASKS_RESPONSE);
        BulkIMAPImportTaskManager.purgeQueue(zsc.getAuthtokenAccountId());
        return response;
    }
}
