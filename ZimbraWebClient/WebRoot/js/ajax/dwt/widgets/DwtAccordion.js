/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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
 * Creates an empty accordion widget.
 * @constructor
 * @class
 * This class implements an accordion widget, which is a stack of expandable
 * accordion headers. Clicking on an accordion header's button expands it in
 * place.
 *
 * @author Parag Shah
 *
 * @param {DwtControl}	parent	the parent widget
 * @param {string}	className	the CSS class
 * @param {Dwt.STATIC_STYLE|Dwt.ABSOLUTE_STYLE|Dwt.RELATIVE_STYLE}	posStyle	the positioning style
 * 
 * @extends		DwtComposite
 */
DwtAccordion = function(parent, className, posStyle) {

	if (arguments.length == 0) return;
	DwtComposite.call(this, {parent:parent, className:className, posStyle:(posStyle || Dwt.ABSOLUTE_STYLE)});

	this._initialize(className);
};

DwtAccordion.prototype = new DwtComposite;
DwtAccordion.prototype.constructor = DwtAccordion;


// Public Methods

/**
 * Returns a string representation of the object.
 * 
 * @return		{string}		a string representation of the object
 */
DwtAccordion.prototype.toString =
function() {
	return "DwtAccordion";
};

/**
 * Adds an item to the accordion, in the form of a table row.
 *
 * @param {hash}	params		a hash of parameters
 * @param {string}      params.title			the text for accordion header
 * @param {hash}      params.data			the item data
 * @param {string}      params.icon			the icon
 * @param {boolean}      params.hideHeader	if <code>true</code>, do not show header (ideal when there's only one visible header item)
 */
DwtAccordion.prototype.addAccordionItem =
function(params) {

	if (!this.isListenerRegistered(DwtEvent.CONTROL)) {
		this.addControlListener(new AjxListener(this, this._controlListener));
	}

	var itemNum = this.__ITEMCOUNT++;
	var item = new DwtAccordionItem(itemNum, params.title, params.data, this);
	var subs = {
		id: this._htmlElId,
		itemNum: itemNum,
		title: params.title,
		icon: params.icon
	};

	// append new accordion item
	var row = this._table.insertRow(-1);
	var cell = row.insertCell(-1);
	cell.id = this._htmlElId + "_cell_" + itemNum;
	cell.className = "ZAccordionCell";
	cell.innerHTML = AjxTemplate.expand("dwt.Widgets#ZAccordionItem", subs);

	// add onclick event handler to header DIV
	var headerDiv = document.getElementById(this._htmlElId + "_header_" + itemNum);
	if (params.hideHeader) {
		Dwt.setVisible(headerDiv, false);
	} else {
		headerDiv.onclick = AjxCallback.simpleClosure(this._handleOnClickHeader, this, item);
		headerDiv.oncontextmenu = AjxCallback.simpleClosure(this._handleOnRightClickHeader, this, item);
		headerDiv.onmouseover = AjxCallback.simpleClosure(this._handleOnMouseoverHeader, this, item);
		headerDiv.onmouseout = AjxCallback.simpleClosure(this._handleOnMouseoutHeader, this, item);
	}
	this._items.push(item);

	return item;
};

/**
 * Gets the ordered list of accordion items.
 * 
 * @return	{array}	an array of {@link DwtAccordionItem} objects
 */
DwtAccordion.prototype.getItems =
function() {
	return this._items;
};

/**
 * Gets the accordion item with the given ID.
 *
 * @param {number}	id	the accordion item ID
 * @return	{DwtAccordionItem}		the item or <code>null</code> if not found
 */
DwtAccordion.prototype.getItem =
function(id) {
	for (var i = 0; i < this._items.length; i++) {
		if (this._items[i].id == id) {
			return this._items[i];
		}
	}
	return null;
};

/**
 * Gets the item by index.
 * 
 * @param {number}	id	the accordion item index
 * @return	{DwtAccordionItem}		the item or <code>null</code> if not found
 */
DwtAccordion.prototype.getItemByIndex =
function(index) {
	return (index >=0 && index < this._items.length) ? this._items[index] : null;
}

