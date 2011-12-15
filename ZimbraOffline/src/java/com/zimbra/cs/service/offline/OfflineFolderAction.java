/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.service.ServiceException.Argument;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.FileUtil;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbMailItem.QueryParams;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.SearchFolder;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.archive.OfflineArchiveUtil;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.mail.FolderAction;
import com.zimbra.cs.service.mail.ItemActionHelper;
import com.zimbra.cs.service.mail.ItemActionHelper.Op;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineFolderAction extends FolderAction {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException, SoapFaultException {
        MailboxManager mmgr = MailboxManager.getInstance();
        if (!(mmgr instanceof OfflineMailboxManager))
            return super.handle(request, context);

        Element action = request.getElement(MailConstants.E_ACTION);
        String operation = action.getAttribute(MailConstants.A_OPERATION).toLowerCase();
        // if action is folder move from account to Local Account, don't proxy it.
        if (StringUtil.equal(Op.MOVE.toString(), operation)) {
            String target = action.getAttribute(MailConstants.A_FOLDER);
            if (!StringUtil.isNullOrEmpty(target)) {
                String targetAccountId = target.split(":")[0];
                if (!StringUtil.isNullOrEmpty(targetAccountId)
                        && OfflineConstants.LOCAL_ACCOUNT_ID.equals(targetAccountId)) {
                    String source = action.getAttribute(MailConstants.A_ID);
                    String sourceAccountId = source.split(":")[0];
                    Mailbox mbox = OfflineMailboxManager.getOfflineInstance().getMailboxByAccountId(sourceAccountId);
                    ZimbraSoapContext zsc = getZimbraSoapContext(context);
                    OperationContext octxt = getOperationContext(zsc, context);

                    moveToLocalFolder(source, target, mbox, zsc, octxt);

                    Element resp = getZimbraSoapContext(context).createElement(MailConstants.FOLDER_ACTION_RESPONSE);
                    return resp;
                } else {
                    throw ServiceException.INVALID_REQUEST("unsupported operation, expect local account id, action:("
                            + action + ")", null);
                }
            }
        }
        if (!operation.equals(OP_REFRESH) && !operation.equals(OP_IMPORT) && !operation.equals(OP_GRANT)
                && !operation.equals(OP_REVOKE)) {
            return super.handle(request, context);
        }

        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        String zid = "";
        String folderId = action.getAttribute(MailConstants.A_ID);
        int pos = folderId.indexOf(':');
        if (pos > 0) {
            zid = folderId.substring(0, pos);
            folderId = folderId.substring(pos + 1);
        }
        int id = Integer.parseInt(folderId);
        Folder folder = mbox.getFolderById(octxt, id);

        if (!(mbox instanceof ZcsMailbox)) {
            // load rss feed locally for non-zimbra accounts
            if ((operation.equals(OP_REFRESH) || operation.equals(OP_IMPORT)) && !folder.getUrl().equals(""))
                return super.handle(request, context);
            else
                throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: "
                        + mbox.getClass().getSimpleName());
        }

        ZcsMailbox ombx = (ZcsMailbox) mbox;
        boolean traceOn = ombx.getOfflineAccount().isDebugTraceEnabled();
        boolean quietWhenOffline = !operation.equals(OP_GRANT) && !operation.equals(OP_REVOKE);

        ombx.pushNewFolder(octxt, id, false, zsc);
        if (operation.equals(OP_REFRESH) || operation.equals(OP_IMPORT)) {
            // before doing anything, make sure all data sources are pushed to the server
            ombx.sync(true, traceOn);
        }

        // even if folder is not new, it might have been renumbered by a background sync. getFolderById() checks
        // renumbered so we are covered in either case
        folder = mbox.getFolderById(octxt, id);
        String renumFolderId = Integer.toString(folder.getId());
        if (!folderId.equals(renumFolderId)) {
            action.addAttribute(MailConstants.A_ID, zid.equals("") ? renumFolderId : zid + ":" + renumFolderId);
        }
        // proxy this operation to the remote server
        Element response = ombx.proxyRequest(request, zsc.getResponseProtocol(), quietWhenOffline, operation);
        if (response != null)
            response.detach();

        if (operation.equals(OP_REFRESH) || operation.equals(OP_IMPORT)) {
            // and get a head start on the sync of the newly-pulled-in messages
            ombx.sync(true, traceOn);
        }

        return response;
    }

    /**
     * @param source sourceAcctId:folderId
     * @param target localAcctId:folderId
     * @param mbox source account's mailbox
     * @param octxt Operation context
     * @throws ServiceException
     */
    private void moveToLocalFolder(final String source, final String target, final Mailbox mbox,
            final ZimbraSoapContext zsc, final OperationContext octxt) throws ServiceException {

        final String sourceAccount = source.split(":")[0];
        final int sourceFolderId = Integer.parseInt(source.split(":")[1]);
        final int targetParentFolderId = Integer.parseInt(target.split(":")[1]);

        new Thread("folder-move-" + source) {

            @Override
            public void run() {
                try {
                    Folder srcFolder = (Folder) mbox.getItemById(octxt, sourceFolderId, MailItem.TYPE_FOLDER);

                    String url = UserServlet.getRestUrl(srcFolder);
                    NameValuePair[] params = new NameValuePair[] { new NameValuePair(UserServlet.QP_FMT, "tgz"),
                            new NameValuePair(UserServlet.QP_TYPES, "message"),
                            new NameValuePair(UserServlet.QP_NOHIERARCHY, "1") };
                    File backupFile = null;

                    OfflineLog.offline.debug(
                            "starting folder move, from folder %d of account %s, to folder %d under Local folder",
                            sourceFolderId, source.split(":")[0], targetParentFolderId);

                    long exportStart = System.currentTimeMillis();
                    OfflineSyncManager.getInstance().registerDialog(
                            sourceAccount,
                            new Pair<String, String>(OfflineDialogAction.DIALOG_TYPE_FOLDER_MOVE_START,
                                    OfflineDialogAction.DIALOG_TYPE_FOLDER_MOVE_START_MSG));

                    // export src folder to backup file
                    boolean isSrcFolderEmpty = false;
                    try {
                        backupFile = OfflineArchiveUtil.exportArchive(mbox.getAccount(), srcFolder, url, params);
                    } catch (Exception e) {
                        if (e instanceof ServiceException
                                && ServiceException.RESOURCE_UNREACHABLE.equals(((ServiceException) e).getCode())) {
                            // might be empty folder
                            Argument arg = ((ServiceException) e).getArgs()[0];
                            if (String.valueOf(HttpStatus.SC_NO_CONTENT).equals(arg.getName())) {
                                isSrcFolderEmpty = true;
                            } else {
                                OfflineLog.offline.debug("[Folder Move] resource unreachable, export tgz file failed",
                                        e);
                                throw e;
                            }
                        } else {
                            OfflineLog.offline.debug("[Folder Move] export tgz file failed", e);
                            throw e;
                        }
                    }
                    Mailbox destMbox = OfflineMailboxManager.getOfflineInstance().getLocalAccountMailbox();
                    if (isSrcFolderEmpty) {
                        if (srcFolder instanceof SearchFolder) {
                            // if it's search folder, append original account's root folder.
                            SearchFolder srcSearchFolder = (SearchFolder) srcFolder;
                            StringBuilder query = new StringBuilder();
                            query.append(srcSearchFolder.getQuery()).append(" (underid:\"").append(sourceAccount)
                                    .append(":").append(Mailbox.ID_FOLDER_USER_ROOT).append("\")");
                            destMbox.createSearchFolder(octxt, targetParentFolderId, srcSearchFolder.getName(),
                                    query.toString(), "", srcSearchFolder.getSortField(),
                                    srcSearchFolder.getFlagBitmask(), srcSearchFolder.getColor());
                        } else {
                            // just create a new folder
                            destMbox.createFolder(octxt, srcFolder.getName(), targetParentFolderId,
                                    srcFolder.getDefaultView(), srcFolder.getFlagBitmask(), srcFolder.getColor(),
                                    srcFolder.getUrl());
                        }
                    } else {
                        // import backup file to target folder
                        Account destAcct = OfflineProvisioning.getOfflineInstance().getLocalAccount();
                        try {
                            OfflineArchiveUtil.importArchive(destAcct, destMbox.getFolderById(targetParentFolderId),
                                    backupFile);
                        } catch (Exception e) {
                            OfflineLog.offline.debug("[Folder Move] import tgz file failed", e);
                            if (backupFile != null) {
                                FileUtil.delete(backupFile);
                            }
                            throw e;
                        }
                        // delete backup file
                        if (backupFile != null) {
                            FileUtil.delete(backupFile);
                        }
                    }

                    // delete src folder
                    if (srcFolder instanceof SearchFolder) {
                        mbox.delete(octxt, sourceFolderId, MailItem.TYPE_SEARCHFOLDER);
                    } else {
                        // force move items arriving after exporting begins
                        List<Folder> folders = srcFolder.getSubfolderHierarchy();
                        List<Integer> folderIds = new ArrayList<Integer>();
                        for (Folder folder : folders) {
                            folderIds.add(folder.getId());
                        }
                        ItemId targetFolder = new ItemId(target, zsc);
                        QueryParams dbParams = new QueryParams();
                        dbParams.setFolderIds(folderIds);
                        dbParams.setIncludedTypes(MailItem.TYPE_MESSAGE, MailItem.TYPE_CONVERSATION);
                        dbParams.setChangeDateAfter(exportStart / 1000);
                        try {
                            synchronized (mbox) {
                                Set<Integer> newlyChangedItemIds = DbMailItem.getIds(mbox, DbPool.getConnection(),
                                        dbParams, false);
                                // need to use message type even for conversation
                                ItemActionHelper.MOVE(octxt, mbox, zsc.getResponseProtocol(), new ArrayList<Integer>(
                                        newlyChangedItemIds), MailItem.TYPE_MESSAGE, null, targetFolder);
                                // now, can delete source folder
                                mbox.delete(octxt, sourceFolderId, MailItem.TYPE_FOLDER);
                            }
                        } catch (Exception e) {
                            OfflineLog.offline.debug("[Folder Move] force move delta items failed", e);
                            destMbox.delete(octxt, targetFolder.getId(), MailItem.TYPE_FOLDER);
                            throw e;
                        }
                    }

                    OfflineSyncManager.getInstance().registerDialog(
                            sourceAccount,
                            new Pair<String, String>(OfflineDialogAction.DIALOG_TYPE_FOLDER_MOVE_COMPLETE,
                                    OfflineDialogAction.DIALOG_TYPE_FOLDER_MOVE_COMPLETE_MSG));
                    OfflineLog.offline
                            .debug("folder move completed, from folder %d of account %s, to folder %d under Local folder. Spent %d ms",
                                    sourceFolderId, sourceAccount, targetParentFolderId, System.currentTimeMillis()
                                            - exportStart);
                } catch (Exception e) {
                    OfflineSyncManager.getInstance().registerDialog(
                            sourceAccount,
                            new Pair<String, String>(OfflineDialogAction.DIALOG_TYPE_FOLDER_MOVE_FAIL,
                                    OfflineDialogAction.DIALOG_TYPE_FOLDER_MOVE_FAIL_MSG));
                    OfflineLog.offline.warn("[Folder Move] failed", e);
                }
            }
        }.start();
    }
}