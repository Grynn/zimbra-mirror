/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
