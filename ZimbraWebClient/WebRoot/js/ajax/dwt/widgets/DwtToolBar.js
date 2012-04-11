/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
 * @overview
 * This file defines a toolbar.
 *
 */

/**
 * Creates a toolbar.
 * @constructor
 * @class
 * Creates a toolbar. Components must be added via the <code>add*()</code> functions.
 * A toolbar is a horizontal or vertical strip of widgets (usually buttons).
 *
 * @author Ross Dargahi
 * 
 * @param {hash}	params		a hash of parameters
 * @param	{DwtComposite}	params.parent	the parent widget
 * @param	{string}	params.className				the CSS class
 * @param	{DwtToolBar.HORIZ_STYLE|DwtToolBar.VERT_STYLE}	params.posStyle		the positioning style
 * @param	{constant}	params.style					the menu style
 * @param	{number}	params.index 				the index at which to add this control among parent's children
 * 
 * @extends	DwtComposite
 */
DwtToolBar = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtToolBar.PARAMS);

	params.className = params.className || "ZToolbar";
	DwtComposite.call(this, params);

	// since we attach event handlers at the toolbar level, make sure we don't double up on
	// handlers when we have a toolbar within a toolbar
	if (params.parent instanceof DwtToolBar) {
		this._hasSetMouseEvents = params.parent._hasSetMouseEvents;
	}
	if (params.handleMouse !== false && !this._hasSetMouseEvents) {
		var events = AjxEnv.isIE ? [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP] :
								   [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP, DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT];
		this._setEventHdlrs(events);
		this._hasSetMouseEvents = true;
	}

	this._style = params.style || DwtToolBar.HORIZ_STYLE;
    this._items = [];
    this._createHtml();

    this._numFillers = 0;
	this._curFocusIndex = 0;

	var suffix = (this._style == DwtToolBar.HORIZ_STYLE) ? "horiz" : "vert";
	this._keyMapName = ["DwtToolBar", suffix].join("-");
};

DwtToolBar.PARAMS = ["parent", "className", "posStyle", "style", "index", "id"];

DwtToolBar.prototype = new DwtComposite;
DwtToolBar.prototype.constructor = DwtToolBar;

/**
 * Returns a string representation of the object.
 * 
 * @return		{string}		a string representation of the object
 */
DwtToolBar.prototype.toString =
function() {
	return "DwtToolBar";
};

//
// Constants
//

/**
 * Defines the "horizontal" style.
 */
DwtToolBar.HORIZ_STYLE	= 1;
/**
 * Defines the "vertical" style.
 */
DwtToolBar.VERT_STYLE	= 2;

DwtToolBar.ELEMENT		= 1;
DwtToolBar.SPACER		= 2;
DwtToolBar.SEPARATOR	= 3;
DwtToolBar.FILLER		= 4;

DwtToolBar.FIRST_ITEM    = "ZFirstItem";
DwtToolBar.LAST_ITEM     = "ZLastItem";
DwtToolBar.SELECTED_NEXT = DwtControl.SELECTED + "Next";
DwtToolBar.SELECTED_PREV = DwtControl.SELECTED + "Prev";
DwtToolBar._NEXT_PREV_RE = new RegExp(
    "\\b" +
    [ DwtToolBar.SELECTED_NEXT, DwtToolBar.SELECTED_PREV ].join("|") +
    "\\b", "g"
);

//
// Data
//

// main template

DwtToolBar.prototype.TEMPLATE = "dwt.Widgets#ZToolbar";

// item templates

DwtToolBar.prototype.ITEM_TEMPLATE = "dwt.Widgets#ZToolbarItem";
DwtToolBar.prototype.SEPARATOR_TEMPLATE = "dwt.Widgets#ZToolbarSeparator";
DwtToolBar.prototype.SPACER_TEMPLATE = "dwt.Widgets#ZToolbarSpacer";
DwtToolBar.prototype.FILLER_TEMPLATE = "dwt.Widgets#ZToolbarFiller";

// static data

DwtToolBar.__itemCount = 0;

//
// Public methods
//

DwtToolBar.prototype.dispose =
function() {
	this._itemsEl = null;
	this._prefixEl = null;
	this._suffixEl = null;
	DwtComposite.prototype.dispose.call(this);
};

/**
 * Gets the item.
 * 
 * @param	{int}		index	the index
 * @return	{Object}	the item
 */
