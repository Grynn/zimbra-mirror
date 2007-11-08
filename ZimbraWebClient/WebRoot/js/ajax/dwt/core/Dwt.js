/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
 * @constructor
 * @class
 * Dwt is a static class that defines a number of contants and helper methods that
 * support the Dwt package, as well as client's using Dwt
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 */

Dwt = function() {
};

// Constants for positioning
/**  Static position style
 * @type String */
Dwt.STATIC_STYLE = "static";

/** Absolute position style
 * @type String*/
Dwt.ABSOLUTE_STYLE = "absolute";

/** Relative position style
 * @type String*/
Dwt.RELATIVE_STYLE = "relative";

// Background repeat
/** Don't repeat background image
 * @type String */
Dwt.NO_REPEAT = "no-repeat";

/** Repeat background image
 * @type String */
Dwt.REPEAT = "repeat";

/** Repeat background image horizontally
 * @type String */
Dwt.REPEAT_X = "repeat-x";

/** Repeat background image vertically
 * @type String */
Dwt.REPEAT_Y = "repeat-y";


// display style
/** Inline display style
 * @type String
 */
Dwt.DISPLAY_INLINE = "inline";

/** Block display style
 * @type String
 */
Dwt.DISPLAY_BLOCK = "block";

/** No display style
 * @type String
 */
Dwt.DISPLAY_NONE = "none";

/** Table row style
 * @type String
 */
Dwt.DISPLAY_TABLE_ROW = AjxEnv.isIE ? Dwt.DISPLAY_BLOCK : "table-row";

/** Table cell style
 * @type String
 */
Dwt.DISPLAY_TABLE_CELL = AjxEnv.isIE ? Dwt.DISPLAY_BLOCK : "table-cell";

// constants for layout
/*
Dwt.LEFT = 100;
Dwt.RIGHT = 101;
Dwt.TOP = 102;
Dwt.BOTTOM = 103;

Dwt.ABOVE = 104;
Dwt.BELOW = 105;

Dwt.WIDTH = 106;
Dwt.HEIGHT = 107;
*/

// Scroll constants
/** Clip on overflow
 * @type Int*/
Dwt.CLIP = 1;

/** Allow overflow to be visible
 * @type Int*/
Dwt.VISIBLE = 2;

/** Automatically create scrollbars if content overflows
 * @type Int*/
Dwt.SCROLL = 3;

/** Always have scrollbars whether content overflows or not
 * @type Int*/
Dwt.FIXED_SCROLL = 4;


// z-index order
/** hidden layer. Elements at this layer will be hidden from view
 * @type Int*/
Dwt.Z_HIDDEN = 100;

/** Curtain layer.
 * @type Int
 * @see DwtShell*/
Dwt.Z_CURTAIN = 200;

/** Visible layer. Elements at this layer will be in view
 * @type Int*/
Dwt.Z_VIEW = 300;

/** DwtWindowManager.  It holds modeless dialogs (DwtResizableWindow).
 */
Dwt.Z_WINDOW_MANAGER = 490;

/** Popup menu layer. Used by the menu components
 * @type Int*/
Dwt.Z_MENU = 500;

/** Veil layer. The veil appears just behind modal dialogs render other components
 * unable to receive mouse input
 * @type Int*/
Dwt.Z_VEIL = 600;

/** Dialog layer. Dialogs are positioned at this layer
 * @type Int*/
Dwt.Z_DIALOG = 700;

/** Used by menus that are part of a dialog
 * @type Int*/
Dwt.Z_DIALOG_MENU = 750;

/** Tooltips layer
 * @type Int*/
Dwt.Z_TOOLTIP = 775;

/** Drag and Drop (DnD) icon layer. DnD icons are positioned at this layer so they
 * move across the top of other components
 * @type Int*/
Dwt.Z_DND = 800;		// Drag N Drop icons

/** This layer appears in front of other layers to block all user mouse input
 * @type Int*/
Dwt.Z_BUSY = 900;

Dwt.Z_TOAST = 950;

/** Used by the splash screens
 * @type Int*/
Dwt.Z_SPLASH = 1000;


