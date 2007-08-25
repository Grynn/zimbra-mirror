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


DwtGrouper = function(parent, className, posStyle) {
	if (arguments.length == 0) return;
	className = className || "DwtGrouper";
	posStyle = posStyle || DwtControl.STATIC_STYLE;
	DwtComposite.call(this, parent, null, posStyle);
	
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