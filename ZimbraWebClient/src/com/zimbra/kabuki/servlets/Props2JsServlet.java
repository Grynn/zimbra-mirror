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


package com.zimbra.kabuki.servlets;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/**
 * This class looks for the resource bundle for the requested file (e.g.
 * "/path/Messages.js"), resolves it, and generates a JavaScript file with
 * a class that contains all of the properties in the bundle. The servlet
 * takes into account the locale of the user request in order to load the
 * correct resource bundle.
 * <p/>
 * For example, if the client requested the URL "/path/Messages.js" and
 * the locale was set to Japanese/Japan, the servlet would try to load the
 * Japanese version of the resource bundle. The base name of the bundle
 * would be just "/path/Messages" but the ResourceBundle class would
 * resolve this with the locale and look for the resource files.
 * <p/>
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
 * <p/>
 * Messages["one"] = "One";
 * Messages["two"] = "TwoTwo";
 * Messages["three"] = "ThreeThreeThree";
 * </pre>
 * <p/>
 * <strong>Note:</strong>
 * The implementation assumes that the basename of the resource bundle
 * will always be "/${dir}/" concatenated with the filename without the
 * extension. The token "${dir}" is the directory immediately preceding
 * the filename in the URL.
 * <p/>
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

    protected static final String COMPRESSED_EXT = ".zgz";

    protected static final String P_DEBUG = "debug";
    protected static final String P_BASENAME_PATTERNS = "basename-patterns";

    protected static final String A_REQUEST_URI = "request-uri";
    protected static final String A_BASENAME_PATTERNS = P_BASENAME_PATTERNS;
	protected static final String A_BASENAME_PATTERNS_LIST = A_BASENAME_PATTERNS+"-list";

	//
    // Data
    //

    private Map<Locale, Map<String, byte[]>> buffers =
            new HashMap<Locale, Map<String, byte[]>>();

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

        // get request info
        Locale locale = getLocale(req);
        String uri = getRequestURI(req);
        boolean debug = req.getParameter(P_DEBUG) != null;

        // get locale buffers
        Map<String, byte[]> localeBuffers = buffers.get(locale);
        if (localeBuffers == null) {
            localeBuffers = new HashMap<String, byte[]>();
            buffers.put(locale, localeBuffers);
        }

        // get byte buffer
        byte[] buffer = !debug ? localeBuffers.get(uri) : null;
        if (buffer == null) {
            buffer = getBuffer(req, locale, uri);
            if (!debug) {
                // compress JS
                JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(new String(buffer)), new ErrorReporter() {

                    public void warning(String message, String sourceName,
                                        int line, String lineSource, int lineOffset) {
                        if (line < 0) {
                            warn("\n" + message);
                        } else {
                            warn("\n" + line + ':' + lineOffset + ':' + message);
                        }
                    }

                    public void error(String message, String sourceName,
                                      int line, String lineSource, int lineOffset) {
                        if (line < 0) {
                            Props2JsServlet.this.error("\n" + message);
                        } else {
                            Props2JsServlet.this.error("\n" + line + ':' + lineOffset + ':' + message);
                        }
                    }

                    public EvaluatorException runtimeError(String message, String sourceName,
                                                           int line, String lineSource, int lineOffset) {
                        error(message, sourceName, line, lineSource, lineOffset);
                        return new EvaluatorException(message);
                    }
                });
                StringWriter out = new StringWriter();
                compressor.compress(out, 0, true, false, false, false);
                buffer = out.toString().getBytes();

                // gzip response
                if (uri.endsWith(COMPRESSED_EXT)) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer.length);
                    OutputStream gzos = new GZIPOutputStream(bos);
                    gzos.write(buffer);
                    gzos.close();
                    buffer = bos.toByteArray();
                }

                localeBuffers.put(uri, buffer);
            }
        }

        // generate output
        OutputStream out = resp.getOutputStream();
		try {
			if (uri.endsWith(COMPRESSED_EXT)) {
				resp.setHeader("Content-Encoding", "gzip");
			}
			resp.setContentType("application/x-javascript");
		}
		catch (Exception e) {
			error(e.getMessage());
		}
		out.write(buffer);
        out.flush();
    } // doGet(HttpServletRequest,HttpServletResponse)

    //
    // Protected methods
    //

	protected void warn(String message) {
		System.err.println(message);
	}
	protected void error(String message) {
		System.err.println(message);
	}

	protected String getRequestURI(HttpServletRequest req) {
        String uri = (String) req.getAttribute(A_REQUEST_URI);
        if (uri == null) {
            uri = req.getRequestURI();
        }
        return uri;
    }

    protected List<String> getBasenamePatternsList(HttpServletRequest req) {
		List<String> list = new LinkedList<String>();
		String patterns = (String) req.getAttribute(A_BASENAME_PATTERNS);
		if (patterns == null) {
			patterns = this.getInitParameter(P_BASENAME_PATTERNS);
		}
		if (patterns == null) {
			patterns = "WEB-INF/classes/${dir}/${name}";
		}
		list.add(patterns);
		return list;
	}

    protected Locale getLocale(HttpServletRequest req) {
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

    protected synchronized byte[] getBuffer(HttpServletRequest req,
                                          Locale locale, String uri)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bos);
        out.println("// Locale: " + locale);

		// tokenize the list of patterns
		List<String> patternsList = this.getBasenamePatternsList(req);
		List<List<String>> basenamePatterns = new LinkedList<List<String>>();
		for (String patterns : patternsList) {
			StringTokenizer tokenizer = new StringTokenizer(patterns, ",");
			List<String> basenamesList = new LinkedList<String>();
			basenamePatterns.add(basenamesList);
			while (tokenizer.hasMoreTokens()) {
				String pattern = this.getDirPath(tokenizer.nextToken().trim());
				basenamesList.add(pattern);
			}
		}

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

        String filenames = uri.substring(uri.lastIndexOf('/') + 1);
        String classnames = filenames.substring(0, filenames.indexOf('.'));
        StringTokenizer tokenizer = new StringTokenizer(classnames, ",");
        while (tokenizer.hasMoreTokens()) {
            String classname = tokenizer.nextToken();
            load(req, out, locale, basenamePatterns, basedir, classname);
        }

        // save buffer
        out.close();

        return bos.toByteArray();
    } // getBuffer(Locale,String):byte[]

    protected void load(HttpServletRequest req,
                      PrintStream out, Locale locale,
                      List<List<String>> basenamePatterns,
                      String basedir, String classname) {
        String basename = basedir + classname;

        out.println();
        out.println("// Basename: " + basename);

		for (List<String> basenames : basenamePatterns) {
			try {
				ClassLoader parentLoader = this.getClass().getClassLoader();
				ClassLoader loader = new PropsLoader(parentLoader, basenames, basedir, classname);
				ResourceBundle bundle = ResourceBundle.getBundle(basename, locale, loader);
				Props2Js.convert(out, bundle, classname);
			}
			catch (MissingResourceException e) {
				out.println("// resource bundle not found");
			}
			catch (IOException e) {
				out.println("// error: " + e.getMessage());
			}
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
            this.dir = basedir.replaceAll("/[^/]+$", "").replaceAll("^.*/", "");
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
