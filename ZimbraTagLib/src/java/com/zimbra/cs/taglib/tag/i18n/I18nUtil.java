package com.zimbra.cs.taglib.tag.i18n;

import java.util.*;
import java.text.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.el.*;
import javax.servlet.jsp.tagext.*;

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

	public static final String DEFAULT_SCOPE_NAME = PAGE;
	public static final int DEFAULT_SCOPE_VALUE = PageContext.PAGE_SCOPE;

	public static final String DEFAULT_STYLE_NAME = STYLE_MEDIUM;
	public static final int DEFAULT_STYLE_VALUE = DateFormat.MEDIUM;
	public static final String DEFAULT_TYPE_NAME = TYPE_DATE;

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

	public static Object findObject(PageContext pageContext, String var, int scope) {
		// NOTE: This assumes that the scope levels are defined as contiguously
		//       increasing values in the following order (from lowest to highest):
		//
		//         page, request, session, application 
		//
		//       This works fine in with the JSTL implementation but if we can't
		//       make this assumption in the future, we'll have to change this!
		for (int i = scope; i <= PageContext.APPLICATION_SCOPE; i++) {
			Object object = pageContext.getAttribute(var, i);
			if (object != null) {
				return object;
			}
		}
		return null;
	}

	// locale functions

	public static Locale getLocale(String id) {
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
		String skin = (String)pageContext.getRequest().getAttribute(A_SKIN);
		return skin != null ? "/skins/"+skin+basename : basename;
	}

	public static String makeBundleKey(PageContext pageContext, String basename) {
		return makeBundleKey(pageContext, basename, findLocale(pageContext));
	}
	public static String makeBundleKey(PageContext pageContext, String basename, Locale locale) {
		String skin = (String)pageContext.getRequest().getAttribute(A_SKIN);
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
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				try {
					bundle = ResourceBundle.getBundle(basename, locale, loader);
					pageContext.setAttribute(bundleKey, bundle, PageContext.APPLICATION_SCOPE);
				}
				catch (MissingResourceException e) {
					// ignore -- nothing we can do
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

	//
	// Constructors
	//
	
	private I18nUtil() {}

} // class I18nUtil