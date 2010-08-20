/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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

/*
 * Created on Aug 20, 2010
 */
package com.zimbra.cs.service.offline;

import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.ZimbraAuthToken;
import com.zimbra.cs.service.AuthProviderException;
import com.zimbra.cs.service.ZimbraAuthProvider;

public class OfflineZimbraAuthProvider extends ZimbraAuthProvider {
    
    public static final String PROVIDER_NAME = "offline";

    public OfflineZimbraAuthProvider() {
        super(PROVIDER_NAME);
    }
    
    protected AuthToken genAuthToken(String encodedAuthToken) throws AuthProviderException, AuthTokenException {
        if (StringUtil.isNullOrEmpty(encodedAuthToken))
            throw AuthProviderException.NO_AUTH_DATA();
        AuthToken at = ZimbraAuthToken.getAuthToken(encodedAuthToken);
        if (at instanceof ZimbraAuthToken) {
           try {
               return (AuthToken)((ZimbraAuthToken)at).clone();
           } catch (CloneNotSupportedException e) {
               ZimbraLog.system.error("Unable to clone zimbra auth token",e);
               return at;
           }
        } else {
            return at;
        }
    }
}