/** Default value. Used when setting such things as size and bounds to indicate a
 * component should not be set. For example if setting size and not wishing to set
 * the height; Dwt.setSize(htmlElement, 100, Dwt.DEFAULT)
 * @type Int
 */
Dwt.DEFAULT = -123456789;

/** Used to clear a value. */
Dwt.CLEAR = -20000;

/** Offscreen position. Used when setting a elements position
 * @type Int
 */
Dwt.LOC_NOWHERE = -10000;

// Drag N Drop action constants
/** No drag and drop operation
 * @type Int
 */
Dwt.DND_DROP_NONE = 0;

/** Copy drag and drop operation
 * @type Int
 */
Dwt.DND_DROP_COPY = 1;

/** Move drag and drop operation
 * @type Int
 */
Dwt.DND_DROP_MOVE = 2;


// Keys used for retrieving data
// TODO JSDoc
Dwt.KEY_OBJECT = "_object_";
Dwt.KEY_ID = "_id_";


/** Constants related to the hack to make the blinking cursor show up in Firefox.
 * There is an explanation of this hack in Firefox's bug database:
 * https://bugzilla.mozilla.org/show_bug.cgi?id=167801#c6
 */
Dwt.CARET_HACK_ENABLED = AjxEnv.isFirefox;
if (Dwt.CARET_HACK_ENABLED) {
	Dwt.CARET_HACK_BEGIN = "<div style='overflow:auto;'>";
	Dwt.CARET_HACK_END = "</div>";
} else {
	Dwt.CARET_HACK_BEGIN = "";
	Dwt.CARET_HACK_END = "";
}

/** z-index increment unit. Used by compenets if they need to bump their z-index
 * @type Int
 */
Dwt._Z_INC = 1;


/** @private */
Dwt.__nextId = 1;

/**
 * This method is used to generate a unique id to be used for an HTML element's id
 * attribute.
 *
 * @return the next available element ID.
 * @type String
 */
Dwt.getNextId =
function() {
	return "DWT" + Dwt.__nextId++;
}
/**
 * This method builds an indirect association between a DOM object and a JavaScript
 * object. This indirection is important to prevent memory leaks (particularly in IE) by
 * not directly creating a circular reference between a DOM object
 *
 * @param {DOMElement} domElement The DOM element (typically an HTML element)
 * @param {Object} jsObject The JavaScript object
 *
 * @see #disassociateElementFromObject
 * @see #getObjectFromElement
 */
Dwt.associateElementWithObject =
function(domElement, jsObject, attrName) {
	domElement[attrName||"dwtObj"] = jsObject.__internalId = AjxCore.assignId(jsObject);
};

/**
 * This method breaks the indirect association between a DOM object and a JavaScript
 * object that was created by the <code>Dwt.associateElementWithObject</code>method
 *
 * @param {DOMElement} domElement The DOM element (typically an HTML element)
 * @param {Object} jsObject The JavaScript object
 *
 * @see #associateElementWithObject
 * @see #getObjectFromElement
 */
Dwt.disassociateElementFromObject =
function(domElement, jsObject, attrName) {
	if (domElement){
		domElement.removeAttribute(attrName||"dwtObj");
	}
	if (jsObject.__internalId){
		AjxCore.unassignId(jsObject.__internalId);
	}
};

Dwt.getObjectFromElement =
function(domElement, attrName) {
	return AjxCore.objectWithId(domElement[attrName||"dwtObj"]);
};

Dwt.findAncestor =
function(domElement, attrName) {
	while (domElement && (Dwt.getAttr(domElement, attrName) == void 0)) {
		domElement = domElement.parentNode;
	}
	return domElement;
};


Dwt.setHandler =
function(htmlElement, event, func) {
	if (event == DwtEvent.ONMOUSEWHEEL && AjxEnv.isGeckoBased) {
		Dwt.clearHandler(htmlElement, event);
	}
	htmlElement[event] = func;
	if (event == DwtEvent.ONMOUSEWHEEL && AjxEnv.isGeckoBased) {
		htmlElement.addEventListener("DOMMouseScroll", func, true);
	}
};

Dwt.clearHandler =
function(htmlElement, event) {
	if (event == DwtEvent.ONMOUSEWHEEL && AjxEnv.isGeckoBased) {
		if (htmlElement[event]) {
			var func = htmlElement[event];
			htmlElement.removeEventListener("DOMMouseScroll", func, true);
		}
	}
	htmlElement[event] = null;
};

