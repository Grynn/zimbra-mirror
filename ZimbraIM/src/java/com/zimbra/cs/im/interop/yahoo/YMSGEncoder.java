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
package com.zimbra.cs.im.interop.yahoo;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.zimbra.common.util.Pair;

/**
 * 
 */
public class YMSGEncoder implements MessageEncoder {

    private static final byte[] C080 = { (byte)0xc0, (byte)0x80 };
    private static final Set<Class> TYPES;
    
    static
    {
        Set<Class> types = new HashSet<Class>();
        types.add(YMSGPacket.class );
        TYPES = Collections.unmodifiableSet( types );
    }
    
    private static final byte[] getHeader(YMSGPacket packet, int payloadLen, Set<Map.Entry<Integer, List<String>>> entries) {
        YMSGHeader hdr = new YMSGHeader(YMSGHeader.YMSG_VERSION,
            payloadLen, packet.getService(), packet.getStatus(),
            packet.getSessionId());
        
        return hdr.toBuf();
    }
    
    private static final int putString(ByteBuffer buf, String s) {
        try {
            byte[] bytes = s.getBytes("UTF-8");
            buf.put(bytes);
            return bytes.length;
        } catch (UnsupportedEncodingException e) {
            // wtf?  UTF8?
            e.printStackTrace();
            return 0;
        }
    }
    
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        YMSGPacket packet = (YMSGPacket)message;
        
        ByteBuffer buf = ByteBuffer.allocate( YMSGHeader.HEADER_LENGTH );
        buf.setAutoExpand( true ); // Enable auto-expand for easier encoding
        buf.skip(YMSGHeader.getByteLength());
        
        Set<Map.Entry<Integer, List<String>>> entries = packet.entrySet();
        int payloadLen = 0;
        
        for (Pair<Integer, String> p : packet.getOriginalStrings()) {
            if (p.getSecond() != null) {
                payloadLen += putString(buf, Integer.toString(p.getFirst()));
                buf.put(C080); payloadLen+=2;
                payloadLen += putString(buf, p.getSecond());
                buf.put(C080); payloadLen+=2;
            }
        }

        // skip to the beginning, write the header, then skip back
        // to the current position...
        int endPos = buf.position();
        buf.position(0);
        buf.put(getHeader(packet, payloadLen, entries));
        buf.position(endPos);
        
        buf.flip();
        out.write(buf);
        out.flush();
    }
    
    public Set<Class> getMessageTypes() { return TYPES; }
}
