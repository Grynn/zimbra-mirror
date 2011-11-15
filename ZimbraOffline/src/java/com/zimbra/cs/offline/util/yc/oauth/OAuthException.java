/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yc.oauth;

import com.zimbra.cs.offline.util.yc.YContactException;


public class OAuthException extends YContactException {

    private static final long serialVersionUID = 1738407396643201340L;

    public static final String BAD_REQUEST = "oauth.Bad_Request"; // 400
    public static final String UNAUTHORIZED = "oauth.Unauthorized"; // 401
    public static final String FORBIDDEN = "oauth.Forbidden"; // 403
    public static final String INTERNAL_ERROR = "oauth.Internal_Error"; // 500
    public static final String UNSUPPORTED = "oauth.Unsupported"; // 501
    public static final String EXCESSIVE_REQ = "oauth.ExcessiveRequests"; // 999

    protected OAuthException(String message, String code, boolean isReceiversFault, Throwable cause,
            Argument[] arguments) {
        super(message, code, isReceiversFault, cause, arguments);
    }

    public static OAuthException BAD_REQUEST(String phase) {
        String msg = " .You omitted a required parameter or Yahoo! couldn't make sense of a parameter you supplied. "
                + "Maybe it was a unrecognized User token or perhaps a location Yahoo! couldn't make sense of.";
        return new OAuthException("bad request (400) at " + phase + msg, BAD_REQUEST, SENDERS_FAULT, null, null);
    }

    public static OAuthException UNAUTHORIZED(String phase) {
        String msg = " .This error is normally caused by a problem with the OAuth parameters attached to your request. "
                + "The OAuth signature or OAuth Verifier may be incorrect or one of the tokens is in a bad state";
        return new OAuthException("unauthonized error (401) at " + phase + msg, UNAUTHORIZED, SENDERS_FAULT, null, null);
    }

    public static OAuthException FORBIDDEN(String phase) {
        String msg = ". User hasn't granted your application permission to complete the requested operation";
        return new OAuthException("forbidden error (403) at " + phase + msg, FORBIDDEN, SENDERS_FAULT, null, null);
    }

    public static OAuthException INTERNAL_ERROR(String phase) {
        String msg = " .An internal error within Yahoo! occured";
        return new OAuthException("internal error (500) at " + phase + msg, INTERNAL_ERROR, RECEIVERS_FAULT, null, null);
    }

    public static OAuthException UNSUPPORTED(String phase) {
        String msg = " .Unsupported HTTP Method";
        return new OAuthException("unsupported error (501) at " + phase + msg, UNSUPPORTED, SENDERS_FAULT, null, null);
    }
    
    public static OAuthException EXCESSIVE_REQ(String phase) {
        String msg = " .Excessive OAuth requests";
        return new OAuthException("excessive OAuth requests (999) at " + phase + msg, EXCESSIVE_REQ, SENDERS_FAULT, null, null);
    }

    public static OAuthException UNKNOWN_ERROR(int errorCode, String phase) {
        return new OAuthException("Unknown error (" + errorCode + ") at " + phase, "" + errorCode, SENDERS_FAULT, null, null);
    }

    public static OAuthException handle(int errorCode, String phase) {
        switch (errorCode) {
        case 400:
            return BAD_REQUEST(phase);
        case 401:
            return UNAUTHORIZED(phase);
        case 403:
            return FORBIDDEN(phase);
        case 500:
            return INTERNAL_ERROR(phase);
        case 501:
            return UNSUPPORTED(phase);
        case 999:
            return EXCESSIVE_REQ(phase);
        default:
            return UNKNOWN_ERROR(errorCode, phase);
        }
    }
}
