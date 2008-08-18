package com.zimbra.cs.offline.util.yab;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;

import com.zimbra.cs.offline.util.Xml;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Request {
    protected final Session session;
    protected final List<NameValuePair> params;

    protected Request(Session session) {
        this.session = session;
        params = new ArrayList<NameValuePair>();
        session.encodeParams(this);
    }
    
    protected boolean isPOST() {
        return false; // Default is GET request
    }

    protected abstract String getAction();

    public void addParam(String name, String value) {
        params.add(new NameValuePair(name, value));
    }
    
    public void addParams(String... params) {
        for (String param : params) {
            int i = param.indexOf('=');
            if (i == -1) {
                throw new IllegalArgumentException(
                    "Invalid parameter specification: " + param);
            }
            addParam(param.substring(0, i), param.substring(i + 1));
        }
    }

    public Element toXml(Document doc) {
        return null; // Only needed for POST requests
    }

    public Response send() throws IOException {
        HttpMethod method = getHttpMethod();
        if (Yab.isDebug()) {
            Yab.debug("Auth: %s", session.getAuth());
            if (isPOST()) {
                Yab.debug("Sending request: POST %s\n%s",
                          method.getURI(), Xml.toString(toXml()));
            } else {
                Yab.debug("Sending request: GET %s", method.getURI());
            }
        }
        int code = session.getHttpClient().executeMethod(method);
        InputStream is = method.getResponseBodyAsStream();
        if (code != 200) {
            if (Yab.isDebug()) {
                ErrorResult error = getErrorResult(method);
                if (error != null) {
                    Yab.debug("Received error response: code = %d, user = %s, debug = %s",
                              error.getCode(), error.getUserMessage(), error.getDebugMessage());
                }
            }
            throw new HttpException("HTTP request failed: " + code + ": " +
                                    HttpStatus.getStatusText(code));
        }
        Document doc = session.parseDocument(is);
        if (Yab.isDebug()) {
            Yab.debug("Received response:\n%s", Xml.toString(doc));
        }
        return parseResponse(doc);
    }

    private ErrorResult getErrorResult(HttpMethod method) throws IOException {
        InputStream is = method.getResponseBodyAsStream();
        try {
            Document doc = session.parseDocument(is);
            return ErrorResult.fromXml(doc.getDocumentElement());
        } catch (IOException e) {
            return null;
        }
    }

    private String readResponse(HttpMethod method, int maxLen) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.min(maxLen, 1024));
        InputStream is = method.getResponseBodyAsStream();
        int c;
        int off = 0;
        while (off < maxLen && (c = is.read()) != -1) {
            baos.write((byte) c);
        }
        return baos.toString("UTF8");
    }
    
    protected abstract Response parseResponse(Document doc);

    private HttpMethod getHttpMethod() {
        String uri = Yab.BASE_URI + '/' + getAction();
        HttpMethod method = isPOST() ? new PostMethod(uri) : new GetMethod(uri);
        method.setQueryString(params.toArray(new NameValuePair[params.size()]));
        method.addRequestHeader("Cookie", session.getAuth().getCookie());
        method.addRequestHeader("Content-Type", "application/" + session.getFormat());
        if (method instanceof PostMethod) {
            ((PostMethod) method).setRequestEntity(session.getRequestEntity(toXml()));
        }
        return method;
    }

    private Document toXml() {
        Document doc = session.createDocument();
        doc.appendChild(toXml(doc));
        return doc;
    }
}
