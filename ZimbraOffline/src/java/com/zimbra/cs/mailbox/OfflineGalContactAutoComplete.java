/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.mailbox;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.ContactAutoComplete.AutoCompleteResult;
import com.zimbra.cs.mailbox.ContactAutoComplete.ContactEntry;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineGalContactAutoComplete extends ContactAutoComplete {

    public OfflineGalContactAutoComplete(Account acct, OperationContext octxt) {
        super(acct, octxt);
    }

    public OfflineGalContactAutoComplete(Account acct, ZimbraSoapContext zsc,
                    OperationContext octxt) {
        super(acct, zsc, octxt);
    }

    @Override
    protected void addEntry(ContactEntry entry, AutoCompleteResult result) {
        if (entry.isGroup() && result.entries.contains(entry)) {
            //duplicate non-group added; addEntry rejects duplicates so we need to manually set the flag
            //this occurs because GAL search in ZD uses mailbox search; there can be multiple entries for one addr 
            //for example VMware GAL has server-team@zimbra.com as type=account and Zimbra GAL has server-team@zimbra.com as type=group
            for (ContactEntry exist : result.entries) {
                if (entry.getKey().equals(exist.getKey()) && !exist.isGroup()) {
                    exist.setIsGalGroup(true);
                }
            }
        } else {
            result.addEntry(entry);
        }
    }
}
