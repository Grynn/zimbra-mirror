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

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentHandler;
import org.dom4j.QName;

/**
 * 
 * @author sposetti
 *
 */
public	class	ServiceDispatcher extends DocumentDispatcher {
	
	private	Root	root = null;
	private	Service		service = null;
	private	ServiceRegisterListener	listener = null;
	
	/**
	 * Constructor.
	 * 
	 */
	ServiceDispatcher (Root root, Service service, ServiceRegisterListener listener) {
		this.root = root;
		this.service = service;
		this.listener = listener;
	}
	
	/**
	 * Registers the service document dispather.
	 * 
	 */
	public void registerHandler(QName qname, DocumentHandler handler) {
		if (listener.registerCommand(qname, handler))
			this.service.addCommand(handler.getClass().getName(), qname.getNamespace().getURI());
	}
	
} // end class ServiceDispatcher