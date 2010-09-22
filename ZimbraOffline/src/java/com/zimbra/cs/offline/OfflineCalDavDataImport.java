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

import java.io.IOException;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.DataSourceConfig;
import com.zimbra.cs.datasource.CalDavDataImport;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.client.CalDavClient;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.common.OfflineConstants;

public class OfflineCalDavDataImport extends CalDavDataImport {
    private final String serviceName;
    
    private static final String CALDAV_APPNAME = "Desktop";
    
    public OfflineCalDavDataImport(DataSource ds, String serviceName) throws ServiceException {
        super(ds);
        this.serviceName = serviceName;
    }
    
    public void test() throws ServiceException {
        try {
            DataSourceConfig.Service ks =
                DataSourceManager.getConfig().getService(serviceName);
            String url, path;
            if (ks != null && ((url = ks.getCalDavTargetUrl()) != null &&
                              ((path = ks.getCalDavPrincipalPath()) != null))) {
                OfflineLog.offline.debug("offline caldav login test: url=" + url + " path=" + path);
                CalDavClient client = new CalDavClient(url);
                client.setAppName(CALDAV_APPNAME);
                client.setCredential(getUsername(), getDecryptedPassword());
                try {
                    client.login(path.replaceAll("@USERNAME@", getUsername()));
                } catch (IOException x) {
                    throw ServiceException.FAILURE("caldav login test failed", x);
                }
            } else {
                throw new DavException("offline caldav login test: missing caldav parameters for " + serviceName, 599);
            }
        } catch (DavException x) {
            doCalDavFailures(serviceName, x);
        }
    }

    public void importData(List<Integer> folderIds, boolean fullSync) throws ServiceException {
        if (!fullSync)
            return;

        ZimbraLog.calendar.info("Importing calendar for account '%s'", dataSource.getName());
        try {
    		super.importData(folderIds, fullSync);
    	} catch (ServiceException x) {
    		Throwable t = SystemUtil.getInnermostException(x);
    		if (t instanceof DavException)
    			doCalDavFailures(serviceName, (DavException)t);
    		throw x;
    	}
        ZimbraLog.calendar.info("Finished importing calendar for account '%s'", dataSource.getName());
    }
    
    private static void doCalDavFailures(String serviceName, DavException x) throws ServiceException {
		int status = x.getStatus();
        if (status == 502 && serviceName.equals("yahoo.com")) {
            throw OfflineServiceException.YCALDAV_NEED_UPGRADE();
        } else if (status == 404 && serviceName.equals("gmail.com")) {
            throw OfflineServiceException.GCALDAV_NEED_ENABLE();
        } else {
            OfflineLog.offline.debug("caldav login failed: service=%s; status=%d", serviceName, status);
            throw OfflineServiceException.CALDAV_LOGIN_FAILED();
        }
    }
    
    @Override
    protected String getTargetUrl() {
        DataSourceConfig.Service ks = ((OfflineDataSource)dataSource).getKnownService();
        return ks != null ? ks.getCalDavTargetUrl() : null;
    }
    
    @Override
    protected String getPrincipalUrl() {
        DataSourceConfig.Service ks = ((OfflineDataSource)dataSource).getKnownService();
        if (ks != null) {
            String path = ks.getCalDavPrincipalPath();
            if (path != null) {
                return path.replaceAll("@USERNAME@", dataSource.getUsername());
            }
        }
        return null;
    }
    
    @Override
    protected String getAppName() {
        return CALDAV_APPNAME;
    }
    
    @Override protected byte getDefaultColor() {
        if (serviceName.equals("yahoo.com"))
            return 4;
        else if (serviceName.equals("gmail.com"))
            return 5;
        return 0;
    }
    
    @Override
    protected int getRootFolderId(DataSource ds) throws ServiceException {
    	return ds.getIntAttr(OfflineConstants.A_zimbraDataSourceCalendarFolderId, ds.getFolderId());
    }
}
