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
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Props2JsServlet extends com.zimbra.kabuki.servlets.Props2JsServlet {

	//
	// Constants
	//

	protected static final String P_SKIN = "skin";
	protected static final String A_SKIN = P_SKIN;

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

	protected String getRequestURI(HttpServletRequest req) {
		return this.getSkin(req) + super.getRequestURI(req);
	}

	protected List<String> getBasenamePatternsList(HttpServletRequest req) {
		List<String> list = super.getBasenamePatternsList(req);
		String skin = this.getSkin(req);
		String patterns = "skins/"+skin+"/messages/${name},skins/"+skin+"/keys/${name}";
		list.add(patterns);
		return list;
	};

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