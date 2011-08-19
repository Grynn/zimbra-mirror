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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.MockOfflineProvisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.datasource.DataSourceDbMapping;
import com.zimbra.cs.datasource.MockDataSourceDbMapping;
import com.zimbra.cs.mailbox.MockOfflineMailboxManager.Type;

public class TagSyncTest {

    private static String accountId;

    private static final String V_OTHER = "7.0.0";
    private static final String V_CURRENT = "8.0.0";
    private static MockDataSourceDbMapping mockMapping = new MockDataSourceDbMapping();

    @BeforeClass
    public static void init() throws ServiceException {
      MockOfflineProvisioning prov = new MockOfflineProvisioning();
      accountId = prov.createAccount("test@zimbra.com", "secret",
              Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, "0-0-0")).getId();
      Provisioning.setInstance(prov);
      MailboxManager.setInstance(new MockOfflineMailboxManager(Type.ZCS));
      DataSourceDbMapping.setInstance(mockMapping);
    }

    @Before
    public void clearData() throws ServiceException {
        ((OfflineAccount) Provisioning.getInstance().getAccount(accountId)).resetRemoteServerVersion();
        mockMapping.clearData();
    }

    private TagSync initTagSync(String version) throws ServiceException {
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put(OfflineProvisioning.A_offlineRemoteServerVersion, version);
        Provisioning.getInstance().modifyAttrs(Provisioning.getInstance().getAccount(accountId), attrs);
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(accountId);
        return new TagSync((ZcsMailbox)mbox);
    }

    @Test
    public void mappingNotRequired() throws ServiceException {
        Assert.assertFalse(initTagSync(V_CURRENT).isMappingRequired());
    }

    @Test
    public void mappingRequired() throws ServiceException {
        Assert.assertTrue(initTagSync(V_OTHER).isMappingRequired());
    }

    @Test
    public void mappingRequiredNewId() throws ServiceException {
        TagSync tagSync = initTagSync(V_CURRENT);
        Assert.assertFalse(tagSync.isMappingRequired());
        Assert.assertTrue(tagSync.isMappingRequired(12342512));
        Assert.assertTrue(tagSync.isMappingRequired());

    }

    @Test
    public void mapNewTag() throws ServiceException {
        TagSync tagSync = initTagSync(V_OTHER);
        int remoteId = 9999999;
        Assert.assertFalse(tagSync.mappingExists(remoteId));
        tagSync.mapTag(remoteId, 123);
        Assert.assertTrue(tagSync.mappingExists(remoteId));
    }

    @Test
    public void removeMapping() throws ServiceException {
        TagSync tagSync = initTagSync(V_OTHER);
        int remoteId = 9999999;
        Assert.assertFalse(tagSync.mappingExists(remoteId));
        tagSync.mapTag(remoteId, 123);
        Assert.assertTrue(tagSync.mappingExists(remoteId));
        tagSync.removeTagMapping(123);
        Assert.assertFalse(tagSync.mappingExists(remoteId));
    }

    @Test
    public void lookupRemote() throws ServiceException {
        TagSync tagSync = initTagSync(V_OTHER);
        int remoteId = 9999999;
        int localId = 67;
        tagSync.mapTag(remoteId, localId);
        Assert.assertEquals(remoteId, tagSync.remoteTagId(localId));
    }

    @Test
    public void lookupLocal() throws ServiceException {
        TagSync tagSync = initTagSync(V_OTHER);
        int remoteId = 9999999;
        int localId = 67;
        tagSync.mapTag(remoteId, localId);
        Assert.assertEquals(localId, tagSync.localTagId(remoteId));
    }

    @Test
    public void lookupLists() throws ServiceException {
        TagSync tagSync = initTagSync(V_OTHER);
        int[] localIds = {65, 72, 100};
        int[] remoteIds = {1234512,938291,123412};
        int i = 0;
        StringBuilder localStr = new StringBuilder();
        StringBuilder remoteStr = new StringBuilder();
        for (int local : localIds) {
            tagSync.mapTag(remoteIds[i], local);
            localStr.append(local).append(",");
            remoteStr.append(remoteIds[i]).append(",");
            i++;
        }
        localStr.setLength(localStr.length() - 1);
        remoteStr.setLength(remoteStr.length() - 1);
        //test assumes order is maintained, which isn't strictly required but is nice for testing
        Assert.assertEquals(remoteStr.toString(), tagSync.remoteAttrFromTags(localStr.toString()));
        Assert.assertEquals(localStr.toString(), tagSync.localTagsFromRemote(remoteStr.toString()));
    }

    @Test
    public void lookupByName() throws ServiceException {
        TagSync tagSync = initTagSync(V_OTHER);
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(accountId);
        int[] localIds = {65, 72, 100};
        int[] remoteIds = {1234512,938291,123412};
        String[] names = {"tag1","tag2","tag3"};
        int i = 0;
        StringBuilder nameStr = new StringBuilder();
        StringBuilder localStr = new StringBuilder();
        for (int local : localIds) {
            tagSync.mapTag(remoteIds[i], local);
            ((MockZcsMailbox) mbox).addStubTag(names[i], local);
            localStr.append(local).append(",");
            nameStr.append(names[i]).append(",");
            i++;
        }
        localStr.setLength(localStr.length() - 1);
        nameStr.setLength(nameStr.length() - 1);
        Assert.assertEquals(localStr.toString(), tagSync.localTagsFromNames(nameStr.toString(), ",", ","));
    }

    @Test
    public void overflowTag() throws ServiceException {
        TagSync tagSync = initTagSync(V_OTHER);
        int i = 0;
        for (i = TagSync.TAG_ID_OFFSET ; i < TagSync.MAX_TAG_COUNT + TagSync.TAG_ID_OFFSET; i++) {
            tagSync.mapTag(100000+i, i);
        }
        boolean found = false;
        try {
            tagSync.mapTag(9999999, i);
        } catch (MailServiceException se) {
            //expected
            if (se.getCode().equals(MailServiceException.NO_SUCH_TAG)) {
                found = true;
            }
        }
        Assert.assertTrue(found);
        tagSync.mapOverflowTag(999999999);
    }
}