Dwt.getBackgroundRepeat =
function(htmlElement) {
	return DwtCssStyle.getProperty(htmlElement, "background-repeat");
};

Dwt.setBackgroundRepeat =
function(htmlElement, style) {
	htmlElement.style.backgroundRepeat = style;
};

/**
 * Get the bounds of an htmlElement
 *
 * @param {HTMLElement} htmlElement
 *
 * @return The elements bounds
 * @type DwtRectangle
 *
 * @see #setBounds
 * @see #getLocation
 * @see #getSize
 */
Dwt.getBounds =
function(htmlElement, rect) {
	if (!Dwt.__tmpPoint)
		Dwt.__tmpPoint = new DwtPoint(0, 0);
	var tmpPt = Dwt.__tmpPoint;

	Dwt.getLocation(htmlElement, tmpPt);
	var locX = tmpPt.x;
	var locY = tmpPt.y;

	Dwt.getSize(htmlElement, tmpPt);

	if (!rect) {
		return new DwtRectangle(locX, locY, tmpPt.x, tmpPt.y);
	} else {
		rect.set(locX, locY, tmpPt.x, tmpPt.y);
		return rect;
	}
};

/**
 * Sets the bounds of an HTML element. The position type of the element must
 * be absolute or else an exception is thrown. To omit setting a value set the
 * actual parameter value to <i>Dwt.DEFAULT</i>
 *
 * @param {HTMLElement} htmlElement absolutely positioned HTML element
 * @param {Int|String} x x coordinate of the element. e.g. 10, "10px", Dwt.DEFAULT
 * @param {Int|String} y y coordinate of the element. e.g. 10, "10px", Dwt.DEFAULT
 * @param {Int} width width of the element e.g. 100, "100px", "75%", Dwt.DEFAULT
 * @param {Int} height height of the element  e.g. 100, "100px", "75%", Dwt.DEFAULT
 *
 * @throws DwtException
 *
 * @see #getBounds
 * @see #setLocation
 * @see #setSize
 */
Dwt.setBounds =
function(htmlElement, x, y, width, height) {
	Dwt.setLocation(htmlElement, x, y);
	Dwt.setSize(htmlElement, width, height);
};

/**
 * Given an html element returns the element's cursor
 *
 * @param {HTMLElement} htmlElement
 *
 * @return the html elements cursor
 * @type String
 *
 * @see #setCursor
 */
Dwt.getCursor =
function(htmlElement) {
	return DwtCssStyle.getProperty(htmlElement, "cursor");
};

/**
 * Sets an HTML element's cursor
 *
 * @param {HTMLElement} htmlElement element for which to set the cursor
 * @param {String} cursorName name of the new cursor
 *
 * @see #setCursor
 */
Dwt.setCursor =
function(htmlElement, cursorName) {
	htmlElement.style.cursor = cursorName;
};

/**
 * Returns the location of an html element
 *
 * @param {HTMLElement} htmlElement
 *
 * @return the location of <code>htmlElement</code>
 *
 * @see #setLocation
 * @see #getBounds
 * @see #getSize
 */
Dwt.getLocation =
function(htmlElement, point) {
	var point = (point) ? point : new DwtPoint(0, 0);
	if (htmlElement.style.position == Dwt.ABSOLUTE_STYLE) {
		point.set(parseInt(DwtCssStyle.getProperty(htmlElement, "left")),
		          parseInt(DwtCssStyle.getProperty(htmlElement, "top")));
		return point;
	} else {
		return Dwt.toWindow(htmlElement, 0, 0, null, null, point);
	}
};

/**
 * Sets the location of an HTML element. The position type of the element must
 * be absolute or else an exception is thrown. To only set one of the coordinates,
 * pass in a value of <i>Dwt.DEFAULT</i> for the coordinate for which the value is
 * not to be set
 *
 * @param {HTMLElement} htmlElement absolutely positioned HTML element
 * @param {Int|String} x x coordinate of the element. e.g. 10, "10px", Dwt.DEFAULT
 * @param {Int|String} y y coordinate of the element. e.g. 10, "10px", Dwt.DEFAULT
 *
 * @throws DwtException
 *
 * @see #getLocation
 * @see #setBounds
 * @see #setSize
 */
