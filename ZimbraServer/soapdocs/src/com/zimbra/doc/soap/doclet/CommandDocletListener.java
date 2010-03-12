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

		List<Attribute> requestAttributes = getAttributesSubList(AbstractElement.TYPE_REQUEST);
		List<Attribute> responseAttributes = getAttributesSubList(AbstractElement.TYPE_RESPONSE);
		
		loadAttributes(rootRequestElement, requestAttributes);
		loadAttributes(rootResponseElement, responseAttributes);
		
		this.command.setRequest(rootRequestElement);
		this.command.setResponse(rootResponseElement);
	}
	
	/**
	 * Loads the elements for the given parent element.
	 * 
	 */
	private	static	void	loadAttributes(Element parent, List<Attribute> attributes) {
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Attribute attr = (Attribute)it.next();

			if (parent.getName().equalsIgnoreCase(attr.getElementName())) {
				parent.addAttribute(attr);
				continue;
			}

			Iterator eit = parent.getElements().iterator();
			while (eit.hasNext()) {
				Element e = (Element)eit.next();
				if (e.getName().equalsIgnoreCase(attr.getElementName())) {
					e.addAttribute(attr);
				} else {
					loadAttributes(e, attributes);
				}
			}
			
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
			
			if (e.getName().equals(this.command.getNameByElementType(type))) {
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
	private	static	void	loadElements(Element parent, List<Element> elements) {
		Iterator se = parent.getSubElementsMap().entrySet().iterator();
		while (se.hasNext()) {
			Map.Entry entry = (Map.Entry)se.next();
			
			String elementName = (String)entry.getKey();
			Iterator eit = elements.iterator();
			while (eit.hasNext()) {
				Element e = (Element)eit.next();
				if (e.getName().equalsIgnoreCase(elementName)) {
					Integer v = (Integer)entry.getValue();
					e.setOccurrence(v.intValue());
					parent.addElement(e);
					loadElements(e, elements);
				}
			}
			
		}

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
	 * @return	the element type (see <code>Element.TYPE_</code> constants)
	 */
	private	static	int		getElementType(String tagName) {
		if (tagName.equals(TAG_COMMAND_REQUEST_ELEMENT) ||
				tagName.equals(TAG_COMMAND_REQUEST_ATTRIBUTE) )
			return	AbstractElement.TYPE_REQUEST;
		
		return	AbstractElement.TYPE_RESPONSE;
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