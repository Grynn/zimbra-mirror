/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.qa.unittest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.jsp.ConfigServlet;
import com.zimbra.cs.offline.jsp.JspProvStub;
import com.zimbra.cs.offline.jsp.XsyncBean;
import com.zimbra.cs.zclient.ZContact;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMessage;
import com.zimbra.cs.zclient.ZSearchParams;
import com.zimbra.cs.zclient.ZTag;
import com.zimbra.cs.zclient.ZMailbox.ZAttachmentInfo;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage.MessagePart;

import junit.framework.TestCase;

public class TestXsync extends TestCase {

    private static final String ACCOUNT = "XSYNC";
    private static final String USERNAME = "xsync";
    private static final String PASSWORD = "test123";
    private static final String EMAIL = "xsync@jjmac.local";
    private static final String HOST = "localhost";
    private static final int PORT = 7070;
    private static final boolean isSSL = false;
    private static final String LOCAL_ADMIN_URL = "http://localhost:7733/service/admin/soap";
    private static final String LOCAL_SOAP_URL = "http://localhost:7733/service/soap";
    //private static final String REMOTE_SOAP_URL = "http://localhost:7070/service/soap";
    
    private ZMailbox localMailbox;
    private ZMailbox remoteMailbox;
    
    public void testXsync() throws Exception {
        try {
            syncEmail();
            syncContacts();
        } catch (Exception x) {
            ZimbraLog.test.warn("test xsync", x);
        }
    }
    
