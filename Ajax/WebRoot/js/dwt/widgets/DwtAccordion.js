/*
 * Copyright (C) 2006, The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
* Creates an Accordion widget.
* @constructor
* @class
* This class implements an accordion widget.
*
* @author Parag Shah
* @param parent		the parent widget
* @param className	CSS class
* @param posStyle	positioning style (absolute, static, or relative)
*/
function DwtAccordion(parent, className, posStyle) {

	if (arguments.length == 0) return;
	DwtComposite.call(this, parent, className, posStyle);

	this._initialize(className);
};

DwtAccordion.prototype = new DwtComposite;
DwtAccordion.prototype.constructor = DwtAccordion;


// Public Methods

DwtAccordion.prototype.toString =
function() {
	return "DwtAccordion";
};

DwtAccordion.prototype.addAccordionItem =
function(params) {

	if (!this.isListenerRegistered(DwtEvent.CONTROL)) {
		this.addControlListener(new AjxListener(this, this._controlListener));
	}

	var itemNum = this.__ITEMCOUNT++;
	var item = new DwtAccordianItem(itemNum, params.title, params.data);
	var subs = {id:this._htmlElId, itemNum:itemNum, title:params.title };

	// append new accordion item
	var row = this._table.insertRow(-1);
	var cell = row.insertCell(-1);
	cell.id = this._htmlElId + "_cell_" + itemNum;
	cell.className = "ZAccordionCell";
	cell.innerHTML = AjxTemplate.expand("ajax.dwt.templates.Widgets#ZAccordionItem", subs);

	// add onclick event handler to header DIV
	var headerDiv = document.getElementById(this._htmlElId + "_header_" + itemNum);
	headerDiv.onclick = AjxCallback.simpleClosure(this._handleOnClickHeader, this, item);

	this._items.push(itemNum);
	

	return item;
};

DwtAccordion.prototype.showAccordionItems =
function(show, itemId) {
	for (var i = 0; i < this._items.length; i++) {
		var currItemId = this._items[i];

		// if given itemId, check if its the one we're looking for
		if (itemId != null && currItemId != itemId)
			continue;

		var header = document.getElementById(this._htmlElId + "_header_" + currItemId);
		if (header) {
			Dwt.setVisible(header, show);
		}

		// again, if given itemId and we got this far, then we're done
		if (itemId != null) {
			return;
		}
	}
};

DwtAccordion.prototype.resize =
function(width, height) {
	if (width) {
		// if width changed, resize all header items
		for (var i = 0; i < this._items.length; i++) {
			var itemId = this._items[i];
			var title = document.getElementById(this._htmlElId + "_title_" + itemId);
			Dwt.setSize(title, width-30);
		}
	}

	var newHeight;
	if (height) {
		// just get the first header item as sample
		var hdr = document.getElementById(this._htmlElId + "_header_" + this._items[0]);
		var hdrHeightSum = Dwt.getSize(hdr).y * this._getVisibleHeaderCount();
		newHeight = Math.max(100, height-hdrHeightSum);							// force min. height of 100px?
	}

	// body height for each header item should be the same so just get the first one
	var body = document.getElementById(this._htmlElId + "_body_" + this._items[0]);
	Dwt.setSize(body, width, newHeight);
};

DwtAccordion.prototype.expandItem =
function(id) {
	for (var i = 0; i < this._items.length; i++) {
		var itemId = this._items[i];
		var header = document.getElementById(this._htmlElId + "_header_" + itemId);
		var body = document.getElementById(this._htmlElId + "_body_" + itemId);
		var cell = document.getElementById(this._htmlElId + "_cell_" + itemId);

		if (id == itemId)
		{
			Dwt.setVisible(body, true);
			header.className = "ZAccordionHeader ZWidget ZSelected";
			cell.style.height = "100%";
		}
		else
		{
			Dwt.setVisible(body, false);
			header.className = "ZAccordionHeader ZWidget";
			cell.style.height = "0px";
		}
	}
};

DwtAccordion.prototype.getBody =
function(id) {
	return document.getElementById(this._htmlElId + "_body_" + id);
};

DwtAccordion.prototype.show =
function(show) {
	var div = document.getElementById(this._htmlElId + "_div");
	if (div) Dwt.setVisible(div, show);
};

/**
* Adds a listener to be notified when the button is pressed.
*
* @param listener	a listener
*/
DwtAccordion.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};


// Private Methods

DwtAccordion.prototype._initialize =
function(className) {
	this._items = [];
	this.__ITEMCOUNT = 0;

	this.getHtmlElement().innerHTML = AjxTemplate.expand("ajax.dwt.templates.Widgets#ZAccordion", {id: this._htmlElId});
	this._table = document.getElementById(this._htmlElId + "_accordion_table");
};

DwtAccordion.prototype._getVisibleHeaderCount =
function() {
	var count = 0;
	for (var i = 0; i < this._items.length; i++) {
		var hdr = document.getElementById(this._htmlElId + "_header_" + this._items[i]);
		if (hdr && Dwt.getVisible(hdr))
			count++;
	}
	return count;
};


// Listeners

DwtAccordion.prototype._handleOnClickHeader =
function(item, ev) {
	ev = ev || window.event;

	this.expandItem(item.itemId);

	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
		var selEv = DwtShell.selectionEvent;
		DwtUiEvent.copy(selEv, ev);
		selEv.item = this;
		selEv.detail = item;
		this.notifyListeners(DwtEvent.SELECTION, selEv);
	}
};

DwtAccordion.prototype._controlListener =
function(ev) {
	if (this.getScrollStyle() != Dwt.CLIP)
		return;

	var newWidth = ev.oldWidth != ev.newWidth ? ev.newWidth : null;
	var newHeight = ev.oldHeight != ev.newHeight ? ev.newHeight : null;

	if ((!newWidth && !newHeight) ||
		ev.newWidth < 0 || ev.newHeight < 0)
	{
		return;
	}

	this.resize(newWidth, newHeight);
};

function DwtAccordianItem(itemId, title, data) {
	this.itemId = itemId;
	this.title = title;
	this.data = data;
};

