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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.jsp.ConfigServlet;
import com.zimbra.cs.offline.jsp.JspProvStub;
import com.zimbra.cs.offline.jsp.ZmailBean;
import com.zimbra.client.ZAppointment;
import com.zimbra.client.ZContact;
import com.zimbra.client.ZEmailAddress;
import com.zimbra.client.ZFolder;
import com.zimbra.client.ZMailbox;
import com.zimbra.client.ZMessage;
import com.zimbra.client.ZSearchFolder;
import com.zimbra.client.ZSearchParams;
import com.zimbra.client.ZTag;
import com.zimbra.client.ZMailbox.ZAppointmentResult;
import com.zimbra.client.ZMailbox.ZAttachmentInfo;
import com.zimbra.client.ZMailbox.ZOutgoingMessage;
import com.zimbra.client.ZMailbox.ZOutgoingMessage.MessagePart;
import com.zimbra.soap.type.SearchSortBy;

import junit.framework.TestCase;

public class TestXsync extends TestCase {

    private static final String ACCOUNT = "XSYNC";
    private static final String USERNAME = "xsync";
    private static final String PASSWORD = "test123";
    private static final String USER1 = "user1";
    private static final String EMAIL = "xsync@bostonceltics.local";
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
            //syncEmail includes test cases for both syncMessage and syncFolder
            syncEmail();
            syncContacts();
            syncTag();
            syncSearchFolder();
            syncCalender();
        } catch (Exception x) {
            ZimbraLog.test.warn("test xsync", x);
        }
    }

    private void syncTag() throws Exception {
        //create remote tag "cool" and add MSG1 to cool
        ZimbraLog.test.info("syncTag TEST 1");
        ZTag rcoolTag = remoteMailbox.createTag("cool", ZTag.Color.defaultColor);
        String rawmessage = TestUtil.getTestMessage("MSG1");
        String msgId = TestUtil.addRawMessage(remoteMailbox, rawmessage);
        remoteMailbox.markMessageRead(msgId, false);
        remoteMailbox.tagMessage(msgId, rcoolTag.getId(), true);
        sync();
        ZTag lcoolTag = localMailbox.getTagByName("cool");
        assertNotNull("lcoolTag tag", lcoolTag);
        assertEquals("lcoolTag color", ZTag.Color.defaultColor.getValue(), lcoolTag.getColor().getValue());
        checkMsgCount(localMailbox, "tag:cool is:unread", 1);

        //change the color of tag "cool" to regular color and unread the MSG1
        ZimbraLog.test.info("syncTag TEST 2");
        remoteMailbox.modifyTagColor(rcoolTag.getId(), ZTag.Color.red);
        remoteMailbox.markMessageRead(msgId, true);
        sync();
        lcoolTag = localMailbox.getTagByName("cool");
        assertNotNull("lcoolTag tag", lcoolTag);
        assertEquals("lcoolTag color", ZTag.Color.red.getValue(), lcoolTag.getColor().getValue());
        checkMsgCount(localMailbox, "tag:cool", 1);

        //change the color of tag "cool" to custom color and remove MSG1
        ZimbraLog.test.info("syncTag TEST 3");
        remoteMailbox.modifyTagColor(rcoolTag.getId(), ZTag.Color.rgbColor.setRgbColor("#006600"));
        remoteMailbox.deleteMessage(msgId);
        sync();
        lcoolTag = localMailbox.getTagByName("cool");
        assertEquals("lcoolTag color", "#006600", lcoolTag.getColor().getRgbColor());
        checkMsgCount(localMailbox, "tag:cool", 0);

        //rename the tag "cool" to "boston" and check the name and color
        ZimbraLog.test.info("syncTag TEST 4");
        remoteMailbox.renameTag(rcoolTag.getId(), "boston");
        localMailbox.modifyTagColor(lcoolTag.getId(), ZTag.Color.rgbColor.setRgbColor("#CC0000"));
        sync();
        ZTag rbostonTag = remoteMailbox.getTagById(rcoolTag.getId());
        assertNotNull("rbostonTag tag", rbostonTag);
        assertEquals("rbostonTag color", "#CC0000", rbostonTag.getColor().getRgbColor());
        ZTag lbostonTag = localMailbox.getTagById(rcoolTag.getId());
        assertNotNull("lbostonTag tag", lbostonTag);
        assertEquals("lbostonTag name", "boston", lbostonTag.getName());

        //update the tags name and color
        ZimbraLog.test.info("syncTag TEST 5");
        remoteMailbox.updateTag(rbostonTag.getId(), "Costello", ZTag.Color.rgbColor.setRgbColor("#EDEDED"));
        sync();
        ZTag lcostelloTag = localMailbox.getTagByName("Costello");
        assertNotNull("lcostelloTag tag", lcostelloTag);
        assertEquals("lcostelloTag color", "#EDEDED", lcostelloTag.getColor().getRgbColor());

        //change color at remote as well as local
        ZimbraLog.test.info("syncTag TEST 6");
        localMailbox.modifyTagColor(lcostelloTag.getId(), ZTag.Color.rgbColor.setRgbColor("#00CCCC"));
        remoteMailbox.modifyTagColor(rbostonTag.getId(), ZTag.Color.rgbColor.setRgbColor("#1A1A1A"));
        sync();
        ZTag rcostelloTag = remoteMailbox.getTag("Costello");
        assertEquals("lcostelloTag color", "#00CCCC", rcostelloTag.getColor().getRgbColor());

        //double tag a message and check the tag color at the inbox
        ZimbraLog.test.info("syncTag TEST 7");
        ZTag umassTag = localMailbox.createTag("Umass", ZTag.Color.blue);
        msgId = TestUtil.addRawMessage(localMailbox, rawmessage);
        localMailbox.markMessageRead(msgId, false);
        localMailbox.tagMessage(msgId, rcostelloTag.getId(), true);
        localMailbox.tagMessage(msgId, umassTag.getId(), true);
        sync();
        ZFolder rinBox = remoteMailbox.getInbox();
        assertNotNull("remote inbox", rinBox);
        checkMsgCount(localMailbox, "in:inbox is:unread", rinBox.getMessageCount());
        ZMessage recv = TestUtil.search(remoteMailbox, "in:inbox is:unread").get(0);
        assertEquals(2, recv.getMailbox().getTags(umassTag.getId() + "," + lcostelloTag.getId()).size());

        //remove the message and check the tag count
        ZimbraLog.test.info("syncTag TEST 8");
        localMailbox.deleteMessage(msgId);
        sync();
        checkMsgCount(remoteMailbox, "tag:Umass", 0);
    }

    private void syncSearchFolder() throws Exception {
        //create remote F1 and add raw MSG1 to remote inbox and then check the searchfolder for the MSG1
        ZimbraLog.test.info("syncSearchFolder TEST 1");
        String rawmessage = TestUtil.getTestMessage("MSGSRCH1");
        String msgId = TestUtil.addRawMessage(remoteMailbox, rawmessage);
        remoteMailbox.markMessageRead(msgId, false);
        ZSearchFolder sf1 = remoteMailbox.createSearchFolder("" + Mailbox.ID_FOLDER_USER_ROOT, "SRCHF1", "is:unread", null, SearchSortBy.dateDesc, ZFolder.Color.rgbColor.setRgbColor("#00CCCC"));
        sync();
        ZSearchFolder cf1 = localMailbox.getSearchFolderById(sf1.getId());
        assertNotNull("local /SRCHF1", cf1);
        assertEquals("Folder SRCHF1 color", "#00CCCC", cf1.getColor().getRgbColor());
        assertEquals(1, TestUtil.search(cf1.getMailbox(), "is:unread").size());

        //change the search query and check the searchfolder for unread message
        ZimbraLog.test.info("syncSearchFolder TEST 2");
        localMailbox.modifySearchFolder(cf1.getId(), "is:read", null, SearchSortBy.dateDesc);
        remoteMailbox.markMessageRead(msgId, true);
        sync();
        assertEquals(0, TestUtil.search(cf1.getMailbox(), "is:unread").size());

        //conflict resolution - make changes in both local & remote accounts
        ZimbraLog.test.info("syncSearchFolder TEST 3");
        localMailbox.modifySearchFolder(cf1.getId(), "is:unread", null, SearchSortBy.dateDesc);
        remoteMailbox.modifySearchFolder(sf1.getId(), "is:read", null, SearchSortBy.dateDesc);
        sync();
        assertEquals(0, TestUtil.search(cf1.getMailbox(), "is:unread").size());

        ZimbraLog.test.info("syncSearchFolder TEST 4");
        localMailbox.deleteFolder(cf1.getId());
        remoteMailbox.modifySearchFolder(sf1.getId(), "is:read", null, SearchSortBy.dateDesc);
        sync();
        ZFolder cf5 = remoteMailbox.getFolderByPath("/SRCHF1");
        assertNull("local /F5", cf5);
    }

    private void syncCalender() throws Exception {
        ZimbraLog.test.info("syncCalender TEST 1");
        ZMailbox invite = TestUtil.getZMailbox(USER1);
        String subject = "syncCalender testInvite request 1";
        Date startDate = new Date(System.currentTimeMillis() + Constants.MILLIS_PER_DAY);
        Date endDate = new Date(startDate.getTime() + Constants.MILLIS_PER_HOUR);
        ZAppointmentResult remoteAppointment = TestUtil.createAppointment(remoteMailbox, subject, invite.getName(), startDate, endDate);
        sync();
        ZAppointment localAppointment = localMailbox.getAppointment(remoteAppointment.getCalItemId());
        assertNotNull("local Appointment", localAppointment);
        assertEquals("calAppointment name", subject, localAppointment.getInvites().get(0).getComponent().getName());

        ZimbraLog.test.info("syncCalender TEST 2");
        ZMessage msg = TestUtil.search(invite, "in:inbox subject:\"" + subject + "\"").get(0);
        TestUtil.sendInviteReply(invite, msg.getId(), remoteMailbox.getName(), subject, ZMailbox.ReplyVerb.ACCEPT);
        sync();
        msg = TestUtil.waitForMessage(localMailbox, "in:inbox subject:\"" + subject + "\"");
    }

    private void syncEmail() throws Exception {
        //send message to self
        ZimbraLog.test.info("syncEmail TEST 1");
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
        ZimbraLog.test.info("syncEmail TEST 2");
        remoteMailbox.deleteMessage(recv.getId());
        localMailbox.deleteMessage(sent.getId());
        sync();
        checkMsgCount(localMailbox, "in:inbox", 0);
        checkMsgCount(remoteMailbox, "in:sent", 0);

        //create remote F1 and add MSG1 to F1
        ZimbraLog.test.info("syncEmail TEST 3");
        ZFolder sf1 = TestUtil.createFolder(remoteMailbox, "" + Mailbox.ID_FOLDER_USER_ROOT,  "F1");
        String sm1Id = TestUtil.addMessage(remoteMailbox, "MSG1", sf1.getId(), "u");
        sync();
        ZFolder cf1 = localMailbox.getFolderByPath("/F1");
        assertNotNull("local /F1", cf1);
        checkMsgCount(localMailbox, "in:F1 is:unread", 1);

        //create remote F2, move MSG1 to F2, and mark MSG1 read
        ZimbraLog.test.info("syncEmail TEST 4");
        ZFolder sf2 = remoteMailbox.createFolder(sf1.getId(), "F2", null, ZFolder.Color.rgbColor.setRgbColor("#00CCCC"), null, null);
        remoteMailbox.moveMessage(sm1Id, sf2.getId());
        remoteMailbox.markMessageRead(sm1Id, true);
        sync();
        ZFolder cf2 = localMailbox.getFolderByPath("/F1/F2");
        assertNotNull("local /F1/F2", cf2);
        checkMsgCount(localMailbox, "in:F1", 0);
        checkMsgCount(localMailbox, "in:F1/F2", 1);
        assertEquals("F2 color", "#00CCCC", cf2.getColor().getRgbColor());
        ZMessage cm1 = TestUtil.search(localMailbox, "in:F1/F2").get(0);
        assertFalse("MSG1 unread", cm1.isUnread());

        //create local F3 and move MSG1 into it, and mark MSG1 unread
        ZimbraLog.test.info("syncEmail TEST 5");
        ZFolder cf3 = localMailbox.createFolder(cf2.getId(), "F3", null, ZFolder.Color.rgbColor.setRgbColor("#1A1A1A"), null, null);
        localMailbox.moveMessage(cm1.getId(), cf3.getId());
        localMailbox.markMessageRead(cm1.getId(), false);
        sync();
        ZFolder sf3 = remoteMailbox.getFolderByPath("/F1/F2/F3");
        assertNotNull("remote /F1/F2/F3", sf3);
        assertEquals("F3 color", "#1A1A1A", cf2.getColor().getRgbColor());
        checkMsgCount(remoteMailbox, "in:F1/F2", 0);
        checkMsgCount(remoteMailbox, "in:F1/F2/F3", 1);
        ZMessage sm1 = TestUtil.search(remoteMailbox, "in:F1/F2/F3").get(0);
        assertTrue("MSG1 read", sm1.isUnread()); //need to preserv local changes when moving a message

        //create local F4 and move F3 into it
        ZimbraLog.test.info("syncEmail TEST 6");
        ZFolder cf4 = TestUtil.createFolder(localMailbox, "" + Mailbox.ID_FOLDER_USER_ROOT, "F4");
        localMailbox.moveFolder(cf3.getId(), cf4.getId());
        localMailbox.modifyFolderColor(cf3.getId(), ZFolder.Color.red);
        sync();
        checkMsgCount(remoteMailbox, "in:F4/F3", 1);
        ZFolder sf4 = remoteMailbox.getFolderByPath("/F4");
        assertNotNull("remote /F4", sf4);
        assertNull("remote /F1/F2/F3", remoteMailbox.getFolderByPath("/F1/F2/F3"));
        sf3 = remoteMailbox.getFolderByPath("/F4/F3");
        assertEquals("F3 color", ZFolder.Color.red.getValue(), sf3.getColor().getValue());
        assertNotNull("remote /F4/F3", sf3);

        //add remote F5 into F2, but delete local F2. F5 should be /F5 on both local and remote
        ZimbraLog.test.info("syncEmail TEST 7");
        ZFolder sf5 = TestUtil.createFolder(remoteMailbox, sf2.getId(),  "F5");
        localMailbox.deleteFolder(cf2.getId());
        sync();
        ZFolder cf5 = localMailbox.getFolderByPath("/F5");
        assertNotNull("local /F5", cf5);
        sf5 = remoteMailbox.getFolderByPath("/F5");
        assertNotNull("remote /F5", sf5);

        //add /F5/F6 on both local and remote, remote F6 should be merged with local F6.
        ZimbraLog.test.info("syncEmail TEST 8");
        ZFolder cf6 = TestUtil.createFolder(localMailbox, cf5.getId(), "F6");
        ZFolder sf6 = TestUtil.createFolder(remoteMailbox, sf5.getId(), "F6");
        sync();
        cf6 = localMailbox.getFolderByPath("/F5/F6");
        assertNotNull("local /F5/F6", cf6);
        sf6 = remoteMailbox.getFolderByPath("/F5/F6");
        assertNotNull("remote /F5/F6", sf6);

        //add /F5/F6/F5 on remote, but delete /F5/F6 local, new F5 should be relocated to /F5 and renamed
        ZimbraLog.test.info("syncEmail TEST 9");
        ZFolder sf5_ = TestUtil.createFolder(remoteMailbox, sf6.getId(), "F5");
        localMailbox.deleteFolder(cf6.getId());
        sync();
        sf5_ = remoteMailbox.getFolderById(sf5_.getId());
        assertNotNull("remote /F5...", sf5_);
        ZFolder cf5_ = localMailbox.getFolderByPath("/" + sf5_.getName());
        assertNotNull("local /F5...", cf5_);

        ZimbraLog.test.info("syncEmail TEST 10");
        //move remote /F5 to /F4/F3/F5 and rename that to /F4/F3/F1, but delete /F4 locally, expect renamed F1 get moved to /F1 and renamed again
        remoteMailbox.moveFolder(sf5.getId(), sf3.getId());
        remoteMailbox.renameFolder(sf5.getId(), "F1", sf3.getId());
        localMailbox.deleteFolder(cf4.getId());
        sync();
        assertTrue("MSG1 deleted", TestUtil.search(remoteMailbox, "subject:MSG1").isEmpty());
        ZFolder sf1_ = remoteMailbox.getFolderById(sf5.getId());
        assertNotNull("remote /F1...", sf1_);
        ZFolder cf1_ = localMailbox.getFolderByPath("/" + sf1_.getName());
        assertNotNull("local /F1...", cf1_);

        //delete remote F1, but move local F1... into F1. remote F1 should get recreated. 
        ZimbraLog.test.info("syncEmail TEST 11");
        remoteMailbox.deleteFolder(sf1_.getId());
        localMailbox.moveFolder(cf1.getId(), cf1_.getId());
        sync();
        cf1_ = localMailbox.getFolderByPath("/F1/" + cf1.getName());
        assertNotNull("local /F1/F1...", cf1_);
        sf1 = remoteMailbox.getFolderByPath("/F1");
        assertNotNull("remote /F1", sf1);

        //move /F5_ under /F1 and create /F1/F1_/F7. now we have /F1/F1_/F7 and /F1/F5_
        ZimbraLog.test.info("syncEmail TEST 12");
        localMailbox.moveFolder(cf5_.getId(), sf1.getId());
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
        ZimbraLog.test.info("syncEmail TEST 13");
        remoteMailbox.deleteFolder(sf1.getId());
        localMailbox.moveFolder(cf5_.getId(), "" + Mailbox.ID_FOLDER_USER_ROOT);
        localMailbox.moveFolder(cf7.getId(), sf1.getId());
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

        ZimbraLog.test.info("syncEmail TEST 14");
        TestUtil.addMessage(remoteMailbox, "MSG2", sf1.getId(), "u");
        TestUtil.addMessage(remoteMailbox, "MSG3", sf8.getId(), "u");
        String sm4Id = TestUtil.addMessage(remoteMailbox, "MSG4", "" + Mailbox.ID_FOLDER_INBOX, "u");
        sync();
        ZMessage cm2 = TestUtil.search(localMailbox, "in:F1").get(0);
        ZMessage cm3 = TestUtil.search(localMailbox, "subject:MSG3").get(0);
        ZMessage cm4 = TestUtil.search(localMailbox, "in:inbox").get(0);

        ZimbraLog.test.info("syncEmail TEST 15");
        remoteMailbox.deleteFolder(sf1.getId());
        localMailbox.moveMessage(cm4.getId(), cf1_.getId()); //remote /F1 and /F1/F1... should be recreated
        localMailbox.moveMessage(cm3.getId(), "" + Mailbox.ID_FOLDER_INBOX); //cm3 should be deleted
        localMailbox.moveMessage(cm2.getId(), cf8.getId()); //cm2 should be deleted, along with F8
        sync();
        sf1_ = remoteMailbox.getFolderByPath(cf1_.getPath());
        assertNotNull("remote /F1/F1...", sf1_);
        assertEquals(0, TestUtil.search(localMailbox, "subject:MSG2").size());
        assertEquals(0, TestUtil.search(localMailbox, "subject:MSG3").size());
        ZMessage sm4 = remoteMailbox.getMessageById(sm4Id);
        assertEquals("remote MSG4", sm4.getFolderId(), sf1_.getId());

        //move MSG4 to Inbox and delete local /F1/F1...
        ZimbraLog.test.info("syncEmail TEST 16");
        localMailbox.moveMessage(cm4.getId(), "" + Mailbox.ID_FOLDER_INBOX);
        localMailbox.deleteFolder(sf1_.getId());
        sync();
        assertNull("remote /F1/F1...", remoteMailbox.getFolderByPath(sf1_.getPath()));
        sm4 = remoteMailbox.getMessageById(sm4Id);
        assertEquals("remote MSG4", sm4.getFolderId(), "" + Mailbox.ID_FOLDER_INBOX);

        ZimbraLog.test.info("syncEmail TEST 17");
        localMailbox.moveMessage(cm4.getId(), sf1.getId());
        localMailbox.deleteFolder(sf1.getId());
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
    	ZmailBean.createAccount(ACCOUNT, USERNAME, PASSWORD, EMAIL, HOST, PORT, isSSL);
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
        	ZmailBean.deleteAccount(local.getId());
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
