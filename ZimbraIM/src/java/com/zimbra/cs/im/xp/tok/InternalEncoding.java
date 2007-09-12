package com.zimbra.cs.im.xp.tok;

/**
 * An <CODE>Encoding</CODE> for UTF-16 in big-endian byte order
 * (network byte order), but with newlines already normalized to U+000A.
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:51:04 $
 */
class InternalEncoding extends Encoding {
  private static final byte[][] internalCharTypeTable = new byte[256][];

  InternalEncoding() {
    super(2);
  }

  final int byteType(byte[] buf, int off) {
    return internalCharTypeTable[buf[off] & 0xFF][buf[off + 1] & 0xFF];
  }

  // c is a significant ASCII character
  final boolean charMatches(byte[] buf, int off, char c) {
    return buf[off] == 0 && (char)buf[off + 1] == c;
  }

  final int byteToAscii(byte[] buf, int off) {
    return buf[off] == 0 ? (char)buf[off + 1] : -1;
  }

  public int convert(byte[] sourceBuf, int sourceStart, int sourceEnd,
		     char[] targetBuf, int targetStart) {
    int origTargetStart = targetStart;
    while (sourceStart != sourceEnd) {
      int c = (sourceBuf[sourceStart++] & 0xFF) << 8;
      c |= sourceBuf[sourceStart++] & 0xFF;
      targetBuf[targetStart++] = (char)c;
    }
    return targetStart - origTargetStart;
  }

  public int getFixedBytesPerChar() {
    return 2;
  }

  public void movePosition(final byte[] buf, int off, int end, Position pos) {
    int lineNumber = pos.lineNumber;
    /* Maintain invariant: off - colStart = colNumber*2 */
    int colStart = off - (pos.columnNumber << 1);
    while (off != end) {
      if (buf[off] == 0) {
	off++;
	if (buf[off++] == (byte)'\n') {
	  lineNumber++;
	  colStart = off;
	}
      }
      else
	off += 2;
    }
    pos.lineNumber = lineNumber;
    pos.columnNumber = (off - colStart) >> 1;
  }

  static {
    System.arraycopy(charTypeTable, 0, internalCharTypeTable, 0, 256);
    internalCharTypeTable[0] = new byte[256];
    System.arraycopy(charTypeTable[0], 0, internalCharTypeTable[0], 0, 256);
    internalCharTypeTable[0]['\r'] = BT_S;
  }

}
