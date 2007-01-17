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
