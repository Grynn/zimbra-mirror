/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline;

import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.DataSource;
import com.zimbra.common.service.ServiceException;

import java.util.List;
import java.util.ArrayList;

public class YMailImport implements DataSource.DataImport {
    private final OfflineImport imapImport;
    private OfflineImport yabImport;
    private OfflineImport calDavImport;
    
    public YMailImport(OfflineDataSource ds) throws ServiceException {
        imapImport = OfflineImport.imapImport(ds);
        if (ds.isContactSyncEnabled()) {
            yabImport = OfflineImport.yabImport(ds.getContactSyncDataSource());
        }
        if (ds.isCalendarSyncEnabled()) {
            calDavImport = OfflineImport.ycalImport(ds.getCalendarSyncDataSource());
        }
    }

    public void test() throws ServiceException {
        if (yabImport != null) {
            yabImport.test();
        }
        if (calDavImport != null) {
            calDavImport.test();
        }
        imapImport.test();
    }

    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        List<ServiceException> errors = new ArrayList<ServiceException>();
        try {
            imapImport.importData(folderIds, fullSync);
        } catch (ServiceException e) {
            errors.add(e);
        }
        if (yabImport != null) {
            try {
                yabImport.importData(folderIds, fullSync);
            } catch (ServiceException e) {
                errors.add(e);
            }
        }
        if (calDavImport != null) {
            try {
                calDavImport.importData(null, fullSync);
            } catch (ServiceException e) {
                errors.add(e);
            }
        }
        if (!errors.isEmpty()) {
            throw errors.get(0);
        }
    }
}
