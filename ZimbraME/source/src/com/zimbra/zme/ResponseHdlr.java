/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme;

public interface ResponseHdlr {
	/**
	 * This method is called (by Mailbox run()) to alert the ResponseHdlr to 
	 * the thread that is handling the current request. The ResponseHdlr
	 * commonly passes this to Mailbox.cancelOp() should it wish to cancel
	 * the ongoing operation
	 * @param svcThreadName
	 */
	/*public void svcThread(String svcThreadName);*/

	public void handleResponse(Object op,
			                   Object resp);
}
