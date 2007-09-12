package com.zimbra.cs.im.xp.tok;

/**
 * An Encoding for US-ASCII
 *
 * @version $Revision: 1.2 $ $Date: 1998/08/13 08:33:58 $
 */
final class ASCIIEncoding extends Encoding {

  private static final byte[] asciiTable = new byte[256];

  static {
    System.arraycopy(asciiTypeTable, 0, asciiTable, 0, 128);
    for (int i = 128; i < 256; i++)
      asciiTable[i] = (byte)BT_MALFORM;
  }

  ASCIIEncoding() {
    super(1);
  }

  int byteType(byte[] buf, int off) {
    return asciiTable[buf[off] & 0xFF];
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
      targetBuf[targetStart++] = (char)sourceBuf[sourceStart++];
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
    while (off != end && asciiTable[buf[off] & 0xFF] >= 0)
      off++;
    return off;
  }

}
