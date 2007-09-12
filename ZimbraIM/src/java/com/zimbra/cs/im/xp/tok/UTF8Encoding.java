package com.zimbra.cs.im.xp.tok;

/**
 * An <CODE>Encoding</CODE> for UTF-8.
 * @version $Revision: 1.5 $ $Date: 1998/05/27 05:43:15 $
 */
final class UTF8Encoding extends Encoding {
  private static final byte[] utf8HiTypeTable = {
    /* 0x80 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0x84 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0x88 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0x8C */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0x90 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0x94 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0x98 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0x9C */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xA0 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xA4 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xA8 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xAC */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xB0 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xB4 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xB8 */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xBC */ BT_MALFORM, BT_MALFORM, BT_MALFORM, BT_MALFORM,
    /* 0xC0 */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xC4 */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xC8 */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xCC */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xD0 */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xD4 */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xD8 */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xDC */ BT_LEAD2, BT_LEAD2, BT_LEAD2, BT_LEAD2,
    /* 0xE0 */ BT_LEAD3, BT_LEAD3, BT_LEAD3, BT_LEAD3,
    /* 0xE4 */ BT_LEAD3, BT_LEAD3, BT_LEAD3, BT_LEAD3,
    /* 0xE8 */ BT_LEAD3, BT_LEAD3, BT_LEAD3, BT_LEAD3,
    /* 0xEC */ BT_LEAD3, BT_LEAD3, BT_LEAD3, BT_LEAD3,
    /* 0xF0 */ BT_LEAD4, BT_LEAD4, BT_LEAD4, BT_LEAD4,
    /* 0xF4 */ BT_LEAD4, BT_LEAD4, BT_LEAD4, BT_LEAD4,
    /* 0xF8 */ BT_NONXML, BT_NONXML, BT_NONXML, BT_NONXML,
    /* 0xFC */ BT_NONXML, BT_NONXML, BT_MALFORM, BT_MALFORM
  };

  private static final byte[] utf8TypeTable = new byte[256];

  static {
    System.arraycopy(asciiTypeTable, 0, utf8TypeTable, 0, 128);
    System.arraycopy(utf8HiTypeTable, 0, utf8TypeTable, 128, 128);
  }

  UTF8Encoding() {
    super(1);
  }

  int byteType(byte[] buf, int off) {
    return utf8TypeTable[buf[off] & 0xFF];
  }

  int byteToAscii(byte[] buf, int off) {
    return (char)buf[off];
  }

  // c is a significant ASCII character
  boolean charMatches(byte[] buf, int off, char c) {
    return (char)buf[off] == c;
  }

  /* A 2 byte UTF-8 representation splits the characters 11 bits
     between the bottom 5 and 6 bits of the bytes. */

  int byteType2(byte[] buf, int off) {
    final byte[] page = charTypeTable[(buf[off] >>> 2) & 0x7];
    return page[((buf[off] & 3) << 6) | (buf[off + 1] & 0x3F)];
  }

  /* A 3 byte UTF-8 representation splits the characters 16 bits
     between the bottom 4, 6 and 6 bits of the bytes. */

  /* This will (incorrectly) return BT_LEAD4 for surrogates, but that
     doesn't matter. */
  int byteType3(byte[] buf, int off) {
    final byte[] page = charTypeTable[((buf[off] & 0xF) << 4)
				      | ((buf[off + 1] >>> 2) & 0xF)];
    return page[((buf[off + 1] & 3) << 6) | (buf[off + 2] & 0x3F)];
  }

  void check3(byte[] buf, int off) throws InvalidTokenException {
    switch (buf[off]) {
    case 0xEF - 0x100:
      /* 0xFFFF 0xFFFE */
      if (buf[off + 1] == (0xBF - 0x100)
	  && (buf[off + 2] == (0xBF - 0x100)
	      || buf[off + 2] == (0xBE - 0x100)))
	break;
      return;
    case 0xED - 0x100:
      /* 0xD800..0xDFFF <=> top 5 bits are 11011 */
      if ((buf[off + 1] & 0x20) != 0)
	break;
      return;
    default:
      return;
    }
    throw new InvalidTokenException(off);
  }

