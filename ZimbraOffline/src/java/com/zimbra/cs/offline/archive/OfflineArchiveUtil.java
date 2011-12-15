/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
package com.zimbra.cs.offline.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.google.common.base.Strings;
import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.service.ServiceException.Argument;
import com.zimbra.common.service.ServiceException.InternalArgument;
import com.zimbra.common.util.FileUtil;
import com.zimbra.common.util.ZimbraHttpConnectionManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.ZAttrProvisioning;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.formatter.ArchiveFormatter;
import com.zimbra.cs.service.formatter.ArchiveFormatter.Resolve;

public final class OfflineArchiveUtil {

    private final static String dateFormat = "yyyy-MM-dd_HHmmss";
    private final static SimpleDateFormat df = new SimpleDateFormat(dateFormat);
    public static DocumentBuilder builder = Xml.newDocumentBuilder();
    private static final String ARCHIVE_SUFFIX = ".tgz";
    private static final File ARCHIVE_DIR = new File(OfflineLC.zdesktop_archive_dir.value());

    private OfflineArchiveUtil() {
    }

    private static String getExportFileName(String accountName, String folder) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(accountName).append("_").append(getSanitizedFolderName(folder)).append("_")
                .append(df.format(new Date())).append(ARCHIVE_SUFFIX);
        return buffer.toString();
    }

    private static String getSanitizedFolderName(String folderName) {
        String name = Strings.nullToEmpty(folderName).trim();
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        return folderName.replace("/", "_");
    }

    /**
     * Export a folder into a tgz file
     * @param acct account to export
     * @param folder folder to export
     * @param url REST url
     * @param params query params UserServlet
     * @return tgz file that archived the folder under account
     * @throws ServiceException
     */
    public static File exportArchive(Account acct, Folder folder, String url, NameValuePair[] params)
            throws ServiceException {
        HttpClient client = ZimbraHttpConnectionManager.getInternalHttpConnMgr().newHttpClient();
        GetMethod get = new GetMethod(url);
        get.setQueryString(params);
        AuthToken authtoken = AuthProvider.getAuthToken(acct);
        authtoken.encode(client, get, false, acct.getAttr(ZAttrProvisioning.A_zimbraMailHost));
        try {
            int statusCode = HttpClientUtil.executeMethod(client, get);
            if (statusCode != HttpStatus.SC_OK) {
                if (statusCode == HttpStatus.SC_NO_CONTENT) {
                    //could be empty folder
                    throw ServiceException.RESOURCE_UNREACHABLE("[Archive Util] no content.", null,
                            new InternalArgument(""+HttpStatus.SC_NO_CONTENT, null, Argument.Type.STR));
                } else {
                    throw ServiceException.RESOURCE_UNREACHABLE("[Archive Util] can't fetch account data for export.",
                            null, new InternalArgument(ServiceException.URL, url, Argument.Type.STR));
                }
            }
        } catch (HttpException e) {
            throw ServiceException.PROXY_ERROR(e, url);
        } catch (IOException e) {
            throw ServiceException.RESOURCE_UNREACHABLE("[Archive Util] can't fetch account data for export.", e,
                    new InternalArgument(ServiceException.URL, url, Argument.Type.STR));
        }
        String filename = OfflineArchiveUtil.getExportFileName(acct.getName(), folder.getName());
        File destFile = null;
        try {
            FileUtil.ensureDirExists(ARCHIVE_DIR);
            destFile = new File(ARCHIVE_DIR, filename);
            FileUtil.copy(get.getResponseBodyAsStream(), true, destFile);
            OfflineLog.offline.debug("[Archive Util] Exported from Account %s's Folder [%s] to %s", acct.getName(),
                    folder.getName(), destFile.getAbsolutePath());
        } catch (IOException e) {
            throw ServiceException.FAILURE("[Archive Util] unable to stream response to disk.", e);
        }
        return destFile;
    }

    /**
     * Import tgz file's content into account's certain folder
     * @param acct account to be imported into
     * @param destFolder destination folder to be imported, this should be the parent of tgz file's top folder
     * @param archivedFile tgz file
     * @throws ServiceException
     */
    public static void importArchive(Account acct, Folder destFolder, File archivedFile) throws ServiceException {
        AuthToken authtoken = AuthProvider.getAuthToken(acct);
        String url = UserServlet.getRestUrl(destFolder);
        HttpClient client = ZimbraHttpConnectionManager.getInternalHttpConnMgr().newHttpClient();
        PostMethod post = new PostMethod(url);
        NameValuePair[] params = new NameValuePair[] { new NameValuePair(UserServlet.QP_FMT, "tgz"),
                new NameValuePair(ArchiveFormatter.PARAM_RESOLVE, Resolve.Skip.toString()) };
        post.setQueryString(params);
        try {
            HttpClientUtil.addInputStreamToHttpMethod(post, new FileInputStream(archivedFile), archivedFile.length(),
                    "application/x-tar");
        } catch (FileNotFoundException e) {
            throw ServiceException.UNKNOWN_DOCUMENT("File " + archivedFile + " not found", e);
        }
        authtoken.encode(client, post, false, acct.getAttr(ZAttrProvisioning.A_zimbraMailHost));
        try {
            int statusCode = HttpClientUtil.executeMethod(client, post);
            if (statusCode != HttpStatus.SC_OK) {
                throw ServiceException.FAILURE("[Archive Util] unable to restore account return code: " + statusCode,
                        null);
            }
            OfflineLog.offline.debug("[Archive Util] Imported to Account %s's Folder [%s] from %s", acct.getName(),
                    destFolder.getName(), archivedFile.getAbsolutePath());
        } catch (HttpException e) {
            throw ServiceException.PROXY_ERROR(e, url);
        } catch (IOException e) {
            throw ServiceException.FAILURE("[Archive Util] unable to restore account due to IOException.", e);
        }
    }
}
