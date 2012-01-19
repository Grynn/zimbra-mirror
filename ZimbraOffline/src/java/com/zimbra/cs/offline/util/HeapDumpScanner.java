/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011,2012 Zimbra, Inc.
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
package com.zimbra.cs.offline.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import com.google.common.base.Strings;
import com.zimbra.common.mime.shim.JavaMailInternetAddress;
import com.zimbra.common.util.FileUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.MailSender;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.util.JMSession;

public class HeapDumpScanner {

    private static HeapDumpScanner instance = new HeapDumpScanner();

    private HeapDumpScanner() {
    }

    public static HeapDumpScanner getInstance() {
        return instance;
    }

    private volatile boolean isCheckingCalled = false;

    public boolean hasHeapDump() {
        if (!OfflineLC.zdesktop_heapdump_enabled.booleanValue()) {
            return false;
        }
        String dumpPath;
        try {
            dumpPath = new File(OfflineLC.zdesktop_heapdump_dir.value()).getCanonicalPath();
        } catch (IOException e) {
            OfflineLog.offline.warn("[heapdump] IOException when getting heapdump path");
            return false;
        }
        File dumpDir = new File(dumpPath);
        if (dumpDir == null || !dumpDir.exists()) {
            OfflineLog.offline.warn("[heapdump] heapdump path: %s doesn't exist", dumpPath);
            return false;
        }
        File[] dumps = dumpDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".hprof");
            }
        });
        if (dumps == null || dumps.length == 0) {
            OfflineLog.offline.debug("[heapdump] no heapdump available");
            return false;
        }
        isCheckingCalled = true;
        return true;
    }

    public void upload() {
        if (!isCheckingCalled) {
            throw new IllegalArgumentException("need to check heap dump first");
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    String dumpPath = new File(OfflineLC.zdesktop_heapdump_dir.value()).getCanonicalPath();
                    File dumpDir = new File(dumpPath);
                    File[] dumps = dumpDir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.isFile() && file.getName().endsWith(".hprof");
                        }
                    });
                    long lastModTime = Long.MIN_VALUE;
                    File latestDump = null;
                    for (File f : dumps) {
                        if (f.lastModified() > lastModTime) {
                            latestDump = f;
                            lastModTime = f.lastModified();
                        }
                    }
                    boolean success = false;
                    SimpleFTP client = new SimpleFTP();
                    String uploadedFileName = "";
                    try {
                        OfflineLog.offline.debug("transferring heap dump to FTP");
                        client.connect(OfflineLC.zdesktop_heapdump_ftp.value(), 21,
                                OfflineLC.zdesktop_heapdump_ftp_user.value(),
                                OfflineLC.zdesktop_heapdump_ftp_psw.value());
                        uploadedFileName = UUID.randomUUID().toString() + "_" + latestDump.getName();
                        client.upload(uploadedFileName, latestDump);
                        success = true;
                    } catch (Exception e) {
                        OfflineLog.offline.error("Transferring heap dump to FTP error", e);
                    } finally {
                        client.disconnect();
                    }
                    if (success) {
                        try {
                            sendMsg(uploadedFileName);
                        } catch (Exception e) {
                            OfflineLog.offline.error("sending heap dump report mail error", e);
                        }
                        FileUtil.deleteDir(dumpDir);
                        OfflineLog.offline.debug("finished transferring heap dump to FTP");
                    } else {
                        OfflineLog.offline.debug("unsuccessful transferring heap dump to FTP");
                    }
                } catch (Exception e) {
                    OfflineLog.offline.error("report heap dump error", e);
                }
            }
        }.start();
    }

    private void sendMsg(String uploadedFileName) throws Exception {
        Account sendAccount = null;
        for (Account account : OfflineProvisioning.getOfflineInstance().getAllAccounts()) {
            if (!((OfflineAccount) account).isGalAccount()) {
                sendAccount = account;
                break;
            }
        }
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(sendAccount);
        MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSession());
        mm.setSentDate(new Date());
        mm.setFrom(new JavaMailInternetAddress(sendAccount.getName()));
        mm.setRecipient(RecipientType.TO, new JavaMailInternetAddress(OfflineLC.zdesktop_support_email.value()));
        mm.setSubject("Heap dump has been uploaded by " + sendAccount.getMail());
        mm.setText("heap dump is uploaded by " + sendAccount.getMail() + ", filename: " + uploadedFileName);
        mm.saveChanges();

        MailSender mailSender = mbox.getMailSender();
        mailSender.sendMimeMessage(null, mbox, mm);
    }

    private static final class SimpleFTP {
        private Socket socket = null;
        private BufferedInputStream reader = null;
        private OutputStreamWriter writer = null;

        private static final Pattern PASV_PATTERN = Pattern
                .compile("(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3})");
        private static final String LINE_SEPARATOR = "\r\n";

        private void sendLine(String str) throws IOException {
            writer.write(str);
            writer.write(LINE_SEPARATOR);
            writer.flush();
        }

        private String readLine() throws IOException {
            byte[] buf = new byte[4096];
            int len = reader.read(buf);
            String line = "";
            if (len != -1) {
                line = new String(buf);
                OfflineLog.offline.debug("ftp resp: %s", line);
            }
            return line;
        }

        public void connect(String host, int port, String user, String pass) throws IOException {
            socket = new Socket(host, port);
            reader = new BufferedInputStream(socket.getInputStream());
            writer = new OutputStreamWriter(socket.getOutputStream(), "ASCII");

            sendLine("user " + user);
            readLine();
            sendLine("pass " + pass);
            readLine();
        }

        public void upload(String destFilename, File dumpFile) throws IOException {
            if (socket == null) {
                throw new IOException("Need to connect to FTP first.");
            }
            sendLine("cwd thread_dump");
            readLine();
            sendLine("pasv");
            String line = Strings.nullToEmpty(readLine()).trim();
            OfflineLog.offline.debug("passive resp from FTP, %s", line);
            String addressAndPort = "";
            Matcher m = PASV_PATTERN.matcher(line);
            if (m.find()) {
                int start = m.start();
                int end = m.end();
                addressAndPort = line.substring(start, end);
            }
            String[] addrAndPort = addressAndPort.split(",");
            int b1 = Integer.parseInt(addrAndPort[0]);
            int b2 = Integer.parseInt(addrAndPort[1]);
            int b3 = Integer.parseInt(addrAndPort[2]);
            int b4 = Integer.parseInt(addrAndPort[3]);
            int p1 = Integer.parseInt(addrAndPort[4]);
            int p2 = Integer.parseInt(addrAndPort[5]);
            final int remotePort = (p1 << 8) | p2;

            socket = new Socket(InetAddress.getByAddress(new byte[] { (byte) b1, (byte) b2, (byte) b3, (byte) b4 }),
                    remotePort);
            sendLine("stor " + destFilename);
            readLine();
            FileInputStream input = new FileInputStream(dumpFile);
            BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
            byte[] buffer = new byte[4096 * 4];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                OfflineLog.offline.debug("transfered %d bytes to FTP", bytesRead);
            }
            output.flush();
            output.close();
            input.close();
        }

        public void disconnect() {
            if (socket == null) {
                return;
            }
            try {
                sendLine("quit");
                readLine();
            } catch (IOException e) {
                OfflineLog.offline.error("disconnecting from heap dump FTP error, %s", e);
            } finally {
                socket = null;
            }
        }
    }
}
