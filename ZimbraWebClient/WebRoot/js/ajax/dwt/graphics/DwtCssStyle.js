/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Default constructor.
 * @constructor
 * @class
 * This is a static class that defines a number of constants and helper methods that
 * support the working with CSS.
 * 
 * @author Ross Dargahi
 * 
 * @private
 */
DwtCssStyle = function() {
}

// Common class name constants used in Dwt

/**
 * "mouseOver": transitory state while mouse is over the item.
 */
DwtCssStyle.HOVER = "hover";

/**
 * "mouseDown": transitory state while left mouse button is being pressed on the item.
 */
DwtCssStyle.ACTIVE = "active";

/**
 * item is "on", (for example: selected tab, select item(s) in list, or button that stays depressed).
 */
DwtCssStyle.SELECTED = "selected";

/**
 * "disabled": item is not actionable (for example: because not appropriate or some other condition needs to be true).
 */
DwtCssStyle.DISABLED = "disabled";

/**
 * "focused": item has keyboard focus.
 */
DwtCssStyle.FOCUSED = "focused";

/**
 * UI component is target of some external action, for example:
 * <ul>
 * <li>item is the target of right-click (for example: show menu)</li>
 * <li>item is the thing being dragged</li>
 * </ul>
 */
DwtCssStyle.ACTIONED = "actioned";

/**
 * Matched item in a list (for example: in conv list view, items that match the search. NOT used if *all* items match the search).
 */
DwtCssStyle.MATCHED	 = "matched";

/**
 * UI component is the current, valid drop target.
 */
DwtCssStyle.DRAG_OVER = "dragOver";

/**
 * Item being dragged is over a valid drop target.
 */
DwtCssStyle.DROPPABLE = "droppable";

/**
 * Item being dragged is NOT over a valid drop target.
 */
DwtCssStyle.NOT_DROPPABLE = "notDroppable";

/**
 * Represents of an item *as it is being dragged* (for example: thing moving around the screen).
 */
DwtCssStyle.DRAG_PROXY = "dragProxy";

/**
 * Class applies only to linux browsers.
 */
DwtCssStyle.LINUX = "linux";


DwtCssStyle.getProperty = 
function(htmlElement, cssPropName) {
	var result;
	if (htmlElement.ownerDocument == null) {
		// IE5.5 does not support ownerDocument
		for (var parent = htmlElement.parentNode; parent.parentNode != null; parent = parent.parentNode) {}
		var doc = parent;
	} else {
		var doc = htmlElement.ownerDocument;
	}

	if (doc.defaultView && doc.defaultView.getComputedStyle) {
		var cssDecl = doc.defaultView.getComputedStyle(htmlElement, "");
		if (cssDecl && cssDecl.length > 0) { //on Chrome/Safari it returns cssDecl with length 0 for some elements for some reason. (a wild guess could be invisible items, as it happens with invite toolbar when it's invisible) So in that case fall back on the other ways.
			return cssDecl.getPropertyValue(cssPropName);
		}
	}
	
	// Convert CSS -> DOM name for IE etc
	var tokens = cssPropName.split("-");
	// Shift one word off the array and capitalize the rest
	var propName = tokens.shift() + AjxUtil.map(tokens, AjxStringUtil.capitalize).join("");

	if (htmlElement.currentStyle) {
		return htmlElement.currentStyle[propName];
	} else if (htmlElement.style) {
		return htmlElement.style[propName];
	}
};

DwtCssStyle.getComputedStyleObject = 
function(htmlElement) {
	if (htmlElement.ownerDocument == null) {
		// IE5.5 does not suppoert ownerDocument
		for (var parent = htmlElement.parentNode; parent.parentNode != null; parent = parent.parentNode) {}
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

/**
 * Adds a rule to a stylesheet.
 * 
 * @param {StyleSheet}	stylesheet		a CSS stylesheet
 * @param {string}		selector		rule selector
 * @param {string}		declaration		styles
 * @param {string}		index			insertion index (optional)
 * 
 * @return	index at which rule was inserted (for later removal)
 */
DwtCssStyle.addRule =
function(stylesheet, selector, declaration, index) {
	if (stylesheet.addRule) {	// IE
		//if index is not specified insert at the end so that new rule takes precedence
		index = index || (stylesheet.rules.length);
		stylesheet.addRule(selector, declaration, index);
	}
	else {
		//if index is not specified insert at the end so that new rule takes precedence
		index = index || (stylesheet.cssRules.length);
		stylesheet.insertRule(selector + "{" + declaration + "}", index);
	}
	return index;
};

/**
 * Removes the rule at the given index.
 * 
 * @param {StyleSheet}	stylesheet		a CSS stylesheet
 * @param {string}		index			insertion index (optional)
 */
DwtCssStyle.removeRule =
function(stylesheet, index) {
	if (stylesheet.removeRule) {	// IE
		stylesheet.removeRule(index);
	}
	else {
		stylesheet.deleteRule(index);
	}
};

DwtCssStyle.__PIXEL_RE = /^(-?[0-9]+(?:\.[0-9]*)?)px$/;
DwtCssStyle.__DIMENSION_RE = /^(-?[0-9]+(?:\.[0-9]*)?)([a-z]*|%)$/;
DwtCssStyle.__NUMBER_RE = /^(-?[0-9]+(?:\.[0-9]*)?)+$/

/**
 * Obtain the font size of the root element. We assume and verify that
 * it's specified in pixels.
 */
DwtCssStyle.__getRootFontSize =
function() {
	var fontsize =
		DwtCssStyle.getProperty(document.documentElement, 'font-size');

	if (!DwtCssStyle.__PIXEL_RE.test(fontsize)) {
		throw new Error('font size of root element is not in pixels!');
	}

	return parseInt(fontsize);
};

/**
 * Convert a CSS value to a pixel count; unhandled units raise an error.
 */
DwtCssStyle.asPixelCount =
function(val) {
	var dimension, unit, match;

	// assume pixels if no unit is specified
	if (typeof val === 'number' || DwtCssStyle.__NUMBER_RE.test(val)) {
		dimension = Number(val);
		unit = 'px';
	} else if ((match = DwtCssStyle.__DIMENSION_RE.exec(val))) {
		dimension = Number(match[1]);
		unit = match[2];
	} else {
		throw new Error('unsupported argument: ' + val);
	}

	switch (unit) {
		case 'rem': {
			return dimension * DwtCssStyle.__getRootFontSize();
		}

		// see http://www.w3.org/TR/css3-values/#absolute-lengths
		case 'mm': {
			dimension /= 10;
		}

		case 'cm': {
			dimension /= 2.54;
		}

		case 'in': {
			dimension *= 6;
		}

		case 'pc': {
			dimension *= 12;
		}

		case 'pt': {
			dimension /= 0.75;
		}

		case 'px': {
			return dimension;
		}

		case 'ch':
		case 'em':
		case 'ex': {
			throw new Error('cannot convert context-dependent CSS unit ' +
							unit);
		}

		default: {
			throw new Error('unrecognized CSS unit ' + unit);
		}
	}
};
