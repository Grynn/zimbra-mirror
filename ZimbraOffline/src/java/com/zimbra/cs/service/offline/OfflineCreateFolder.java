package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.service.mail.CreateFolder;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineCreateFolder extends CreateFolder {
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        if (!(mbox instanceof OfflineMailbox))
            return super.handle(request, context);
            
        Element t = request.getElement(MailConstants.E_FOLDER);
        String url = t.getAttribute(MailConstants.A_URL, null);
        if (url != null && !url.equals(""))
            t.addAttribute(MailConstants.A_SYNC, false); // for zimbra accounts don't load rss on folder creation
        return super.handle(request, context);
    }
}
