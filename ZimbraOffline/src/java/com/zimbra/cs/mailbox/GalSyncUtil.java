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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.DataSource.Type;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;

/**
 * Utility class for common gal sync operations
 *
 */
public final class GalSyncUtil {

    private GalSyncUtil() {
    }

    /**
     * Find contact id from data source database
     * @param id
     * @param dsource
     * @return contact id, or -1 if not found
     * @throws ServiceException
     */
    public static int findContact(String id, DataSource dsource) throws ServiceException {
        DataSourceItem dsItem = DbDataSource.getReverseMapping(dsource, id);
        if (dsItem.itemId > 0)
            return dsItem.itemId;
        return -1;
    }

    /**
     * Fetch distribution list members from ZCS
     * @param dlName
     * @param mbox
     * @return JSON serialization of the members, or null if list has none
     * @throws ServiceException
     */
    public static String fetchDlMembers(String dlName, ZcsMailbox mbox) throws ServiceException {
        try {
            XMLElement req = new XMLElement(AccountConstants.GET_DISTRIBUTION_LIST_MEMBERS_REQUEST);
            req.addElement(AdminConstants.E_DL).setText(dlName);
            Element response = mbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12);
            int total = response.getAttributeInt(AccountConstants.A_TOTAL);
            if (total < 1) {
                return null;
            }
            List<String> members = new ArrayList<String>();
            for (Element member : response.listElements(AccountConstants.E_DLM)) {
                members.add(member.getText());
            }
            return Contact.encodeMultiValueAttr(members.toArray(new String[members.size()]));
        } catch (JSONException e) {
            throw ServiceException.FAILURE("Unable to encode dlist members", e);
        } catch (ServiceException e) {
            if (e.getCode().equals(ServiceException.PERM_DENIED)) {
                OfflineLog.offline.debug("Permission denied fetching dlist members for %s",dlName);
                return null;
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Create a DataSource instance for gal account
     * @param galAccount
     * @return DataSource for the account
     * @throws ServiceException
     */
    public static DataSource createDataSourceForAccount(Account galAccount) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        String dsId = galAccount.getAttr(OfflineConstants.A_offlineGalAccountDataSourceId, false);
        if (dsId == null) {
            dsId = UUID.randomUUID().toString();
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountDataSourceId, dsId);
        }
        return new DataSource(galAccount, Type.gal, galAccount.getName(), dsId,
            new HashMap<String, Object>(), prov);
    }
    
    /**
     * Retrieve a contact specified by email address from OfflineGal
     * @param requestedAcct
     * @param addr
     * @return Contact for the address or null if it does not exist
     * @throws ServiceException
     */
    public static Contact getGalContact(Account requestedAcct, String addr) throws ServiceException {
        Contact con = null;
        ZimbraQueryResults dlResult = (new OfflineGal((OfflineAccount)requestedAcct)).search(addr, "all", "", 0, 0, null);
        if (dlResult != null) {
            try {
                if (dlResult.hasNext()) {
                    ZimbraHit hit = dlResult.getNext();
                    con = (Contact) hit.getMailItem();
                }
            } finally {
                dlResult.doneWithSearchResults();
            }
        }
        return con;
    }
    
    public static String getContactLogStr(ParsedContact contact) {
        StringBuilder logBuf = new StringBuilder();
        logBuf.append(" name=\"").append(contact.getFields().get(ContactConstants.A_fullName)).append("\"")
              .append(" type=\"").append(contact.getFields().get(ContactConstants.A_type)).append("\"");
        return logBuf.toString();
    }
    
    public static void fillContactAttrMap(ZcsMailbox mbox, Map<String, String> map) throws ServiceException {
        String fullName = map.get(ContactConstants.A_fullName);
        if (fullName == null) {
            String fname = map.get(ContactConstants.A_firstName);
            String lname = map.get(ContactConstants.A_lastName);
            fullName = fname == null ? "" : fname;
            if (lname != null)
                fullName = fullName + (fullName.length() > 0 ? " " : "") + lname;
            if (fullName.length() > 0)
                map.put(ContactConstants.A_fullName, fullName);
        }
        String type = map.get(ContactConstants.A_type);
        if (type == null) {
            type = map.get(OfflineGal.A_zimbraCalResType) == null ? OfflineGal.CTYPE_ACCOUNT : OfflineGal.CTYPE_RESOURCE;
            map.put(ContactConstants.A_type, type);
        }
        if (type.equals(OfflineGal.CTYPE_GROUP) && mbox.getRemoteServerVersion().isAtLeast7xx()) {
            String dlName = map.get(ContactConstants.A_email);
            String dlMembers = GalSyncUtil.fetchDlMembers(dlName, mbox);
            if (dlMembers == null) {
                OfflineLog.offline.debug("No members in dlist %s",dlName);
            } else {
                map.put(ContactConstants.A_member, dlMembers);
            }
        }
    }
    
