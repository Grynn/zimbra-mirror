// ***** BEGIN LICENSE BLOCK *****
// Version: MPL 1.1
//
// The contents of this file are subject to the Mozilla Public License
// Version 1.1 ("License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at
// http://www.zimbra.com/license
//
// Software distributed under the License is distributed on an "AS IS"
// basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
// the License for the specific language governing rights and limitations
// under the License.
//
// The Original Code is: Zimbra Collaboration Suite Server.
//
// The Initial Developer of the Original Code is Zimbra, Inc.
// Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
// All Rights Reserved.
//
// Contributor(s):
//
// ***** END LICENSE BLOCK *****

/**
 * This class is the base class for all commands. The public API to commands is via the <b>execute</b> method.
 * Due to the fact that this method will take different parameters based on the derived class' function, it is not
 * definined in this class, but is rather a pattern
 * <p>
 * When implementing a command, developers should understand that there are two phases to a command the request
 * phase and the response phase.
 * <p>
 * During the request phase, derived classes will invoke the following methods:
 *
 * <ul>
 * <li><i>beginReq</i> - Sets up the HTTP connection to the server </li>
 * <li><i>setReqHeader</i> - Called by the derived class if the request has a header element</li>
 * <li><i>beginReqBody</i> - Begins the request body </li>
 * <li><i>endReqbody</i> - Ends the request body </li>
 * <li><i>endReq</i> - End the request </li>
 * </ul>
 *
 * To begin the response phase the derived class calls the <i>handleResp</i> method. This method actually sends the
 * request to the server and waits for the response. It will then call the following methods which are implemented
 * by derived classes:
 *
 * <ul>
 * <li><i>processHeader</i> - If there is a response header, this method will be called</li>
 * <li><i>processCmd</i> - The command result</li>
 * <li><i>processFault</i> - There was an error, process it. There is a default implementation provided for this method
 * </ul>
 *
 * @author Ross Dargahi
 */
package com.zimbra.zme.client;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import org.kxml2.io.KXmlSerializer;
import org.kxml2.io.KXmlParser;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;

public abstract class Command {

    static String PR_SOAP = "soap";

    static String NS_SOAP = "http://www.w3.org/2003/05/soap-envelope";
    static String NS_ZIMBRA = "urn:zimbra";
    static String NS_ZIMBRA_ACCT = "urn:zimbraAccount";

    static String EL_ACCT = "account";
    static String EL_AUTH_REQ = "AuthRequest";
    static String EL_AUTH_TOKEN = "authToken";
    static String EL_BODY = "Body";
    static String EL_CONTEXT = "context";
    static String EL_ENV = "Envelope";
    static String EL_FAULT = "Fault";
    static String EL_HEADER = "Header";
    static String EL_PASSWD = "password";
    static String EL_USER_AGENT = "userAgent";

    static String AT_BY = "by";
    static String AT_NAME = "name";
    static String AT_VERSION = "version";

    static String NAME = "name";
    static String VERSION = "1.0";
    static String USER_AGENT = "Zimbra Mobile Edition (ZME)";

    protected HttpConnection mConn;
    protected String mUrl;
    protected XmlSerializer mSerializer;
    protected XmlPullParser mParser;

    protected Command(String url) {
        mUrl = url;
        mSerializer = new KXmlSerializer();
    }

    /**
     * Begins the request
     *
     * @throws IOException
     */
    protected void beginReq()
            throws IOException {
        mConn = (HttpConnection)Connector.open(mUrl);
        mConn.setRequestMethod(HttpConnection.POST);
        mConn.setRequestProperty("User-Agent", USER_AGENT);

        mSerializer.setOutput(mConn.openOutputStream(), "UTF-8");
        //mSerializer.setOutput(System.out, "UTF-8");
        mSerializer.startDocument("UTF-8", null);
        mSerializer.setPrefix(PR_SOAP, NS_SOAP);
        mSerializer.startTag(NS_SOAP, EL_ENV);
    }

    /**
     * Sets the request header element
     *
     * @param authToken Auth token. May be null
     * @throws IOException
     */
    protected void setReqHeader(String authToken)
            throws IOException {
        mSerializer.startTag(NS_SOAP, EL_HEADER);
        mSerializer.startTag(NS_ZIMBRA, EL_CONTEXT);

        mSerializer.startTag(null, EL_USER_AGENT);
        mSerializer.attribute(null, AT_NAME, USER_AGENT);
        mSerializer.attribute(null, AT_VERSION, VERSION);
        mSerializer.endTag(null, EL_USER_AGENT);

        if (authToken != null) {
            mSerializer.startTag(null, EL_AUTH_TOKEN);
            mSerializer.text(authToken);
            mSerializer.endTag(null, EL_AUTH_TOKEN);
        }

        mSerializer.endTag(NS_ZIMBRA, EL_CONTEXT);
        mSerializer.endTag(NS_SOAP, EL_HEADER);
    }

    protected void beginReqBody()
            throws IOException {
        mSerializer.startTag(NS_SOAP, EL_BODY);
    }

    protected void endReqBody()
            throws IOException {
        mSerializer.endTag(NS_SOAP, EL_BODY);
    }

    protected void endReq()
            throws IOException {
        mSerializer.endTag(NS_SOAP, EL_ENV);
        mSerializer.endDocument();
    }

    /**
     * Handles the response from the server. This actually includes sending the request to the server. This method
     * will call <i>processHeader</i>, <i>processCmd</i>, and <i>processFault</i> as required
     *
     * @throws IOException
     * @throws XmlPullParserException
     */
    protected void handleResp()
            throws IOException,
                   XmlPullParserException {
        /* Getting the response code will open the connection, send the request, and read the HTTP response headers.
         * The headers are stored until requested.
         */
        int rc = mConn.getResponseCode();
        if (rc != HttpConnection.HTTP_OK) {
            throw new IOException("HTTP response code: " + rc);
        }

        mParser = new KXmlParser();
        mParser.setInput(mConn.openInputStream(), "UTF-8");

        int eventType = mParser.getEventType();
        if (eventType != XmlPullParser.START_DOCUMENT)
            throw new IOException("Invalid response from server");

        mParser.next();

        String elName = mParser.getName();
        if (elName.equalsIgnoreCase(EL_HEADER))
            processHeader(mParser);

        mParser.next();
        if (elName.equalsIgnoreCase(EL_BODY)) {
            mParser.next();
            if (elName.equalsIgnoreCase(EL_FAULT))
                processFault(mParser);
            else
                processCmd(mParser);
        } else {
            //TODO Throw exception
        }

    }

    /**
     * Derived classes should implement this method to process the response header (if one is set for the command).
     * The default implementation of this method is empty
     * @param parser
     */
    protected void processHeader(XmlPullParser parser) {}

    /**
     * Derived classes should implement this method to process the response body (if one is set for the command).
     * The default implementation of this method is empty
     * @param parser
     */
    protected void processCmd(XmlPullParser parser) {}

    /**
     * Derived classes should implement this method to process the error that was returned from the server.
     * The default implementation of this method is empty
     * @param parser
     */
    protected void processFault(XmlPullParser parser) {}
    
    /**
     * Skips to the end of <i>elName</i>
     *
     * @param elName  Element to skip to the end of
     * @throws IOException
     * @throws XmlPullParserException
     */
    protected void skipToEnd(String elName)
            throws IOException,
                   XmlPullParserException {
        boolean done = false;
        while (!done) {
            mParser.next();
            int evtType = mParser.getEventType();
            if (evtType == XmlPullParser.END_TAG && mParser.getName().compareTo(elName) == 0)
                return;
        }
    }

}
