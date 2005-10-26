/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
 * License Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra AJAX Toolkit.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function DwtPropertySheet(parent, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtPropertySheet";
	DwtComposite.call(this, parent, className, positionType);

	this._properties = [];
	
	var document = this.getDocument();
	this._tableEl = document.createElement("TABLE");
	this._tableEl.border = 0;
	this._tableEl.cellSpacing = 3;
	this._tableEl.cellPadding = 0;
	
	var element = this.getHtmlElement();
	element.appendChild(this._tableEl);
}

DwtPropertySheet.prototype = new DwtComposite;
DwtPropertySheet.prototype.constructor = DwtPropertySheet;

DwtPropertySheet.prototype.toString = 
function() {
	return "DwtPropertySheet";
}

// Data

DwtPropertySheet.prototype._labelCssClass = "Label";
DwtPropertySheet.prototype._valueCssClass = "Field";

DwtPropertySheet.prototype._tableEl;

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
	row.vAlign = "top";
	
	var labelCell = row.insertCell(row.cells.length);
	labelCell.className = this._labelCssClass;
	labelCell.innerHTML = label;
	if (required) {
		var asterisk = this._tableEl.ownerDocument.createElement("SUP");
		asterisk.innerHTML = "*";
		labelCell.insertBefore(asterisk, labelCell.firstChild);
	}
	
	var valueCell = row.insertCell(row.cells.length);
	valueCell.className = this._valueCssClass;
	if (value instanceof DwtControl) {
		valueCell.appendChild(value.getHtmlElement());
	}
	/**** NOTE: IE says Element is undefined
	else if (value instanceof Element) {
	/***/
	else if (value.nodeType == AjxUtil.ELEMENT_NODE) {
	/***/
		valueCell.appendChild(value);
	}
	else {
		valueCell.innerHTML = String(value);
	}
	
	return index;
};

DwtPropertySheet.prototype.removeProperty = function(index) {
	var row = this._tableEl.rows[index];
	row.parentNode.removeChild(row);
};