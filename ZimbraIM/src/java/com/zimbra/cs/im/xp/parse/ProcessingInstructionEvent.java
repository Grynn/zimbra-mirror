package com.zimbra.cs.im.xp.parse;

/**
 * Information about a processing instruction.
 * @see com.zimbra.cs.im.xp.parse.Application#processingInstruction
 * @version $Revision: 1.6 $ $Date: 1998/05/27 19:07:23 $
 */
public interface ProcessingInstructionEvent extends LocatedEvent {
  /**
   * Returns the target of the processing instruction.
   */
  String getName();
  /**
   * Returns the part of the processing instruction following the
   * target.  Leading white space is not included.
   * The string will be empty rather than null if the processing
   * instruction contains only a target.
   */
  String getInstruction();
}
