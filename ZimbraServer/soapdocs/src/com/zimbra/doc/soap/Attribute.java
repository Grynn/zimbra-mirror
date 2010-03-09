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

package com.zimbra.doc.soap;

/**
 * 
 * @author sposetti
 *
 */
public	class	Attribute	extends	AbstractElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private	Value[]	values = new Value[0];

	/**
	 * Checks if the attribute has values.
	 * 
	 * @return	<code>true</code> if the attribute has values
	 */
	public	boolean		hasValues() {
		return	(values.length > 0);
	}

	/**
	 * Gets the values.
	 * 
	 * @return	an array of values or an empty array for none
	 */
	public	Value[]	getValues() {
		return	values;
	}

	/**
	 * Gets the values.
	 * 
	 * @return	an array of values or an empty array for none
	 */
	public	String[]	getValuesAsStringArray() {
		String[]	str = new String[values.length];
		
		for (int i=0; i < values.length; i++)
			str[i] = values[i].getName();
		
		return	str;
	}

	/**
	 * Gets the values as a string.
	 * 
	 * @param	delim		the delimiter
	 * @return	a string representation of the values or an empty string for none
	 */
	public	String	getValuesAsString(String delim) {
		StringBuffer	buf = new StringBuffer();
		
		for (int i=0; i < values.length; i++) {
			buf.append(values[i].getName());
			if (i < values.length)
				buf.append(delim);				
		}

		return	buf.toString();
	}

	/**
	 * 
	 * @author sposetti
	 *
	 */
	public	class		Value	extends	AbstractElement implements java.io.Serializable {

		private static final long serialVersionUID = 1L;

	} // end inner Value class

} // end Attribute class
