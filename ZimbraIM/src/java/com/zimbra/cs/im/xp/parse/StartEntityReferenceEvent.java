package com.zimbra.cs.im.xp.parse;

/**
 * Information about the end of the reference to an entity.
 * @see com.zimbra.cs.im.xp.parse.Application#startEntityReference
 * @version $Revision: 1.1 $ $Date: 1998/06/10 09:45:12 $
 */
public interface StartEntityReferenceEvent {
  /**
   * Returns the name of the referenced entity.
   */
  String getName();
}
