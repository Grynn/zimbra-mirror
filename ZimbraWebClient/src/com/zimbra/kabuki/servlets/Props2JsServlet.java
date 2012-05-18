/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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


package com.zimbra.kabuki.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.BufferStream;

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
public class Props2JsServlet extends HttpServlet {
    protected static final String COMPRESSED_EXT = ".zgz";

    protected static final String P_DEBUG = "debug";
    protected static final String P_BASENAME_PATTERNS = "basename-patterns";

    protected static final String A_FLUSH_CACHE = "flushCache"; /* i.e. FlushCache.FLUSH_CACHE */

    protected static final String A_REQUEST_URI = "request-uri";
    protected static final String A_BASENAME_PATTERNS = P_BASENAME_PATTERNS;
    protected static final String A_BASENAME_PATTERNS_LIST = A_BASENAME_PATTERNS+"-list";

    private static Map<Locale, Map<String, byte[]>> buffers =
        new HashMap<Locale, Map<String, byte[]>>();

    private String getDirPath(String dirname) {
        if (new File(dirname).isAbsolute()) {
            return dirname;
        }
        String basedir = this.getServletContext().getRealPath("/");
        if (!basedir.endsWith("/"))
            basedir += "/";
        return basedir + dirname;
    }

    public void service(ServletRequest req, ServletResponse resp)
    throws IOException, ServletException {
        if (flushCache(req)) {
            return;
        }
        super.service(req, resp);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws
        IOException, ServletException {
        // get request info
        boolean debug = req.getParameter(P_DEBUG) != null;
        Locale locale = getLocale(req);
        Map<String, byte[]> localeBuffers;
        String uri = getRequestURI(req);
        
        synchronized(buffers) {
            localeBuffers = buffers.get(locale);
            if (localeBuffers == null) {
                localeBuffers =  Collections.synchronizedMap(new HashMap<String,
                    byte[]>());
                buffers.put(locale, localeBuffers);
            }
        }
        
        // get byte buffer
        byte[] buffer = debug ? null : localeBuffers.get(uri);

        if (buffer == null) {
            buffer = getBuffer(req, locale, uri);
            // do not need to compress JS because Prop2Js has been optimized
            if (uri.endsWith(COMPRESSED_EXT)) {
                // gzip response
                ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer.length / 2);
                OutputStream gzos = new GZIPOutputStream(bos);
                
                gzos.write(buffer);
                gzos.close();
                buffer = bos.toByteArray();
            }
            if (!LC.zimbra_minimize_resources.booleanValue())
                localeBuffers.put(uri, buffer);
        }

        // generate output
        OutputStream out = resp.getOutputStream();
        try {
            if (uri.endsWith(COMPRESSED_EXT))
                resp.setHeader("Content-Encoding", "gzip");
            resp.setContentType("application/x-javascript");
        } catch (Exception e) {
            if (isErrorEnabled())
                error(e.getMessage());
        }
        out.write(buffer);
        out.flush();
    }

    protected boolean flushCache(ServletRequest req) {
        Boolean flushCache = (Boolean)req.getAttribute(A_FLUSH_CACHE);
        if (flushCache != null && flushCache.booleanValue()) {
            int oldSize = buffers.size();
            buffers.clear();
            int newSize = buffers.size();
            if (isDebugEnabled()) {
                debug("flushed uistrings cache: "+oldSize+" entries > "+newSize+" entries");
            }
            return true;
        }
        return false;
    }

    protected boolean isWarnEnabled() {
        return true;
    }
    protected boolean isErrorEnabled() {
        return true;
    }
    protected boolean isDebugEnabled() {
        return true;
    }

    protected void warn(String message) {
        System.err.println(message);
    }
    protected void error(String message) {
        System.err.println(message);
    }
    protected void debug(String message) {
        System.err.println(message);
    }

    protected String getRequestURI(HttpServletRequest req) {
        String uri = (String) req.getAttribute(A_REQUEST_URI);
        if (uri == null)
            uri = req.getRequestURI();
        return uri;
    }

    protected List<String> getBasenamePatternsList(HttpServletRequest req) {
        List<String> list = new LinkedList<String>();
        String patterns = (String) req.getAttribute(A_BASENAME_PATTERNS);
        
        if (patterns == null)
            patterns = this.getInitParameter(P_BASENAME_PATTERNS);
        if (patterns == null)
            patterns = "WEB-INF/classes/${dir}/${name}";
        list.add(patterns);
        return list;
    }

    protected Locale getLocale(HttpServletRequest req) {
        String language = req.getParameter("language");
        String locid = req.getParameter("locid");
        if (language != null) {
            String country = req.getParameter("country");
            if (country != null) {
                return new Locale(language, country);
            }
            return new Locale(language);
        }
        else if (locid != null) {
            String[] parts = locid.split("_");
            if (parts.length > 1) {
                return new Locale(parts[0], parts[1]);
            } else {
                return new Locale(parts[0]);
            }
        }
        return req.getLocale();
    }

	private String getCommentSafeString(String st) {
		return st.replaceAll("<", "") //make sure you can't start a "script" tag within the comment cuz genius IE supposedly exectutes it
				.replaceAll("\n", ""); //make sure no newline can be injected to start a malicious script too

	}

