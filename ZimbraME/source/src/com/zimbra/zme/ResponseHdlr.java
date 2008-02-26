/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