/**
 * Hides all accordion items.
 */
DwtAccordion.prototype.hideAccordionItems =
function() {
	for (var i = 0; i < this._items.length; i++) {
		var header = document.getElementById(this._htmlElId + "_header_" + this._items[i].id);
		if (header) {
			Dwt.setVisible(header, false);
		}
	}
};

/**
 * Shows single accordion item based on given id.
 *
 * @param {number}	id	the accordion item ID
 */
DwtAccordion.prototype.showAccordionItem =
function(id) {
	var header = document.getElementById(this._htmlElId + "_header_" + id);
	if (header) {
		Dwt.setVisible(header, true);
	}
};

/**
 * Allows the accordion items to be clickable or not. If disabled, the label of
 * each accordion item will be grayed out.
 *
 * @param {boolean}	enabled			if <code>true</code>, enabled.
 */
DwtAccordion.prototype.setEnabled =
function(enabled) {
	if (enabled == this._enabled) { return; }

	this._enabled = enabled;

	for (var i in this._items) {
		var item = this._items[i];
		if (this._currentItemId != item.id) {
			item._setEnabled(enabled);
		}
	}
};

/**
 * Resizes the accordion. This override applies accordion size changes to accordion items as well.
 *
 * @param {number}	width		the new width for accordion
 * @param {number}	height		the new height for accordion
 */
DwtAccordion.prototype.resize =
function(width, height) {
	if (width) {
		// if width changed, resize all header items
		for (var i = 0; i < this._items.length; i++) {
			var id = this._items[i].id;
			var title = document.getElementById(this._htmlElId + "_title_" + id);
			var fudge = 30;

			var iconCell = document.getElementById(this._htmlElId + "_icon_" + id);
			if (iconCell && iconCell.className && iconCell.className != "") {
				fudge += 16; // the default width of an icon
			}
			Dwt.setSize(title, width - fudge);

		}
	}

	var newHeight;
	if (height) {
		var hdr = document.getElementById(this._htmlElId + "_header_" + this._currentItemId);
		if (hdr) {
			var hdrHeightSum = Dwt.getSize(hdr).y * this._getVisibleHeaderCount();
			newHeight = (height - hdrHeightSum); // force min. height of 100px?
		}
	}

	var body = document.getElementById(this._htmlElId + "_body_" + this._currentItemId);
	if (body) {
		Dwt.setSize(body, width, newHeight);
		if (body.firstChild) {
			Dwt.setSize(body.firstChild, width, newHeight);
		}
	}
};

DwtAccordion.prototype.setBounds =
function(x, y, width, height) {
	DwtComposite.prototype.setBounds.call(this, x, y, width, height);

	this.resize(width, height);
};

/**
 * Gets the expanded accordion item.
 * 
 * @return	{DwtAccordionItem}		the item
 */
DwtAccordion.prototype.getExpandedItem =
function() {
	return this._items[this._currentItemId || 0];
};

/**
 * Expands the accordion item with the given ID by making its body visible. The bodies of
 * other items are hidden.
 *
 * @param {number}	id	the accordion item ID
 * @param {boolean}	notify	if <code>true</code>, selection listeners are to be notified
 */
DwtAccordion.prototype.expandItem =
function(id, notify) {
	var selectedItem;
	for (var i = 0; i < this._items.length; i++) {
		var itemId = this._items[i].id;
		var header = document.getElementById(this._htmlElId + "_header_" + itemId);
		var body = document.getElementById(this._htmlElId + "_body_" + itemId);
		var cell = document.getElementById(this._htmlElId + "_cell_" + itemId);
		var status = document.getElementById(this._htmlElId + "_status_" + itemId);

		if (id == itemId) {
			Dwt.setVisible(body, true);
			header.className = "ZAccordionHeader ZWidget ZSelected";
			status.className = "ImgAccordionOpened";
			cell.style.height = "100%";
			this._currentItemId = id;
			selectedItem = this._items[i];
		} else {
			Dwt.setVisible(body, false);
			header.className = "ZAccordionHeader ZWidget";
			status.className = "ImgAccordionClosed";
			cell.style.height = "0px";
		}
	}

	if (selectedItem && notify && this.isListenerRegistered(DwtEvent.SELECTION)) {
		this.notifySelectionListeners(selectedItem);
	}
};

