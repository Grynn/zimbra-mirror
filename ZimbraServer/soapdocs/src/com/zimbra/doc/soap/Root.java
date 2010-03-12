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

import com.zimbra.soap.DocumentService;
import com.zimbra.doc.soap.doclet.*;
import java.util.*;

/**
 * This class represents the root data model for the SOAP API.
 * 
 * @author sposetti
 *
 */
public	class	Root {

	public	static	final	String			PROP_SERVICE_LIST = "service-list";
	public	static	final	String			PROP_SERVICE_SRC_DIR = "service-src-dir";
	public	static	final	String			PROP_SERVICE_REGISTER_LISTENER = "service-register-listener";
	
	private	List<Service>	services = new LinkedList<Service>();
	private	String			serviceSrcDir = null;
	
	/**
	 * Constructor.
	 * 
	 * @param	context		the context properties
	 */
    public Root(Map	context) {
    	
    	List<DocumentService> docs = getServiceListProp(context);
    	this.serviceSrcDir = getServiceSrcDirProp(context);
    	
    	Iterator it = docs.iterator();
    	while(it.hasNext()) {
    		DocumentService s = (DocumentService)it.next();
    		addService(s);
    	}
    	
    	ServiceRegisterListener listener = getServiceRegisterListenerProp(context);
    	initialize(listener);    	
    }

    /**
     * Gets the service list property.
     * 
     * @param	context		the doc root context
     * @return	a list of {@link DocumentService} objects
     */
    private	List<DocumentService>	getServiceListProp(Map context) {
    	
    	Object obj = context.get(PROP_SERVICE_LIST);
    	
    	if (obj == null)
    		throw new IllegalArgumentException("must specify the service list");

    	if ((obj instanceof List) == false)
    		throw new IllegalArgumentException("service list must be a list of DocumentService objects");

    	return	(List)obj;
    }

    /**
     * Gets the service source directory.
     * 
     * @param	context		the doc root context
     * @return	the service source directory
     */
    private	String	getServiceSrcDirProp(Map context) {
    	
    	Object obj = context.get(PROP_SERVICE_SRC_DIR);
    	
    	if (obj == null)
    		throw new IllegalArgumentException("must specify the service source directory");

    	return	(String)obj;
    }

    /**
     * Gets the service register listener property.
     * 
     * @param	context		the doc root context
     * @return	a register listener
     */
    private	ServiceRegisterListener	getServiceRegisterListenerProp(Map context) {
    	
    	Object obj = context.get(PROP_SERVICE_REGISTER_LISTENER);
    	
    	if (obj == null)
    		return	new DefaultServiceRegisterListener();
    	
    	if ((obj instanceof ServiceRegisterListener) == false)
    		throw new IllegalArgumentException("service register listener must be an instance of ServiceRegisterListener");

    	return	(ServiceRegisterListener)obj;
    }

    /**
     * Registers the service.
     * 
     */
    private	Service	addService(DocumentService docService) {
    	Service s = new Service(this, docService);

    	ZmDoclet.registerListener(new ServiceDocletListener(s));

    	String className = s.getDocumentServiceClassName();
    	String	srcPath = buildSourcePath(className);
    	
    	// read file source
    	String[] args = new String[] {
    			"-doclet",
    			ZmDoclet.class.getName(),
    			srcPath
    	};

		com.sun.tools.javadoc.Main.execute(args);

    	services.add(s);
    	return	s;
    }

    /**
     * Builds the source file path from the class name.
     * 
     * @param	className		the class name
     * @return	the source file path
     */
    public	String	buildSourcePath(String className) {
    	StringBuffer buf = new StringBuffer();
    	
    	String classFilePath = className.replaceAll("\\.", "/");
    	
    	buf.append(this.serviceSrcDir);
    	buf.append("/"); // it's OK to use "/" since that's what javadoc is expecting in src files list
       	buf.append(classFilePath);
       	buf.append(".java");

       	return	buf.toString();
    }
    
    /**
     * Gets the services.
     * 
     * @return	a list of {@link Service} objects
     */
    public		List<Service>	getServices() {
    	return	Collections.unmodifiableList(this.services);
    }

    /**
     * Initializes the doc root.
     * 
     * @param	listener		the service register listener
     */
    private	void	initialize(ServiceRegisterListener listener) {
    	Iterator it = this.services.iterator();
    	while (it.hasNext()) {
    		Service s = (Service)it.next();
        	ServiceDispatcher serviceDispatcher = new ServiceDispatcher(this, s, listener);
    		s.getDocumentService().registerHandlers(serviceDispatcher);
    	}
    }
    
    /**
     * Gets a list of all commands in all services.
     * 
     * @return	a list of {@link Command} objects
     */
    public	List<Command>	getAllCommands() {
    	List<Command>	allCommands = new LinkedList<Command>();
    	
    	Iterator sit = this.getServices().iterator();
    	while (sit.hasNext()) {
    		Service	s = (Service)sit.next();
        	Iterator cit = s.getCommands().iterator();
        	while (cit.hasNext()) {
        		Command	c = (Command)cit.next();
        		allCommands.add(c);
        	}
    	}

    	Collections.sort(allCommands, new Command.CommandComparator());
    	
    	return	allCommands;
    }
    
    /**
     * Dumps the contents to <code>System.out.println</code>
     * 
     */
    public	void	dump() {

		System.out.println("Dump doc root...");
		System.out.println(this);

		System.out.println("Dump services...");
    	Iterator it = this.services.iterator();
    	while (it.hasNext()) {
    		Service s = (Service)it.next();
    		s.dump(true);
    	}
    }
    
    /**
     * Returns a string representation of this object.
     * 
     * @return	a string representation of this object
     */
    public	String	toString() {
    	StringBuffer buf = new StringBuffer();

		buf.append("[docroot");
		buf.append(";hashCode=");
		buf.append(hashCode());
		buf.append(";serviceCount=");
		buf.append(this.services.size());
		buf.append("]");

		return	buf.toString();
    }
    
    
} // end Root class