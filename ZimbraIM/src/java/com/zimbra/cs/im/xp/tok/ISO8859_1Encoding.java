package com.zimbra.cs.im.xp.tok;

/**
 * An Encoding for ISO 8859-1.
 *
 * @version $Revision: 1.3 $ $Date: 1998/08/28 10:07:26 $
 */
final class ISO8859_1Encoding extends Encoding {

  private static final byte[] latin1Table;

  static {
    latin1Table = charTypeTable[0];
  }

  ISO8859_1Encoding() {
    super(1);
  }

  int byteType(byte[] buf, int off) {
    return latin1Table[buf[off] & 0xFF];
  }

  int byteToAscii(byte[] buf, int off) {
    return (char)buf[off];
  }

  // c is a significant ASCII character
  boolean charMatches(byte[] buf, int off, char c) {
    return (char)buf[off] == c;
  }

  public int convert(byte[] sourceBuf, int sourceStart, int sourceEnd,
		     char[] targetBuf, int targetStart) {
    int initTargetStart = targetStart;
    int c;
    while (sourceStart != sourceEnd) 
      targetBuf[targetStart++] = (char)(sourceBuf[sourceStart++] & 0xFF);
    return targetStart - initTargetStart;
  }

  public int getFixedBytesPerChar() {
    return 1;
  }

  public void movePosition(final byte[] buf, int off, int end, Position pos) {
    /* Maintain the invariant: off - colStart == colNumber. */
    int colStart = off - pos.columnNumber;
    int lineNumber = pos.lineNumber;
    while (off != end) {
      byte b = buf[off++];
      switch (b) {
      case (byte)'\n':
	lineNumber += 1;
	colStart = off;
	break;
      case (byte)'\r':
	lineNumber += 1;
	if (off != end && buf[off] == '\n')
	  off++;
	colStart = off;
	break;
      }
    }
    pos.columnNumber = off - colStart;
    pos.lineNumber = lineNumber;
  }

  int extendData(final byte[] buf, int off, final int end) {
    while (off != end && latin1Table[buf[off] & 0xFF] >= 0)
      off++;
    return off;
  }

}
