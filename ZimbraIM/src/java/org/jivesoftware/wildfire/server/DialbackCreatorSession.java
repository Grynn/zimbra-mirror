/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package org.jivesoftware.wildfire.server;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.Connection;
import org.jivesoftware.wildfire.PacketException;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.wildfire.StreamID;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.StreamError;

/**
 * 
 */
public class DialbackCreatorSession extends Session {
    
    protected ServerDialback mMethod;

    /**
     * @param serverName
     * @param connection
     * @param streamID
     */
    public DialbackCreatorSession(String serverName, Connection connection, StreamID streamID, 
        ServerDialback method) {
        super(serverName, connection, streamID);
        mMethod = method;
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.Session#getAvailableStreamFeatures()
     */
    @Override
    public String getAvailableStreamFeatures() {
        assert(false);
        return null;
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.ChannelHandler#process(org.xmpp.packet.Packet)
     */
    public void process(Packet packet) throws UnauthorizedException, PacketException {
        assert(false);
    }
    
    public Session processSecondElement(Element doc) {
        try {
            if ("db".equals(doc.getNamespacePrefix()) && "result".equals(doc.getName())) {
                if (mMethod.validateRemoteDomain(doc, this.getStreamID())) {
                    String hostname = doc.attributeValue("from");
                    String recipient = doc.attributeValue("to");
                    // Create a server Session for the remote server
                    IncomingServerSession session = sessionManager.
                            createIncomingServerSession(getConnection(), recipient, getStreamID());
                    // Set the first validated domain as the address of the session
                    session.setAddress(new JID(null, hostname, null));
                    // Add the validated domain as a valid domain
                    session.addValidatedDomain(hostname);
                    // Set the domain or subdomain of the local server used when
                    // validating the session
                    session.setLocalDomain(recipient);
                    return session;
                }
            } else if ("db".equals(doc.getNamespacePrefix()) && "verify".equals(doc.getName())) {
                // When acting as an Authoritative Server the Receiving Server will send a
                // db:verify packet for verifying a key that was previously sent by this
                // server when acting as the Originating Server
                ServerDialback.verifyReceivedKey(doc, getConnection());
                // Close the underlying connection
                getConnection().close();
                String verifyFROM = doc.attributeValue("from");
                String id = doc.attributeValue("id");
                Log.debug("AS - Connection closed for host: " + verifyFROM + " id: " + id);
                return null;
            } else {
                // The remote server sent an invalid/unknown packet
                getConnection().deliverRawText(
                        new StreamError(StreamError.Condition.invalid_xml).toXML());
                // Close the underlying connection
                getConnection().close();
                return null;
            }
        }
        catch (Exception e) {
            Log.error("An error occured while creating a server session", e);
            // Close the underlying connection
            getConnection().close();
            return null;
        }
        // Include the invalid-namespace stream error condition in the response
        this.getConnection().deliverRawText(
            new StreamError(StreamError.Condition.invalid_namespace).toXML());
        // Close the underlying connection
        this.getConnection().close();
        return null;
    }
    
    public boolean handleStreamHeader(Element streamElt) {
        Log.debug("DialbackCreatorSession.handleStreamHeader: "+streamElt.asXML());
        Namespace ns = streamElt.getNamespaceForPrefix("db");
        if (ns != null && ns.getURI().equals("jabber:server:dialback")) {
            StringBuilder sb;
            
            sb = new StringBuilder();
            sb.append("<stream:stream");
            sb.append(" xmlns:stream=\"http://etherx.jabber.org/streams\"");
            sb.append(" xmlns=\"jabber:server\" xmlns:db=\"jabber:server:dialback\"");
            sb.append(" id=\"");
            sb.append(this.getStreamID().toString());
            sb.append("\">");
            this.getConnection().deliverRawText(sb.toString());
            return true;
        } else {
            // Include the invalid-namespace stream error condition in the response
            this.getConnection().deliverRawText(
                new StreamError(StreamError.Condition.invalid_namespace).toXML());
            // Close the underlying connection
            this.getConnection().close();
            return false;
        }
    }
}
