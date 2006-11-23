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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.imap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.cs.mailbox.Tag;

public class ImapFlagCache {
    static final class ImapFlag {
        String  mName;
        String  mImapName;
        int     mId;
        long    mBitmask;
        boolean mPositive;
        boolean mPermanent;
        boolean mListed;

        static final boolean VISIBLE = true, HIDDEN = false;

        ImapFlag(String name, Tag ltag, boolean positive) {
            mName = ltag.getName();  mImapName  = normalize(name, mId);
            mId   = ltag.getId();    mBitmask   = ltag.getBitmask();
            mPositive = positive;    mPermanent = true;
            mListed = VISIBLE;
        }

        ImapFlag(String name, short bitmask, boolean listed) {
            mName = name;      mImapName  = name;
            mId   = 0;         mBitmask   = bitmask;
            mPositive = true;  mPermanent = false;
            mListed = listed;
        }

        private String normalize(String name, int id) {
            String imapName = name.replaceAll("[ *(){%*\\]\\\\]+", "");
            if (name.startsWith("\\"))
                imapName = '\\' + imapName;
            if (!name.equals(""))
                return imapName;
            return ":FLAG" + Tag.getIndex(id);
        }

        @Override
        public String toString()  { return mImapName; }
    }


    Map<String, ImapFlagCache.ImapFlag> mNames  = new LinkedHashMap<String, ImapFlagCache.ImapFlag>();
    Map<Long, ImapFlagCache.ImapFlag> mBitmasks = new HashMap<Long, ImapFlagCache.ImapFlag>();


    ImapFlagCache.ImapFlag cache(ImapFlagCache.ImapFlag i4flag) {
        mNames.put(i4flag.mImapName.toUpperCase(), i4flag);
        Long bitmask = new Long(i4flag.mBitmask);
        if (!mBitmasks.containsKey(bitmask))
            mBitmasks.put(bitmask, i4flag);
        return i4flag;
    }

    void uncacheTag(int tagId) {
        if (!Tag.validateId(tagId))
            return;
        int index = Tag.getIndex(tagId);
        ImapFlagCache.ImapFlag i4flag = mBitmasks.remove(new Long(1L << index));
        if (i4flag != null)
            mNames.remove(i4flag.mImapName.toUpperCase());
    }

    void clear() {
        mNames.clear();
        mBitmasks.clear();
    }


    ImapFlagCache.ImapFlag getByName(String name) {
        return mNames.get(name.toUpperCase());
    }

    ImapFlagCache.ImapFlag getByMask(long mask) {
        return mBitmasks.get(mask);
    }


    List<String> listNames(boolean permanentOnly) {
        List<String> names = new ArrayList<String>();
        for (Map.Entry<String, ImapFlagCache.ImapFlag> entry : mNames.entrySet()) {
            ImapFlagCache.ImapFlag i4flag = entry.getValue();
            if (i4flag.mListed && (!permanentOnly || i4flag.mPermanent))
                names.add(i4flag.mImapName);
        }
        return names;
    }
}
