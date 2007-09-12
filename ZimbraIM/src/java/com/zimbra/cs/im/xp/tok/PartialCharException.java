package com.zimbra.cs.im.xp.tok;

/**
 * Thrown to indicate that the subarray being tokenized is not the
 * complete encoding of one or more characters, but might be if
 * more bytes were added.
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:24:11 $
 */
public class PartialCharException extends PartialTokenException {
  private int leadByteIndex;
  PartialCharException(int leadByteIndex) {
    this.leadByteIndex = leadByteIndex;
  }
  /**
   * Returns the index of the first byte that is not part of the complete
   * encoding of a character.
   */
  public int getLeadByteIndex() {
    return leadByteIndex;
  }
}
