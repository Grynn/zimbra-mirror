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

import com.zimbra.doc.soap.util.StringUtil;
import java.util.*;

/**
 * 
 * @author sposetti
 *
 */
public	class	Attribute	extends	AbstractElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public	static	final	String				CDATA = "CDATA";
	
	private	static	final	String				REGEX_ATTRIBUTE_TAG_DELIM = "[ \t]+";
	private	static	final	String				REGEX_VALUES_DELIM = "[|]+";

	private	List<Value>	values = new LinkedList<Value>();
	private	String		elementName = null;
	
	/**
	 * Constructor.
	 * 
	 * @param	name		the name
	 * @param	description	the description
	 * @param	type		the element type (see <code>TYPE_</code> constants)
	 */
	private	Attribute(String elementName, String name, String description, int type, List<Value> values, int occurrence) {
		this.elementName = elementName;
		this.name = name;
		this.description = description;
		this.type = type;
		this.values = values;
		this.occurrence = occurrence;
	}

	/**
	 * Creates an attribute by parsing the tag text.
	 * 
	 * @param	tagText		the tag text
	 * @param	req		if <code>true</code>, the element is related to the request
	 * @return	the attribute
	 */
	public	static	Attribute	createAttribute(String tagText, int	type) {
		
		String[] tokens = tagText.split(REGEX_ATTRIBUTE_TAG_DELIM);

		int idx = 0;
		String	elementName = tokens[idx++];
		String	name = tokens[idx++];
		String	content = tokens[idx++];
		String	occStr = tokens[idx++];
		String 	description = StringUtil.createString(tokens, idx, " ");

		List<Value>	values = parseValuesFromContent(content);

		int	occurrence = parseOccurrence(occStr);

		return	 new Attribute(elementName, name, description, type, values, occurrence);
	}

	/**
	 * Creates an attribute by parsing the tag text.
	 * 
	 * @param	tagText		the tag text
	 * @param	req		if <code>true</code>, the element is related to the request
	 * @return	the attribute
	 */
	public	static	Attribute	createCDATAAttribute(String elementName, String description, int type) {
		return	new Attribute(elementName, Attribute.CDATA, description, type, new LinkedList(), AbstractElement.OCCURRENCE_REQUIRED);
	}
	
	/**
	 * Parses the values list from the tag content.
	 * 
	 * @param	content		the tag content string
	 * @return	a list of values
	 */
	private	static	List<Value>		parseValuesFromContent(String content) {
		List<Value>		values = new LinkedList<Value>();
		
		if (content.startsWith("(") && content.endsWith(")")) {
			content = content.substring(1, content.length()-1);
			String[]	valuesArray = content.split(REGEX_VALUES_DELIM);
			for (int i=0; i < valuesArray.length; i++) {
				values.add(new Value(valuesArray[i]));
			}
		}
			
		return	values;
	}

	/**
	 * Parses the occurrence.
	 * 
	 * @param	occStr		the occurrence string
	 * @return	the occurrence
	 */
	private	static	int		parseOccurrence(String occStr) {
		if (occStr.equals(OCCURRENCE_OPTIONAL_STR))
			return	OCCURRENCE_OPTIONAL;
			
		return	OCCURRENCE_REQUIRED;
	}

	/**
	 * Gets the element name.
	 * 
	 * @return	the element name
	 */
	public	String		getElementName() {
		return	this.elementName;
	}
	
	/**
	 * Checks if the attribute has values.
	 * 
	 * @return	<code>true</code> if the attribute has values
	 */
	public	boolean		hasValues() {
		return	(values.size() > 0);
	}

	/**
	 * Gets the values.
	 * 
	 * @return	a list of values
	 */
	public	List<Value>	getValues() {
		return	values;
	}

	/**
	 * Gets the values.
	 * 
	 * @return	an array of values or an empty array for none
	 */
	public	String[]	getValuesAsStringArray() {
		String[]	str = new String[values.size()];
		
		Value[] array = (Value[])values.toArray(new Value[values.size()]);
		for (int i=0; i < array.length; i++)
			str[i] = array[i].getName();
		
		return	str;
	}

	/**
	 * Gets the values as a string.
	 * 
	 * @return	a string representation of the values or an empty string for none
	 */
	public	String	getValuesAsString() {
		return	getValuesAsString(", ");
	}

	/**
	 * Gets the values as a string.
	 * 
	 * @param	delim		the delimiter
	 * @return	a string representation of the values or an empty string for none
	 */
	public	String	getValuesAsString(String delim) {
		StringBuffer	buf = new StringBuffer();

		if (values == null || values.size() <= 0)
			return	buf.toString();

		buf.append("[");

		Value[] array = (Value[])values.toArray(new Value[values.size()]);
		for (int i=0; i < array.length; i++) {
			buf.append(array[i].getName());
			if (i < (array.length-1))
				buf.append(delim);				
		}

		buf.append("]");

		return	buf.toString();
	}

    /**
     * Returns a string representation of this object.
     * 
     * @return	a string representation of this object
     */
    public	String	toString() {
    	StringBuffer buf = new StringBuffer();

		buf.append("[attribute");
		buf.append(";hashCode=");
		buf.append(hashCode());
		buf.append(";name=");
		buf.append(this.getName());
		buf.append(";type=");
		buf.append(this.getType());
		buf.append(";occurrence=");
		buf.append(this.getOccurrence());
		buf.append(";values=");
		buf.append(this.getValuesAsString());
		buf.append("]");

		return	buf.toString();
    }

	/**
	 * 
	 * @author sposetti
	 *
	 */
	public	static	class		Value	extends	AbstractElement implements java.io.Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor.
		 * 
		 */
		Value(String name) {
			this.name = name;
		}
		
	} // end inner Value class

} // end Attribute class
