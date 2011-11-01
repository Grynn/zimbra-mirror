/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.ymail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.internet.SharedInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.ContainerException;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapHttpTransport;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.util.Log;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.util.yauth.Auth;

public class YMailClient {
    private final Auth auth;
    private final SoapHttpTransport transport;

    private static final Log LOG = OfflineLog.ymail;

    private static final String BASE_URL = "http://mail.yahooapis.com";
    private static final String SOAP_URL = BASE_URL + "/ws/mail/v1.1/soap";
    private static final String UPLOAD_URL = BASE_URL + "/ya/upload";

    // Maximum size of text attachment to include inline.
    private static final int MAX_INLINE_DATA_SIZE = 64*1024;

    private static final String ENCODING_BASE64 = "base64";
    private static final String ENCODING_BINARY = "binary";
    private static final String ENCODING_7BIT = "7bit";
    private static final String ENCODING_QUOTABLE_PRINTABLE = "quoted-printable";
    private static final String ENCODING_8BIT = "8bit";

    private static final String CONTENT_TYPE_RFC822 = "message/rfc822";

    private static final Namespace NS = Namespace.get("urn:yahoo:ymws");
    private static final QName GET_USER_DATA = new QName("GetUserData", NS);
    private static final QName SEND_MESSAGE = new QName("SendMessage", NS);

    public YMailClient(Auth auth) {
        this.auth = auth;
        transport = getTransport(auth);
    }

    private static SoapHttpTransport getTransport(Auth auth) {
        SoapHttpTransport transport =
            new SoapHttpTransport(SOAP_URL + "?" + getQueryString(auth)) {
            public boolean generateContextHeader() {
                return false;
            }
        }; 
        transport.setUserAgent(OfflineLC.zdesktop_name.value(), OfflineLC.getFullVersion());
        transport.setRequestProtocol(SoapProtocol.Soap11);
        transport.setResponseProtocol(SoapProtocol.Soap11);
        transport.setTimeout(OfflineLC.http_connection_timeout.intValue());
        transport.setRetryCount(1);
        transport.getCustomHeaders().put("Cookie", auth.getCookie());
        //transport.setPrettyPrint(true);
        return transport;
    }

