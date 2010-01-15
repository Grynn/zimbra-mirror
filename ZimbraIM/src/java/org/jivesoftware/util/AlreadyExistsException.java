/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.util;

/**
 * <p>Flags an exception when something to be created or added already exists.</p>
 * <p>Use this class when it's not worth creating a unique
 * xAlreadyExistsException class, or where the context of
 * the call makes it obvious what type of object was not found.</p>
 *
 * @author Iain Shigeoka
 */
public class AlreadyExistsException extends Exception {

    public AlreadyExistsException() {
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}
