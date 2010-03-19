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

import com.zimbra.doc.soap.*;
import com.zimbra.doc.soap.util.StringUtil;
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
	private	DocletDataModelProvider provider = null;
	private	ServiceDispatcherListener	listener = null;
	
	/**
	 * Constructor.
	 * 
	 */
	public	ServiceDispatcher (Root root, Service service, DocletDataModelProvider provider, ServiceDispatcherListener listener) {
		this.root = root;
		this.service = service;
		this.provider = provider;
		this.listener = listener;
	}
	
	/**
	 * Registers the command document handler.
	 * 
	 */
	public void registerHandler(QName qname, DocumentHandler handler) {
		if (listener.registerCommand(qname, handler)) {
			String className = handler.getClass().getName();
			String name = StringUtil.getClassName(className);
			String namespace = qname.getNamespace().getURI();
			
			Command cmd = new Command(this.service, className, name, namespace);
				
			if (cmd.getName().equals("Browse") || cmd.getName().equals("ChangePassword")) {
				// load command file source
		    	ZmDoclet.registerListener(new CommandDocletListener(this.service, cmd));
		
		    	String cmdClassName = cmd.getClassName();
		    	String	srcPath = this.provider.buildSourcePath(cmdClassName);
		  
		    	// read file source
		    	String[] args = new String[] {
		    			"-doclet",
		    			ZmDoclet.class.getName(),
		    			srcPath
		    	};
		
				com.sun.tools.javadoc.Main.execute(args);
			}

			this.service.addCommand(cmd);
		}
	}
	
} // end class ServiceDispatcher