package com.zimbra.cs.im.xp.parse;

/**
 * Interface for events which provide location information.
 * @version $Revision: 1.1 $ $Date: 1998/05/27 19:07:46 $
 */
public interface LocatedEvent {
  /**
   * Returns the location
   * of the first character of the markup of the event.
   * The return value is valid only so long as the event
   * itself is valid.
   */
  ParseLocation getLocation();
}