DwtToolBar.prototype.getItem =
function(index) {
	return this._children.get(index);
};

/**
 * Gets the item count.
 * 
 * @return	{number}	the size of the children items
 */
DwtToolBar.prototype.getItemCount =
function() {
	return this._children.size();
};

/**
 * Gets the items.
 * 
 * @return	{array}	an array of children items
 */
DwtToolBar.prototype.getItems =
function() {
	return this._children.getArray();
};

// item creation
/**
 * Adds a spacer.
 * 
 * @param	{number}	size		the space size
 * @param	{number}	index		the index for the spacer
 * @return	{Object}	the newly added element
 */
DwtToolBar.prototype.addSpacer =
function(size, index) {
    var el = this._createSpacerElement();
	this._addItem(DwtToolBar.SPACER, el, index);
	return el;
};

/**
 * Adds a separator.
 * 
 * @param	{string}	className	the separator CSS class name
 * @param	{number}	index		the index for the separator
 * @return	{Object}	the newly added element
 */
DwtToolBar.prototype.addSeparator =
function(className, index) {
	var el = this._createSeparatorElement();
	this._addItem(DwtToolBar.SEPARATOR, el, index);
	return el;
};

/**
 * Removes a separator.
 * 
 * @param	{Object}	el		the element
 */
DwtToolBar.prototype.removeSeparator =
function(el) {
	this._removeItem(el);
};

/**
 * Adds a filler.
 * 
 * @param	{string}	className	the CSS class name
 * @param	{number}	index		the index for the filler
 * @return	{Object}	the newly added element
 */
DwtToolBar.prototype.addFiller =
function(className, index) {
	var el = this._createFillerElement();
	this._addItem(DwtToolBar.FILLER, el, index);
	return el;
};

// DwtComposite methods

/**
 * Adds a child item.
 * 
 * @param	{Object}	child	the child item
 * @param	{number}	index		the index for the child
 */
DwtToolBar.prototype.addChild =
function(child, index) {
	
	if (child.isDwtControl) {
    	DwtComposite.prototype.addChild.apply(this, arguments);
	}

    var itemEl = this._createItemElement();
    itemEl.appendChild(child.isDwtControl ? child.getHtmlElement() : child);

    this._addItem(DwtToolBar.ELEMENT, itemEl, index);
};

// keyboard nav

/**
 * Gets the key map name.
 * 
 * @return	{string}	the key map name
 */
DwtToolBar.prototype.getKeyMapName =
function() {
    return this._keyMapName;
};

DwtToolBar.prototype.handleKeyAction =
function(actionCode, ev) {

	var item = this.getItem(this._curFocusIndex);
	var numItems = this.getItemCount();
	if (numItems < 2) {
		return true;
	}

	switch (actionCode) {

		case DwtKeyMap.PREV:
			if (this._curFocusIndex > 0) {
				this._moveFocus(true);
			}
			break;

		case DwtKeyMap.NEXT:
			if (this._curFocusIndex < (numItems - 1)) {
				this._moveFocus();
			}
			break;

		default:
			// pass everything else to currently focused item
			if (item) {
				return item.handleKeyAction(actionCode, ev);
			}
	}
	return true;
};

//
// Protected methods
//

// utility

/**
 * @private
 */
DwtToolBar.prototype._createItemId =
function(id) {
    id = id || this._htmlElId;
    var itemId = [id, "item", ++DwtToolBar.__itemCount].join("_");
    return itemId;
};

// html creation

/**
 * @private
 */
DwtToolBar.prototype._createHtml =
function() {
    var data = { id: this._htmlElId };
    this._createHtmlFromTemplate(this.TEMPLATE, data);
    this._itemsEl = document.getElementById(data.id+"_items");
    this._prefixEl = document.getElementById(data.id+"_prefix");
    this._suffixEl = document.getElementById(data.id+"_suffix");
};

/**
 * @private
 */
DwtToolBar.prototype._createItemElement =
function(templateId) {
        templateId = templateId || this.ITEM_TEMPLATE;
        var data = { id: this._htmlElId, itemId: this._createItemId() };
        var html = AjxTemplate.expand(templateId, data);

        // the following is like scratching your back with your heel:
        //     var fragment = Dwt.toDocumentFragment(html, data.itemId);
        //     return (AjxUtil.getFirstElement(fragment));

        var cont = AjxStringUtil.calcDIV();
        cont.innerHTML = html;
        return cont.firstChild.rows[0].cells[0]; // DIV->TABLE->TR->TD
};

