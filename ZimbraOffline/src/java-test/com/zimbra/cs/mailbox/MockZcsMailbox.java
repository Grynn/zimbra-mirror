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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.MailItem.UnderlyingData;

public class MockZcsMailbox extends ZcsMailbox {
    
    private Account account;
    private Map<String, Metadata> metadata = new HashMap<String, Metadata>();
    
    private Map<String, Tag> tagsByName = new HashMap<String, Tag>();

    MockZcsMailbox(Account account, MailboxData data) throws ServiceException {
        super(data);
        this.account = account;
    }

    @Override
    public Metadata getConfig(OperationContext octxt, String section) {
        return metadata.get(section);
    }

    @Override
    public void setConfig(OperationContext octxt, String section,
            Metadata config) {
        metadata.put(section, config);
    }

    @Override
    public Account getAccount() throws ServiceException {
        if (account == null) {
            return super.getAccount();
        } else {
            return account;
        }
    }

    @Override
    public String getAccountId() {
        if (account == null) {
            return super.getAccountId();
        } else {
            return account.getId();
        }
    }

    @Override
    public synchronized List<Tag> getTagList(OperationContext octxt)
                    throws ServiceException {
        return new ArrayList<Tag>();

    }
    
    @Override
    public synchronized Tag getTagByName(String name) throws ServiceException {
        return tagsByName.get(name);
    }
    
    public void addStubTag(String name, Integer id) throws ServiceException {
        UnderlyingData data = new UnderlyingData();
        data.id          = id;
        data.type        = MailItem.Type.TAG.toByte();
        data.name        = name;
//        data.subject     = name;
        Tag tag = new Tag(this, data);
        tagsByName.put(name, tag);
    }
}
