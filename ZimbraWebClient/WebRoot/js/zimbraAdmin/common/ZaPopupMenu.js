/**
* @class ZaPopupMenu
* @constructor
* @param parent
* @param className
* @param dialog
* @param opList
*
* This widget class extends ZaPopupMenu. Similar to ZaToolBar, this class creates
* buttons form an array of ZaOperation objects
**/
function ZaPopupMenu(parent, className, dialog, opList) {
	if (arguments.length == 0) return;
	className = className || "ActionMenu";
	DwtMenu.call(this, parent, DwtMenu.POPUP_STYLE, className, null, dialog);
	this._menuItems = new Object();	
	if(opList) {
		var cnt = opList.length;
		for(var ix=0; ix < cnt; ix++) {
			this.createMenuItem(opList[ix].id, opList[ix].imageId, opList[ix].caption, null, true);
			this.addSelectionListener(opList[ix].id, opList[ix].listener);		
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
function(delay, x, y) {
	if (delay == null)
		delay = 0;
	if (x == null) 
		x = Dwt.DEFAULT;
	if (y == null)
		y = Dwt.DEFAULT;
	this.setLocation(x, y);
	DwtMenu.prototype.popup.call(this, delay);
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
	var mi = this._menuItems[menuItemId] = new DwtMenuItem(this, style, radioGroupId);
	if (imageId)
		mi.setImage(imageId);
	if (text)
		mi.setText(text);
	if (disImageId)
		mi.setDisabledImage(disImageId);
	mi.setEnabled(enabled !== false);
	return mi;
}

ZaPopupMenu.prototype.createSeparator =
function() {
	new DwtMenuItem(this, DwtMenuItem.SEPARATOR_STYLE);
}