    protected byte[] getBuffer(HttpServletRequest req,
        Locale locale, String uri) throws IOException {
        BufferStream bos = new BufferStream(24 * 1024);
        DataOutputStream out = new DataOutputStream(bos);
		String sanitizedLocale = locale.toString()
				.replaceAll("<", "") //make sure you can't start a "script" tag within the comment cuz genius IE supposedly exectutes it
				.replaceAll("\n", ""); //make sure no newline can be injected to start a malicious script too
		out.writeBytes("// Locale: " + getCommentSafeString(locale.toString()) + '\n');

        // tokenize the list of patterns
        List<String> patternsList = this.getBasenamePatternsList(req);
        List<List<String>> basenamePatterns = new LinkedList<List<String>>();
        for (String patterns : patternsList) {
            StringTokenizer tokenizer = new StringTokenizer(patterns, ",");
            List<String> basenamesList = new LinkedList<String>();
            basenamePatterns.add(basenamesList);
            while (tokenizer.hasMoreTokens()) {
                String pattern = tokenizer.nextToken().trim();
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
        // then the basedir is "/keys/".
        //
        // NOTE: The <url-pattern>s in the web.xml file restricts
        //       which URLs map to this servlet so there's no risk
        //       that the basedir will be other than what we expect.
        int lastSlash = uri.lastIndexOf('/');
        int prevSlash = uri.substring(0, lastSlash).lastIndexOf('/');
        String basedir = uri.substring(prevSlash, lastSlash + 1);
        String dirname = this.getDirPath("");
        String filenames = uri.substring(uri.lastIndexOf('/') + 1);
        String classnames = filenames.substring(0, filenames.indexOf('.'));
        StringTokenizer tokenizer = new StringTokenizer(classnames, ",");
        
        if (isDebugEnabled()) {
            for (List<String> basenames : basenamePatterns) {
                debug("!!! basenames: "+basenames);
            }
            debug("!!! basedir:   "+basedir);
        }
        while (tokenizer.hasMoreTokens()) {
            String classname = tokenizer.nextToken();
            if (isDebugEnabled()) {
                debug("!!! classname: "+classname);
            }
            load(req, out, locale, basenamePatterns, basedir, dirname, classname);
        }
        return bos.toByteArray();
    } 
    
    protected void load(HttpServletRequest req, DataOutputStream out,
        Locale locale, List<List<String>> basenamePatterns,
        String basedir, String dirname, String classname) throws IOException {
        String basename = basedir + classname;

        out.writeBytes("// Basename: " + getCommentSafeString(basename) + '\n');
        for (List<String> basenames : basenamePatterns) {
            try {
                ClassLoader parentLoader = this.getClass().getClassLoader();
                PropsLoader loader = new PropsLoader(parentLoader, basenames,
                    basedir, dirname, classname);
                
                // load path list, but not actual properties to prevent caching
                ResourceBundle.getBundle(basename, locale, loader);
                for (File file : loader.getFiles()) {
                    if (locale.getLanguage() == "zh") {
                        out.writeBytes("// file: " + getCommentSafeString(file.getName()) + '\n');    
                    }
                    Props2Js.convert(out, file, classname);
                }
            } catch (MissingResourceException e) {
                out.writeBytes("// properties for " + classname + " not found\n");
            } catch (IOException e) {
                out.writeBytes("// properties error for " + classname +
                    " - see server log\n");
                error(e.getMessage());
            }
        }
    }

    public static class PropsLoader extends ClassLoader {
        private List<File> files;
        private List<String> patterns;
        private String dir;
        private String dirname;
        private String name;

        private static Pattern RE_LOCALE = Pattern.compile(".*(_[a-z]{2}(_[A-Z]{2})?)\\.properties");
        private static Pattern RE_SYSPROP = Pattern.compile("\\$\\{(.*?)\\}");

        public PropsLoader(ClassLoader parent, List<String> patterns,
            String basedir, String dirname, String classname) {
            super(parent);
            this.patterns = patterns;
            this.dir = basedir.replaceAll("/[^/]+$", "").replaceAll("^.*/", "");
            this.dirname = dirname;
            this.name = classname;
            this.files = new LinkedList<File>();
        }

        public List<File> getFiles() { return files; }
        
        public InputStream getResourceAsStream(String rname) {
            String filename = rname.replaceAll("^.*/", "");
            Matcher matcher = RE_LOCALE.matcher(filename);
            String locale = matcher.matches() ? matcher.group(1) : "";
            String ext = rname.replaceAll("^[^\\.]*", "");
            for (String basename : this.patterns) {
                basename = basename.replaceAll("\\$\\{dir\\}", this.dir);
                basename = basename.replaceAll("\\$\\{name\\}", this.name);
                basename = replaceSystemProps(basename);
                basename += locale + ext;
                File file = new File(this.dirname+basename);
                if (!file.exists()) {
                    file = new File(basename);
                }
                if (file.exists()) {
                    files.add(file);
                    return new ByteArrayInputStream(new byte[0]);
                }
            }
            return super.getResourceAsStream(rname);
        }

        private static String replaceSystemProps(String s) {
            Matcher matcher = RE_SYSPROP.matcher(s);
            if (!matcher.find()) return s;
            StringBuilder str = new StringBuilder();
            int index = 0;
            do {
                str.append(s.substring(index, matcher.start()));
                String pname = matcher.group(1);
                String pvalue = System.getProperty(pname);
                str.append(pvalue != null ? pvalue : matcher.group(0));
                index = matcher.end();
            } while (matcher.find());
            str.append(s.substring(index));
            return str.toString();
        }
    }
}
