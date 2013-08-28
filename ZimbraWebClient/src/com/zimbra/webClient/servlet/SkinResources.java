/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import com.zimbra.common.account.Key;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.HttpUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.servlet.DiskCacheServlet;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.kabuki.util.Colors;

/**
 * TODO: Clean up this code!
 */
public class SkinResources
		extends DiskCacheServlet {

	//
	// Constants
	//

	public static final String A_IMAGE_CACHE = SkinResources.class.getName()+":images";

	private static final String P_SKIN = "skin";
	private static final String P_DEFAULT_SKIN = "zimbraDefaultSkin";
	private static final String P_DEFAULT_ADMIN_SKIN = "zimbraDefaultAdminSkin";
	private static final String P_USER_AGENT = "agent";
	private static final String P_DEBUG = "debug";
	private static final String P_CLIENT = "client";
	private static final String P_LOCALE = "locale";
	private static final String P_LANGUAGE = "language";
	private static final String P_COUNTRY = "country";
	private static final String P_VARIANT = "variant";
	private static final String P_SERVER_NAME = "server-name";
	private static final String P_SERVLET_PATH = "servlet-path";
	private static final String P_TEMPLATES = "templates";
	private static final String P_COMPRESS = "compress";
	private static final String P_CUSTOMER_DOMAIN = "customerDomain";

	private static final String V_TRUE = "true";
	private static final String V_FALSE = "false";
	private static final String V_SPLIT = "split";
	private static final String V_ONLY = "only";

	private static final long MAX_INCLUDED_TEMPLATES_SIZE = 1 << 13; // 8K

	private static final String A_TEMPLATES_INCLUDED = "skin.templates.included";

	private static final String A_SKIN_FOREGROUND_COLOR = "zimbraSkinForegroundColor";
	private static final String A_SKIN_BACKGROUND_COLOR = "zimbraSkinBackgroundColor";
	private static final String A_SKIN_SECONDARY_COLOR = "zimbraSkinSecondaryColor";
	private static final String A_SKIN_SELECTION_COLOR = "zimbraSkinSelectionColor";

	private static final String A_SKIN_LOGO_LOGIN_BANNER = "zimbraSkinLogoLoginBanner";
	private static final String A_SKIN_LOGO_APP_BANNER = "zimbraSkinLogoAppBanner";
	private static final String A_SKIN_LOGO_URL = "zimbraSkinLogoURL";

	private static final String A_SKIN_FAVICON = "zimbraSkinFavicon";

	private static final String A_HELP_ADMIN_URL = "zimbraHelpAdminURL";
	private static final String A_HELP_ADVANCED_URL = "zimbraHelpAdvancedURL";
	private static final String A_HELP_DELEGATED_URL = "zimbraHelpDelegatedURL";
	private static final String A_HELP_STANDARD_URL = "zimbraHelpStandardURL";

	private static final String A_VERSION = "version";

	private static final String H_USER_AGENT = "User-Agent";

	private static final String C_SKIN = "ZM_SKIN";
	private static final String C_ADMIN_SKIN = "ZA_SKIN";

	private static final String T_CSS = "css";
	private static final String T_HTML = "html";
	private static final String T_JAVASCRIPT = "javascript";
    private static final String T_APPCACHE = "appcache";

	private static final String N_SKIN = "skin";
	private static final String N_IMAGES = "images";

	private static final String SKIN_MANIFEST = "manifest.xml";

	private static final String CLIENT_STANDARD = "standard";
	private static final String CLIENT_ADVANCED = "advanced";

	private static final Pattern RE_IFDEF = Pattern.compile("^\\s*#ifdef\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern RE_IFNDEF = Pattern.compile("^\\s*#ifndef\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern RE_ENDIF = Pattern.compile("^\\s*#endif(\\s+.*)?$", Pattern.CASE_INSENSITIVE);
	private static final Pattern RE_ELSE = Pattern.compile("^\\s*#else\\s*$", Pattern.CASE_INSENSITIVE);

	private static final String RE_COMMENTS = "/\\*[^*]*\\*+([^/][^*]*\\*+)*/";
	private static final String RE_WHITESPACE = "\\s+";

	private static final Pattern RE_VERSION = Pattern.compile("\\d+\\.\\d+");

    /*
     * this regex will match any of the below pattern
     * url(/path/name) or url('/path/name') or url("/path/name") or url('/path/name?v=123456789')
    */
    private static final Pattern RE_CSSURL = Pattern.compile("^(?!/\\*).*url\\(\'?\"?(.*?)\\??v?=?\\d*\'?\"?\\)");

	private static final String IMAGE_CSS = "img/images.css";

	private static final Map<String, String> TYPES = new HashMap<String, String>();

    private boolean supportsGzip = true;

	static {
		TYPES.put("css", "text/css");
		TYPES.put("html", "text/html");
		TYPES.put("js", "text/javascript");
		TYPES.put("plain", "text/plain");
        TYPES.put("appcache", "text/appcache");
	}

	//
	// Data
	//

	/**
	 * <strong>Note:</strong>
	 * This is needed because only the generate method knows if the
	 * templates were included. But we need that information on
	 * subsequent requests so that we can tell the callee if the
	 * templates were included.
	 * <p>
	 * Not knowing on subsequent requests whether templates were
	 * included caused bug 26563 and a 0-byte skin.js file to be
	 * requested even though everything had been inlined into
	 * launchZCS.jsp.
	 */
	private Map<String,Boolean> included = new HashMap<String,Boolean>();

	//
	// Constructors
	//

	public SkinResources() {
		super("skinres");
	}

	//
	// DiskCacheServlet methods
	//

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String val = getServletConfig().getInitParameter("shouldSupportGzip");
        if (val != null) {
            this.supportsGzip = Boolean.valueOf(val);
        } else {
            this.supportsGzip = true;
        }
    }

	@Override
    protected boolean flushCache(ServletRequest req) {
		boolean flushed = super.flushCache(req);
		if (flushed) {
			// NOTE: The app:imginfo tag for the standard client stores its
			// NOTE: image cache in this servlet's ServletContext object so
			// NOTE: that the image info can be flushed with the command:
			// NOTE: "zmprov fc skin".
			ServletContext context = getServletContext();
			// NOTE: We don't care what the image cache actually *is*
			// NOTE: because we can just remove it from the servlet context
			// NOTE: and it will be recreated the next time app:imginfo is
			// NOTE: called.
			Object cache = context.getAttribute(A_IMAGE_CACHE);
			if (cache != null) {
				context.removeAttribute(A_IMAGE_CACHE);
			}
		}
		return flushed;
	}

	//
	// HttpServlet methods
	//

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doGet(req, resp);
	}

	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String uri = getRequestURI(req);
		String contentType = getContentType(uri);
		String type = contentType.replaceAll("^.*/", "");
		String debugStr = req.getParameter(P_DEBUG);
		boolean debug =  debugStr != null && (debugStr.equals(Boolean.TRUE.toString()) || debugStr.equals("1"));
		String client = req.getParameter(P_CLIENT);
		if (client == null) {
			client = CLIENT_ADVANCED;
		}
		String cacheBusterVersion = (String) req.getAttribute(A_VERSION);

		String userAgent = getUserAgent(req);
		Map<String, String> macros = parseUserAgent(userAgent);
		String browserType = getMacroNames(macros.keySet());

		String skin = getSkin(req);
		String templates = req.getParameter(P_TEMPLATES);
		if (templates == null) templates = V_TRUE;
		String serverName = getServerName(req);

		String cacheId = serverName + ":" + uri + ":" + client + ":" + skin + "/templates=" + templates + ":" + browserType + ":" + cacheBusterVersion;

		Locale locale = getLocale(req);
		if (type.equals(T_JAVASCRIPT) || type.equals(T_CSS) || type.equals(T_APPCACHE)) {
			cacheId += ":" + locale;
		}

		String compressStr = req.getParameter(P_COMPRESS);
		boolean compress =
			supportsGzip &&
			(compressStr != null && (compressStr.equals("true") || compressStr.equals("1")))
		;
		compress = compress && macros.get("MSIE_6") == null;
		// NOTE: Keep compressed extension at end of cacheId.
		if (compress) {
			cacheId += EXT_COMPRESSED;
		}

		if (ZimbraLog.webclient.isDebugEnabled()) {
			ZimbraLog.webclient.debug("DEBUG: === debug is " + debug+" ("+debugStr+") ===");
			ZimbraLog.webclient.debug("DEBUG: querystring=" + req.getQueryString());
			ZimbraLog.webclient.debug("DEBUG: uri=" + uri);
			ZimbraLog.webclient.debug("DEBUG: type=" + type);
			ZimbraLog.webclient.debug("DEBUG: contentType=" + contentType);
			ZimbraLog.webclient.debug("DEBUG: client=" + client);
			ZimbraLog.webclient.debug("DEBUG: skin=" + skin);
			ZimbraLog.webclient.debug("DEBUG: templates="+templates);
			ZimbraLog.webclient.debug("DEBUG: browserType=" + browserType);
			ZimbraLog.webclient.debug("DEBUG: locale=" + locale);
			ZimbraLog.webclient.debug("DEBUG: cacheId=" + cacheId);
		}

		// generate buffer
		String buffer = null;
		File file = !debug ? getCacheFile(cacheId) : null;
		if (file == null || !file.exists()) {
			if (ZimbraLog.webclient.isDebugEnabled()) ZimbraLog.webclient.debug("DEBUG: generating buffer");
			buffer = generate(req, resp, cacheId, macros, type, client, locale, templates, cacheBusterVersion);
			if (!debug) {
				if (type.equals(T_CSS)) {
					CssCompressor compressor = new CssCompressor(new StringReader(buffer));
					StringWriter out = new StringWriter();
					compressor.compress(out, 0);
					buffer = out.toString();
				}
				if (type.equals(T_JAVASCRIPT)) {
					JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(buffer), new ErrorReporter() {

						@Override
                        public void warning(String message, String sourceName,
											int line, String lineSource, int lineOffset) {
							if (line < 0) {
								ZimbraLog.webclient.warn("\n" + message);
							} else {
								ZimbraLog.webclient.warn("\n" + line + ':' + lineOffset + ':' + message);
							}
						}

						@Override
                        public void error(String message, String sourceName,
										  int line, String lineSource, int lineOffset) {
							if (line < 0) {
								ZimbraLog.webclient.error("\n" + message);
							} else {
								ZimbraLog.webclient.error("\n" + line + ':' + lineOffset + ':' + message);
							}
						}

						@Override
                        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
							error(message, sourceName, line, lineSource, lineOffset);
							return new EvaluatorException(message);
						}
					});
					StringWriter out = new StringWriter();
					compressor.compress(out, 0, true, false, false, false);
					buffer = out.toString();
				}
				ZimbraLog.webclient.debug("DEBUG: buffer.length: "+buffer.length());

				// write buffer to cache file
				if (!debug) {
					// NOTE: This assumes that the cacheId will *end* with the compressed
					// NOTE: extension. Therefore, make sure to keep in sync.
					String uncompressedCacheId = compress ?
						cacheId.substring(0, cacheId.length() - EXT_COMPRESSED.length()) : cacheId;

					// store uncompressed file in cache
					file = createCacheFile(uncompressedCacheId, type);
					if (ZimbraLog.webclient.isDebugEnabled()) ZimbraLog.webclient.debug("DEBUG: buffer file: "+file);
					copy(buffer, file);
					putCacheFile(uncompressedCacheId, file);

					// store compressed file in cache
					if (compress) {
						String compressedCacheId = cacheId;
						File gzfile = createCacheFile(compressedCacheId, type+EXT_COMPRESSED);
						if (ZimbraLog.webclient.isDebugEnabled()) ZimbraLog.webclient.debug("DEBUG: buffer file: " + gzfile);
						file = compress(file, gzfile);
						putCacheFile(compressedCacheId, file);
					}
				}
			}
		} else {
			if (ZimbraLog.webclient.isDebugEnabled()) ZimbraLog.webclient.debug("DEBUG: using previous buffer");
		}

		// set headers
		try {
			// We browser sniff so need to make sure any caches do the same.
			resp.addHeader("Vary", "User-Agent");
			// Cache It!
			String maxAge = (String)req.getAttribute("init.Expires");
			if (maxAge == null) {
				maxAge = "2595600";
			}
			resp.setHeader("Cache-control", "public, max-age="+maxAge);
			resp.setContentType(type.equals(T_APPCACHE)? "text/cache-manifest" : contentType);

			if (compress && file != null) {
				resp.setHeader("Content-Encoding", "gzip");
			}
            if (type.equals(T_APPCACHE)){
                resp.setHeader("Expires", "Tue, 24 Jan 2000 17:46:50 GMT");
	            resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
	            resp.setHeader("Pragma", "no-cache");
            }

			// NOTE: I cast the file length to an int which I think is
			//	   fine. If the aggregated contents are larger than
			//	   Integer.MAX_VALUE, then we've got other problems. ;)
			//
			// NOTE: We can only be certain we know the final size if we are
			// NOTE: *not* compressing the output OR if we're just writing
			// NOTE: the contents of the generated file to the stream.
			if (!compress || file != null) {
				resp.setContentLength(file != null ? (int)file.length() : buffer.length());
			}
		}
		catch (IllegalStateException e) {
			// ignore -- thrown if called from including JSP
		}

		// write buffer
		if (file != null) {
			// NOTE: If we saved the buffer to a file and compression is
			// NOTE: enabled then the file has *already* been compressed
			// NOTE: and the Content-Encoding header has been added.
			copy(file, resp, false);
		}
		else {
			copy(buffer, resp, compress);
		}

		// keep track of whether the templates were included
		Boolean included = this.included.get(cacheId);
		if (included != null) {
			req.setAttribute(A_TEMPLATES_INCLUDED, included);
		}

	} // doGet(HttpServletRequest,HttpServletResponse)

	//
	// Private methods
	//

	private Locale getLocale(HttpServletRequest req) {
		String language = null, country = null, variant = null;
		String locale = req.getParameter(P_LOCALE);
		if (locale != null) {
			StringTokenizer tokenizer = new StringTokenizer(locale, "_");
			language = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
			country = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
			variant = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
		}
		else {
			language = req.getParameter(P_LANGUAGE);
			country = req.getParameter(P_COUNTRY);
			variant = req.getParameter(P_VARIANT);
		}
		if (language != null) {
			if (country != null) {
				if (variant != null) {
					return new Locale(language, country, variant);
				}
				return new Locale(language, country);
			}
			return new Locale(language);
		}
		return req.getLocale();
	} // getLocale(HttpServletRequest):Locale

	private String generate(HttpServletRequest req, HttpServletResponse resp,
							String cacheId, Map<String, String> macros,
							String type, String client, Locale requestedLocale,
							String templatesParam, String cacheBusterVersion)
			throws IOException {
		String commentStart = "/* ";
		String commentContinue = " * ";
		String commentEnd = " */";
		if (type.equals(T_HTML)) {
			commentStart = "<!-- ";
			commentContinue = " - ";
			commentEnd = " -->";
		}

		// create data buffers
		CharArrayWriter cout = new CharArrayWriter(4096 << 2); // 16K buffer to start
		PrintWriter out = new PrintWriter(cout);

		// get data
		String skin = getSkin(req);
		out.println(commentStart);
		for (String mname : macros.keySet()) {
			String mvalue = macros.get(mname);
			out.print(commentContinue);
			out.println("#define " + mname + " " + mvalue);
		}
		out.println(commentEnd);
		out.println();

		String uri = getRequestURI(req);
		String filenames = uri;
		String ext = "." + type;

		int slash = uri.lastIndexOf('/');
		if (slash != -1) {
			filenames = uri.substring(slash + 1);
		}

		int dot = filenames.lastIndexOf('.');
		if (dot != -1) {
			ext = filenames.substring(dot);
			filenames = filenames.substring(0, dot);
		}
        if (type.equals(T_APPCACHE)) {
            ext = ".css";
        }

		ServletContext context = getServletContext();

		String rootDirname = context.getRealPath("/");
		File rootDir = new File(rootDirname);
		File fileDir = new File(rootDir, type.equals(T_APPCACHE) ? "css" : type);
		String skinDirname = context.getRealPath("/skins/" + skin);
		File skinDir = new File(skinDirname);
		File manifestFile = new File(skinDir, SKIN_MANIFEST);

		String appContextPath = req.getContextPath();
		if (appContextPath == null) {
			ZimbraLog.webclient.debug("!!!Did not find context path in request object!");
			appContextPath = "/zimbra";
		}
		// domain overrides

		if (cacheBusterVersion == null) {
			cacheBusterVersion = "";
		}
		Map<String,String> substOverrides = new HashMap<String,String>();
		substOverrides.put(Manifest.S_APP_CONTEXT_PATH, appContextPath);
		substOverrides.put(Manifest.S_JS_VERSION, cacheBusterVersion);

		try {
			SoapProvisioning provisioning = new SoapProvisioning();
			String soapUri =
				LC.zimbra_admin_service_scheme.value() +
				LC.zimbra_zmprov_default_soap_server.value() +
				':' +
				LC.zimbra_admin_service_port.intValue() +
				AdminConstants.ADMIN_SERVICE_URI
			;
			provisioning.soapSetURI(soapUri);
			String serverName = getServerName(req);
			Entry info = provisioning.getDomainInfo(Key.DomainBy.virtualHostname, serverName);
			if (info == null) {
				info = provisioning.getConfig();
			}
			if (info != null) {
				// colors
				substOverrides.put(Manifest.S_SKIN_FOREGROUND_COLOR, info.getAttr(A_SKIN_FOREGROUND_COLOR));
				substOverrides.put(Manifest.S_SKIN_BACKGROUND_COLOR, info.getAttr(A_SKIN_BACKGROUND_COLOR));
				substOverrides.put(Manifest.S_SKIN_SECONDARY_COLOR, info.getAttr(A_SKIN_SECONDARY_COLOR));
				substOverrides.put(Manifest.S_SKIN_SELECTION_COLOR, info.getAttr(A_SKIN_SELECTION_COLOR));
				// images
				substOverrides.put(Manifest.S_SKIN_LOGO_LOGIN_BANNER, info.getAttr(A_SKIN_LOGO_LOGIN_BANNER));
				substOverrides.put(Manifest.S_SKIN_LOGO_APP_BANNER, info.getAttr(A_SKIN_LOGO_APP_BANNER));
				substOverrides.put(Manifest.S_SKIN_LOGO_URL, info.getAttr(A_SKIN_LOGO_URL));
				// favicon
				substOverrides.put(Manifest.S_SKIN_FAVICON, info.getAttr(A_SKIN_FAVICON));
				// help
				substOverrides.put(Manifest.S_HELP_ADMIN_URL, info.getAttr(A_HELP_ADMIN_URL));
				substOverrides.put(Manifest.S_HELP_ADVANCED_URL, info.getAttr(A_HELP_ADVANCED_URL));
				substOverrides.put(Manifest.S_HELP_DELEGATED_URL, info.getAttr(A_HELP_DELEGATED_URL));
				substOverrides.put(Manifest.S_HELP_STANDARD_URL, info.getAttr(A_HELP_STANDARD_URL));
			}
		}
		catch (Exception e) {
			if (ZimbraLog.webclient.isDebugEnabled()) {
				ZimbraLog.webclient.debug("!!! Unable to get domain config");
			}
		}

		// load manifest
		Manifest manifest = new Manifest(manifestFile, macros, client, substOverrides, requestedLocale);

		// process input files
		StringTokenizer tokenizer = new StringTokenizer(filenames, ",");
		while (tokenizer.hasMoreTokens()) {
			String filename = tokenizer.nextToken();
			if (ZimbraLog.webclient.isDebugEnabled()) ZimbraLog.webclient.debug("DEBUG: filename " + filename);
			String filenameExt = filename + ext;

			List<File> files = new LinkedList<File>();

			if (filename.equals(N_SKIN)) {
				if (type.equals(T_CSS) || type.equals(T_APPCACHE)) {
					for (File file : manifest.getFiles(type)) {
						files.add(file);
						String cssFilename = file.getName().replaceAll("\\.css$", "");
						String cssExt = file.getName().replaceAll("^.*\\.", ".");
						addLocaleFiles(files, requestedLocale, file.getParentFile(), cssFilename, cssExt);
					}

					File file = new File(skinDir, IMAGE_CSS);
					files.add(file);
					String cssFilename = file.getName().replaceAll("\\.css$", "");
					String cssExt = file.getName().replaceAll("^.*\\.", ".");
					addLocaleFiles(files, requestedLocale, file.getParentFile(), cssFilename, cssExt);
				}
				else if (type.equals(T_JAVASCRIPT)) {
					// decide whether to include templates
					boolean only = templatesParam.equals(V_ONLY);
					boolean split = templatesParam.equals(V_SPLIT);
					boolean include = only || split || templatesParam.equals(V_TRUE);

					// ignore main skin files if only want templates
					if (!only) {
						for (File file : manifest.getFiles(type)) {
							files.add(file);
							// TODO: Not sure if we want to allow different skin JS files
							//	   (aside from templates) based on locale.
//							String jsFilename = file.getName().replaceAll("\\.js$", "");
//							String jsExt = file.getName().replaceAll("^.*\\.", ".");
//							addLocaleFiles(files, requestedLocale, file.getParentFile(), jsFilename, jsExt);
						}
					}

					// include templates, unless request to split and too big
					if (include) {
						List<File> templates = manifest.templateFiles();
						boolean included = includeTemplates(templates, split);
						if (included) {
							for (File file : templates) {
								// TODO: optimize
								files.add(new File(file.getParentFile(), file.getName() + ".js"));
								String templateFilename = file.getName().replaceAll("\\.template$", "");
								String templateExt = ".template.js";
								addLocaleFiles(files, requestedLocale, file.getParentFile(), templateFilename, templateExt);
							}
						}
						this.included.put(cacheId, included);
					}
				}
				else {
					files.addAll(manifest.getFiles(type));
					// TODO: Add locale variants? Probably not...
				}
			} else {
				File dir = fileDir;
				File file = new File(dir, filenameExt);
				if (ZimbraLog.webclient.isDebugEnabled())
					ZimbraLog.webclient.debug("DEBUG: file " + file.getAbsolutePath());
				if (!file.exists() && (type.equals(T_CSS) || type.equals(T_APPCACHE)) && filename.equals(N_IMAGES)) {
					file = new File(rootDir, IMAGE_CSS);
					dir = file.getParentFile();
					if (ZimbraLog.webclient.isDebugEnabled())
						ZimbraLog.webclient.debug("DEBUG: !file.exists() " + file.getAbsolutePath());
				}
				files.add(file);
				if (type.equals(T_CSS) || type.equals(T_APPCACHE) || type.equals(T_JAVASCRIPT)) {
					addLocaleFiles(files, requestedLocale, dir, filename, ext);
				}
			}

			for (File file : files) {
				if (!file.exists()) {
					out.print(commentStart);
					out.print("Error: file doesn't exist - " + URLEncoder.encode(file.getAbsolutePath().replaceAll("^.*/webapps/", ""), "UTF-8"));
					out.println(commentEnd);
					out.println();
					continue;
				}
				if (ZimbraLog.webclient.isDebugEnabled())
					ZimbraLog.webclient.debug("DEBUG: preprocess " + file.getAbsolutePath());
				preprocess(file, cout, macros, manifest,
						commentStart, commentContinue, commentEnd, requestedLocale);
			}
		}

		// return data
		out.flush();
		if (type.equals(T_APPCACHE)) {
            String debugStr = req.getParameter(P_DEBUG);
			String debug = "";
			if (debugStr != null && !"".equals(debugStr)) {
				debug = "debug=" + debugStr + "&";
			}
            String skinStr = (getCookie(req, "ZM_CACHE_NEW_SKIN") != null) ? getCookie(req, "ZM_CACHE_NEW_SKIN").getValue() : skin;
            String localeStr = (getCookie(req, "ZM_CACHE_NEW_LANG") != null) ? getCookie(req, "ZM_CACHE_NEW_LANG").getValue() : null;
			String locale = "";
			if (localeStr != null && !"".equals(localeStr)) {
				locale = "locale=" + localeStr + "&";
			}
            if (ZimbraLog.webclient.isDebugEnabled()) {
			    ZimbraLog.webclient.debug("DEBUG: skin=" + skinStr);
			    ZimbraLog.webclient.debug("DEBUG: locale=" + localeStr);
		    }
			//create the full manifest file.
			StringBuffer sb = new StringBuffer();
			sb.append("CACHE MANIFEST\n\n");
			sb.append("#version ").append(cacheBusterVersion).append(" \n");
			sb.append("CACHE:\n");
            sb.append("\n#HTML files\n\n");
            sb.append("\n");
            sb.append(appContextPath).append("/");
            if (debugStr != null && (debugStr.equals(Boolean.TRUE.toString()) || debugStr.equals("1"))) {
                sb.append("?dev=1");
            }
			sb.append("\n#images\n\n");
			sb.append("/img/zimbra.gif\n"); //TODO remove this hardcoded image.
			sb.append("/img/zimbra.png\n"); //TODO remove this hardcoded image.
            sb.append("/skins/_base/logos/LoginBanner.png?v=").append(cacheBusterVersion).append(" \n"); //TODO remove this hardcoded image.
			sb.append("\n#style sheet images\n\n");
			//find all the css rules with a url in it
			Set<String> imgSet = new LinkedHashSet();
			for(String s: cout.toString().split("\\r?\\n")) {
				//run the regex on each line
				Matcher m = RE_CSSURL.matcher(s.trim());
				if (m.find()) {
					imgSet.add(m.group(1)); //use linked hash set to avoid duplicate images
				}
			}
            File imgFile = null;
            for (String fileName : imgSet){
                imgFile = new File(context.getRealPath(fileName));
                if (!imgFile.exists()){
                    continue;
                }
                fileName = fileName + "?v=" + cacheBusterVersion;
                sb.append("\n")
                .append(fileName);

            }
			sb.append("\n\n#style sheets\n");
			//create the url of the css files
            sb.append("\n").append(appContextPath).append("/css/").append(filenames).append(".css?v=").append(cacheBusterVersion)
              .append("&")
              .append(debug)
              .append("skin=").append(skinStr)
			  .append("&locale=" + localeStr);

			sb.append("\n").append(appContextPath).append("/css/msgview.css?v=").append(cacheBusterVersion);
			sb.append("\n\n#resources\n");
            sb.append("\n")
              .append(appContextPath);
			//create the resources url
            if (debugStr != null && (debugStr.equals(Boolean.TRUE.toString()) || debugStr.equals("1"))) {
                sb.append("/res/I18nMsg,AjxMsg,ZMsg,ZmMsg,AjxKeys,ZmKeys,ZdMsg,AjxTemplateMsg.js?v=");
            }
            else {
                sb.append("/res/I18nMsg,AjxMsg,ZMsg,ZmMsg,AjxKeys,ZmKeys,ZdMsg,AjxTemplateMsg.js.zgz?v=");
            }
            sb.append(cacheBusterVersion)
              .append('&')
              .append(debug);
			if (localeStr != null && !"".equals(localeStr)) {
				sb.append("language=").append(requestedLocale.getLanguage());
                String country = requestedLocale.getCountry();
                if (country != null){
                    sb.append("&country=").append(country).append("&");
                }
			}
            sb.append("skin=").append(skinStr);

			sb.append("\n").append(appContextPath).append("/js/skin.js?");
            if (client != null && !"".equals(client)) {
                sb.append("client=").append(client).append("&");
            }
            sb.append("skin=").append(skinStr)
              .append("&locale=" + localeStr)
              .append("&")
              .append(debug);

            String templatesStr = req.getParameter(P_TEMPLATES);
            if (templatesStr != null && !"".equals(templatesStr)) {
                sb.append("templates=").append(templatesStr);
            }
            sb.append("&v=").append(cacheBusterVersion);

			sb.append("\n\n#javascript files\n");

			if (debugStr != null && (debugStr.equals(Boolean.TRUE.toString()) || debugStr.equals("1"))) {
				cout = new CharArrayWriter(4096 << 2); // 16K buffer to start
				String[] allPackages = "Startup1_1,Startup1_2,Boot,Startup2,CalendarCore,Calendar,CalendarAppt,ContactsCore,Contacts,MailCore,Mail,BriefcaseCore,Briefcase,PreferencesCore,Preferences,TasksCore,Tasks,Extras,Share,Zimlet,ZimletApp,Alert,ImportExport,BrowserPlus,Voicemail".split(",");
				for(String name: allPackages) {
					File file = new File(rootDir,"js/" + name + ".appcache");
					preprocess(file, cout, null, null, null, null, null, requestedLocale);
				}
				sb.append("\n");
				sb.append((cout.toString().replaceAll("<%=contextPath%>",appContextPath)).replaceAll("<%=vers%>", cacheBusterVersion));
				//TODO find a way to get this template files list
				sb.append("\n").append(appContextPath).append("/templates/abook/Contacts.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/calendar/Appointment.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/calendar/Calendar.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/data/ImportExport.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/dwt/Widgets.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/mail/Message.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/prefs/Options.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/prefs/Pages.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/prefs/Widgets.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/share/App.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/share/Dialogs.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/share/Quota.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/share/Widgets.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/tasks/Tasks.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/voicemail/Voicemail.template.js?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/templates/zimbra/Widgets.template.js?v=").append(cacheBusterVersion);

				sb.append("\n");

			} else {
				//hardcoded prod deploy manifest js files
				//TODO find which apps have been enabled and add only those manifest files here.
				sb.append("\n").append(appContextPath).append("/js/Startup1_1_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/Startup1_2_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/MailCore_all.js.zgz?v=").append(cacheBusterVersion);
                sb.append("\n").append(appContextPath).append("/js/Mail_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/Startup2_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/CalendarCore_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/Calendar_all.js.zgz?v=").append(cacheBusterVersion);
				//sb.append("\n").append(appContextPath).append("/js/Share_all.js.zgz").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/Zimlet_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/ContactsCore_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/Extras_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/Contacts_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n").append(appContextPath).append("/js/TasksCore_all.js.zgz?v=").append(cacheBusterVersion);
				sb.append("\n");
			}
			sb.append("\nNETWORK:\n").append("*\n");
			return sb.toString();
		}
		return cout.toString();
	}

	static void addLocaleFiles(List<File> files, Locale requestedLocale,
							   File dir, String filename, String ext) {
		Locale defaultLocale = Locale.getDefault();
		Locale[] locales = defaultLocale.equals(requestedLocale)
						 ? new Locale[]{ requestedLocale }
						 : new Locale[]{ defaultLocale, requestedLocale };
		if (ZimbraLog.webclient.isDebugEnabled()) {
			ZimbraLog.webclient.debug("addLocaleFiles: files="+files+", reqLoc="+requestedLocale+", dir="+dir+", fname="+filename+", ext="+ext);
		}
		for (Locale locale : locales) {
			// NOTE: Overrides are loaded in backwards order from
			//	   resource bundles because CSS/JS that appears
			//	   later in the file take precedence. This is
			//	   different than resource bundles where the
			//	   first entry seen takes precedence.
			String language = locale.getLanguage();
			File langFile = new File(dir, filename+"_"+language+ext);
			if (langFile.exists()) {
				if (ZimbraLog.webclient.isDebugEnabled()) {
					ZimbraLog.webclient.debug("  adding file: "+langFile.getAbsolutePath());
				}
				files.add(langFile);
			}
			String country = locale.getCountry();
			if (country != null && country.length() > 0) {
				File langCountryFile = new File(dir, filename+"_"+language+"_"+country+ext);
				if (langCountryFile.exists()) {
					if (ZimbraLog.webclient.isDebugEnabled()) {
						ZimbraLog.webclient.debug("  adding file: "+langCountryFile.getAbsolutePath());
					}
					files.add(langCountryFile);
				}
				String variant = locale.getVariant();
				if (variant != null && variant.length() > 0) {
					File langCountryVariantFile = new File(dir, filename+"_"+language+"_"+country+"_"+variant+ext);
					if (langCountryVariantFile.exists()) {
						if (ZimbraLog.webclient.isDebugEnabled()) {
							ZimbraLog.webclient.debug("  adding file: "+langCountryVariantFile.getAbsolutePath());
						}
						files.add(langCountryVariantFile);
					}
				}
			}
		}
	}

	static boolean includeTemplates(List<File> templates, boolean split) {
		boolean include = true;
		if (split) {
			long size = 0;
			for (File file : templates) {
				// TODO: optimize
				File template = new File(file.getParentFile(), file.getName()+".js");
				size += template.exists() ? template.length() : 0;
			}
			include = size <= MAX_INCLUDED_TEMPLATES_SIZE;
		}
		return include;
	}

	static void preprocess(File file,
						   Writer writer,
						   Map<String, String> macros,
						   Manifest manifest,
						   String commentStart,
						   String commentContinue,
						   String commentEnd,
						   Locale locale)
			throws IOException {
		String filename = file.getName().replaceAll("\\..*?$", "");
		String ext = file.getName().replaceAll("^.*(\\..*?)$", "$1");

		// get list of files
		List<File> files = new LinkedList<File>();
		files.add(file);
		addLocaleFiles(files, locale, file.getParentFile(), filename, ext);

		// print the files in order
		PrintWriter out = new PrintWriter(writer);
		for (File ifile : files) {
			preprocess0(ifile , out, macros, manifest, commentStart, commentContinue, commentEnd);
		}
	}

	static void preprocess0(File file,
						   PrintWriter out,
						   Map<String, String> macros,
						   Manifest manifest,
						   String commentStart,
						   String commentContinue,
						   String commentEnd)
			throws IOException {
		if (commentStart != null) {
			out.println(commentStart);
			out.print(commentContinue);
			out.println("File: " + file.getAbsolutePath().replaceAll("^.*/webapps/",""));
			out.println(commentEnd);
			out.println();
		}

		BufferedReader in = new BufferedReader(new FileReader(file));
		Stack<Boolean> ignore = new Stack<Boolean>();
		ignore.push(false);
		String line;
		while ((line = in.readLine()) != null) {
			Matcher ifdef = RE_IFDEF.matcher(line);
			if (ifdef.matches()) {
//				out.print(commentStart);
//				out.print("Info: "+line);
//				out.println(commentEnd);
				String macroName = ifdef.group(1);
				ignore.push(ignore.peek() || macros.get(macroName) == null);
				continue;
			}
			Matcher ifndef = RE_IFNDEF.matcher(line);
			if (ifndef.matches()) {
//				out.print(commentStart);
//				out.print("Info: "+line);
//				out.println(commentEnd);
				String macroName = ifndef.group(1);
				ignore.push(ignore.peek() || macros.get(macroName) != null);
				continue;
			}
			Matcher endif = RE_ENDIF.matcher(line);
			if (endif.matches()) {
//				out.print(commentStart);
//				out.print("Info: "+line);
//				out.println(commentEnd);
				ignore.pop();
				continue;
			}
			Matcher elseMatcher = RE_ELSE.matcher(line);
			if (elseMatcher.matches()) {
//				out.print(commentStart);
//				out.print("Info: "+line);
//				out.println(commentEnd);
				boolean ignoring = ignore.pop();
				ignore.push(!ignoring);
				continue;
			}
			if (ignore.peek()) {
				continue;
			}

			if (manifest != null) {
				line = manifest.replace(line);
			}
			out.println(line);
		}
		in.close();

		out.flush();
	}

	//
	// Private static functions
	//

	private String getServerName(HttpServletRequest req) {
		String serverName = req.getParameter(P_CUSTOMER_DOMAIN);

		if(serverName==null || serverName.trim().length() == 0)
			serverName = getServletConfig().getInitParameter(P_SERVER_NAME);

		return serverName != null ? serverName.trim() : HttpUtil.getVirtualHost(req);
	}

	/**
	 * Return the request URI without any path parameters.
	 * We do this because we are only concerned with the type and
	 * filenames that we need to aggregate and return. And various
	 * web containers may insert the jsessionid path parameter to
	 * URIs returned by <code>getRequestURI</code> if no session
	 * ID cookie has been set.
	 *
	 * @param req The HTTP request
	 * @return Request URI
	 */
	private static String getRequestURI(HttpServletRequest req) {
		String servletPath = req.getParameter(P_SERVLET_PATH);
		if (servletPath == null) servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();
		return pathInfo != null ? servletPath + pathInfo : servletPath;
	}

	private static Cookie getCookie(HttpServletRequest req, String name) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
	}

	private static String getMacroNames(Set<String> mnames) {
		Set<String> snames = new TreeSet<String>(mnames);
		StringBuilder str = new StringBuilder();
		for (String mname : snames) {
			str.append(mname);
			str.append(' ');
		}
		return str.toString().trim();
	}

	private String getSkin(HttpServletRequest req) {
		String zimbraAdminURL = null;

		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			zimbraAdminURL = (String) envCtx.lookup("adminUrl");
		} catch (NamingException ne) {
		}
		if (zimbraAdminURL == null) {
			zimbraAdminURL = "/zimbraAdmin";
		}

		String defaultSkinPara = null;
		String defaultCookiePara = null;
		String contentPath = req.getContextPath();
		if (contentPath != null && contentPath.equalsIgnoreCase(zimbraAdminURL)) {
		defaultSkinPara = P_DEFAULT_ADMIN_SKIN;
				defaultCookiePara = C_ADMIN_SKIN;
		} else {
		defaultSkinPara = P_DEFAULT_SKIN;
		defaultCookiePara = C_SKIN;
		}

		String skin = req.getParameter(P_SKIN);
		if (skin == null) {
			Cookie cookie;
			cookie = getCookie(req, defaultCookiePara);
			skin = cookie != null ? cookie.getValue() : getServletContext().getInitParameter(defaultSkinPara);
		}
        if (skin != null) {
            skin = skin.replaceAll("[^A-Za-z0-9]", "");
        }
        try {
		    File manifest = new File(getServletContext().getRealPath("/skins/"+skin+"/"+SKIN_MANIFEST));
            if (!manifest.exists()) {
                skin = getServletContext().getInitParameter(defaultSkinPara);
            }
        }
        catch(NullPointerException e) {
            if (ZimbraLog.webclient.isDebugEnabled()) {
                ZimbraLog.webclient.debug("DEBUG: cannot get skin file " + skin);
            }
            skin = getServletContext().getInitParameter(defaultSkinPara);
        }

		return StringUtil.escapeHtml(skin);
	}

	private static String getContentType(String uri) {
		int index = uri.lastIndexOf('/');
		if (index != -1) {
			uri = uri.substring(0, index);
		}
		index = uri.lastIndexOf('/');
		String key = index != -1 ? uri.substring(index + 1) : "plain";
		String type = TYPES.get(key);
		return type != null ? type : TYPES.get("plain");
	}

	private static String getUserAgent(HttpServletRequest req) {
		String agent = req.getParameter(P_USER_AGENT);
		if (agent == null) {
			agent = req.getHeader(H_USER_AGENT);
		}
		return agent;
	}

	private static Map<String, String> parseUserAgent(String agent) {
		Map<String, String> macros = new HashMap<String, String>();

		// state
		double browserVersion = -1.0;
		double geckoDate = 0;
		double mozVersion = -1;
		double webKitVersion = -1;
		double tridentVersion = -1;
		boolean isMac = false;
		boolean isWindows = false;
		boolean isLinux = false;
		boolean isNav = false;
		boolean isIE = false;
		boolean isNav4 = false;
		boolean trueNs = false;
		boolean isNav6 = false;
		boolean isNav6up = false;
		boolean isNav7 = false;
		boolean isIE3 = false;
		boolean isIE4 = false;
		boolean isIE4up = false;
		boolean isIE5 = false;
		boolean isIE5_5 = false;
		boolean isIE5up = false;
		boolean isIE5_5up = false;
		boolean isIE6 = false;
		boolean isIE6up = false;
		boolean isIE7 = false;
		boolean isIE7up = false;
		boolean isIE8 = false;
		boolean isIE8up = false;
		boolean isIE9 = false;
		boolean isIE9up = false;
		boolean isIE10 = false;
		boolean isIE10up = false;
		boolean isModernIE = false;
		boolean isFirefox = false;
		boolean isFirefox1up = false;
		boolean isFirefox1_5up = false;
		boolean isFirefox4up = false;
		boolean isMozilla = false;
		boolean isMozilla1_4up = false;
		boolean isSafari = false;
		boolean isSafari2 = false;
		boolean isSafari2up = false;
		boolean isSafari3 = false;
		boolean isSafari5up = false;
		boolean isChrome = false;
		boolean isChrome4up = false;
		boolean isTrident = false;
		boolean isGeckoBased = false;
		boolean isGecko1_8up = false;
		boolean isGecko2up = false;
		boolean isWebKitBased = false;
		boolean isOpera = false;
		boolean isIPhone = false;

		// parse user agent
		if (agent == null) agent = "";
		String agt = agent.toLowerCase();
		StringTokenizer agtArr = new StringTokenizer(agt, " ;()");
		int index = -1;
		boolean isSpoofer = false;
		boolean isWebTv = false;
		boolean isHotJava = false;
		boolean beginsWithMozilla = false;
		boolean isCompatible = false;

		if (agtArr.hasMoreTokens()) {
			String token = agtArr.nextToken();
			Pattern pattern = Pattern.compile("\\s*mozilla");
			Matcher mozilla = pattern.matcher(token);
			if (mozilla.find()) {
				index = mozilla.start();
				beginsWithMozilla = true;
				browserVersion = parseFloat(token.substring(index + 8));
				isNav = true;
			}
			do {
				if (token.indexOf("compatible") != -1) {
					isCompatible = true;
					isNav = false;
				} else if ((token.indexOf("opera")) != -1) {
					isOpera = true;
					isNav = false;
					if (agtArr.hasMoreTokens()) {
						browserVersion = parseVersion(agtArr.nextToken());
					}
				} else if ((token.indexOf("spoofer")) != -1) {
					isSpoofer = true;
					isNav = false;
				} else if ((token.indexOf("webtv")) != -1) {
					isWebTv = true;
					isNav = false;
				} else if ((token.indexOf("iphone")) != -1) {
					isIPhone = true;
				} else if ((token.indexOf("hotjava")) != -1) {
					isHotJava = true;
					isNav = false;
				} else if (token.indexOf("msie") != -1) {
					isIE = true;
					if (agtArr.hasMoreTokens()) {
						browserVersion = parseVersion(agtArr.nextToken());
					}
				} else if ((index = token.indexOf("trident/")) != -1) {
					isTrident = true;
					tridentVersion = parseFloat(token.substring(index + 8));
				} else if ((index = token.indexOf("gecko/")) != -1) {
					isGeckoBased = true;
					geckoDate = parseFloat(token.substring(index + 6));
				} else if ((index = token.indexOf("applewebkit/")) != -1) {
					isWebKitBased = true;
					webKitVersion = parseFloat(token.substring(index + 12));
				} else if ((index = token.indexOf("rv:")) != -1) {
					mozVersion = parseVersion(token.substring(index + 3));
					browserVersion = mozVersion;
				} else if ((index = token.indexOf("firefox/")) != -1) {
					isFirefox = true;
					browserVersion = parseVersion(token.substring(index + 8));
				} else if ((index = token.indexOf("netscape6/")) != -1) {
					trueNs = true;
					browserVersion = parseVersion(token.substring(index + 10));
				} else if ((index = token.indexOf("netscape/")) != -1) {
					trueNs = true;
					browserVersion = parseVersion(token.substring(index + 9));
				} else if ((index = token.indexOf("safari/")) != -1) {
					isSafari = true;
					browserVersion = parseVersion(token.substring(index + 7));
				} else if ((index = token.indexOf("chrome/")) != -1) {
					isChrome = true;
					browserVersion = parseVersion(token.substring(index + 7));
				} else if (token.indexOf("windows") != -1) {
					isWindows = true;
				} else if ((token.indexOf("macintosh") != -1) || (token.indexOf("mac_") != -1)) {
					isMac = true;
				} else if (token.indexOf("linux") != -1) {
					isLinux = true;
				}

				token = agtArr.hasMoreTokens() ? agtArr.nextToken() : null;
			} while (token != null);

			isIE = (isIE && !isOpera);
			isIE3 = (isIE && (browserVersion < 4));
			isIE4 = (isIE && (browserVersion == 4.0));
			isIE4up = (isIE && (browserVersion >= 4));
			isIE5 = (isIE && (browserVersion == 5.0));
			isIE5_5 = (isIE && (browserVersion == 5.5));
			isIE5up = (isIE && (browserVersion >= 5.0));
			isIE5_5up = (isIE && (browserVersion >= 5.5));
			isIE6 = (isIE && (browserVersion == 6.0));
			isIE6up = (isIE && (browserVersion >= 6.0));
			isIE7 = (isIE && (browserVersion == 7.0));
			isIE7up = (isIE && (browserVersion >= 7.0));
			isIE8 = (isIE && (browserVersion == 8.0));
			isIE8up = (isIE && (browserVersion >= 8.0));
			isIE9 = (isIE && (browserVersion == 9.0));
			isIE9up = (isIE && (browserVersion >= 9.0));
			isIE10 = (isIE && (browserVersion == 10.0));
			isIE10up = (isIE && (browserVersion >= 10.0));

			isModernIE = (!isIE && isTrident &&
			              mozVersion == browserVersion &&
			              mozVersion > 0);

			// Note: Opera and WebTV spoof Navigator. We do strict client detection.
			isNav = (beginsWithMozilla && !isSpoofer && !isCompatible && !isOpera && !isWebTv && !isHotJava && !isSafari && !isChrome);
			isNav4 = (isNav && (browserVersion == 4) && (!isIE));
			isNav6 = (isNav && trueNs && (browserVersion >= 6.0) && (browserVersion < 7.0));
			isNav6up = (isNav && trueNs && (browserVersion >= 6.0));
			isNav7 = (isNav && trueNs && (browserVersion == 7.0));

			isMozilla = ((isNav && mozVersion > -1.0 && isGeckoBased && (geckoDate != 0)));
			isMozilla1_4up = (isMozilla && (mozVersion >= 1.4));
			isFirefox = ((isMozilla && isFirefox));
			isFirefox1up = (isFirefox && browserVersion >= 1.0);
			isFirefox1_5up = (isFirefox && browserVersion >= 1.5);
			isFirefox4up = (isFirefox && browserVersion >= 4);
			isGecko1_8up = (isGeckoBased && browserVersion >= 1.8);
			isGecko2up = (isGeckoBased && browserVersion >= 2);

			isSafari2 = (isSafari && browserVersion == 2.0);
			isSafari2up = (isSafari && browserVersion >= 2);
			isSafari3 = (isSafari && browserVersion == 3.0);
			isSafari5up = (isSafari && browserVersion >= 5);

			isChrome4up = (isChrome && browserVersion >= 4);

			// operating systems
			define(macros, "LINUX", isLinux);
			define(macros, "MACINTOSH", isMac);
			define(macros, "WINDOWS", isWindows);

			// browser variants
			define(macros, "CHROME", isChrome);
			define(macros, "CHROME_4_OR_HIGHER", isChrome4up);
			define(macros, "FIREFOX", isFirefox);
			define(macros, "FIREFOX_1_OR_HIGHER", isFirefox1up);
			define(macros, "FIREFOX_1_5_OR_HIGHER", isFirefox1_5up);
			define(macros, "FIREFOX_4_OR_HIGHER", isFirefox4up);
			define(macros, "GECKO", isGeckoBased);
			define(macros, "GECKO_1_8_OR_HIGHER", isGecko1_8up);
			define(macros, "GECKO_2_OR_HIGHER", isGecko2up);
			define(macros, "HOTJAVA", isHotJava);
			define(macros, "IPHONE", isIPhone);
			define(macros, "MOZILLA", isMozilla);
			define(macros, "MOZILLA_1_4_OR_HIGHER", isMozilla1_4up);
			define(macros, "MSIE", isIE);
			define(macros, "MSIE_3", isIE3);
			define(macros, "MSIE_4", isIE4);
			define(macros, "MSIE_4_OR_HIGHER", isIE4up);
			define(macros, "MSIE_5", isIE5);
			define(macros, "MSIE_5_OR_HIGHER", isIE5up);
			define(macros, "MSIE_5_5", isIE5_5);
			define(macros, "MSIE_5_5_OR_HIGHER", isIE5_5up);
			define(macros, "MSIE_6", isIE6);
			define(macros, "MSIE_6_OR_HIGHER", isIE6up);
			define(macros, "MSIE_LOWER_THAN_7", isIE && !isIE7up);
			define(macros, "MSIE_7", isIE7);
			define(macros, "MSIE_7_OR_HIGHER", isIE7up);
			define(macros, "MSIE_8", isIE8);
			define(macros, "MSIE_8_OR_HIGHER", isIE8up);
			define(macros, "MSIE_LOWER_THAN_9", isIE && !isIE9up);
			define(macros, "MSIE_9", isIE9);
			define(macros, "MSIE_9_OR_HIGHER", isIE9up);
			define(macros, "MSIE_LOWER_THAN_10", isIE && !isIE10up);
			define(macros, "MSIE_10", isIE10);
			define(macros, "MSIE_10_OR_HIGHER", isIE10up);
			define(macros, "MODERN_IE", isModernIE);
			define(macros, "NAVIGATOR", isNav);
			define(macros, "NAVIGATOR_4", isNav4);
			define(macros, "NAVIGATOR_6", isNav6);
			define(macros, "NAVIGATOR_6_OR_HIGHER", isNav6up);
			define(macros, "NAVIGATOR_7", isNav7);
			define(macros, "NAVIGATOR_COMPATIBLE", isCompatible);
			define(macros, "OPERA", isOpera);
			define(macros, "SAFARI", isSafari);
			define(macros, "SAFARI_2", isSafari2);
			define(macros, "SAFARI_2_OR_HIGHER", isSafari2up);
			define(macros, "SAFARI_3", isSafari3);
			define(macros, "SAFARI_5_OR_HIGHER", isSafari5up);
			define(macros, "WEBKIT", isWebKitBased);
			define(macros, "WEBTV", isWebTv);
		}

		return macros;

	}

	private static double parseFloat(String s) {
		try {
			return Float.parseFloat(s);
		}
		catch (NumberFormatException e) {
			// ignore
		}
		return -1.0;
	}

	private static double parseVersion(String s) {
		Matcher matcher = RE_VERSION.matcher(s);
		if (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			return parseFloat(s.substring(start, end));
		}
		return parseFloat(s);
	}

	private static void define(Map<String, String> macros, String mname, boolean defined) {
		if (defined) {
			macros.put(mname, Boolean.TRUE.toString());
		}
	}

	//
	// Classes
	//

	static class Manifest {

		//
		// Constants
		//

		public static final String S_SKIN_FOREGROUND_COLOR = "TxtC";
		public static final String S_SKIN_BACKGROUND_COLOR = "AppC";
		public static final String S_SKIN_SECONDARY_COLOR = "AltC";
		public static final String S_SKIN_SELECTION_COLOR = "SelC";

		public static final String S_SKIN_LOGO_LOGIN_BANNER = "LoginBannerImg";
		public static final String S_SKIN_LOGO_APP_BANNER = "AppBannerImg";
		public static final String S_SKIN_LOGO_URL = "LogoURL";

		public static final String S_SKIN_FAVICON = "FavIcon";

		private static final String S_HELP_ADMIN_URL = "HelpAdminURL";
		private static final String S_HELP_ADVANCED_URL = "HelpAdvancedURL";
		private static final String S_HELP_DELEGATED_URL = "HelpDelegatedURL";
		private static final String S_HELP_STANDARD_URL = "HelpStandardURL";

		private static final String S_APP_CONTEXT_PATH = "AppContextPath";
		private static final String S_JS_VERSION = "jsVersion";

		private static final String E_SKIN = "skin";
		private static final String E_SUBSTITUTIONS = "substitutions";
		private static final String E_CSS = "css";
		private static final String E_HTML = "html";
		private static final String E_SCRIPT = "script";
		private static final String E_TEMPLATES = "templates";
		private static final String E_FILE = "file";
		private static final String E_COMMON = "common";
		private static final String E_STANDARD = "standard";
		private static final String E_ADVANCED = "advanced";

		private static final Pattern RE_TOKEN = Pattern.compile("@.+?@");
		private static final Pattern RE_SKIN_METHOD = Pattern.compile("@(\\w+)\\((.*?)\\)@");


		//
		// Data
		//

		private String client;

		private List<File> substList = new LinkedList<File>();
		private List<File> cssList = new LinkedList<File>();
		private List<File> htmlList = new LinkedList<File>();
		private List<File> scriptList = new LinkedList<File>();
		private List<File> templateList = new LinkedList<File>();
		private List<File> resourceList = new LinkedList<File>();
		private Map<String, String> macros;

		private Properties substitutions = new Properties();

		//
		// Constructors
		//

		public Manifest(File manifestFile, Map<String, String> macros, String client,
						Map<String,String> substOverrides, Locale locale)
				throws IOException {
			this.client = client;
			// rememeber the macros passed in (for skin substitution functions)
			this.macros = macros;

			// load document
			Document document;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				factory.setValidating(false);
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(manifestFile);
			}
			catch (IOException e) {
				throw e;
			}
			catch (Exception e) {
				throw new IOException(e.getMessage());
			}

			// gather files
			File skinDir = manifestFile.getParentFile();
			getFiles(document, E_SUBSTITUTIONS, skinDir, substList);
			getFiles(document, E_CSS, skinDir, cssList);
			getFiles(document, E_HTML, skinDir, htmlList);
			getFiles(document, E_SCRIPT, skinDir, scriptList);
			getFiles(document, E_TEMPLATES, skinDir, templateList);

			// process substitutions
			for (File file : substList) {
				if (ZimbraLog.webclient.isDebugEnabled()) ZimbraLog.webclient.debug("DEBUG: subst file = " + file);
				try {
					CharArrayWriter out = new CharArrayWriter(4096); // 4K
					SkinResources.preprocess(file, out, macros, null, "#", "#", "#", locale);
					String content = out.toString();
					// NOTE: properties files should be ISO-Latin-1 with
					//	   escaped Unicode char sequences.
					byte[] bytes = content.getBytes("ISO-8859-1");

					InputStream in = new ByteArrayInputStream(bytes);

					substitutions.load(in);
					if (substOverrides != null) {
						for (String key : substOverrides.keySet()) {
							String value = substOverrides.get(key);
							if (value == null) continue;
							substitutions.setProperty(key, value);
							if (key.equals(S_SKIN_BACKGROUND_COLOR) || key.equals(S_SKIN_FOREGROUND_COLOR) ||
								key.equals(S_SKIN_SELECTION_COLOR)) {
								try {
									Color color = getColor(null, value);
									for (int i = 5; i < 100; i += 5) {
										float delta = (float)i / (float)100;
										substitutions.setProperty(
											key + "-" + (i < 10 ? "0" : "") + i, lightenColor(color, delta)
										);
										substitutions.setProperty(
											key + "+" + (i < 10 ? "0" : "") + i, darkenColor(color, delta)
										);
									}
								}
								catch (Exception e) {
									// ignore
								}
							}
						}
					}
				} catch (OutOfMemoryError e) {
				Zimbra.halt("out of memory", e);
				} catch (Throwable t) {
					ZimbraLog.webclient.debug("ERROR loading subst file: " + file);
				}

				if (ZimbraLog.webclient.isDebugEnabled())
					ZimbraLog.webclient.debug("DEBUG: _SkinName_ = " + substitutions.getProperty("_SkinName_"));
			}

			Stack<String> stack = new Stack<String>();
			Enumeration substKeys = substitutions.propertyNames();
			if (ZimbraLog.webclient.isDebugEnabled())
				ZimbraLog.webclient.debug("DEBUG: InsetBg (before) = " + substitutions.getProperty("InsetBg"));
			while (substKeys.hasMoreElements()) {
				stack.removeAllElements();

				String substKey = (String) substKeys.nextElement();
				if (substKey.equals("InsetBg")) {
					if (ZimbraLog.webclient.isDebugEnabled())
						ZimbraLog.webclient.debug("DEBUG: InsetBg (loop) = " + substitutions.getProperty("InsetBg"));
				}
				getProperty(stack, substKey);
			}
			if (ZimbraLog.webclient.isDebugEnabled())
				ZimbraLog.webclient.debug("DEBUG: InsetBg (after) = " + substitutions.getProperty("InsetBg"));

			if (ZimbraLog.webclient.isDebugEnabled())
				ZimbraLog.webclient.debug("DEBUG: _SkinName_ = " + substitutions.getProperty("_SkinName_"));
		} // <init>(File,Map<String,String>,String,String)

		//
		// Public methods
		//

		// lists

		public List<File> substitutionFiles() {
			return substList;
		}

		public List<File> cssFiles() {
			return cssList;
		}

		public List<File> htmlFiles() {
			return htmlList;
		}

		public List<File> scriptFiles() {
			return scriptList;
		}

		public List<File> templateFiles() {
			return templateList;
		}

		public List<File> resourceFiles() {
			return resourceList;
		}

		public List<File> getFiles(String type) {
			if (type.equals(SkinResources.T_CSS) || type.equals(SkinResources.T_APPCACHE)) return cssFiles();
			if (type.equals(SkinResources.T_HTML)) return htmlFiles();
			if (type.equals(SkinResources.T_JAVASCRIPT)) return scriptFiles();
			return null;
		}

		// operations

		public String replace(String s) {
			return replace(null, s);
		}

		//
		// Private methods
		//

		private boolean isBrowser(String name) {
			String booleanStr = macros.get(name);
			return (booleanStr != null && booleanStr.equalsIgnoreCase("true"));
		}

		public String getProperty(Stack<String> stack, String pname) {
			// check for cycles
			if (stack != null) {
				for (String s : stack) {
					if (s.equals(pname)) {
						return "/*ERR:" + pname + "*/";
					}
				}
				stack.push(pname);
			}

			// substitute and return
			String pvalue = substitutions.getProperty(pname);
			pvalue = replace(stack, pvalue);
			if (stack != null) {
				stack.pop();
			}
			substitutions.setProperty(pname, pvalue);
			return pvalue;
		}

		private String replace(Stack<String> stack, String s) {
			if (s == null) {
				return "";
			}

			s = this.handleMethodCalls(stack, s);

			Matcher matcher = RE_TOKEN.matcher(s);
			if (!matcher.find()) {
				return s;
			}

			StringBuilder str = new StringBuilder();
			int offset = 0;
			do {
				int start = matcher.start();
				int end = matcher.end();

				String substKey = s.substring(start + 1, end - 1);
				String substValue = getProperty(stack, substKey);
				if (substValue != null) {
					str.append(s.substring(offset, start));
					str.append(substValue);
				} else {
					str.append("/*");
					str.append(s.substring(offset, end));
					str.append("*/");
				}

				offset = end;
			} while (matcher.find(offset));
			str.append(s.substring(offset));

			return str.toString();
		}


		// handle a method call in a skin replacemented file
		//	syntax:	@methodName(param,param,param)@
		private String handleMethodCalls(Stack<String> stack, String s) {
			Matcher matcher = RE_SKIN_METHOD.matcher(s);
			if (!matcher.find()) return s;

			StringBuilder str = new StringBuilder();
			int offset = 0;
			do {
				int start = matcher.start();
				str.append(s.substring(offset, start));

				String operation = matcher.group(1).toLowerCase();
				String[] params = matcher.group(2).split(" *, *");

				try {
					String result;
					// "darken" or "-"
					if (operation.equals("darken") || operation.equals("+")) {
						result = outputDarkerColor(stack, params);

					// "lighten" or "-"
					} else if (operation.equals("lighten") || operation.equals("-")) {
						result = outputLighterColor(stack, params);

					// "invert"
					} else if (operation.equals("invert")) {
						result = outputInvertColor(stack, params);

					// "border"
					} else if (operation.equals("border")) {
						result = outputBorder(stack, params);

					// "grad"
					} else if (operation.equals("grad")){
						result = outputGrad(stack, params);

					// "image" or "img"
					} else if (operation.equals("image") || operation.equals("img")) {
						result = outputImage(stack, params);

					// "cssShadow"
					} else if (operation.equals("cssshadow")) {
						result = outputCssShadow(stack, params);

					// "cssText" or "cssTextProp[ertie]s"
					} else if (operation.indexOf("csstext") == 0) {
						result = outputCssTextProperties(stack, params);

					// "cssValue"
					} else if (operation.indexOf("cssvalue") == 0) {
						result = outputCssValue(stack, params);

					// "css" or "cssProp[ertie]s"
					} else if (operation.indexOf("css") == 0) {
						result = outputCssProperties(stack, params);

					// "round" or "roundCorners"
					} else if (operation.indexOf("round") == 0) {
						result = outputRoundCorners(stack, params);

					// "opacity"
					} else if (operation.equals("opacity")) {
						result = outputOpacity(stack, params);

					} else {
						throw new IOException("Couldn't understand operation "+matcher.group(1)+".");
					}

					// and output the results in place
					str.append(result);

				} catch (IOException e) {
					str.append("/***"+e.getMessage()+"***/");
				}

				offset = matcher.end();
			} while (matcher.find(offset));
			str.append(s.substring(offset));
			return str.toString();
		}


		//
		//
		//	Color routines
		//
		//

		//
		// replace occurances of @Darken(color,percent)@ with the adjusted color
		//
		private String outputDarkerColor(Stack<String> stack, String[] params) throws IOException {
			Color color = this.getColor(stack, params[0]);
			float delta = (Float.parseFloat(params[1]) / 100);
			return this.darkenColor(color, delta);
		}

		//
		// replace occurances of @Lighten(color,percent)@ with the adjusted color
		//
		private String outputLighterColor(Stack<String> stack, String[] params) throws IOException {
			Color color = this.getColor(stack, params[0]);
			float delta = (Float.parseFloat(params[1]) / 100);
			return this.lightenColor(color, delta);
		}


		// darken a color object by given fraction, returns a hex color string
		private String darkenColor(Color color, float delta) {
			return colorToColorString(
						new Color(	darken(color.getRed(), delta),
									darken(color.getGreen(), delta),
									darken(color.getBlue(), delta)
						)
					);
		}

		// lighten a color object by given fraction, returns a hex color string
		private String lightenColor(Color color, float delta) {
			return colorToColorString(
						new Color(	lighten(color.getRed(), delta),
									lighten(color.getGreen(), delta),
									lighten(color.getBlue(), delta)
						)
					);
		}


		private int lighten(int value, float delta) {
			return (int) Math.max(0, Math.min(255, value + (255 - value) * delta));
		}

		private int darken(int value, float delta) {
			return (int) Math.max(0, Math.min(255, value * (1 - delta)));
		}

		//
		// replace occurances of @invert(color)@ with the inverted color
		//
		private String outputInvertColor(Stack<String> stack, String[] params) throws IOException {
			Color color = this.getColor(stack, params[0]);
			return this.invertColor(color);
		}

		// invert color object
		private String invertColor(Color color) {
			return colorToColorString( new Color( Math.abs(color.getRed() - 255),
                        				Math.abs(color.getGreen() - 255),
							Math.abs(color.getBlue() - 255))
	 					);
		}

		// given a color (either '#fffff' or 'ffffff' or a substitution),
		//	return a Color object that corresponds to that color.
		//
		// TODO: make this handle rgb(#,#,#) and 'ccc' or '#ccc'
		private Color getColor(Stack<String> stack, String colorStr) throws IOException {
			// if there is a space in there, strip everything after it
			//	(to remove '!important' and stuff like that
			Color color = Colors.getColor(colorStr.replaceAll(" .*$", ""));
			if (color == null) {
				String prop = getProperty(stack, colorStr);
				if (prop != null) {
					color = Colors.getColor(prop);
				}
			}
			if (color == null) {
				throw new IOException("Unknown color:" + colorStr);
			}
			return color;
		}

		private String colorToColorString(Color color) {
			if (color == null) return "NULL_COLOR";
			int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
			StringBuilder str = new StringBuilder("#");
			for (int val : rgb) {
				if (val < 16) str.append("0");
				str.append(Integer.toHexString(val));
			}
			return str.toString();
		}



		//
		//
		//	CSS manipulation routines
		//
		//

		//
		// replace occurances of @border(size,type,color,colorDelta)@ with the CSS for the border
		//
		//	TODO: 	if more than 1 px, do pretty borders on Moz?
		//
		private String outputBorder(Stack<String> stack, String[] params) throws IOException {
			String size = (params.length > 0 ? params[0] : "1px");
			String type = (params.length > 1 ? params[1].toLowerCase() : "solid");
			Color color = (params.length > 2 ? this.getColor(stack, params[2]) : Color.decode("#fffff"));
			float delta = (float) (params.length > 3 ? (Float.parseFloat(params[3]) / 100) : .25);

			String sizeStr = (size.indexOf(" ") == -1 ? " " + size + ";" : "; border-width:" + size + ";");

			if (type.equals("transparent")) {
				if (isBrowser("MSIE_LOWER_THAN_7")) {
					return "margin:" + size +";border:0px;";
				} else {
					return "border:solid transparent" + sizeStr;
				}
			} else if (type.equals("solid")) {
				return "border:solid " + colorToColorString(color) + sizeStr;

			} else if (type.equals("inset") || type.equals("outset")) {
				String tlColor = (type.equals("inset") ? darkenColor(color, delta) : lightenColor(color, delta));
				String brColor = (type.equals("inset") ? lightenColor(color, delta) : darkenColor(color, delta));
				return "border:solid" + sizeStr + "border-color:"
							+ tlColor + " " + brColor + " " + brColor + " " + tlColor + ";";
			}
			throw new IOException("border("+type+"): type not understood: use 'transparent', 'solid', 'inset' or 'outset'");
		}

		private String getDataURI(String mimetype, String source)
			throws IOException
		{
			byte[] data = source.getBytes("UTF-8");
			String base64 = DatatypeConverter.printBase64Binary(data);

			return String.format("data:%s;base64,%s", mimetype, base64);
		}

		//
		// replace occurances of @grad(to, from, type)@, @grad(to, from) with the CSS for the cross-browser linear gradient
		// default type is linear-vertical
		//
		private String outputGrad(Stack<String> stack, String[] params) throws IOException {
			String from = (params.length > 0 ? this.colorToColorString(this.getColor(null, params[0])) : null);
			String to = (params.length > 1 ? this.colorToColorString(this.getColor(null, params[1])) : null);
			String type = (params.length > 2 ? params[2] : "linear-vertical");
			String endDirection = "bottom";
			String topLeft = "";
			String result = "background-color:" + from;   // Default
			int gradType = 0;

			if (from == null || to == null)
				throw new IOException("grad(): specify from, to");

			if (type.equals("linear-horizontal")){
				endDirection = "right";
				gradType = 1; // for IE 8 or lower vertical:0, horizontal: 1
				topLeft = "left"; // used for horizontal gradient only
			} else if (!type.equals("linear-vertical")){
				throw new IOException("grad():type not understood: use 'linear-vertical', 'linear-horizontal");
			}

			if (isBrowser("MSIE_9")) {
				String svgsource = "<?xml version=\"1.0\" ?>" +
					"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100%%\" height=\"100%%\" viewBox=\"0 0 1 1\" preserveAspectRatio=\"none\">" +
					"<linearGradient id=\"thegradient\" gradientUnits=\"userSpaceOnUse\" x1=\"0%%\" y1=\"0%%\" x2=\"%1$s\" y2=\"%2$s\">" +
					"<stop offset=\"0%%\" stop-color=\"%3$s\" stop-opacity=\"1\"/>" +
					"<stop offset=\"100%%\" stop-color=\"%4$s\" stop-opacity=\"1\"/>" +
					"</linearGradient>" +
					"<rect x=\"0\" y=\"0\" width=\"1\" height=\"1\" fill=\"url(#thegradient)\" />" +
					"</svg>";

				svgsource = String.format(svgsource,
										  type.equals("linear-vertical") ? "0%" : "100%",
										  type.equals("linear-horizontal") ? "0%" : "100%",
										  from, to);

				result = String.format("background: url(\"%s\");", getDataURI("image/svg+xml", svgsource));
			} else if (isBrowser("MSIE_LOWER_THAN_9")) {
				result = String.format("filter: progid:DXImageTransform.Microsoft.gradient(startColorStr='%s', EndColorStr='%s' , GradientType=%d);", from, to, gradType);
			} else if (isBrowser("FIREFOX")) {
				result = String.format("background-image: -moz-linear-gradient(top %s, %s, %s);",topLeft, from, to);
			} else if (isBrowser("WEBKIT")){
				result = String.format("background-image: -webkit-gradient(linear, left top, %s bottom, to(%s), from(%s)); " +
                        "background-image : -webkit-linear-gradient(%s, %s, %s);",endDirection, from, to, (gradType == 1) ? "left":"top", from, to );
			} else { // All other browsers
				result = String.format("background-image: linear-gradient(to %s, %s, %s);", endDirection, from, to);
			}

			return result;
		}

		//
		// replace occurances of @image(dir, filename.extension, width, height, repeat)@ with the CSS for the image
		//		as a background-image (or filter for PNG's in IE)
		//
		private String outputImage(Stack<String> stack, String[] params) throws IOException {
			String dir = (params.length > 0 ? params[0] : "");
			String name = (params.length > 1 ? params[1] : null);
			String width = (params.length > 2 ? params[2] : null);
			String height = (params.length > 3 ? params[3] : null);
			String repeat = (params.length > 4 ? params[4] : null);

			if (name == null) throw new IOException("image(): specify directory, name, width, height");

			// if there is no extension in the name, assume it's a sub
			if (name.indexOf(".") == -1) {
				name = getProperty(stack, name);
			}
			if (name == null) throw new IOException("image(): specify directory, name, width, height");

			boolean isPNG = (name.toLowerCase().indexOf(".png") > -1);

			dir = (dir == null || dir.equals("") ? "" : getProperty(stack, dir));
			// make sure there's a slash between the directory and the image name
			if (!dir.equals("") && (dir.lastIndexOf("/") != dir.length()-1 || name.indexOf("/") != 0)) {
				dir = dir + "/";
			}

			String url = dir + name;

			if (isPNG && isBrowser("MSIE_LOWER_THAN_7")) {
				return "background-image:none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"',sizingMethod='image');"
							+ (width != null ? "width:"+width+";" : "")
							+ (height != null ? "height:"+height+";" : "");
			} else {
				return "background-image:url(" + url + ");"
							+ (repeat != null ? "background-repeat:"+repeat+";" : "")
							+ (width != null ? "width:"+width+";" : "")
							+ (height != null ? "height:"+height+";" : "");
			}
		}

		//
		// replace occurances of @cssValue(token, property)@ with the css value of that replacement token
		//
		private String outputCssValue(Stack<String> stack, String[] params) throws IOException {
			if (params.length != 2) throw new IOException("cssValue(): pass replacement, property");
			String token = params[0];
			String cssString = getProperty(stack, token);
			if (cssString == "") throw new IOException("cssValue(): '"+token+"' not found");

			Map<String, String> map = parseCSSProperties(cssString);
			return map.get(params[1]);
		}

		//
		// replace occurances of @cssProperties(token, property[..., property])@ with the css name:value pairs of the replacement token
		//
		private String outputCssProperties(Stack<String> stack, String[] params) throws IOException {
			if (params.length < 2) throw new IOException("cssProperties(): pass at least replacement, property");
			String token = params[0];
			String cssString = getProperty(stack, token);
			if (cssString == "") throw new IOException("cssProperties(): '"+token+"' not found");

			StringBuilder output = new StringBuilder();
			Map<String, String> map = parseCSSProperties(cssString);
			for (int i = 1; i < params.length; i++) {
				String value = map.get(params[i]);
				if (value != null) {
					output.append(params[i] + ":" + value + ";");
				}
			}
			return output.toString();
		}

		//
		// replace occurances of @cssTextProperties(token)@ with the CSS-text properties of that replacement token
		//
		private String outputCssTextProperties(Stack<String> stack, String[] params) throws IOException {
			if (params.length == 0) throw new IOException("cssTextProperties(): pass a replacement");
			String token = params[0];
			String[] newParams = {token, "color", "line-height", "text-align", "text-decoration", "white-space",
													"font", "font-family", "font-size", "font-style", "font-weight", "font-variant"
									// skipping the following properties for speed reasons (???)
									//				"direction", "letter-spacing", "text-indent", "text-shadow", "text-transform", "word-spacing"
									//				"font-size-adjust", "font-stretch",
											};
			return outputCssProperties(stack, newParams);
		}

		//
		// replace occurances of @cssShadow(size, color)@ with CSS to show a shadow, specific to the platform
		//
		private String outputCssShadow(Stack<String> stack, String[] params) throws IOException {
			if (isBrowser("SAFARI_3")) {
				String size = (params.length > 1 ? params[0] : "5px");
				String color = (params.length > 1 ? colorToColorString(this.getColor(stack, params[1])) : "#666666");
				return "-webkit-box-shadow:" + size + " " + color + ";";
			}
			return "";
		}

		//
		// replace occurances of @roundCorners(size[ size[ size[ size]]])@ with CSS to round corners, specific to the platform
		//
		private String outputRoundCorners(Stack<String> stack, String[] params) throws IOException {
			boolean isFirefox = isBrowser("FIREFOX");
			boolean isFirefox4up = isBrowser("FIREFOX_4_OR_HIGHER");
			boolean isSafari = isBrowser("SAFARI");
			boolean isSafari5up = isBrowser("SAFARI_5_OR_HIGHER");
			boolean isChrome = isBrowser("CHROME");
			boolean isChrome4up = isBrowser("CHROME_4_OR_HIGHER");

			String propName;

			// Pick out browsers that require prefixes for rounding --
			// all other browsers either support the W3C syntax or
			// safely disregard it.
			//
			// https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius
			if (isFirefox && !isFirefox4up) {
				propName = "-moz-border-radius:";
			} else if ((isChrome && !isChrome4up) || (isSafari && !isSafari5up)) {
				propName = "-webkit-border-radius:";
			} else {
				propName = "border-radius:";
			}

			String size = (params.length > 0 ? params[0] : null);
			if (size == null || size.equals("") )
				return propName + "3px;";  // Default value
			String[] tokens = size.split(" ");
			StringBuffer outStr = new StringBuffer(propName);
			for(int i=0; i<tokens.length; i++){
				String propertyString = (tokens[i].matches("^[a-zA-Z]+")) ? getProperty(stack, tokens[i]) : tokens[i];
				propertyString = (propertyString != null) ? propertyString : tokens[i];
				outStr.append(propertyString).append((i == tokens.length-1) ? ";": " ");
			}

			return outStr.toString();
		}

		//
		// replace occurances of @opacity(percentage)@ with CSS opacity value (correct for each platform)
		//
		//	TODO: does IE7 support regular opacity?   No!
		//
		private String outputOpacity(Stack<String> stack, String[] params) throws IOException {
			float opacity;
			try {
				opacity = Float.parseFloat(params[0]) / 100;
			} catch (Exception e) {
				throw new IOException("opacity(): pass opacity as integer percentage");
			}
			if (isBrowser("MSIE") && !isBrowser("MSIE_9_OR_HIGHER")) {
				return "filter:alpha(opacity=" + ((int)(opacity * 100)) + ");";
			} else {
				return "opacity:"+opacity+";";
			}
		}


		//
		//	given a string of CSS properties, turn them into a name:value map
		//
		private Map<String, String> parseCSSProperties(String cssString) {
			Map<String, String> map = new HashMap<String, String>();

			String[] props = cssString.trim().split("\\s*;\\s*");
			for (int i = 0; i < props.length; i++) {
				String[] prop = props[i].split("\\s*:\\s*");
				if (prop.length == 2) {
					map.put(prop[0], prop[1]);
				}
			}
			return map;
		}


		//
		// Private functions
		//

		private void getFiles(Document document, String ename,
							  File baseDir, List<File> list) {
			Element docElement = getFirstChildElement(document, E_SKIN);
			Element common = getFirstChildElement(docElement, E_COMMON);
			addFiles(common, ename, baseDir, list);
			Element root = getFirstChildElement(docElement, this.client);
			if (root == null && this.client.equals(SkinResources.CLIENT_ADVANCED)) {
				root = docElement;
			}
			addFiles(root, ename, baseDir, list);
		}

		private void addFiles(Element root, String ename,
							  File baseDir, List<File> list) {
			if (root == null) return;

			Element element = getFirstChildElement(root, ename);
			if (element != null) {
				Element fileEl = getFirstChildElement(element, E_FILE);
				while (fileEl != null) {
					String filename = getChildText(fileEl);
					File file = new File(baseDir, filename);
					list.add(file);
					fileEl = getNextSiblingElement(fileEl, E_FILE);
				}
			}
		}

		private static Element getFirstChildElement(Node parent, String ename) {
			Node child = parent.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.ELEMENT_NODE &&
						child.getNodeName().equals(ename)) {
					return (Element) child;
				}
				child = child.getNextSibling();
			}
			return null;
		}

		private static Element getNextSiblingElement(Node node, String ename) {
			Node sibling = node.getNextSibling();
			while (sibling != null) {
				if (sibling.getNodeType() == Node.ELEMENT_NODE &&
						sibling.getNodeName().equals(ename)) {
					return (Element) sibling;
				}
				sibling = sibling.getNextSibling();
			}
			return null;
		}

		private static String getChildText(Node node) {
			StringBuilder str = new StringBuilder();
			Node child = node.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.TEXT_NODE) {
					str.append(child.getNodeValue());
				}
				child = child.getNextSibling();
			}
			return str.toString();
		}
	} // class Manifest

	/**
	 * <strong>Note:</strong>
	 * This class is used by the app:imginfo tag. It needs to be defined
	 * outside of the tag itself because of JSP class loader weirdness.
	 * Otherwise, once an ImageInfo object is put into the cache, trying
	 * to pull it out of the cache will result in a ClassCastException
	 * that looks like "N cannot be cast to N".
	 */
	public static class ImageInfo {

		// Constants
		public static final int DEFAULT_WIDTH = 16; // TODO: settable?
		public static final int DEFAULT_HEIGHT = 16; // TODO: settable?

		// Data
		private File file;
		private String src;
		private int width;
		private int height;

		// Constructors
		public ImageInfo(File file, String src) {
			this.file = file;
			this.src = src;
			try {
				// NOTE: Assuming the web container is running "headless"
				ImageIcon icon = new ImageIcon(file.toURL());
				this.width = icon.getIconWidth();
				this.height = icon.getIconHeight();
				if (this.width == -1) {
					throw new Exception("no size data available");
				}
			}
			catch (Exception e) {
				this.width = DEFAULT_WIDTH;
				this.height = DEFAULT_HEIGHT;
			}
		}

		// Public methods
		public String getSrc() { return src; }
		public int getWidth() { return width; }
		public int getHeight() { return height; }

	} // class ImageInfo

} // class SkinResources
