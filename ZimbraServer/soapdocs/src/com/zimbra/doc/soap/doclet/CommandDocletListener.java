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
import com.zimbra.doc.soap.Command;
import com.zimbra.doc.soap.AbstractElement;
import com.zimbra.doc.soap.Element;
import com.zimbra.doc.soap.Attribute;
import java.util.*;
import com.sun.javadoc.*;

/**
 * 
 * @author sposetti
 *
 */
public 	class CommandDocletListener	extends	DocletListener {
	
	public	static	final	String			TAG_COMMAND_DESCRIPTION = "@zm-description";
	public	static	final	String			TAG_COMMAND_REQUEST = "@zm-request";
	public	static	final	String			TAG_COMMAND_RESPONSE = "@zm-response";
	public	static	final	String			TAG_COMMAND_REQUEST_ELEMENT = "@zm-request-element";
	public	static	final	String			TAG_COMMAND_REQUEST_ATTRIBUTE = "@zm-request-attribute";
	public	static	final	String			TAG_COMMAND_RESPONSE_ELEMENT = "@zm-response-element";
	public	static	final	String			TAG_COMMAND_RESPONSE_ATTRIBUTE = "@zm-response-attribute";

	private	Service		service = null;
	private	Command		command = null;
	
	private	List<Element>	elementList = new LinkedList<Element>();
	private	List<Attribute>	attributeList = new LinkedList<Attribute>();
	
	/**
	 * Constructor.
	 * 
	 * @param		service		the service
	 */
	public	CommandDocletListener(Service service, Command command) {
		super(command.getClassName());
		this.service = service;
		this.command = command;
	}

	/**
	 * Called when a registered class is found.
	 * 
	 * @param	tags		the tags
	 */
	public	void		tagsEvent(Tag[] tags) {
		for (int i=0; i < tags.length; i++) {
			String	tagName = tags[i].name();

			int type = getElementType(tagName);

			if (type == -1) {
				String text = tags[i].text();
				if (tagName.equals(TAG_COMMAND_DESCRIPTION))
					command.setDescription(text);
				else if (tagName.equals(TAG_COMMAND_REQUEST))
					command.setRequestName(text);
				else if (tagName.equals(TAG_COMMAND_RESPONSE))
					command.setResponseName(text);
				continue;
			}
			
			if (isElementTag(tagName)) {
				processElementTag(tags[i], type);
				continue;
			}

			if (isAttributeTag(tagName)) {
				processAttributeTag(tags[i], type);
				continue;
			}
		}

		Element rootRequestElement = buildElementTree(AbstractElement.TYPE_REQUEST);
		Element rootResponseElement = buildElementTree(AbstractElement.TYPE_RESPONSE);

		// load request attributes
		List<Attribute> requestAttributes = getAttributesSubList(AbstractElement.TYPE_REQUEST);		
		loadElementAttributes(rootRequestElement, requestAttributes);
		loadAttributes(rootRequestElement, requestAttributes);

		// load response attributes
		List<Attribute> responseAttributes = getAttributesSubList(AbstractElement.TYPE_RESPONSE);
		loadElementAttributes(rootResponseElement, responseAttributes);
		loadAttributes(rootResponseElement, responseAttributes);
		
		this.command.setRootElements(rootRequestElement, rootResponseElement);
	}
	
	/**
	 * Loads the elements for the given parent element.
	 * 
	 */
	private	static	void	loadAttributes(Element parent, List<Attribute> allAttributes) {
		if (parent == null || parent.getElements() == null)
			return;
		
		Iterator eit = parent.getElements().iterator();
		while (eit.hasNext()) {
			Element e = (Element)eit.next();

			loadElementAttributes(e, allAttributes);

			loadAttributes(e, allAttributes); // no load the attrs for this element
		}
	}
	
