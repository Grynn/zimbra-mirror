/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
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
 * Creates an empty accordion widget.
 * @constructor
 * @class
 * This class implements an accordion widget, which is a stack of expandable
 * accordion headers. Clicking on an accordion header's button expands it in
 * place.
 *
 * @author Parag Shah
 *
 * @param parent	[DwtControl]	the parent widget
 * @param className	[string]		CSS class
 * @param posStyle	[constant]		positioning style (absolute, static, or relative)
 */
DwtAccordion = function(parent, className, posStyle) {

	if (arguments.length == 0) return;
	DwtComposite.call(this, parent, className, (posStyle || Dwt.ABSOLUTE_STYLE));

	this._initialize(className);
};

DwtAccordion.prototype = new DwtComposite;
DwtAccordion.prototype.constructor = DwtAccordion;


// Public Methods

DwtAccordion.prototype.toString =
function() {
	return "DwtAccordion";
};

/**
 * Adds an item to the accordion, in the form of a table row.
 *
 * @param params	[hash]			hash of params:
 *        title		[string]		text for accordion header
 *        data		[hash]			item data
 *        icon		[string]*		icon
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
	headerDiv.onclick = AjxCallback.simpleClosure(this._handleOnClickHeader, this, item);
	headerDiv.oncontextmenu = AjxCallback.simpleClosure(this._handleOnRightClickHeader, this, item);
	headerDiv.onmouseover = AjxCallback.simpleClosure(this._handleOnMouseoverHeader, this, item);
	headerDiv.onmouseout = AjxCallback.simpleClosure(this._handleOnMouseoutHeader, this, item);

	this._items.push(item);

	return item;
};

/**
 * Returns the ordered list of accordion items.
 */
DwtAccordion.prototype.getItems =
function() {
	return this._items;
};

/**
 * Returns the accordion item with the given ID.
 *
 * @param id	[int]	accordion item ID
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
 * @param id	[int]	accordion item ID
 */
DwtAccordion.prototype.showAccordionItem =
function(id) {
	var header = document.getElementById(this._htmlElId + "_header_" + id);
	if (header) {
		Dwt.setVisible(header, true);
	}
};

/**
 * This override applies accordion size changes to accordion items as well.
 *
 * @param width		[int]	new width for accordion
 * @param height	[int]	new height for accordion
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
		Dwt.setSize(body.firstChild, width, newHeight);
	}
};

DwtAccordion.prototype.setBounds =
function(x, y, width, height) {
	DwtComposite.prototype.setBounds.call(this, x, y, width, height);

	this.resize(width, height);
};

/**
 * Expands the accordion item with the given ID by making its body visible. The bodies of
 * other items are hidden.
 *
 * @param id	[int]	accordion item ID
 * @param notify	[boolean]	True if selection listeners are to be notified.
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
		var selEv = DwtShell.selectionEvent;
		selEv.item = this;
		selEv.detail = selectedItem;
		this.notifyListeners(DwtEvent.SELECTION, selEv);
	}
};

/**
 * Attaches the HTML content of the given control to the accordion item with
 * the given ID.
 *
 * @param id				[int]			accordion item ID
 * @param contentObject		[DwtControl]	control that contains this item's content
 */
DwtAccordion.prototype.setItemContent =
function(id, contentObject) {
	var aiBody = this.getBody(id);
	if (aiBody) {
		contentObject.reparentHtmlElement(aiBody);
		var size = contentObject.getSize();
		this.resize(size.x, size.y);
	}
};

/**
 * Returns the BODY element of the accordion item with the given ID.
 *
 * @param id	[int]	accordion item ID
 */
DwtAccordion.prototype.getBody =
function(id) {
	return document.getElementById(this._htmlElId + "_body_" + id);
};

/**
 * Shows or hides the accordion.
 *
 * @param show	[boolean]	if true, show the accordion, otherwise hide it
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
 * @param listener	[AjxListener]	a listener
 */
DwtAccordion.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};

// Private Methods

/**
 * Creates the HTML skeleton for the accordion.
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
 * @param item		[DwtAccordionItem]		the accordion item whose header was clicked
 * @param ev		[DwtUiEvent]			the click event
 */
DwtAccordion.prototype._handleOnClickHeader =
function(item, ev) {
	ev = ev || window.event;

	this.expandItem(item.id);

	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
		var selEv = DwtShell.selectionEvent;
		DwtUiEvent.copy(selEv, ev);
		selEv.item = this;
		selEv.detail = item;
		this.notifyListeners(DwtEvent.SELECTION, selEv);
	}
};

/**
 * When a header button is right-clicked, any listeners will be notified so a
 * context menu can be shown, for example.
 *
 * @param item		[DwtAccordionItem]		the accordion item whose header was clicked
 * @param ev		[DwtUiEvent]			the click event
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
	this.setToolTipContent("");
};

/**
 * Handles a resize event.
 *
 * @param ev	[DwtEvent]		the control event
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
 * This class represents a single expandable accordion item.
 *
 * @param id		[string]		unique ID for this item
 * @param title		[string]		text for the item header
 * @param data		[hash]			arbitrary data for this item
 * @param accordion	[DwtAccordion]	owning accordion
 */
DwtAccordionItem = function(id, title, data, accordion) {
	this.id = id;
	this.title = title;
	this.data = data;
	this.accordion = accordion;
};

DwtAccordionItem.prototype.toString =
function() {
	return "DwtAccordionItem";
};

DwtAccordionItem.prototype.setIcon =
function(icon) {
	var iconCell = document.getElementById(this.accordion._htmlElId + "_icon_" + this.id);
	if (iconCell) {
		iconCell.className = icon;
	}
};
