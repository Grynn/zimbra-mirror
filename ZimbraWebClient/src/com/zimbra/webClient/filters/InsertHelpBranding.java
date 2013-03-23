/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.webClient.filters;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.zimbra.common.util.ZimbraLog;

public class InsertHelpBranding implements Filter {

    //
    // Constants
    //

    private static final String P_EXTENSIONS = "exts";
    private static final String P_MIME_TYPES = "types";

    private static final String A_MIME_TYPE = InsertHelpBranding.class.getName()+":mime-type";

    private static final String RB_BRANDING = "/messages/ZbMsg";

    private static final Pattern RE_LOCALE_ID = Pattern.compile("/([a-z]{2}(_[A-Z]{2}))/");

    //
    // Data
    //

    private ServletContext context;

    private List<ExtensionFilter> filters;

    //
    // Filter methods
    //

    public void init(FilterConfig config) throws ServletException {
        this.context = config.getServletContext();
        this.filters = createFiltersFor(config.getInitParameter(P_EXTENSIONS), config.getInitParameter(P_MIME_TYPES));
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        String uri = httpRequest.getRequestURI();
        for (ExtensionFilter filter : this.filters) {
            if (filter.accept(null, uri)) {
                httpRequest.setAttribute(A_MIME_TYPE, filter.getType());
                doGet(httpRequest, httpResponse);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        this.context = null;
    }

    //
    // Private methods
    //

    private void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        Locale locale = getRequestedLocale(request);
        ResourceBundle bundle = ResourceBundle.getBundle(RB_BRANDING, locale);

        String filename = this.context.getRealPath(request.getServletPath());
        File file = new File(filename);

        // TODO: assume input and output encodings are UTF-8
        // TODO: this seems to be case even for Asian languages
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            OutputStream out = response.getOutputStream();

            // set content-type
            String type = (String)request.getAttribute(A_MIME_TYPE);
            if (type != null) {
                response.setHeader("Content-Type", type);
            }

            // replace tokens
            Buffer buffer = new Buffer();
            int count;
            while (buffer.fill(in)) {
                // look for tokens to replace
                int last = 0;
                for (int i = 0; i < buffer.count; i++) {
                    int c = buffer.data[i];
                    // start of token
                    if (c == '@') {
                        // NOTE: + 1 to get past the initial '@'
                        String token = readToken(in, buffer, i + 1);
                        if (token == null) continue;

                        String value;
                        try {
                            value = bundle.getString(token);
                            if (value == null) continue;
                        }
                        catch (MissingResourceException e) {
                            continue;
                        }

                        // dump from last up to start of token
                        out.write(buffer.data, last, i - last);

                        // dump token replacement
                        byte[] utf8bytes = value.getBytes("UTF-8");
                        out.write(utf8bytes, 0, utf8bytes.length);

                        // advance past token
                        // NOTE: We assume token is ASCII, one byte per character
                        // NOTE: + 2 to get past the '@' delimiters
                        i += token.length() + 2;
                        last = i;
                    }
                }
                // dump whatever is left over
                out.write(buffer.data, last, buffer.count - last);
            }
        } catch(FileNotFoundException ex){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    private static String readToken(InputStream in, Buffer buffer, int offset)
    throws IOException {
        for (int i = offset; i <= buffer.count; i++) {
            if (i == buffer.count) {
                if (!buffer.extend(in)) break;
                continue;
            }
            int c = buffer.data[i];
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') continue;
            if (c == '@') return new String(buffer.data, offset, i-offset, "UTF-8");
            break;
        }
        return null;
    }

    private static List<ExtensionFilter> createFiltersFor(String exts, String types) {
        List<ExtensionFilter> filters = new LinkedList<ExtensionFilter>();
        StringTokenizer tokenizer = new StringTokenizer(exts, ",");
        StringTokenizer tokenizer2 = new StringTokenizer(types != null ? types : "", ",");
        while (tokenizer.hasMoreTokens()) {
            String ext = tokenizer.nextToken().trim();
            String type = tokenizer2.hasMoreTokens() ? tokenizer2.nextToken() : null;
            filters.add(new ExtensionFilter(ext, type));
        }
        return filters;
    }

    private static Locale getRequestedLocale(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Matcher matcher = RE_LOCALE_ID.matcher(uri);
        if (!matcher.matches()) return request.getLocale();

        String locid = matcher.group(1);
        String[] parts = locid.split("_");
        if (parts.length == 3) return new Locale(parts[0], parts[1], parts[2]);
        if (parts.length == 2) return new Locale(parts[0], parts[1]);
        return new Locale(parts[0]);
    }

    //
    // Classes
    //

    static class Buffer {
        // Data
        public byte[] data = new byte[4096];
        public int count;
        // Public methods
        public boolean fill(InputStream in) throws IOException {
            count = in.read(data);
            return count != -1;
        }
        public boolean extend(InputStream in) throws IOException {
            // increase storage
            if (this.count == data.length) {
                byte[] array = new byte[data.length*2];
                System.arraycopy(data, 0, array, 0, this.count);
                data = array;
            }
            // now try filling more
            int count = in.read(data, this.count, data.length-this.count);
            if (count != -1) this.count += count;
            return count != -1;
        }
    }

    static class ExtensionFilter implements FilenameFilter {
        // Data
        private String ext;
        private String type;
        // Constructors
        public ExtensionFilter(String ext, String type) {
            this.ext = ext.toLowerCase();
            this.type = type;
        }
        // Public methods
        public String getType() { return type; }
        // FilenameFilter methods
        public boolean accept(File dir, String filename) {
            return filename.toLowerCase().endsWith(this.ext);
        }
    }

} // class InsertHelpBranding
