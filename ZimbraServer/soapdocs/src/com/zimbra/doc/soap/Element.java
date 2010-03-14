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

import com.zimbra.doc.soap.util.StringUtil;
import java.util.*;

/**
 * 
 * @author sposetti
 *
 */
public class Element extends AbstractElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private	static	final	String				REGEX_ELEMENT_TAG_DELIM = "[ \t]+";
	private	static	final	String				REGEX_SUBELEMENT_NAME_DELIM = "[,]+";
	
	private	List<Element>	subElements = new LinkedList<Element>();
	private	List<Object[]>			subElementsMap = new LinkedList<Object[]>();
	
	private	List<Attribute>	attributes = new LinkedList<Attribute>();

	private	boolean		loaded = false;

	/**
	 * Constructor.
	 * 
	 */
	private	Element() {
		this.loaded = false;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param	name		the name
	 * @param	description	the description
	 * @param	type		the element type (see <code>TYPE_</code> constants)
	 */
	private	Element(String name, String description, int type, List<Object[]> subElementsMap) {
		this.loaded = true;
		this.name = name;
		this.description = description;
		this.type = type;
		this.subElementsMap = subElementsMap;
	}
	
	/**
	 * Creates an empty element.
	 * 
	 * @return	the element
	 */
	public	static	Element	createElement() {
		return	new Element();
	}

	/**
	 * Creates a copy element.
	 * 
	 * @return	the element
	 */
	public	Element	createCopy() {
		Element newEl = new Element(this.name, this.description, this.type, this.subElementsMap);
		
		newEl.addAttributes(this.getAttributes());
		
		return	newEl;
	}

	/**
	 * Creates an element by parsing the tag text.
	 * 
	 * @param	tagText		the tag text
	 * @param	req		if <code>true</code>, the element is related to the request
	 * @return	the element
	 */
	public	static	Element	createElement(String tagText, int	type) {
		
		String[] tokens = tagText.split(REGEX_ELEMENT_TAG_DELIM);

		String	name = tokens[0];
		String	content = tokens[1];
		String description = StringUtil.createString(tokens, 2, " ");

		List<Object[]>	subElementsMap = parseSubElementsMap(content);

		Element newEl = new Element(name, description, type, subElementsMap);

		if (content != null && content.equals(Attribute.CDATA)) {
			Attribute attr = Attribute.createCDATAAttribute(name, description, type);
			newEl.addAttribute(attr);
		}
		
		return	newEl;
	}
	
	/**
	 * Checks if this element is loaded.
	 * 
	 * @return	<code>true</code> if the element is loaded
	 */
	public	boolean	isLoaded() {
		return	this.loaded;
	}

	/**
	 * Gets the sub-element map.
	 * 
	 * @return	a map of sub-elements
	 */
	public	List<Object[]>		getSubElementsMap() {
		return	this.subElementsMap;
	}

	/**
	 * Parses the sub-element map from the tag content.
	 * 
	 * @param	content		the tag content string
	 * @return	a map of sub-elements
	 */
	private	static	List<Object[]>		parseSubElementsMap(String content) {
		List<Object[]>		subElementsMap = new LinkedList<Object[]>();
		
		if (content.startsWith("(") && content.endsWith(")")) {
			content = content.substring(1, content.length()-1);
			String[]	names = content.split(REGEX_SUBELEMENT_NAME_DELIM);
			for (int i=0; i < names.length; i++) {
				Object[] obj = parseElementFromContent(names[i]);
				subElementsMap.add(obj);
			}
		}
			
		return	subElementsMap;
	}
	
	/**
	 * Parses the sub-element names from the tag content.
	 * 
	 * @param	content		the tag content string
	 * @return	an array of sub-element names or an empty array for none
	 */
	private	static	Object[]		parseElementFromContent(String elementContent) {
		Object[] obj = new Object[2];

		int occ = getOccurenceFromString(elementContent);
		if (occ != OCCURRENCE_REQUIRED)
			elementContent = elementContent.substring(0, elementContent.length()-1);

		obj[0] = elementContent;
		obj[1] = new Integer(occ);

		return	obj;
	}
	
	/**
	 * Checks if the element has elements.
	 * 
	 * @return	<code>true</code> if the element has elements
	 */
	public	boolean		hasElements() {
		return	(subElements.size() > 0);
	}

	/**
	 * Adds the element.
	 * 
	 * @param	e		the element to add
	 */
	public	boolean		addElement(Element e) {
		this.subElements.add(e);
		if (e.getName().equalsIgnoreCase(this.getName()))
			return	false;
		
		return	true;
	}

	/**
	 * Adds the attribute.
	 * 
	 * @param	attr		the attribute to add
	 */
	public	void		addAttribute(Attribute attr) {
		this.attributes.add(attr);
	}

	/**
	 * Adds the attributes.
	 * 
	 * @param	attrs		a list of attributes to add
	 */
	public	void		addAttributes(List<Attribute> attrs) {
		this.attributes.addAll(attrs);
	}

	/**
	 * Checks if the element has attributes.
	 * 
	 * @return	<code>true</code> if the element has attributes
	 */
	public	boolean		hasAttributes() {
		return	(attributes.size() > 0);
	}

	/**
	 * Gets the attributes.
	 * 
	 * @return	a list of attributes
	 */
	public	List<Attribute>	getAttributes() {
		return	this.attributes;
	}

	/**
	 * Gets the sub-elements.
	 * 
	 * @return	a list of elements
	 */
	public	List<Element>	getElements() {
		return	this.subElements;
	}

	/**
	 * Checks if this element is required.
	 * 
	 * @return	<code>true</code> if this element is required
	 */
	public	boolean		isRequired() {
		return	(this.occurrence == OCCURRENCE_REQUIRED || this.occurrence == OCCURRENCE_REQUIRED_MORE);
	}

	/**
	 * Checks if this element equals the specified element.
	 * 
	 * @return	<code>true</code> if the elements are equal
	 */
	public	boolean		equals(Object obj) {
		if (obj instanceof Element) {
			Element el = (Element)obj;
			String n1 = this.getName();
			String n2 = el.getName();
			
			return	n1.equals(n2);
		}
		
		return	false;
	}

    /**
     * Returns a string representation of this object.
     * 
     * @return	a string representation of this object
     */
    public	String	toString() {
    	StringBuffer buf = new StringBuffer();

		buf.append("[element");
		buf.append(";hashCode=");
		buf.append(hashCode());
		buf.append(";name=");
		buf.append(this.getName());
		buf.append(";type=");
		buf.append(this.getType());
		buf.append(";occurrence=");
		buf.append(this.getOccurrence());
		buf.append(";subElements-count=");
		buf.append(this.subElements.size());
		buf.append(";attributes-count=");
		buf.append(this.attributes.size());
		buf.append("]");

		return	buf.toString();
    }

} // end Element class
