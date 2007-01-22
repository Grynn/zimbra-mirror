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

public class ZTagLibException extends ServiceException {

    public static final String TAG_EXCEPTION       = "ztaglib.TAG_EXCEPTION";

    public static final String UPLOAD_SIZE_LIMIT_EXCEEDED = "ztaglib.UPLOAD_SIZE_LIMIT_EXCEEDED";

    public static final String UPLOAD_FAILED = "ztaglib.TAG_UPLOAD_FAILED";

    public static final String EMPTY_CONTACT = "ztaglib.EMPTY_CONTACT";      

    private ZTagLibException(String message, String code, boolean isReceiversFault) {
        super(message, code, isReceiversFault);
    }

    private ZTagLibException(String message, String code, boolean isReceiversFault, Throwable cause) {
        super(message, code, isReceiversFault, cause);
    }

    public static ZTagLibException TAG_EXCEPTION(String msg, Throwable cause) {
        return new ZTagLibException(msg, TAG_EXCEPTION, SENDERS_FAULT, cause);
    }

    public static ZTagLibException UPLOAD_SIZE_LIMIT_EXCEEDED(String msg, Throwable cause) {
        return new ZTagLibException(msg, UPLOAD_SIZE_LIMIT_EXCEEDED, SENDERS_FAULT, cause);
    }

    public static ZTagLibException UPLOAD_FAILED(String msg, Throwable cause) {
        return new ZTagLibException(msg, UPLOAD_FAILED, SENDERS_FAULT, cause);
    }

    public static ZTagLibException EMPTY_CONTACT(String msg, Throwable cause) {
        return new ZTagLibException(msg, EMPTY_CONTACT, SENDERS_FAULT, cause);
    }
}
