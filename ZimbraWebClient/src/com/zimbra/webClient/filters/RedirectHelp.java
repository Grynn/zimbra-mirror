/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.webClient.filters;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.zimbra.common.util.ZimbraLog;

public class RedirectHelp implements Filter {

	//
	// Constants
	//

	private static final String P_INPUT_DIRNAME = "input.dir";
	private static final String P_OUTPUT_DIRNAME = "output.dir";
	private static final String P_LOCALE_ID = "locid";

	private static final String DEFAULT_INPUT_DIRNAME = "/help";
	private static final String DEFAULT_OUTPUT_DIRNAME = "/help";

	//
	// Data
	//

	private ServletContext context;

	private String inDirName;
	private String outDirName;
	private File outDir;

	private Pattern inDirPattern;
	private Pattern outDirPattern;

	//
	// Filter methods
	//

	public void init(FilterConfig config) throws ServletException {
		this.context = config.getServletContext();

		// get input and output dirs
		this.inDirName = config.getInitParameter(P_INPUT_DIRNAME);
		if (this.inDirName== null) this.inDirName = DEFAULT_INPUT_DIRNAME;
		this.outDirName = config.getInitParameter(P_OUTPUT_DIRNAME);
		if (this.outDirName == null) this.outDirName = DEFAULT_OUTPUT_DIRNAME;
		if (ZimbraLog.webclient.isDebugEnabled()) {
			ZimbraLog.webclient.debug("### indir:  "+this.inDirName);
			ZimbraLog.webclient.debug("### outdir: "+this.outDirName);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		// create input/output dir patterns
		String contextPath = httpRequest.getContextPath();
		if (this.inDirPattern == null) {
			// NOTE: Have to do this here because the context path is not
			//       available in init().
			this.inDirPattern = Pattern.compile("^"+escape(contextPath)+escape(this.inDirName)+"/(.*)");
			this.outDirPattern = Pattern.compile("^"+escape(contextPath)+"/help/[a-z]{2}(?:_[A-Z]{2})?/.*");
			if (ZimbraLog.webclient.isDebugEnabled()) {
				ZimbraLog.webclient.debug("### indir pattern:  "+this.inDirPattern.pattern());
				ZimbraLog.webclient.debug("### outdir pattern: "+this.outDirPattern.pattern());
			}
		}

		// check to see if we need to redirect this request
		String requestUri = httpRequest.getRequestURI();
		if (this.outDirPattern.matcher(requestUri).matches()) {
			// allow it to go through
			chain.doFilter(request, response);
			return;
		}

		// make list of potential locales to check
		Locale preferredLocale = getLocale(httpRequest);
		String language = preferredLocale.getLanguage();
		String country = preferredLocale.getCountry();
		Locale[] locales = {
			preferredLocale,
			country != null ? new Locale(language) : null,
			Locale.US
		};
		if (ZimbraLog.webclient.isDebugEnabled()) {
			for (Locale locale : locales) {
				ZimbraLog.webclient.debug("locale: "+locale);
			}
		}

		// find out which version of the requested file exists
		Locale actualLocale = preferredLocale;
		Matcher matcher = this.inDirPattern.matcher(requestUri);
		if (!matcher.matches()) {
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Help URL doesn't match input pattern.");
			return;
		}

		if (ZimbraLog.webclient.isDebugEnabled()) {
			ZimbraLog.webclient.debug("### filename: "+matcher.group(1));
		}
		String filename = decode(matcher.group(1)).replace('/', File.separatorChar);
		if (ZimbraLog.webclient.isDebugEnabled()) {
			ZimbraLog.webclient.debug("### filename: "+filename);
		}
		File baseDir = new File(this.context.getRealPath("/"));
		if (ZimbraLog.webclient.isDebugEnabled()) {
			ZimbraLog.webclient.debug("### basedir:  "+baseDir);
		}
		for (Locale locale : locales) {
			if (locale == null) continue;
			File file = new File(baseDir,
				this.outDirName.replaceAll("\\{locale\\}", locale.toString()) + 
				File.separatorChar +
				filename
			);
			if (file.exists()) {
				actualLocale = locale;
				break;
			}
		}

		// redirect
		String redirectUrl =
			contextPath +
			this.outDirName.replaceAll("\\{locale\\}", actualLocale.toString())+"/" +
			filename
		;
		if (ZimbraLog.webclient.isDebugEnabled()) {
			ZimbraLog.webclient.debug("redirecting to: "+redirectUrl);
		}
		httpResponse.sendRedirect(redirectUrl);
	}

	public void destroy() {
		this.context = null;
	}

	//
	// Protected methods
	//

	protected Locale getLocale(HttpServletRequest request) {
		String locid = request.getParameter(P_LOCALE_ID);
		if (locid == null || locid.length() == 0) {
			return request.getLocale();
		}
		StringTokenizer tokenizer = new StringTokenizer(locid, "_-");
		String language = String.valueOf(tokenizer.nextToken()).toLowerCase();
		String country = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
		if (country != null) {
			return new Locale(language, country.toUpperCase());
		}
		return new Locale(language);
	}

	//
	// Private functions
	//

	private static String escape(String pattern) {
		return pattern.replaceAll("[?+*\\\\\\{\\[\\(]", "\\$1");
	}

	//
	// Private functions
	//

	private static final Pattern PATTERN = Pattern.compile("((?:%[0-9a-fA-F]{2})+)");

	/**
	 * Replace occurrences of "%ab" with the character represented by the hex
	 * value. Strings of escaped characters are treated as UTF-8 byte sequences
	 * and decoded appropriately.
	 */
	private static String decode(String s) {
		int length = s.length();
		StringBuilder str = new StringBuilder(length);
		Matcher matcher = PATTERN.matcher(s);
		int offset = 0;
		byte[] bb = null;
		while (matcher.find(offset)) {
			int count = matcher.groupCount();
			for (int i = 0; i < count; i++) {
				String match = matcher.group(0);
				int num = match.length() / 3;
				if (bb == null || bb.length < num) {
					bb = new byte[num];
				}
				for (int j = 0; j < num; j++) {
					int head = j * 3 + 1;
					int tail = head + 2;
					bb[j] = (byte)Integer.parseInt(match.substring(head, tail), 16);
				}
				try {
					String text = new String(bb, "UTF-8");
					str.append(s.substring(offset, matcher.start()));
					str.append(text);
				}
				catch (UnsupportedEncodingException e) {
					// NOTE: This should *never* be thrown because all
					//       JVMs are required to support UTF-8. I mean,
					//       the strings in the .class file are all in
					//       a modified UTF-8, for pete's sake! :)
				}
			}
			offset = matcher.end();
		}
		if (offset < length) {
			str.append(s.substring(offset));
		}
		return str.toString();
	}

} // class RedirectHelp