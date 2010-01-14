package com.zimbra.cs.service.offline;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.util.ZimbraApplication;
import com.zimbra.soap.DocumentHandler;


public class OfflineGetExtensions extends DocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        List<String> extensionNames = ZimbraApplication.getInstance().getExtensionNames();
        Element response = getZimbraSoapContext(context).createElement(OfflineConstants.GET_EXTENSIONS_RESPONSE);
        if (extensionNames != null) {
            for (String ext : extensionNames)
                response.addElement(OfflineConstants.EXTENSION).addAttribute(OfflineConstants.EXTENSION_NAME, ext);
        }
        return response;
    }

    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return false;
    }

    @Override
    public boolean needsAdminAuth(Map<String, Object> context) {
        return false;
    }
}
