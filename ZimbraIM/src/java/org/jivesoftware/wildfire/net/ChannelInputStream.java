/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Class that simulate an InputStream given a un-blocking channel.
 *
 * @author Daniele Piras
 */
class ChannelInputStream extends InputStream
{
  ByteBuffer buf = ByteBuffer.allocate(1024);

  ReadableByteChannel inputChannel;

  public ChannelInputStream(ReadableByteChannel ic)
  {
    inputChannel = ic;
  }

  private void doRead() throws IOException
  {
    final int cnt = inputChannel.read(buf);
    if (cnt > 0)
    {
      buf.flip();
    }
    else
    {
      if (cnt == -1)
      {
        buf.flip();
      }
    }
  }

  public synchronized int read(byte[] bytes, int off, int len)
      throws IOException
  {
    if (buf.position() == 0)
    {
      doRead();
    }
    else
    {
      buf.flip();
    }
    len = Math.min(len, buf.remaining());
    if (len == 0)
    {
      return -1;
    }
    buf.get(bytes, off, len);
    if (buf.hasRemaining())
    {
      // Discard read data and move unread data to the begining of the buffer.
      // Leave
      // the position at the end of the buffer as a way to indicate that there
      // is
      // unread data
      buf.compact();
    }
    else
    {
      buf.clear();
    }
    return len;
  }

  @Override
  public int read() throws IOException
  {
    byte[] tmpBuf = new byte[1];
    int byteRead = read(tmpBuf, 0, 1);
    if (byteRead < 1)
    {
      return -1;
    }
    else
    {
      return tmpBuf[0];
    }
  }
}
