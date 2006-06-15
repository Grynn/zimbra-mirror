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


function DwtToolBar(parent, className, posStyle, cellSpacing, cellPadding, style) {

	if (arguments.length == 0) return;
	className = className ? className : "DwtToolBar";
	DwtComposite.call(this, parent, className, posStyle);
	
	this._style = style ? style : DwtToolBar.HORIZ_STYLE;
	this._table = document.createElement("table");
	this._table.border = 0;
	this._table.cellPadding = cellPadding ? cellPadding : 0;
	this._table.cellSpacing = cellSpacing ? cellSpacing : 0;
	this.getHtmlElement().appendChild(this._table);
	this._table.backgroundColor = DwtCssStyle.getProperty(this.parent.getHtmlElement(), "background-color");

	this._numFillers = 0;
	this._curFocusIndex = 0;
};

DwtToolBar.prototype = new DwtComposite;
DwtToolBar.prototype.constructor = DwtToolBar;

DwtToolBar.HORIZ_STYLE	= 1;
DwtToolBar.VERT_STYLE	= 2;

DwtToolBar.ELEMENT		= 1;
DwtToolBar.SPACER		= 2;
DwtToolBar.SEPARATOR	= 3;
DwtToolBar.FILLER		= 4;

DwtToolBar.DEFAULT_SPACER = 10;

DwtToolBar.prototype.toString = 
function() {
	return "DwtToolBar";
};

// bug fix #33 - IE defines box model differently
DwtToolBar.prototype.__itemPaddingRight = AjxEnv.isIE ? "4px" : "0px";

DwtToolBar.prototype.getItem =
function(index) {
	return this._children.get(index);
};

DwtToolBar.prototype.getItemCount =
function() {
	return this._children.size();
};

DwtToolBar.prototype.getItems =
function() {
	return this._children.toArray();
};

DwtToolBar.prototype.addSpacer =
function(size, index) {
	var el = this._createSpacerElement();
	var dimension = this._style == DwtToolBar.HORIZ_STYLE ? "width" : "height";
	el.style[dimension] = size ? size : DwtToolBar.DEFAULT_SPACER;

	this._addItem(DwtToolBar.SPACER, el, index);
	return el;
};

DwtToolBar.prototype._createSpacerElement = 
function() {
	return document.createElement("div");
};

DwtToolBar.prototype.addSeparator =
function(className, index) {
	var el = this._createSeparatorElement();
	el.className = className;
	this._addItem(DwtToolBar.SEPARATOR, el, index);
	return el;
};

DwtToolBar.prototype._createSeparatorElement = DwtToolBar.prototype._createSpacerElement;
DwtToolBar.prototype._createFillerElement = DwtToolBar.prototype._createSpacerElement;

DwtToolBar.prototype.addFiller =
function(className, index) {
	var el = this._createFillerElement();
	el.className = className ? className : this._defaultFillClass;
	this._addItem(DwtToolBar.FILLER, el, index);
	return el;
};

DwtToolBar.prototype.addChild =
function(child, index) {
	this._children.add(child, index);
	var htmlEl = child._removedEl ? child._removedEl : child.getHtmlElement();
	this._addItem(DwtToolBar.ELEMENT, htmlEl, index);
};

DwtToolBar.prototype._addItem =
function(type, element, index) {

	var row, col;
	if (this._style == DwtToolBar.HORIZ_STYLE) {
		row = (this._table.rows.length != 0) ? this._table.rows[0]: this._table.insertRow(0);
		row.align = "center";
		row.vAlign = "middle";
		
		var cellIndex = index ? index : row.cells.length;
		col = row.insertCell(cellIndex);
		col.align = "center";
		col.vAlign = "middle";
		col.noWrap = true;
		// bug fix #33 - IE defines box model differently
		col.style.paddingRight = this.__itemPaddingRight;

		if (type == DwtToolBar.FILLER) {
			this._numFillers++;
			var perc = Math.floor(100 / this._numFillers);
			col.style.width = [perc, "%"].join("");
		} else {
			col.style.width = "1";
		}
			
		col.appendChild(element);
	} else {
		var rowIndex = index || -1;
		row = this._table.insertRow(rowIndex);
		row.align = "center";
		row.vAlign = "middle";
		
		col = row.insertCell(0);
		col.align = "center";
		col.vAlign = "middle";
		col.noWrap = true;

		if (type == DwtToolBar.FILLER) {
			this._numFillers++;
			var perc = Math.floor(100 / this._numFillers);
			col.style.height = [perc, "%"].join("");
		}

		col.appendChild(element);
	}
};

// transfer focus to the current item
DwtToolBar.prototype._focus =
function() {
	var item = this._getFocusItem(this._curFocusIndex);
	if (item) {
		item._hasFocus = true;	// so that focus class is set
		item._focus();
	}
};

// blur the current item
DwtToolBar.prototype._blur =
function() {
	var item = this._getFocusItem(this._curFocusIndex);
	if (item) {
		item._hasFocus = false;
		item._blur();
	}
};

// for now, don't include sub-toolbars as focus items
DwtToolBar.prototype._getFocusItem =
function(index) {
	var item = this.getItem(index);
	return (item && !(item instanceof DwtToolBar)) ? item : null;
};

DwtToolBar.prototype.getKeyMapName = 
function() {
	return "DwtToolBar";
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
				// make sure we can actually transfer focus
				var oldItem = this._getFocusItem(this._curFocusIndex);
				var newItem = this._getFocusItem(this._curFocusIndex - 1);
				if (oldItem && newItem) {
					this._blur();
					this._curFocusIndex--;
					this._focus();
				}
			}
			break;
			
		case DwtKeyMap.NEXT:
			if (this._curFocusIndex < (numItems - 1)) {
				// make sure we can actually transfer focus
				var oldItem = this._getFocusItem(this._curFocusIndex);
				var newItem = this._getFocusItem(this._curFocusIndex + 1);
				if (oldItem && newItem) {
					this._blur();
					this._curFocusIndex++;
					this._focus();
				}
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
