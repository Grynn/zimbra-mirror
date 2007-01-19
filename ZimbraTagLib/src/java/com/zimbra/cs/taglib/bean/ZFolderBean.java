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

import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZGrant;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMountpoint;
import com.zimbra.cs.zclient.ZSearchFolder;

import java.util.List;

public class ZFolderBean {
    
    private ZFolder mFolder;
    
    public ZFolderBean(ZFolder folder) {
        mFolder = folder;
    }

    public ZFolderBean getParent() { return mFolder.getParent() == null ? null : new ZFolderBean(mFolder.getParent()); }

    public String getId() { return mFolder.getId(); }

    /** Returns the folder's name.  Note that this is the folder's
     *  name (e.g. <code>"foo"</code>), not its absolute pathname
     *  (e.g. <code>"/baz/bar/foo"</code>).
     * 
     * @see #getPath()
     * @return name
     * 
     */
    public String getName() { return mFolder.getName(); }

    /** Returns the folder's absolute path.  Paths are UNIX-style with 
     *  <code>'/'</code> as the path delimiter.  Paths are relative to
     *  the user root folder,
     *  which has the path <code>"/"</code>.  So the Inbox's path is
     *  <code>"/Inbox"</code>, etc.
     * @return path
     */
    public String getPath() { return mFolder.getPath(); }

    /** Returns the folder's  path relative to the root
     * @return path
     */
    public String getRootRelativePath() { return mFolder.getRootRelativePath(); }

    /**
     * @return Returns the folder's absolute path, with special chars in the names
     * URL encoded.
     *
     */
    public String getPathUrlEncoded() { return mFolder.getPathUrlEncoded(); }

    /**
     * 
     * @return parent id of folder, or null if root folder.
     */
    public String getParentId() { return mFolder.getParentId(); }

    /**
     * @return number of unread items in folder
     */
    public int getUnreadCount() { 
        return getIsDrafts() ? getMessageCount() : mFolder.getUnreadCount();
    }
    
    public boolean getHasUnread() { return getUnreadCount() > 0; }

    /**
     * @return number of unread items in folder
     */
    public int getMessageCount() { return mFolder.getMessageCount(); }
    
    /** @return Returns the "hint" as to which view to use to display the folder's
     *  contents.
     */
    public String getDefaultView() { return mFolder.getDefaultView().name(); } 
    
    /**
     *  checked in UI (#), exclude free/(b)usy info, IMAP subscribed (*)
     * @return the flags
     */
    public String getFlags() { return mFolder.getFlags(); }

    public boolean getHasFlags() { return mFolder.hasFlags(); }
    
    public boolean getIsCheckedInUI() { return mFolder.isCheckedInUI(); }

    public boolean getIsExcludedFromFreeBusy() { return mFolder.isExcludedFromFreeBusy(); }

    public boolean getIsIMAPSubscribed() { return mFolder.isIMAPSubscribed(); }

    /**
     * range 0-127; defaults to 0 if not present; client can display only 0-7
     * 
     * @return color
     */
    public String getColor() { return mFolder.getColor().name(); }

    /**
     * remote URL (RSS, iCal, etc) this folder syncs to
     * 
     * @return remote url
     */
    public String getRemoteURL() { return mFolder.getRemoteURL(); }
    
    /**
     * for remote folders, the access rights the authenticated user has on the folder.
     * 
     * @return effective perms
     */
    public String getEffectivePerm() { return mFolder.getEffectivePerm(); }
    
    /**
     * url to the folder on rest interface for rest-enabled apps (such as wiki and notebook)
     * 
     * @return URL, if returned from server.
     */
    public String getRestURL() { return mFolder.getRestURL(); }
    
    /**
     * @return return grants or empty list if no grants
     */
    public List<ZGrant> getGrants() { return mFolder.getGrants(); }

    /**
     * @return sub folders, or empty list if no sub folders
     */
    public List<ZFolder> getSubFolders() { return mFolder.getSubFolders(); }

    public boolean getHasChildren() { return !mFolder.getSubFolders().isEmpty(); }
    
    public boolean getIsSearchFolder() { return mFolder instanceof ZSearchFolder; }
    
    public String getQuery() { return getIsSearchFolder() ? ((ZSearchFolder) mFolder).getQuery() : ""; }
    
    //public ZFolder getSubFolderByPath(String path);
    
