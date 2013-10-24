/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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
 * 
 * @private
 */
DwtPropertySheet = function(parent, className, posStyle, labelSide) {
	if (arguments.length == 0) return;
	className = className || "DwtPropertySheet";
	DwtComposite.call(this, {parent:parent, className:className, posStyle:posStyle});

	this._labelSide = labelSide || DwtPropertySheet.DEFAULT;

	this._propertyIdCount = 0;
	this._propertyList = [];
	this._propertyMap = {};
	
	this._tableEl = document.createElement("TABLE");
	// Cellspacing needed for IE in quirks mode
	this._tableEl.cellSpacing = 6;
	
	var element = this.getHtmlElement();
	element.appendChild(this._tableEl);
}

DwtPropertySheet.prototype = new DwtComposite;
DwtPropertySheet.prototype.constructor = DwtPropertySheet;

DwtPropertySheet.prototype.toString = 
function() {
	return "DwtPropertySheet";
}

// Constants

DwtPropertySheet.RIGHT = "right";
DwtPropertySheet.LEFT = "left";
DwtPropertySheet.DEFAULT = DwtPropertySheet.LEFT;

// Data

DwtPropertySheet.prototype._labelCssClass = "Label";
DwtPropertySheet.prototype._valueCssClass = "Field";

// Public methods

/**
 * Adds a property.
 *
 * @param label [string] The property label. The value is used to set the
 *				inner HTML of the property label cell.
 * @param value The property value. If the value is an instance of DwtControl
 *				the element returned by <code>getHtmlElement</code> is used;
 *				if the value is an instance of Element, it is added directly;
 * 				anything else is set as the inner HTML of the property value
 *				cell.
 * @param required [boolean] Determines if the property should be marked as
 *				   required. This is denoted by an asterisk next to the label.
 */
DwtPropertySheet.prototype.addProperty = function(label, value, required) {
	var index = this._tableEl.rows.length;

	var row = this._tableEl.insertRow(index);
	row.vAlign = this._vAlign ? this._vAlign : "top";

	if (this._labelSide == DwtPropertySheet.LEFT) {
		this._insertLabel(row, label, required);
		this._insertValue(row, value, required);
	}
	else {
		this._insertValue(row, value, required);
		this._insertLabel(row, label, required);
	}
	
	var id = this._propertyIdCount++;
	var property = { id: id, index: index, row: row, visible: true };
	this._propertyList.push(property);
	this._propertyMap[id] = property;
	return id;
};

DwtPropertySheet.prototype._insertLabel = function(row, label, required) {
	var labelCell = row.insertCell(-1);
	labelCell.className = this._labelCssClass;
	if (this._labelSide != DwtPropertySheet.LEFT) {
		labelCell.width = "100%";
		labelCell.style.textAlign = "left";
	}
	labelCell.innerHTML = label;
	if (required) {
		var asterisk = this._tableEl.ownerDocument.createElement("SUP");
		asterisk.innerHTML = "*";
		labelCell.insertBefore(asterisk, labelCell.firstChild);
	}
};

DwtPropertySheet.prototype._insertValue = function(row, value, required) {
	var valueCell = row.insertCell(-1);
	valueCell.className = this._valueCssClass;
	if (!value) {
		valueCell.innerHTML = "&nbsp;";
	} else if (value instanceof DwtControl) {
		valueCell.appendChild(value.getHtmlElement());
	}
	/**** NOTE: IE says Element is undefined
	else if (value instanceof Element) {
	/***/
	else if (value.nodeType == AjxUtil.ELEMENT_NODE) {
	/***/
		valueCell.appendChild(value);
	} else {
		valueCell.innerHTML = String(value);
	}
};

DwtPropertySheet.prototype.removeProperty = function(id) {
	var prop = this._propertyMap[id];
	if (prop) {
		var propIndex = prop.index;
		if (prop.visible) {
			var tableIndex = this.__getTableIndex(propIndex);
			var row = this._tableEl.rows[tableIndex];
			row.parentNode.removeChild(row);
		}
		prop.row = null;
		for (var i = propIndex + 1; i < this._propertyList.length; i++) {
			this._propertyList[i].index--;
		}
		this._propertyList.splice(propIndex, 1);
		delete this._propertyMap[id];
	}
};

DwtPropertySheet.prototype.setPropertyVisible = function(id, visible) {
	var prop = this._propertyMap[id];
	if (prop && prop.visible != visible) {
		prop.visible = visible;
		var propIndex = prop.index;
		if (visible) {
			var tableIndex = this.__getTableIndex(propIndex);
			var row = this._tableEl.insertRow(tableIndex);
			DwtPropertySheet.__moveChildNodes(prop.row, row);
			prop.row = row;
		}
		else {
			var row = prop.row;
			if (row && row.parentNode) {
				row.parentNode.removeChild(row);
			}
		}
	}
};

DwtPropertySheet.prototype.__getTableIndex = function(propIndex) {
	var tableIndex = 0;
	for (var i = 0; i < propIndex; i++) {
		var prop = this._propertyList[i];
		if (prop.visible) {
			tableIndex++;
		}
	}
	return tableIndex;
};

DwtPropertySheet.__moveChildNodes = function(srcParent, destParent) {
	if (srcParent === destParent) return;
	var srcChild = srcParent.firstChild;
	while (srcChild != null) {
		destParent.appendChild(srcChild);
		srcChild = srcParent.firstChild;
	}
};
