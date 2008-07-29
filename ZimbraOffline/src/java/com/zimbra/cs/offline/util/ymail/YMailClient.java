/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.ymail;

import com.yahoo.mail.YmwsPortType;
import com.yahoo.mail.Ymws;
import com.yahoo.mail.ComposeMessage;
import com.yahoo.mail.ComposeAddress;
import com.yahoo.mail.UserData;
import com.yahoo.mail.Address;
import com.yahoo.mail.ComposeMessagePart;
import com.yahoo.mail.MidRequest;
import com.yahoo.mail.Message;
import com.yahoo.mail.ErrorCode;
import com.zimbra.cs.offline.util.yauth.Auth;
import com.zimbra.cs.offline.util.Xml;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.Binding;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.SharedInputStream;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimePart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Header;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public final class YMailClient {
    private final Auth auth;
    private final YmwsPortType stub;
    private int maxInlineDataSize = MAX_INLINE_DATA_SIZE;
    private PrintStream traceOut;

    private static DatatypeFactory dataTypeFactory;

    private static final Logger LOG = Logger.getLogger(YMailClient.class);
    
    private static final String SOAP_URL =
        "http://mail.yahooapis.com/ws/mail/v1.1/soap";
    
    private static final String UPLOAD_URL =
        "http://mail.yahooapis.com/ya/upload";

    // Maximum size of text/* attachment to include inline.
    private static final int MAX_INLINE_DATA_SIZE = 1024;

    public YMailClient(Auth auth) {
        this.auth = auth;
        this.stub = getStub(auth);
    }

    public void close() throws IOException {
        ((Closeable) stub).close();
    }

    public void setMaxInlineDataSize(int size) {
        maxInlineDataSize = size;
    }
    
    public UserData getUserData() throws IOException {
        try {
            return stub.getUserData();
        } catch (WebServiceException e) {
            throw requestFailed(e);
        }
    }
    
    public String sendMessage(MimeMessage mm) throws IOException {
        try {
            ComposeMessage cm = getComposeMessage(mm);
            return stub.sendMessage(cm, true);
        } catch (Exception e) {
            throw requestFailed(e);
        }
    }

    public Message getMessage(String fid, String... mids)
        throws IOException {
        List<MidRequest> mrs = new ArrayList<MidRequest>(mids.length);
        for (String mid : mids) {
            MidRequest mr = new MidRequest();
            mr.setMid(mid);
            mrs.add(mr);
        }
        Holder<List<Message>> msgs = new Holder<List<Message>>();
        Holder<List<ErrorCode>> codes = new Holder<List<ErrorCode>>();
        try {
            stub.getMessage(
                null,       // Long truncateAt
                fid,        // String fid
                null,       // List<String> mid
                mrs,        // List<MidRequest> message
                null,       // String charsetHint
                null,       // Holder<BigInteger> total
                null,       // Holder<FolderData> folder
                msgs,       // Holder<List<Message>> message0
                null,       // Holder<List<Header>> header
                codes);     // Holder<List<ErrorCode>> code
        } catch (Exception e) {
            throw requestFailed(e);
        }
        if (codes.value != null) {
            throw new IOException(
                "getMessage failed: " + codes.value.get(0).getCode());    
        }
        if (msgs.value == null || msgs.value.isEmpty()) {
            return null;
        }
        return msgs.value.get(0);
    }

    public void enableTrace(PrintStream out) {
        traceOut = out;
        Binding binding = ((BindingProvider) stub).getBinding();
        List<Handler> handlers = binding.getHandlerChain();
        handlers.add(new LoggingHandler());
        binding.setHandlerChain(handlers);
    }

    private ComposeMessage getComposeMessage(MimeMessage mm)
        throws MessagingException, IOException {
        ComposeMessage cm = new ComposeMessage();
        cm.setSubject(mm.getSubject());
        List<Address> from = getAddresses(mm.getFrom());
        if (from.isEmpty()) {
            throw new IllegalArgumentException("Missing 'From' header field");
        }
        cm.setFrom(from.get(0));
        List<Address> replyTo = getAddresses(mm.getReplyTo());
        if (!replyTo.isEmpty()) {
            cm.setReplyto(replyTo.get(0));
        }
        cm.getTo().addAll(getComposeAddresses(mm.getRecipients(RecipientType.TO)));
        cm.getCc().addAll(getComposeAddresses(mm.getRecipients(RecipientType.CC)));
        cm.getBcc().addAll(getComposeAddresses(mm.getRecipients(RecipientType.BCC)));
        Date date = mm.getSentDate();
        if (date == null) {
            date = new Date();
        }
        cm.setDate(getXmlGregorianCalendar(date));
        String[] mailer = mm.getHeader("X-Mailer");
        if (mailer != null) {
            cm.setMailer(mailer[0]);
        }
        ComposeMessagePart cmp = new ComposeMessagePart();
        setAttributes(cmp, mm);
        Object content = mm.getContent();
        if (content instanceof String) {
            cmp.setData((String) content);
        } else if (content instanceof Multipart) {
            addSubparts(cmp, (Multipart) content);
        } else {
            throw new IllegalArgumentException(
                "Unsupported content type: " + mm.getContentType());
        }
        cm.setBody(cmp);
        return cm;
    }

    private void addSubparts(ComposeMessagePart cmp, Multipart mp)
        throws MessagingException, IOException {
        for (int i = 0; i < mp.getCount(); i++) {
            cmp.getSubparts().add(getSubpart((MimeBodyPart) mp.getBodyPart(i)));
        }
    }

    private ComposeMessagePart getSubpart(MimeBodyPart mbp)
        throws MessagingException, IOException {
        ComposeMessagePart cmp = new ComposeMessagePart();
        setAttributes(cmp, mbp);
        ContentType ct = new ContentType(mbp.getContentType());
        String ptype = ct.getPrimaryType();
        if (ptype.equals("text") && !mustAttach(mbp)) {
            cmp.setData((String) mbp.getContent());
        } else if (ptype.equals("multipart")) {
            addSubparts(cmp, (Multipart) mbp.getContent());
        } else {
            cmp.setAttachment("upload://" + uploadAttachment(mbp));
        }
        return cmp;
    }

    private boolean mustAttach(MimeBodyPart mbp)
        throws MessagingException, IOException {
        int size = getContentSize(mbp);
        return size < 0 || size > maxInlineDataSize;
    }
    
    private static int getContentSize(MimeBodyPart mbp)
        throws MessagingException, IOException {
        InputStream is = mbp.getRawInputStream();
        return (is instanceof SharedInputStream ||
                is instanceof ByteArrayInputStream) ? is.available() : -1;
    }
    
    private static void setAttributes(ComposeMessagePart cmp, MimePart mp)
        throws MessagingException {
        ContentType ct = new ContentType(mp.getContentType());
        cmp.setType(ct.getPrimaryType());
        cmp.setSubtype(ct.getSubType());
        cmp.setCharset(ct.getParameter("charset"));
        cmp.setEncoding(mp.getEncoding());
        cmp.setFilename(mp.getFileName());
        cmp.setContentid(mp.getContentID());
        cmp.setDisposition(mp.getDisposition());
    }
    
    private static List<Address> getAddresses(javax.mail.Address[] addrs) {
        if (addrs == null) {
            return Collections.emptyList();
        }
        List<Address> yaddrs = new ArrayList<Address>(addrs.length);
        for (javax.mail.Address addr : addrs) {
            yaddrs.add(newAddress((InternetAddress) addr));
        }
        return yaddrs;
    }

    private static List<ComposeAddress> getComposeAddresses(javax.mail.Address[] addrs) {
        if (addrs == null) {
            return Collections.emptyList();
        }
        List<ComposeAddress> caddrs = new ArrayList<ComposeAddress>(addrs.length);
        for (javax.mail.Address addr : addrs) {
            caddrs.add(newComposeAddress((InternetAddress) addr));
        }
        return caddrs;
    }

    private static Address newAddress(InternetAddress inaddr) {
        Address addr = new Address();
        addr.setName(inaddr.getPersonal());
        addr.setEmail(inaddr.getAddress());
        return addr;
    }

    private static ComposeAddress newComposeAddress(InternetAddress inaddr) {
        ComposeAddress addr = new ComposeAddress();
        addr.setName(inaddr.getPersonal());
        addr.setEmail(inaddr.getAddress());
        return addr;
    }

    public String uploadAttachment(MimeBodyPart mbp) throws IOException {
        Part part;
        try {
            part = getPart(mbp);
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Mime content error", e);
        }
        PostMethod post = new PostMethod(UPLOAD_URL);
        post.setFollowRedirects(false);
        post.setQueryString(getQueryString(auth) + "&resulturl=http://upload");
        post.setRequestHeader("Cookie", auth.getCookie());
        post.setRequestEntity(
            new MultipartRequestEntity(new Part[] { part }, post.getParams()));
        HttpClient client = new HttpClient();
        int status = client.executeMethod(post);
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
        if (traceOut != null) {
            traceOut.println(String.format(
                "Uploaded YMail attachment: id = %s, filesize = %s, mimetype = %s",
                id, params.get("filesize"), params.get("mimetype")));
        }
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

    private static XMLGregorianCalendar getXmlGregorianCalendar(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        return getDatatypeFactory().newXMLGregorianCalendar(gc);
    }

    private static synchronized DatatypeFactory getDatatypeFactory() {
        if (dataTypeFactory == null) {
            try {
                dataTypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new IllegalStateException("Configuration error", e);
            }
        }
        return dataTypeFactory;
    }

    private static YmwsPortType getStub(Auth auth) {
        Ymws service = new Ymws();
        YmwsPortType stub = service.getYmws();
        Map<String, Object> rc = ((BindingProvider) stub).getRequestContext();
        String url = SOAP_URL + "?" + getQueryString(auth);
        rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        rc.put(MessageContext.HTTP_REQUEST_HEADERS, getHeaders(auth));
        return stub;
    }

    private static String getQueryString(Auth auth) {
        try {
            return "appid=" + URLEncoder.encode(auth.getAppId(), "UTF-8") +
                   "&WSSID=" + URLEncoder.encode(auth.getWSSID(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("URL encoding error", e);
        }
    }

    private static Map<String, List<String>> getHeaders(Auth auth) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Cookie", Arrays.asList(auth.getCookie()));
        return headers;
    }

    private static IOException requestFailed(Throwable cause) {
        IOException ioe = new IOException("Request failed");
        ioe.initCause(cause);
        return ioe;
    }

    private class LoggingHandler implements SOAPHandler<SOAPMessageContext> {
        public boolean handleMessage(SOAPMessageContext smc) {
            return logMessage(smc);
        }

        public boolean handleFault(SOAPMessageContext smc) {
            return logMessage(smc);
        }

        public void close(MessageContext mc) {}

        public Set<QName> getHeaders() { return null; }

        private boolean logMessage(SOAPMessageContext smc) {
            if (traceOut != null) {
                boolean outbound = (Boolean)
                    smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (outbound) {
                    traceOut.println("*** YMail SOAP Request ***");
                } else {
                    traceOut.println("*** YMail SOAP Response ***");
                }
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    smc.getMessage().writeTo(baos);
                    DocumentBuilder db = Xml.newDocumentBuilder();
                    Document doc = db.parse(new ByteArrayInputStream(baos.toByteArray()));
                    Xml.print(doc, traceOut);
                    traceOut.println();
                } catch (Exception e) {
                    traceOut.println("Exception in handler: " + e);
                }
            }
            return true;
        }
    }
}