    private void syncEmail() throws Exception {
        //send message to self
        ZimbraLog.test.info("TEST 1");
        ZOutgoingMessage msg = new ZOutgoingMessage();
        List<ZEmailAddress> addresses = new ArrayList<ZEmailAddress>();
        addresses.add(new ZEmailAddress(EMAIL, null, null, ZEmailAddress.EMAIL_TYPE_TO));
        msg.setAddresses(addresses);
        msg.setSubject("SEND");
        msg.setMessagePart(new MessagePart("text/plain", "This is the outer message"));
        localMailbox.sendMessage(msg, null, false);
        sync();
        ZMessage recv = TestUtil.search(remoteMailbox, "in:inbox is:unread").get(0);
        assertNotNull(recv);
        checkMsgCount(localMailbox, "in:inbox is:unread", 1);
        ZMessage sent = TestUtil.search(localMailbox, "in:sent").get(0);
        assertNotNull(sent);
        
        //delete both sent and recv
        ZimbraLog.test.info("TEST 2");
        remoteMailbox.deleteMessage(recv.getId());
        localMailbox.deleteMessage(sent.getId());
        sync();
        checkMsgCount(localMailbox, "in:inbox", 0);
        checkMsgCount(remoteMailbox, "in:sent", 0);
        
        //create remote F1 and add MSG1 to F1
        ZimbraLog.test.info("TEST 3");
        ZFolder sf1 = TestUtil.createFolder(remoteMailbox, "" + Mailbox.ID_FOLDER_USER_ROOT,  "F1");
        String sm1Id = TestUtil.addMessage(remoteMailbox, "MSG1", sf1.getId(), "u");
        sync();
        ZFolder cf1 = localMailbox.getFolderByPath("/F1");
        assertNotNull("local /F1", cf1);
        checkMsgCount(localMailbox, "in:F1 is:unread", 1);
        
        //create remote F2, move MSG1 to F2, and mark MSG1 read
        ZimbraLog.test.info("TEST 4");
        ZFolder sf2 = TestUtil.createFolder(remoteMailbox, sf1.getId(),  "F2");
        remoteMailbox.moveMessage(sm1Id, sf2.getId());
        remoteMailbox.markMessageRead(sm1Id, true);
        sync();
        ZFolder cf2 = localMailbox.getFolderByPath("/F1/F2");
        assertNotNull("local /F1/F2", cf2);
        checkMsgCount(localMailbox, "in:F1", 0);
        checkMsgCount(localMailbox, "in:F1/F2", 1);
        ZMessage cm1 = TestUtil.search(localMailbox, "in:F1/F2").get(0);
        assertFalse("MSG1 unread", cm1.isUnread());

        //create local F3 and move MSG1 into it, and mark MSG1 unread
        ZimbraLog.test.info("TEST 5");
        ZFolder cf3 = TestUtil.createFolder(localMailbox, cf2.getId(), "F3");
        localMailbox.moveMessage(cm1.getId(), cf3.getId());
        localMailbox.markMessageRead(cm1.getId(), false);
        sync();
        ZFolder sf3 = remoteMailbox.getFolderByPath("/F1/F2/F3");
        assertNotNull("remote /F1/F2/F3", sf3);
        checkMsgCount(remoteMailbox, "in:F1/F2", 0);
        checkMsgCount(remoteMailbox, "in:F1/F2/F3", 1);
        ZMessage sm1 = TestUtil.search(remoteMailbox, "in:F1/F2/F3").get(0);
        assertTrue("MSG1 read", sm1.isUnread()); //need to preserv local changes when moving a message
        
        //create local F4 and move F3 into it
        ZimbraLog.test.info("TEST 6");
        ZFolder cf4 = TestUtil.createFolder(localMailbox, "" + Mailbox.ID_FOLDER_USER_ROOT, "F4");
        localMailbox.moveFolder(cf3.getId(), cf4.getId());
        sync();
        checkMsgCount(remoteMailbox, "in:F4/F3", 1);
        ZFolder sf4 = remoteMailbox.getFolderByPath("/F4");
        assertNotNull("remote /F4", sf4);
        assertNull("remote /F1/F2/F3", remoteMailbox.getFolderByPath("/F1/F2/F3"));
        sf3 = remoteMailbox.getFolderByPath("/F4/F3");
        assertNotNull("remote /F4/F3", sf3);
        
        //add remote F5 into F2, but delete local F2. F5 should be /F5 on both local and remote
        ZimbraLog.test.info("TEST 7");
        ZFolder sf5 = TestUtil.createFolder(remoteMailbox, sf2.getId(),  "F5");
        localMailbox.deleteFolder(cf2.getId());
        sync();
        ZFolder cf5 = localMailbox.getFolderByPath("/F5");
        assertNotNull("local /F5", cf5);
        sf5 = remoteMailbox.getFolderByPath("/F5");
        assertNotNull("remote /F5", sf5);
        
        //add /F5/F6 on both local and remote, remote F6 should be merged with local F6.
        ZimbraLog.test.info("TEST 8");
        ZFolder cf6 = TestUtil.createFolder(localMailbox, cf5.getId(), "F6");
        ZFolder sf6 = TestUtil.createFolder(remoteMailbox, sf5.getId(), "F6");
        sync();
        cf6 = localMailbox.getFolderByPath("/F5/F6");
        assertNotNull("local /F5/F6", cf6);
        sf6 = remoteMailbox.getFolderByPath("/F5/F6");
        assertNotNull("remote /F5/F6", sf6);
        
        //add /F5/F6/F5 on remote, but delete /F5/F6 local, new F5 should be relocated to /F5 and renamed
        ZimbraLog.test.info("TEST 9");
        ZFolder sf5_ = TestUtil.createFolder(remoteMailbox, sf6.getId(), "F5");
        localMailbox.deleteFolder(cf6.getId());
        sync();
        sf5_ = remoteMailbox.getFolderById(sf5_.getId());
        assertNotNull("remote /F5...", sf5_);
        ZFolder cf5_ = localMailbox.getFolderByPath("/" + sf5_.getName());
        assertNotNull("local /F5...", cf5_);
        
        ZimbraLog.test.info("TEST 10");
        //move remote /F5 to /F4/F3/F5 and rename that to /F4/F3/F1, but delete /F4 locally, expect renamed F1 get moved to /F1 and renamed again
        remoteMailbox.renameFolder(sf5.getId(), "F1", sf3.getId());
        localMailbox.deleteFolder(cf4.getId());
        sync();
        assertTrue("MSG1 deleted", TestUtil.search(remoteMailbox, "subject:MSG1").isEmpty());
        ZFolder sf1_ = remoteMailbox.getFolderById(sf5.getId());
        assertNotNull("remote /F1...", sf1_);
        ZFolder cf1_ = localMailbox.getFolderByPath("/" + sf1_.getName());
        assertNotNull("local /F1...", cf1_);
        
        //delete remote F1, but move local F1... into F1. remote F1 should get recreated. 
        ZimbraLog.test.info("TEST 11");
        remoteMailbox.deleteFolder(sf1.getId());
        localMailbox.moveFolder(cf1_.getId(), cf1.getId());
        sync();
        cf1_ = localMailbox.getFolderByPath("/F1/" + cf1_.getName());
        assertNotNull("local /F1/F1...", cf1_);
        sf1 = remoteMailbox.getFolderByPath("/F1");
        assertNotNull("remote /F1", sf1);
        sf1_ = remoteMailbox.getFolderByPath("/F1/" + sf1_.getName());
        assertNotNull("remote /F1/F1...", sf1_);
        
        //move /F5_ under /F1 and create /F1/F1_/F7. now we have /F1/F1_/F7 and /F1/F5_
        ZimbraLog.test.info("TEST 12");
        localMailbox.moveFolder(cf5_.getId(), cf1.getId());
        ZFolder cf7 = TestUtil.createFolder(localMailbox, cf1_.getId(), "F7");
        sync();
        cf5_ = localMailbox.getFolderByPath("/F1/" + cf5_.getName());
        assertNotNull("local /F1/F5...", cf5_);
        ZFolder sf7 = remoteMailbox.getFolderByPath("/F1/" + cf1_.getName() + "/F7");
        assertNotNull("remote /F1/F1.../F7", sf7);
        
        //delete /F1 on remote, but locally move /F1/F5_ to /F5_, /F1/F1.../F7 to /F1/F7, and create /F1/F1.../F8
        //the expected result is: 1) /F1 and /F1/F1... gets recreated on remote
        //                        2) /F1/F1.../F8 gets created on remote
        //                        3) local /F1/F7 is deleted, and local /F5_ is deleted
        ZimbraLog.test.info("TEST 13");
        remoteMailbox.deleteFolder(sf1.getId());
        localMailbox.moveFolder(cf5_.getId(), "" + Mailbox.ID_FOLDER_USER_ROOT);
        localMailbox.moveFolder(cf7.getId(), cf1.getId());
        ZFolder cf8 = TestUtil.createFolder(localMailbox, cf1_.getId(), "F8");
        sync();
        sf1 = remoteMailbox.getFolderByPath("/F1");
        assertNotNull("remote /F1", sf1);
        ZFolder sf8 = remoteMailbox.getFolderByPath(cf8.getPath());
        assertNotNull("remote /F1/F1.../F8", sf8);
        cf5_ = localMailbox.getFolderById(cf5_.getId());
        assertNull("local /F5_", cf5_);
        cf7 = localMailbox.getFolderById(cf7.getId());
        assertNull("local /F1/F7", cf7);
        
        ZimbraLog.test.info("TEST 14");
        TestUtil.addMessage(remoteMailbox, "MSG2", sf1.getId(), "u");
        TestUtil.addMessage(remoteMailbox, "MSG3", sf8.getId(), "u");
        String sm4Id = TestUtil.addMessage(remoteMailbox, "MSG4", "" + Mailbox.ID_FOLDER_INBOX, "u");
        sync();
        ZMessage cm2 = TestUtil.search(localMailbox, "in:F1").get(0);
        ZMessage cm3 = TestUtil.search(localMailbox, "subject:MSG3").get(0);
        ZMessage cm4 = TestUtil.search(localMailbox, "in:inbox").get(0);
        
        ZimbraLog.test.info("TEST 15");
        remoteMailbox.deleteFolder(sf1.getId());
        localMailbox.moveMessage(cm4.getId(), cf1_.getId()); //remote /F1 and /F1/F1... should be recreated
        localMailbox.moveMessage(cm3.getId(), "" + Mailbox.ID_FOLDER_INBOX); //cm3 should be deleted
        localMailbox.moveMessage(cm2.getId(), cf8.getId()); //cm2 should be deleted, along with F8
        sync();
        sf1_ = remoteMailbox.getFolderByPath(sf1_.getPath());
        assertNotNull("remote /F1/F1...", sf1_);
        assertEquals(0, TestUtil.search(localMailbox, "subject:MSG2").size());
        assertEquals(0, TestUtil.search(localMailbox, "subject:MSG3").size());
        ZMessage sm4 = remoteMailbox.getMessageById(sm4Id);
        assertEquals("remote MSG4", sm4.getFolderId(), sf1_.getId());
        
        //move MSG4 to Inbox and delete local /F1/F1...
        ZimbraLog.test.info("TEST 16");
        localMailbox.moveMessage(cm4.getId(), "" + Mailbox.ID_FOLDER_INBOX);
        localMailbox.deleteFolder(cf1_.getId());
        sync();
        assertNull("remote /F1/F1...", remoteMailbox.getFolderByPath(cf1_.getPath()));
        sm4 = remoteMailbox.getMessageById(sm4Id);
        assertEquals("remote MSG4", sm4.getFolderId(), "" + Mailbox.ID_FOLDER_INBOX);
        
        ZimbraLog.test.info("TEST 17");
        localMailbox.moveMessage(cm4.getId(), cf1.getId());
        localMailbox.deleteFolder(cf1.getId());
        sync();
        assertNull("remote /F1", remoteMailbox.getFolderByPath("/F1"));
        assertEquals(0, TestUtil.search(remoteMailbox, "subject:MSG4").size());
    }
    
