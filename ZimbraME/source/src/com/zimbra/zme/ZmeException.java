/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme;

public class ZmeException extends Exception {
    private static int i = 1;

    public static final int ERROR_READING_MSG_FILE = i++;
    public static final int IO_ERROR = i++;
    public static final int MSG_FILE_NOT_FOUND = i++;
    public static final int OP_IN_PROGRESS = i++;
    public static final int PARSER_ERROR = i++;

    public int mErrCode;

    public ZmeException(int errCode,
                        String msg) {
        super(msg);
        mErrCode = errCode;
    }

}
