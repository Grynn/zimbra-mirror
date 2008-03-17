package com.zimbra.cs.offline.util.yab;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;

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
    protected Session session;
    protected HttpMethod method;
    protected List<NameValuePair> params;

    protected Request(Session session) {
        this.session = session;
        params = new ArrayList<NameValuePair>();
        addParam("appid", session.getAppId());
        addParam("WSSID", session.getAuth().getWSSID());
        addParam("format", session.getFormat());
    }
    
    protected boolean isPOST() {
        return false; // Default is GET request
    }

    protected abstract String getAction();

    public void addParam(String name, String value) {
        params.add(new NameValuePair(name, value));
    }
    
    public void addParams(String params) {
        for (String s : params.split("&")) {
            int i = s.indexOf('=');
            if (i == -1) {
                throw new IllegalArgumentException(
                    "Invalid parameter specification: " + params);
            }
            addParam(s.substring(0, i), s.substring(i + 1));
        }
    }

    public Element toXml(Document doc) {
        return null; // Only needed for POST requests
    }

    public Response send() throws IOException {
        if (Yab.DEBUG && isPOST()) {
            Yab.debug("Sending request:");
            Xml.print(toXml(), System.out);
        }
        HttpMethod method = getHttpMethod();
        int code = session.getHttpClient().executeMethod(method);
        if (code != 200) {
            throw new HttpException("HTTP request failed: " + code + ": " +
                                    HttpStatus.getStatusText(code));
        }
        InputStream is = method.getResponseBodyAsStream();
        Document doc = session.parseDocument(is);
        if (Yab.DEBUG) {
            Yab.debug("Received response:");
            Xml.print(doc, System.out);
        }
        return parseResponse(doc);
    }

    protected abstract Response parseResponse(Document doc);

    private HttpMethod getHttpMethod() {
        String uri = Yab.getBaseUri() + '/' + getAction();
        HttpMethod method = isPOST() ? new PostMethod(uri) : new GetMethod(uri);
        method.setQueryString(params.toArray(new NameValuePair[params.size()]));
        method.addRequestHeader("Cookie", session.getAuth().getCookie());
        method.addRequestHeader("Content-Type", "application/" + session.getFormat());
        if (isPOST()) {
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
