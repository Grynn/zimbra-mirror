package com.zimbra.cs.offline.util.ymail;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import com.zimbra.cs.offline.util.yauth.RawAuthManager;
import com.zimbra.cs.offline.OfflineLC;
import com.yahoo.mail.UserData;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.InternetAddress;
import javax.mail.Session;
import javax.mail.Address;
import javax.mail.Message;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

public class TestYMailClient {
    private static YMailClient ymc;

    private static final Logger LOG = Logger.getLogger(TestYMailClient.class);
    
    private static final File TOKENS_FILE = new File("/tmp/tokens");

    private static final String APPID = OfflineLC.zdesktop_yauth_appid.value();
    private static final String USER = "jjztest1";
    private static final String PASS = "test1234";

    private static final String FROM = USER + "@yahoo.com";
    private static final String TO = FROM;

    private static final File DATA_DIR =
        new File("/Users/dac/src/zimbra/FRANKLIN/ZimbraServer/data/TestMailRaw");

    private static final File TEST_MSG_1 = new File(DATA_DIR, "15");
    private static final File TEST_MSG_2 = new File(DATA_DIR, "11");

    @BeforeClass
    public static void setUpOnce() throws Exception {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        LOG.setLevel(Level.DEBUG);
        Logger.getLogger(YMailClient.class).setLevel(Level.DEBUG);
        RawAuthManager ram = new RawAuthManager(TOKENS_FILE);
        ymc = new YMailClient(ram.authenticate(APPID, USER, PASS));
        ymc.enableTrace(System.out);
    }

    @Test
    public void testUserData() throws Exception {
        UserData ud = ymc.getUserData();
        assertTrue(ud.getUserFeaturePref().isIsPremium());
    }

    @Test
    public void testUpload() throws Exception {
        debug("Testing upload of message %s", TEST_MSG_1);
        MimeMessage mm = parseMimeMessage(TEST_MSG_1);
        MimeMultipart mmp = (MimeMultipart) mm.getContent();
        debug("Message part count = %d", mmp.getCount());
        for (int i = 0; i < mmp.getCount(); i++) {
            MimeBodyPart mbp = (MimeBodyPart) mmp.getBodyPart(i);
            debug("Uploading attachment size=%d, type=%s, encoding=%s",
                  mbp.getSize(), mbp.getContentType(), mbp.getEncoding());
            ymc.uploadAttachment(mbp);
        }
    }

    @Test
    public void testSendSimple() throws Exception {
        debug("Testing send of simple message %s", TEST_MSG_2);
        MimeMessage mm = parseMimeMessage(TEST_MSG_2);
        ymc.sendMessage(mm);
    }

    @Test
    public void testSendMultipart() throws Exception {
        debug("Testing send of multipart message %s", TEST_MSG_1);
        MimeMessage mm = parseMimeMessage(TEST_MSG_1);
        ymc.sendMessage(mm);
    }

    private static MimeMessage parseMimeMessage(File file) throws Exception {
        Session session = Session.getInstance(new Properties());
        InputStream is = new FileInputStream(file);
        try {
            MimeMessage mm = new MimeMessage(session, is);
            mm.setFrom(new InternetAddress(FROM));
            mm.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
            mm.setRecipients(Message.RecipientType.CC, (Address[]) null);
            mm.setRecipients(Message.RecipientType.BCC, (Address[]) null);
            return mm;
        } finally {
            is.close();
        }
    }

    private static void debug(String fmt, Object... args) {
        LOG.debug(String.format(fmt, args));
    }
}
