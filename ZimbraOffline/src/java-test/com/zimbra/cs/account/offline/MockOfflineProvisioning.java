/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011 Zimbra, Inc.
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
package com.zimbra.cs.account.offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.account.Key.CalendarResourceBy;
import com.zimbra.common.account.Key.CosBy;
import com.zimbra.common.account.Key.DataSourceBy;
import com.zimbra.common.account.Key.DistributionListBy;
import com.zimbra.common.account.Key.DomainBy;
import com.zimbra.common.account.Key.IdentityBy;
import com.zimbra.common.account.Key.ServerBy;
import com.zimbra.common.account.Key.SignatureBy;
import com.zimbra.common.account.Key.XMPPComponentBy;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.CalendarResource;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.GlobalGrant;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.NamedEntry.Visitor;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Signature;
import com.zimbra.cs.account.XMPPComponent;
import com.zimbra.cs.account.Zimlet;
import com.zimbra.cs.account.auth.AuthContext;
import com.zimbra.cs.account.auth.AuthContext.Protocol;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.mime.MockMimeTypeInfo;
import com.zimbra.cs.mime.handler.UnknownTypeHandler;
import com.zimbra.soap.admin.type.CacheEntryType;
import com.zimbra.soap.admin.type.DataSourceType;

public final class MockOfflineProvisioning extends OfflineProvisioning {

    private final Map<String, Account> id2account = new HashMap<String, Account>();
    private final Map<String, Account> name2account = new HashMap<String, Account>();
    private final Server localhost;

    public MockOfflineProvisioning() {
        super(true);
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraServiceHostname, "localhost");
        localhost = new Server("localhost", "localhost", attrs, Collections.<String, Object> emptyMap(), this);
    }

    @Override
    public synchronized Account createAccount(String email, String password, Map<String, Object> attrs) throws ServiceException {
        validate(ProvisioningValidator.CREATE_ACCOUNT, email, null, attrs);

        Account account = new OfflineAccount(email, email, attrs, null, null, this);
        try {
            name2account.put(email, account);
            id2account.put(account.getId(), account);
            return account;
        } finally {
            validate(ProvisioningValidator.CREATE_ACCOUNT_SUCCEEDED, email, account);
        }
    }

    @Override
    public Account get(AccountBy keyType, String key) {
        switch (keyType) {
            case name:
                return name2account.get(key);
            case id:
            default:
                return id2account.get(key);
        }
    }

    private final Map<String, List<MimeTypeInfo>> mimeConfig = new HashMap<String, List<MimeTypeInfo>>();

    @Override
    public synchronized List<MimeTypeInfo> getMimeTypes(String mime) {
        List<MimeTypeInfo> result = mimeConfig.get(mime);
        if (result != null) {
            return result;
        } else {
            MockMimeTypeInfo info = new MockMimeTypeInfo();
            info.setHandlerClass(UnknownTypeHandler.class.getName());
            return Collections.<MimeTypeInfo> singletonList(info);
        }
    }

    @Override
    public synchronized List<MimeTypeInfo> getAllMimeTypes() {
        List<MimeTypeInfo> result = new ArrayList<MimeTypeInfo>();
        for (List<MimeTypeInfo> entry : mimeConfig.values()) {
            result.addAll(entry);
        }
        return result;
    }

    public void addMimeType(String mime, MimeTypeInfo info) {
        List<MimeTypeInfo> list = mimeConfig.get(mime);
        if (list == null) {
            list = new ArrayList<MimeTypeInfo>();
            mimeConfig.put(mime, list);
        }
        list.add(info);
    }

    private final Config config = new Config(new HashMap<String, Object>(), this);

    @Override
    public Config getConfig() {
        return config;
    }

