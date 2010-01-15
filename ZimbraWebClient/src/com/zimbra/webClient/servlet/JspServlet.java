/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.webClient.servlet;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZMailbox;

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
 */
public class JspServlet extends org.apache.jasper.servlet.JspServlet {

	//
	// Constants
	//

	public static final String P_SKIN = "skin";
	public static final String P_DEFAULT_SKIN = "zimbraDefaultSkin";

	public static final String A_SKIN = "skin";

	protected static final String MANIFEST = "manifest.xml";

	//
	// Servlet methods
	//

	public void service(ServletRequest request, ServletResponse response)
	throws IOException, ServletException {
		// set custom class loader
		Thread thread = Thread.currentThread();
		ClassLoader oLoader = thread.getContextClassLoader();
		ClassLoader nLoader = new ResourceLoader(oLoader, this, request, response);
		thread.setContextClassLoader(nLoader);

		// default processing
		try {
			super.service(request, response);
		}

		// restore previous class loader
		finally {
			thread.setContextClassLoader(oLoader);
		}
	}

	//
	// Private methods
	//

	String setSkin(ServletRequest request, ServletResponse response) {
		// start with if skin is already set as an attribute
		String skin = (String)request.getAttribute(A_SKIN);
//		ZimbraLog.webclient.debug("### request: "+skin);

		// is skin specified in request parameter?
		if (skin == null) {
			skin = request.getParameter(P_SKIN);
//			ZimbraLog.webclient.debug("### param: "+skin);
		}

		// is it available in session?
		if (skin == null) {
			HttpSession hsession = ((HttpServletRequest)request).getSession(false);
			if (hsession != null) {
				skin = (String)hsession.getAttribute(A_SKIN);
//				ZimbraLog.webclient.debug("### http session: "+skin);
			}
		}

		// user preference
		ZMailbox mailbox = null;
		if (skin == null) {
			JspFactory factory  = JspFactory.getDefaultFactory();
			PageContext context = factory.getPageContext(this, request, response, null, true, 0, true);
			if (ZJspSession.hasSession(context)) {
				try {
					ZJspSession zsession = ZJspSession.getSession(context);
					if (zsession != null) {
						mailbox = ZJspSession.getZMailbox(context);
						skin = mailbox.getPrefs().getSkin();
//						ZimbraLog.webclient.debug("### zimbra session: "+skin);
					}
				}
				catch (Exception e) {
					if (ZimbraLog.webclient.isDebugEnabled()) {
						ZimbraLog.webclient.debug("no zimbra session");
					}
				}
				finally {
					factory.releasePageContext(context);
				}
			}
		}

		/*** NOTE: This causes an additional SOAP request ***
		// is this skin allowed?
		if (skin != null && mailbox != null) {
			try {
				boolean found = false;
				for (String name : mailbox.getAvailableSkins()) {
					if (name.equals(skin)) {
						found = true;
						break;
					}
				}
				if (!found) {
					skin = null;
				}
			}
			catch (Exception e) {
				ZimbraLog.webclient.error("unable to get available skins");
				skin = null;
			}
		}
		/***/

		// is the skin even present?
		if (skin != null) {
			File manifest = new File(getServletContext().getRealPath("/skins/"+skin+"/"+MANIFEST));
			if (!manifest.exists()) {
				ZimbraLog.webclient.debug("selected skin ("+skin+") doesn't exist");
				skin = null;
			}
		}

		// fall back to default skin
		if (skin == null) {
			skin = getServletContext().getInitParameter(P_DEFAULT_SKIN);
//			ZimbraLog.webclient.debug("### default: "+skin);
		}

		// store in the request
		request.setAttribute(A_SKIN, skin);

		return skin;
	}

	//
	// Classes
	//

	static class ResourceLoader extends ClassLoader {

		//
		// Data
		//

		private JspServlet servlet;
		private ServletRequest request;
		private ServletResponse response;

		//
		// Constructors
		//

		public ResourceLoader(ClassLoader parent, JspServlet servlet,
							  ServletRequest request, ServletResponse response) {
			super(parent);
			this.servlet = servlet;
			this.request = request;
			this.response = response;
		}

		//
		// ClassLoader methods
		//

		public InputStream getResourceAsStream(String filename) {
			if (ZimbraLog.webclient.isDebugEnabled()) {
				ZimbraLog.webclient.debug("getResourceAsStream: filename="+filename);
			}

			// default resource
			String basename = filename.replaceAll("^/skins/[^/]+", "");
			boolean isMsgOrKey = basename.startsWith("/messages/") || basename.startsWith("/keys/");
			if (!isMsgOrKey) {
				return super.getResourceAsStream(filename);
			}

			// aggregated resources
			InputStream stream = super.getResourceAsStream(basename);

			String skin = (String)this.request.getAttribute(JspServlet.A_SKIN);
			if (skin == null) {
				skin = this.servlet.setSkin(this.request, this.response);
			}
			File file = new File(this.servlet.getServletContext().getRealPath("/skins/"+skin+basename));
			if (file.exists()) {
				if (ZimbraLog.webclient.isDebugEnabled()) {
					ZimbraLog.webclient.debug("  found message overrides for skin="+skin);
				}
				try {
					InputStream skinStream = new FileInputStream(file);

					// NOTE: We have to add a newline in case the original
					//       stream doesn't end with one. Otherwise, the
					//       first line from the skin stream will appear
					//       as part of the value of the last line in the
					//       original stream.
					InputStream newlineStream = new ByteArrayInputStream("\n".getBytes());

					stream = stream != null
						   ? new SequenceInputStream(stream, new SequenceInputStream(newlineStream, skinStream))
						   : skinStream;
				}
				catch (FileNotFoundException e) {
					// ignore
				}
			}

			return stream;
		}

	} // class ResourceLoader

} // class JspServlet
