/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
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
package com.zimbra.cs.taglib.tag.i18n;

import com.zimbra.client.ZMailbox;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.taglib.ZJspSession;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.el.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;

public class I18nUtil {

	//
	// Constants
	//

	public static final String PAGE = "page";
	public static final String REQUEST = "request";
	public static final String SESSION = "session";
	public static final String APPLICATION = "application";

	public static final String STYLE_SHORT = "short";
	public static final String STYLE_MEDIUM = "medium";
	public static final String STYLE_LONG = "long";
	public static final String STYLE_FULL = "full";

	public static final String TYPE_DATE = "date";
	public static final String TYPE_TIME = "time";
	public static final String TYPE_BOTH = "both";

	public static final String TYPE_NUMBER = "number";
	public static final String TYPE_CURRENCY = "currency";
	public static final String TYPE_PERCENTAGE = "percent";

	public static final String DEFAULT_SCOPE_NAME = PAGE;
	public static final int DEFAULT_SCOPE_VALUE = PageContext.PAGE_SCOPE;

	public static final String DEFAULT_STYLE_NAME = STYLE_MEDIUM;
	public static final int DEFAULT_STYLE_VALUE = DateFormat.MEDIUM;
	public static final String DEFAULT_DATE_TYPE_NAME = TYPE_DATE;
	public static final String DEFAULT_NUMBER_TYPE_NAME = TYPE_NUMBER;

	private static final String PACKAGE = I18nUtil.class.getPackage().getName();

	public static final String DEFAULT_LOCALE_VAR = PACKAGE+".locale";
	public static final String DEFAULT_TIMEZONE_VAR = PACKAGE+".timezone";
	public static final String DEFAULT_BUNDLE_VAR = PACKAGE+".bundle";
	public static final String DEFAULT_BUNDLE_KEY = PACKAGE+".key";

	private static final String A_SKIN = "skin";

	//
	// Static functions
	//

	// messages

	public static String getLocalizedMessage(PageContext pageContext,
											 String key) {
		return getLocalizedMessage(pageContext, key, null, DEFAULT_BUNDLE_VAR, DEFAULT_SCOPE_VALUE, null);
	}

	public static String getLocalizedMessage(PageContext pageContext,
											 String key, Object[] args) {
		return getLocalizedMessage(pageContext, key, args, DEFAULT_BUNDLE_VAR, DEFAULT_SCOPE_VALUE, null);
	}

	public static String getLocalizedMessage(PageContext pageContext,
											 String key,
											 String basename) {
		return getLocalizedMessage(pageContext, key, null, null, -1, basename);
	}

	public static String getLocalizedMessage(PageContext pageContext,
											 String key, Object[] args,
											 String basename) {
		return getLocalizedMessage(pageContext, key, args, null, -1, basename);
	}

	private static String getLocalizedMessage(PageContext pageContext,
											  String key, Object[] args,
											  String var, int scope,
											  String basename) {
		// get message pattern
		String pattern;
		try {
			ResourceBundle bundle = findBundle(pageContext, var, scope, basename);
			pattern = bundle.getString(key);
		}
		catch (Exception e) {
			pattern = "???"+key+"???";
		}

		// format message
		String message = pattern;
		if (args != null && args.length > 0) {
			Locale locale = findLocale(pageContext);
			TimeZone timeZone = findTimeZone(pageContext);

			MessageFormat formatter = new MessageFormat(pattern, locale);
			for (Format format : formatter.getFormatsByArgumentIndex()) {
				if (format != null && format instanceof DateFormat) {
					((DateFormat)format).setTimeZone(timeZone);
				}
			}

			message = formatter.format(args);
		}

		return message;
	}

	// evaluate

	public static Object evaluate(PageContext pageContext, String expression, Class type)
	throws JspException {
		// is there anything to do?
		if (expression == null || expression.trim().length() == 0) {
			return null;
		}

		// evaluate
		ExpressionEvaluator evaluator = pageContext.getExpressionEvaluator();
		VariableResolver resolver = pageContext.getVariableResolver();
		// TODO: What should this be?
		FunctionMapper mapper = null;
		try {
			return evaluator.evaluate(expression, type, resolver, mapper);
		}
		catch (ELException e) {
			throw new JspException(e);
		}
	}

	// scope

	public static int getScope(String scope) {
		if (PAGE.equalsIgnoreCase(scope)) return PageContext.PAGE_SCOPE;
		if (REQUEST.equalsIgnoreCase(scope)) return PageContext.REQUEST_SCOPE;
		if (SESSION.equalsIgnoreCase(scope)) return PageContext.SESSION_SCOPE;
		if (APPLICATION.equalsIgnoreCase(scope)) return PageContext.APPLICATION_SCOPE;
		return DEFAULT_SCOPE_VALUE;
	}

