/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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


DwtText = function(parent, className, posStyle) {

	if (arguments.length == 0) return;
	className = className || "DwtText";
	DwtControl.call(this, {parent:parent, className:className, posStyle:posStyle});
}

DwtText.prototype = new DwtControl;
DwtText.prototype.constructor = DwtText;

DwtText.prototype.toString = 
function() {
	return "DwtText";
}

DwtText.prototype.setText =
function(text) {
	if (!this._textNode) {
		 this._textNode = document.createTextNode(text);
		 this.getHtmlElement().appendChild(this._textNode);
	} else {
		this._textNode.data = text;
	}
}

DwtText.prototype.getText =
function() {
	return this._textNode.data;
}

DwtText.prototype.getTextNode =
function() {
	return this._textNode;
}
