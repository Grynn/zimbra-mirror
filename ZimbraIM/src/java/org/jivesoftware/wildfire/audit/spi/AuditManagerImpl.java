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
package org.jivesoftware.wildfire.audit.spi;

import org.jivesoftware.util.IMConfig;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.audit.AuditManager;
import org.jivesoftware.wildfire.audit.Auditor;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.interceptor.InterceptorManager;
import org.jivesoftware.wildfire.interceptor.PacketInterceptor;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

import java.io.File;
import java.util.*;

/**
 * Implementation of the AuditManager interface.
 */
public class AuditManagerImpl extends BasicModule implements AuditManager {

    private boolean enabled;
    private boolean auditMessage;
    private boolean auditPresence;
    private boolean auditIQ;
    private boolean auditXPath;
    private List xpath = new LinkedList();
    private AuditorImpl auditor = null;
    /**
     * Max size in bytes that all audit log files may have. When the limit is reached
     * oldest audit log files will be removed until total size is under the limit.
     */
    private int maxTotalSize;
    /**
     * Max size in bytes that each audit log file may have. Once the limit has been
     * reached a new audit file will be created.
     */
    private int maxFileSize;
    /**
     * Max number of days to keep audit information. Once the limit has been reached
     * audit files that contain information that exceed the limit will be deleted.
     */
    private int maxDays;
    private int logTimeout;
    private String logDir;
    private Collection<String> ignoreList = new ArrayList<String>();
    private AuditorInterceptor interceptor;

    public AuditManagerImpl() {
        super("Audit Manager");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Auditor getAuditor() {
        if (auditor == null) {
            throw new IllegalStateException("Must initialize audit manager first");
        }
        return auditor;
    }

    public int getMaxTotalSize() {
        return maxTotalSize;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public int getLogTimeout() {
        return logTimeout;
    }

    public String getLogDir() {
        return logDir;
    }

    public boolean isAuditMessage() {
        return auditMessage;
    }

    public boolean isAuditPresence() {
        return auditPresence;
    }

    public boolean isAuditIQ() {
        return auditIQ;
    }

    public boolean isAuditXPath() {
        return auditXPath;
    }

    public void addXPath(String xpathExpression) {
        xpath.add(xpathExpression);
        saveXPath();
    }

    public void removeXPath(String xpathExpression) {
        xpath.remove(xpathExpression);
        saveXPath();
    }

    private void saveXPath() {
        String[] filters = new String[xpath.size()];
        filters = (String[]) xpath.toArray(filters);
        // TODO: save XPath values!
    }

    public Iterator getXPathFilters() {
        return xpath.iterator();
    }

    public Collection<String> getIgnoreList() {
        return Collections.unmodifiableCollection(ignoreList);
    }

    // #########################################################################
    // Basic module methods
    // #########################################################################

    public void initialize(XMPPServer server) {
        super.initialize(server);
        enabled = IMConfig.XMPP_AUDIT_ACTIVE.getBoolean();
        auditMessage = IMConfig.XMPP_AUDIT_MESSAGE.getBoolean();
        auditPresence = IMConfig.XMPP_AUDIT_PRESENCE.getBoolean();
        auditIQ = IMConfig.XMPP_AUDIT_IQ.getBoolean();
        auditXPath = IMConfig.XMPP_AUDIT_XPATH.getBoolean();
        String[] filters = IMConfig.XMPP_AUDIT_XPATH_STRINGS.getStrings();
        if (filters != null) {
            for (String s : filters) {
                xpath.add(s);
            }
        }
        maxTotalSize = IMConfig.XMPP_AUDIT_TOTALSIZE_MB.getInt();
        maxFileSize = IMConfig.XMPP_AUDIT_FILESIZE_MB.getInt();
        maxDays = IMConfig.XMPP_AUDIT_DAYS.getInt();
        logTimeout = IMConfig.XMPP_AUDIT_LOG_SWEEP_TIME_MS.getInt();
        logDir = IMConfig.XMPP_AUDIT_LOGDIR.getString();
        String ignoreString = IMConfig.XMPP_AUDIT_IGNORE.getString();
        if (ignoreString != null) {
            // Decode the ignore list
            StringTokenizer tokenizer = new StringTokenizer(ignoreString, ", ");
            while (tokenizer.hasMoreTokens()) {
                String username = tokenizer.nextToken();
                ignoreList.add(username);
            }
        }

        auditor = new AuditorImpl(this);
        auditor.setMaxValues(maxTotalSize, maxFileSize, maxDays);
        auditor.setLogDir(logDir);
        auditor.setLogTimeout(logTimeout);

        interceptor = new AuditorInterceptor();
        if (enabled) {
            InterceptorManager.getInstance().addInterceptor(interceptor);
        }
    }

    public void stop() {
        if (auditor != null) {
            auditor.stop();
        }
    }

    private class AuditorInterceptor implements PacketInterceptor {

        public void interceptPacket(Packet packet, Session session, boolean read, boolean processed) {
            if (!processed) {
                // Ignore packets sent or received by users that are present in the ignore list
                JID from = packet.getFrom();
                JID to = packet.getTo();
                if ((from == null || !ignoreList.contains(from.toBareJID())) &&
                        (to == null || !ignoreList.contains(to.toBareJID()))) {
                    auditor.audit(packet, session);
                }
            }
        }
    }
}
