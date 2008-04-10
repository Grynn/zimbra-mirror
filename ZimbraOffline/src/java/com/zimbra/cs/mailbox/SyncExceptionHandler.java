package com.zimbra.cs.mailbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.util.JMSession;

class SyncExceptionHandler {
	
	private static final String MESSAGE_SYNC_FAILED = "message sync failed";
	private static final String CALENDAR_SYNC_FAILED = "calendar sync failed";
	private static final String DELETE_ITEM_FAILED = "delete item failed";
	private static final String PUSH_ITEM_FAILED = "push item failed";
	
	
	static void checkRecoverableException(ServiceException exception) throws ServiceException {
		if (OfflineSyncManager.isConnectionDown(exception) || OfflineSyncManager.isAuthEerror(exception) || OfflineSyncManager.isReceiverFault(exception))
			throw exception; // let it bubble in case it's server issue so we interrupt sync to retry later
	}
	
	static void syncMessageFailed(OfflineMailbox ombx, int itemId, ServiceException exception) throws ServiceException {
		saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, null, 0, exception);
	}
	
	private static final int MESSAGE_DATA_LIMIT = 4* 1024 * 1024;
	public static void syncMessageFailed(OfflineMailbox ombx, int itemId, ParsedMessage pm, ServiceException exception) throws ServiceException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			ByteUtil.copy(pm.getRawInputStream(), true, bao, true, MESSAGE_DATA_LIMIT);
			saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, bao.toString(), pm.getRawSize(), exception);
		} catch (IOException x) {
			saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, null, 0, exception);
		} catch (MessagingException x) {
			saveFailureReport(ombx, itemId, MESSAGE_SYNC_FAILED, null, 0, exception);
		}
	}
	
	static void syncCalendarFailed(OfflineMailbox ombx, int itemId, ServiceException exception) throws ServiceException {
		saveFailureReport(ombx, itemId, CALENDAR_SYNC_FAILED, null, 0, exception);
		
	}
	
	static void syncCalendarFailed(OfflineMailbox ombx, int itemId, String xml, ServiceException exception) throws ServiceException {
		saveFailureReport(ombx, itemId, CALENDAR_SYNC_FAILED, xml, xml.length(), exception);
	}
	
	static void localDeleteFailed(OfflineMailbox ombx, int itemId, ServiceException exception) throws ServiceException {
		saveFailureReport(ombx, itemId, DELETE_ITEM_FAILED, null, 0, exception);
	}
	
	static void pushItemFailed(OfflineMailbox ombx, int itemId, ServiceException exception) throws ServiceException {
		saveFailureReport(ombx, itemId, PUSH_ITEM_FAILED, null, 0, exception);
	}
	
    private static void saveFailureReport(DesktopMailbox dmbx, int id, String error, String data, int totalSize, ServiceException exception) {
		try {
			Date now = new Date();
			
			OfflineLog.offline.warn("sync failure for id=" + id + "; generating failure report", exception);
			
			dmbx.ensureFailureFolderExists();

			//TODO: need to i18n the entire block here
			StringBuilder sb = new StringBuilder();
			sb.append("Please post this error report to Zimbra Desktop Support Forums at http://www.zimbra.com/forums/zimbra-desktop/.\n\n");
			sb.append("Product name:    Zimbra Desktop\n");
			sb.append("Product version: ").append(OfflineLC.zdesktop_version.value()).append("\n");
			sb.append("Build ID:        ").append(OfflineLC.zdesktop_buildid.value()).append("\n");
			sb.append("Release type:    ").append(OfflineLC.zdesktop_relabel.value()).append("\n");
			sb.append("OS Platform:     ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.arch")).append(" ").append(System.getProperty("os.version")).append("\n");
			sb.append("Time of failure: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now)).append("\n");
			sb.append("Failure summary: ").append(error).append("\n\n");

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

			ByteArrayOutputStream bao = new ByteArrayOutputStream() {
				private static final int STATCK_TRACE_LIMIT = 1024 * 1024;
				
				@Override
				public synchronized void write(byte[] b, int off, int len) {
					len = len > STATCK_TRACE_LIMIT - count ? STATCK_TRACE_LIMIT - count : len;
					if (len > 0)
						super.write(b, off, len);
					//otherwise discard
				}

				@Override
				public synchronized void write(int b) {
					if (count < STATCK_TRACE_LIMIT)
						super.write(b);
				}
			};
			PrintStream ps = new PrintStream(bao);
			exception.printStackTrace(ps);
			ps.flush();
			
			sb.append("Failure details: \n");
			sb.append("----------------------------------------------------------------------------\n\n");
			sb.append(bao.toString());
			sb.append("\n----------------------------------------------------------------------------\n");
			
			MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSession());
			mm.setSentDate(now);
			mm.setFrom(new InternetAddress(dmbx.getAccount().getName()));
    		mm.setRecipient(RecipientType.TO, new InternetAddress(dmbx.getAccount().getName()));
    		mm.setSubject("zdesktop error report: " + exception.getCode());
    		mm.setText(sb.toString());
    		mm.saveChanges(); //must call this to update the headers
		
    		//save failure alert to "Sync Failures" folder
    		ParsedMessage pm = new ParsedMessage(mm, true);
    		dmbx.addMessage(new OfflineMailbox.OfflineContext(), pm, DesktopMailbox.ID_FOLDER_FAILURE, true, Flag.BITMASK_UNREAD, null);
		} catch (Exception e) {
			OfflineLog.offline.warn("can't save failure report for id=" + id, e);
		}
    }
}