	public static String getScope(int scope) {
		switch (scope) {
			case PageContext.PAGE_SCOPE: return PAGE;
			case PageContext.REQUEST_SCOPE: return REQUEST;
			case PageContext.SESSION_SCOPE: return SESSION;
			case PageContext.APPLICATION_SCOPE: return APPLICATION;
		}
		return DEFAULT_SCOPE_NAME;
	}

	// style

	public static int getStyle(String style) {
		if (STYLE_SHORT.equalsIgnoreCase(style)) return DateFormat.SHORT;
		if (STYLE_MEDIUM.equalsIgnoreCase(style)) return DateFormat.MEDIUM;
		if (STYLE_LONG.equalsIgnoreCase(style)) return DateFormat.LONG;
		if (STYLE_FULL.equalsIgnoreCase(style)) return DateFormat.FULL;
		return DEFAULT_STYLE_VALUE;
	}

	// object query

	public static Object findObject(PageContext pageContext, String var) {
		return findObject(pageContext, var, PageContext.PAGE_SCOPE);
	}

	public static Object findObject(PageContext pageContext, String var, int scope) {
		// NOTE: This assumes that the scope levels are defined as contiguously
		//       increasing values in the following order (from lowest to highest):
		//
		//         page, request, session, application
		//
		//       This works fine in with the JSTL implementation but if we can't
		//       make this assumption in the future, we'll have to change this!
		for (int i = scope; i <= PageContext.APPLICATION_SCOPE; i++) {
			try {
				Object object = pageContext.getAttribute(var, i);
				if (object != null) {
					return object;
				}
			}
			catch (Exception e) {
				// ignore -- usually session scope checks when no session
			}
		}
		return null;
	}

	// locale functions

	public static Locale getLocale(String id) {
        if ("nb-NO".equals(id)) // ybug - 2966850
            return new Locale("no", "NO", "NY");
        StringTokenizer tokenizer = new StringTokenizer(id, "_-");
		String language = tokenizer.nextToken();
		String country = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
		if (country != null) {
			String variant = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
			if (variant != null) {
				return new Locale(language, country, variant);
			}
			return new Locale(language, country);
		}
		return new Locale(language);
	}

	public static Locale findLocale(PageContext pageContext) {
		return findLocale(pageContext, DEFAULT_LOCALE_VAR, DEFAULT_SCOPE_VALUE);
	}

	public static Locale findLocale(PageContext pageContext, String var) {
		return findLocale(pageContext, var, DEFAULT_SCOPE_VALUE);
	}

	public static Locale findLocale(PageContext pageContext, String var, int scope) {
		Locale locale = (Locale) I18nUtil.findObject(pageContext, var, scope);
		// NOTE: For simplicity, just consider the primary request locale
		if (locale == null) locale = pageContext.getRequest().getLocale();
		if (locale == null) locale = Locale.getDefault();
		return locale;
	}

	// timezone query

	public static TimeZone findTimeZone(PageContext pageContext) {
		return findTimeZone(pageContext, DEFAULT_TIMEZONE_VAR, DEFAULT_SCOPE_VALUE);
	}

	public static TimeZone findTimeZone(PageContext pageContext, String var) {
		return findTimeZone(pageContext, var, DEFAULT_SCOPE_VALUE);
	}

	public static TimeZone findTimeZone(PageContext pageContext, String var, int scope) {
		TimeZone tz = (TimeZone) I18nUtil.findObject(pageContext, var, scope);
		if (tz == null) tz = TimeZone.getDefault();
		return tz;
	}

	// bundle query

	public static String makeBasename(PageContext pageContext, String basename) {
		String skin = (String)findObject(pageContext, A_SKIN);
		return skin != null ? "/skins/"+skin+basename : basename;
	}

	public static String makeBundleKey(PageContext pageContext, String basename) {
		return makeBundleKey(pageContext, basename, findLocale(pageContext));
	}
	public static String makeBundleKey(PageContext pageContext, String basename, Locale locale) {
		String skin = (String)findObject(pageContext, A_SKIN);
		if (skin == null) skin = "[unknown]";
		return skin+":"+basename+":"+locale+":"+DEFAULT_BUNDLE_KEY;
	}

	public static ResourceBundle findBundle(PageContext pageContext) {
		return findBundle(pageContext, DEFAULT_BUNDLE_VAR, DEFAULT_SCOPE_VALUE, null);
	}

	public static ResourceBundle findBundle(PageContext pageContext, String var) {
		return findBundle(pageContext, var, DEFAULT_SCOPE_VALUE, null);
	}

	public static ResourceBundle findBundle(PageContext pageContext, String var, int scope) {
		return findBundle(pageContext, var, scope, null);
	}

