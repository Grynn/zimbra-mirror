package com.zimbra.cs.im.xp.parse;

/**
 * Information about a comment
 * @see com.zimbra.cs.im.xp.parse.Application#comment
 * @version $Revision: 1.1 $ $Date: 1998/06/10 09:45:10 $
 */
public interface CommentEvent extends LocatedEvent {
  /**
   * Returns the body of the comment occurring between
   * the <code>&lt;--</code> and <code>--&gt;</code>.
   */
  String getComment();
}
