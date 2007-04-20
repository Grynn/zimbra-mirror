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

import java.io.IOException;
import java.util.Arrays;
import java.util.Formatter;

/**
 * 
 * Packet Header:
 * 0x00-0x03  Y M S G
 * 0x04-0x05  VERSION    (INT)
 * 0x06-0x07  PADDING (0x0)
 * 0x08-0x09  LENGTH     (SHORT)
 * 0x0A-0x0B  SERVICE-ID (SHORT)
 * 0x0C-0x0F  STATUS     (INT)
 * 0x10-0x13  SESSION-ID (INT)
 * 
 */
class YMSGHeader {
    // you'll want to use BufUtils.uInt() and BufUtils.uShort() when 
    // you compare against these!
    public final int version;
    public final int length; // this does **NOT** include the header length
    public final int service;
    public final long status;
    public final long sessionId;
    
    public static final YMSGHeader parse(byte[] buf) throws IOException {
        if (buf.length < HEADER_LENGTH) 
            throw new IllegalArgumentException("Buffer too short for YMSG header");
        
        long ymsg = YMSGBufUtils.readUInt(buf, OFFSET_YMSG);
        if (ymsg != YMSG) {
            throw new IOException("Invalid packet header: "+ YMSGBufUtils.readCharString(buf, OFFSET_YMSG, 4));
        }

        return new YMSGHeader(
            YMSGBufUtils.readUShort(buf, OFFSET_VERSION),
            YMSGBufUtils.readUShort(buf, OFFSET_LENGTH),
            YMSGBufUtils.readUShort(buf, OFFSET_SERVICE_ID),
            YMSGBufUtils.readUInt(buf, OFFSET_STATUS),
            YMSGBufUtils.readUInt(buf, OFFSET_SESSION_ID)
        );
    }
    
    public byte[] toBuf() {
        byte[] toRet = new byte[HEADER_LENGTH];
        YMSGBufUtils.writeInt(YMSG, toRet, OFFSET_YMSG);
        YMSGBufUtils.writeShort(version, toRet, OFFSET_VERSION);
        YMSGBufUtils.writeShort(0, toRet, OFFSET_PADDING);
        YMSGBufUtils.writeShort(length, toRet, OFFSET_LENGTH);
        YMSGBufUtils.writeShort(service, toRet, OFFSET_SERVICE_ID);
        YMSGBufUtils.writeInt(status, toRet, OFFSET_STATUS);
        YMSGBufUtils.writeInt(sessionId, toRet, OFFSET_SESSION_ID);
        
        return toRet;
    }
    
    /**
     * @return the length in bytes of the header written as a network packet
     */
    public static final int getByteLength() {
        return 20;
    }
    
    public static final YMSGHeader create(int version, int length, int service, long status, long sessionId) {
        return new YMSGHeader(version, length, service, status, sessionId);
    }
    
    
    public static final long YMSG = 'Y' << 24 | 'M' << 16 | 'S' << 8 | 'G';
    public static final int OFFSET_YMSG        = 0;
    public static final int OFFSET_VERSION     = 0x4;
    public static final int OFFSET_PADDING     = 0x6;
    public static final int OFFSET_LENGTH      = 0x8;
    public static final int OFFSET_SERVICE_ID  = 0x0a;
    public static final int OFFSET_STATUS      = 0x0C;
    public static final int OFFSET_SESSION_ID  = 0x10;
    public static final int HEADER_LENGTH = 0x14;
    
    public static final int YMSG_VERSION = 12;
    
    YMSGHeader(int version, int length, int service, long status, long sessionId) {
        this.version = version;
        this.length = length;
        this.service = service;
        this.status = status;
        this.sessionId = sessionId;
    }
    
    public String toString() {
        StringBuilder t= new StringBuilder("HDR[");
        t.append("ver=").append(YMSGBufUtils.toHex(version)).append(", ");
        t.append("len=").append(length).append(", ");
        t.append("svc=").append(YMSGBufUtils.toHex(service)).append(", ");
        t.append("sta=").append(YMSGBufUtils.toHex(status)).append(", ");
        t.append("ses=").append(YMSGBufUtils.toHex(sessionId)).append("]");
        return t.toString();
    }
    
    public static void main(String[] args) {
        
        //
        //0x59 0x4d 0x53 0x47 0x0 0x0 0x0 0x0 0x3 0xcc 0x0 0x55 0x0 0x0 0x0 0x5 0x6b 0x9c 0xbf 0x11        
        
        byte[] TEST_PACKET = new byte[] {
            'Y', 'M', 'S', 'G', 
            0x00, 0x00, // vers
            0x00, 0x00, // pad
            (byte)0x03, (byte)0xcc,   // len
            0x0, 0x55, // service
            (byte)0x0, 0x0, (byte)0x0, 5, // status
            (byte)0x6b, (byte)0x9c, (byte)0xbf, (byte)0x11 // session 
        };
//        byte[] TEST_PACKET = new byte[] {
//            'Y', 'M', 'S', 'G', 
//            0x00, 0x0a, // vers
//            0x00, 0x00, // pad
//            (byte)0xF0, 0x0D,   // len
//            0x12, 0x34, // service
//            (byte)0xFF, 0x9, (byte)0xFE, 1, // status
//            0x12, 0x34, 0x56, 0x78 // session 
//        };
        
        StringBuilder sb = new StringBuilder("Buffer: ");
        
        for (int i = 0; i < TEST_PACKET.length; i++) {
            sb.append(new Formatter().format("%02x,", TEST_PACKET[i]));
        }
        System.out.println(sb.toString());
        try {
            YMSGHeader hdr = YMSGHeader.parse(TEST_PACKET);
//            assert(hdr.version == BufUtils.uShort(0x0A));
//            assert(hdr.length == BufUtils.uShort(0xF00D));
//            assert(hdr.service == BufUtils.uShort(0x1234));
//            assert(hdr.status == BufUtils.uInt(0xff09fe01));
//            assert(hdr.sessionId == BufUtils.uInt(0x12345678));
            assert(hdr.version == YMSGBufUtils.uShort(0x0));
            assert(hdr.length == YMSGBufUtils.uShort(0x3cc));
            assert(hdr.service == YMSGBufUtils.uShort(0x55));
            assert(hdr.status == YMSGBufUtils.uInt(0x5));
            assert(hdr.sessionId == YMSGBufUtils.uInt(0x6b9cbf11));
            byte[] packetBuf = hdr.toBuf();
            assert(Arrays.equals(TEST_PACKET, packetBuf));
            System.out.println("\nRead Header:\n\t"+hdr.toString());
        } catch (IOException e) {
            System.out.println("Caught IOException: "+e);
            e.printStackTrace();
        }
    }
}
