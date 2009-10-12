package com.zimbra.qa.unittest;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.jsp.ConfigServlet;
import com.zimbra.cs.offline.jsp.XsyncBean;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMessage;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage.MessagePart;

import junit.framework.TestCase;

public class TestXsync extends TestCase {

    private static final String ACCOUNT = "XSYNC";
    private static final String USERNAME = "user2";
    private static final String PASSWORD = "test123";
    private static final String EMAIL = "user2@jjmac.local";
    private static final String HOST = "localhost";
    private static final int PORT = 7070;
    private static final boolean isSSL = false;
    private static final String LOCAL_ADMIN_URL = "http://localhost:7733/service/admin/soap";
    private static final String LOCAL_SOAP_URL = "http://localhost:7733/service/soap";
    private static final String REMOTE_SOAP_URL = "http://localhost:7070/service/soap";
    
    private String localAccountId;
    private ZMailbox localMailbox;
    private ZMailbox remoteMailbox;
    
    public void testXsync() throws Exception {
        sync();
        checkMsgCount("in:inbox");
        checkMsgCount("in:inbox is:unread");
        
        //send message to self
        ZOutgoingMessage msg = new ZOutgoingMessage();
        List<ZEmailAddress> addresses = new ArrayList<ZEmailAddress>();
        addresses.add(new ZEmailAddress(EMAIL, null, null, ZEmailAddress.EMAIL_TYPE_TO));
        msg.setAddresses(addresses);
        msg.setSubject("MSG1");
        msg.setMessagePart(new MessagePart("text/plain", "This is the outer message"));
        localMailbox.sendMessage(msg, null, false);
        sync();
        ZMessage sm1 = TestUtil.search(remoteMailbox, "in:inbox is:unread").get(0);
        assertNotNull(sm1);
        ZMessage cm1 = TestUtil.search(localMailbox, "in:inbox is:unread").get(0);
        assertNotNull(cm1);
        ZMessage sent = TestUtil.search(localMailbox, "in:sent").get(0);
        assertNotNull(sent);
        
        //create remote F1 and add MSG1 to F1, and delete local sent message
        ZFolder sf1 = TestUtil.createFolder(remoteMailbox, "" + Mailbox.ID_FOLDER_USER_ROOT,  "F1");
        remoteMailbox.moveMessage(sm1.getId(), sf1.getId());
        localMailbox.deleteMessage(sent.getId());
        sync();
        ZFolder cf1 = localMailbox.getFolderByPath("/F1");
        assertNotNull("local /F1", cf1);
        checkMsgCount(localMailbox, "in:F1 is:unread", 1);
        checkMsgCount(remoteMailbox, "in:sent", 0);
        
        //create remote F2, move MSG1 to F2, and mark MSG1 read
        ZFolder sf2 = TestUtil.createFolder(remoteMailbox, sf1.getId(),  "F2");
        remoteMailbox.moveMessage(sm1.getId(), sf2.getId());
        remoteMailbox.markMessageRead(sm1.getId(), true);
        sync();
        ZFolder cf2 = localMailbox.getFolderByPath("/F1/F2");
        assertNotNull("local /F1/F2", cf2);
        checkMsgCount(localMailbox, "in:F1", 0);
        checkMsgCount(localMailbox, "in:F1/F2", 1);
        cm1 = TestUtil.search(localMailbox, "in:F1/F2").get(0);
        assertFalse("MSG1 unread", cm1.isUnread());

        //create local F3 and move MSG1 into it, and mark MSG1 unread
        ZFolder cf3 = TestUtil.createFolder(localMailbox, cf2.getId(), "F3");
        localMailbox.moveMessage(cm1.getId(), cf3.getId());
        localMailbox.markMessageRead(cm1.getId(), false);
        sync();
        ZFolder sf3 = remoteMailbox.getFolderByPath("/F1/F2/F3");
        assertNotNull("remote /F1/F2/F3", sf3);
        checkMsgCount(remoteMailbox, "in:F1/F2", 0);
        checkMsgCount(remoteMailbox, "in:F1/F2/F3", 1);
        sm1 = TestUtil.search(remoteMailbox, "in:F1/F2/F3").get(0);
        assertTrue("MSG1 read", sm1.isUnread()); //need to preserv local changes when moving a message
        
        //create local F4 and move F3 into it
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
        ZFolder sf5 = TestUtil.createFolder(remoteMailbox, sf2.getId(),  "F5");
        localMailbox.deleteFolder(cf2.getId());
        sync();
        ZFolder cf5 = localMailbox.getFolderByPath("/F5");
        assertNotNull("local /F5", cf5);
        sf5 = remoteMailbox.getFolderByPath("/F5");
        assertNotNull("remote /F5", sf5);
        
        //add /F5/F6 on both local and remote, remote F6 should be merged with local F6.
        ZFolder cf6 = TestUtil.createFolder(localMailbox, cf5.getId(), "F6");
        ZFolder sf6 = TestUtil.createFolder(remoteMailbox, sf5.getId(), "F6");
        sync();
        cf6 = localMailbox.getFolderByPath("/F5/F6");
        assertNotNull("local /F5/F6", cf6);
        sf6 = remoteMailbox.getFolderByPath("/F5/F6");
        assertNotNull("remote /F5/F6", sf6);
        
        //add /F5/F6/F5 on remote, but delete /F5/F6 local, new F5 should be relocated to /F5 and renamed
        ZFolder sf5_ = TestUtil.createFolder(remoteMailbox, sf6.getId(), "F5");
        localMailbox.deleteFolder(cf6.getId());
        sync();
        sf5_ = remoteMailbox.getFolderById(sf5_.getId());
        assertNotNull("remote /F5...", sf5_);
        ZFolder cf5_ = localMailbox.getFolderByPath("/" + sf5_.getName());
        assertNotNull("local /F5...", cf5_);
        
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
        
        String sm2Id = TestUtil.addMessage(remoteMailbox, "MSG2", sf1.getId(), "u");
        String sm3Id = TestUtil.addMessage(remoteMailbox, "MSG3", sf8.getId(), "u");
        String sm4Id = TestUtil.addMessage(remoteMailbox, "MSG4", "" + Mailbox.ID_FOLDER_INBOX, "u");
        sync();
        ZMessage cm2 = TestUtil.search(localMailbox, "in:F1").get(0);
        ZMessage cm3 = TestUtil.search(localMailbox, "subject:MSG3").get(0);
        ZMessage cm4 = TestUtil.search(localMailbox, "in:inbox").get(0);
        
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
        localMailbox.moveMessage(cm4.getId(), "" + Mailbox.ID_FOLDER_INBOX);
        localMailbox.deleteFolder(cf1_.getId());
        sync();
        assertNull("remote /F1/F1...", remoteMailbox.getFolderByPath(cf1_.getPath()));
        sm4 = remoteMailbox.getMessageById(sm4Id);
        assertEquals("remote MSG4", sm4.getFolderId(), "" + Mailbox.ID_FOLDER_INBOX);
        
        localMailbox.moveMessage(cm4.getId(), cf1.getId());
        localMailbox.deleteFolder(cf1.getId());
        sync();
        assertNull("remote /F1", remoteMailbox.getFolderByPath("/F1"));
        assertEquals(0, TestUtil.search(remoteMailbox, "subject:MSG4").size());
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
        ZimbraLog.toolSetupLog4j("INFO", null, false);
        
        ConfigServlet.LOCALHOST_ADMIN_URL = LOCAL_ADMIN_URL;
        
        createLocalAccount();
        
//        localAccount = (OfflineAccount)OfflineProvisioning.getOfflineInstance().get(AccountBy.id, localAccountId);
//        ds = OfflineProvisioning.getOfflineInstance().getDataSource(localAccount);
//        xmbox = (ExchangeMailbox)(MailboxManager.getInstance().getMailboxByAccount(localAccount));
        
        ZMailbox.Options options = new ZMailbox.Options();
        options.setAccount(EMAIL);
        options.setAccountBy(AccountBy.name);
        options.setPassword(PASSWORD);
        options.setUri(LOCAL_SOAP_URL);
        options.setRequestProtocol(SoapProtocol.Soap12);
        options.setResponseProtocol(SoapProtocol.Soap12);
        localMailbox = ZMailbox.getMailbox(options);
        
        options = new ZMailbox.Options();
        options.setAccount(EMAIL);
        options.setAccountBy(AccountBy.name);
        options.setPassword(PASSWORD);
        options.setUri(REMOTE_SOAP_URL);
        options.setRequestProtocol(SoapProtocol.Soap12);
        options.setResponseProtocol(SoapProtocol.Soap12);
        remoteMailbox = ZMailbox.getMailbox(options);
    }  
    
    @Override
    protected void tearDown() throws Exception {
        deleteLocalAccount();
    }
    
    private void createLocalAccount() throws Exception {
        localAccountId = XsyncBean.createAccount(ACCOUNT, USERNAME, PASSWORD, EMAIL, HOST, PORT, isSSL);
    }
    
    private void deleteLocalAccount() throws Exception {
        XsyncBean.deleteAccount(localAccountId);
    }
}
