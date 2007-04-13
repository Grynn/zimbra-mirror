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
/*
	// XXX: test code...
	var ids = [];
	ids.push(this.addItem({title:"foo"}));
	ids.push(this.addItem({title:"bar"}));
	this.showItem(ids[0]);
*/
};

DwtAccordion.prototype = new DwtComposite;
DwtAccordion.prototype.constructor = DwtAccordion;


// Public Methods

DwtAccordion.prototype.toString =
function() {
	return "DwtAccordion";
};

DwtAccordion.prototype.addItem =
function(params) {
	var itemNum = this.__ITEMCOUNT++;
	var subs = {id: this._htmlElId, itemNum: itemNum, title: params.title };

	// append new accordion item
	var row = this._table.insertRow(-1);
	var cell = row.insertCell(-1);
	cell.id = this._htmlElId + "_cell_" + itemNum;
	cell.className = "ZAccordionCell";
	cell.innerHTML = AjxTemplate.expand("ajax.dwt.templates.Widgets#ZAccordionItem", subs);

	// add onclick event handler to header DIV
	var headerDiv = document.getElementById(this._htmlElId + "_header_" + itemNum);
	headerDiv.onclick = AjxCallback.simpleClosure(this._handleOnClickHeader, this, itemNum);

	this._items.push(itemNum);

	return itemNum;
};

DwtAccordion.prototype.showItem =
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
			cell.style.height = "auto";
		}
	}
};

DwtAccordion.prototype.getBody =
function(id) {
	id = id || 0;

	for (var i = 0; i < this._items.length; i++) {
		if (this._items[i] == id) {
			return (document.getElementById(this._htmlElId + "_body_" + id));
		}
	}
	return null;
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


// Listeners

DwtAccordion.prototype._handleOnClickHeader =
function(itemNum, ev) {
	this.showItem(itemNum);

	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
		var selEv = DwtShell.selectionEvent;
		DwtUiEvent.copy(selEv, ev);
		selEv.item = this;
		selEv.detail = itemNum;
		this.notifyListeners(DwtEvent.SELECTION, selEv);
	}
};
