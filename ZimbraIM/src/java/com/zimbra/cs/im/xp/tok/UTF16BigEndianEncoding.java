package com.zimbra.cs.im.xp.tok;

/**
 * An <CODE>Encoding</CODE> for UTF-16 in big-endian byte order
 * (network byte order)
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:51:05 $
 */
class UTF16BigEndianEncoding extends Encoding {
  UTF16BigEndianEncoding() {
    super(2);
  }

  final int byteType(byte[] buf, int off) {
    return charTypeTable[buf[off] & 0xFF][buf[off + 1] & 0xFF];
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
	switch (buf[off++]) {
	case (byte)'\n':
	  lineNumber++;
	  colStart = off;
	  break;
	case (byte)'\r':
	  if (off != end && buf[off] == 0 && buf[off + 1] == '\n')
	    off += 2;
	  lineNumber++;
	  colStart = off;
	  break;
	}
      }
      else
	off += 2;
    }
    pos.lineNumber = lineNumber;
    pos.columnNumber = (off - colStart) >> 1;
  }

  Encoding getUTF16Encoding() {
    return this;
  }
}
