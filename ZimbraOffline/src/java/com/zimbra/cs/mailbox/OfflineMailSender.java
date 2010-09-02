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
package com.zimbra.cs.mailbox;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mime.ParsedAddress;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.util.AccountUtil;

public class OfflineMailSender extends MailSender {

    public OfflineMailSender() {
        setTrackBadHosts(false);
    }
    
    @Override
    public ItemId sendMimeMessage(OperationContext octxt, Mailbox mbox,
            MimeMessage mm) throws ServiceException {
        try {
            // for messages that aren't actually *sent*, just go down the standard save-to-sent path
            if (mm.getAllRecipients() == null)
                return super.sendMimeMessage(octxt, mbox, mm);
        } catch (MessagingException me) {
            throw ServiceException.FAILURE("could not determine message recipients; aborting mail send", me);
        }

        Account acct = mbox.getAccount();
        Account authuser = octxt == null ? null : octxt.getAuthenticatedUser();        
        if (authuser == null)
            authuser = acct;
        // bug 49820: Hide the "local@host.local" fake account address from From/Sender header checks. 
        if (AccountUtil.isZDesktopLocalAccount(authuser.getId()))
            authuser = acct;

        try {
            // set the From, Sender, Date, Reply-To, etc. headers
            updateHeaders(mm, acct, authuser, octxt, null /* don't set originating IP in offline client */, isReplyToSender(), false);

            // save as a draft to be sent during sync interval
            ParsedMessage pm = new ParsedMessage(mm, mm.getSentDate().getTime(),
                mbox.attachmentsIndexingEnabled());
            if (getIdentity() == null)
                setIdentity(Provisioning.getInstance().getDefaultIdentity(authuser));
            String identityId = getIdentity() == null ? null :
                getIdentity().getAttr(Provisioning.A_zimbraPrefIdentityId);
            int draftId = mbox.saveDraft(octxt, pm, Mailbox.ID_AUTO_INCREMENT,
                (getOriginalMessageId() != null ? getOriginalMessageId().toString(acct) : null), getReplyType(),
                identityId, acct.getId(), 0).getId();
            mbox.move(octxt, draftId, MailItem.TYPE_MESSAGE, DesktopMailbox.ID_FOLDER_OUTBOX);

            // we can now purge the uploaded attachments
            if (getUploads() != null)
                FileUploadServlet.deleteUploads(getUploads());

            // add any new contacts to the personal address book
            if (getSaveContacts() != null) {
                Mailbox contactMbox = mbox;
                
                if (!acct.isFeatureContactsEnabled()) {
                    Account localAcct = OfflineProvisioning.getOfflineInstance().getLocalAccount();
                    
                    contactMbox = MailboxManager.getInstance().getMailboxByAccount(localAcct);
                }
                for (InternetAddress iaddr : getSaveContacts()) {
                    ParsedAddress addr = new ParsedAddress(iaddr);
                    try {
                        ParsedContact pc = new ParsedContact(addr.getAttributes());
                        contactMbox.createContact(octxt, pc, Mailbox.ID_FOLDER_AUTO_CONTACTS, null);
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
