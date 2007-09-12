package com.zimbra.cs.im.xp.util;

import java.io.InputStream;
import java.io.IOException;

public class NonblockingInputStream extends InputStream {
    
    boolean mEof = false;

    byte[] mBuf = null;
    int mBufOff;
    
    public NonblockingInputStream() {
        
    }
    
    public void setEof() { mEof = true; }
    public boolean eof() { return mEof; }
    
    public void addBytes(byte[] b) {
        System.out.print("Adding Chars: ");
        for (byte bin : b) {
            System.out.print((char)bin);
        }
        System.out.println("\n");
        
        if (mBuf != null) {
            byte[] newBuf = new byte[mBuf.length + b.length - mBufOff];
            System.arraycopy(mBuf, mBufOff, newBuf, 0, mBuf.length-mBufOff);
            System.arraycopy(b, 0, newBuf, mBuf.length-mBufOff, b.length);
            mBuf = newBuf;
            mBufOff = 0;
        } else {
            mBuf = b;
        }
    }
    
    public int read() throws IOException {
        if (mEof)
            return -1;
        if (mBuf == null || mBufOff >= mBuf.length) {
            assert(false);
            return -1;
        }
        int toRet = mBuf[mBufOff++];
        if (mBufOff >= mBuf.length) {
            mBuf = null;
            mBufOff = 0;
        }
        return toRet;
    }
    
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (mEof)
            return -1;
        if (mBuf == null || mBufOff >= mBuf.length)
            return 0;
        int lenToCopy = Math.min(mBuf.length-mBufOff, len);
        System.arraycopy(mBuf, mBufOff, b, off, lenToCopy);
        
        mBufOff+=lenToCopy;
        if (mBufOff >= mBuf.length) {
            mBuf = null;
            mBufOff = 0;
        }
        return lenToCopy;
    }
}