    public void syncContacts() throws Exception {
        ZTag bizTag = remoteMailbox.createTag("biz", null);
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put(ContactConstants.A_email, "user1@jjmac.local");
        String fakejpeg = "fakejpeg";
        String attachId = remoteMailbox.uploadAttachment("fakejpeg.jpg", fakejpeg.getBytes(), "image/jpeg", (int)Constants.MILLIS_PER_MINUTE);
        Map<String, ZAttachmentInfo> attachments = new HashMap<String, ZAttachmentInfo>();
        ZAttachmentInfo info = new ZAttachmentInfo().setAttachmentId(attachId);
        info.setPartName(null);
        attachments.put(ContactConstants.A_image, info);
        ZContact sc1 = remoteMailbox.createContact("" + Mailbox.ID_FOLDER_CONTACTS, bizTag.getId(), attrs, attachments);
        sync();
        String cId1 = TestUtil.search(localMailbox, "in:contacts", ZSearchParams.TYPE_CONTACT).get(0);
        ZContact cc1 = localMailbox.getContact(cId1);
        assertEquals("cc1 email", "user1@jjmac.local", cc1.getAttrs().get(ContactConstants.A_email));
        assertEquals("cc1 attach", "fakejpeg", new String(ByteUtil.readInput(cc1.getAttachmentData(ContactConstants.A_image), 0, Integer.MAX_VALUE)));
        assertEquals("cc1 tag", "biz", localMailbox.getTags(cc1.getTagIds()).get(0).getName());
        
        attrs.put(ContactConstants.A_firstName, "jj");
        cc1.modify(attrs, false);        
        sync();
        sc1 = remoteMailbox.getContact(sc1.getId());
        assertEquals("sc1 email", "user1@jjmac.local", sc1.getAttrs().get(ContactConstants.A_email));
        assertEquals("sc1 firstname", "jj", sc1.getAttrs().get(ContactConstants.A_firstName));
        assertEquals("sc1 attach", "fakejpeg", new String(ByteUtil.readInput(sc1.getAttachmentData(ContactConstants.A_image), 0, Integer.MAX_VALUE)));
        assertEquals("sc1 tag", "biz", remoteMailbox.getTags(sc1.getTagIds()).get(0).getName());
        
        attrs.put(ContactConstants.A_lastName, "O'Matic");
        sc1.modify(attrs, false);        
        sync();
        cc1 = localMailbox.getContact(cc1.getId());
        assertEquals("cc1 email", "user1@jjmac.local", cc1.getAttrs().get(ContactConstants.A_email));
        assertEquals("cc1 firstname", "jj", cc1.getAttrs().get(ContactConstants.A_firstName));
        assertEquals("cc1 lastname", "O'Matic", cc1.getAttrs().get(ContactConstants.A_lastName));
        assertEquals("cc1 attach", "fakejpeg", new String(ByteUtil.readInput(cc1.getAttachmentData(ContactConstants.A_image), 0, Integer.MAX_VALUE)));
        assertEquals("cc1 tag", "biz", localMailbox.getTags(cc1.getTagIds()).get(0).getName());
        
        ZFolder cfx = localMailbox.createFolder("" + Mailbox.ID_FOLDER_USER_ROOT,  "ConX", ZFolder.View.contact, null, null, null);
        localMailbox.moveContact(cc1.getId(), cfx.getId());
        
        bizTag = localMailbox.getTagByName("biz");
        cc1.tag(bizTag.getId(), false);
        ZTag funTag = localMailbox.createTag("fun", null);
        cc1.tag(funTag.getId(), true);
        sync();
        String sId1 = TestUtil.search(remoteMailbox, "in:ConX", ZSearchParams.TYPE_CONTACT).get(0);
        sc1 = remoteMailbox.getContact(sId1);
        assertEquals("sc1 email", "user1@jjmac.local", sc1.getAttrs().get(ContactConstants.A_email));
        assertEquals("sc1 firstname", "jj", sc1.getAttrs().get(ContactConstants.A_firstName));
        assertEquals("sc1 lastname", "O'Matic", sc1.getAttrs().get(ContactConstants.A_lastName));
        assertEquals("sc1 attach", "fakejpeg", new String(ByteUtil.readInput(sc1.getAttachmentData(ContactConstants.A_image), 0, Integer.MAX_VALUE)));
        assertEquals("sc1 tag", "fun", remoteMailbox.getTags(sc1.getTagIds()).get(0).getName());
        
        ZFolder sfx = remoteMailbox.getFolderByPath("/ConX");
        remoteMailbox.deleteFolder(sfx.getId());
        sync();
        assertEquals("no local contacts", 0, TestUtil.search(localMailbox, "*", ZSearchParams.TYPE_CONTACT).size());
    }
    
