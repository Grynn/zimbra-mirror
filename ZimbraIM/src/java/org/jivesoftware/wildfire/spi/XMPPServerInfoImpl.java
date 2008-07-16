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
package org.jivesoftware.wildfire.spi;

import org.jivesoftware.util.Version;
import org.jivesoftware.wildfire.XMPPServerInfo;
import org.jivesoftware.wildfire.ConnectionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Collections;

/**
 * Implements the server info for a basic server. Optimization opportunities
 * in reusing this object the data is relatively static.
 *
 * @author Iain Shigeoka
 */
public class XMPPServerInfoImpl implements XMPPServerInfo {

    private Date startDate;
    private Date stopDate;
    private Collection<String> names;
    private Version ver;
    private ConnectionManager connectionManager;

    /**
     * Simple constructor
     *
     * @param serverName the server's serverName (e.g. example.org).
     * @param version the server's version number.
     * @param startDate the server's last start time (can be null indicating
     *      it hasn't been started).
     * @param stopDate the server's last stop time (can be null indicating it
     *      is running or hasn't been started).
     * @param connectionManager the object that keeps track of the active ports.
     */
    public XMPPServerInfoImpl(Collection<String> serverNames, Version version, Date startDate, Date stopDate,
            ConnectionManager connectionManager)
    {
        this.names = serverNames;
        this.ver = version;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.connectionManager = connectionManager;
    }

    public Version getVersion() {
        return ver;
    }

    public Collection<String> getNames() {
        return Collections.unmodifiableCollection(names);
    }
    
    public String getDefaultName() {
        if (names.size() > 0)
            return names.iterator().next();
        else
            return null;
    }
    
    private String packNamesStr() {
        StringBuilder sb = new StringBuilder();
        boolean atStart = true;
        for (String s : names) {
            if (atStart) 
                sb.append(',');
            else
                atStart = false;
            sb.append(s);
        }
        return sb.toString();
    }
    
    static Collection<String> unpackNamesStr(String s) {
        Collection<String> toRet = new ArrayList<String>();
        s = s.trim();
        while (s.length() > 0) {
            int off = s.indexOf(',');
            if (off > 0) {
                toRet.add(s.substring(0, off));
                s = s.substring(off+1);
            } else {
                toRet.add(s);
                s = "";
            }
        }
        return toRet;
    }

    public Date getLastStarted() {
        return startDate;
    }

    public Date getLastStopped() {
        return stopDate;
    }

    public Iterator getServerPorts() {
        if (connectionManager == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        else {
            return connectionManager.getPorts();
        }
    }
}