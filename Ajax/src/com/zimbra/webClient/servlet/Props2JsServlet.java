package com.zimbra.webClient.servlet;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

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
 * Messages.one = "One";
 * Messages.two = "TwoTwo";
 * Messages.three = "ThreeThreeThree";
 * </pre>
 * <p>
 * <strong>Note:</strong>
 * The implementation assumes that the basename of the resource bundle
 * will always be "/msgs/" concatenated with the filename without the
 * extension.
 * 
 * @author Andy Clark
 */
public class Props2JsServlet 
	extends HttpServlet {

    // Constants
    
    private static final String BASENAME_PREFIX = "/msgs/";
    
    // HttpServlet methods
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException {
        PrintWriter out = resp.getWriter();

        Locale locale = req.getLocale();
        
        String requestUri = req.getRequestURI();
        String filename = requestUri.substring(requestUri.lastIndexOf('/')+1);
        String classname = filename.substring(0, filename.lastIndexOf('.'));
        String basename = BASENAME_PREFIX+classname;
        
        /***
        resp.setContentType("text/html");
        out.println("<h1>Information</h1>");
        out.println("<li>Locale: "+locale);
        out.println("<li>FileName: "+filename);
        out.println("<li>ClassName: "+classname);
        out.println("<li>BaseName: "+basename);
        out.println("<li>ContextPath: "+req.getContextPath());
        out.println("<li>PathInfo: "+req.getPathInfo());
        out.println("<li>PathTranslated: "+req.getPathTranslated());
        out.println("<li>QueryString: "+req.getQueryString());
        out.println("<li>RequestURI: "+req.getRequestURI());
        out.println("<li>RequestURL: "+req.getRequestURL());
        
        out.println("<h1>Output</h1>");
        out.println("<pre>");
        /***/
        resp.setContentType("text/plain");
        /***/
        out.println("// Basename: "+basename);
        out.println("// Locale: "+locale);
        out.println();
        out.println("function "+classname+"(){}");
        out.println();
        
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle(basename, locale);
        }
        catch (MissingResourceException e) {
            out.println("// resource bundle not found");
            /***
            out.println("</pre>");
            /***/
            return;
        }

        Enumeration keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = bundle.getString(key);

            out.print(classname+"."+key+" = \"");
            int length = value.length();
            for (int i = 0; i < length; i++) {
                char c = value.charAt(i);
                switch (c) {
                    case '\t': out.print("\\t"); break;
                    case '\n': out.print("\\n"); break;
                    case '\r': out.print("\\r"); break;
                    case '\\': out.print("\\\\"); break;
                    case '"': out.print("\\\""); break;
                    default: {
                        if (c < 32 || c > 127) {
                            String cs = Integer.toString(c, 16);
                            out.print("\\u");
                            int cslen = cs.length();
                            for (int j = cslen; j < 4; j++) {
                                out.print('0');
                            }
                            out.print(cs);
                        }
                        else {
                            out.print(c);
                        }
                    }
                }
            }
            out.println("\";");
        }
        /***
        out.println("</pre>");
        /***/
    }
    
} // class Props2JsServlet