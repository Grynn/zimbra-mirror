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
package com.zimbra.cs.mailbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.util.JMSession;

public class SyncExceptionHandler {

    private static final String MESSAGE_SYNC_FAILED = "message sync failed";
    private static final String CALENDAR_SYNC_FAILED = "calendar sync failed";
    private static final String CONTACT_SYNC_FAILED = "calendar sync failed";
    private static final String DELETE_ITEM_FAILED = "delete item failed";
    private static final String PUSH_ITEM_FAILED = "push item failed";
    private static final String SEND_MAIL_FAILED = "send mail failed";
    private static final String DOCUMENT_SYNC_FAILED = "document sync failed";


    public static void checkRecoverableException(String message, Exception exception) throws ServiceException {
        if (!OfflineSyncManager.getInstance().isServiceActive() ||
            OfflineSyncManager.isIOException(exception) || OfflineSyncManager.isConnectionDown(exception) ||
            OfflineSyncManager.isAuthError(exception) || OfflineSyncManager.isReceiversFault(exception) ||
            OfflineSyncManager.isMailboxInMaintenance(exception))
            throw ServiceException.FAILURE(message, exception); // let it bubble in case it's server issue so we interrupt sync to retry later
    }

    static void syncMessageFailed(ZcsMailbox ombx, int itemId, Exception exception) throws ServiceException {
        saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, exception);
    }

    private static final int MESSAGE_DATA_LIMIT = 4* 1024 * 1024;
    public static void syncMessageFailed(ZcsMailbox ombx, int itemId, ParsedMessage pm, Exception exception) throws ServiceException {
        if (pm != null) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            InputStream msgStream = null; 
            try {
                msgStream = pm.getRawInputStream();
                ByteUtil.copy(msgStream, true, bao, true, MESSAGE_DATA_LIMIT);
                saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, bao.toString(), pm.getRawSize(), exception);
            } catch (IOException x) {
                saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, exception);
            } finally {
                ByteUtil.closeStream(msgStream);
            }
        } else {
            saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, exception);
        }
    }

    static void syncCalendarFailed(ZcsMailbox ombx, int itemId, Exception exception) throws ServiceException {
        saveFailureReport(ombx, itemId, CALENDAR_SYNC_FAILED, exception);
    }

    static void syncCalendarFailed(ZcsMailbox ombx, int itemId, String xml, Exception exception) throws ServiceException {
        saveFailureReport(ombx, itemId, CALENDAR_SYNC_FAILED, xml, xml.length(), exception);
    }

    public static void syncContactFailed(DesktopMailbox dmbx, int itemId, Exception exception) throws ServiceException {
        saveFailureReport(dmbx, itemId, CONTACT_SYNC_FAILED, exception);
    }

    public static void syncContactFailed(DesktopMailbox dmbx, int itemId, String xml, Exception exception) throws ServiceException {
        saveFailureReport(dmbx, itemId, CONTACT_SYNC_FAILED, xml, xml.length(), exception);
    }

    static void localDeleteFailed(ZcsMailbox ombx, int itemId, Exception exception) throws ServiceException {
        saveFailureReport(ombx, itemId, DELETE_ITEM_FAILED, exception);
    }

    static void pushItemFailed(ZcsMailbox ombx, int itemId, Exception exception) throws ServiceException {
        saveFailureReport(ombx, itemId, PUSH_ITEM_FAILED, exception);
    }

    static String sendMailFailed(ZcsMailbox ombx, int itemId, Exception exception) throws ServiceException {
        return saveFailureReport(ombx, itemId, SEND_MAIL_FAILED, exception);
    }

    static void syncDocumentFailed(ZcsMailbox ombx, int itemId, Exception exception) throws ServiceException {
        saveFailureReport(ombx, itemId, DOCUMENT_SYNC_FAILED, exception);
    }

    public static void importFailed(DesktopMailbox dmbx, int id, String error, Exception exception) throws ServiceException {
        saveFailureReport(dmbx, id, error, exception);
    }

    public static String saveFailureReport(DesktopMailbox dmbx, int id, String error, Exception exception) throws ServiceException {
        return saveFailureReport(dmbx, id, error, null, 0, exception);
    }

    public static String saveFailureReport(DesktopMailbox dmbx, int id, String error, String data, int totalSize, Exception exception) throws ServiceException {
        if (exception != null && OfflineSyncManager.isMailboxInMaintenance(exception))
            throw (ServiceException)exception;

        if (exception != null && OfflineSyncManager.isDbShutdown(exception))
            throw ServiceException.FAILURE("DbPool permanently shutdown", exception);

        Date now = new Date();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        OfflineLog.offline.warn("sync failure for id=" + id + "; generating failure report", exception);

        String code = "no exception";
        if (exception != null)
            code = exception instanceof ServiceException ? ((ServiceException)exception).getCode() : exception.getMessage();

            //TODO: need to i18n the entire block here
            StringBuilder sb = new StringBuilder();
            sb.append("Product name:    Zimbra Desktop\n");
            sb.append("Product version: ").append(OfflineLC.zdesktop_version.value()).append("\n");
            sb.append("Build ID:        ").append(OfflineLC.zdesktop_buildid.value()).append("\n");
            sb.append("Release type:    ").append(OfflineLC.zdesktop_relabel.value()).append("\n");
            sb.append("OS Platform:     ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.arch")).append(" ").append(System.getProperty("os.version")).append("\n");
            sb.append("Time of event:   ").append(timestamp).append("\n");
            sb.append("Error type:      ").append(code).append("\n");
            sb.append("Item ID:         ").append(id).append("\n");
            sb.append("Error summary:   ").append(error).append("\n\n");

            if (data != null) {
                sb.append("----------------------------------------------------------------------------\n");
                sb.append("Affected data - PLEASE REMOVE ANY SENSITIVE INFORMATION");
                if (totalSize > data.length())
                    sb.append(" (truncated, original size of ").append(totalSize).append(")");
                else
                    sb.append(" (size=").append(data.length()).append(")");
                sb.append(":\n");
                sb.append("----------------------------------------------------------------------------\n\n");
                sb.append(data);
                sb.append("\n\n----------------------------------------------------------------------------\n");
            }

            if (exception != null) {
                ByteArrayOutputStream bao = new ByteArrayOutputStream() {
                    private static final int STACK_TRACE_LIMIT = 1024 * 1024;

                    @Override
                    public synchronized void write(byte[] b, int off, int len) {
                        len = len > STACK_TRACE_LIMIT - count ? STACK_TRACE_LIMIT - count : len;
                        if (len > 0)
                            super.write(b, off, len);
                        //otherwise discard
                    }

                    @Override
                    public synchronized void write(int b) {
                        if (count < STACK_TRACE_LIMIT)
                            super.write(b);
                    }
                };
                PrintStream ps = new PrintStream(bao);
                exception.printStackTrace(ps);
                ps.flush();

                sb.append("Failure details - PLEASE REMOVE ANY SENSITIVE INFORMATION\n");
                sb.append("----------------------------------------------------------------------------\n\n");
                if (exception instanceof SoapFaultException) {
                    SoapFaultException sfe = (SoapFaultException)exception;
                    if (sfe.getFaultRequest() != null)
                        sb.append(sfe.getFaultRequest()).append("\n\n");
                    if (sfe.getFaultResponse() != null)
                        sb.append(sfe.getFaultResponse()).append("\n\n");
                    else if (sfe.getFault() != null)
                        sb.append(sfe.getFault().prettyPrint()).append("\n\n");
                }
                sb.append(bao.toString());
                sb.append("\n----------------------------------------------------------------------------\n");
            }

            logSyncErrorMessage(dmbx, id, "zdesktop error report (" + timestamp + "): " + code, sb.toString());
            return sb.toString();
    }

    public static class Revision {
        int version;
        long modifiedDate;
        String editor;
    }

    static void logDocumentEditConflict(SyncMailbox dmbx, MailItem item, ArrayList<Revision> revisions) {
        Date now = new Date();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
        String subject = "Edit conflict on "+item.getName()+" ("+timestamp+")";
        StringBuilder buf = new StringBuilder();
        buf.append("During the sync the following revisions for '");
        buf.append(item.getName());
        buf.append("' were overwritten on the server:\n\n");
        for (Revision rev : revisions) {
            buf.append("revision ").append(rev.version);
            if (rev.modifiedDate > 0) {
                buf.append(" edited by ").append(rev.editor);
                buf.append(" on ").append(new Date(rev.modifiedDate));
            }
            buf.append("\n");
        }
        try {
            MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSession());
            mm.setSentDate(now);
            mm.setFrom(new InternetAddress(dmbx.getAccount().getName()));
            mm.setRecipient(RecipientType.TO, new InternetAddress(dmbx.getAccountName()));
            mm.setSubject(subject);
            mm.setText(buf.toString());
            mm.saveChanges(); //must call this to update the headers

            //save failure alert to "Sync Failures" folder
            ParsedMessage pm = new ParsedMessage(mm, true);
            dmbx.addMessage(new ChangeTrackingMailbox.TracelessContext(), pm, DesktopMailbox.ID_FOLDER_INBOX, true, Flag.BITMASK_UNREAD, null);
        } catch (Exception e) {
            OfflineLog.offline.warn("can't save failure report", e);
        }
    }

    private static void logSyncErrorMessage(DesktopMailbox dmbx, int id, String subject, String message) {
        OfflineLog.offline.warn(message);
        try {
            Date now = new Date();
            MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSession());
            mm.setSentDate(now);
            mm.setFrom(new InternetAddress(dmbx.getAccount().getName()));
            mm.setRecipient(RecipientType.TO, new InternetAddress(OfflineLC.zdesktop_support_email.value()));
            mm.setSubject(subject);
            mm.setText(message);
            mm.saveChanges(); //must call this to update the headers

            //save failure alert to "Sync Failures" folder
            ParsedMessage pm = new ParsedMessage(mm, true);
            dmbx.addMessage(new ChangeTrackingMailbox.TracelessContext(), pm, DesktopMailbox.ID_FOLDER_FAILURE, true, Flag.BITMASK_UNREAD, null);
        } catch (Exception e) {
            OfflineLog.offline.warn("can't save failure report for id=" + id, e);
        }
    }
}
