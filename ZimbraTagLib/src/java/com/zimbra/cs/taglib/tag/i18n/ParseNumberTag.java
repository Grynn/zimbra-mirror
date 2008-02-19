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
import java.text.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class ParseNumberTag  extends BodyTagSupport {

	//
	// Data
	//

	protected String value;
	protected String type = I18nUtil.DEFAULT_NUMBER_TYPE_NAME;
	protected String pattern;
	protected Locale parseLocale;
	protected Boolean integerOnly;
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

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setParseLocale(Locale locale) {
		this.parseLocale = locale;
	}

	public void setIntegerOnly(boolean integerOnly) {
		this.integerOnly = integerOnly;
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
		Locale locale = this.parseLocale;
		if (locale == null) locale = I18nUtil.findLocale(pageContext);

		// create parser
		NumberFormat parser;
		if (this.pattern != null) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
			parser = new DecimalFormat(this.pattern, symbols);
		}
		else {
			String type = this.type != null ? this.type : I18nUtil.DEFAULT_NUMBER_TYPE_NAME;
			if (I18nUtil.TYPE_NUMBER.equals(type)) {
				parser = NumberFormat.getNumberInstance(locale);
			}
			else if (I18nUtil.TYPE_CURRENCY.equals(type)) {
				parser = NumberFormat.getCurrencyInstance(locale);
			}
			else {
				parser = NumberFormat.getPercentInstance(locale);
			}

			if (this.integerOnly != null) {
				parser.setParseIntegerOnly(this.integerOnly);
			}
		}

		// parse value
		Number number = null;
		try {
			number = parser.parse(value);
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
				pageContext.getOut().print(number);
			}
			catch (IOException e) {
				throw new JspException(e);
			}
		}

		// save variable
		else {
			pageContext.setAttribute(this.var, number, this.scope);
		}

		// clear state
		this.value = null;
		this.type = I18nUtil.DEFAULT_NUMBER_TYPE_NAME;
		this.pattern = null;
		this.parseLocale = null;
		this.integerOnly = null;
		this.var = null;
		this.scope = I18nUtil.DEFAULT_SCOPE_VALUE;

		return EVAL_PAGE;
	}

} // class ParseNumberTag