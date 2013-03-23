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

public class ContactAddOrUpdate implements ContactOperation {

    private ParsedContact pc;
    private Contact ycontact;
    private int itemId = Integer.MAX_VALUE;
    private boolean isPush;

    public ContactAddOrUpdate(ParsedContact pc, Contact ycontact, boolean isPush) {
        this.pc = pc;
        this.ycontact = ycontact;
        this.isPush = isPush;
    }

    @Override
    public String getRemoteId() {
        return this.ycontact.getId();
    }

    @Override
    public Action getOp() {
        return this.ycontact.getOp();
    }

    @Override
    public ParsedContact getParsedContact() {
        return this.pc;
    }

    @Override
    public boolean isPushOperation() {
        return this.isPush;
    }

    @Override
    public Contact getYContact() {
        return this.ycontact;
    }

    @Override
    public int compareTo(ContactOperation contactOp) {
        return this.itemId - contactOp.getItemId();
    }

    @Override
    public String toString() {
        ToStringHelper helper = Objects.toStringHelper(this);
        return helper.add("remoteId", this.getRemoteId())
                .add("itemId", this.itemId)
                .add("Op", this.getOp().name())
                .add("isPush", this.isPushOperation()).toString();
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
