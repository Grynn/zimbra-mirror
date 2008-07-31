/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2005, 2006 Zimbra, Inc.
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
package com.zimbra.cs.mime.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * @author dkarp
 */
public class UTF7 extends Charset {

    private static final int MAX_UTF7_CHAR_VALUE = 0x7f;

    protected char BEGIN_SHIFT;
    protected char END_SHIFT;

    protected final byte[] BASE_64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };
    protected final byte INVERSE_BASE_64[] = new byte[128];
    protected static final byte NON_BASE_64 = -1;

    protected final boolean NO_SHIFT_REQUIRED[] = new boolean[128];


    public class UTF7Decoder extends CharsetDecoder {
        private boolean shifted = false, first = false;
        private int decoder = 0, bits = 0;

        protected UTF7Decoder(Charset cs) {
			super(cs, (float) 0.4, 1);
		}

        @Override protected void implReset() {
            shifted = first = false;
            decoder = bits = 0;
        }

        @Override protected CoderResult implFlush(CharBuffer out) {
            if (shifted && decoder != 0)
                return CoderResult.malformedForLength(1);
            return CoderResult.UNDERFLOW;
		}

        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            while (in.hasRemaining()) {
                if (!out.hasRemaining())
                    return CoderResult.OVERFLOW;
                byte c = in.get();
                if (c < 0 || c > MAX_UTF7_CHAR_VALUE) {
                    in.position(in.position() - 1);
                    return CoderResult.malformedForLength(1);
                }

                if (shifted) {
                    byte decodedChar = INVERSE_BASE_64[c];
                    if (decodedChar == NON_BASE_64) {
                        boolean malformed = decoder != 0;
                        shifted = false;
                        bits = decoder = 0;
                        if (first && c == END_SHIFT)
                            out.put(BEGIN_SHIFT);
                        if (malformed) {
                            in.position(Math.max(0, in.position() - 2));
                            return CoderResult.malformedForLength(1);
                        }
                        if (c == END_SHIFT)
                            continue;
                    } else {
                        decoder = (decoder << 6) | decodedChar;
                        first = false;
                        bits += 6;
                        if (bits >= 16) {
                            bits -= 16;
                            out.put((char) (decoder >> bits));
                            decoder &= ~(0xFFFF << bits);
                        }
                    }
                }

                if (!shifted) {
                    if (c == BEGIN_SHIFT)
                        shifted = first = true;
                    else
                        out.put((char) c);
                }
            }
            return CoderResult.UNDERFLOW;
		}
    }

    public class UTF7Encoder extends CharsetEncoder {
        private boolean shifted = false;
        private int encoder = 0, bits = 0;

        protected UTF7Encoder(Charset cs) {
			super(cs, (float) 2.5, 5);
		}

        @Override protected void implReset() {
            shifted = false;
            encoder = bits = 0;
        }

        @Override protected CoderResult implFlush(ByteBuffer out) {
            if (shifted) {
                if (out.remaining() < 2)
                    return CoderResult.OVERFLOW;
                if (bits > 0) {
                    encoder <<= (6-bits);
                    out.put(BASE_64[encoder]);
                    encoder = bits = 0;
                }
                out.put((byte) END_SHIFT);
                shifted = false;
            }
            return CoderResult.UNDERFLOW;
        }

        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
			while (in.hasRemaining()) {
                if (out.remaining() < 4)
                    return CoderResult.OVERFLOW;
                char c = in.get();
                boolean needsShift = c > MAX_UTF7_CHAR_VALUE || !NO_SHIFT_REQUIRED[c];
                
                if (needsShift && !shifted) {
                    out.put((byte) BEGIN_SHIFT);
                    if (c == BEGIN_SHIFT)
                        out.put((byte) END_SHIFT);
                    else
                        shifted = true;
                }
                
                if (shifted) {
                    if (needsShift) {
                        encoder = (encoder << 16) | c;
                        bits += 16;
                        do {
                            out.put(BASE_64[0x3F & (encoder >> (bits-6))]);
                            bits -= 6;
                        } while (bits >= 6);
                        encoder &= (0x3F >> (6-bits));
                    } else
                        implFlush(out);
                }
                
                if (!needsShift)
                    out.put((byte) c);
            }
            // need to force a flush (sigh)
            // return CoderResult.UNDERFLOW;
            return implFlush(out);
		}
    }

    UTF7(String canonicalName, String[] aliases) {
		super(canonicalName, aliases);

        BEGIN_SHIFT = '+';
        END_SHIFT   = '-';

        for (int i = 0; i < INVERSE_BASE_64.length; i++)
            INVERSE_BASE_64[i] = NON_BASE_64;
        for (byte i = 0; i < BASE_64.length; i++)
            INVERSE_BASE_64[BASE_64[i]] = i;

        final String unshifted = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'(),-./:? \t\r\n";
        for (int i = 0; i < unshifted.length(); i++)
            NO_SHIFT_REQUIRED[unshifted.charAt(i)] = true;
	}

	public boolean contains(Charset cs) {
		return true;
	}

	public CharsetDecoder newDecoder() {
		return new UTF7Decoder(this);
	}

	public CharsetEncoder newEncoder() {
		return new UTF7Encoder(this);
	}
}
