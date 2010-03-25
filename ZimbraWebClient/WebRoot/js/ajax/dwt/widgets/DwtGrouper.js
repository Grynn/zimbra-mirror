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
 * 
 * @private
 */
DwtGrouper = function(parent, className, posStyle) {
	if (arguments.length == 0) return;
	className = className || "DwtGrouper";
	posStyle = posStyle || DwtControl.STATIC_STYLE;
	DwtComposite.call(this, {parent:parent, posStyle:posStyle});
	
	this._labelEl = document.createElement("LEGEND");
	this._insetEl = document.createElement("DIV");
	this._borderEl = document.createElement("FIELDSET");
	this._borderEl.appendChild(this._labelEl);
	this._borderEl.appendChild(this._insetEl);
	
	var element = this.getHtmlElement();
	element.appendChild(this._borderEl);
}

DwtGrouper.prototype = new DwtComposite;
DwtGrouper.prototype.constructor = DwtGrouper;

// Data

DwtGrouper.prototype._borderEl;
DwtGrouper.prototype._labelEl;
DwtGrouper.prototype._insetEl;

// Public methods

DwtGrouper.prototype.setLabel = function(htmlContent) {
	Dwt.setVisible(this._labelEl, Boolean(htmlContent));
	// HACK: undo block display set by Dwt.setVisible
	this._labelEl.style.display = "";
	this._labelEl.innerHTML = htmlContent ? htmlContent : "";
};

DwtGrouper.prototype.setContent = function(htmlContent) {
	var element = this._insetEl;
	element.innerHTML = htmlContent;
};

DwtGrouper.prototype.setElement = function(htmlElement) {
	var element = this._insetEl;
	Dwt.removeChildren(element);
	element.appendChild(htmlElement);
};

DwtGrouper.prototype.setView = function(control) {
	this.setElement(control.getHtmlElement());
};

DwtGrouper.prototype.getInsetHtmlElement = function() {
	return this._insetEl;
};