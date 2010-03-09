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
public class ZmDoclet {

	private	static		Map<String,DocletListener>		listeners = new HashMap<String,DocletListener>();
	
	/**
	 * Registers the listener.
	 * 
	 * @param	listener		the listener
	 */
	public	static	void	registerListener(DocletListener listener) {
		listeners.put(listener.getClassName(), listener);
	}
	
	/**
	 * Starts processing the classes at the root document
	 * 
	 * @param	root		the root document
	 */
	public static boolean start(RootDoc root) {
		processContents(root.classes());
		return true;
	}

	/**
	 * Processes the content.
	 * 
	 * @param	classes		the classes to process
	 */
	private static void processContents(ClassDoc[] classes) {
		for (int i=0; i < classes.length; i++) {
			DocletListener listener = checkListener(classes[i]);
			if (listener != null) {
				Tag[] tags = classes[i].tags();
				listener.tagsEvent(tags);				
			}
		}
	}
	
	/**
	 * Checks the registered listeners for a given class.
	 * 
	 * @param	doc		the class
	 * @return	the listener or <code>null</code> if not listener registered for that class
	 */
	private	static		DocletListener	checkListener(ClassDoc doc) {
		String	docClassName = doc.toString();
		return	(DocletListener)listeners.get(docClassName);
	}

} // end ZmDoclet class
