/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaToolBar
* @contructor
* generic class that serves the purpose of creating any toolbar in the admin UI
* @param parent parent object
* @param opList array of ZaOperation objects
**/
function ZaToolBar(parent, opList, posStyle, className) {
	if (arguments.length == 0) return;
	className = className || "ZaToolBar";
	posStyle = posStyle || DwtControl.ABSOLUTE_STYLE;

	DwtToolBar.call(this, parent, className, posStyle);
	this._buttons = new Object();
		
	if(opList) {
		var cnt = opList.length;
		for(var ix=0; ix < cnt; ix++) {
			this._createButton(opList[ix].id, opList[ix].imageId, opList[ix].caption, opList[ix].disImageId, opList[ix].tt, true);
			this._createSeparator();
			this.addSelectionListener(opList[ix].id, opList[ix].listener);		
		}
	}
}

ZaToolBar.VIEW_DATA = "ZaToolBar.VIEW";

ZaToolBar.prototype = new DwtToolBar;
ZaToolBar.prototype.constructor = ZaToolBar;

ZaToolBar.prototype.toString = 
function() {
	return "ZaToolBar";
}

ZaToolBar.prototype.addSelectionListener =
function(buttonId, listener) {
	this._buttons[buttonId].addSelectionListener(listener);
}

ZaToolBar.prototype.removeSelectionListener =
function(buttonId, listener) {
	this._buttons[buttonId].removeSelectionListener(listener);
}

ZaToolBar.prototype.getButton =
function(buttonId) {
	return this._buttons[buttonId];
}

ZaToolBar.prototype.setData = 
function(buttonId, key, data) {
	this._buttons[buttonId].setData(key, data);
}

/**
* Enables/disables buttons.
*
* @param ids		a list of button IDs
* @param enabled	whether to enable the buttons
*/
ZaToolBar.prototype.enable =
function(ids, enabled) {
	if (!(ids instanceof Array))
		ids = [ids];
	for (var i = 0; i < ids.length; i++)
		if (this._buttons[ids[i]])
			this._buttons[ids[i]].setEnabled(enabled);
}

ZaToolBar.prototype.enableAll =
function(enabled) {
	for (var i in this._buttons)
		this._buttons[i].setEnabled(enabled);
}

ZaToolBar.prototype.computeHeight =
function(enabled) {
	var h = 0;
	for (var i in this._buttons)
		h = Math.max(h, this._buttons[i].getSize().y);
	return h;
}

ZaToolBar.prototype._createButton =
function(buttonId, imageId, text, disImageId, toolTip, enabled, style, align) {
	if (!style)
		style = "TBButton"
	var b = this._buttons[buttonId] = new DwtButton(this, align, style);
	if (imageId)
		b.setImage(imageId);
	if (text)
		b.setText(text);
	if (toolTip)
		b.setToolTipContent(toolTip);
	if (disImageId) 
		b.setDisabledImage(disImageId);
	b.setEnabled((enabled) ? true : false);
	b.setData("_buttonId", buttonId);
	return b;
}

ZaToolBar.prototype._createSeparator =
function() {
	new DwtControl(this, "vertSep");
}

ZaToolBar.prototype._buttonId =
function(button) {
	return button.getData("_buttonId");
}