/**
 * @private
 */
DwtToolBar.prototype._createSpacerElement =
function(templateId) {
    return this._createItemElement(templateId || this.SPACER_TEMPLATE);
};

/**
 * @private
 */
DwtToolBar.prototype._createSeparatorElement =
function(templateId) {
    return this._createItemElement(templateId || this.SEPARATOR_TEMPLATE);
};

/**
 * @private
 */
DwtToolBar.prototype._createFillerElement =
function(templateId) {
    return this._createItemElement(templateId || this.FILLER_TEMPLATE);
};

// item management

/**
 * @private
 */
DwtToolBar.prototype._addItem =
function(type, element, index) {

    // get the reference element for insertion
    var placeEl = this._items[index] || this._suffixEl;

    // insert item
	var spliceIndex = index || (typeof index == "number") ? index : this._items.length;
	this._items.splice(spliceIndex, 0, element);
    this._itemsEl.insertBefore(element, placeEl);

    // append spacer
    // TODO!
};

/**
 * @private
 */
DwtToolBar.prototype._removeItem =
function(item) {
	for (var i = 0; i < this._items.length; i++) {
		if (this._items[i] == item) {
			this._items.splice(i,1);
			this._itemsEl.removeChild(item);
			break;
		}
	}
};

/**
 * transfer focus to the current item
 * @private
 */
DwtToolBar.prototype._focus =
function(item) {
	DBG.println(AjxDebug.DBG3, "DwtToolBar: FOCUS");
	// make sure the key for expanding a button submenu matches our style
	if (!this._submenuKeySet) {
		var kbm = this.shell.getKeyboardMgr();
		if (kbm.isEnabled()) {
			var kmm = kbm.__keyMapMgr;
			if (kmm) {
				if (this._style == DwtToolBar.HORIZ_STYLE) {
					kmm.removeMapping("DwtButton", "ArrowRight");
					kmm.setMapping("DwtButton", "ArrowDown", DwtKeyMap.SUBMENU);
				} else {
					kmm.removeMapping("DwtButton", "ArrowDown");
					kmm.setMapping("DwtButton", "ArrowRight", DwtKeyMap.SUBMENU);
				}
				kmm.reloadMap("DwtButton");
			}
		}
		this._submenuKeySet = true;
	}

	item = item ? item : this._getFocusItem(this._curFocusIndex);
	if (item) {
		item._hasFocus = true;	// so that focus class is set
		item._focus();
	} else {
		// if current item isn't focusable, find first one that is
		this._moveFocus();
	}
};

/**
 * blur the current item.
 * 
 * @private
 */
DwtToolBar.prototype._blur =
function(item) {
	DBG.println(AjxDebug.DBG3, "DwtToolBar: BLUR");
	item = item ? item : this._getFocusItem(this._curFocusIndex);
	if (item) {
		item._hasFocus = false;
		item._blur();
	}
};

/**
 * Returns the item at the given index, as long as it can accept focus.
 * For now, we only move focus to simple components like buttons. Also,
 * the item must be enabled and visible.
 *
 * @param {number}	index		the index of item within toolbar
 * @return	{Object}	the item
 * 
 * @private
 */
DwtToolBar.prototype._getFocusItem =
function(index) {
	var item = this.getItem(index);
	if (!item || (item instanceof DwtToolBar))	{ return null; }
	if (item._noFocus)							{ return null; }
	if (item.getEnabled && !item.getEnabled())	{ return null; }
	if (item.getVisible && !item.getVisible())	{ return null; }
	return item;
};

/**
 * Moves focus to next or previous item that can take focus.
 *
 * @param {boolean}	back		if <code>true</code>, move focus to previous item
 * 
 * @private
 */
DwtToolBar.prototype._moveFocus =
function(back) {
	var index = this._curFocusIndex;
	var maxIndex = this.getItemCount() - 1;
	var item = null;
	while (!item && index >= 0 && index <= maxIndex) {
		index = back ? index - 1 : index + 1;
		item = this._getFocusItem(index);
	}
	if (item) {
		this._blur();
		this._curFocusIndex = index;
		this._focus(item);
	}
};

/**
 * @private
 */
