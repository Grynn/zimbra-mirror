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

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class BundleTag extends TagSupport {

	//
	// Data
	//

	protected ResourceBundle bundle;

	protected String basename;
	protected String prefix;
	protected boolean force;

	//
	// Public methods
	//

	public ResourceBundle getBundle() {
		return this.bundle;
	}

	public String getPrefix() {
		return this.prefix;
	}

	// properties

	public void setBasename(String basename) {
		this.basename = basename;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	//
	// TagSupport methods
	//

	public int doStartTag() throws JspException {
		PageContext pageContext = super.pageContext;
		String basename = I18nUtil.makeBasename(pageContext, this.basename);
		this.bundle = I18nUtil.findBundle(pageContext, null, -1, basename);
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		this.basename = null;
		this.prefix = null;
		this.bundle = null;
		this.force = false;

		return EVAL_PAGE;
	}

} // class BundleTag