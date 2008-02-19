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

public class FormatNumberTag extends BodyTagSupport  {

	//
	// Data
	//

	protected Number value;
	protected String type = I18nUtil.DEFAULT_NUMBER_TYPE_NAME;
	protected String pattern;
	protected String currencyCode;
	protected String currencySymbol;
	protected Boolean groupingUsed;
	protected int maxIntegerDigits = -1;
	protected int minIntegerDigits = -1;
	protected int maxFractionDigits = -1;
	protected int minFractionDigits = -1;
	protected String var;
	protected int scope = I18nUtil.DEFAULT_SCOPE_VALUE;

	//
	// Public methods
	//

	public void setValue(String value) {
		try {
			this.value = Double.valueOf(value);
		}
		catch (NumberFormatException e) {
			this.value = Long.valueOf(value);
		}
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setCurrencyCode(String code) {
		this.currencyCode = code;
	}

	public void setCurrencySymbol(String symbol) {
		this.currencySymbol = symbol;
	}

	public void setGroupingUsed(boolean used) {
		this.groupingUsed = used;
	}

	public void setMaxIntegerDigits(int digits) {
		this.maxIntegerDigits = digits;
	}

	public void setMinIntegerDigits(int digits) {
		this.minIntegerDigits = digits;
	}

	public void setMaxFractionDigits(int digits) {
		this.maxFractionDigits = digits;
	}

	public void setMinFractionDigits(int digits) {
		this.minFractionDigits = digits;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = I18nUtil.getScope(scope);
	}

	//
	// SimpleTag methods
	//

	public int doStartTag() throws JspException {
		return this.value == null ? EVAL_BODY_BUFFERED : SKIP_BODY;
	}

	public int doAfterBody() throws JspException {
		String text = getBodyContent().getString().trim();
		setValue(text);
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		PageContext pageContext = this.pageContext;

		// create formatter
		Locale locale = I18nUtil.findLocale(pageContext);
		NumberFormat formatter;
		if (this.pattern != null) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
			formatter = new DecimalFormat(this.pattern, symbols);
		}
		else {
			String type = this.type != null ? this.type : I18nUtil.DEFAULT_NUMBER_TYPE_NAME;
			if (I18nUtil.TYPE_NUMBER.equals(type)) {
				formatter = NumberFormat.getNumberInstance(locale);
			}
			else if (I18nUtil.TYPE_CURRENCY.equals(type)) {
				formatter = NumberFormat.getCurrencyInstance(locale);
				if (this.currencyCode != null) {
					try {
						Currency currency = Currency.getInstance(this.currencyCode);
						formatter.setCurrency(currency);
					}
					catch (Exception e) {
						throw new JspException("invalid currency", e);
					}
				}
				if (this.currencySymbol != null) {
					DecimalFormat decimalFormatter = (DecimalFormat)formatter;
					DecimalFormatSymbols symbols = decimalFormatter.getDecimalFormatSymbols();
					symbols.setCurrencySymbol(this.currencySymbol);
					decimalFormatter.setDecimalFormatSymbols(symbols);
				}
			}
			else {
				formatter = NumberFormat.getPercentInstance(locale);
			}

			if (this.groupingUsed != null) {
				formatter.setGroupingUsed(this.groupingUsed);
			}
			if (this.maxIntegerDigits != -1) {
				formatter.setMaximumIntegerDigits(this.maxIntegerDigits);
			}
			if (this.minIntegerDigits != -1) {
				formatter.setMinimumIntegerDigits(this.minIntegerDigits);
			}
			if (this.maxFractionDigits != -1) {
				formatter.setMaximumFractionDigits(this.maxFractionDigits);
			}
			if (this.minFractionDigits != -1) {
				formatter.setMinimumFractionDigits(this.minFractionDigits);
			}
		}

		// format message
		String message = this.value instanceof Double || this.value instanceof Float
					   ? formatter.format(this.value.doubleValue())
					   : formatter.format(this.value.longValue());

		// output string
		if (this.var == null) {
			try {
				pageContext.getOut().print(message);
			}
			catch (IOException e) {
				throw new JspException(e);
			}
		}

		// save variable
		else {
			pageContext.setAttribute(this.var, message, this.scope);
		}

		// clear state
		this.value = null;
		this.type = I18nUtil.DEFAULT_NUMBER_TYPE_NAME;
		this.pattern = null;
		this.currencyCode = null;
		this.currencySymbol = null;
		this.groupingUsed = null;
		this.maxIntegerDigits = -1;
		this.minIntegerDigits = -1;
		this.maxFractionDigits = -1;
		this.minFractionDigits = -1;
		this.var = null;
		this.scope = I18nUtil.DEFAULT_SCOPE_VALUE;

		return EVAL_PAGE;
	} // doEndTag():int

} // class FormatNumberTag