Dwt.setLocation =
function(htmlElement, x, y) {
	if (htmlElement.style.position != Dwt.ABSOLUTE_STYLE &&
		htmlElement.style.position != Dwt.RELATIVE_STYLE) {
		DBG.println(AjxDebug.DBG1, "Cannot position static widget " + htmlElement.className);
		throw new DwtException("Static widgets may not be positioned", DwtException.INVALID_OP, "Dwt.setLocation");
	}
	if (x = Dwt.__checkPxVal(x))
		htmlElement.style.left = x;
	if (y = Dwt.__checkPxVal(y))
		htmlElement.style.top = y;
};

Dwt.getPosition =
function(htmlElement) {
	return htmlElement.style.position;
};

Dwt.setPosition =
function(htmlElement, posStyle) {
	htmlElement.style.position = posStyle;
};

/**
 * Returns <code>htmlElement</code>'s scroll style. The scroll style determines the element's
 * behaviour when content overflows its boundries. Possible values are:
 * <ul>
 * <li><i>Dwt.CLIP</i> - Clip on overflow</li>
 * <li><i>Dwt.VISIBLE</i> - Allow overflow to be visible</li>
 * <li><i>Dwt.SCROLL</i> - Automatically create scrollbars if content overflows</li>
 * <li><i>Dwt.FIXED_SCROLL</i> - Always have scrollbars whether content overflows or not</li>
 * </ul>
 *
 * @param {HTMLElement} htmlElement HTML element
 *
 * @return the elements scroll style
 * @type Int
 */
Dwt.getScrollStyle =
function(htmlElement) {
	var overflow =  DwtCssStyle.getProperty(htmlElement, "overflow");
	if (overflow == "hidden")
		return Dwt.CLIP;
	else if (overflow =="auto")
		return Dwt.SCROLL;
	else if (overflow =="scroll")
		return Dwt.FIXED_SCROLL;
	else
		return Dwt.VISIBLE;
};

/**
 * Sets the <code>htmlElement</code>'s scroll style. The scroll style determines the elements's
 * behaviour when content overflows its div's boundries. Possible values are:
 * <ul>
 * <li><i>Dwt.CLIP</i> - Clip on overflow</li>
 * <li><i>Dwt.VISIBLE</i> - Allow overflow to be visible</li>
 * <li><i>Dwt.SCROLL</i> - Automatically create scrollbars if content overflows</li>
 * <li><i>Dwt.FIXED_SCROLL</i> - Always have scrollbars whether content overflows or not</li>
 * </ul>
 *
 * @param {HTMLElement} htmlElement HTML element
 * @param {Int} scrollStyle the elements's new scroll style
 */

Dwt.setScrollStyle =
function(htmlElement, scrollStyle) {
	if (scrollStyle == Dwt.CLIP)
		htmlElement.style.overflow = "hidden";
	else if (scrollStyle == Dwt.SCROLL)
		htmlElement.style.overflow = "auto";
	else if (scrollStyle == Dwt.FIXED_SCROLL)
		htmlElement.style.overflow = "scroll";
	else
		htmlElement.style.overflow = "visible";
};

// Note: in FireFox, offsetHeight includes border and clientHeight does not;
// may want to look at clientHeight for FF
Dwt.getSize =
function(htmlElement, point) {
	var p;
	if (!point) {
		p = new DwtPoint(0, 0);
	} else {
		p = point;
		p.set(0, 0);
	}

    if(!htmlElement) {return p;}
    if (htmlElement.offsetWidth != null) {
		p.x = htmlElement.offsetWidth;
		p.y = htmlElement.offsetHeight;
	} else if (htmlElement.clip && htmlElement.clip.width != null) {
		p.x = htmlElement.clip.width;
		p.y = htmlElement.clip.height;
	} else if (htmlElement.style && htmlElement.style.pixelWidth != null) {
		p.x = htmlElement.style.pixelWidth;
		p.y = htmlElement.style.pixelHeight;
	}
	p.x = parseInt(p.x);
	p.y = parseInt(p.y);
	return p;
};