//    @Override
//    public List<Zimlet> getObjectTypes() {
//        return Collections.emptyList();
//    }

    @Override
    public void modifyAttrs(Entry entry, Map<String, ? extends Object> attrs, boolean checkImmutable) {
        Map<String, Object> map = entry.getAttrs(false);
        for (Map.Entry<String, ? extends Object> attr : attrs.entrySet()) {
            if (attr.getValue() != null) {
                map.put(attr.getKey(), attr.getValue());
            } else {
                map.remove(attr.getKey());
            }
        }
        entry.setAttrs(map); //needed since OfflineAccount returns new Map, not a reference to internal map..
    }

    @Override
    public Server getLocalServer() {
        return localhost;
    }

    @Override
    public void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable, boolean allowCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reload(Entry e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean inDistributionList(Account acct, String zimbraId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Set<String> getDistributionLists(Account acct) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<DistributionList> getDistributionLists(Account acct, boolean directOnly, Map<String, String> via) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<DistributionList> getDistributionLists(DistributionList list, boolean directOnly, Map<String, String> via) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean healthCheck() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized GlobalGrant getGlobalGrant() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Account restoreAccount(String emailAddress, String password, Map<String, Object> attrs,
            Map<String, Object> origAttrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAccount(String zimbraId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void renameAccount(String zimbraId, String newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Account> getAllAdminAccounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void setCOS(Account acct, Cos cos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void modifyAccountStatus(Account acct, String newStatus) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void authAccount(Account acct, String password, Protocol proto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void authAccount(Account acct, String password, Protocol proto, Map<String, Object> authCtxt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void preAuthAccount(Account acct, String accountName, String accountBy, long timestamp, long expires,
            String preAuth, Map<String, Object> authCtxt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void ssoAuthAccount(Account acct, AuthContext.Protocol proto, Map<String, Object> authCtxt) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void changePassword(Account acct, String currentPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized SetPasswordResult setPassword(Account acct, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void checkPasswordStrength(Account acct, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void addAlias(Account acct, String alias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void removeAlias(Account acct, String alias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Domain createDomain(String name, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Domain get(DomainBy keyType, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Domain> getAllDomains() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteDomain(String zimbraId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Cos createCos(String name, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Cos copyCos(String srcCosId, String destCosName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void renameCos(String zimbraId, String newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Cos get(CosBy keyType, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Cos> getAllCos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteCos(String zimbraId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Server createServer(String name, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Server get(ServerBy keyName, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Server> getAllServers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Server> getAllServers(String service) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteServer(String zimbraId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized DistributionList createDistributionList(String listAddress, Map<String, Object> listAttrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized DistributionList get(DistributionListBy keyType, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteDistributionList(String zimbraId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void addAlias(DistributionList dl, String alias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void removeAlias(DistributionList dl, String alias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void renameDistributionList(String zimbraId, String newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Zimlet getZimlet(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Zimlet> listAllZimlets() {
        return Collections.emptyList();
    }

    @Override
    public synchronized Zimlet createZimlet(String name, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteZimlet(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized CalendarResource createCalendarResource(String emailAddress, String password, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteCalendarResource(String zimbraId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void renameCalendarResource(String zimbraId, String newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized CalendarResource get(CalendarResourceBy keyType, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Account> getAllAccounts(Domain d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void getAllAccounts(Domain d, Visitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void getAllAccounts(Domain d, Server s, Visitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<?> getAllCalendarResources(Domain d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void getAllCalendarResources(Domain d, Visitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void getAllCalendarResources(Domain d, Server s, Visitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<?> getAllDistributionLists(Domain d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void addMembers(DistributionList list, String[] members) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void removeMembers(DistributionList list, String[] member) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Identity createIdentity(Account account, String identityName, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Identity restoreIdentity(Account account, String identityName, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void modifyIdentity(Account account, String identityName, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteIdentity(Account account, String identityName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Identity> getAllIdentities(Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Identity get(Account account, IdentityBy keyType, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Signature createSignature(Account account, String signatureName, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Signature restoreSignature(Account account, String signatureName, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void modifySignature(Account account, String signatureId, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteSignature(Account account, String signatureId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Signature> getAllSignatures(Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Signature get(Account account, SignatureBy keyType, String key) {
        throw new UnsupportedOperationException();
    }

    private final Map<String, DataSource> dataSourcesById = new HashMap<String, DataSource>();
    private final Map<String, DataSource> dataSourcesByName = new HashMap<String, DataSource>();

    @Override
    public synchronized DataSource createDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs) {
        DataSource ds = new OfflineDataSource(account, type, dataSourceName, (String) attrs.get(A_zimbraDataSourceId), attrs, this);
        dataSourcesById.put(ds.getId(), ds);
        dataSourcesByName.put(ds.getName(), ds);
        return ds;
    }

    @Override
    public synchronized DataSource createDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs,
            boolean passwdAlreadyEncrypted) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized DataSource restoreDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyDataSource(Account account, String dataSourceId, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteDataSource(Account account, String dataSourceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<DataSource> getAllDataSources(Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized DataSource get(Account account, DataSourceBy keyType, String key) {
        if (keyType == DataSourceBy.id) {
            return dataSourcesById.get(key);
        } else if (keyType == DataSourceBy.name) {
            return dataSourcesByName.get(key);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public XMPPComponent createXMPPComponent(String name, Domain domain, Server server, Map<String, Object> attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMPPComponent get(XMPPComponentBy keyName, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<XMPPComponent> getAllXMPPComponents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteXMPPComponent(XMPPComponent comp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushCache(CacheEntryType type, CacheEntry[] entries) {
        throw new UnsupportedOperationException();
    }

}
