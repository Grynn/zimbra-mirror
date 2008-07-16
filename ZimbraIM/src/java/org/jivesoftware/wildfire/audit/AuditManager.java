/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.audit;

import java.util.Collection;
import java.util.Iterator;

/**
 * Manages and directs server message auditing behavior. Turning on
 * all auditing options can produce copious amounts of data and
 * significantly slow the server as it saves the data to persistent storage.<p>
 *
 * Auditing currently saves audit data to a raw XML file
 * which can later be processed and mined for information.
 *
 * @author Iain Shigeoka
 */
public interface AuditManager {

    // Presence transitions
    public static final int PRESENCE_UNAVAILABLE_AVAILABLE = 1;
    public static final int PRESENCE_AVAILABLE_AVAILABLE = 2;
    public static final int PRESENCE_AVAILABLE_UNAVAILABLE = 4;
    public static final int PRESENCE_UNAVAILABLE_UNAVAILABLE = 8;

    /**
     * Determines if auditing is enabled at all.
     *
     * @return true if auditing is enabled, false indicates no auditing will occur
     */
    boolean isEnabled();

    /**
     * Factory method for creating auditors that are configured by this
     * audit manager.
     *
     * @return a new auditor that will obey the configuration of the audit manager.
     */
    Auditor getAuditor();

    /**
     * Returns the maximum size in megabytes that all audit log files may have. When the
     * limit is reached oldest audit log files will be removed until total size is under
     * the limit.
     *
     * @return the maximum size of all audit logs in megabytes.
     */
    int getMaxTotalSize();

    /**
     * Obtain the maximum size of audit log files in megabytes.
     * Logs that exceed the max size will be rolled over to another
     * file.
     *
     * @return the maximum size of an audit log in megabytes.
     */
    int getMaxFileSize();

    /**
     * Returns the maximum number of days to keep audit information. Once the limit
     * has been reached audit files that contain information that exceed the limit
     * will be deleted.
     *
     * @return the maximum number of days to keep audit information
     *         or -1 for unlimited
     */
    int getMaxDays();

    /**
     * Returns the time in milliseconds between successive executions of the task that will save
     * the queued audited packets to a permanent store.
     *
     * @return the time in milliseconds between successive executions of the task that will save
     *         the queued audited packets to a permanent store.
     */
    int getLogTimeout();

    /**
     * Returns the absolute path to the directory where the audit log files will be saved.
     *
     * @return the absolute path to the directory where the audit log files will be saved.
     */
    String getLogDir();

    /**
     * <p>Determines if the server will audit all message packets.</p>
     * <p>This is a speed optimization and convenience for logging all message packets
     * rather than using an XPath expression.</p>
     *
     * @return true if all messages are to be audited
     */
    boolean isAuditMessage();

    /**
     * <p>Determines if the server will audit all presence packets.</p>
     * <p>This is a speed optimization and convenience for logging all presence packets
     * rather than using an XPath expression.</p>
     *
     * @return True if all presence are to be audited
     */
    boolean isAuditPresence();

    /**
     * <p>Determines if the server will audit all iq packets.</p>
     * <p>This is a speed optimization and convenience for logging all iq packets
     * rather than using an XPath expression.</p>
     *
     * @return True if all iq are to be audited
     */
    boolean isAuditIQ();

    /**
     * Determines if the server will audit packets using XPath expressions.
     * XPath expressions provide a lot of power in specifying what is logged.
     * However, it is much more compute intensive than other techniques and requires
     * all packets be transformed into DOM objects (which can be computationally expensive).
     *
     * @return true if XPath expressions should be audited.
     */
    boolean isAuditXPath();

    /**
     * Adds an XPath expression to be used for filtering packets to be audited.
     * XPath expressions aren't evaluated or used for filtering unless isAuditXPath()
     * returns true.
     *
     * @param xpathExpression the xpath expression to add to the list of auditing filters.
     */
    void addXPath(String xpathExpression);

    /**
     * <p>Removes the XPath expression from the set being used for filtering packets to be audited.</p>
     * <p>XPath expressions aren't evaluated or used for filtering unless isAuditXPath()
     * returns true.</p>
     *
     * @param xpathExpression The xpath expression to remove from the list of auditing filters
     */
    void removeXPath(String xpathExpression);

    /**
     * <p>Obtain an iterator over the XPath expressions (Strings) currently registered
     * with the audit manager.</p>
     * <p>XPath expressions aren't evaluated or used for filtering unless isAuditXPath()
     * returns true.</p>
     *
     * @return An iterator of all XPath expressions the audit manager is using
     */
    Iterator getXPathFilters();

    /**
     * Returns the list of usernames that won't be audited. Packets sent or received by any of
     * these users will be ignored by the auditor.
     *
     * @return the list of usernames that won't be audited.
     */
    Collection<String> getIgnoreList();
}