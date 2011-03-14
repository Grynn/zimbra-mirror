/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2005, 2006, 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.versioncheck;

import com.zimbra.common.service.ServiceException;
import com.zimbra.qa.unittest.TestVersionCheck;
import com.zimbra.qa.unittest.ZimbraSuite;
import com.zimbra.soap.SoapServlet;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.service.versioncheck.VersionCheckService;

/**
 * @author Greg Solovyev
 */
public class VersionCheckExtension implements ZimbraExtension {
    public static final String EXTENSION_NAME_VERSIONCHECK = "versioncheck";
    
    public void init() throws ServiceException {
        SoapServlet.addService("AdminServlet", new VersionCheckService());
        // XXX bburtin: Disabling test to avoid false positives until bug 54812 is fixed.
        // ZimbraSuite.addTest(TestVersionCheck.class);        
    }

    public void destroy() {

    }
    
    public String getName() {
        return EXTENSION_NAME_VERSIONCHECK;
    }

}
