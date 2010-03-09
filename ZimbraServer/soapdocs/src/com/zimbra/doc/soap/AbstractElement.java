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

	public	static	final	int			OCCURRENCE_REQUIRED = 0; // one and only one
	public	static	final	int			OCCURRENCE_OPTIONAL = 1; // zero or one

	protected	int			occurrence = OCCURRENCE_OPTIONAL;

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
	 * Checks if this element is required.
	 * 
	 * @return	<code>true</code> if this element is required
	 */
	public	boolean		isRequired() {
		return	(this.occurrence == OCCURRENCE_REQUIRED);
	}

}
