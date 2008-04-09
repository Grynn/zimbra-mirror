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

import com.zimbra.cs.taglib.tag.i18n.I18nUtil;

import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class SetBundleTag extends SimpleTagSupport  {

	//
	// Data
	//

	protected String basename;
	protected String var = I18nUtil.DEFAULT_BUNDLE_VAR;
	protected int scope = I18nUtil.DEFAULT_SCOPE_VALUE;
	protected boolean force;

	//
	// Public methods
	//

	public void setBasename(String basename) {
		this.basename = basename;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = I18nUtil.getScope(scope);
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	//
	// SimpleTag methods
	//
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		String basename = I18nUtil.makeBasename(pageContext, this.basename);
		if (this.force) {
			I18nUtil.clearBundle(pageContext, this.var, this.scope, basename);
		}
		ResourceBundle bundle = I18nUtil.findBundle(pageContext, this.var, this.scope, basename);
		pageContext.setAttribute(this.var, bundle, this.scope);

		// clear state
		this.basename = null;
		this.var = I18nUtil.DEFAULT_BUNDLE_VAR;
		this.scope = I18nUtil.DEFAULT_SCOPE_VALUE;
		this.force = false;
	}

} // class SetBundleTag