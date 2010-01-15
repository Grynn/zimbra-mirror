/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mime.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;

public class Macintosh extends Charset {

    private static final char[] MAC_HIGH_CHAR_MAP = new char[] {
        '\u00C4', '\u00C5', '\u00C7', '\u00C9', '\u00D1', '\u00D6', '\u00DC', '\u00E1',
        '\u00E0', '\u00E2', '\u00E4', '\u00E3', '\u00E5', '\u00E7', '\u00E9', '\u00E8',
        '\u00EA', '\u00EB', '\u00ED', '\u00EC', '\u00EE', '\u00EF', '\u00F1', '\u00F3',
        '\u00F2', '\u00F4', '\u00F6', '\u00F5', '\u00FA', '\u00F9', '\u00FB', '\u00FC',
        '\u2020', '\u00B0', '\u00A2', '\u00A3', '\u00A7', '\u2022', '\u00B6', '\u00DF',
        '\u00AE', '\u00A9', '\u2122', '\u00B4', '\u00A8', '\u2260', '\u00C6', '\u00D8',
        '\u221E', '\u00B1', '\u2264', '\u2265', '\u00A5', '\u00B5', '\u2202', '\u2211',
        '\u220F', '\u03C0', '\u222B', '\u00AA', '\u00BA', '\u03A9', '\u00E6', '\u00F8',
        '\u00BF', '\u00A1', '\u00AC', '\u221A', '\u0192', '\u2248', '\u2206', '\u00AB',
        '\u00BB', '\u2026', '\u00A0', '\u00C0', '\u00C3', '\u00D5', '\u0152', '\u0153',
        '\u2013', '\u2014', '\u201C', '\u201D', '\u2018', '\u2019', '\u00F7', '\u25CA',
        '\u00FF', '\u0178', '\u2044', '\u20AC', '\u2039', '\u203A', '\uFB01', '\uFB02',
        '\u2021', '\u00B7', '\u201A', '\u201E', '\u2030', '\u00C2', '\u00CA', '\u00C1',
        '\u00CB', '\u00C8', '\u00CD', '\u00CE', '\u00CF', '\u00CC', '\u00D3', '\u00D4',
        '\uF8FF', '\u00D2', '\u00DA', '\u00DB', '\u00D9', '\u0131', '\u02C6', '\u02DC',
        '\u00AF', '\u02D8', '\u02D9', '\u02DA', '\u00B8', '\u02DD', '\u02DB', '\u02C7',
    };

    private static final Map<Character, Byte> MAC_ENCODABLE_CHARS = new HashMap<Character, Byte>();
        static {
            for (int i = 0; i < MAC_HIGH_CHAR_MAP.length; i++)
                MAC_ENCODABLE_CHARS.put(MAC_HIGH_CHAR_MAP[i], (byte) (i + 0x80));
        }

    protected char[] HIGH_CHAR_MAP;
    protected Map<Character, Byte> ENCODABLE_CHARS;


    public class MacintoshDecoder extends CharsetDecoder {
        protected MacintoshDecoder(Charset cs) {
            super(cs, 1, 1);
        }

        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            while (in.hasRemaining()) {
                if (!out.hasRemaining())
                    return CoderResult.OVERFLOW;
                byte c = in.get();
                out.put(c >= 0x00 && c <= 0x7F ? (char) c : HIGH_CHAR_MAP[c + 0x80]);
            }
            return CoderResult.UNDERFLOW;
        }
    }

    public class MacintoshEncoder extends CharsetEncoder {
        protected MacintoshEncoder(Charset cs) {
            super(cs, 1, 1);
        }

        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            while (in.hasRemaining()) {
                if (!out.hasRemaining())
                    return CoderResult.OVERFLOW;
                char c = in.get();
                if (c >= 0x00 && c <= 0x7F)
                    out.put((byte) c);
                else {
                    Byte encoded = ENCODABLE_CHARS.get(c);
                    if (encoded == null) {
                        in.position(in.position() - 1);
                        return CoderResult.unmappableForLength(1);
                    }
                    out.put(encoded);
                }
            }
            return CoderResult.UNDERFLOW;
        }
    }

    Macintosh(String canonicalName, String[] aliases) {
        super(canonicalName, aliases);

        HIGH_CHAR_MAP   = MAC_HIGH_CHAR_MAP;
        ENCODABLE_CHARS = MAC_ENCODABLE_CHARS;
    }

    public boolean contains(Charset cs) {
        return cs.displayName().equalsIgnoreCase("US-ASCII");
    }

    public CharsetDecoder newDecoder() {
        return new MacintoshDecoder(this);
    }

    public CharsetEncoder newEncoder() {
        return new MacintoshEncoder(this);
    }
}