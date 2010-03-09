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

package com.zimbra.doc.soap.doclet;

import java.util.*;
import com.sun.javadoc.*;

/**
 * 
 * @author sposetti
 *
 */
public abstract	class DocletListener	{
	
	private	String			className = null;
	
	/**
	 * Constructor.
	 * 
	 * @param		className		the class to register
	 */
	public	DocletListener(String className) {
		this.className = className;
	}

	/**
	 * Gets the class name.
	 * 
	 * @return	the class name
	 */
	public	String		getClassName() {
		return	this.className;
	}
	
	/**
	 * Called when a registered tag is found.
	 * 
	 * @param	tag		the tag
	 */
	public	abstract	void		tagsEvent(Tag[] tags);
	
} // end DocletListener class