	/**
	 * Loads the attributes for a given element.
	 * 
	 * @param	el		the element
	 * @param	attributes	the attributes list to read
	 */
	private	static	void	loadElementAttributes(Element el, List<Attribute> attributes) {
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Attribute attr = (Attribute)it.next();

			if (el.getName().equalsIgnoreCase(attr.getElementName()))
				el.addAttribute(attr);
		}
	}
	
	/**
	 * Builds the element tree.
	 * 
	 * @param	type		the element type (see <code>Element.TYPE_</code> constants)
	 * @return	the root element
	 */
	private		Element		buildElementTree(int type) {
		List<Element> subList = getElementsSubList(type);

		// find root element (element with name == command)
		Element root = null;
		Iterator it = subList.iterator();
		while (it.hasNext()) {
			Element e = (Element)it.next();
			
			String typeName = this.command.getNameByElementType(type);
			
			if (e.getName().equals(typeName)) {
				root = e;
				subList.remove(e);
				break;
			}
		}

		// start at root, build tree
		if (root != null)
			loadElements(root, subList);
		
		return	root;
	}

	/**
	 * Loads the elements for the given parent element.
	 * 
	 */
	private	static	void	loadElements(Element parent, List<Element> allElements) {
		loadElements(parent, allElements, new LinkedList<String[]>());
	}
	
	/**
	 * Loads the elements for the given parent element.
	 * 
	 */
	private	static	void	loadElements(Element parent, List<Element> allElements, List<String[]> loadedList) {
		Iterator se = parent.getSubElementsMap().iterator();
		while (se.hasNext()) {
			Object[] obj = (Object[])se.next();
			
			String elementName = (String)obj[0];
			Iterator eit = allElements.iterator();
			while (eit.hasNext()) {
				Element e = (Element)eit.next();
				if (e.getName().equalsIgnoreCase(elementName)) {
 					Integer v = (Integer)obj[1];
 					Element tmpElement = e.createCopy();
					tmpElement.setOccurrence(v.intValue());
					if (parent.addElement(tmpElement)) {
						if (isElementLoaded(parent.getName(), elementName, loadedList)) {
							continue;
						} else {
							addElementLoaded(parent.getName(), elementName, loadedList);
							loadElements(tmpElement, allElements, loadedList);
						}
					}
				}
			
			}
			
		}

	}
	
	/**
	 * Checks if the parent/child combo is loaded.
	 * 
	 * @return	<code>true</code> if loaded
	 */
	private	static	boolean	isElementLoaded(String parentName, String childName, List<String[]> loadedList) {
		
		Iterator it = loadedList.iterator();
		while (it.hasNext()) {
			String[] obj = (String[])it.next();
			if (obj[0].equals(parentName) && obj[1].equals(childName))
				return	true;
		}
		return	false;
	}
	
	/**
	 * Adds the parent/child combo to loaded list.
	 * 
	 */
	private	static	void	addElementLoaded(String parentName, String childName, List<String[]> loadedList) {
		String[] obj = new String[2];
		
		obj[0] = parentName;
		obj[1] = childName;
		
		loadedList.add(obj);
	}
	
	/**
	 * Gets a list of elements by type.
	 * 
	 * @param	type		the type
	 * @return	a list of {@link Element} objects
	 */
	private		List<Element>	getElementsSubList(int type) {
		List<Element> els = new LinkedList<Element>();
		
		Iterator it = this.elementList.iterator();
		while (it.hasNext()) {
			Element el = (Element)it.next();
			if (el.getType() == type)
				els.add(el);
		}
		
		return	els;
	}

	/**
	 * Gets a list of attributes by type.
	 * 
	 * @param	type		the type
	 * @return	a list of {@link Attribute} objects
	 */
	private		List<Attribute>	getAttributesSubList(int type) {
		List<Attribute> atrs = new LinkedList<Attribute>();
		
		Iterator it = this.attributeList.iterator();
		while (it.hasNext()) {
			Attribute at = (Attribute)it.next();
			if (at.getType() == type)
				atrs.add(at);
		}
		
		return	atrs;
	}

	/**
	 * Processes the element tag.
	 * 
	 * @param	tag		the tag to process
	 * @param	type	the element type
	 */
	private	void		processElementTag(Tag tag, int type) {
		Element	el = Element.createElement(tag.text(), type);
		elementList.add(el);
	}

	/**
	 * Processes the attribute tag.
	 * 
	 * @param	tag		the tag to process
	 * @param	type	the element type
	 */
	private	void		processAttributeTag(Tag tag, int type) {
		Attribute	attr = Attribute.createAttribute(tag.text(), type);
		attributeList.add(attr);
	}

	/**
	 * Gets the element type.
	 * 
	 * @return	the element type (see <code>Element.TYPE_</code> constants) or -1 for neither
	 */
	private	static	int		getElementType(String tagName) {
		if (tagName.equals(TAG_COMMAND_REQUEST_ELEMENT) ||
				tagName.equals(TAG_COMMAND_REQUEST_ATTRIBUTE) )
			return	AbstractElement.TYPE_REQUEST;

		if (tagName.equals(TAG_COMMAND_RESPONSE_ELEMENT) ||
				tagName.equals(TAG_COMMAND_RESPONSE_ATTRIBUTE) )
			return	AbstractElement.TYPE_RESPONSE;

		return	-1;
	}

	/**
	 * Checks if the tag is an attribute tag.
	 * 
	 * @return	<code>true</code> if tag is an attribute tag
	 */
	private	static	boolean		isAttributeTag(String tagName) {
		if (tagName.equals(TAG_COMMAND_REQUEST_ATTRIBUTE) ||
				tagName.equals(TAG_COMMAND_RESPONSE_ATTRIBUTE) )
			return	true;
		
		return	false;
	}

	/**
	 * Checks if the tag is an element tag.
	 * 
	 * @return	<code>true</code> if tag is an element tag
	 */
	private	static	boolean		isElementTag(String tagName) {
		if (tagName.equals(TAG_COMMAND_REQUEST_ELEMENT) ||
				tagName.equals(TAG_COMMAND_RESPONSE_ELEMENT) )
			return	true;
		
		return	false;
	}

} // end CommandDocletListener class