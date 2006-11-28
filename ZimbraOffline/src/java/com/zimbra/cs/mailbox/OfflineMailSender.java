/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.mime.ParsedAddress;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.FileUploadServlet.Upload;

public class OfflineMailSender extends MailSender {

    @Override
    public int sendMimeMessage(OperationContext octxt, Mailbox mbox, int saveToFolder, MimeMessage mm, List<InternetAddress> newContacts,
                               List<Upload> uploads, int origMsgId, String replyType, boolean ignoreFailedAddresses, boolean replyToSender)
    throws ServiceException {
        try {
            // for messages that aren't actually *sent*, just go down the standard save-to-sent path
            if (mm.getAllRecipients() == null)
                return super.sendMimeMessage(octxt, mbox, saveToFolder, mm, newContacts, uploads, origMsgId, replyType, ignoreFailedAddresses, replyToSender);
        } catch (MessagingException me) {
            throw ServiceException.FAILURE("could not determine message recipients; aborting mail send", me);
        }

        Account acct = mbox.getAccount();
        Account authuser = octxt == null ? null : octxt.getAuthenticatedUser();
        if (authuser == null)
            authuser = acct;

        try {
            // set the From, Sender, Date, Reply-To, etc. headers
            updateHeaders(mm, acct, authuser, replyToSender);

            // save as a draft for now...
            ParsedMessage pm = new ParsedMessage(mm, mm.getSentDate().getTime(), mbox.attachmentsIndexingEnabled());
            int draftId = mbox.saveDraft(octxt, pm, Mailbox.ID_AUTO_INCREMENT, origMsgId, replyType).getId();
            mbox.move(octxt, draftId, MailItem.TYPE_MESSAGE, OfflineMailbox.ID_FOLDER_OUTBOX);

            // we can now purge the uploaded attachments
            if (uploads != null)
                FileUploadServlet.deleteUploads(uploads);

            // add any new contacts to the personal address book
            if (newContacts != null) {
                for (InternetAddress iaddr : newContacts) {
                    ParsedAddress addr = new ParsedAddress(iaddr);
                    try {
                        mbox.createContact(octxt, addr.getAttributes(), Mailbox.ID_FOLDER_AUTO_CONTACTS, null);
                    } catch (ServiceException e) {
                        sLog.warn("ignoring error while auto-adding contact", e);
                    }
                }
            }

            return draftId;
        } catch (MessagingException me) {
            sLog.warn("exception occurred during SendMsg", me);
            throw ServiceException.FAILURE("MessagingException", me);
        } catch (IOException ioe) {
            sLog.warn("exception occured during send msg", ioe);
            throw ServiceException.FAILURE("IOException", ioe);
        }
    }
}
