/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.index.queryparser.ParseException;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.ContactAutoComplete.AutoCompleteResult;
import com.zimbra.cs.mailbox.ContactAutoComplete;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.service.util.ItemId;

public class OfflineGal {

    public static final String CTYPE_ACCOUNT = "account";
    public static final String CTYPE_RESOURCE = "resource";
    public static final String CTYPE_ALL = "all";
    
    public static final String CRTYPE_LOCATION = "Location";
    public static final String CRTYPE_EQUIPMENT = "Equipment";
    
    public static final String A_zimbraCalResType = "zimbraCalResType";
    public static final String A_zimbraCalResLocationDisplayName = "zimbraCalResLocationDisplayName";
    
    public static final String SECOND_GAL_FOLDER = "Contacts2";
 
    public static final List<String> EMAIL_KEYS = Arrays.asList(ContactConstants.A_email, ContactConstants.A_email2, ContactConstants.A_email3);
    
    private OfflineAccount mAccount;
    private Mailbox mGalMbox = null;
    private OperationContext mOpContext = null;
   
    public OfflineGal(OfflineAccount account) {
        mAccount = account;
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
        
    public ZimbraQueryResults search(String name, int limit, String type) throws ServiceException {        
        String galAcctId = mAccount.getAttr(OfflineConstants.A_offlineGalAccountId, false);
        mGalMbox = null;
        
        if (galAcctId != null && galAcctId.length() > 0)
            mGalMbox = MailboxManager.getInstance().getMailboxByAccountId(galAcctId, false);
        if (mGalMbox == null) {
            OfflineLog.offline.debug("unable to access GAL mailbox for " + mAccount.getName());
            return null;
        }
       
        byte[] types = new byte[1];
        types[0] = MailItem.TYPE_CONTACT;
        mOpContext = new OperationContext(mGalMbox);
        
        Folder fstFolder = mGalMbox.getFolderById(mOpContext, Mailbox.ID_FOLDER_CONTACTS);
        Folder currFolder = fstFolder;
        try {
            Folder sndFolder = mGalMbox.getFolderByPath(mOpContext, SECOND_GAL_FOLDER);
            if (fstFolder.getItemCount() < sndFolder.getItemCount())
                currFolder = sndFolder;
        } catch (MailServiceException.NoSuchItemException e) {}
        
        name = name.trim();
        String query = "in:\"" + currFolder.getName() + "\"";
        if (name.length() > 0 && !name.equals("."))
            query = query + " AND contact:(" + name + ")";
        if (type.equals(CTYPE_ACCOUNT))
            query = query + " AND #" + ContactConstants.A_type + ":" + CTYPE_ACCOUNT;
        else if (type.equals(CTYPE_RESOURCE))
            query = query + " AND #" + ContactConstants.A_type + ":" + CTYPE_RESOURCE;
    
        try  {
            return mGalMbox.search(mOpContext, query, types, SortBy.SCORE_DESCENDING, limit);
        } catch (ParseException e) {
            OfflineLog.offline.debug("gal mailbox parse error (" + mAccount.getName() + "): " + e.getMessage());
            return null;
        } catch (IOException e) {
            OfflineLog.offline.debug("gal mailbox IO error (" + mAccount.getName() + "): " + e.getMessage());
            return null;
        }
    }   
    
    public void search(Element response, String name, String type) throws ServiceException {
        search(response, name, 0, type);
    }
    
    public void search(Element response, String name, int limit, String type) throws ServiceException {
        limit = limit == 0 ? mAccount.getIntAttr(Provisioning.A_zimbraGalMaxResults, 100) : limit;        
        ZimbraQueryResults zqr = search(name, limit + 1, type); // use limit + 1 so that we know when to set "had more"
        if (zqr == null) {
            response.addAttribute(AccountConstants.A_MORE, false);
            return;
        }
                   
        try {
            int c = 0;
            while (c++ < limit && zqr.hasNext()) {
                int id = zqr.getNext().getItemId();
            
                Contact contact = (Contact) mGalMbox.getItemById(mOpContext, id, MailItem.TYPE_CONTACT);
                Element cn = response.addElement(MailConstants.E_CONTACT);
                cn.addAttribute(MailConstants.A_ID, id);
            
                Map<String, String> fields = contact.getFields();
                Iterator<String> it = fields.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if (!key.equals(MailConstants.A_ID) && !key.equals("type") && !key.equals(OfflineConstants.GAL_LDAP_DN))
                        cn.addKeyValuePair(key, fields.get(key), MailConstants.E_ATTRIBUTE, MailConstants.A_ATTRIBUTE_NAME);
                }
            }
                    
            response.addAttribute(AccountConstants.A_MORE, zqr.hasNext());
        } finally {
            zqr.doneWithSearchResults();
        }
    }
    
    public void search(AutoCompleteResult result, String name, int limit, String type) throws ServiceException {
        ZimbraQueryResults zqr = search(name, limit, type);
        if (zqr == null)
            return;
        
        ContactAutoComplete ac = new ContactAutoComplete(mAccount.getId());
        try {
            while (zqr.hasNext()) {
                int id = zqr.getNext().getItemId();            
                Contact contact = (Contact) mGalMbox.getItemById(mOpContext, id, MailItem.TYPE_CONTACT);
                ItemId iid = new ItemId(mGalMbox, id);
                ac.addMatchedContacts(name, contact.getFields(), ContactAutoComplete.FOLDER_ID_GAL, iid, result);
                if (!result.canBeCached)
                    break;
            }                    
        } finally {
            zqr.doneWithSearchResults();
        }        
    }
    
    // Return: first  - id of current GAL folder, second - id of previous GAL folder
    public static Pair<Integer, Integer> getSyncFolders(Mailbox galMbox, OperationContext context) throws ServiceException {
        Folder fstFolder = galMbox.getFolderById(context, Mailbox.ID_FOLDER_CONTACTS);
        Folder sndFolder = galMbox.getFolderByPath(context, SECOND_GAL_FOLDER);
        int fstId = fstFolder.getId();
        int sndId = sndFolder.getId();
        return fstFolder.getItemCount() > 0 ? new Pair<Integer, Integer>(new Integer(fstId), new Integer(sndId)) :
            new Pair<Integer, Integer>(new Integer(sndId), new Integer(fstId));
    }
}
