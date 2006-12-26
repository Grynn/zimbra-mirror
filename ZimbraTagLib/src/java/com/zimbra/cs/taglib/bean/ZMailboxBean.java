/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZFeatures;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZPrefs;

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
    
    public ZFolderBean getInbox() throws ServiceException { return getFolderBeanById(ZFolder.ID_INBOX); }
    
    public ZFolderBean getTrash() throws ServiceException { return getFolderBeanById(ZFolder.ID_TRASH); }
    
    public ZFolderBean getSpam() throws ServiceException { return getFolderBeanById(ZFolder.ID_SPAM); }
    
    public ZFolderBean getSent() throws ServiceException { return getFolderBeanById(ZFolder.ID_SENT); }
    
    public ZFolderBean getDrafts() throws ServiceException { return getFolderBeanById(ZFolder.ID_DRAFTS); }
    
    public ZFolderBean getCalendar() throws ServiceException { return getFolderBeanById(ZFolder.ID_CALENDAR); }
    
    public ZFolderBean getContacts() throws ServiceException { return getFolderBeanById(ZFolder.ID_CONTACTS); }
    
    public ZFolderBean getAutoContacts() throws ServiceException { return getFolderBeanById(ZFolder.ID_AUTO_CONTACTS); }
 
    public Map<String, List<String>> getAttrs() throws ServiceException { return mMbox.getAccountInfo(false).getAttrs(); }

    public ZGetInfoResult getAccountInfo() throws ServiceException { return mMbox.getAccountInfo(false); }

    public ZGetInfoResult getAccountInfoReload() throws ServiceException { return mMbox.getAccountInfo(true); }

    public ZPrefs getPrefs() throws ServiceException { return mMbox.getPrefs(); }
    
    public ZFeatures getFeatures() throws ServiceException { return mMbox.getFeatures(); }

    public boolean getHasTags() throws ServiceException { return !mMbox.getAllTags().isEmpty(); }

    public List<String> getAvailableSkins() throws ServiceException { return mMbox.getAvailableSkins(); }
    
}
