/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class UnfoldingInputStream extends PushbackInputStream {

    private int mLinesUnfolded;

    /**
     * @param in an input stream to read from
     */
    public UnfoldingInputStream(final InputStream in) {
        super(in, 3);
    }

    /**
     * @return number of lines unfolded so far while reading
     */
    public int getLinesUnfolded() {
        return mLinesUnfolded;
    }

    private static final int CR = 13;
    private static final int LF = 10;
    private static final int SPACE = 32;
    private static final int TAB = 9;

    public int read() throws IOException {
        while (true) {
            int b1 = super.read();
            if (b1 == -1)
                return -1;
            if (b1 == CR) {
                int b2 = super.read();
                if (b2 == LF) {
                    int b3 = super.read();
                    if (b3 == SPACE || b3 == TAB) {
                        ++mLinesUnfolded;
                        // keep looping
                    } else {
                        if (b3 != -1)
                            unread(b3);
                        unread(b2);  // LF
                        return b1;   // CR
                    }
                } else {
                    if (b2 != -1)
                        unread(b2);
                    return b1;  // CR
                }
            } else if (b1 == LF) {
                int b2 = super.read();
                if (b2 == SPACE || b2 == TAB) {
                    ++mLinesUnfolded;
                    // keep looping
                } else {
                    if (b2 != -1)
                        unread(b2);
                    return b1;   // LF
                }
            } else {
                return b1;
            }
        }
    }

    public int read(byte b[], int off, int len) throws IOException {
        // Must force the use of read(void) in this class.
        // copied from InputStream.read(byte[], int, int)
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
               ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
            c = read();
            if (c == -1) {
                break;
            }
            if (b != null) {
                b[off + i] = (byte)c;
            }
            }
        } catch (IOException ee) {
        }
        return i;
    }

    public int read(byte b[]) throws IOException {
        // Must force the use of read(void) in this class.
        return read(b, 0, b.length);
    }
}
