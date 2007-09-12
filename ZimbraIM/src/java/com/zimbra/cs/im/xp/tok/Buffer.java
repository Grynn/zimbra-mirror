package com.zimbra.cs.im.xp.tok;

/**
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:51:03 $
 */
public final class Buffer {
  private static final int INIT_SIZE = 64;
  private char[] buf = new char[INIT_SIZE];
  private int len;

  public void clear() {
    len = 0;
  }

  public void append(char c) {
    need(1);
    buf[len++] = c;
  }

  public void appendRefCharPair(Token t) {
    need(2);
    t.getRefCharPair(buf, len);
    len += 2;
  }

  public void append(Encoding enc, byte[] bbuf, int start, int end) {
    need((end - start)/enc.getMinBytesPerChar());
    len += enc.convert(bbuf, start, end, buf, len);
  }

  private void need(int n) {
    if (len + n <= buf.length)
      return;
    char[] tem = buf;
    if (n > tem.length)
      buf = new char[n * 2];
    else
      buf = new char[tem.length << 1];
    System.arraycopy(tem, 0, buf, 0, tem.length);
  }

  public byte[] getBytes() {
    byte[] text = new byte[len << 1];
    int j = 0;
    for (int i = 0; i < len; i++) {
      char c = buf[i];
      text[j++] = (byte)(c >> 8);
      text[j++] = (byte)(c & 0xFF);
    }
    return text;
  }

  public String toString() {
    return new String(buf, 0, len);
  }

  public int length() {
    return len;
  }

  public char charAt(int i) {
    if (i >= len)
      throw new IndexOutOfBoundsException();
    return buf[i];
  }

  public void chop() {
    --len;
  }
}