Dwt.setSize =
function(htmlElement, width, height) {
	if(!htmlElement.style) {return;}
	if (width == Dwt.CLEAR)
		htmlElement.style.width = null;
	else if (width = Dwt.__checkPxVal(width, true))
		htmlElement.style.width = width;
	if (height == Dwt.CLEAR)
		htmlElement.style.height = null;
	else if (height = Dwt.__checkPxVal(height, true))
		htmlElement.style.height = height;
};

/**
* Measure the extent in pixels of a section of html. This is not the worlds cheapest
* method to invoke so do so judiciously
*
* @param {String} html html content for which that extents are to be calculated
*
* @return the extent of the content
* @type DwtPoint
*
* @see DwtPoint
*/
Dwt.getHtmlExtent =
function(html) {
	var div = AjxStringUtil.calcDIV();
	div.innerHTML = html;
	return Dwt.getSize(div);
};

Dwt.toDocumentFragment = function(html, id) {
    var div = AjxStringUtil.calcDIV();
    div.innerHTML = html;

    var fragment = document.createDocumentFragment();
    var container = id && document.getElementById(id);
    if (container) {
        fragment.appendChild(container);
    }
    else {
        for (var child = div.firstChild; child; child = div.firstChild) {
            fragment.appendChild(child);
        }
    }
    return fragment;
};

Dwt.getAttr =
function(htmlEl, attr, recursive) {
	// test for tagName so we dont try to eval non-html elements (i.e. document)
	if (!recursive) {
		return htmlEl && htmlEl.tagName
			? (htmlEl.getAttribute(attr) || htmlEl[attr])
			: null;
	} else {
		while (htmlEl) {
			if (Dwt.getAttr(htmlEl, attr) != null) {
				return htmlEl;
			}
			htmlEl = htmlEl.parentNode;
		}
		return null;
	}
};

Dwt.getVisible =
function(htmlElement) {
	var disp = DwtCssStyle.getProperty(htmlElement, "display");
	return (disp != Dwt.DISPLAY_NONE);
};

Dwt.setVisible =
function(htmlElement, visible) {
    var isRow = htmlElement.nodeName.match(/tr/i);
    var isCell = htmlElement.nodeName.match(/td|th/i);
    var display = isRow ? Dwt.DISPLAY_TABLE_ROW : (isCell ? Dwt.DISPLAY_TABLE_CELL : Dwt.DISPLAY_BLOCK);
    htmlElement.style.display = visible ? display : Dwt.DISPLAY_NONE;
};

Dwt.getVisibility =
function(htmlElement) {
	var vis = DwtCssStyle.getProperty(htmlElement, "visibility");
	return (vis == "visible");
};

Dwt.setVisibility =
function(htmlElement, visible) {
	htmlElement.style.visibility = visible ? "visible" : "hidden";
};

Dwt.__MSIE_OPACITY_RE = /alpha\(opacity=(\d+)\)/;

Dwt.getOpacity =
function(htmlElement) {
    if (AjxEnv.isIE) {
        var filter = htmlElement.style.filter;
        var m = Dwt.__MSIE_OPACITY_RE.exec(filter) || [ filter, "100" ];
        return Number(m[1]);
    }
    return Number(htmlElement.style.opacity || 1) * 100;
};

Dwt.setOpacity =
function(htmlElement, opacity) {
	if (AjxEnv.isIE) htmlElement.style.filter = "alpha(opacity="+opacity+")";
	else htmlElement.style.opacity = opacity/100;
};

Dwt.getZIndex =
function(htmlElement) {
	return DwtCssStyle.getProperty(htmlElement, "z-index");
};

Dwt.setZIndex =
function(htmlElement, idx) {
//DBG.println(AjxDebug.DBG3, "set zindex for " + htmlElement.className + ": " + idx);
	htmlElement.style.zIndex = idx;
};

Dwt.getDisplay =
function(htmlElement) {
	DwtCssStyle.getProperty(htmlElement, "display");
};

Dwt.setDisplay =
function(htmlElement, value) {
	htmlElement.style.display = value;
};

