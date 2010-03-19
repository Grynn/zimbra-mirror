/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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

package com.zimbra.doc.soap.util;

import java.util.*;
import com.zimbra.doc.soap.*;
import com.zimbra.doc.soap.template.*;

/**
 * This class represents a utility for string manipulation.
 * 
 * @author sposetti
 *
 */
public	class	StringUtil {


	/**
	 * Creates a string from the array.
	 * 
	 * @param	strs		a string array
	 * @param	startIdx	the starting idx
	 * @param	delim		the string delimiter
	 * @return	the resulting string
	 */
	public	static	String	createString(String[] strs, int startIdx, String delim) {
		StringBuffer buf = new StringBuffer();
		
		if (strs == null || startIdx > (strs.length-1))
			return	buf.toString();
		
		for (int i=startIdx; i < strs.length; i++) {
			buf.append(strs[i]);
			if (i < (strs.length-1))
				buf.append(delim);
		}
		
		return	buf.toString();
	}
	
	/**
	 * Gets the class name from a FQCN.
	 * 
	 * @return	the class name
	 */
	public	static	String	getClassName(String className) {
		String[]	strs = className.split("\\.");
		
		String	cname = strs[strs.length-1];
		
		if (cname.endsWith("Service"))
			cname = cname.substring(0, cname.length() - "Service".length());
		
		return	cname;
	}

}