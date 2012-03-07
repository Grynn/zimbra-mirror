/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 Zimbra, Inc.
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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.io.Closeables;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.index.ContactHit;
import com.zimbra.cs.index.ResultsPager;
import com.zimbra.cs.index.SearchParams;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.ContactAutoComplete;
import com.zimbra.cs.mailbox.ContactAutoComplete.AutoCompleteResult;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineGalContactAutoComplete;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.util.ItemId;

public class OfflineGal {

    public static final String CTYPE_ACCOUNT = "account";
    public static final String CTYPE_GROUP = "group";
    public static final String CTYPE_RESOURCE = "resource";
    public static final String CTYPE_ALL = "all";

    public static final String CRTYPE_LOCATION = "Location";
    public static final String CRTYPE_EQUIPMENT = "Equipment";

    public static final String A_zimbraCalResType = "zimbraCalResType";
    public static final String A_zimbraCalResLocationDisplayName = "zimbraCalResLocationDisplayName";

    public static final String SECOND_GAL_FOLDER = "Contacts2";

    public static final List<String> EMAIL_KEYS = Arrays.asList(ContactConstants.A_email, ContactConstants.A_email2,
            ContactConstants.A_email3);

    private OfflineAccount mAccount;
    private Mailbox mGalMbox = null;
    private OperationContext mOpContext = null;
    private SearchParams searchParams = null;

    public OfflineGal(OfflineAccount account) throws OfflineServiceException {
        if (account.isGalAccount()) {
            mAccount = account;
        } else if (account.isZcsAccount() && account.isFeatureGalEnabled() && account.isFeatureGalSyncEnabled()) {
            try {
                mAccount = (OfflineAccount) OfflineProvisioning.getOfflineInstance().getGalAccountByAccount(account);
            } catch (ServiceException e) {
                OfflineLog.offline.debug("failed to get GAL account for account %s", account.getName());
            } catch (NullPointerException e) {
                OfflineLog.offline.debug("no GAL account attached to Domain yet for account %s", account.getName());
                throw OfflineServiceException.GAL_NOT_READY();
            }
        }
        if (mAccount == null) {
            throw OfflineServiceException.GAL_NOT_READY();
        }
    }

    public OfflineAccount getAccount() {
        return mAccount;
    }

    public Mailbox getGalMailbox() {
        return mGalMbox;
    }

    public OperationContext getOpContext() {
        return mOpContext;
    }

    public ZimbraQueryResults search(String name, String type, SortBy sortBy, int offset, int limit, Element cursor)
            throws ServiceException {
        Set<String> names = new HashSet<String>();
        names.add(name);
        return search(names, type, sortBy, offset, limit, cursor);
    }

    public ZimbraQueryResults search(Set<String> names, String type, SortBy sortBy, int offset, int limit,
            Element cursor) throws ServiceException {
        String galAcctId = mAccount.getId();
        mGalMbox = null;

        if (galAcctId != null && galAcctId.length() > 0)
            mGalMbox = MailboxManager.getInstance().getMailboxByAccountId(galAcctId, false);
        if (mGalMbox == null) {
            OfflineLog.offline.debug("unable to access GAL mailbox for " + mAccount.getName());
            return null;
        }

        mOpContext = new OperationContext(mGalMbox);
        Folder folder = getSyncFolder(mGalMbox, mOpContext, false);

        String query = buildGalSearchQueryString(names, type, folder);
        this.searchParams = new SearchParams();
        this.searchParams.setQueryString(query);
        this.searchParams.setTypes(EnumSet.of(MailItem.Type.CONTACT));
        this.searchParams.setSortBy(sortBy);
        this.searchParams.setOffset(offset);
        this.searchParams.setLimit(limit);
        return mGalMbox.index.search(SoapProtocol.Soap12, mOpContext, this.searchParams);
    }

    private String buildGalSearchQueryString(Set<String> names, String type, Folder folder) {
        StringBuilder query = new StringBuilder("in:\"").append(folder.getName()).append("\"");
        boolean firstName = true;
        for (String name : names) {
            name = name.trim();
            // '.' is a special operator that matches everything.
            if (name.length() > 0 && !name.equals(".")) {
                if (firstName) {
                    query.append(" AND (");
                    firstName = false;
                } else {
                    query.append(" OR ");
                }
                // escape quotes
                query.append(" contact:\"").append(name.replace("\"", "\\\"")).append("\"");
            }
        }
        if (!firstName) {
            query.append(" ) ");
        }
        if (type.equals(CTYPE_ACCOUNT)) {
            query.append(" AND (#").append(ContactConstants.A_type).append(":").append(CTYPE_ACCOUNT).append(" OR #")
                    .append(ContactConstants.A_type).append(":").append(CTYPE_GROUP).append(")");
        } else if (type.equals(CTYPE_RESOURCE)) {
            query.append(" AND #").append(ContactConstants.A_type).append(":").append(CTYPE_RESOURCE);
        } else if (type.equals(CTYPE_GROUP)) {
            query.append(" AND (#").append(ContactConstants.A_type).append(":").append(CTYPE_GROUP).append(")");
        }
        return query.toString();
    }

