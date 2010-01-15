/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.auth.AuthFactory;
import org.jivesoftware.wildfire.sasl.AuthorizationManager;
import org.jivesoftware.wildfire.user.UserNotFoundException;

import javax.security.auth.callback.*;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import java.io.IOException;

/**
 * Callback handler that may be used when doing SASL authentication. A CallbackHandler
 * may be required depending on the SASL mechanism being used.<p>
 *
 * Mechanisms that use a digest don't include a password so the server needs to use the
 * stored password of the user to compare it (somehow) with the specified digest. This
 * operation requires that the UserProvider being used supports passwords retrival.
 * {@link SASLAuthentication} should not offer these kind of SASL mechanisms if the user
 * provider being in use does not support passwords retrieval.
 *
 * @author Hao Chen
 */
public class XMPPCallbackHandler implements CallbackHandler {

    public XMPPCallbackHandler() {
    }

    public void handle(final Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

        String realm;
        String name = null;

        for (int i = 0; i < callbacks.length; i++) {
            // Log.info("Callback: " + callbacks[i].getClass().getSimpleName());
            if (callbacks[i] instanceof RealmCallback) {
                realm = ((RealmCallback) callbacks[i]).getText();
                if (realm == null) {
                    realm = ((RealmCallback) callbacks[i]).getDefaultText();
                }
                //Log.info("RealmCallback: " + realm);
            }
            else if (callbacks[i] instanceof NameCallback) {
                name = ((NameCallback) callbacks[i]).getName();
                if (name == null) {
                    name = ((NameCallback) callbacks[i]).getDefaultName();
                }
                //Log.info("NameCallback: " + name);
            }
            else if (callbacks[i] instanceof PasswordCallback) {
                try {
                    // Get the password from the UserProvider. Some UserProviders may not support
                    // this operation
                    ((PasswordCallback) callbacks[i])
                            .setPassword(AuthFactory.getPassword(name).toCharArray());

                    //Log.info("PasswordCallback: "
                    //+ new String(((PasswordCallback) callbacks[i]).getPassword()));
                }
                catch (UserNotFoundException e) {
                    throw new IOException(e.toString());
                }
            }
            else if (callbacks[i] instanceof AuthorizeCallback) {
                AuthorizeCallback authCallback = ((AuthorizeCallback) callbacks[i]);
                String authenId =
                        authCallback.getAuthenticationID(); // Principal that authenticated
                String authorId =
                        authCallback.getAuthorizationID();  // Username requested (not full JID)
                if (AuthorizationManager.authorize(authorId, authenId)) {
                    authCallback.setAuthorized(true);
                    authCallback.setAuthorizedID(authorId);
                    Log.debug(authenId + " authorized to " + authorId);
                }
                else {
                    Log.debug(authenId + " not authorized to " + authorId);
                }
            }
            else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }
}