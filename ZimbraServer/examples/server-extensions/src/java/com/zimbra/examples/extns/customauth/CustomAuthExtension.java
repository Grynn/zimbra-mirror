package com.zimbra.examples.extns.customauth;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;
import com.zimbra.cs.extension.ZimbraExtension;

/**
 * This extension registers a custom authentication mechanism.
 *
 * @author vmahajan
 */
public class CustomAuthExtension implements ZimbraExtension {
    
    /**
     * Defines a name for the extension. It must be an identifier.
     *
     * @return
     */
    public String getName() {
        return "customAuthExtn";
    }

    /**
     * Initializes the extension. Called when the extension is loaded.
     *
     * @throws com.zimbra.common.service.ServiceException
     *
     */
    public void init() throws ServiceException {
        ZimbraCustomAuth.register("simple", new SimpleAuth());
    }

    /**
     * Terminates the extension. Called when the server is shut down.
     */
    public void destroy() {
    }
}
