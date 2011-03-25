package com.zimbra.cs.offline.extension;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.convert.OfflineConvertHandler;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;

public class OfflineExtension implements ZimbraExtension {

    @Override
    public String getName() {
        return "offline-ext";
    }

    @Override
    public void init() throws ExtensionException, ServiceException {
        ExtensionDispatcherServlet.register(this, new OfflineConvertHandler());
    }

    @Override
    public void destroy() {
        ExtensionDispatcherServlet.unregister(this);
    }

}
