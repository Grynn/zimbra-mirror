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
public abstract	class AbstractElement {

	public	static	final	int			OCCURRENCE_REQUIRED = 0; // one and only one 1:1 = ""
	public	static	final	int			OCCURRENCE_OPTIONAL = 1; // zero or one 0:1 = "?"
	public	static	final	int			OCCURRENCE_REQUIRED_MORE = 2; // one or more 1:* = "+"
	public	static	final	int			OCCURRENCE_OPTIONAL_MORE = 3; // zero or more 0:* = "*"

	protected	static	final	String		OCCURRENCE_REQUIRED_STR = "#REQUIRED";
	protected	static	final	String		OCCURRENCE_OPTIONAL_STR = "#OPTIONAL";

	protected	int			occurrence = OCCURRENCE_REQUIRED;

	public	static	final	int			TYPE_REQUEST = 1;
	public	static	final	int			TYPE_RESPONSE = 2;

	protected	int			type = TYPE_REQUEST;
	
	protected	String		name = null;
	protected	String		description = null;

	/**
	 * Gets the item name.
	 * 
	 * @return	the name
	 */
	public	String	getName() {
		return	this.name;
	}

	/**
	 * Gets the item description.
	 * 
	 * @return	the description
	 */
	public	String	getDescription() {
		return	this.description;
	}

	/**
	 * Sets the item description.
	 * 
	 * @param	description 	the description
	 */
	public	void	setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the occurrence.
	 * 
	 * @return	the occurrence (see <code>OCCURRENCE_</code> constants)
	 */
	public	int		getOccurrence() {
		return	this.occurrence;
	}

	/**
	 * Sets the occurrence.
	 * 
	 * @param	occurence	the occurrence (see <code>OCCURRENCE_</code> constants)
	 */
	public	void	setOccurrence(int occurence) {
		this.occurrence = occurence;
	}

	/**
	 * Checks if this element is required.
	 * 
	 * @return	<code>true</code> if this element is required
	 */
	public	boolean		isRequired() {
		return	(this.occurrence == OCCURRENCE_REQUIRED);
	}

	/**
	 * Gets the occurence from the string.
	 * 
	 * @param	str		the string
	 * @return	the occurenace (see <code>OCCURENCE_</code> constants)
	 */
	protected	static		int		getOccurenceFromString(String str) {
		if (str.endsWith("?"))
			return	OCCURRENCE_OPTIONAL;

		if (str.endsWith("+"))
			return	OCCURRENCE_REQUIRED_MORE;

		if (str.endsWith("*"))
			return	OCCURRENCE_OPTIONAL_MORE;

		
		return	OCCURRENCE_REQUIRED;
	}

	/**
	 * Gets the element type.
	 * 
	 * @return	the element type (see <code>TYPE_</code> constants
	 */
	public	int		getType() {
		return	this.type;
	}

	/**
	 * Checks if the element is the request.
	 * 
	 * @return	<code>true</code> if the element is the request
	 */
	public	boolean	isRequest() {
		return	(this.type == TYPE_REQUEST);
	}

	/**
	 * Checks if the element is the response.
	 * 
	 * @return	<code>true</code> if the element is the response
	 */
	public	boolean	isResponse() {
		return	(this.type == TYPE_RESPONSE);
	}

}
