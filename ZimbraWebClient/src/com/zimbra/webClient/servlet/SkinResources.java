/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Web Client
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.webClient.servlet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Clean up this code!
 */
public class SkinResources
extends HttpServlet {

	//
	// Constants
	//

	private static final String P_SKIN = "skin";
	private static final String P_USER_AGENT = "agent";
	private static final String P_DEBUG = "debug";

	private static final String H_USER_AGENT = "User-Agent";

	private static final String C_SKIN = "ZM_SKINF";

	private static final String T_HTML = "html";

	private static final String N_SKIN = "skin";

	private static final String DEFAULT_SKIN = "sand";
	private static final String SKIN_MANIFEST_EXT = ".xml";

	private static final Pattern RE_IFDEF = Pattern.compile("^\\s*#ifdef\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern RE_IFNDEF = Pattern.compile("^\\s*#ifndef\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern RE_ENDIF = Pattern.compile("^\\s*#endif(\\s+.*)?$", Pattern.CASE_INSENSITIVE);

	private static final boolean DEBUG = false;

	//
	// Data
	//

	/**
	 * <ul>
	 * <li>Key: alphabetical list of macro names based on user agent
	 *          (e.g. GECKO NAVIGATOR MACINTOSH)
	 * <li>Value: Map
	 *   <ul>
	 *   <li>Key: request uri
	 *   <li>Value: String buffer
	 *   </ul>
	 * </ul>
	 */
	private Map<String,Map<String,String>> cache =
		new HashMap<String,Map<String,String>>();

	//
	// HttpServlet methods
	//

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, ServletException {
		String uri = getRequestURI(req);
		String contentType = getContentType(uri);
		String type = contentType.replaceAll("^.*/", "");
		boolean debug = req.getParameter(P_DEBUG) != null;

		String userAgent = getUserAgent(req);
		Map<String,String> macros = parseUserAgent(userAgent);
		String browserType = getMacroNames(macros.keySet());
		String skin = getSkin(req);

		String cacheId = skin+": "+browserType;

		if (DEBUG) {
			System.err.println("DEBUG: browserType="+browserType);
			System.err.println("DEBUG: uri="+uri);
			System.err.println("DEBUG: cacheId="+cacheId);
		}

		// generate buffer
		Map<String,String> buffers = cache.get(cacheId);
		String buffer = buffers != null && !debug ? buffers.get(uri) : null;
		if (buffer == null) {
			if (DEBUG) System.err.println("DEBUG: generating buffer");
			buffer = generate(req, macros, type);
			if (!debug) {
				if (buffers == null) {
					buffers = new HashMap<String,String>();
					cache.put(cacheId, buffers);
				}
				buffers.put(uri, buffer);
			}
		}
		else {
			if (DEBUG) System.err.println("DEBUG: using previous buffer");
		}

		// write buffer
		try {
		 	resp.setContentType(contentType);
			resp.setContentLength(buffer.length());
		}
		catch (IllegalStateException e) {
			// ignore -- thrown if called from including JSP
		}

		try {
			OutputStream out = resp.getOutputStream();
			byte[] bytes = buffer.getBytes("UTF-8");
			out.write(bytes);
		}
		catch (IllegalStateException e) {
			// use writer if called from including JSP
			PrintWriter out = resp.getWriter();
			out.print(buffer);
		}

	} // doGet(HttpServletRequest,HttpServletResponse)

	//
	// Private methods
	//

	private String generate(HttpServletRequest req,
							Map<String,String> macros,
							String type)
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
			out.println("#define "+mname+" "+mvalue);
		}
		out.println(commentEnd);
		out.println();

		String uri = getRequestURI(req);
		String filenames = uri;
		String ext = "."+type;

		int slash = uri.lastIndexOf('/');
		if (slash != -1) {
			filenames = uri.substring(slash + 1);
		}

		int dot = filenames.lastIndexOf('.');
		if (dot != -1) {
			ext = filenames.substring(dot);
			filenames = filenames.substring(0, dot);
		}

		ServletContext context = getServletContext();

		String fileDirname = context.getRealPath("/"+type);
		File fileDir = new File(fileDirname);
		String skinDirname = context.getRealPath("/skins/" + skin);
		File skinDir = new File(skinDirname);
		File manifestFile = new File(skinDir, skin + SKIN_MANIFEST_EXT);

		// load manifest
		Manifest manifest = new Manifest(manifestFile, macros);

		// process input files
		StringTokenizer tokenizer = new StringTokenizer(filenames, ",");
		while (tokenizer.hasMoreTokens()) {
			String filename = tokenizer.nextToken();
			String filenameExt = filename + ext;

			List<File> files = new LinkedList<File>();

			if (filename.equals(N_SKIN)) {
				files.addAll(manifest.getFiles(type));
			}
			else {
				File file = new File(skinDir, filenameExt);
				if (!file.exists()) {
					file = new File(fileDir, filenameExt);
				}
				files.add(file);
			}

			for (File file : files) {
				if (!file.exists()) {
					out.print(commentStart);
					out.print("Error: file doesn't exist - "+file);
					out.println(commentEnd);
					out.println();
					continue;
				}

				preprocess(file, cout, macros, manifest,
							commentStart, commentContinue, commentEnd);
			}
		}

		// return data
		return cout.toString();
	}

	static void preprocess(File file,
						   Writer writer,
						   Map<String,String> macros,
						   Manifest manifest,
						   String commentStart,
						   String commentContinue,
						   String commentEnd)
	throws IOException {
		PrintWriter out = new PrintWriter(writer);

		out.println(commentStart);
		out.print(commentContinue);
		out.println("File: "+file.getName());
		out.println(commentEnd);
		out.println();

		BufferedReader in = new BufferedReader(new FileReader(file));
		Stack<Boolean> ignore = new Stack<Boolean>();
		ignore.push(false);
		String line;
		while ((line = in.readLine()) != null) {
			Matcher ifdef = RE_IFDEF.matcher(line);
			if (ifdef.matches()) {
				out.print(commentStart);
				out.print("Info: "+line);
				out.println(commentEnd);
				String macroName = ifdef.group(1);
				ignore.push(macros.get(macroName) == null);
				continue;
			}
			Matcher ifndef = RE_IFNDEF.matcher(line);
			if (ifndef.matches()) {
				out.print(commentStart);
				out.print("Info: "+line);
				out.println(commentEnd);
				String macroName = ifndef.group(1);
				ignore.push(macros.get(macroName) != null);
				continue;
			}
			Matcher endif = RE_ENDIF.matcher(line);
			if (endif.matches()) {
				out.print(commentStart);
				out.print("Info: "+line);
				out.println(commentEnd);
				ignore.pop();
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
	}

	//
	// Private static functions
	//

	private static String getRequestURI(HttpServletRequest req) {
		return req.getRequestURI();
	}

	private static Cookie getCookie(HttpServletRequest req, String name) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
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

	private static String getSkin(HttpServletRequest req) {
		String skin = req.getParameter(P_SKIN);
		if (skin == null) {
			Cookie cookie = getCookie(req, C_SKIN);
			skin = cookie != null ? cookie.getValue() : DEFAULT_SKIN;
		}
		return skin;
	}

	private static String getContentType(String uri) {
		int index = uri.lastIndexOf('/');
		if (index != -1) {
			uri = uri.substring(0, index);
		}
		index = uri.lastIndexOf('/');
		String type = index != -1 ? uri.substring(index + 1) : "plain";
		return "text/" + type;
	}

	private static String getUserAgent(HttpServletRequest req) {
		String agent = req.getParameter(P_USER_AGENT);
		if (agent == null) {
			agent = req.getHeader(H_USER_AGENT);
		}
		return agent;
	}

	private static Map<String,String> parseUserAgent(String agent) {
		Map<String,String> macros = new HashMap<String,String>();

		// state
		double browserVersion = -1.0;
		double geckoDate = 0;
		double mozVersion = -1;
		boolean isMac = false;
		boolean isWindows = false;
		boolean isLinux = false;
		boolean isNav  = false;
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
		boolean isIE6  = false;
		boolean isIE6up = false;
		boolean isFirefox = false;
		boolean isFirefox1up = false;
		boolean isFirefox1_5up = false;
		boolean isMozilla = false;
		boolean isMozilla1_4up = false;
		boolean isSafari = false;
		boolean isGeckoBased = false;
		boolean isOpera = false;

		// parse user agent
		String agt = agent.toLowerCase();
		StringTokenizer agtArr = new StringTokenizer(agt, " ;()");
		int i = 0;
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
			if (mozilla.find()){
				index = mozilla.start();
				beginsWithMozilla = true;
				browserVersion = parseFloat(token.substring(index + 8));
				isNav = true;
			}
			do {
				if (token.indexOf("compatible") != -1 ) {
					isCompatible = true;
					isNav = false;
				} else if ((token.indexOf("opera")) != -1){
					isOpera = true;
					isNav = false;
					if (agtArr.hasMoreTokens()) {
						browserVersion = parseFloat(agtArr.nextToken());
					}
				} else if ((token.indexOf("spoofer")) != -1){
					isSpoofer = true;
					isNav = false;
				} else if ((token.indexOf("webtv")) != -1) {
					isWebTv = true;
					isNav = false;
				} else if ((token.indexOf("hotjava")) != -1) {
					isHotJava = true;
					isNav = false;
				} else if ((index = token.indexOf("msie")) != -1) {
					isIE = true;
					if (agtArr.hasMoreTokens()) {
						browserVersion = parseFloat(agtArr.nextToken());
					}
				} else if ((index = token.indexOf("gecko/")) != -1){
					isGeckoBased = true;
					geckoDate = Float.parseFloat(token.substring(index + 6));
				} else if ((index = token.indexOf("rv:")) != -1){
					mozVersion = parseFloat(token.substring(index + 3));
					browserVersion = mozVersion;
				} else if ((index = token.indexOf("firefox/")) != -1){
					isFirefox = true;
					browserVersion = parseFloat(token.substring(index + 8));
				} else if ((index = token.indexOf("netscape6/")) != -1){
					trueNs = true;
					browserVersion = parseFloat(token.substring(index + 10));
				} else if ((index = token.indexOf("netscape/")) != -1){
					trueNs = true;
					browserVersion = parseFloat(token.substring(index + 9));
				} else if ((index = token.indexOf("safari/")) != -1){
					isSafari = true;
					browserVersion = parseFloat(token.substring(index + 7));
				} else if (token.indexOf("windows") != -1){
					isWindows = true;
				} else if ((token.indexOf("macintosh") != -1) ||
						   (token.indexOf("mac_") != -1)){
					isMac = true;
				} else if (token.indexOf("linux") != -1){
					isLinux = true;
				}

				token = agtArr.hasMoreTokens() ? agtArr.nextToken() : null;
			} while (token != null);

			// Note: Opera and WebTV spoof Navigator.
			// We do strict client detection.
			isNav  = (beginsWithMozilla && !isSpoofer && !isCompatible &&
							!isOpera && !isWebTv && !isHotJava &&
							!isSafari);

			isIE = (isIE && !isOpera);

			isNav4 = (isNav && (browserVersion == 4) &&
							(!isIE));
			isNav6 = (isNav && trueNs &&
							(browserVersion >=6.0) &&
							(browserVersion < 7.0));
			isNav6up = (isNav && trueNs &&
							  (browserVersion >= 6.0));
			isNav7 = (isNav && trueNs &&
							(browserVersion == 7.0));

			isIE3 = (isIE && (browserVersion < 4));
			isIE4 = (isIE && (browserVersion == 4.0));
			isIE4up = (isIE && (browserVersion >= 4));
			isIE5 = (isIE && (browserVersion == 5.0));
			isIE5_5 = (isIE && (browserVersion == 5.5));
			isIE5up = (isIE && (browserVersion >= 5.0));
			isIE5_5up =(isIE && (browserVersion >= 5.5));
			isIE6  = (isIE && (browserVersion == 6.0));
			isIE6up = (isIE && (browserVersion >= 6.0));

			isMozilla = ((isNav && mozVersion > -1.0 &&
								isGeckoBased && (geckoDate != 0)));
			isMozilla1_4up = (isMozilla && (mozVersion >= 1.4));
			isFirefox = ((isMozilla && isFirefox));
			isFirefox1up = (isFirefox && browserVersion >= 1.0);
			isFirefox1_5up = (isFirefox && browserVersion >= 1.5);

			// operating systems
			define(macros, "WINDOWS", isWindows);
			define(macros, "MACINTOSH", isMac);
			define(macros, "LINUX", isLinux);

			// browser variants
			define(macros, "NAVIGATOR", isNav);
			define(macros, "NAVIGATOR_4", isNav4);
			define(macros, "NAVIGATOR_6", isNav6);
			define(macros, "NAVIGATOR_6_OR_HIGHER", isNav6up);
			define(macros, "NAVIGATOR_7", isNav7);
			define(macros, "NAVIGATOR_COMPATIBLE", isCompatible);

			define(macros, "MOZILLA", isMozilla);
			define(macros, "MOZILLA_1_4_OR_HIGHER", isMozilla1_4up);

			define(macros, "OPERA", isOpera);

			define(macros, "FIREFOX", isFirefox);
			define(macros, "FIREFOX_1_OR_HIGHER", isFirefox1up);
			define(macros, "FIREFOX_1_5_OR_HIGHER", isFirefox1_5up);

			define(macros, "GECKO", isGeckoBased);

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

			define(macros, "SAFARI", isSafari);

			define(macros, "WEBTV", isWebTv);
			define(macros, "HOTJAVA", isHotJava);
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

	private static void define(Map<String,String> macros, String mname, boolean defined) {
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

		private static final String E_SUBSTITUTIONS = "substitutions";
		private static final String E_CSS = "css";
		private static final String E_HTML = "html";
		private static final String E_FILE = "file";

		private static final Pattern RE_TOKEN = Pattern.compile("@.+?@");

		//
		// Data
		//

		private List<File> substList = new LinkedList<File>();
		private List<File> cssList = new LinkedList<File>();
		private List<File> htmlList = new LinkedList<File>();

		private Properties substitutions = new Properties();

		//
		// Constructors
		//

		public Manifest(File manifestFile, Map<String,String> macros)
		throws IOException {
			// load document
			Document document = null;
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

			// process substitutions
			for (File file : substList) {
				if (DEBUG) System.err.println("DEBUG: subst file = "+file);
				try {
					/***/
					CharArrayWriter out = new CharArrayWriter(4096); // 4K
					SkinResources.preprocess(file, out, macros, null, "#", "#", "#");
					String content = out.toString();
					// NOTE: properties files should be ASCII with
					//       escaped Unicode char sequences.
					byte[] bytes = content.getBytes("US-ASCII");

					InputStream in = new ByteArrayInputStream(bytes);

					substitutions.load(in);
				}
				catch (Throwable t) {
					System.err.println("ERROR loading subst file: "+file);
				}

				if (DEBUG) System.err.println("DEBUG: _SkinName_ = "+substitutions.getProperty("_SkinName_"));
			}

			Stack<String> stack = new Stack<String>();
			Enumeration substKeys = substitutions.propertyNames();
			if (DEBUG) System.err.println("DEBUG: InsetBg (before) = "+substitutions.getProperty("InsetBg"));
			while (substKeys.hasMoreElements()) {
				stack.removeAllElements();

				String substKey = (String)substKeys.nextElement();
				if (substKey.equals("InsetBg")) {
					if (DEBUG) System.err.println("DEBUG: InsetBg (loop) = "+substitutions.getProperty("InsetBg"));
				}
				getProperty(stack, substKey);
			}
			if (DEBUG) System.err.println("DEBUG: InsetBg (after) = "+substitutions.getProperty("InsetBg"));

			if (DEBUG) System.err.println("DEBUG: _SkinName_ = "+substitutions.getProperty("_SkinName_"));
		}

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

		public List<File> getFiles(String type) {
			if (type.equals("css")) {
				return cssFiles();
			}
			if (type.equals("html")) {
				return htmlFiles();
			}
			return null;
		}

		// operations

		public String replace(String s) {
			return replace(null, s);
		}

		//
		// Private methods
		//

		public String getProperty(Stack<String> stack, String pname) {
			// check for cycles
			if (stack != null) {
				for (String s : stack) {
					if (s.equals(pname)) {
						return "/*ERR:"+pname+"*/";
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
				}
				else {
					str.append("/*");
					str.append(s.substring(offset, end));
					str.append("*/");
				}

				offset = end;
			} while (matcher.find(offset));
			str.append(s.substring(offset));

			return str.toString();
		}

		//
		// Private functions
		//

		private static void getFiles(Document document, String ename,
									 File baseDir, List<File> list) {
			Element element = getFirstElementByTagName(document, ename);
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

		private static Element getFirstElementByTagName(Document doc, String ename) {
			NodeList nodes = doc.getElementsByTagName(ename);
			return (Element)nodes.item(0);
		}

		private static Element getFirstChildElement(Node parent, String ename) {
			Node child = parent.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals(ename)) {
					return (Element)child;
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
					return (Element)sibling;
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

} // class SkinResources