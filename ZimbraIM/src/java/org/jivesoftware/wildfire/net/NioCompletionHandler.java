package org.jivesoftware.wildfire.net;

public interface NioCompletionHandler {
    void nioReadCompleted(org.apache.mina.common.ByteBuffer buf);
    void nioClosed();
}
