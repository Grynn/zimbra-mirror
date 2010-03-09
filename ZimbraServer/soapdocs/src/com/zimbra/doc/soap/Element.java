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
public class Element extends AbstractElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public	static	final	int			TYPE_NONE = 0;
	public	static	final	int			TYPE_REQUEST = 1;
	public	static	final	int			TYPE_RESPONSE = 2;

	public	static	final	int			OCCURRENCE_REQUIRED_MORE = 2; // one or more
	public	static	final	int			OCCURRENCE_OPTIONAL_MORE = 3; // zero or more

	private	Element		parent	= null;
	private	Element[]	elements = new Element[0];
	private	Attribute[]	attributes = new Attribute[0];

	private	int			type = TYPE_NONE;
	
	/**
	 * Gets the parent element.
	 * 
	 * @return	the parent element or <code>null</code> for none
	 */
	public	Element	getParent() {
		return	this.parent;
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

	/**
	 * Checks if the element has elements.
	 * 
	 * @return	<code>true</code> if the element has elements
	 */
	public	boolean		hasAElements() {
		return	(elements.length > 0);
	}

	/**
	 * Checks if the element has attributes.
	 * 
	 * @return	<code>true</code> if the element has attributes
	 */
	public	boolean		hasAttributes() {
		return	(attributes.length > 0);
	}

	/**
	 * Gets the attributes.
	 * 
	 * @return	an array of values or an empty array for none
	 */
	public	Attribute[]	getAttributes() {
		return	attributes;
	}

	/**
	 * Checks if this element is required.
	 * 
	 * @return	<code>true</code> if this element is required
	 */
	public	boolean		isRequired() {
		return	(this.occurrence == OCCURRENCE_REQUIRED || this.occurrence == OCCURRENCE_REQUIRED_MORE);
	}

} // end Element class
