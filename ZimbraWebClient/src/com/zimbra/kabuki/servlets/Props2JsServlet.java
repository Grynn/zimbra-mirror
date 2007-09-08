/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.zimbra.kabuki.servlets;

import com.zimbra.common.util.ZimbraLog;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mozilla.javascript.Scriptable;

/**
 * This class looks for the resource bundle for the requested file (e.g.
 * "/path/Messages.js"), resolves it, and generates a JavaScript file with
 * a class that contains all of the properties in the bundle. The servlet
 * takes into account the locale of the user request in order to load the
 * correct resource bundle.
 * <p>
 * For example, if the client requested the URL "/path/Messages.js" and
 * the locale was set to Japanese/Japan, the servlet would try to load the
 * Japanese version of the resource bundle. The base name of the bundle
 * would be just "/path/Messages" but the ResourceBundle class would
 * resolve this with the locale and look for the resource files.
 * <p>
 * Once all of the properties in the resource bundle have been resolved,
 * then the servlet iterates over the resource keys and generates a line
 * of JavaScript for each value. For example, if "/path/Messages.properties"
 * contains the following:
 * <pre>
 * one = One
 * two : Two\
 * Two
 * three = Three\
 * 		Three\
 * 		Three
 * </pre>
 * the generated JavaScript would look like this:
 * <pre>
 * function Messages() {}
 * 
 * Messages["one"] = "One";
 * Messages["two"] = "TwoTwo";
 * Messages["three"] = "ThreeThreeThree";
 * </pre>
 * <p>
 * <strong>Note:</strong>
 * The implementation assumes that the basename of the resource bundle
 * will always be "/${dir}/" concatenated with the filename without the
 * extension. The token "${dir}" is the directory immediately preceding
 * the filename in the URL.
 * <p>
 * The path to the file can be overridden to allow the servlet to look
 * in multiple places for the resource files. It does this by implementing
 * a custom class loader that looks at the "basedirs" servlet init parameter.
 * The init parameter is a comma-separated list of locations to look for the
 * resource file. If relative, the base directory of the servlet's webapp is
 * assumed. Each entry in the list can contain the tokens "${dir}" and
 * "${file}" which represent the directory name and filename (w/o extension)
 * of the request, respectively. 
 * 
 * @author Andy Clark
 */
