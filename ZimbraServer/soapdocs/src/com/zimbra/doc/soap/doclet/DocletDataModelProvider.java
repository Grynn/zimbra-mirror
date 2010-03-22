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

import com.zimbra.soap.DocumentService;
import com.zimbra.doc.soap.*;
import com.zimbra.doc.soap.util.StringUtil;
import java.util.*;

/**
 * This class represents the root data model for the SOAP API.
 * 
 * @author sposetti
 *
 */
public	class	DocletDataModelProvider extends	DataModelProvider {

	public	static	final	String			PROP_SERVICE_DISPATCHER_LISTENER = "service-dispatcher-listener";
	public	static	final	String			PROP_SERVICE_SRC_DIR = "service-src-dir";
	public	static	final	String			PROP_SERVICE_LIST = "service-list";

	private	ServiceDispatcherListener	listener = new DefaultServiceDispatcherListener();
	private	String			serviceSrcDir = null;
	private	List<DocumentService> serviceDocuments = new LinkedList<DocumentService>();

	/**
	 * Constructor.
	 * 
	 * @param	context		the provider context props (see <code>PROP_</code> constants)
	 */
	public	DocletDataModelProvider(Map<String,Object> props) {
		
    	this.serviceSrcDir = getServiceSrcDirProp(props);
    	this.listener = getServiceDispatcherListenerProp(props);
    	this.serviceDocuments = getServiceListProp(props);
	}
	
    /**
     * Gets the service list property.
     * 
     * @param	context		the doc root context
     * @return	a list of {@link DocumentService} objects
     */
    private	static	List<DocumentService>	getServiceListProp(Map<String,Object> context) {
    	
    	Object obj = context.get(PROP_SERVICE_LIST);
    	
    	if (obj == null)
    		throw new IllegalArgumentException("must specify the service list");

    	if ((obj instanceof List) == false)
    		throw new IllegalArgumentException("service list must be a list of (DocumentService) objects");

    	return	(List)obj;
    }

    /**
     * Gets the service source directory.
     * 
     * @param	context		the doc root context
     * @return	the service source directory
     */
    private	static	String	getServiceSrcDirProp(Map<String,Object> props) {
    	
    	Object obj = props.get(PROP_SERVICE_SRC_DIR);
    	
    	if (obj == null || (obj instanceof String) == false)
    		throw new IllegalArgumentException("must specify the (String) service source directory");

    	return	(String)obj;
    }

    /**
     * Gets the service register listener property.
     * 
     * @param	context		the doc root context
     * @return	a register listener
     */
    private	static	ServiceDispatcherListener	getServiceDispatcherListenerProp(Map<String,Object> props) {
    	
    	Object obj = props.get(PROP_SERVICE_DISPATCHER_LISTENER);
    	
    	if (obj == null)
    		return	new DefaultServiceDispatcherListener();
    	
    	if ((obj instanceof ServiceDispatcherListener) == false)
    		throw new IllegalArgumentException("service register listener must be an instance of ServiceDispatcherListener");

    	return	(ServiceDispatcherListener)obj;
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
	 * Adds a command.
	 * 
	 * @param service			the service			
	 * @param className			the command class name
	 * @param name				the command name
	 * @param	namespace		the namespace
	 * @return	the newly created command 
	 */
	protected	Command	addCommand(Service service, String className, String name, String namespace) {
		return	this.createCommand(service, className, name, namespace);
	}

	/**
	 * 
	 */
	protected	Root	loadDataModel(Root root) {

    	Iterator it = this.serviceDocuments.iterator();
    	while(it.hasNext()) {
    		DocumentService serviceDoc = (DocumentService)it.next();

    		String	className = serviceDoc.getClass().getName();
    		String	name = StringUtil.getClassName(className);
	    	String	srcPath = buildSourcePath(className);

    	    Service s = createService(root, className, name);

            ServiceDispatcher serviceDispatcher = new ServiceDispatcher(root, s, this, listener);
            serviceDoc.registerHandlers(serviceDispatcher);

	    	ZmDoclet.registerListener(new ServiceDocletListener(s));

	    	// read file source
	    	String[] args = new String[] {
	    			"-doclet",
	    			ZmDoclet.class.getName(),
	    			srcPath
	    	};

			com.sun.tools.javadoc.Main.execute(args);

            root.addService(s);
    	}

    	return	root;
	}
	
}