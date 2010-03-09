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

public class Command implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private	Element		request = null;
	private	Element		response = null;
	
	private	Service		service = null;
	private	String		className = null;
	private	String		name = null;
	private	String		namespace = null;
	
	/**
	 * Constructor.
	 * 
	 * @param service			the service			
	 * @param name				the command name
	 * @param	namespace		the namespace
	 */
	Command(Service service, String className, String namespace) {
		this.service = service;
		this.className = className;
		this.name = this.service.getClassName(className);
		this.namespace = namespace;
	}
	
	/**
	 * Gets the namespace.
	 * 
	 * @return	the namespace
	 */
	public	String		getNamespace() {
		return	this.namespace;
	}

	/**
	 * Gets the name.
	 * 
	 * @return	the name
	 */
	public	String		getName() {
		return	this.name;
	}

	/**
	 * Gets the service.
	 * 
	 * @return	the service
	 */
	public	Service	getService() {
		return	this.service;
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
     * Dumps the contents to <code>System.out.println</code>
     * 
     */
    public	void	dump() {

		System.out.println("Dump command...");
		System.out.println(this);
    }
    

    /**
     * 
     */
    public	String	toString() {
    	StringBuffer buf = new StringBuffer();

		buf.append("[command");
		buf.append(";hashCode=");
		buf.append(hashCode());
		buf.append(";name=");
		buf.append(this.getName());
		buf.append(";namespace=");
		buf.append(this.getNamespace());
		buf.append(";className=");
		buf.append(this.getClassName());
		buf.append("]");

		return	buf.toString();
    }

    public	static	class		CommandComparator implements java.util.Comparator {
    	
    	/**
    	 * 
    	 */
    	public	int		compare(Object o1, Object o2) {
    		if ((o1 instanceof Command) == false)
    			return	-1;
    		if ((o2 instanceof Command) == false)
    			return	-1;
    		
    		Command c1 = (Command)o1;
    		Command c2 = (Command)o2;
    		
    		String n1 = c1.getName();
    		String n2 = c2.getName();
    		
    		return	n1.compareTo(n2);
    	}
    }
    
}
