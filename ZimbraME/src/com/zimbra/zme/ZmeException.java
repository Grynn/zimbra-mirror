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
package com.zimbra.zme;

public class ZmeException extends Exception {
    private static int i = 1;

    public static int ERROR_READING_MSG_FILE = i++;
    public static int MSG_FILE_NOT_FOUND = i++;

    public String mMsgProp;
    public int mErrCode;

    public ZmeException(int errCode,
                        String msg) {
        super(msg);
        mErrCode = errCode;
    }

    public ZmeException(int errCode,
                        String msgProp,
                        String msg) {
        super(msg);
        mErrCode = errCode;
        mMsgProp = msgProp;
    }

    public int getErrorCode() {
        return mErrCode;
    }

    public String getMsgProperty() {
        if (mMsgProp != null)
            return Messages.getMsgForProperty(mMsgProp);
        return null;
    }
}
