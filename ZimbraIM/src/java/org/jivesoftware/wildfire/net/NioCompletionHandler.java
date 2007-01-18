package org.jivesoftware.wildfire.net;

import org.apache.mina.common.ByteBuffer;

public interface NioCompletionHandler {
    void nioReadCompleted(ByteBuffer buf);
    void nioClosed();
}
