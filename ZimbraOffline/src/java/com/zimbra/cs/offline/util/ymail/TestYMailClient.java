package com.zimbra.cs.offline.util.ymail;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import com.zimbra.cs.offline.util.yauth.RawAuthManager;
import com.zimbra.cs.offline.util.yauth.FileTokenStore;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.mailclient.imap.ImapConnection;
import com.zimbra.cs.mailclient.imap.ImapConfig;
import com.zimbra.cs.mailclient.imap.IDInfo;
import com.zimbra.cs.mailclient.imap.Mailbox;
import com.zimbra.cs.mailclient.imap.MessageData;
import com.zimbra.cs.mailclient.imap.Body;
import com.zimbra.cs.mailclient.imap.BodyStructure;
import com.zimbra.cs.util.ZimbraApplication;
import com.zimbra.cs.util.JMSession;
import com.yahoo.mail.UserData;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimePart;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;
import javax.mail.Session;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.MessagingException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Map;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import junit.framework.TestSuite;

public class TestYMailClient {
    private static YMailClient ymc;
    private static ImapConnection imc;
    private static Mailbox mb;

    private static final Logger LOG = Logger.getLogger(TestYMailClient.class);
    
    private static final File TOKENS_FILE = new File("/tmp/tokens");

    private static final String APPID = OfflineLC.zdesktop_yauth_appid.value();
    private static final String USER = "dacztest";
    private static final String PASS = "test1234";

    private static final String FROM = USER + "@yahoo.com";
    private static final String TO = FROM;
    private static final String SENT = "Sent";

    private static final File DATA_DIR =
        new File("/Users/dac/src/zimbra/FRANKLIN/ZimbraServer/data/TestMailRaw");

    private static final File MSG_MULTIPART = new File("/Users/dac/mail.txt");
    // new File(DATA_DIR, "15");
    private static final File MSG_SIMPLE = new File(DATA_DIR, "11");
    private static final File MSG_FORWARDED = new File(DATA_DIR, "spam.txt");

    @BeforeClass
    public static void setUpOnce() throws Exception {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        LOG.setLevel(Level.DEBUG);
        // Set up YMail client
        Logger.getLogger(YMailClient.class).setLevel(Level.DEBUG);
        RawAuthManager ram = new RawAuthManager(new FileTokenStore(TOKENS_FILE));
        ymc = new YMailClient(ram.authenticate(APPID, USER, PASS));
        ymc.enableTrace(System.out);
        // Set up IMAP connection
        imc = connect(USER, PASS);
    }

    @AfterClass
    public static void tearDownOnce() throws Exception {
        imc.logout();
    }
    
    private static ImapConnection connect(String user, String pass)
        throws IOException {
        ImapConfig config = new ImapConfig();
        config.setHost("imap.mail.yahoo.com");
        config.setAuthenticationId(user);
        config.setMaxLiteralMemSize(200);
        config.setDebug(true);
        // config.setTrace(true);
        config.setTimeout(600);
        ImapConnection connection = new ImapConnection(config);
        connection.connect();
        IDInfo id = new IDInfo();
        id.put("guid", ZimbraApplication.getInstance().getId());
        connection.id(id);
        connection.login(pass);
        return connection;
    }

    @Test
    public void testUserData() throws Exception {
        UserData ud = ymc.getUserData();
        assertTrue(ud.getUserFeaturePref().isIsPremium());
    }

    @Test
    public void testUpload() throws Exception {
        debug("Testing upload of message %s", MSG_MULTIPART);
        MimeMessage mm = readMessage(MSG_MULTIPART);
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
        debug("Testing send of simple message %s", MSG_SIMPLE);
        MimeMessage mm = readMessage(MSG_SIMPLE);
        ymc.sendMessage(mm);
    }

    @Test
    public void testSendMultipart() throws Exception {
        debug("Testing send of multipart message %s", MSG_MULTIPART);
        sendAndCompare(MSG_MULTIPART);
    }

    private void sendAndCompare(File msg) throws Exception {
        MimeMessage mm = readMessage(msg);
        System.out.println("Sending message:");
        dump(mm);
        Mailbox mb = imc.select(SENT);
        assertNotNull(mb);
        String mid = ymc.sendMessage(mm);
        assertNotNull(mid);
        imc.select(SENT);
        long uid = findUid(mid, mb.getUidNext());
        System.out.println("Receiving message:");
        dump(getMessage(uid));
        // BodyStructure bs = getBodyStructure(uid);
        // System.out.println(bs);
        // compare(mm, sent);
    }

    private void dump(MimeMessage mp) throws Exception {
        dump(mp, 0, 0);
    }
    
