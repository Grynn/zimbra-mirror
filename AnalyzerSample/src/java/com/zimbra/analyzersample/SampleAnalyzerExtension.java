/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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

package com.zimbra.analyzersample;

import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;

import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.index.ZimbraAnalyzer;
import com.zimbra.common.service.ServiceException;

/**
 * A sample Zimbra Extension which provides a custom Lucene analyzer.
 * <p>
 * The extension must call {@link ZimbraAnalyzer#registerAnalyzer(String, org.apache.lucene.analysis.Analyzer)})
 * on startup to register itself with the system. The custom analyzer is invoked based on the COS or Account setting
 * {@code zimbraTextAnalyzer}.
 */
public class SampleAnalyzerExtension implements ZimbraExtension {
    private static final Log LOG = LogFactory.getLog(SampleAnalyzerExtension.class);

    @Override
    public synchronized void init() {
        LOG.info("Initializing %s", getName());
        try {
            // The extension can provide any name, however that name must be unique or else the registration will fail.
            ZimbraAnalyzer.registerAnalyzer(getName(), new SampleAnalyzer());
        } catch (ServiceException e) {
            LOG.error("Error while registering extension %s", getName(), e);
        }
    }

    @Override
    public synchronized void destroy() {
        LOG.info("Destroying %s", getName());
        ZimbraAnalyzer.unregisterAnalyzer(getName());
    }

    @Override
    public String getName() {
        return "SampleAnalyzer";
    }

}
