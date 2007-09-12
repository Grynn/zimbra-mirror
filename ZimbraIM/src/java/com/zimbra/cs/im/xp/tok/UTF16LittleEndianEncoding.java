package com.zimbra.cs.im.xp.tok;

/**
 * An <CODE>Encoding</CODE> for UTF-16 in little-endian byte order
 * (least significant byte first).
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:51:05 $
 */
class UTF16LittleEndianEncoding extends Encoding {
  UTF16LittleEndianEncoding() {
    super(2);
  }

  final int byteType(byte[] buf, int off) {
    return charTypeTable[buf[off + 1] & 0xFF][buf[off] & 0xFF];
  }

  // c is a significant ASCII character
  final boolean charMatches(byte[] buf, int off, char c) {
    return buf[off + 1] == 0 && (char)buf[off] == c;
  }

  final int byteToAscii(byte[] buf, int off) {
    return buf[off + 1] == 0 ? (char)buf[off] : -1;
  }

  public int convert(byte[] sourceBuf, int sourceStart, int sourceEnd,
		     char[] targetBuf, int targetStart) {
    int origTargetStart = targetStart;
    while (sourceStart != sourceEnd) {
      int c = (sourceBuf[sourceStart++] & 0xFF);
      c |= (sourceBuf[sourceStart++] & 0xFF) << 8;
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
      switch (buf[off++]) {
      case (byte)'\n':
	if (buf[off++] == 0) {
	  lineNumber++;
	  colStart = off;
	}
	break;
      case (byte)'\r':
	if (buf[off++] == 0) {
	  if (off != end && buf[off] == '\n' && buf[off + 1] == 0)
	    off += 2;
	  lineNumber++;
	  colStart = off;
	}
	break;
      default:
	off++;
	break;
      }
    }
    pos.lineNumber = lineNumber;
    pos.columnNumber = (off - colStart) >> 1;
  }

  Encoding getUTF16Encoding() {
    return this;
  }
}
