/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.util.yc;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.zimbra.cs.mime.ParsedContact;

public class ContactRemove implements ContactOperation {

    private int itemId;
    private String remoteId;

    public ContactRemove(String contactId) {
        this.remoteId = contactId;
    }

    @Override
    public String getRemoteId() {
        return this.remoteId;
    }

    @Override
    public Action getOp() {
        return Action.REMOVE;
    }

    @Override
    public ParsedContact getParsedContact() {
        throw new UnsupportedOperationException("remove contact operation doesnt need parse contact");
    }

    @Override
    public boolean isPushOperation() {
        throw new UnsupportedOperationException("remove contact operation is push neutral");
    }

    @Override
    public Contact getYContact() {
        throw new UnsupportedOperationException("remove contact operation doesnt need ycontact");
    }

    @Override
    public int compareTo(ContactOperation contactOp) {
        return this.itemId - contactOp.getItemId();
    }

    @Override
    public String toString() {
        ToStringHelper helper = Objects.toStringHelper(this);
        return helper.add("remoteId", this.remoteId).add("itemId", this.itemId).add("Op", this.getOp().name())
                .toString();
    }

    @Override
    public void setItemId(int id) {
        this.itemId = id;
    }

    @Override
    public int getItemId() {
        return this.itemId;
    }
}
