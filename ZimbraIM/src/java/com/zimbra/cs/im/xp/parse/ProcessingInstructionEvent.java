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