    public void search(Element response, String name, String type, SortBy sortBy, int offset, int limit, Element cursor)
            throws ServiceException {
        limit = limit == 0 ? mAccount.getIntAttr(Provisioning.A_zimbraGalMaxResults, 100) : limit;
        if (sortBy == null) {
            sortBy = SortBy.NAME_ASC;
        }
        ZimbraQueryResults zqr = search(name, type, sortBy, offset, limit + 1, cursor); // use limit + 1 so that we know when to set "had more"
        if (zqr == null) {
            response.addAttribute(AccountConstants.A_MORE, false);
            return;
        }
        ResultsPager pager = ResultsPager.create(zqr, this.searchParams);
        int num = 0;
        try {
            while (pager.hasNext()) {
                ZimbraHit hit = pager.getNextHit();
                if (hit instanceof ContactHit) {
                    int id = hit.getItemId();
                    Contact contact = (Contact) mGalMbox.getItemById(mOpContext, id, MailItem.Type.CONTACT);
                    Element cn = response.addElement(MailConstants.E_CONTACT);
                    cn.addAttribute(MailConstants.A_ID, hit.getAcctIdStr() + ":" + Integer.toString(id));
                    Map<String, String> fields = contact.getFields();
                    Iterator<String> it = fields.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (!key.equals(MailConstants.A_ID) && !key.equals(OfflineConstants.GAL_LDAP_DN))
                            cn.addKeyValuePair(key, fields.get(key), MailConstants.E_ATTRIBUTE,
                                    MailConstants.A_ATTRIBUTE_NAME);
                    }
                    Object sf = hit.getSortField(sortBy);
                    if (sf != null && sf instanceof String)
                        cn.addAttribute(MailConstants.A_SORT_FIELD, (String) sf);
                }
                num++;
                if (num == searchParams.getLimit()) {
                    break;
                }
            }
            response.addAttribute(MailConstants.A_SORTBY, sortBy.toString());
            response.addAttribute(MailConstants.A_QUERY_OFFSET, offset);
            response.addAttribute(AccountConstants.A_MORE, zqr.hasNext());
        } catch (Exception e) {
            OfflineLog.offline.debug("search on GalSync account failed...%s", e.getCause());
        } finally {
            if (zqr != null) {
                Closeables.closeQuietly(zqr);
            }
        }
    }

    public void search(AutoCompleteResult result, String name, int limit, String type) throws ServiceException {
        ZimbraQueryResults zqr = search(name, type, SortBy.NAME_ASC, 0, limit, null);
        if (zqr == null) {
            return;
        }
        ContactAutoComplete ac = new OfflineGalContactAutoComplete(mAccount, mOpContext);
        ac.setNeedCanExpand(true);
        try {
            while (zqr.hasNext()) {
                int id = zqr.getNext().getItemId();
                Contact contact = (Contact) mGalMbox.getItemById(mOpContext, id, MailItem.Type.CONTACT);
                ItemId iid = new ItemId(mGalMbox, id);
                ac.addMatchedContacts(name, contact.getFields(), ContactAutoComplete.FOLDER_ID_GAL, iid, result);
                if (!result.canBeCached)
                    break;
            }
        } finally {
            Closeables.closeQuietly(zqr);
        }
    }

    private static ConcurrentMap<String, Folder> syncFolderCache = new ConcurrentHashMap<String, Folder>();

    /*
     * We used to have two alternating folders to store GAL. After the upgrade, we should continue to use the 
     * "current" folder (sync folder) to store GAL, until a full-sync when we can safely delete the "second"
     * folder and only use the system folder "Contacts" from then on.
     */
    public static Folder getSyncFolder(Mailbox galMbox, OperationContext context, boolean fullSync) throws ServiceException {
        if (fullSync) {
            Folder sndFolder = getSecondFolder(galMbox, context);
            if (sndFolder != null) { // migration: empty and delete "Contacts2" folder
                // there is a small chance (only on full sync) of race condition here if user is searching in gal when
                // this migration is run. but since the chance is small and is one-time only, the condition is not handled
                galMbox.emptyFolder(context, sndFolder.getId(), false);
                galMbox.delete(context, sndFolder.getId(), MailItem.Type.FOLDER);
                syncFolderCache.remove(galMbox.getAccountId());
                OfflineLog.offline.debug("Offline GAL deleted second sync folder");
            }
        }

        Folder syncFolder = syncFolderCache.get(galMbox.getAccountId());
        if (syncFolder == null) {
            syncFolder = galMbox.getFolderById(context, Mailbox.ID_FOLDER_CONTACTS);
            Folder sndFolder = getSecondFolder(galMbox, context);
            if (sndFolder != null && sndFolder.getItemCount() > 0) {
                syncFolder = sndFolder;
            }
            syncFolderCache.put(galMbox.getAccountId(), syncFolder);
        }
        return syncFolder;
    }

    private static Folder getSecondFolder(Mailbox galMbox, OperationContext context) throws ServiceException {
        try {
            return galMbox.getFolderByPath(context, SECOND_GAL_FOLDER);
        } catch (MailServiceException.NoSuchItemException e) {
            return null;
        }
    }
}
