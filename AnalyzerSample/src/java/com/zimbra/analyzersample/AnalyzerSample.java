/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.analyzersample;

import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;

import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.index.ZimbraAnalyzer;
import com.zimbra.common.service.ServiceException;

/**
 * 
 * A sample Zimbra Extension which provides a custom
 * Lucene analyzer.  The extension must call ZimbraAnalyzer.registerAnalyzer()
 * on startup to register itself with the system.  The custom analyzer is
 * invoked based on the COS or Account setting zimbraTextAnalyzer.
 * 
 */
public class AnalyzerSample implements ZimbraExtension {

    private static Log sLog = LogFactory.getLog(AnalyzerSample.class);

    public AnalyzerSample() {
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.extension.ZimbraExtension#init()
     * 
     * The extension can provide any name to ZimbraAnalyzer.registerAnalyzer() 
     * however that name must be unique or else the registration will fail.
     * 
     */
    public synchronized void init() {
        sLog.info("Initializing "+getName());
        try {
            ZimbraAnalyzer.registerAnalyzer(getName(), new Analyzer());
        } catch (ServiceException e) {
            sLog.error("Error while registering extension "+getName(), e);
        }
    }            

    public synchronized void destroy() {
        sLog.info("Destroying "+getName());
        ZimbraAnalyzer.unregisterAnalyzer(getName());
    }

    public String getName() {
        return "AnalyzerSample";
    }
}