	public static ResourceBundle findBundle(PageContext pageContext,
											String var, int scope, String basename) {

		// first go directly to the main cache to find bundle
		ResourceBundle bundle = null;
		if (basename != null) {
			String bundleKey = makeBundleKey(pageContext, basename);
			bundle = (ResourceBundle)pageContext.getAttribute(bundleKey, PageContext.APPLICATION_SCOPE);
			// bundle has never been loaded, so load it now
			if (bundle == null) {
				Locale locale = findLocale(pageContext);
                /**
                 * bug 66698, 66765: The context ClassLoader is overridden so that we can
                 * transparently merge skin message files into the default ones allowing
                 * skins to independently override messages. Moved overriding of ClassLoader
                 * from JspServlet to here since the override needs to happen only in case
                 * of <fmt:setBundle> to load the correct bundle resource specific to the
                 * skin. There is no need to override class loader for all jsp pages.
                 */
                Thread thread = Thread.currentThread();
				ClassLoader oLoader = thread.getContextClassLoader();
                ClassLoader nLoader = oLoader;

                //override context class loader
                try {
                    nLoader = new ResourceLoader(oLoader, pageContext);
                    thread.setContextClassLoader(nLoader);
                }catch(Exception e) {
                    ZimbraLog.webclient.debug("FindBundle:error in overriding the class loader" + e);
                    e.printStackTrace();
                }
				try {
					bundle = ResourceBundle.getBundle(basename, locale, nLoader);
					pageContext.setAttribute(bundleKey, bundle, PageContext.APPLICATION_SCOPE);
				}
				catch (MissingResourceException e) {
					// ignore -- nothing we can do
                    ZimbraLog.webclient.debug("MissingResourceException:" + e);
                    e.printStackTrace();
				}
				catch (Exception e) {
					// ignore -- nothing we can do
                    ZimbraLog.webclient.debug("FindBundle:error in fetching the bundle resource" + e);
                    e.printStackTrace();
				}
                finally {
                    //restore old class loader
                    thread.setContextClassLoader(oLoader);
                }
			}
		}

		// find the bundle in scopes
		else if (var != null) {
			bundle = (ResourceBundle)I18nUtil.findObject(pageContext, var, scope);
		}

		// store bundle in desired scope
		if (bundle != null && var != null) {
			pageContext.setAttribute(var, bundle, scope);
		}

		// return bundle
		return bundle;
	}

	public static void clearBundle(PageContext pageContext,
								   String var, int scope, String basename) {
		if (basename != null) {
			String bundleKey = makeBundleKey(pageContext, basename);
			pageContext.removeAttribute(bundleKey, PageContext.APPLICATION_SCOPE);
		}
		if (var != null) {
			// Remove the var, if it exists, from the specified scope
			// down to the page scope. This is done so that a reference
			// in an EL expression (which in turn does a findAttribute)
			// will not find some other variable with the same name at
			// a lower scope. So this is just defensive programming.
			// NOTE: Assumes that the scope constants are contiguous.
			for (int i = scope; i >= PageContext.PAGE_SCOPE; i--) {
				pageContext.removeAttribute(var, i);
			}
		}
	}

	//
	// Constructors
	//
	
	private I18nUtil() {}

    static class ResourceLoader extends ClassLoader {

    //
    // Data
    //

    private PageContext pageContext;

    //
    // Constants
    //

    public static final String P_SKIN = "skin";
    public static final String P_DEFAULT_SKIN = "zimbraDefaultSkin";

    public static final String A_SKIN = "skin";
    protected static final String MANIFEST = "manifest.xml";


    //
    // Constructors
    //

    public ResourceLoader(ClassLoader parent, PageContext pageContext) {
        super(parent);
        this.pageContext = pageContext;
    }

    //
    //  Private Methods
    //

    String setSkin(ServletRequest request, ServletResponse response) {
        // start with if skin is already set as an attribute
        String skin = (String)request.getAttribute(A_SKIN);
//		ZimbraLog.webclient.debug("### request: "+skin);

        // is skin specified in request parameter?
        if (skin == null) {
            skin = request.getParameter(P_SKIN);
//		ZimbraLog.webclient.debug("### param: "+skin);
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
            PageContext context = this.pageContext;
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
            File manifest = new File(this.pageContext.getServletContext().getRealPath("/skins/"+skin+"/"+MANIFEST));
            if (!manifest.exists()) {
                ZimbraLog.webclient.debug("selected skin ("+skin+") doesn't exist");
                skin = null;
            }
        }

        // fall back to default skin
        if (skin == null) {
            skin = this.pageContext.getServletContext().getInitParameter(P_DEFAULT_SKIN);
//			ZimbraLog.webclient.debug("### default: "+skin);
        }

        // store in the request
        pageContext.setAttribute(A_SKIN, skin) ;

        return skin;
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

        String skin = (String)pageContext.getAttribute("skin");
        if (skin == null) {
            skin = this.setSkin(this.pageContext.getRequest(), this.pageContext.getResponse());
        }

        ZimbraLog.webclient.debug("omega:" + (this.pageContext.getServletContext().getRealPath("/skins/"+skin+basename)));

        File file = new File(this.pageContext.getServletContext().getRealPath("/skins/"+skin+basename));
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
                ZimbraLog.webclient.debug("FileNotFoundException:" + e);
            }
        }

        return stream;
    }

} // class ResourceLoader

} // class I18nUtil