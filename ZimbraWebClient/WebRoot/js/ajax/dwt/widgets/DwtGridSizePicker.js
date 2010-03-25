/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Creates a grid size picker.
 * @class
 * This class represents a widget that provides the ability to select number of rows/cols similar to
 * Word's "insert table" popup menu.  Mainly designed for the "insert table"
 * feature in {@link ZmHtmlEditor}, this widget will usually be embedded in a DwtMenu
 * with style {@link DwtMenu.GENERIC_WIDGET_STYLE}.
 *
 * @author Mihai Bazon <mihai@zimbra.com>
 * 
 * @param	{DwtComposite}	parent 	the parent widget
 * @param	{string}	title		the title
 * 
 * @extends		DwtControl
 */
DwtGridSizePicker = function(parent, title) {
	if (arguments.length == 0)
		return;
	DwtControl.call(this, {parent:parent, className:"DwtGridSizePicker"});
	this._title = title;
	if (parent instanceof DwtMenu)
		parent.addPopdownListener(new AjxListener(this, this._reset));
	this._reset();
// 	if (AjxEnv.isIE && parent && parent instanceof DwtMenu) {
// 		var table = document.createElement("table");
// 		table.cellSpacing = table.cellPadding = table.border = 0;
// 		var tbody = document.createElement("tbody");
// 		table.appendChild(tbody);
// 		var tr = document.createElement("tr");
// 		tbody.appendChild(tr);
// 		var td = document.createElement("td");
// 		tr.appendChild(td);
// 		var div = this.getHtmlElement();
// 		var parent = div.parentNode;
// 		parent.insertBefore(table, div);
// 		td.appendChild(div);
// 	}
};
DwtGridSizePicker.prototype = new DwtControl;
DwtGridSizePicker.prototype.constructor = DwtGridSizePicker;

/**
 * Adds a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtGridSizePicker.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtGridSizePicker.prototype.removeSelectionListener =
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
};

DwtGridSizePicker.prototype._createGrid = function() {
	var el = this.getHtmlElement();
	var html = [ "<table cellpadding='0' border='0' cellspacing='0'>",
		     "<tr class='info'><td colspan='", this.maxCols, "'>", this._title, "</td></tr>" ];
	var row = [ "<tr class='grid'>" ];
	for (var i = 0; i < this.maxCols; ++i) {
		// row.push("<td unselectable='unselectable'>&nbsp;</td>");
		row.push("<td unselectable='unselectable'></td>");
	}
	row.push("</tr>");
	row = row.join("");
	for (var i = 0; i < this.maxRows; ++i)
		html.push(row);
	html.push("</table>");
	html = html.join("");
	el.innerHTML = html;
	el.onmouseover = DwtGridSizePicker._onMouseOver;
	el.onmouseup = DwtGridSizePicker._onSelect;
	// el.firstChild.onmouseout = AjxCallback.simpleClosure(this._onMouseOut, this);
};

DwtGridSizePicker.prototype._reset = function() {
	this._endRow = -1;
	this._endCol = -1;
	this.maxRows = 7;
	this.maxCols = 7;
	this._createGrid();
};

DwtGridSizePicker._onSelect = function(ev) {
	if (AjxEnv.isIE)
		ev = window.event;
	DwtControl.getTargetControl(ev)._onSelect(ev);
};

DwtGridSizePicker.prototype._onSelect = function(ev) {
	if (AjxEnv.isIE)
		ev = window.event;
	var dwtev = new DwtUiEvent();
	dwtev.setFromDhtmlEvent(ev);
	var target = dwtev.target;
	if (target.tagName.toLowerCase() == "td") {
		var row = target.parentNode.rowIndex;
		if (row > 0) {
			var col = target.cellIndex;
			var selev = DwtShell.selectionEvent;
			selev.item = this;
			selev.detail = { rows: row, cols: col + 1 };
			if (this.parent instanceof DwtMenu)
				DwtMenu.closeActiveMenu();
			this.notifyListeners(DwtEvent.SELECTION, selev);
		}
	}
};

DwtGridSizePicker.prototype._onMouseOut = function(ev) {
	if (AjxEnv.isIE)
		ev = window.event;
	var dwtev = new DwtUiEvent();
	dwtev.setFromDhtmlEvent(ev);
	var tgt = dwtev.target;
	var table = this.getHtmlElement().firstChild;
	try {
		while (tgt && tgt !== table)
			tgt = tgt.parentNode;
	} catch(ex) {
		tgt = null;
	};
	if (!tgt) {
		this._endRow = this._endCol = -1;
		this._update();
	}
};

DwtGridSizePicker._onMouseOver = function(ev) {
	if (AjxEnv.isIE)
		ev = window.event;
	DwtControl.getTargetControl(ev)._onMouseOver(ev);
};

DwtGridSizePicker.prototype._onMouseOver = function(ev) {
	var dwtev = new DwtUiEvent(true);
	dwtev.setFromDhtmlEvent(ev);
	var target = dwtev.target;
	if (this._updateTimeout)
		clearTimeout(this._updateTimeout);
	this._updateTimeout = setTimeout(
		AjxCallback.simpleClosure(
			this._updateOnTimeout, this, target),
		10);
	dwtev.setToDhtmlEvent(ev);
};

DwtGridSizePicker.prototype._addRow = function() {
	var table = this.getHtmlElement().firstChild;
	var tr = table.insertRow(-1);
	tr.className = "grid";
	for (var i = 0; i < this.maxCols; ++i) {
		var td = tr.insertCell(-1);
		td.unselectable = true;
		// td.innerHTML = "&nbsp;";
	}
	++this.maxRows;
};

DwtGridSizePicker.prototype._addCol = function() {
	var table = this.getHtmlElement().firstChild;
	var rows = table.rows;
	for (var i = 1; i <= this.maxRows; ++i) {
		var tr = rows[i];
		var td = tr.insertCell(-1);
		td.unselectable = true;
		// td.innerHTML = "&nbsp;";
	}
	++table.rows[0].cells[0].colSpan;
	++this.maxCols;
};

DwtGridSizePicker.prototype._updateOnTimeout = function(target) {
	var clear = true, row, col;
	if (target.tagName.toLowerCase() == "td") {
		row = target.parentNode.rowIndex;
		if (row > 0) {
			col = this._endCol = target.cellIndex;
			this._endRow = target.parentNode.rowIndex - 1;
			clear = false;
		}
	}
	if (clear)
		this._endRow = this._endCol = -1;
	this._update();
	if (!clear) {
		if (row == this.maxRows)
			this._addRow();
		if (col == this.maxCols - 1)
			this._addCol();
	}
};

DwtGridSizePicker.prototype._update = function() {
	var endRow = this._endRow;
	var endCol = this._endCol;
	var table = this.getHtmlElement().firstChild;
	var header = table.rows[0].cells[0];
	this._rows = endRow + 1;
	this._cols = endCol + 1;
	if (this._rows > 0 && this._cols > 0)
		header.innerHTML = this._rows + " x " + this._cols;
	else
		header.innerHTML = this._title;
	for (var i = 0; i < this.maxRows; ++i) {
		var row = table.rows[i+1];
		for (var j = 0; j < this.maxCols; ++j) {
			var cell = row.cells[j];
			var clear = (i > this._endRow) || (j > this._endCol);
			if (clear)
				Dwt.delClass(cell, "Hovered");
			else
				Dwt.addClass(cell, "Hovered");
		}
	}
};
