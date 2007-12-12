/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.mime.ParsedAddress;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.FileUploadServlet.Upload;
import com.zimbra.cs.service.util.ItemId;

public class OfflineMailSender extends MailSender {

    @Override
    public ItemId sendMimeMessage(OperationContext octxt, Mailbox mbox, boolean saveToSent, MimeMessage mm,
                                  List<InternetAddress> newContacts, List<Upload> uploads,
                                  ItemId origMsgId, String replyType, Identity identity,
                                  boolean ignoreFailedAddresses, boolean replyToSender)
    throws ServiceException {
        try {
            // for messages that aren't actually *sent*, just go down the standard save-to-sent path
            if (mm.getAllRecipients() == null)
                return super.sendMimeMessage(octxt, mbox, saveToSent, mm, newContacts, uploads, origMsgId, replyType, identity, ignoreFailedAddresses, replyToSender);
        } catch (MessagingException me) {
            throw ServiceException.FAILURE("could not determine message recipients; aborting mail send", me);
        }

        Account acct = mbox.getAccount();
        Account authuser = octxt == null ? null : octxt.getAuthenticatedUser();
        if (authuser == null)
            authuser = acct;

        try {
            // set the From, Sender, Date, Reply-To, etc. headers
            updateHeaders(mm, acct, authuser, octxt, null /* don't set originating IP in offline client */, replyToSender);

            // save as a draft for now...
            ParsedMessage pm = new ParsedMessage(mm, mm.getSentDate().getTime(), mbox.attachmentsIndexingEnabled());
            String identityId = identity.getAttr(Provisioning.A_zimbraPrefIdentityId);
            int draftId = mbox.saveDraft(octxt, pm, Mailbox.ID_AUTO_INCREMENT, (origMsgId != null ? origMsgId.toString(acct) : null), replyType, identityId).getId();
            mbox.move(octxt, draftId, MailItem.TYPE_MESSAGE, OfflineMailbox.ID_FOLDER_OUTBOX);

            // we can now purge the uploaded attachments
            if (uploads != null)
                FileUploadServlet.deleteUploads(uploads);

            // add any new contacts to the personal address book
            if (newContacts != null) {
                for (InternetAddress iaddr : newContacts) {
                    ParsedAddress addr = new ParsedAddress(iaddr);
                    try {
                        ParsedContact pc = new ParsedContact(addr.getAttributes());
                        mbox.createContact(octxt, pc, Mailbox.ID_FOLDER_AUTO_CONTACTS, null);
                    } catch (ServiceException e) {
                        OfflineLog.offline.warn("ignoring error while auto-adding contact", e);
                    }
                }
            }

            return new ItemId(mbox, draftId);
        } catch (MessagingException me) {
            OfflineLog.offline.warn("exception occurred during SendMsg", me);
            throw ServiceException.FAILURE("MessagingException", me);
        } catch (IOException ioe) {
            OfflineLog.offline.warn("exception occured during send msg", ioe);
            throw ServiceException.FAILURE("IOException", ioe);
        }
    }
}
