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

import com.zimbra.doc.soap.Service;
import java.util.*;
import com.sun.javadoc.*;

/**
 * 
 * @author sposetti
 *
 */
public 	class ServiceDocletListener	extends	DocletListener {
	
	public	static	final	String			TAG_SERVICE_DESCRIPTION = "@zm-service-description";

	private	Service		service = null;
	
	/**
	 * Constructor.
	 * 
	 * @param		service		the service
	 */
	public	ServiceDocletListener(Service service) {
		super(service.getClassName());
		this.service = service;
	}

	/**
	 * Called when a registered class is found.
	 * 
	 * @param	tags		the tags
	 */
	public	void		tagsEvent(Tag[] tags) {
		String description = getTagText(tags, TAG_SERVICE_DESCRIPTION);
		this.service.setDescription(description);
	}
	
} // end ServiceDocletListener class