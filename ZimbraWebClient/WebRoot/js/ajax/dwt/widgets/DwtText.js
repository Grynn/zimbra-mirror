/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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
 * Creates a text control.
 * @constructor
 * @class
 * This class represents a container for a piece of text.
 * 
 * @author Ross Dargahi
 * 
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}      parent	the parent widget
 * @param {string}      className		CSS class
 * @param {constant}      posStyle		the positioning style (see {@link DwtControl})
 * @param {string}      id			an explicit ID to use for the control's HTML element
 * 
 * @extends		DwtControl
 */
DwtText = function(params) {
	if (arguments.length == 0) return;
	params = Dwt.getParams(arguments, DwtText.PARAMS);
	params.className = params.className || "DwtText";
	DwtControl.call(this, params);
};

DwtText.PARAMS = ["parent", "className", "posStyle"];

DwtText.prototype = new DwtControl;
DwtText.prototype.constructor = DwtText;

DwtText.prototype.toString =
function() {
	return "DwtText";
};

DwtText.prototype.getTabGroupMember = function() {
	return null;
};

/**
 * Sets the text.
 * 
 * @param	{string}	text		the text
 */
DwtText.prototype.setText =
function(text) {
	if (!this._textNode) {
		 this._textNode = document.createTextNode(text);
		 this.getHtmlElement().appendChild(this._textNode);
	} else {
		try { // IE mysteriously throws an error sometimes, but still does the right thing
			this._textNode.data = text;
		} catch (e) {}
	}
};

/**
 * Gets the text.
 * 
 * @return	{string}	the text
 */
DwtText.prototype.getText =
function() {
	return this._textNode ? this._textNode.data : "";
};

/**
 * Gets the text node.
 * 
 * @return	{Object}	the node
 */
DwtText.prototype.getTextNode =
function() {
	return this._textNode;
};