  void check4(byte[] buf, int off) throws InvalidTokenException {
    switch (buf[off] & 0x7) {
    default:
      return;
    case 5: case 6: case 7:
      break;
    case 4:
      if ((buf[off + 1] & 0x30) == 0)
	return;
      break;
    }
    throw new InvalidTokenException(off);
  }

  public int convert(byte[] sourceBuf, int sourceStart, int sourceEnd,
		     char[] targetBuf, int targetStart) {
    int initTargetStart = targetStart;
    int c;
    while (sourceStart != sourceEnd) {
      byte b = sourceBuf[sourceStart++];
      if (b >= 0)
	targetBuf[targetStart++] = (char)b;
      else {
	switch (utf8TypeTable[b & 0xFF]) {
	case BT_LEAD2:
	  /* 5, 6 */
	  targetBuf[targetStart++]
	    = (char)(((b & 0x1F) << 6) | (sourceBuf[sourceStart++] & 0x3F));
	  break;
	case BT_LEAD3:
	  /* 4, 6, 6 */
	  c = (b & 0xF) << 12;
	  c |= (sourceBuf[sourceStart++] & 0x3F) << 6;
	  c |= (sourceBuf[sourceStart++] & 0x3F);
	  targetBuf[targetStart++] = (char)c;
	  break;
	case BT_LEAD4:
	  /* 3, 6, 6, 6 */
	  c = (b & 0x7) << 18;
	  c |= (sourceBuf[sourceStart++] & 0x3F) << 12;
	  c |= (sourceBuf[sourceStart++] & 0x3F) << 6;
	  c |= (sourceBuf[sourceStart++] & 0x3F);
	  c -= 0x10000;
      	  targetBuf[targetStart++] = (char)((c >> 10) | 0xD800);
	  targetBuf[targetStart++] = (char)((c & ((1 << 10) - 1)) | 0xDC00);
	  break;
	}
      }
    }
    return targetStart - initTargetStart;
  }

  public int getFixedBytesPerChar() {
    return 0;
  }

  public void movePosition(final byte[] buf, int off, int end, Position pos) {
    /* Maintain the invariant: off - colDiff == colNumber. */
    int colDiff = off - pos.columnNumber;
    int lineNumber = pos.lineNumber;
    while (off != end) {
      byte b = buf[off];
      if (b >= 0) {
	++off;
	switch (b) {
	case (byte)'\n':
	  lineNumber += 1;
	  colDiff = off;
	  break;
	case (byte)'\r':
	  lineNumber += 1;
	  if (off != end && buf[off] == '\n')
	    off++;
	  colDiff = off;
	  break;
	}
      }
      else {
	switch (utf8TypeTable[b & 0xFF]) {
	default:
	  off += 1;
	  break;
	case BT_LEAD2:
	  off += 2;
	  colDiff++;
	  break;
	case BT_LEAD3:
	  off += 3;
	  colDiff += 2;
	  break;
	case BT_LEAD4:
	  off += 4;
	  colDiff += 3;
	  break;
	}
      }
    }
    pos.columnNumber = off - colDiff;
    pos.lineNumber = lineNumber;
  }

  int extendData(final byte[] buf, int off, final int end) throws InvalidTokenException {
    while (off != end) {
      int type = utf8TypeTable[buf[off] & 0xFF];
      if (type >= 0)
	off++;
      else if (type < BT_LEAD4)
	break;
      else {
	if (end - off + type < 0)
	  break;
	switch (type) {
	case BT_LEAD3:
	  check3(buf, off);
	  break;
	case BT_LEAD4:
	  check4(buf, off);
	  break;
	}
	off -= type;
      }
    }
    return off;
  }

}
