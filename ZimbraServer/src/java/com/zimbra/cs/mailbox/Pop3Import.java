/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Message;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.service.ServiceException;


public class Pop3Import
implements MailItemImport {

    private static Session sSession;
    private static Log sLog = LogFactory.getLog(Pop3Import.class);
    private static final byte[] CRLF = { '\r', '\n' };
    
    static {
        Properties props = new Properties();
        sSession = Session.getDefaultInstance(props);
    }

    public String test(MailItemDataSource ds) {
        String error = null;
        
        try {
            Store store = sSession.getStore("pop3");
            store.connect(ds.getHost(), ds.getPort(), ds.getUsername(), ds.getPassword());
            store.close();
        } catch (MessagingException e) {
            sLog.debug("Testing " + ds, e);
            error = e.getMessage();
        }
        return error;
    }
    
    public void importData(MailItemDataSource dataSource)
    throws ServiceException {
        try {
            fetchMessages(dataSource);
        } catch (MessagingException e) {
            throw ServiceException.FAILURE("Importing data from " + dataSource, e);
        } catch (IOException e) {
            throw ServiceException.FAILURE("Importing data from " + dataSource, e);
        }
    }
    
    private void fetchMessages(MailItemDataSource ds)
    throws MessagingException, IOException, ServiceException {
        ZimbraLog.mailbox.info("Importing POP3 messages from " + ds);
        
        // Connect (USER, PASS, STAT)
        Store store = sSession.getStore("pop3");
        store.connect(ds.getHost(), ds.getPort(), ds.getUsername(), ds.getPassword());
        POP3Folder folder = (POP3Folder) store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        Message msgs[] = folder.getMessages();
        
        sLog.debug("Retrieving " + msgs.length + " messages");

        if (msgs.length > 0) {
            /*
            if (ds.leaveMailOnServer()) {
                // Fetch message UID's for reconciliation (UIDL)
                FetchProfile fp = new FetchProfile();
                fp.add(UIDFolder.FetchProfileItem.UID);
                folder.fetch(folder.getMessages(), fp);
            }
            */
            
            // Fetch message bodies (RETR)
            Mailbox mbox = MailboxManager.getInstance().getMailboxById(ds.getMailboxId());
            
            for (int i = 0; i < msgs.length; i++) {
                POP3Message msg = (POP3Message) msgs[i];
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                
                // Headers
                Enumeration e = msg.getAllHeaderLines();
                while (e.hasMoreElements()) {
                    String line = (String) e.nextElement();
                    os.write(line.getBytes());
                    os.write(CRLF);
                }
                
                // Line break between headers and content
                os.write(CRLF);
                
                // Content
                InputStream is = msg.getRawInputStream();
                ByteUtil.copy(is, true, os, true);
                ParsedMessage pm = new ParsedMessage(os.toByteArray(), mbox.attachmentsIndexingEnabled());
                mbox.addMessage(null, pm, ds.getFolderId(), false, Flag.BITMASK_UNREAD, null);
            }

            // Mark all messages for deletion (DELE)
            for (Message msg : msgs) {
                msg.setFlag(Flags.Flag.DELETED, true);
            }
        }
        
        // Expunge if necessary and disconnect (QUIT)
        // folder.close(!ds.leaveMailOnServer());
        folder.close(true);
        store.close();
    }
}
