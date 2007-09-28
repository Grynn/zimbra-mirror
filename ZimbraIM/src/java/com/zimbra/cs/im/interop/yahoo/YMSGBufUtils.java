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

import java.util.Formatter;

/**
 * 
 */
class YMSGBufUtils {
    
    public static final long uInt(long value) {
        // signed-unsigned java crap
        return value & 0xffffffffL;
    }
    
    public static final int uShort(int value) {
        // signed-unsigned crap
        return value & 0xffff;
    }
    
    public static final String toHex(long value) {
        return new Formatter().format("0x%x", value).toString();
    }
    public static final String toHex(int value) {
        return new Formatter().format("0x%x", value).toString();
    }
    public static final String toHex(short value) {
        return new Formatter().format("0x%x", value).toString();
    }
    public static final String toHex(byte value) {
        return new Formatter().format("0x%x", value).toString();
    }
    
    
    public static final long readUInt(byte[] buf, int offset) {
        long toRet = 0;
        for (int i = offset; i < offset+4; i++) {
            toRet<<=8;
            toRet |= (buf[i])&0xff;
        }
        return uInt(toRet);
    }
    
    /**
     * Unsigned Short returned as an 'int' because of java signedness 
     * 
     * @param buf
     * @param offset
     * @return
     */
    public static final int readUShort(byte[] buf, int offset) {
        int toRet = 0; // store as int to avoid signed/unsigned issues
        for (int i = offset; i < offset+2; i++) {
            toRet<<=8;
            toRet |= buf[i]&0xff;
        }
        return uShort(toRet);
    }
    
    public static final void writeInt(long value, byte[] buf, int offset) {
        buf[offset] =   (byte)((value >> 24) & 0xff);
        buf[offset+1] = (byte)((value >> 16) & 0xff);
        buf[offset+2] = (byte)((value >>  8) & 0xff);
        buf[offset+3] = (byte)(value & 0xff);
    }
    
    public static final void writeShort(int value, byte[] buf, int offset) {
        buf[offset] =   (byte)((value >> 8) & 0xff);
        buf[offset+1] = (byte)(value & 0xff);
    }
    
    public static final String readCharString(byte[] buf, int offset, int length) {
        StringBuilder toRet = new StringBuilder();
        
        for (int i = offset; i < offset+length; i++) {
            toRet.append(Character.valueOf((char)(buf[offset])));
        }
        return toRet.toString();
    }
    
}
