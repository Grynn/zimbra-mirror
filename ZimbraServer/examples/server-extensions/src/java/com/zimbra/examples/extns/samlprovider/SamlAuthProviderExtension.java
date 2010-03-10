package com.zimbra.examples.extns.samlprovider;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.service.AuthProvider;

/**
 * @author vmahajan
 */
public class SamlAuthProviderExtension implements ZimbraExtension {
    /**
     * Defines a name for the extension. It must be an identifier.
     *
     * @return
     */
    public String getName() {
        return "samlProviderExtn";
    }

    /**
     * Initializes the extension. Called when the extension is loaded.
     *
     * @throws com.zimbra.common.service.ServiceException
     *
     */
    public void init() throws ServiceException {
        ExtensionDispatcherServlet.register(this, new SamlRequestHandler());

        AuthProvider.register(new SamlAuthProvider());
        AuthProvider.refresh();
    }

    /**
     * Terminates the extension. Called when the server is shut down.
     */
    public void destroy() {
        ExtensionDispatcherServlet.unregister(this);
    }
}