/**
* Returns the window size of the browser
*/
Dwt.getWindowSize =
function(point) {
	var p = (!point) ? new DwtPoint(0, 0) : point;
	if (window.innerWidth) {
		p.x = window.innerWidth;
		p.y = window.innerHeight;
	} else if (AjxEnv.isIE6CSS) {
		p.x = document.body.parentElement.clientWidth;
		p.y = document.body.parentElement.clientHeight;
	} else if (document.body && document.body.clientWidth) {
		p.x = document.body.clientWidth;
		p.y = document.body.clientHeight;
	}
	return p;
}

Dwt.toWindow =
function(htmlElement, x, y, containerElement, dontIncScrollTop, point) {
	var p;
	if (!point) {
		p = new DwtPoint(x, y);
	} else {
		p = point;
		p.set(x, y);
	}

	var offsetParent = htmlElement;
	while (offsetParent && offsetParent != containerElement) {
		p.x += offsetParent.offsetLeft - offsetParent.scrollLeft;
		p.y += offsetParent.offsetTop;
		if (!dontIncScrollTop) {
			var scrollTop = AjxEnv.isOpera ? offsetParent.pageYOffset : offsetParent.scrollTop;
			if (scrollTop) {
				p.y -= scrollTop;
			}
			var parentNode = offsetParent.parentNode;
			while (parentNode != offsetParent.offsetParent && parentNode != containerElement) {
				scrollTop = AjxEnv.isOpera ? parentNode.pageYOffset : parentNode.scrollTop;
				if (scrollTop) {
					p.y -= scrollTop;
				}
				parentNode = parentNode.parentNode;
			}
		}
		offsetParent = offsetParent.offsetParent;
	}
	return p;
};

Dwt.getInsets = function(htmlElement) {
	// return an object with the insets (border + padding size) for each side of the element, eg:
	//		{ left: 3, top:0, right:3, bottom:0 }
	// NOTE: assumes values from computedStyle are returned in pixels!!!

	var style = DwtCssStyle.getComputedStyleObject(htmlElement);

	var bl = parseInt(style.borderLeftWidth) 	|| 0;
	var bt = parseInt(style.borderTopWidth) 	|| 0;
	var br = parseInt(style.borderRightWidth)	|| 0;
	var bb = parseInt(style.borderBottomWidth)	|| 0;

	var pl = parseInt(style.paddingLeft) 	|| 0;
	var pt = parseInt(style.paddingTop) 	|| 0;
	var pr = parseInt(style.paddingRight)	|| 0;
	var pb = parseInt(style.paddingBottom)	|| 0;

	return {
			left 	: bl + pl,
			top  	: bt + pt,
			right 	: br + pr,
			bottom	: bb + pb
		};
}

Dwt.insetBounds = function(bounds, insets) {
	// given a 'bounds' object [from Dwt.getBounds()] 
	//	and an 'insets' object [from Dwt.getInsets()]
	//	munge the bounds so it takes the insets into account.
	// Useful to get the inner dimensions of an element.
	if (isNaN(bounds.x) || isNaN(insets.left)) return bounds;
	bounds.x += insets.left;
	bounds.y += insets.top;
	bounds.width  -= insets.left + insets.right;
	bounds.height -= insets.top + insets.bottom;
	return bounds;
}

Dwt.setStatus =
function(text) {
	window.status = text;
};

Dwt.getTitle =
function() {
	return window.document.title;
};

Dwt.setTitle =
function(text) {
	window.document.title = text;
};

Dwt.getIframeDoc =
function(iframeObj) {
	if (iframeObj) {
		return AjxEnv.isIE
			? iframeObj.contentWindow.document
			: iframeObj.contentDocument;
	}
	return null;
};

Dwt.getIframeWindow =
function(iframeObj) {
	return iframeObj.contentWindow;
};

