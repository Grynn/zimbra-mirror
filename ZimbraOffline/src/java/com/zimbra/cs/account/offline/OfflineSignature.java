/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.Map;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Signature;

class OfflineSignature extends Signature {
    OfflineSignature(Account acct, String name, Map<String,Object> attrs, Provisioning prov) {
        super(acct, name, (String)attrs.get(Provisioning.A_zimbraSignatureId), attrs, prov);
    }
    
    OfflineSignature(Account acct, Map<String,Object> attrs, Provisioning prov) {
        super(acct, (String)attrs.get(Provisioning.A_zimbraSignatureName), (String)attrs.get(Provisioning.A_zimbraSignatureId), attrs, prov);
    }

    void setName(String name) {
        mName = name;
    }
}
