/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Web Client
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

package com.zimbra.webClient.servlet;

import com.zimbra.common.util.ZimbraLog;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Props2JsServlet extends com.zimbra.kabuki.servlets.Props2JsServlet {

	//
	// Constants
	//

	protected static final String P_SKIN = "skin";
	protected static final String A_SKIN = P_SKIN;

	//
	// HttpServlet methods
	//

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, ServletException {

		// output original properties
		super.doGet(req, resp);

		// output skin overrides
		Object oBasenames = req.getAttribute(A_BASENAME_PATTERNS);
		Object oRequestUri = req.getAttribute(A_REQUEST_URI);

		String skin = this.getSkin(req);
		String patterns = "skins/"+skin+"/messages/${name},skins/"+skin+"/keys/${name}";
		String requestUri = skin+"/"+this.getRequestURI(req);
		req.setAttribute(A_BASENAME_PATTERNS, patterns);
		req.setAttribute(A_REQUEST_URI, requestUri);

		super.doGet(req, resp);

		req.setAttribute(A_BASENAME_PATTERNS, oBasenames);
		req.setAttribute(A_REQUEST_URI, oRequestUri);

	} // doGet(HttpServletRequest,HttpServletResponse)

	//
	// Protected methods
	//

	protected String getSkin(HttpServletRequest req) {
		String skin = (String)req.getAttribute(A_SKIN);
		if (skin == null) {
			skin = req.getParameter(P_SKIN);
		}
		return skin;
	}

	//
	// com.zimbra.kabuki.servlets.Props2JsServlet methods
	//

	protected void warn(String message) {
		ZimbraLog.webclient.warn(message);
	}
	protected void error(String message) {
		ZimbraLog.webclient.error(message);
	}

} // class Props2JsServlet