/**
* Creates and returns an element from a string of HTML.
*
* @param {String} html HTML text
* @param {Boolean} isRow true if the element is a TR (optional)
*
* @return an HTMLElement with the <code>html</code> as its content. if <code>isRow</code.
* 		is true, then the element will be a table
* @type HTMLElement
*/
Dwt.parseHtmlFragment =
function(html, isRow) {
	if (!Dwt._div)
		Dwt._div = document.createElement('div');
	// TR element needs to have surrounding table
	if (isRow)
		html = "<table style='table-layout:fixed'>" + html + "</table>";
	Dwt._div.innerHTML = html;

	if (isRow) {
		var fragment = document.createDocumentFragment();
		var rows = Dwt._div.firstChild.rows;
		for (var i = rows.length - 1; i >= 0; i--) {
			// NOTE: We always grab the first row because once we append it
			//       to the fragment, it will be removed from the table.
			fragment.appendChild(rows[0]);
		}
		return fragment.childNodes.length > 1 ? fragment : fragment.firstChild;
	}
	return Dwt._div.firstChild;
};

Dwt.contains =
function(parentEl, childEl) {
  	var isContained = false;
	if (parentEl.compareDocumentPosition) {
		var relPos = parentEl.compareDocumentPosition(childEl);
		if ((relPos == (document.DOCUMENT_POSITION_CONTAINED_BY | document.DOCUMENT_POSITION_FOLLOWING))) {
			isContained = true;
		}
  	} else if (parentEl.contains) {
  		isContained = parentEl.contains(childEl);
  	}
  	return isContained;
};

Dwt.removeChildren =
function(htmlEl) {
	while (htmlEl.hasChildNodes())
		htmlEl.removeChild(htmlEl.firstChild);
};

/**
* Opera always returns zero for cellIndex property of TD element :(
*
* @param cell		TD object we want cell index for
*/
Dwt.getCellIndex =
function(cell) {
	if (AjxEnv.isOpera) {
		if (cell.tagName && cell.tagName.toLowerCase() == "td") {
			// get the cells collection from the TD's parent TR
			var cells = cell.parentNode.cells;
			var len = cells.length;
			for (var i = 0; i < len; i++) {
				if (cells[i] == cell)
					return i;
			}
		}
	} else {
		return cell.cellIndex;
	}
	return -1;
};

/**
 * Remove the <code>del</code> class name from the element's CSS class names and
 * optionally add <code>add</code> class name if given provided
 *
 * @param {HTMLElement} el HTML Element to which to add/delete class names
 * @param {String} del Class name to delete (optional)
 * @param {String} add Class name to add (optional)
 */
Dwt.delClass =
function(el, del, add) {
	if (el == null) { return };

	if (typeof del == "string") {
		del = Dwt._DELCLASS_CACHE[del] || (Dwt._DELCLASS_CACHE[del] = new RegExp("\\b" + del + "\\b", "ig"));
	}
	var className = el.className || "";
	className = className.replace(del, " ");
	el.className = add ? className + " " + add : className;
};

// cache the regexps here to avoid compiling the same regexp multiple times
Dwt._DELCLASS_CACHE = {};

/**
 * Adds the given class name to the element's CSS class names
 *
 * @param {HTMLElement} el HTML Element to which to add the class name
 * @param {String} c Class name
 *
 * @see #delClass
 */
Dwt.addClass =
function(el, c) {
	Dwt.delClass(el, c, c);
};

/**
 * Conditionally add or remove a class name from an element
 *
 * @param {HTMLElement} el target element
 * @param {boolean} condition condition to check
 * @param {String} a class name when condition is true
 * @param {String} b class name when condition is false
 */
Dwt.condClass = function(el, condition, a, b) {
	if (!!condition)
		Dwt.delClass(el, b, a);
	else
		Dwt.delClass(el, a, b);
};

/**
 * Sets the selection range.
 *
 * @param {input|iframe} input input for which to find the selection start point. This
 * 		may be a text input field or an iframe in design mode
 * @param {Int} start starting position
 * @param {Int} end ending position
 *
 *
 * @see #getSelectionStart
 * @see #getSelectionEnd
 * @see #setSelectionText
 */
Dwt.setSelectionRange = function(input, start, end) {
	if (AjxEnv.isGeckoBased) {
		input.setSelectionRange(start, end);
	} else if (AjxEnv.isIE) {
		var range = input.createTextRange();
		range.collapse(true);
		range.moveStart("character", start);
		range.moveEnd("character", end - start);
		range.select();
	} else {
		// FIXME: find solutions for other browsers
		input.select();
	}
};

