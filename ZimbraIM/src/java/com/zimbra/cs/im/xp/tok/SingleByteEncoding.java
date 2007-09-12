package com.zimbra.cs.im.xp.tok;

/**
 * An <CODE>Encoding</CODE> for an arbitrary encoding
 * that represents every character by exactly one byte.
 *
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:51:04 $
 */
final class SingleByteEncoding extends Encoding {

  private final byte[] byteTypeTable = new byte[256];
  private final int[] asciiToByte = new int[128];
  private final String map;

  SingleByteEncoding(String map) {
    super(1);
    this.map = map;
    for (int i = 0; i < 128; i++)
      asciiToByte[i] = 0xFF;
    for (int i = 0; i < 256; i++) {
      char c = map.charAt(i);
      if (c != 0xFFFD) {
	byteTypeTable[i] = charTypeTable[c >> 8][c & 0xFF];
	  if (c < 128)
	    asciiToByte[c] = (byte)i;
      }
      else
	byteTypeTable[i] = BT_MALFORM;
    }
  }

  int byteType(byte[] buf, int off) {
    return byteTypeTable[buf[off] & 0xFF];
  }

  int byteToAscii(byte[] buf, int off) {
    return map.charAt(buf[off] & 0xFF);
  }

  // c is a significant ASCII character
  boolean charMatches(byte[] buf, int off, char c) {
    return asciiToByte[c] == buf[off];
  }

  public int convert(byte[] sourceBuf, int sourceStart, int sourceEnd,
		     char[] targetBuf, int targetStart) {
    int initTargetStart = targetStart;
    int c;
    while (sourceStart != sourceEnd) 
      targetBuf[targetStart++] = map.charAt(sourceBuf[sourceStart++] & 0xFF);
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
      switch (byteTypeTable[buf[off++] & 0xFF]) {
      case BT_CR:
	lineNumber += 1;
	colStart = off;
	break;
      case BT_LF:
	lineNumber += 1;
	if (off != end && buf[off] == asciiToByte['\n'])
	  off++;
	colStart = off;
	break;
      }
    }
    pos.columnNumber = off - colStart;
    pos.lineNumber = lineNumber;
  }

  int extendData(final byte[] buf, int off, final int end) {
    while (off != end && byteTypeTable[buf[off] & 0xFF] >= 0)
      off++;
    return off;
  }

}
