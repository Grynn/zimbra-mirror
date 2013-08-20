/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.webClient.servlet;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.client.ZMailbox;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

/**
 * This class sub-classes the Jasper JspServlet in order to override
 * the context class loader. This is done so that we can transparently
 * merge skin message files into the default ones allowing skins to
 * independently override messages; JSP authors continue to use the
 * same mechanisms to load and format messages in JSP pages without
 * having to care about how the skin messages are overloaded.
 *
 * @author Andy Clark
 * Note: The above mentioned logic has been moved to I18nUtil.java
 * since we want to avoid overriding of context class loader for all
 * jsp pages.
 */
public class JspServlet extends org.apache.jasper.servlet.JspServlet {

	//
	// Servlet methods
	//

	public void service(ServletRequest request, ServletResponse response)
	throws IOException, ServletException {
		// set custom class loader
//		Thread thread = Thread.currentThread();
//		ClassLoader oLoader = thread.getContextClassLoader();
//		ClassLoader nLoader = new ResourceLoader(oLoader, this, request, response);
//		thread.setContextClassLoader(nLoader);

		// default processing
		try {
			super.service(request, response);
		}

		// restore previous class loader
		finally {
//			thread.setContextClassLoader(oLoader);
		}
	}
} // class JspServlet