DwtAccordion.prototype.notifySelectionListeners =
function(selectedItem) {
	var selEv = DwtShell.selectionEvent;
	selEv.item = this;
	selEv.detail = selectedItem;
	this.notifyListeners(DwtEvent.SELECTION, selEv);
};

/**
 * Attaches the HTML content of the given control to the accordion item with
 * the given ID.
 *
 * @param {number}	id				the accordion item ID
 * @param {DwtControl}	contentObject		the control that contains this item's content
 */
DwtAccordion.prototype.setItemContent =
function(id, contentObject) {
	var aiBody = this.getBody(id);
	if (aiBody) {
		this._items[id].control = contentObject;
		contentObject.reparentHtmlElement(aiBody);
		var size = contentObject.getSize();
		this.resize(size.x, size.y);
	}
};

/**
 * Gets the <code><body></code> element of the accordion item with the given ID.
 *
 * @param {number}		id	the accordion item ID
 * @return	{Element}		the element
 */
DwtAccordion.prototype.getBody =
function(id) {
	return document.getElementById(this._htmlElId + "_body_" + id);
};

/**
 * Gets the <code><header></code> element of the accordion item with the given ID.
 *
 * @param {number}		id	the accordion item ID
 * @return	{Element}		the element
 */
DwtAccordion.prototype.getHeader =
function(id) {
	return document.getElementById(this._htmlElId + "_header_" + id);
};

/**
 * Shows or hides the accordion.
 *
 * @param {boolean}	show	if <code>true</code>, show the accordion; otherwise hide it
 */
DwtAccordion.prototype.show =
function(show) {
	var div = document.getElementById(this._htmlElId + "_div");
	if (div) {
		Dwt.setVisible(div, show);
	}
};

/**
 * Adds a listener to be notified when the button is pressed.
 *
 * @param {AjxListener}	listener	a listener
 */
DwtAccordion.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Shows or hides an alert (aka orange background) on the accordion header
 *
 * @param {number}	id	the accordion item ID
 * @param {boolean}	show	if <code>true</code>, show the alert
 */
DwtAccordion.prototype.showAlert =
function(id, show) {
	var header = document.getElementById(this._htmlElId + "_header_" + id);
	if (show) {
		Dwt.delClass(header, null, "ZAlert");
	} else {
		Dwt.delClass(header, "ZAlert", null);
	}
};

// Private Methods

/**
 * Creates the HTML skeleton for the accordion.
 * 
 * @private
 */
DwtAccordion.prototype._initialize =
function() {
	this._items = [];
	this.__ITEMCOUNT = 0;

	this.getHtmlElement().innerHTML = AjxTemplate.expand("dwt.Widgets#ZAccordion", {id: this._htmlElId});
	this._table = document.getElementById(this._htmlElId + "_accordion_table");

	this._setMouseEventHdlrs();
};

/**
 * Returns the number of accordion items which have visible headers.
 * 
 * @private
 */
DwtAccordion.prototype._getVisibleHeaderCount =
function() {
	var count = 0;
	for (var i = 0; i < this._items.length; i++) {
		var hdr = document.getElementById(this._htmlElId + "_header_" + this._items[i].id);
		if (hdr && Dwt.getVisible(hdr)) {
			count++;
		}
	}
	return count;
};


// Listeners

/**
 * When a header button is clicked, the item is expanded. Also, any listeners
 * are notified.
 *
 * @param {DwtAccordionItem}	item		the accordion item whose header was clicked
 * @param {DwtUiEvent}	ev		the click event
 * 
 * @private
 */
DwtAccordion.prototype._handleOnClickHeader =
function(item, ev) {
	if (!this._enabled) { return; }

	this.expandItem(item.id, true);
};