/**
 * Retrieves the start of the selection.  For a collapsed range, this is
 * equivalent to getSelectionEnd.  Based on some reverse engineering that I
 * described here: http://www.bazon.net/mishoo/articles.epl?art_id=1292
 *
 * @param {input|iframe} input input for which to find the selection start point. This
 * 		may be a text input field or an iframe in design mode
 *
 * @return starting position of the selection
 * @type Int
 *
 * @see #getSelectionEnd
 * @see #setSelectionText
 * @see #setSelectionRange
 */
Dwt.getSelectionStart = function(input) {
	if (AjxEnv.isGeckoBased) {
		return input.selectionStart;
	} else if (AjxEnv.isIE) {
		var range = document.selection.createRange();
		var isCollapsed = range.compareEndPoints("StartToEnd", range) == 0;
		if (!isCollapsed)
			range.collapse(true);
		var b = range.getBookmark();
		return b.charCodeAt(2) - 2;
	}
	// FIXME: find solutions for other browsers
	return input.value.length;
};

/**
 * Retrieves the end of the selection.
 *
 * @param {input|iframe} input input for which to find the selection start point. This
 * 		may be a text input field or an iframe in design mode
 *
 * @return starting position of the selection
 * @type Int
 *
 * @see #getSelectionStart
 * @see #setSelectionText
 * @see #setSelectionRange
 */
Dwt.getSelectionEnd = function(input) {
	if (AjxEnv.isGeckoBased) {
		return input.selectionEnd;
	} else if (AjxEnv.isIE) {
		var range = document.selection.createRange();
		var isCollapsed = range.compareEndPoints("StartToEnd", range) == 0;
		if (!isCollapsed)
			range.collapse(false);
		var b = range.getBookmark();
		return b.charCodeAt(2) - 2;
	}
	// FIXME: find solutions for other browsers
	return input.value.length;
};

/**
 * Sets the selection text
 *
 * @param {input|iframe} input input for which to find the selection start point. This
 * 		may be a text input field or an iframe in design mode
 * @param {String} text Text to set as the selection
 *
 * @see #getSelectionStart
 * @see #getSelectionEnd
 * @see #setSelectionRange
 */
Dwt.setSelectionText = function(input, text) {
	var start = Dwt.getSelectionStart(input);
	var end = Dwt.getSelectionEnd(input);
	var str = input.value;
	var val = [ str.substr(0, start),
		    text,
		    str.substr(end) ].join("");
	if (typeof input.setValue == "function")
		input.setValue(val);
	else
		input.value = val;
	Dwt.setSelectionRange(input, start, start + text.length);
};

Dwt.instanceOf =
function(obj, className) {
	return (window[className] && obj instanceof window[className]);
};

//////////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS
//////////////////////////////////////////////////////////////////////////////////

/** @private */
Dwt.__checkPxVal =
function(val, check) {
	if (val == Dwt.DEFAULT) return false;

	if (check && val < 0 && val != Dwt.LOC_NOWHERE) {
		DBG.println(AjxDebug.DBG1, "negative pixel value: " + val);
		val = 0;
	}
	if (typeof(val) == "number")
		val = val + "px";

	return val;
};






/////////////
//	NEW STUFF FROM OWEN
/////////////
Dwt.byId = function(id) {
	return (typeof id == "string" ? document.getElementById(id) : id);
}
Dwt.byTag = function(tagName) {
	return document.getElementsByTagName(tagName);
}

Dwt.show = function(it) {
	Dwt.setVisible(Dwt.byId(it),true);
}

Dwt.hide = function(it) {
	Dwt.setVisible(Dwt.byId(it),false);
}

Dwt.toggle = function(it, show) {
	it = Dwt.byId(it);
	if (show == null) show = (Dwt.getVisible(it) != true);
	Dwt.setVisible(it, show);
}

//setText Methods

Dwt.setText = function(htmlEl,text){
	htmlEl.appendChild(document.createTextNode(text));
};

Dwt.populateText = function(){
		if(arguments.length == 0 ) return;
		var node, index = 0, length = arguments.length;
		while(index < length){
			node = document.getElementById(arguments[index]);
			if(node) Dwt.setText(node,arguments[index+1]);
			index += 2;
		}
};

//setHtml Methods

Dwt.setInnerHtml = function(htmlEl,html){
	htmlEl.innerHTML = html;
};