    private void dump(MimePart mp, int depth, int count) throws Exception {
        ContentType ct = new ContentType(mp.getContentType());
        if ("multipart".equals(ct.getPrimaryType())) {
            Multipart m = (Multipart) mp.getContent();
            pf(depth, "%d: type=%s/%s encoding=%s disposition=%s count=%d",
               count, ct.getPrimaryType(), ct.getSubType(), mp.getEncoding(),
               mp.getDisposition(), m.getCount());
            for (int i = 0; i < m.getCount(); i++) {
                dump((MimePart) m.getBodyPart(i), depth + 1, i);
            }
        } else {
            MimeBodyPart mbp = (MimeBodyPart) mp;
            InputStream is = MimeUtility.decode(
                mbp.getRawInputStream(), getEncoding(mp));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copy(is, baos);
            pf(depth, "%d: type=\"%s\" encoding=%s disposition=%s size=%d",
               count, mp.getContentType(), mp.getEncoding(),
               mp.getDisposition(), baos.size());
            if ("text".equals(ct.getPrimaryType()) && baos.size() < 64) {
                pf(depth, "   content=\"%s\"", baos.toString());
            }
        }
    }

    private void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int len;
        while ((len = is.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
    }
    
    private String getEncoding(MimePart mp) throws MessagingException {
        String encoding = mp.getEncoding();
        return encoding != null ? encoding : "7bit";
    }

    private String getDisposition(MimePart mp) throws MessagingException {
        String disposition = mp.getDisposition();
        return disposition != null ? disposition : "inline";
    }
    
    private int getSize(InputStream is) throws IOException {
        int count = 0;
        while (is.read() != -1) count++;
        return count;
    }
    
    private void pf(int depth, String fmt, Object... args) {
        System.out.printf("%s%s\n", spaces(depth), String.format(fmt, args));
    }
    
    private String spaces(int depth) {
        char[] spaces = new char[depth * 4];
        Arrays.fill(spaces, ' ');
        return new String(spaces);
    }
    
    private void compare(MimePart mp1, MimePart mp2) throws Exception {
        ContentType ct1 = new ContentType(mp1.getContentType());
        ContentType ct2 = new ContentType(mp2.getContentType());
        assertEquals(ct1.getPrimaryType(), ct2.getPrimaryType());
        assertEquals(ct1.getSubType(), ct2.getSubType());
        // assertEquals(mp1.getEncoding(), mp2.getEncoding());
        // assertEquals(mp1.getDisposition(), mp2.getDisposition());
        // assertEquals(mp1.getSize(), mp2.getSize());
        Object c1 = mp1.getContent();
        Object c2 = mp2.getContent();
        // assertEquals(c1.getClass(), c2.getClass());
        if (c1 instanceof String) {
            assertEquals(c1, c2);
        } else if (c1 instanceof InputStream) {
            compare((InputStream) c1, (InputStream) c2);
        } else if (c1 instanceof Multipart) {
            compare((Multipart) c1, (Multipart) c2);
        } else {
            fail("Unexpected content type: " + mp1.getContentType());
        }
    }

    private void compare(Multipart m1, Multipart m2) throws Exception {
        assertEquals(m1.getCount(), m2.getCount());
        for (int i = 0; i < m1.getCount(); i++) {
            compare((MimePart) m1.getBodyPart(i), (MimePart) m2.getBodyPart(i));
        }
    }

    private void compare(InputStream is1, InputStream is2) throws Exception {
        int count = 0;
        int ch;
        do {
            ch = is1.read();
            count++;
            assertEquals("byte number " + count + " differs", ch, is2.read());
        } while (ch != -1);
    }

    private static final Pattern UMID = Pattern.compile("(?m)^(?i:X-YMAIL-UMID): (.*)$");

    private MimeMessage getMessage(long uid) throws IOException, MessagingException {
        Body body = getMessageData(uid, "BODY.PEEK[]").getBodySections()[0];
        return new MimeMessage(JMSession.getSession(), body.getInputStream());
    }

    private BodyStructure getBodyStructure(long uid) throws IOException {
        return getMessageData(uid, "BODYSTRUCTURE").getBodyStructure();
    }
    
    private MessageData getMessageData(long uid, String param)
        throws IOException {
        Map<Long, MessageData> mds = imc.uidFetch(Long.toString(uid), param);
        if (!mds.containsKey(uid)) {
            fail("Message not found for uid " + uid);
        }
        return mds.get(uid);
    }

    // Finds UID of message matching specified UMID
    private long findUid(String mid, long startUid) throws IOException {
        for (MessageData md : imc.uidFetch(startUid + ":*", "BODY.PEEK[HEADER]").values()) {
            String header = md.getBodySections()[0].getData().toString();
            Matcher matcher = UMID.matcher(header);
            if (matcher.find() && matcher.group(1).equals(mid)) {
                return md.getUid();
            }
        }
        fail("Message not found for mid " + mid);
        return -1;
    }

    private static final String HEADER =
        "Content-Type: text/plain\r\nX-YMail-UMID: 1234\r\nFoo: bar\r\n";
    
    @Test
    public void testMatcher() {
        Matcher matcher = UMID.matcher(HEADER);
        assertTrue(matcher.find());
        assertEquals("1234", matcher.group(1));
    }
    
    /*
    @Test
    public void testForwarded() throws Exception {
        debug("Testing send of forwarded message as attachment %s", MSG_FORWARDED);
        MimeMessage mm = parseMimeMessage(MSG_FORWARDED);
        ymc.sendMessage(mm);
    }
    */

    private static MimeMessage readMessage(File file) throws Exception {
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

    public static void main(String... args) throws Exception {
        setUpOnce();
        try {
            new TestYMailClient().testSendMultipart();
        } finally {
            imc.logout();
        }
    }
}
