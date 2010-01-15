/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @constructor
 * @class
 * DwtCssStyle is a static class that defines a number of contants and helper methods that
 * support the working with CSS
 * 
 * @author Ross Dargahi
 */
DwtCssStyle = function() {
}

// Common class name constants used in Dwt

/** mouseOver: transitory state while mouse is over the item
 * @type string*/
DwtCssStyle.HOVER = "hover";


/** mouseDown: transitory state while left mouse button is being pressed on the item
 * @type string*/
DwtCssStyle.ACTIVE = "active";


/** item is "on", 
	(eg: selected tab, select item(s) in list, or button that stays depressed)
 * @type string*/
DwtCssStyle.SELECTED = "selected";


/** "disabled": item is not actionable 
	(eg: because not appropriate or some other condition needs to be true)
 * @type string*/
DwtCssStyle.DISABLED = "disabled";


/** item has keyboard focus
 * @type string */
DwtCssStyle.FOCUSED = "focused";


/** UI component is target of some external action, eg:
		a) item is the target of right-click (eg: show menu)
		b) item is the thing being dragged
 * @type string*/
DwtCssStyle.ACTIONED = "actioned";

 

/** matched item in a list 
	(eg: in conv list view, items that match the search. 
		NOT used if *all* items match the search.)
 * @type string*/
DwtCssStyle.MATCHED	 = "matched";



/** UI component is the current, valid drop target
 * @type string */
DwtCssStyle.DRAG_OVER = "dragOver";


/** Item being dragged is over a valid drop target
 * @type string*/
DwtCssStyle.DROPPABLE = "droppable";


/** Item being dragged is NOT over a valid drop target
 * @type string*/
DwtCssStyle.NOT_DROPPABLE = "notDroppable";


/** representation of an item *as it is being dragged* (eg: thing moving around the screen)
 * @type string*/
DwtCssStyle.DRAG_PROXY = "dragProxy";




/** class applies only to linux browsers
 * @type string */
DwtCssStyle.LINUX = "linux";


DwtCssStyle.getProperty = 
function(htmlElement, cssPropName) {
	var result;
	if (htmlElement.ownerDocument == null) {
		// IE5.5 does not support ownerDocument
		for(var parent = htmlElement.parentNode; parent.parentNode != null; parent = parent.parentNode);
		var doc = parent;
	} else {
		var doc = htmlElement.ownerDocument;
	}
	
	if (doc.defaultView && !AjxEnv.isSafari) {
		var cssDecl = doc.defaultView.getComputedStyle(htmlElement, "");
		result = cssDecl.getPropertyValue(cssPropName);
	} else {
		  // Convert CSS -> DOM name for IE etc
			var tokens = cssPropName.split("-");
			var propName = "";
			var i;
			var len = tokens.length;
			for (i = 0; i < len; i++) {
				if (i != 0) 
					propName += tokens[i].substring(0, 1).toUpperCase();
				else 
					propName += tokens[i].substring(0, 1);
				propName += tokens[i].substring(1);
			}
			if (htmlElement.currentStyle)
				result = htmlElement.currentStyle[propName];
			else if (htmlElement.style)
				result = htmlElement.style[propName];
	}
	return result;
};

DwtCssStyle.getComputedStyleObject = 
function(htmlElement) {
	if (htmlElement.ownerDocument == null) {
		// IE5.5 does not suppoert ownerDocument
		for(var parent = htmlElement.parentNode; parent.parentNode != null; parent = parent.parentNode);
		var doc = parent;
	} else {
		var doc = htmlElement.ownerDocument;
	}
	
	if (doc.defaultView) {
		var style = doc.defaultView.getComputedStyle(htmlElement, null);
		if (!style && htmlElement.style) {
// TODO: destructive ?
			htmlElement.style.display = "";
			style = doc.defaultView.getComputedStyle(htmlElement, null);
		}
		return style || {};
	} else if (htmlElement.currentStyle)
		return htmlElement.currentStyle;
	else if (htmlElement.style)
		return htmlElement.style;
};

DwtCssStyle.removeProperty = function(el, prop) {
	if (prop instanceof Array) {
		for (var i = prop.length; --i >= 0;)
			DwtCssStyle.removeProperty(el, prop[i]);
	} else {
		if (AjxEnv.isIE) {
			el.style.removeAttribute(prop, true);
		} else {
			prop = prop.replace(/([A-Z])/g, "-$1");
			el.style.removeProperty(prop);
		}
	}
};
