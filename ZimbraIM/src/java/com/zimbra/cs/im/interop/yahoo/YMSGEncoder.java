/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.yahoo;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * 
 */
public class YMSGEncoder implements MessageEncoder {

    private static final Set<Class> TYPES;
    static
    {
        Set<Class> types = new HashSet<Class>();
        types.add(YMSGPacket.class );
        TYPES = Collections.unmodifiableSet( types );
    }
    
    final static byte[] C080 = { (byte)0xc0, (byte)0x80 };
    
    public Set<Class> getMessageTypes() { return TYPES; }
    
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        YMSGPacket ymsg = (YMSGPacket)message;
        
        ByteBuffer buf = ByteBuffer.allocate( YMSGHeader.HEADER_LENGTH );
        buf.setAutoExpand( true ); // Enable auto-expand for easier encoding
        
        Set<Map.Entry<Integer, String>> entries = ymsg.entrySet();    
        buf.put(getHeader(ymsg, entries));
        
        for (Map.Entry<Integer, String> entry : entries) {
            putString(buf, Integer.toString(entry.getKey()));
            buf.put(C080);
            putString(buf, entry.getValue());
            buf.put(C080);
        }
        buf.flip();
        out.write(buf);
        out.flush();
    }
    
    static final void putString(ByteBuffer buf, String s) {
        byte[] bytes;
        try {
            bytes = s.getBytes("UTF-8");
            buf.put(bytes);
        } catch (UnsupportedEncodingException e) {
            // wtf?  UTF8?
            assert(false);
        }
    }
    
    byte[] getHeader(YMSGPacket packet, Set<Map.Entry<Integer, String>> entries) {
        int length = 0;
        
        for (Map.Entry<Integer, String> entry : entries) {
            length += Integer.toString(entry.getKey()).length();
            length += 2; // C080
            length += entry.getValue().length();
            length += 2; // C080
        }
        
        YMSGHeader hdr = new YMSGHeader(YMSGHeader.YMSG_VERSION,
            length, packet.getService(), packet.getStatus(),
            packet.getSessionId());
        
        return hdr.toBuf();
    }
}
