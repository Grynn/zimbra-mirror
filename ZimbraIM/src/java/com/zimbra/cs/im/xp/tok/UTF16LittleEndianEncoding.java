/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
 
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