    public boolean getIsInbox() { return mFolder.getId().equals(ZFolder.ID_INBOX); }
    public boolean getIsTrash() { return mFolder.getId().equals(ZFolder.ID_TRASH); }
    public boolean getIsSpam() { return mFolder.getId().equals(ZFolder.ID_SPAM); }
    public boolean getIsSent() { return mFolder.getId().equals(ZFolder.ID_SENT); }    
    public boolean getIsDrafts() { return mFolder.getId().equals(ZFolder.ID_DRAFTS); }
    public boolean getIsContacts() { return mFolder.getId().equals(ZFolder.ID_CONTACTS); }
    public boolean getIsCalendar() { return mFolder.getId().equals(ZFolder.ID_CALENDAR); }    
    public boolean getIsNotebook() { return mFolder.getId().equals(ZFolder.ID_NOTEBOOK); }    
    public boolean getIsAutoContacts() { return mFolder.getId().equals(ZFolder.ID_AUTO_CONTACTS); }
    
    public boolean getIsMailView() { 
        ZFolder.View view = mFolder.getDefaultView();
        return view == null || view == ZFolder.View.message || view == ZFolder.View.conversation;
    }
    
    public boolean getIsNullView() { return mFolder.getDefaultView() == null; }    
    public boolean getIsMessageView() { return mFolder.getDefaultView() == ZFolder.View.message; }
    public boolean getIsContactView() { return mFolder.getDefaultView() == ZFolder.View.contact; }    
    public boolean getIsConversationView() { return mFolder.getDefaultView() == ZFolder.View.conversation; }        
    public boolean getIsAppointmentView() { return mFolder.getDefaultView() == ZFolder.View.appointment; }
    public boolean getIsWikiView() { return mFolder.getDefaultView() == ZFolder.View.wiki; }
    
    public boolean getIsSystemFolder() { return mFolder.isSystemFolder(); }
    
    public boolean getIsMountPoint() { return mFolder instanceof ZMountpoint; }

    public boolean getIsFeed() { return mFolder.getRemoteURL() != null; }

    public String getCanonicalId() { return (mFolder instanceof ZMountpoint) ? ((ZMountpoint)mFolder).getCanonicalRemoteId() : mFolder.getId(); }

    private int mDepth = -1;
    
    public synchronized int getDepth() {
        if (mDepth != -1) return mDepth;
        int depth=0;
        String path = getPath();
        for (int i=1; i < path.length(); i++) {
            if (path.charAt(i) == ZMailbox.PATH_SEPARATOR_CHAR) depth++; 
        }
        return depth;
    }

    public boolean getIsMessageMoveTarget() {
        return getIsConversationMoveTarget();
    }

    public boolean getIsConversationMoveTarget() {
        return (getIsMessageView() || getIsConversationView() || getIsNullView()) &&
                !(getIsDrafts() || getIsMountPoint() || getIsSearchFolder() || getRemoteURL() != null) &&
                !getId().equals(ZFolder.ID_CHATS);
    }

    public boolean getIsMessageFolderMarkReadTarget() {
        return (getIsMessageView() || getIsConversationView() || getIsNullView()) &&
                !(getIsDrafts() || getIsMountPoint() || getIsSearchFolder());
    }

    public boolean getIsMessageFolderRenameTarget() {
        return (getIsMessageView() || getIsConversationView() || getIsNullView()) &&
                !(getIsSystemFolder() || getIsSearchFolder());
    }

    public boolean getIsMessageFolderMoveSource() {
        return (getIsMessageView() || getIsConversationView() || getIsNullView()) &&
                !(getIsSystemFolder() || getIsSearchFolder());
    }

    public boolean getIsMessageFolderDeleteTarget() {
        return (getIsMessageView() || getIsConversationView() || getIsNullView()) &&
                !(getIsSystemFolder() || getIsSearchFolder());
    }

    public boolean getIsContactMoveTarget() {
        return (getIsContactView() || getIsTrash()) &&
                !(getIsDrafts() || getIsMountPoint() || getIsSearchFolder() || getRemoteURL() != null);
    }

    public boolean getIsContactCreateTarget() {
        return (getIsContactView()) &&
                !(getIsDrafts() || getIsMountPoint() || getIsSearchFolder() || getRemoteURL() != null);
    }
}