    private void sync() throws Exception {
        Element req = remoteMailbox.newRequestElement(OfflineConstants.SYNC_REQUEST);
        localMailbox.invoke(req);
        
        localMailbox.noOp();
        remoteMailbox.noOp();
    }
    
    private void checkMsgCount(ZMailbox mbox, String query, long expectedCount) throws Exception {
        List<ZMessage> msgs = TestUtil.search(mbox, query);
        assertEquals(query + " == " + expectedCount, expectedCount, msgs.size());
    }
    
    private void checkMsgCount(String query) throws Exception {
        assertEquals(query, TestUtil.search(localMailbox, query).size(), TestUtil.search(remoteMailbox, query).size());
    }
    
    @Override
    protected void setUp() throws Exception {
        TestUtil.cliSetup();
        ZimbraLog.toolSetupLog4j("INFO", null, false);
        ConfigServlet.LOCALHOST_ADMIN_URL = LOCAL_ADMIN_URL;
        
        cleanUp();
        createRemoteAccount();
        createLocalAccount();
        
        sync();
        checkMsgCount("in:inbox");
        checkMsgCount("in:inbox is:unread");
    }
    
    @Override
    protected void tearDown() throws Exception {
        cleanUp();
    }
    
    private void cleanUp() throws Exception {
        try {
            deleteLocalAccount();
            deleteRemoteAccount();
        } catch (Exception x) {
            ZimbraLog.test.warn("deleting accounts", x);
        }
    }
    
