/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.filetransfer.proxy;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 *  An output stream which tracks the amount of bytes transfered by proxy sockets.
 */
public class ProxyOutputStream extends DataOutputStream {
    static AtomicLong amountTransfered = new AtomicLong(0);

    public ProxyOutputStream(OutputStream out) {
        super(out);
    }

    public synchronized void write(byte b[], int off, int len) throws IOException {
        super.write(b, off, len);
        amountTransfered.addAndGet(len);
    }
}
