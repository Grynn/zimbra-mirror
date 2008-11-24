package com.zimbra.cs.offline.util.yab;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.util.yauth.Auth;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;

public abstract class Request extends Entity {
    protected final Session session;
    protected final List<String> params;

    protected Request(Session session) {
        this.session = session;
        params = new ArrayList<String>();
    }
    
    protected boolean isPOST() {
        return false; // Default is GET request
    }

    protected abstract String getAction();
    
    public void addParam(String name, String value) {
        params.add(name + "=" + encode(value));
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

    public Response send() throws IOException {
        return sendRequest(session.authenticate());
    }

    private Response sendRequest(Auth auth) throws IOException {
        HttpMethod method = getHttpMethod(auth);
        if (Yab.isDebug()) {
            if (isPOST()) {
                Yab.debug("Sending request: POST %s", method.getURI());
                if (session.isTrace()) {
                    Yab.debug("Request body:\n%s", Xml.toString(toXml()));
                }
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
        if (Yab.isDebug() && session.isTrace()) {
            Yab.debug("Response body:\n%s", Xml.toString(doc));
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

    protected abstract Response parseResponse(Document doc);

    private HttpMethod getHttpMethod(Auth auth) {
        String uri = Yab.BASE_URI + '/' + getAction();
        HttpMethod method = isPOST() ? new PostMethod(uri) : new GetMethod(uri);
        method.setQueryString(getQueryString(auth));
        method.addRequestHeader("Cookie", auth.getCookie());
        method.addRequestHeader("Content-Type", "application/" + session.getFormat());
        if (method instanceof PostMethod) {
            ((PostMethod) method).setRequestEntity(session.getRequestEntity(toXml()));
        }
        return method;
    }

    private String getQueryString(Auth auth) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(encode(auth.getAppId()));
        sb.append("&WSSID=").append(auth.getWSSID());
        sb.append("&format=").append(session.getFormat());
        for (String param : params) {
            sb.append('&').append(param);
        }
        return sb.toString();
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError("UTF-8 encoding not found");
        }
    }
    
    private Document toXml() {
        Document doc = session.createDocument();
        doc.appendChild(toXml(doc));
        return doc;
    }
}
