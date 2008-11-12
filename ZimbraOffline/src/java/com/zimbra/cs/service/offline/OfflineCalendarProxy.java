package com.zimbra.cs.service.offline;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.service.ServiceException;

public class OfflineCalendarProxy {
    public static void uploadAttachments(Element request, String acctId) throws ServiceException {
        Element eAttach = request.getElement(MailConstants.E_MSG).getOptionalElement(MailConstants.E_ATTACH);
        if (eAttach != null) {
            String aid = eAttach.getAttribute(MailConstants.A_ATTACHMENT_ID);
            String[] ids = aid.split(",");
            String newAid = "";
            for (String id : ids) {
                 String newId = OfflineDocumentHandlers.uploadOfflineDocument(id, acctId);
                 if (newAid.length() > 0)
                     newAid += ",";
                 newAid += newId;
            }
            eAttach.addAttribute(MailConstants.A_ATTACHMENT_ID, newAid);
        }
    }
}
