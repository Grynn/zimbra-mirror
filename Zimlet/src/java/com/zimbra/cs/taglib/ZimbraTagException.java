/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib;

import javax.servlet.jsp.JspTagException;

public class ZimbraTagException extends JspTagException {
	public static ZimbraTagException AUTH_FAILURE(String msg) {
		return new ZimbraTagException("missing auth: "+msg);
	}
	public static ZimbraTagException MISSING_ATTR(String msg) {
		return new ZimbraTagException("missing attribute: "+msg);
	}
	public static ZimbraTagException IO_ERROR(Throwable cause) {
		return new ZimbraTagException("io error", cause);
	}
	public static ZimbraTagException SERVICE_ERROR(Throwable cause) {
		return new ZimbraTagException("service error", cause);
	}
	
	public ZimbraTagException(String msg) {
		super(msg);
	}
	public ZimbraTagException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