public class Props2JsServlet 
	extends HttpServlet {

    //
    // Constants
    //
    
    private static final String COMPRESSED_EXT = ".zgz";

	private static final String P_DEBUG = "debug";
	private static final String P_BASENAME_PATTERNS = "basename-patterns";

    //
    // Data
    //

	protected List<String> basenamePatterns;

	private Map<Locale,Map<String,byte[]>> buffers =
		new HashMap<Locale,Map<String,byte[]>>();
    
    //
    // HttpServlet methods
    //

	private String getDirPath(String dirname) {
		if (new File(dirname).isAbsolute()) {
			return dirname;
		}
		String basedir = this.getServletContext().getRealPath("/");
		return basedir + dirname;  
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException {
		// NOTE: This block is not synchronized because even if two requests
		//       happen at the same time, they'll end up with the same result
		//       and, once cached, won't happen again. So you might waste a
		//       few cycles if this happens but is unlikely and saves you all
		//       of the synchronization overhead.
		if (this.basenamePatterns == null) {
			List<String> basenamePatterns = new LinkedList<String>();

			String patterns = this.getInitParameter(P_BASENAME_PATTERNS);
			if (patterns == null) {
				patterns = "WEB-INF/classes/${dir}/${name}";
			}

			StringTokenizer tokenizer = new StringTokenizer(patterns, ",");
			while (tokenizer.hasMoreTokens()) {
				String pattern = this.getDirPath(tokenizer.nextToken().trim());
				basenamePatterns.add(pattern);
			}

			this.basenamePatterns = basenamePatterns;
		}

		// get request info
		Locale locale = getLocale(req);
        String uri = req.getRequestURI();
		boolean debug = req.getParameter(P_DEBUG) != null;

		// get locale buffers
		Map<String,byte[]> localeBuffers = buffers.get(locale);
		if (localeBuffers == null) {
			localeBuffers = new HashMap<String,byte[]>();
			buffers.put(locale, localeBuffers);
		}

		// get byte buffer
		byte[] buffer = !debug ? localeBuffers.get(uri) : null;
		if (buffer == null) {
			buffer = getBuffer(req, locale, uri);
			if (!debug) {
                org.mozilla.javascript.Context context = org.mozilla.javascript.Context.enter();
                context.setOptimizationLevel(-1);
                Scriptable scriptable = context.initStandardObjects();
                Reader reader = new StringReader(buffer.toString());
                String script = null;
                int lineNum = 0;
                Object securityDomain = null;

                String mintext = org.mozilla.javascript.tools.shell.Main.compressScript(
                    context, scriptable, reader,
                    script, uri, lineNum, securityDomain
                );
                if (mintext == null) {
                    ZimbraLog.zimlet.debug("unable to minimize zimlet JS source");
                }
                else {
                    buffer = mintext.getBytes();
                }
                localeBuffers.put(uri, buffer);
			}
		}

		// generate output
		OutputStream out = resp.getOutputStream();
        resp.setContentType("application/x-javascript");
        out.write(buffer);
        out.flush();
    } // doGet(HttpServletRequest,HttpServletResponse)
    
    //
    // Private methods
    //

    private Locale getLocale(HttpServletRequest req) {
    	String language = req.getParameter("language");
    	if (language != null) {
        	String country = req.getParameter("country");
        	if (country != null) {
            	String variant = req.getParameter("variant");
            	if (variant != null) {
            		return new Locale(language, country, variant);
            	}
            	return new Locale(language, country);
        	}
        	return new Locale(language);
    	}
    	return req.getLocale();
    } // getLocale(HttpServletRequest):Locale
    
    private synchronized byte[] getBuffer(HttpServletRequest req, Locale locale, String uri)
	throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream out = uri.endsWith(COMPRESSED_EXT)
						? new PrintStream(new GZIPOutputStream(bos))
						: new PrintStream(bos);
		out.println("// Locale: "+locale);

		// This gets the base directory for the resource bundle
		// basename. For example, if the URI is:
		//
		//   .../messages/I18nMsg.js
		//
		// then the basedir is "/messages/" and if the URI is:
		//
		//   .../keys/ZmKeys.js
		//
		//       then the basedir is "/keys/".
		//
		// NOTE: The <url-pattern>s in the web.xml file restricts
		//       which URLs map to this servlet so there's no risk
		//       that the basedir will be other than what we expect.
		int lastSlash = uri.lastIndexOf('/');
		int prevSlash = uri.substring(0, lastSlash).lastIndexOf('/');
		String basedir = uri.substring(prevSlash, lastSlash + 1);

		String filenames = uri.substring(uri.lastIndexOf('/')+1);
		String classnames = filenames.substring(0, filenames.indexOf('.'));
		StringTokenizer tokenizer = new StringTokenizer(classnames, ",");
		while (tokenizer.hasMoreTokens()) {
			String classname = tokenizer.nextToken();
			load(req, out, locale, basedir, classname);
		}

		// save buffer
		out.close();

        return bos.toByteArray();
    } // getBuffer(Locale,String):byte[]

    private void load(HttpServletRequest req,
					  PrintStream out, Locale locale,
					  String basedir, String classname) {
        String basename = basedir+classname;

        out.println();
        out.println("// Basename: "+basename);

        ResourceBundle bundle;
        try {
			ClassLoader parentLoader = this.getClass().getClassLoader();
			ClassLoader loader = new PropsLoader(parentLoader, this.basenamePatterns, basedir, classname);
			bundle = ResourceBundle.getBundle(basename, locale, loader);
			Props2Js.convert(out, bundle, classname);
		}
		catch (MissingResourceException e) {
			out.println("// resource bundle not found");
			ZimbraLog.webclient.warn("unable to load resource bundle: "+basename);
		}
		catch (IOException e) {
			out.println("// error: "+e.getMessage());
		}
	} // load(PrintStream,String)

	//
	// Classes
	//

	public static class PropsLoader extends ClassLoader {
		// Constants
		private Pattern RE_LOCALE = Pattern.compile(".*(_[a-z]{2}(_[A-Z]{2})?)\\.properties");
		// Data
		private List<String> patterns;
		private String dir;
		private String name;
		// Constructors
		public PropsLoader(ClassLoader parent, List<String> patterns,
						   String basedir, String classname) {
			super(parent);
			this.patterns = patterns;
			this.dir = basedir.replaceAll("/", "");
			this.name = classname;
		}
		// ClassLoader methods
		public InputStream getResourceAsStream(String name) {
			String filename = name.replaceAll("^.*/", "");
			Matcher matcher = RE_LOCALE.matcher(filename);
			String locale = matcher.matches() ? matcher.group(1) : "";
			String ext = name.replaceAll("^[^\\.]*", ""); 
			for (String basename : this.patterns) {
				basename = basename.replaceAll("\\$\\{dir\\}", this.dir);
				basename = basename.replaceAll("\\$\\{name\\}", this.name);
				basename += locale + ext;
				File file = new File(basename);
				if (file.exists()) {
					try {
						return new FileInputStream(file);
					}
					catch (FileNotFoundException e) {
						// ignore
					}
				}
			}
			return super.getResourceAsStream(name);
		}
	}

} // class Props2JsServlet
