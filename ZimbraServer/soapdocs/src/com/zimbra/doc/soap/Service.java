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

/**
 * 
 * @author sposetti
 *
 */
public class Service implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private	List<Command>	commands = new LinkedList<Command>();
	
	private	Root		root = null;
	private	String		className = null;
	private	String		name = null;
	public	String		description = null;

	/**
	 * Constructor.
	 * 
	 * @param	root		the root data model
	 * @param	className	the service class name
	 * @param	name		the service name
	 */
	public	Service(Root root, String className, String name) {
		this.root = root;
		this.className = className;
		this.name = name;
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
	 * Gets the class name.
	 * 
	 * @return	the class name
	 */
	public	String		getClassName() {
		return	this.className;
	}

	/**
	 * Gets the item description.
	 * 
	 * @return	the description
	 */
	public	String	getDescription() {
		if (this.description == null)
			return	"";
		
		return	this.description;
	}

	/**
	 * Sets the item description.
	 * 
	 * @param	desc		the description
	 */
	public	void	setDescription(String desc) {
		this.description = desc;
	}

	/**
	 * Gets the commands.
	 * 
	 * @return	a list of {@link Command} objects
	 */
	public	List<Command>	getCommands() {
    	List<Command>	allCommands = new LinkedList<Command>();

		Iterator cit = this.commands.iterator();
    	while (cit.hasNext()) {
    		Command	c = (Command)cit.next();
    		allCommands.add(c);
    	}

    	Collections.sort(allCommands, new Command.CommandComparator());

    	return	allCommands;
	}
	
	/**
	 * Adds the command.
	 * 
	 * @param	cmd		the command to add
	 */
	public	void	addCommand(Command cmd) {
		this.commands.add(cmd);
	}
	
    /**
     * Dumps the contents to <code>System.out.println</code>
     * 
     */
    public	void	dump() {
    	dump(false);
    }

    /**
     * Dumps the contents to <code>System.out.println</code>
     * 
     * @param	commands		if <code>true</code>, dump commands
     */
    public	void	dump(boolean commands) {

		System.out.println("Dump service...");
		System.out.println(this);

		if (commands) {
			System.out.println("Dump commands...");
	    	Iterator it = this.commands.iterator();
	    	while (it.hasNext()) {
	    		Command c = (Command)it.next();

	    		c.dump();
	    	}
		}
    }
    
    /**
     * Returns a string representation of this object.
     * 
     * @return	a string representation of this object
     */
    public	String	toString() {
    	StringBuffer buf = new StringBuffer();

		buf.append("[service");
		buf.append(";hashCode=");
		buf.append(hashCode());
		buf.append(";name=");
		buf.append(this.getName());
		buf.append(";description=");
		buf.append(this.getDescription());
		buf.append(";commandCount=");
		buf.append(this.commands.size());
		buf.append("]");

		return	buf.toString();
    }

}