    private void createLocalAccount() throws Exception {
        XsyncBean.createAccount(ACCOUNT, USERNAME, PASSWORD, EMAIL, HOST, PORT, isSSL);
        ZMailbox.Options options = new ZMailbox.Options();
        options.setAccount(EMAIL);
        options.setAccountBy(AccountBy.name);
        options.setPassword(PASSWORD);
        options.setUri(LOCAL_SOAP_URL);
        localMailbox = ZMailbox.getMailbox(options);
    }
    
    private void deleteLocalAccount() throws Exception {
        Account local = JspProvStub.getInstance().getOfflineAccountByName(EMAIL);
        if (local != null)
            XsyncBean.deleteAccount(local.getId());
    }
    
    private void createRemoteAccount() throws Exception {
        TestUtil.createAccount(USERNAME);
        remoteMailbox = TestUtil.getZMailbox(USERNAME);
//      options = new ZMailbox.Options();
//      options.setAccount(EMAIL);
//      options.setAccountBy(AccountBy.name);
//      options.setPassword(PASSWORD);
//      options.setUri(REMOTE_SOAP_URL);
//      options.setRequestProtocol(SoapProtocol.Soap12);
//      options.setResponseProtocol(SoapProtocol.Soap12);
//      remoteMailbox = ZMailbox.getMailbox(options);
    }
    
    private void deleteRemoteAccount() throws Exception {
        if (TestUtil.accountExists(USERNAME))
            TestUtil.deleteAccount(USERNAME);
    }
}
