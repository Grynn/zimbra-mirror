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
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class TimeZoneTag extends BodyTagSupport {

	//
	// Data
	//

	protected TimeZone value;

	//
	// Public methods
	//

	public TimeZone getTimeZone() {
		return this.value;
	}

	// properties

	public void setValue(Object value) {
		if (value instanceof String) {
			this.value = TimeZone.getTimeZone(String.valueOf(value));
		}
		else {
			this.value = (TimeZone)value;
		}
	}

	//
	// TagSupport methods
	//

	public int doStartTag() throws JspException {
		// NOTE: This is to keep compatibility with JSTL
		if (this.value == null) {
			this.value = TimeZone.getTimeZone("GMT");
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		this.value = null;
		return EVAL_PAGE;
	}

} // class TimeZoneTag