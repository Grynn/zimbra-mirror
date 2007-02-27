/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline;

import com.zimbra.common.util.LogFactory;

public class OfflineLog {
    /** The "zimbra.offline" logger. For offline sync logs. */
    public static final com.zimbra.common.util.Log offline = LogFactory.getLog("zimbra.offline");

    /** The "zimbra.offline.request" logger. For recording SOAP traffic
     *  sent to the remote server. */
    public static final com.zimbra.common.util.Log request = LogFactory.getLog("zimbra.offline.request");

    /** The "zimbra.offline.response" logger. For recording SOAP traffic
     *  received from the remote server. */
    public static final com.zimbra.common.util.Log response = LogFactory.getLog("zimbra.offline.response");
}
