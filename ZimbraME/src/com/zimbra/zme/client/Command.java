/**
 * User: rossd
 * Date: Dec 12, 2006
 * Time: 11:01:50 PM
 */

package com.zimbra.zme.client;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;

public abstract class Command {

    protected HttpConnection mConn;
    protected String mUrl;
    protected OutputStream mOs;

    protected Command(HttpConnection connection,
                      String url) {
        mUrl = url;
    }

    protected void initRequest()
            throws IOException {
        mConn = (HttpConnection)Connector.open(mUrl);
        mConn.setRequestMethod(HttpConnection.POST);
        mConn.setRequestProperty("User-Agent", "Zimbra Mobile Edition (ZME)");

        // Getting the output stream may flush the headers
        mOs = mConn.openOutputStream();
        mOs.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">".getBytes());
    }

    protected void finishRequest()
            throws IOException {
        mOs.write("</soap:Envelope>".getBytes());

    }

    protected XmlPullParser getResponse() {
        
    }
}
