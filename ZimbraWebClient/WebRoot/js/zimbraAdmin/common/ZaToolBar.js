/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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

/**
* @class ZaToolBar
* @contructor
* generic class that serves the purpose of creating any toolbar in the admin UI
* @param parent parent object
* @param opList array of ZaOperation objects
**/
ZaToolBar = function(parent, opList,btnOrder,posStyle,className) {
	if (arguments.length == 0) return;
	className = className || "ZaToolBar";
	posStyle = posStyle || DwtControl.ABSOLUTE_STYLE;

	DwtToolBar.call(this, parent, className, posStyle);
	this._buttons = new Object();
	if(!AjxUtil.isEmpty(btnOrder) && opList) {
		var cnt = btnOrder.length;
		for(var ix = 0; ix < cnt; ix++) {
			if(opList[btnOrder[ix]] && opList[btnOrder[ix]] instanceof ZaOperation) {
				if(opList[btnOrder[ix]].id == ZaOperation.SEP) {
					this._createSeparator();
				} else if (opList[btnOrder[ix]].id == ZaOperation.NONE) {
					this.addFiller();
				} else if (opList[btnOrder[ix]].id == ZaOperation.LABEL) {
					this._createLabel(opList[btnOrder[ix]].labelId, opList[btnOrder[ix]].imageId, opList[btnOrder[ix]].caption, opList[btnOrder[ix]].disImageId, opList[btnOrder[ix]].tt, true, opList[btnOrder[ix]].className);
				} else {
					this._createButton(opList[btnOrder[ix]].id, opList[btnOrder[ix]].imageId, opList[btnOrder[ix]].caption, opList[btnOrder[ix]].disImageId, opList[btnOrder[ix]].tt, true, opList[btnOrder[ix]].className, opList[btnOrder[ix]].type, opList[btnOrder[ix]].menuOpList);
		
					this.addSelectionListener(opList[btnOrder[ix]].id, opList[btnOrder[ix]].listener);		
				}
			}
		}		
	} else if(opList) {
		for(var ix in opList) {
			if(opList[ix] instanceof ZaOperation) {
				if(opList[ix].id == ZaOperation.SEP) {
					this._createSeparator();
				} else if (opList[ix].id == ZaOperation.NONE) {
					this.addFiller();
				} else if (opList[ix].id == ZaOperation.LABEL) {
					this._createLabel(opList[ix].labelId, opList[ix].imageId, opList[ix].caption, opList[ix].disImageId, opList[ix].tt, true, opList[ix].className);
				} else {
					this._createButton(opList[ix].id, opList[ix].imageId, opList[ix].caption, opList[ix].disImageId, opList[ix].tt, true, opList[ix].className, opList[ix].type, opList[ix].menuOpList);
		
					this.addSelectionListener(opList[ix].id, opList[ix].listener);		
				}
			}
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
function(buttonId, imageId, text, disImageId, toolTip, enabled, className, type, menuOpList) {
	if (!className)
		className = "DwtToolbarButton"
	var b = this._buttons[buttonId] = new ZaToolBarButton(this, null, className);
	if (imageId)
		b.setImage(imageId);
	if (text)
		b.setText(text);
	if (toolTip)
		b.setToolTipContent(toolTip);
	b.setEnabled((enabled) ? true : false);
	b.setData("_buttonId", buttonId);

	if (type == ZaOperation.TYPE_MENU) {
		var menu = new ZaPopupMenu(b, null,null, menuOpList);
		b.setMenu(menu);
	}
	return b;
}


ZaToolBar.prototype._createLabel =
function(buttonId, imageId, text, disImageId, toolTip, enabled, className, style) {

	var b = this._buttons[buttonId] = new ZaToolBarLabel(this, null, className);
	if (imageId)
		b.setImage(imageId);
	if (text)
		b.setText(text);
	if (toolTip)
		b.setToolTipContent(toolTip);
	b.setEnabled((enabled) ? true : false);
	b.setData("_buttonId", buttonId);

	return b;
}
ZaToolBar.prototype._createSeparator =
function() {
	var ctrl = new DwtControl(this);
	var html = ZaToolBar.getSeparatorHtml ();
	ctrl.setContent(html);
}

ZaToolBar.getSeparatorHtml =
function () {
	//return "<table><tr><td class=\"ImgAppToolbarSectionSep\" height=20px width=3px> </td></tr></table>";
	return "<div class=\"vertSep\"/>";
}

ZaToolBar.prototype._buttonId =
function(button) {
	return button.getData("_buttonId");
}
