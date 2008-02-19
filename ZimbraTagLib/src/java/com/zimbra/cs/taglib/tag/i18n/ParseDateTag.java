/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008 Zimbra, Inc.
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

package com.zimbra.cs.taglib.tag.i18n;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class ParseDateTag extends BodyTagSupport {

	//
	// Data
	//

	protected String value;
	protected String type;
	protected String dateStyle;
	protected String timeStyle;
	protected String pattern;
	protected TimeZone timeZone;
	protected Locale locale;
	protected String var;
	protected int scope = I18nUtil.DEFAULT_SCOPE_VALUE;

	//
	// Public methods
	//

	public void setValue(String value) {
		this.value = value;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDateStyle(String style) {
		this.dateStyle = style;
	}

	public void setTimeStyle(String style) {
		this.timeStyle = style;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = (TimeZone)timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = TimeZone.getTimeZone(String.valueOf(timeZone));
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public void setLocale(String locale) {
		this.locale = I18nUtil.getLocale(locale);
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = I18nUtil.getScope(scope);
	}

	//
	// TagSupport methods
	//

	public int doStartTag() throws JspException {
		return this.value == null ? EVAL_BODY_BUFFERED : SKIP_BODY;
	}

	public int doAfterBody() throws JspException {
		this.value = getBodyContent().getString().trim();
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		PageContext pageContext = this.pageContext;
		String value = this.value;
		Locale locale = this.locale;
		if (locale == null) locale = I18nUtil.findLocale(pageContext);

		// create parser
		DateFormat parser = this.pattern != null ? new SimpleDateFormat(this.pattern, locale) : null;
		if (parser == null) {
			int dateStyle = I18nUtil.getStyle(this.dateStyle);
			int timeStyle = I18nUtil.getStyle(this.timeStyle);
			if (I18nUtil.TYPE_DATE.equalsIgnoreCase(this.type)) {
				parser = DateFormat.getDateInstance(dateStyle, locale);
			}
			else if (I18nUtil.TYPE_TIME.equalsIgnoreCase(this.type)) {
				parser = DateFormat.getTimeInstance(timeStyle, locale);
			}
			else {
				parser = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
			}
		}

		TimeZone timeZone = this.timeZone;
		if (timeZone == null) {
			TimeZoneTag timeZoneTag = (TimeZoneTag)findAncestorWithClass(this, TimeZoneTag.class);
			if (timeZoneTag != null) timeZone = timeZoneTag.getTimeZone();
			if (timeZone == null) timeZone = I18nUtil.findTimeZone(pageContext);
		}
		parser.setTimeZone(timeZone);

		// parse value
		Date date = null;
		try {
			date = parser.parse(value);
		}
		catch (ParseException e) {
			throw new JspException(e);
		}

		// output
		if (this.var == null) {
			try {
				// NOTE: The JSTL impl doesn't use the default formatter
				//       but that defeats the whole purpose. So we'll do
				//       the right thing.
				// TODO: use default formatter
				pageContext.getOut().print(date);
			}
			catch (IOException e) {
				throw new JspException(e);
			}
		}

		// save variable
		else {
			pageContext.setAttribute(this.var, date, this.scope);
		}

		// clear state
		this.value = null;
		this.type = null;
		this.dateStyle = null;
		this.timeStyle = null;
		this.pattern = null;
		this.timeZone = null;
		this.locale = null;
		this.var = null;
		this.scope = I18nUtil.DEFAULT_SCOPE_VALUE;

		return EVAL_PAGE;
	}

} // class ParseDateTag