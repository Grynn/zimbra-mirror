/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZContact;
import com.zimbra.cs.zclient.ZFeatures;
import com.zimbra.cs.zclient.ZFilterRule;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZIdentity;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPrefs;
import com.zimbra.cs.zclient.ZSignature;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ZMailboxBean {

    private ZMailbox mMbox;
    
    public ZMailboxBean(ZMailbox mbox) {
        mMbox = mbox;
    }
    
    public String getName() throws ServiceException { return mMbox.getName(); }
    
    public long getSize() throws ServiceException { return mMbox.getSize(); }
       
    private ZFolderBean getFolderBeanById(String id) throws ServiceException {
        ZFolder folder = mMbox.getFolderById(id);
        return folder == null ? null : new ZFolderBean(folder);
    }

    public ZMailbox getMailbox() { return mMbox; }

    public String getNoOp() throws ServiceException { mMbox.noOp(); return "";}
    
    public ZFolderBean getInbox() throws ServiceException { return getFolderBeanById(ZFolder.ID_INBOX); }

    public ZFolderBean getChats() throws ServiceException { return getFolderBeanById(ZFolder.ID_CHATS); }

    public ZFolderBean getTrash() throws ServiceException { return getFolderBeanById(ZFolder.ID_TRASH); }
    
    public ZFolderBean getSpam() throws ServiceException { return getFolderBeanById(ZFolder.ID_SPAM); }
    
    public ZFolderBean getSent() throws ServiceException { return getFolderBeanById(ZFolder.ID_SENT); }
    
    public ZFolderBean getDrafts() throws ServiceException { return getFolderBeanById(ZFolder.ID_DRAFTS); }
    
    public ZFolderBean getCalendar() throws ServiceException { return getFolderBeanById(ZFolder.ID_CALENDAR); }

    public ZFolderBean getTasks() throws ServiceException { return getFolderBeanById(ZFolder.ID_TASKS); }

    public ZFolderBean getBriefcase() throws ServiceException { return getFolderBeanById(ZFolder.ID_BRIEFCASE); }
    
    public ZFolderBean getContacts() throws ServiceException { return getFolderBeanById(ZFolder.ID_CONTACTS); }
    
    public ZFolderBean getAutoContacts() throws ServiceException { return getFolderBeanById(ZFolder.ID_AUTO_CONTACTS); }
 
    public Map<String, List<String>> getAttrs() throws ServiceException { return mMbox.getAccountInfo(false).getAttrs(); }

    public ZGetInfoResult getAccountInfo() throws ServiceException { return mMbox.getAccountInfo(false); }

    public ZGetInfoResult getAccountInfoReload() throws ServiceException { return mMbox.getAccountInfo(true); }

    public List<ZSignature> getSignatures() throws ServiceException { return mMbox.getAccountInfo(false).getSignatures(); }

    public ZPrefs getPrefs() throws ServiceException { return mMbox.getPrefs(); }
    
    public ZFeatures getFeatures() throws ServiceException { return mMbox.getFeatures(); }

    public boolean getHasTags() throws ServiceException { return mMbox.hasTags(); }

    public List<String> getAvailableSkins() throws ServiceException { return mMbox.getAvailableSkins(); }

    public List<ZIdentity> getIdentities()  throws ServiceException { return mMbox.getIdentities(); }

    public List<ZFilterRule> getFilterRules() throws ServiceException { return mMbox.getIncomingFilterRules().getRules(); }

    public List<ZFilterRule> getFilterRulesReload() throws ServiceException { return mMbox.getIncomingFilterRules(true).getRules(); }

    public URI getRestURI(String relativePath) throws ServiceException { return mMbox.getRestURI(relativePath); } 

    public ZIdentity getDefaultIdentity() throws ServiceException {
        for (ZIdentity identity : mMbox.getAccountInfo(false).getIdentities()) {
            if (identity.isDefault()) return identity;
        }
        return null;
    }

	public ZContactBean getMyCard() throws ServiceException {
		ZContact myCard = mMbox.getMyCard();
		return myCard == null ? null : new ZContactBean(myCard);
	}
}