/**
 * When a header button is right-clicked, any listeners will be notified so a
 * context menu can be shown, for example.
 *
 * @param {DwtAccordionItem}	item		the accordion item whose header was clicked
 * @param {DwtUiEvent}	ev		the click event
 * 
 * @private
 */
DwtAccordion.prototype._handleOnRightClickHeader =
function(item, ev) {
	ev = ev || window.event;

	if (this.isListenerRegistered(DwtEvent.ONCONTEXTMENU)) {
		var selEv = DwtShell.selectionEvent;
		DwtUiEvent.copy(selEv, ev);
		selEv.item = this;
		selEv.detail = item;
		this.notifyListeners(DwtEvent.ONCONTEXTMENU, selEv);
	}
};

DwtAccordion.prototype._handleOnMouseoverHeader =
function(item, ev) {
	ev = ev || window.event;

	if (this.isListenerRegistered(DwtEvent.ONMOUSEOVER)) {
		var selEv = DwtShell.selectionEvent;
		DwtUiEvent.copy(selEv, ev);
		selEv.item = this;
		selEv.detail = item;
		this.notifyListeners(DwtEvent.ONMOUSEOVER, selEv);
	}
};

DwtAccordion.prototype._handleOnMouseoutHeader =
function(ev) {
	this.setToolTipContent(null);
};

/**
 * Handles a resize event.
 *
 * @param {DwtEvent}	ev	the control event
 * 
 * @private
 */
DwtAccordion.prototype._controlListener =
function(ev) {
	if (this.getScrollStyle() != Dwt.CLIP) { return; }

	var newWidth = (ev.oldWidth != ev.newWidth) ? ev.newWidth : null;
	var newHeight = (ev.oldHeight != ev.newHeight) ? ev.newHeight : null;

	if ((!newWidth && !newHeight) || ev.newWidth < 0 || ev.newHeight < 0) { return;	}

	this.resize(newWidth, newHeight);
};

/**
 * Creates an accordion item.
 * @class
 * This class represents a single expandable accordion item.
 *
 * @param {string}	id		the unique ID for this item
 * @param {string}	title		the text for the item header
 * @param {hash}	data		a hash of arbitrary data for this item
 * @param {DwtAccordion}	accordion	the owning accordion
 */
DwtAccordionItem = function(id, title, data, accordion) {
	this.id = id;
	this.title = title;
	this.data = data;
	this.accordion = accordion;
};

/**
 * Returns a string representation of the object.
 * 
 * @return		{string}		a string representation of the object
 */
DwtAccordionItem.prototype.toString =
function() {
	return "DwtAccordionItem";
};

/**
 * Sets the icon.
 * 
 * @param	{string}	icon		the icon
 */
DwtAccordionItem.prototype.setIcon =
function(icon) {
	var iconCell = document.getElementById(this.accordion._htmlElId + "_icon_" + this.id);
	if (iconCell) {
		iconCell.className = icon;
	}
};

/**
 * Gets the icon cell.
 * 
 * @return	{Element}		the cell
 */
DwtAccordionItem.prototype.getIconCell =
function() {
	return document.getElementById(this.accordion._htmlElId + "_icon_" + this.id);
};

/**
 * Sets the title.
 * 
 * @param	{string}	title		the title
 */
DwtAccordionItem.prototype.setTitle =
function(title) {
	var titleCell = document.getElementById(this.accordion._htmlElId + "_title_" + this.id);
	if (titleCell) {
		titleCell.innerHTML = title;
	}
};

/**
 * @private
 */
DwtAccordionItem.prototype._setEnabled =
function(enabled) {
	var titleCell = document.getElementById(this.accordion._htmlElId + "_title_" + this.id);
	if (titleCell) {
		if (enabled) {
			Dwt.delClass(titleCell, "ZDisabled");
		} else {
			Dwt.addClass(titleCell, "ZDisabled");
		}
	}

	var status = document.getElementById(this.accordion._htmlElId + "_status_" + this.id);
	if (status) {
		if (enabled) {
			Dwt.delClass(status, "ZDisabledImage ZDisabled");
		} else {
			Dwt.addClass(status, "ZDisabledImage ZDisabled");
		}
	}
};
