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

import java.util.*;

public class Command implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private	static	final	String				REGEX_DESCRIPTION_SHORT_DELIM = "[.]+";

	private	Element		request = Element.createElement();
	private	Element		response = Element.createElement();
	
	private	List<Element>	allElements = new LinkedList<Element>();
	
	private	Service		service = null;
	private	String		className = null;
	private	String		name = null;
	private	String		namespace = null;

	private	String		requestName = null;
	private	String		responseName = null;
	private	String		description = null;

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
	 * Gets the response.
	 * 
	 * @return	the response
	 */
	public	Element	getResponse() {
		return	this.response;
	}

	/**
	 * Gets the request.
	 * 
	 * @return	the request
	 */
	public	Element	getRequest() {
		return	this.request;
	}

	/**
	 * Gets the description.
	 * 
	 * @return	the description
	 */
	public	String	getDescription() {
		if (this.description == null)
			return	""; // worst case
		
		return	this.description;
	}

	/**
	 * Gets the short description. This is the description up to the first
	 * period "." in the description.
	 * 
	 * @return	the short description
	 */
	public	String	getShortDescription() {
		String desc = getDescription();

		String[] tokens = desc.split(REGEX_DESCRIPTION_SHORT_DELIM);

		if (tokens != null && tokens.length > 0 && tokens[0].length() > 0) {
			return	tokens[0]+"."; // since we split at the period, make it a sentence again
		}
		
		return	""; // worst case
	}

	/**
	 * Sets the description.
	 * 
	 * @param	description		the description
	 */
	public	void	setDescription(String	description) {
		this.description = description;
	}

	/**
	 * Sets the root request and response elements.
	 * 
	 * @param	request		the request element
	 * @param	response	the response element
	 */
	public	void		setRootElements(Element request, Element response) {
		this.request = request;
		this.response = response;
		
		// load the all elements list
		this.allElements.add(request);
		this.allElements.add(response);
		loadAllElements(request);
		loadAllElements(response);
	}
	
	/**
	 * Loads all sub-elements of the root element into the all elements list.
	 * 
	 * @param	root	the root element
	 */
	private	void	loadAllElements(Element root) {
		Iterator it = root.getElements().iterator();
		while(it.hasNext()) {
			Element e = (Element)it.next();
			this.allElements.add(e);
			loadAllElements(e);
		}
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
	 * Gets the name by element type.
	 * 
	 * @return	the name by element type
	 */
	public	String		getNameByElementType(int type) {
		switch (type) {
			case Element.TYPE_REQUEST:
				return	getRequestName();
			case Element.TYPE_RESPONSE:
				return	getResponseName();
		}
		
		return	this.name;
	}

	/**
	 * Sets the request name.
	 * 
	 * @param	requestName		the request name
	 */
	public	void		setRequestName(String requestName) {
		this.requestName = requestName;
	}

	/**
	 * Sets the response name.
	 * 
	 * @param	responseName		the response name
	 */
	public	void		setResponseName(String responseName) {
		this.responseName = responseName;
	}

	/**
	 * Gets the request name.
	 * 
	 * @return	the request name
	 */
	public	String		getRequestName() {
		if (this.requestName == null)
			return	this.name+"Request"; // worst case, just fake it

		return	this.requestName;
	}

	/**
	 * Gets the response name.
	 * 
	 * @return	the response name
	 */
	public	String		getResponseName() {
		if (this.responseName == null)
			return	this.name+"Response";  // worst case, just fake it

		return	this.responseName;
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
	 * Gets a list with all elements.
	 * 
	 * @return	a list with all elements
	 */
	public	List<Element>	getAllElements() {
		return	this.allElements;
	}

	/**
	 * Gets a list with all sub-elements. This is the list of all elements below 
	 * 
	 * @return	a list with all sub-elements
	 */
	public	List<Element>	getAllSubElements() {
		List<Element> els = new LinkedList<Element>(this.allElements);
		
		els.remove(this.request);
		els.remove(this.response);
		
		return els;
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
     * Returns a string representation of this object.
     * 
     * @return	a string representation of this object
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
		buf.append(";requestName=");
		buf.append(this.getRequestName());
		buf.append(";responseName=");
		buf.append(this.getResponseName());
		buf.append(";request=");
		buf.append(this.getRequest());
		buf.append(";response=");
		buf.append(this.getResponse());
		buf.append(";allElements=");
		buf.append(this.getAllElements().size());
		buf.append(";allSubElements=");
		buf.append(this.getAllSubElements().size());
		buf.append("]");

		return	buf.toString();
    }

    /**
     * 
     * @author sposetti
     *
     */
    public	static	class		CommandComparator implements java.util.Comparator {
    	
    	/**
    	 * Compares to objects.
    	 * 
    	 * @param	o1
    	 * @param	o2
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
