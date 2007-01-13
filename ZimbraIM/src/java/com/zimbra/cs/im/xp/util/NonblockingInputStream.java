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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
 
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
