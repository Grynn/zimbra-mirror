/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop;

import org.xmpp.component.ComponentException;

public class AlreadyConnectedComponentException extends ComponentException {
    private static final long serialVersionUID = 1032727758287169122L;
    public AlreadyConnectedComponentException(String serviceName, String username) {
        super("Already connected to service "+serviceName+" as user "+username);
    }
}