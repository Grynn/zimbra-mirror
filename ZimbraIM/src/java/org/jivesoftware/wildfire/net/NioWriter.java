/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class NioWriter extends Writer {

    IoSession mIoSession;
    CharsetEncoder mEncoder;

    public NioWriter(IoSession session, CharsetEncoder encoder) {
        mIoSession = session;
        mEncoder = encoder;
    }

    @Override
    public void close() throws IOException {
        mIoSession.close(true);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            IoBuffer wb = IoBuffer.allocate(len);
            wb.putString(CharBuffer.wrap(cbuf, off, len), mEncoder);
            wb.flip();
            mIoSession.write(wb);
        }
    }
}
