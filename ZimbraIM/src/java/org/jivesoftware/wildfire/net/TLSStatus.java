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
package org.jivesoftware.wildfire.net;

/**
 * A TLSStatus enum describing the current handshaking state of this TLS connection.
 *
 * This source file originally from the Tigase project (http://www.tigase.org). Used with permission
 * (DO NOT REMOVE ATTRIBUTION!)
 * 
 * @author Artur Hefczyc 
 */
public enum TLSStatus {

	/**
	 * ust send data to the remote side before handshaking can continue.
	 */
	NEED_WRITE,

	/**
	 * Need to receive data from the remote side before handshaking can continue.
	 */
	NEED_READ,

	/**
	 * Not be able to unwrap the incoming data because there were not enough source bytes available
	 * to make a complete packet.
	 */
	UNDERFLOW,

	/**
	 * The operation just closed this side of the SSLEngine, or the operation could not be completed
	 * because it was already closed.
	 */
	CLOSED,

	/**
	 * Handshaking is OK.
	 */
	OK;
}
