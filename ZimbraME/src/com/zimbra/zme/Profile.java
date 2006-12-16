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

/**
 * This class represents the ZME users profile. The profile include such things as username and password, and URL (if
 * it is not hard coded in the property file. The profile information is stored to persistent storage.
 */
package com.zimbra.zme;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import java.util.Hashtable;

public class Profile {

    private static String PWORD = "PW";
    private static String UNAME = "UN";
    private static String KEEP_SIGNED_IN = "KI";

    private static String RS_NAME = "profile.dat";

    private static String mUname;
    private static String mPword;
    private static String mKeepSignedIn;

    private static RecordStore mProfileRS;
    private static Hashtable mKeys;

    public Profile()
            throws RecordStoreFullException,
                   RecordStoreException {
        if (mProfileRS == null) {
            try {
                mProfileRS = RecordStore.openRecordStore(RS_NAME, false);
                loadProfile();
            } catch (RecordStoreNotFoundException e) {
                initRecordStore();
            }
        }
    }

    public String getUsername() {
        return mUname;
    }

    public void setUsername(String uname) {
        mUname = uname;
        setRecord((Integer)mKeys.get(UNAME), UNAME, mUname);
    }
    public String getPassword() {
        return mPword;
    }

    public void setPassword(String pword) {
        mPword = pword;
        setRecord((Integer)mKeys.get(PWORD), PWORD, mPword);
    }

    private void initRecordStore()
            throws RecordStoreException {
        mProfileRS = RecordStore.openRecordStore(RS_NAME, true);
        // DO THE DIRTY DEED
    }

    private void loadProfile() {

    }

    private void setRecord(Integer recId,
                           String key,
                           String value) {
        mProfileRS.setRecord(recId, XXXXX);

    }
}