    private static List<ParsedContact> getParsedContacts(ZcsMailbox mbox, List<Element> contacts, List<String> ids, List<String> retryIds) throws ServiceException {
        List<ParsedContact> list = new ArrayList<ParsedContact>();
        for (Element elt : contacts) {
          String id = elt.getAttribute(AccountConstants.A_ID);
          Map<String, String> fields = new HashMap<String, String>();
          fields.put(OfflineConstants.GAL_LDAP_DN, id);
          for (Element eField : elt.listElements()) {
              String name = eField.getAttribute(Element.XMLElement.A_ATTR_NAME);
              if (!name.equals("objectClass"))
                  fields.put(name, eField.getText());
          }
          try {
              fillContactAttrMap(mbox, fields);
              list.add(new ParsedContact(fields));
              ids.add(id);
          } catch (ServiceException e) {
              retryIds.add(id);
              //TODO LC ?
              if (retryIds.size() > 100) {
                  retryIds.clear();
                  OfflineLog.offline.info("Offline GAL sync retry aborted, too many failed items");
                  throw e;
              }
          }
        }
        return list;
    }
    
    static void createContact(Mailbox mbox, OperationContext ctxt, int syncFolder, DataSource ds, ParsedContact contact, String id, String logstr, final boolean isBatch) throws ServiceException {
        Contact c = mbox.createContact(ctxt, contact, syncFolder, null, isBatch);
        DbDataSource.addMapping(ds, new DataSourceItem(0, c.getId(), id, null), isBatch);
        OfflineLog.offline.debug("Offline GAL contact created: " + logstr);
    }
    
    private static void saveParsedContact(Mailbox mbox, OperationContext ctxt, int syncFolder, String id, ParsedContact contact, String logstr, boolean isFullSync, DataSource ds) throws ServiceException {
        if (isFullSync) {
            createContact(mbox, ctxt, syncFolder, ds, contact, id, logstr, true);
        } else {
            int itemId = GalSyncUtil.findContact(id, ds);
            if (itemId > 0) {
                try {
                    mbox.modifyContact(ctxt, itemId, contact, true);
                    OfflineLog.offline.debug("Offline GAL contact modified: " + logstr);
                } catch (MailServiceException.NoSuchItemException e) {
                    OfflineLog.offline.warn("Offline GAL modify error - no such contact: " + logstr + " itemId=" + Integer.toString(itemId));
                }
            } else {
                createContact(mbox, ctxt, syncFolder, ds, contact, id, logstr, true);
            }
        }
    }

    public static long fetchContacts(long networkTime, ZcsMailbox mbox, Mailbox galMbox, OperationContext ctxt, int syncFolder, String reqIds, boolean isFullSync, DataSource ds, List<String> retryContactIds, String token, int group, boolean isCheckpointing) throws ServiceException, IOException {
        XMLElement req = new XMLElement(MailConstants.GET_CONTACTS_REQUEST);
        req.addElement(AdminConstants.E_CN).addAttribute(AccountConstants.A_ID, reqIds);
        long start = System.currentTimeMillis();
        Element response = mbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12);
        networkTime += System.currentTimeMillis() - start;

        List<Element> contacts = response.listElements(MailConstants.E_CONTACT);
        List<String> ids = new ArrayList<String>();
        List<ParsedContact> parsedContacts = getParsedContacts(mbox, contacts, ids, retryContactIds);

        if (!parsedContacts.isEmpty()) {
            boolean success = false;
            synchronized (galMbox) {
                try {
                    galMbox.beginTransaction("GALSync", null);

                    int index = 0;
                    for (ParsedContact contact : parsedContacts) {
                        saveParsedContact(galMbox, ctxt, syncFolder, ids.get(index++), contact, getContactLogStr(contact), isFullSync, ds);
                    }

                    if (isCheckpointing) {
                        GalSyncCheckpointUtil.checkpoint(galMbox, token, group);
                    }
                    success = true;
                } finally {
                    galMbox.endTransaction(success);
                }
            }
        }

        return networkTime;
    }
}
