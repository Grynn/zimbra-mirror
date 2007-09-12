package com.zimbra.cs.im.xp.parse;

import java.io.Writer;
import java.io.IOException;

/**
 * Information about character data.
 * There is no guarantee that consecutive characters will
 * be reported in the same <code>CharacterDataEvent</code>.
 * Surrogate pairs are guaranteed not to be split across
 * <code>CharacterDataEvent</code>s.
 * Line boundaries are normalized to <code>'\n'</code> (ASCII code 10).
 * @see com.zimbra.cs.im.xp.parse.Application#characterData
 * @version $Revision: 1.6 $ $Date: 1998/06/10 09:43:54 $
 */
public interface CharacterDataEvent {
  /**
   * Returns the length in chars of the character data.
   * A character represented by a pair of surrogate chars
   * counts as 2 chars.
   */
  int getLength();
  /**
   * Returns an upper bound on the length of the character data.
   * The value returned is guaranteed to be greater than or equal the value
   * returned by <code>getLength</code>.
   * This can be used to ensure that the buffer passed to
   * <code>copyChars</code> is large enough;
   * it is typically much faster to use <code>getLengthMax</code>
   * than <code>getLength</code> for this.
   */
  int getLengthMax();
  /**
   * Copies the character data into the specified character array
   * starting at index <code>off</code>.
   * The length of the array must be sufficient to hold all the
   * character data.
   * @return the number of characters of data
   * (the same as returned by <code>getLength</code>)
   */
  int copyChars(char[] cbuf, int off);

  /**
   * Writes the character data to the specified <code>Writer</code>.
   */
  void writeChars(Writer writer) throws IOException;

  /**
   * Returns true if the character was a result of a character reference
   * or a predefined entity reference.
   * If this returns true, then
   * <code>getLength</code> and <code>getLengthMax</code> will return,
   * unless the referenced character is represented
   * as a surrogate pair in which case 2 will be returned.
   */
  boolean isReference();
}
