/* ***** BEGIN LICENSE BLOCK *****
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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;

public class NioWriter extends Writer {
    
    IoSession mIoSession;
    CharsetEncoder mEncoder;
    
    public NioWriter(IoSession session, CharsetEncoder encoder) {
        mIoSession = session;
        mEncoder = encoder;
    }

    @Override
    public void close() throws IOException {
        mIoSession.close();
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            ByteBuffer wb = ByteBuffer.allocate(len);
            wb.putString(CharBuffer.wrap(cbuf, off, len), mEncoder);
            wb.flip();
            mIoSession.write(wb);
        }
    }
}