    private static String getQueryString(Auth auth) {
        try {
            return "appid=" + URLEncoder.encode(auth.getAppId(), "UTF-8") +
                "&WSSID=" + URLEncoder.encode(auth.getWSSID(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("URL encoding error", e);
        }
    }

    public void test() throws IOException, ContainerException, ServiceException {
        Element req = new Element.XMLElement(AdminConstants.PING_REQUEST);
        transport.invokeWithoutSession(req.detach());
    }

    public void setTrace(boolean enabled) {
        if (enabled) {
            transport.setDebugListener(
                new SoapTransport.DebugListener() {
                    public void receiveSoapMessage(Element e) {
                        LOG.debug("Received response:\n%s", e.prettyPrint());
                    }
                    public void sendSoapMessage(Element e) {
                        LOG.debug("Sending request:\n%s", e.prettyPrint());
                    }
                });
        } else {
            transport.setDebugListener(null);
        }
    }

    public void close() {
        transport.shutdown();
    }

    public boolean isBizMail(String email) throws ServiceException, IOException {
        try {
            Element req = new Element.XMLElement(GET_USER_DATA);
            Element res = transport.invokeWithoutSession(req.detach());
            Element mbs = res.getElement("data").getOptionalElement("otherYahooMboxes");
            if (mbs != null) {
                Iterator<Element> it = mbs.elementIterator("yMbox");
                while (it.hasNext()) {
                    Element mb = it.next();
                    if (mb.getAttribute("email").equalsIgnoreCase(email)) {
                        return mb.getAttributeBool("isBizmail");
                    }
                }
            }
            return false;
        } catch (Exception e) {
            failed("GetUserData", e);
            return false;
        }
    }

    public String sendMessage(MimeMessage mm, boolean saveCopy) throws IOException {
        Element sm = new Element.XMLElement(SEND_MESSAGE);
        try {
            sm.addElement(getComposeMessage(mm));
            addElement(sm, "savecopy", String.valueOf(saveCopy));
        } catch (MessagingException e) {
            throw new YMailException("Unable to create request", e);
        }
        try {
            Element res = transport.invokeWithoutSession(sm);
            Element mid = res.getOptionalElement("mid");
            return mid != null ? mid.getText() : null;
        } catch (Exception e) {
            failed("SendMessage", e);
            return null;
        }
    }

    private Element getComposeMessage(MimeMessage mm)
        throws MessagingException, IOException {
        Element cm = new Element.XMLElement("message");
        addElement(cm, "subject", Mime.getSubject(mm));
        Address[] from = mm.getFrom();
        if (from == null || from.length == 0) {
            throw new IllegalArgumentException("Missing 'From' header field");
        }
        cm.addElement(getAddress("from", (InternetAddress) from[0]));
        addAddresses(cm, "replyto", mm.getReplyTo());
        addAddresses(cm, "to", mm.getRecipients(MimeMessage.RecipientType.TO));
        addAddresses(cm, "cc", mm.getRecipients(MimeMessage.RecipientType.CC));
        addAddresses(cm, "bcc", mm.getRecipients(MimeMessage.RecipientType.BCC));
        Date date = mm.getSentDate();
        if (date == null) {
            date = new Date();
        }
        addElement(cm, "date", DateUtil.toISO8601(date));
        String[] mailer = mm.getHeader("X-Mailer");
        if (mailer != null && mailer.length > 0) {
            addElement(cm, "mailer", mailer[0]);
        }
        Element cmp = new Element.XMLElement("body");
        addAttributes(cmp, mm);
        Object content = mm.getContent();
        if (content instanceof String) {
            addElement(cmp, "data", (String) content);
        } else if (content instanceof Multipart) {
            addSubparts(cmp, (Multipart) content);
        } else {
            throw new IllegalArgumentException(
                "Unsupported content type: " + mm.getContentType());
        }
        cm.addElement(cmp);
        return cm;
    }

    private void addAddresses(Element e, String name, Address[] addrs) {
        if (addrs != null) {
            for (Address addr : addrs) {
                e.addElement(getAddress(name, (InternetAddress) addr));
            }
        }
    }

    private static Element getAddress(String name, InternetAddress addr) {
        Element e = new Element.XMLElement(name);
        addElement(e, "name", addr.getPersonal());
        addElement(e, "email", addr.getAddress());
        return e;
    }

    private void addSubparts(Element cmp, Multipart mp)
        throws MessagingException, IOException {
        for (int i = 0; i < mp.getCount(); i++) {
            cmp.addElement(getSubpart((MimeBodyPart) mp.getBodyPart(i)));
        }
    }

    private Element getSubpart(MimeBodyPart mbp)
        throws MessagingException, IOException {
        Element cmp = new Element.XMLElement("subparts");
        addAttributes(cmp, mbp);
        String type = mbp.getContentType();
        boolean isAttachment = (mbp.getDisposition() != null) && (mbp.getDisposition().equals("attachment"));
        if (!isAttachment && isAsciiText(mbp)) {
            addElement(cmp, "data", getContentString(mbp));
        } else if (type.startsWith("multipart/")) {
            addSubparts(cmp, (Multipart) mbp.getContent());
        } else {
            cmp.addAttribute("attachment", "upload://" + uploadAttachment(mbp));
        }
        return cmp;
    }

    private static boolean isAsciiText(MimeBodyPart mbp)
        throws IOException, MessagingException {
        String type = mbp.getContentType();
        if (type == null || !type.startsWith("text/")) return false;
        Object content = mbp.getContent();
        if (content instanceof String) return true;
        String encoding = mbp.getEncoding();
        return ENCODING_7BIT.equals(encoding) ||
            ENCODING_QUOTABLE_PRINTABLE.equals(encoding);
    }

    private static String getContentString(MimeBodyPart mbp)
        throws IOException, MessagingException {
        Object content = mbp.getContent();
        if (content instanceof String) {
            return (String) content;
        }
        StringBuilder sb = new StringBuilder();
        InputStream is = mbp.getInputStream();
        int c;
        while ((c = is.read()) != -1) {
            sb.append((char) (c & 0x7f));
        }
        return sb.toString();
    }

    private static int getContentSize(MimeBodyPart mbp)
        throws MessagingException, IOException {
        InputStream is = mbp.getRawInputStream();
        return (is instanceof SharedInputStream ||
            is instanceof ByteArrayInputStream) ? is.available() : -1;
    }

    private static void addAttributes(Element cmp, MimePart mp)
        throws MessagingException {
        ContentType ct = new ContentType(mp.getContentType());
        addAttribute(cmp, "type", ct.getPrimaryType());
        addAttribute(cmp, "subtype", ct.getSubType());
        addAttribute(cmp, "charset", ct.getParameter("charset"));
        addAttribute(cmp, "encoding", mp.getEncoding());
        addAttribute(cmp, "filename", mp.getFileName());
        addAttribute(cmp, "contentid", mp.getContentID());
        addAttribute(cmp, "disposition", mp.getDisposition());
        if ("attachment".equals(mp.getDisposition()) && (mp.getContentType() != null) && (mp.getContentType().startsWith("text/plain"))) {
            addAttribute(cmp, "type", "application");	//if block is a workaround for bug #53209
            addAttribute(cmp, "subtype", "octet-stream");
        }
    }

    private static void addAttribute(Element e, String name, String value) {
        if (value != null) {
            e.addAttribute(name, value);
        }
    }
    public String uploadAttachment(MimeBodyPart mbp) throws IOException {
        File tmpFile = null;
        Part part;
        try {
            if (ENCODING_BASE64.equalsIgnoreCase(mbp.getEncoding()) ||
                CONTENT_TYPE_RFC822.equalsIgnoreCase(mbp.getContentType())) {
                InputStream is = mbp.getInputStream();
                try {
                    tmpFile = createTempFile(mbp.getInputStream());
                } finally {
                    is.close();
                }
                String name = mbp.getFileName();
                if (name == null) name = "attachment";
                part = new FilePart(name, name, tmpFile, mbp.getContentType(), null);
            } else {
                part = getPart(mbp);
            }
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Mime content error", e);
        }
        PostMethod post = new PostMethod(UPLOAD_URL);
        post.setFollowRedirects(false);
        post.setQueryString(getQueryString(auth) + "&resulturl=http://upload");
        post.setRequestHeader("Cookie", auth.getCookie());
        post.setRequestEntity(
            new MultipartRequestEntity(new Part[] { part }, post.getParams()));
        int status = HttpClientUtil.executeMethod(post);
        if (tmpFile != null) {
            tmpFile.delete();
        }
        if (status != 302) {
            throw new IOException("Upload failed: " + post.getStatusText());
        }
        Header location = post.getResponseHeader("Location");
        if (location == null) {
            throw new IOException(
                "Invalid upload response (missing redirect location");
        }
        Map<String, String> params = parseParams(location.getValue());
        String id = params.get("diskfilename");
        if (id == null) {
            String code = params.get("errorcode");
            if (code != null) {
                throw new IOException("Upload failed (error = " + code + ")");
            }
            throw new IOException("Upload failed (unknown error)");
        }
        LOG.debug("Uploaded YMail attachment: id = %s, filesize = %s, mimetype = %s",
            id, params.get("filesize"), params.get("mimetype"));
        return id;
    }

    private static Map<String, String> parseParams(String query) {
        Map<String, String> params = new HashMap<String, String>();
        for (String s : query.split("&")) {
            int i = s.indexOf('=');
            if (i != -1) {
                params.put(s.substring(0, i).toLowerCase(), s.substring(i + 1));
            }
        }
        return params;
    }

    private static Part getPart(MimeBodyPart mbp)
        throws MessagingException, IOException {
        System.out.printf("getPart: name = %s, type = %s, encoding = %s, disposition = %s\n",
            mbp.getFileName(), mbp.getContentType(), mbp.getEncoding(), mbp.getDisposition());
        final int size = getContentSize(mbp);
        if (size == -1) {
            throw new IllegalArgumentException(
                "Unable to determine raw content size for attachment");
        }
        final InputStream is = mbp.getRawInputStream();
        final String name = mbp.getFileName();
        PartSource ps = new PartSource() {
            public String getFileName() {
                return name != null ? name : "attachment";
            }
            public long getLength() { return size; }
            public InputStream createInputStream() { return is; }
        };
        FilePart fp = new FilePart(
            ps.getFileName(), ps, mbp.getContentType(), null);
        String encoding = mbp.getEncoding();
        if (encoding != null) {
            fp.setTransferEncoding(encoding);
        }
        return fp;
    }

    private static File createTempFile(InputStream is) throws IOException {
        File file = File.createTempFile("ymail", "dat");
        file.deleteOnExit();
        OutputStream os = new FileOutputStream(file);
        try {
            byte[] b = new byte[8192];
            int len;
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } finally {
            os.close();
        }
        return file;
    }


    private void failed(String name, Exception e) throws IOException {
        if (e instanceof SoapFaultException) {
            SoapFaultException sfe = (SoapFaultException) e;
            Element fault = sfe.getFault();
            String code = getText(fault, "faultcode");
            String msg = getText(fault, "faultstring");
            if (msg == null) {
                msg = code != null ? code : "Unknown error";
            }
            YMailException yme = new YMailException(name + " request failed: " + msg, e);
            if (code != null) {
                yme.setError(YMailError.fromFaultCode(code));
            }
            throw yme;
        }
        // Otherwise, assume its a protocol exception
        IOException ioe = new IOException(name + " could not be sent");
        ioe.initCause(e);
        throw ioe;
    }

    private String getText(Element e, String name) {
        if (e != null) {
            Element child = e.getOptionalElement(name);
            if (child != null) {
                return child.getTextTrim();
            }
        }
        return null;
    }

    private static Element addElement(Element e, String name, String value) {
        if (value != null) {
            Element child = e.addElement(name);
            child.setText(value);
            return child;
        }
        return null;
    }
}
