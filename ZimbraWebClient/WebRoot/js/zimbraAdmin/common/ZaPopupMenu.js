/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
* @class ZaPopupMenu
* @constructor
* @param parent
* @param className
* @param dialog
* @param opList
*
* This widget class extends DwtMenu. Similar to ZaToolBar, this class creates
* buttons form an array of ZaOperation objects
**/
ZaPopupMenu = function(parent, className, dialog, opList, contextId, menuType, btnOrder) {
	if (arguments.length == 0) return;
	className = className || "ActionMenu";
        this._contextId = contextId;
	this._menuType = menuType;
	DwtMenu.call(this, parent, DwtMenu.POPUP_STYLE, className, null, dialog, ZaId.getMenuId(this._contextId,this._menuType));
	this._menuItems = new Object();	
	if(opList) {
		//var cnt = opList.length;

        var ix = 0;
        for(ix = 0; btnOrder && ix < btnOrder.length; ix++) {
            if(opList[btnOrder[ix]] instanceof ZaOperation) {
                if(opList[btnOrder[ix]].id == ZaOperation.NONE  || opList[btnOrder[ix]].id == ZaOperation.HELP)
                    continue;

                var style = (opList[btnOrder[ix]].id == ZaOperation.SEP) ? DwtMenuItem.SEPARATOR_STYLE : DwtMenuItem.NO_STYLE;
                this.createMenuItem(opList[btnOrder[ix]].id, opList[btnOrder[ix]].imageId, opList[btnOrder[ix]].caption, null, opList[btnOrder[ix]].enabled,style,null);
                this.addSelectionListener(opList[btnOrder[ix]].id, opList[btnOrder[ix]].listener);
            }
        }
        // add the remained buttons
        for(ix in opList) {
            if(!btnOrder || AjxUtil.indexOf(btnOrder,ix) < 0) {
                if(opList[ix] instanceof ZaOperation) {
                    if(opList[ix].id == ZaOperation.NONE  || opList[ix].id == ZaOperation.HELP)
                        continue;

                    var style = (opList[ix].id == ZaOperation.SEP) ? DwtMenuItem.SEPARATOR_STYLE : DwtMenuItem.NO_STYLE;
                    this.createMenuItem(opList[ix].id, opList[ix].imageId, opList[ix].caption, null, opList[ix].enabled,style,null);
                    this.addSelectionListener(opList[ix].id, opList[ix].listener);
                }
            }
        }
	}
}

ZaPopupMenu.prototype = new DwtMenu;
ZaPopupMenu.prototype.constructor = ZaPopupMenu;

ZaPopupMenu.prototype.toString = 
function() {
	return "ZaPopupMenu";
}

ZaPopupMenu.prototype.addSelectionListener =
function(menuItemId, listener) {
	this._menuItems[menuItemId].addSelectionListener(listener);
}

ZaPopupMenu.prototype.removeSelectionListener =
function(menuItemId, listener) {
	this._menuItems[menuItemId].removeSelectionListener(listener);
}

ZaPopupMenu.prototype.popup =
function(delay, x, y, kbGenerated) {
	if (delay == null)
		delay = 0;
	if (x == null) 
		x = Dwt.DEFAULT;
	if (y == null)
		y = Dwt.DEFAULT;
	DwtMenu.prototype.popup.call(this, delay, x, y, kbGenerated);
}

ZaPopupMenu.prototype.getMenuItem = 
function (menuItemId) {
	return this._menuItems[menuItemId];
}
/**
* Enables/disables menu items.
*
* @param ids		a list of menu item IDs
* @param enabled	whether to enable the menu items
*/
ZaPopupMenu.prototype.enable =
function(ids, enabled) {
	if (!(ids instanceof Array))
		ids = [ids];
	for (var i = 0; i < ids.length; i++)
		if (this._menuItems[ids[i]])
			this._menuItems[ids[i]].setEnabled(enabled);
}

ZaPopupMenu.prototype.enableAll =
function(enabled) {
	for (var i in this._menuItems)
		this._menuItems[i].setEnabled(enabled);
}

ZaPopupMenu.prototype.addMenuItem =
function(menuItemId, menuItem) {
	this._menuItems[menuItemId] = menuItem;
}

ZaPopupMenu.prototype.createMenuItem =
function(menuItemId, imageId, text, disImageId, enabled, style, radioGroupId) {
	var mi = this._menuItems[menuItemId] = new DwtMenuItem({
		parent:		this, 
		style:		style, 
		radioGroupId: 	radioGroupId, 
		id: 		ZaId.getMenuItemId(this._contextId, ZaOperation.getStringName(menuItemId))
	});
	if (imageId)
		mi.setImage(imageId);
	if (text)
		mi.setText(text);
	mi.setEnabled(enabled !== false);
	return mi;
}

ZaPopupMenu.prototype.createSeparator =
function() {
	new DwtMenuItem(this, DwtMenuItem.SEPARATOR_STYLE);
}
