/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.backup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.service.ServiceException.Argument;
import com.zimbra.common.service.ServiceException.InternalArgument;
import com.zimbra.common.util.FileUtil;
import com.zimbra.common.util.RegexFilenameFilter;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraHttpConnectionManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ZAttrProvisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.UserServlet;

public class AccountBackupProducer {
    
    private static AccountBackupProducer instance;
    
    public synchronized static AccountBackupProducer getInstance() {
        if (instance == null) {
            instance = new AccountBackupProducer();
        }
        return instance;
    }
    
    protected String exportPath = "/tmp/zd_backup";
    protected int backupsToKeep = 2;
   
    //for simplicity date format not currently configurable. if it changes regex needs to change accordingly
    private final String dateFormat = "yyyy-MM-dd_HHmmss"; 
    private final String dateFormatRegex = "\\d{4}-\\d{2}-\\d{2}_\\d{6}";
    private final String optionalFileIndexRegex = "(-\\d+)?";
    //only support export to tgz for now
    private final String suffix = ".tgz"; 
    private final String suffixRegex = "\\"+suffix;
    
    private String makeExportName(String prefix) {
        StringBuilder sb = new StringBuilder(exportPath);
        sb.append("/");
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        String fileName = prefix + df.format(Calendar.getInstance().getTime()); 
        sb.append(fileName);
        //if we ran more than once in timespan covered by date format we get naming conflict.
        //less likely if date format includes Hms, but possible if just yMd useds 
        //append -1, -2, etc if that occurs
        File checkFile = new File(sb.toString()+suffix);
        String[] existing = checkFile.getParentFile().list(new RegexFilenameFilter(fileName+optionalFileIndexRegex+suffixRegex));
        if (existing != null && existing.length > 0) {
            //find the highest existing index and add one
            int maxIndex = 0;
            for (int i = 0; i < existing.length; i++) {
                String name = existing[i];
                if (name.matches(fileName+"-\\d+"+suffixRegex)) {
                    int index = Integer.parseInt(name.substring(fileName.length()+1,name.length()-4));
                    if (index > maxIndex) {
                        maxIndex = index;
                    }
                }
            }
            sb.append("-"+(maxIndex+1));
        }
        sb.append(suffix);
        return StringUtil.sanitizeFilename(sb.toString());
    }
    
    private synchronized void makeBackup(Account acct) throws ServiceException {
        //synchronized so we don't have two threads deleting each others produced backups..
        //get auth token for this account
        AuthToken authtoken = AuthProvider.getAuthToken(acct);
        
        //make req to userservlet to create backup file
        String url = UserServlet.getRestUrl(acct);
        HttpClient client = ZimbraHttpConnectionManager.getInternalHttpConnMgr().newHttpClient();
        GetMethod get = new GetMethod(url);
        NameValuePair[] params = new NameValuePair[] {new NameValuePair("fmt", "tgz")};
        get.setQueryString(params);
        authtoken.encode(client, get, false, acct.getAttr(ZAttrProvisioning.A_zimbraMailHost));
        try {
            int statusCode = HttpClientUtil.executeMethod(client, get);
            if (statusCode != HttpStatus.SC_OK) {
                throw ServiceException.RESOURCE_UNREACHABLE("can't fetch account data for export", null, new InternalArgument(ServiceException.URL, url, Argument.Type.STR));
            }
        } catch (HttpException e) {
            throw ServiceException.PROXY_ERROR(e, url);
        } catch (IOException e) {
            throw ServiceException.RESOURCE_UNREACHABLE("can't fetch account data for export", e, new InternalArgument(ServiceException.URL, url, Argument.Type.STR));
        }
        
        //extract backup from response
        String prefix = acct.getName()+"_"; 
        try {
            String exportName = makeExportName(prefix);
            FileUtil.copy(get.getResponseBodyAsStream(), true, new File(exportName));
            OfflineLog.offline.info("Exported account "+acct.getName()+" to "+exportName);
        } catch (IOException e) {
            throw ServiceException.FAILURE("unable to stream response to disk", e);
        }
        
        //purge old backups
        File dir = new File(exportPath);
        //this regex is coupled to the date format; if one changes the other needs to also
        File[] existing = dir.listFiles(new RegexFilenameFilter(prefix+dateFormatRegex+optionalFileIndexRegex+suffixRegex));
        if (existing != null) {
            int excess = existing.length - backupsToKeep;
            if (excess > 0) {
                FileUtil.sortFilesByModifiedTime(existing);
                for (int i = 0; i < excess; i++) {
                    File deleteFile = existing[i];
                    try {
                        FileUtil.delete(deleteFile);
                        OfflineLog.offline.info("deleted old backup file ["+deleteFile+"]");
                    } catch (IOException ioe) {
                        OfflineLog.offline.warn("unable to delete file ["+deleteFile+"] due to exception, scheduling to delete on exit", ioe);
                        deleteFile.deleteOnExit();
                    }
                }
            }
        }
    }
    

    private void refreshBackupProperties() throws ServiceException {
        backupsToKeep = BackupPropertyManager.getInstance().getBackupsToKeep();
        exportPath = BackupPropertyManager.getInstance().getBackupPath();
    }
    
    private void makeBackup(String accountId) throws ServiceException {
        Account acct = Provisioning.getInstance().getAccount(accountId);
        if (acct == null) {
            throw ServiceException.INVALID_REQUEST("Unknown account "+accountId, null);
        }
        makeBackup(acct);
    }
    
    /**
     * Backup one or more accounts given by id 
     * @throws ServiceException
     */
    public void backupAccounts(String[] accountIds) throws ServiceException {
        if (accountIds != null && accountIds.length > 0) {
            OfflineLog.offline.info("Backup starting");
            refreshBackupProperties();
            try {
                FileUtil.ensureDirExists(exportPath);
            } catch (IOException e) {
                throw ServiceException.FAILURE("IOException making/testing output dir", e);
            }
            for (String acct : accountIds) {
                makeBackup(acct); 
            }
            OfflineLog.offline.info("Backup complete");
        } else {
            throw ServiceException.FAILURE("No accounts selected for backup", null);
        }
    }
    
    /**
     * Backup all accounts which have been configured for automatic backup
     * @throws ServiceException
     */
    public void backupAllAccounts() throws ServiceException {
        backupAccounts(BackupPropertyManager.getInstance().getBackupAccounts());
    }
}
