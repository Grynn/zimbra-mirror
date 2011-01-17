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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

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
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.formatter.ArchiveFormatter.Resolve;

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
    private final String tmpSuffix = ".part";
    
    private static enum Status {SKIPPED, SUCCESS, RESTORED};
    
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
    
    private void makeBackup(Account acct) throws ServiceException {
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
        String prefix = makePrefix(acct.getName()); 
        try {
            String exportName = makeExportName(prefix);
            File tmpFile = new File(exportName+tmpSuffix);
            FileUtil.copy(get.getResponseBodyAsStream(), true, tmpFile);
            FileUtil.rename(tmpFile, new File(exportName));
            OfflineLog.offline.info("Exported account "+acct.getName()+" to "+exportName);
        } catch (IOException e) {
            throw ServiceException.FAILURE("unable to stream response to disk", e);
        }
        
        //purge old backups
        //this regex is coupled to the date format; if one changes the other needs to also
        File[] existing = listStoredBackupFiles(acct); 
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
    

    private String makePrefix(String name) {
        return name+"_";
    }

    private void refreshBackupProperties() throws ServiceException {
        backupsToKeep = BackupPropertyManager.getInstance().getBackupsToKeep();
        exportPath = BackupPropertyManager.getInstance().getBackupPath();
    }

    private StatusTracker statusTracker = new StatusTracker();
    
    private boolean makeBackup(String accountId) throws ServiceException {
        if (!statusTracker.markAccountInProgress(accountId, StatusTracker.Process.BACKUP)) {
            OfflineLog.offline.warn("Backup/Restore already in progress for account "+accountId);
            return false;
        }
        try {
            OfflineLog.offline.info("Backup starting acct="+accountId);
            Account acct = Provisioning.getInstance().getAccount(accountId);
            if (acct == null) {
                throw ServiceException.INVALID_REQUEST("Unknown account "+accountId, null);
            }
            makeBackup(acct);
            OfflineLog.offline.info("Backup complete acct="+accountId);
            return true;
        } finally {
            statusTracker.markAccountDone(accountId);
        }
    }
    
    /**
     * Backup one or more accounts given by id 
     * @throws ServiceException
     */
    public Map<String, String> backupAccounts(String[] accountIds) throws ServiceException {
        if (accountIds != null && accountIds.length > 0) {
            refreshBackupProperties();
            try {
                FileUtil.ensureDirExists(exportPath);
            } catch (IOException e) {
                throw ServiceException.FAILURE("IOException making/testing output dir", e);
            }
            //status one of success or skipped(i.e. ignored due to already in progress) for now; errors are thrown as ServiceException
            Map<String, String> backupStatus = new HashMap<String, String>();
            for (String acct : accountIds) {
                if (makeBackup(acct)) {
                    backupStatus.put(acct, Status.SUCCESS.toString().toLowerCase());
                } else {
                    backupStatus.put(acct, Status.SKIPPED.toString().toLowerCase()); 
                }
            }
            return backupStatus;
        } else {
            throw ServiceException.FAILURE("No accounts selected for backup", null);
        }
    }
    
    /**
     * Backup all accounts which have been configured for automatic backup
     * @throws ServiceException
     */
    public Map<String, String> backupAllAccounts() throws ServiceException {
        return backupAccounts(BackupPropertyManager.getInstance().getBackupAccounts());
    }
    
    private File[] listStoredBackupFiles(Account acct) {
        File dir = new File(exportPath);
        return dir.listFiles(new RegexFilenameFilter(makePrefix(acct.getName())+dateFormatRegex+optionalFileIndexRegex+suffixRegex));
    }
    
    private long extractTimestampFromFilename(String filename, String acctName) throws ServiceException {
        int startIdx = makePrefix(acctName).length();
        int endIdx = startIdx+dateFormat.length();
        String timePart = filename.substring(startIdx, endIdx);
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        try {
            return df.parse(timePart).getTime();
        } catch (ParseException e) {
            throw ServiceException.FAILURE("failed to parse timestamp for file "+filename, e);
        }
    }
    
    public Set<AccountBackupInfo> getStoredBackups() throws ServiceException {
        refreshBackupProperties();
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance(); 
        List<Account> accts = prov.getAllAccounts();
        Account localAcct = prov.getLocalAccount();
        if (!accts.contains(localAcct)) {
            accts.add(localAcct);
        }
        //info for all accts; even if not enabled for backup
        Set<AccountBackupInfo> allInfo = new HashSet<AccountBackupInfo>();
        for (Account acct : accts) {
            AccountBackupInfo acctInfo = new AccountBackupInfo();
            acctInfo.setAccountId(acct.getId());
            allInfo.add(acctInfo);
            File[] backups = listStoredBackupFiles(acct);
            List<BackupInfo> backupInfo = new ArrayList<BackupInfo>(); 
            if (backups != null) {
                for (File file : backups) {
                    BackupInfo bi = new BackupInfo(file);
                    bi.setTimestamp(extractTimestampFromFilename(file.getName(), acct.getName()));
                    backupInfo.add(bi);
                }
            }
            acctInfo.setBackups(backupInfo);
        }
        return allInfo;
    }
    
    public String restoreAccount(String accountId, long timestamp, String resolve) throws ServiceException {
        if (!statusTracker.markAccountInProgress(accountId, StatusTracker.Process.BACKUP)) {
            OfflineLog.offline.warn("Backup/Restore already in progress for account "+accountId);
            return Status.SKIPPED.toString().toLowerCase();
        }
        try {
            Account acct = Provisioning.getInstance().getAccount(accountId);
            File[] stored = listStoredBackupFiles(acct);
            if (stored != null) {
                for (File backupFile : stored) {
                    //timestamp on file doesn't always match file name; could happen if copied
                    long extracted = extractTimestampFromFilename(backupFile.getName(), acct.getName()); 
                    if (extracted == timestamp) {
                        OfflineLog.offline.info("Restoring account "+acct.getName()+" from file "+backupFile.getAbsolutePath());
                        
                        //http://localhost:7733/home/local%40host.local/?fmt=tgz&resolve=reset&callback=ZmImportExportController__callback__import1
                        AuthToken authtoken = AuthProvider.getAuthToken(acct);
                        
                        //make req to userservlet to create backup file
                        String url = UserServlet.getRestUrl(acct);
                        HttpClient client = ZimbraHttpConnectionManager.getInternalHttpConnMgr().newHttpClient();
                        PostMethod post = new PostMethod(url);
                        NameValuePair[] params = new NameValuePair[] {new NameValuePair("fmt", "tgz"), 
                                new NameValuePair("resolve",(resolve != null ? resolve : Resolve.Skip.toString())),
                                new NameValuePair("callback", "ZmImportExportController__callback__import1")};
                        post.setQueryString(params);
                        try {
                            HttpClientUtil.addInputStreamToHttpMethod(post, new FileInputStream(backupFile), backupFile.length(), "application/x-tar");
                        } catch (FileNotFoundException e) {
                            throw ServiceException.UNKNOWN_DOCUMENT("File "+backupFile+" not found", e);
                        }
                        authtoken.encode(client, post, false, acct.getAttr(ZAttrProvisioning.A_zimbraMailHost));
                        try {
                            int statusCode = HttpClientUtil.executeMethod(client, post);
                            if (statusCode != HttpStatus.SC_OK) {
                                throw ServiceException.FAILURE("unable to restore account return code: "+statusCode, null);
                            }
                        } catch (HttpException e) {
                            throw ServiceException.PROXY_ERROR(e, url);
                        } catch (IOException e) {
                            throw ServiceException.FAILURE("unable to restore account due to IOException", e);
                        }
                        //TODO: the response from UserServlet is HTML with warnings etc. encoded in JS function.
                        //not pure SOAP, but could be parsed to provide more feedback beyond success/fail
                        //
                        //<html>
                        //<body onload='onLoad()'>
                        //<script>
                        //function onLoad() {
                        //    window.parent.ZmImportExportController__callback__import1('warn',
                        //    {"Code":{"Value":"soap:Sender"},"Reason":{"Text":"object with that name already exists: Flagged"},"Detail":{"Error":
                        //    {"Code":"mail.ALREADY_EXISTS","Trace":"com.zimbra.cs.mailbox.MailServiceException: object with that name already exists:
                        
                        OfflineLog.offline.info("Finished Restoring account "+acct.getName());
                        return Status.RESTORED.toString().toLowerCase();
                    } 
                }
            }
            throw ServiceException.UNKNOWN_DOCUMENT("No backup with timestamp "+timestamp, null);
        }
        finally {
            statusTracker.markAccountDone(accountId);   
        }
    }    
}
