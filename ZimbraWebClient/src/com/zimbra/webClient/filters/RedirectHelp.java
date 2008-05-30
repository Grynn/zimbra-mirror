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
			ZimbraLog.webclient.debug("### "+this.inDirName);
			ZimbraLog.webclient.debug("### "+this.outDirName);
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
				ZimbraLog.webclient.debug("### "+this.inDirPattern.pattern());
				ZimbraLog.webclient.debug("### "+this.outDirPattern.pattern());
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

		// find out which version of the requested file exists
		Locale actualLocale = preferredLocale;
		Matcher matcher = this.inDirPattern.matcher(requestUri);
		if (!matcher.matches()) {
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Help URL doesn't match input pattern.");
			return;
		}

		String filename = matcher.group(1).replace('/', File.separatorChar);
		File baseDir = new File(this.context.getRealPath("/"));
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

} // class RedirectHelp