DwtToolBar.prototype.__markPrevNext =
function(id, opened) {
    var index = this.__getButtonIndex(id);
    var prev = this.__getButtonAt(index - 1);
    var next = this.__getButtonAt(index + 1);
    if (opened) {
        if (prev) Dwt.delClass(prev.getHtmlElement(), DwtToolBar._NEXT_PREV_RE, DwtToolBar.SELECTED_PREV);
        if (next) Dwt.delClass(next.getHtmlElement(), DwtToolBar._NEXT_PREV_RE, DwtToolBar.SELECTED_NEXT);
    }
    else {
        if (prev) Dwt.delClass(prev.getHtmlElement(), DwtToolBar._NEXT_PREV_RE);
        if (next) Dwt.delClass(next.getHtmlElement(), DwtToolBar._NEXT_PREV_RE);
    }

    // hack: mark the first and last items so we can style them specially
    //	MOW note: this should really not be here, as it only needs to be done once,
    //				but I'm not sure where to put it otherwise
    var first = this.__getButtonAt(0);
    if (first) Dwt.addClass(first.getHtmlElement(), DwtToolBar.FIRST_ITEM);

    var last = this.__getButtonAt(this.getItemCount()-1);
    if (last) Dwt.addClass(last.getHtmlElement(), DwtToolBar.LAST_ITEM);
};

/**
 * Find the array index of a toolbar button by id.
 *
 * Works only if descendent classes implement the _buttons property as a
 * native Array.
 * @param id {number} Index to check and see if exists in the array.
 * @return {number} Index of the id in the array, or -1 if the id does not
 * exist.
 * @private
 */
DwtToolBar.prototype.__getButtonIndex =
function(id) {
    var toolBarButtons = this.getChildren();
    var button = this._buttons[id];
    if (toolBarButtons && toolBarButtons.length && button)
        return AjxUtil.indexOf(toolBarButtons, button);
    return -1;
};

/**
 * Find a toolbar button by id.
 *
 * Works only if descendent classes implement the _buttons property as a
 * native Array.
 * @param index {number} The integer index of the button to retrieve.
 * @return {DwtButton} The DWT button at the current index, or null if the
 * buton does not exist.
 * @private
 */
DwtToolBar.prototype.__getButtonAt =
function(index) {
    var i = 0;
    // NOTE: _buttons seems to always be implemented as an Array.
    // This code should not be needed because:
    // * If we're working with Objects-as-associative-arrays, we don't want
    //   numeric indexes.
    // * If we're working with arrays, id will ALWAYS be i if found, or
    //   undefined if not found. This function could be done via a simple
    //   return this._buttons && this._buttons[index];
    //   or something close to that if falsey values might be valid.
    for (var name in this._buttons) {
        // NOTE: Protect from native Array.prototype extensions
        if (this._buttons.hasOwnProperty(name)) {
            if (i == index) {
                return this._buttons[name];
            }
            i++;
        }
    }
    return null;
};

//
// Classes
//

/**
 * Creates a tool bar button.
 * @constructor
 * @class
 * This class represents a toolbar button.
 * 
 * @param	{hash}		params		a hash of parameters
 * @param {DwtComposite}	parent		the parent widget
 * @param {constant}	style				the menu style
 * @param {string}	className				the CSS class
 * @param {DwtToolBar.HORIZ_STYLE|DwtToolBar.VERT_STYLE}	posStyle		the positioning style
 * @param {Object}	actionTiming 	the action timing
 * @param {string}	id 	the id
 * @param {number}	index 				the index at which to add this control among parent's children
 *
 * @extends	DwtButton
 */
DwtToolBarButton = function(params) {
	if (arguments.length == 0) { return; }
	var params = Dwt.getParams(arguments, DwtToolBarButton.PARAMS);
	params.className = params.className || "ZToolbarButton";
	DwtButton.call(this, params);
};

DwtToolBarButton.PARAMS = ["parent", "style", "className", "posStyle", "actionTiming", "id", "index"];

DwtToolBarButton.prototype = new DwtButton;
DwtToolBarButton.prototype.constructor = DwtToolBarButton;

DwtToolBarButton.prototype.isDwtToolBarButton = true;
DwtToolBarButton.prototype.toString = function() { return "DwtToolBarButton"; };

// Data
DwtToolBarButton.prototype.TEMPLATE = "dwt.Widgets#ZToolbarButton";
