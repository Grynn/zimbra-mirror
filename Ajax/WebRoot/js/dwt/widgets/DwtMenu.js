/*
* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1
*
* The contents of this file are subject to the Mozilla Public
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


/**
* Creates a menu object to menu items can be added. Menus can be created in various styles as
* follows:
*
* DwtMenu.BAR_STYLE - Traditional menu bar.
* DwtMenu.POPUP_STYLE - Popup menu
* DwtMenu.DROPDOWN_STYLE - Used when a menu is a drop down (e.g. parent is a button or another menu item);
* DwtMenu.COLOR_PICKER_STYLE - Menu is hosting a single color picker;
* DwtMenu.CALENDAR_PICKER_STYLE - Menu is hostng a single calendar; 
*
* @constructor
* @class
*
* @author Ross Dargahi
* @param parent		the parent widget
* @param style 		menu's style
* @param className	a CSS class
* @param posStyle	positioning style
* @param dialog 	Dialog that this menu is a part of (if any)
*/
function DwtMenu(parent, style, className, posStyle, dialog) {

	if (arguments.length == 0) return;
	if (parent) {
		if (parent instanceof DwtMenuItem || parent instanceof DwtButton)
			this._style = DwtMenu.DROPDOWN_STYLE;
		else
			this._style = style || DwtMenu.POPUP_STYLE;
		if (!posStyle) 
			posStyle = (this._style == DwtMenu.BAR_STYLE) ? DwtControl.STATIC_STYLE : DwtControl.ABSOLUTE_STYLE; 
	}
	className = className || "DwtMenu";

	// Hack to force us to hang off of the shell for positioning.
	DwtComposite.call(this, (parent instanceof DwtShell) ? parent : parent.shell, className, posStyle);
	this.parent = parent;
	if (parent == null) 
		return;
	this._dialog = dialog;
	
	var htmlElement = this.getHtmlElement();
	this._menuListeners = new AjxVector();
	
	// Don't need to create table for color picker and calendar picker styles
	if (this._style != DwtMenu.COLOR_PICKER_STYLE && this._style != DwtMenu.CALENDAR_PICKER_STYLE) {
		this._table = this.getDocument().createElement("table");
		this._table.border = 0;
		this._table.cellPadding = 0;
		this._table.cellSpacing = 0;
		htmlElement.appendChild(this._table);
		this._table.backgroundColor = DwtCssStyle.getProperty(htmlElement, "background-color");
	}

	if (style != DwtMenu.BAR_STYLE) {
		this.setZIndex(Dwt.Z_HIDDEN);
 		this._isPoppedup = false;		
	} else {
		DwtMenu._activeMenuIds.add(htmlElement.id);
		this._isPoppedup = true;
 	}
	this._popdownAction = new AjxTimedAction();
	this._popdownAction.method = DwtMenu.prototype._doPopdown;
	this._popdownAction.obj = this;
	this._popdownActionId = -1;
	this._popupAction = new AjxTimedAction();
	this._popupAction.method = DwtMenu.prototype._doPopup;
	this._popupAction.obj = this;
	this._popupActionId = -1;
 	if ((this.parent instanceof DwtMenuItem && this.parent.parent._style == DwtMenu.BAR_STYLE)
		|| !(this.parent instanceof DwtMenuItem)){
		this._outsideListener = new AjxListener(this, this._outsideMouseDownListener);
	}

	this._numCheckedStyleItems = 0;
}

DwtMenu.prototype = new DwtComposite;
DwtMenu.prototype.constructor = DwtMenu;

DwtMenu.prototype.toString = 
function() {
	return "DwtMenu";
}

DwtMenu.BAR_STYLE = 1;
DwtMenu.POPUP_STYLE = 2;
DwtMenu.DROPDOWN_STYLE = 3;
DwtMenu.COLOR_PICKER_STYLE =  4;
DwtMenu.CALENDAR_PICKER_STYLE = 5;

DwtMenu._activeMenuUp = false;
DwtMenu._activeMenuIds = new AjxVector();

DwtMenu.prototype.addMenuListener = 
function(listener) {
  if (!this._menuListeners.contains(listener)) {
  	this._menuListeners.add(listener);
  }     	
}

DwtMenu.prototype.removeMenuListener = 
function(listener) {
  this._menuListeners.remove(listener);     	
}

DwtMenu.prototype.addPopdownListener = 
function(listener) {
	this.addListener(DwtEvent.POPDOWN, listener);
}

DwtMenu.prototype.removePopdownListener = 
function(listener) {
	this.removeListener(DwtEvent.POPDOWN, listener);
}

DwtMenu.prototype.getItem =
function(index) {
	return this._children.get(index);
}

DwtMenu.prototype.getItemById =
function(key, id) {
	var items = this.getItems();
    for (var i = 0; i < items.length; i++) {
	    var itemId = items[i].getData(key);
		if (itemId == id)
			return items[i];
	}
	return null;
}

DwtMenu.prototype.getItemCount =
function() {
	return this._children.size();
}

DwtMenu.prototype.getItems =
function() {
	return this._children.getArray();
}

DwtMenu.prototype.getSelectedItem =
function(style) {
	var a = this._children.getArray();
	for (var i = 0; i < a.length; i++) {
		var mi = a[i];
		if ((!style || (mi._style == style)) && mi.getChecked())
			return mi;
	}
	return null;
}

DwtMenu.prototype.isPoppedup =
function() {
	return this._isPoppedup;
}

DwtMenu.prototype.popup =
function(msec, x, y) {
	if (this._style == DwtMenu.BAR_STYLE) 
		return;
	if (this._popdownActionId != -1) {
		AjxTimedAction.cancelAction(this._popdownActionId);
		this._popdownActionId = -1;
	} else {
		if (this._isPoppedup || (this._popupActionId != -1 && msec && msec > 0)) {
			return;
		} else if (this._popupActionId != -1){
			AjxTimedAction.cancelAction(this._popupActionId);
			this._popupActionId = -1;
		}
		if (!msec) {
			this._doPopup({x : x, y : y});
		} else {
			this._popupAction.params.add(x);
			this._popupAction.params.add(y);
			this._popupActionId = AjxTimedAction.scheduleAction(this._popupAction, msec);
		}
	}
}

DwtMenu.prototype.popdown =
function(msec) {
	if (this._style == DwtMenu.BAR_STYLE) return;

	if (this._popupActionId != -1) {
		AjxTimedAction.cancelAction(this._popupActionId);	
		this._popupActionId = -1;
	} else {
		if (!this._isPoppedup || this._popdownActionId != -1) 
			return;
		if (msec == null || msec == 0)
			this._doPopdown();
		else
			this._popdownActionId = AjxTimedAction.scheduleAction(this._popdownAction, msec);
	}
}

/**
 * This allows the caller to associate one object with the menu. Association
 * means, for events, treat the menu, and this object as one. If I click on
 * elements pertaining to this object, we will think of them as part of the
 * menu. 
 * @see _outsideMouseListener.
 */
DwtMenu.prototype.setAssociatedObj =
function (dwtObj) {
	this._associatedObj = dwtObj;
};

DwtMenu.prototype.setAssociatedElementId =
function (id){
	this._associatedElId = id;
}

/*
* Checks a menu item (the menu must be radio or checkbox style). The menu item
* is identified through the given field/value pair.
*
* @param field		a key for menu item data
* @param value		value for the data of the menu item to check
*/
DwtMenu.prototype.checkItem =
function(field, value, skipNotify) {
    var items = this._children.getArray();
    for (var i = 0; i < items.length; i++) {
    	var item = items[i];
		if (item._style != DwtMenuItem.CHECK_STYLE && item._style != DwtMenuItem.RADIO_STYLE)
			continue;
		var val = item.getData(field);
     	if (val == value)
    		item.setChecked(true, skipNotify);
    }
}

DwtMenu.prototype.setSelectedItem =
function (index){
	var mi = this._children.get(index);
	mi.setSelectedStyle();
	this._externallySelected = mi;
};

DwtMenu.prototype.clearExternallySelectedItems =
function () {
	if (this._externallySelected != null){
		this._externallySelected._deselect();
		this._externallySelected = null;
	}
};

DwtMenu.prototype._removeChild =
function(child) {
	if (this._style == DwtMenu.BAR_STYLE) {
		var cell = child.getHtmlElement().parentNode;
		this._table.rows[0].deleteCell(Dwt.getCellIndex(cell));
	} else {
		/* If the item we are removing is check/radio style, and it is the last such item in the menu, then we 
		 * must instruct our other children to delete a "checked column" to ensure that things line up */
		if (child._style == DwtMenuItem.CHECK_STYLE || child._style == DwtMenuItem.RADIO_STYLE) {
			if (this._numCheckedStyleItems == 1) {
				var sz = this._children.size();
				if (sz > 0) {
					var a = this._children.getArray();
					for (var i = 0; i < sz; i++) {
						if (a[i]._style != DwtMenuItem.CHECK_STYLE && a[i]._style != DwtMenuItem.RADIO_STYLE)
							a[i]._checkedItemsRemoved();
					}
				}
			}
			this._numCheckedStyleItems--;
		}
		this._table.deleteRow(child.getHtmlElement().parentNode.parentNode.rowIndex);
	}
	this._children.remove(child);
}

// Override DwtComposite._addChild to do nothing
DwtMenu.prototype._addChild = function(child) {
	// Color pickers and calendars are not menu aware so we have to deal with
	// them acordingly
	if ((child instanceof DwtColorPicker) || (child instanceof DwtCalendar))
		this._addItem(child);
}

DwtMenu.prototype._addItem =
function(item, index) {
	if (this._style == DwtMenu.COLOR_PICKER_STYLE || this._style == DwtMenu.CALENDAR_PICKER_STYLE) {
		// Item better be a color picker & we better not have any children
		if (this._children.size() > 0 || !(item.parent instanceof DwtMenu) 
			|| ((this._style == DwtMenu.COLOR_PICKER_STYLE && !(item instanceof DwtColorPicker))
			    || (this._style == DwtMenu.CALENDAR_PICKER_STYLE && !(item instanceof DwtCalendar))))
			new DwtException("Invalid child", DwtException.INVALID_PARAM, "DwtMenu.prototype._addItem");
		this._children.add(item);
		item.reparentHtmlElement(this.getHtmlElement());
	} else {
		var row;
		var col;
		if (this._style == DwtMenu.BAR_STYLE){
			var rows = this._table.rows;
			row = (rows.length != 0) ? rows[0]: this._table.insertRow(0);
			if (index == null || index > row.cells.length)
				index = rows.cells.length;
			col = row.insertCell(index);
			col.align = "center";
			col.vAlign = "middle";
			var spc = row.insertCell(row.cells.length);
			spc.nowrap = true;
			spc.width = "7px"
		} else {
			/* If the item we are adding is check/radio style, and it is the first such item in the menu, then we 
			 * must instruct our other children to add a "checked column" to ensure that things line up */
			if (item._style == DwtMenuItem.CHECK_STYLE || item._style == DwtMenuItem.RADIO_STYLE) { 
				if (this._numCheckedStyleItems == 0) {
					var sz = this._children.size();
					if (sz > 0) {
						var a = this._children.getArray();
						for (var i = 0; i < sz; i++) {
							if (a[i]._style != DwtMenuItem.CHECK_STYLE && a[i]._style != DwtMenuItem.RADIO_STYLE)
								a[i]._createCheckedStyle();
						}
					}
				}
				this._numCheckedStyleItems++;
			}
			if (index == null || index > this._table.rows.length)
				index = -1;
			var row = this._table.insertRow(index);
			col = row.insertCell(0);
		}
		col.noWrap = true;
		col.appendChild(item.getHtmlElement());
		this._children.add(item, index);
	}
}

DwtMenu.prototype._radioItemSelected =
function(child, skipNotify) {
	var radioGroupId = child._radioGroupId;
	var sz = this._children.size();
	var a = this._children.getArray();
	for (var i = 0; i < sz; i++) {
		if (a[i] != child && a[i]._style == DwtMenuItem.RADIO_STYLE && a[i]._radioGroupId == radioGroupId
			&& a[i]._itemChecked) {
			a[i].setChecked(false, skipNotify);
			break;
		}
	}
}

DwtMenu.prototype._menuHasCheckedItems =
function() {
	return (this._numCheckedStyleItems > 0) ? true : false;
}

DwtMenu.prototype._doPopup =
function(args) {
	var pb = this.parent.getBounds();
	var ws = this.shell.getSize();
	var s = this.getSize();
	var x;
	var y;
	var vBorder;
	var hBorder;
	if (this.parent instanceof DwtMenuItem) {
		var pp = this.parent.parent;
		var ppHtmlElement = pp.getHtmlElement();
		if (pp._style == DwtMenu.BAR_STYLE) {
   			vBorder = (ppHtmlElement.style.borderLeftWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderLeftWidth);
			x = pb.x + vBorder;
			hBorder = (ppHtmlElement.style.borderTopWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderTopWidth);
			hBorder += (ppHtmlElement.style.borderBottomWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderBottonWidth);
			y = pb.y + pb.height + hBorder;		
			x = ((x + s.x) >= ws.x) ? x - (x + s.x - ws.x): x;
			y = ((y + s.y) >= ws.y) ? y - (y + s.y - ws.y) : y;
		} else { // Drop Down
			vBorder = (ppHtmlElement.style.borderLeftWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderLeftWidth);
			vBorder += (ppHtmlElement.style.borderRightWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderRightWidth);
			x = pb.x + pb.width + vBorder;
			hBorder = (ppHtmlElement.style.borderTopWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderTopWidth);
			y = pb.y + hBorder;
			x = ((x + s.x) >= ws.x) ? pb.x - s.x - vBorder: x;
			y = ((y + s.y) >= ws.y) ? y - (y + s.y - ws.y) : y;
		}
		this.setLocation(x, y);
	} else if (this.parent instanceof DwtSelect) {
		var p = this.parent;
		var pHtmlElement = p.getHtmlElement();
		// since buttons are often absolutely positioned, and menus aren't, we need x,y relative to window
		var ptw = Dwt.toWindow(pHtmlElement, 0, 0);
 		vBorder = (pHtmlElement.style.borderLeftWidth == "") ? 0 : parseInt(pHtmlElement.style.borderLeftWidth);
		x = pb.x + vBorder;
		hBorder = (pHtmlElement.style.borderTopWidth == "") ? 0 : parseInt(pHtmlElement.style.borderTopWidth);
		hBorder += (pHtmlElement.style.borderBottomWidth == "") ? 0 : parseInt(pHtmlElement.style.borderBottonWidth);
		y = pb.y + pb.height + hBorder;
		x = ((x + s.x) >= (ws.x - 5 )) ? x - (x + s.x - ws.x): x;
		if ( (y + s.y) >= (ws.y - 5 )) {
			var myEl = this.getHtmlElement();
			myEl.style.height = ws.y - y - 30;
			myEl.style.overflow = "auto";
		}
		//y = ((y + s.y) >= (ws.y - 30 )) ? y - (y + s.y - ws.y) : y;

		this.setLocation(x, y);
	} else if (this.parent instanceof DwtButton) { // Parent is DwtButton
		var p = this.parent;
		var pHtmlElement = p.getHtmlElement();
		// since buttons are often absolutely positioned, and menus aren't, we need x,y relative to window
		var ptw = Dwt.toWindow(pHtmlElement, 0, 0);
 		vBorder = (pHtmlElement.style.borderLeftWidth == "") ? 0 : parseInt(pHtmlElement.style.borderLeftWidth);
		x = ptw.x + vBorder;
		hBorder = (pHtmlElement.style.borderTopWidth == "") ? 0 : parseInt(pHtmlElement.style.borderTopWidth);
		hBorder += (pHtmlElement.style.borderBottomWidth == "") ? 0 : parseInt(pHtmlElement.style.borderBottonWidth);
		y = ptw.y + pb.height + hBorder;
		x = ((x + s.x) >= ws.x) ? x - (x + s.x - ws.x): x;
		y = ((y + s.y) >= ws.y) ? y - (y + s.y - ws.y) : y;

		this.setLocation(x, y);
	} else {
		// Popup menu type
		x = args.x;
		y = args.y;
		var newX = ((x + s.x) >= ws.x) ? x - (x + s.x - ws.x): x;
		var newY = ((y + s.y) >= ws.y) ? y - (y + s.y - ws.y) : y;	
		this.setLocation(newX, newY);	
	}

	// Hide the tooltip
	var tooltip = this.shell.getToolTip();
	if (tooltip)
		tooltip.popdown();

	// 5/2/2005
	// EMC -- changed this to Z_DIALOG_MENU so that you don't have to pass 
	// dialog object. This helps if you are adding an object to a dialog -- 
	// where the object doesn't know anything about its container.
	// var zIndex = this._dialog ? this._dialog.getZIndex() + Dwt.Z_INC : Dwt.Z_MENU;
	var zIndex = this._dialog ? Dwt.Z_DIALOG_MENU : Dwt.Z_MENU;
	this.setZIndex(zIndex);
	this._popupActionId = -1;
	this._isPoppedup = true;
	if (this._outsideListener) {
		this.shell._setEventHdlrs([DwtEvent.ONMOUSEDOWN]);
		this.shell.addListener(DwtEvent.ONMOUSEDOWN, this._outsideListener);
	}
	if (!DwtMenu._activeMenu) {
		DwtMenu._activeMenu = this;
		DwtMenu._activeMenuUp = true;
		DwtEventManager.addListener(DwtEvent.ONMOUSEDOWN, DwtMenu._outsideMouseDownListener);
	}

	DwtMenu._activeMenuIds.add(this._htmlElId);
	DwtMenu._activeMenuIds.sort();		
}

DwtMenu.prototype._doPopdown =
function() {
	// Notify all sub menus to pop themselves down
	var a = this._children.getArray();
	var s = this._children.size();
	for (var i = 0; i < s; i++) {
		if ((a[i] instanceof DwtMenuItem) && a[i]._style != DwtMenuItem.SEPARATOR_STYLE)
			a[i]._popdownMenu();
	}
	this.setZIndex(Dwt.Z_HIDDEN);
	
	this.notifyListeners(DwtEvent.POPDOWN, this);
	
	// TODO: release capture if you have it
	if (this._outsideListener) {
		this.shell._setEventHdlrs([DwtEvent.ONMOUSEDOWN], true);
		this.shell.removeListener(DwtEvent.ONMOUSEDOWN, this._outsideListener);
	}

	if (DwtMenu._activeMenu == this) {
		DwtMenu._activeMenu = null;
		DwtMenu._activeMenuUp = false;
		DwtEventManager.removeListener(DwtEvent.ONMOUSEDOWN, DwtMenu._outsideMouseDownListener);
	}
	DwtMenu._activeMenuIds.remove(this._htmlElId);
	this._popdownActionId = -1;
	this._isPoppedup = false;
}

DwtMenu.prototype._getActiveItem = 
function(){
	var a = this._children.getArray();
	var s = this._children.size();
	for (var i = 0; i < s; i++) {
		if (a[i]._isMenuPoppedup())
			return a[i];
	}
	return null;
}

DwtMenu._mouseDownListener =
function(ev) {
	if (!DwtMenu._activeMenuUp) return;

    var obj = DwtMenu._activeMenu;
    var mi = ev ? ev.dwtObj : null;

	// If we are dealing with a menu item that is itself contained in a menu that is
	// a menu bar, then don't do a popdown, else if the menu item has a menu with a selection
	// listener, then continue with a popdown, else let the menu item deal with it.
	if (obj.parent instanceof DwtMenuItem && obj.parent.parent._style == DwtMenu.BAR_STYLE) {
		obj.parent.parent._getActiveItem()._deselect();
		return true;
	} else if (mi && mi instanceof DwtMenuItem && !mi.isListenerRegistered(DwtEvent.SELECTION) && mi.getMenu() != null) {
		return true;
	}
	obj.popdown();
	return true;		
}

/* Note that a hack has been added to DwtHtmlEditor to call this method when the editor gets focus. The reason
 * for this is that the editor uses an Iframe whose events are independent of the menu's document. In this case
 * event will be null.
 */
DwtMenu._outsideMouseDownListener =
function(ev) {
    if (DwtMenu._activeMenuUp) {
		// figure out if we are over the menu that is up
		var menu = DwtMenu._activeMenu;
		var nearestDwtObj = DwtUiEvent.getDwtObjFromEvent(ev);
		if (menu._associatedObj && menu._associatedObj == nearestDwtObj) {
			return;
		}

		// assuming that the active menu is the parent of all other menus
		// that are up, search through the array of child menu dom IDs as
		// well as our own.
		var id = menu._htmlElId;
		var htmlEl = DwtUiEvent.getTarget(ev);
		while (htmlEl != null) {
			if (htmlEl.id && htmlEl.id != "" && 
				(htmlEl.id == id || htmlEl.id == menu._associatedElId ||
				 DwtMenu._activeMenuIds.binarySearch(htmlEl.id) != -1 )) {
				return;
			}
			htmlEl = htmlEl.parentNode;
		}

		// If we've gotten here, the mousedown happened outside the active
		// menu, so we hide it.
		menu.popdown();
	}
	// propagate the event
	ev._stopPropagation = false;
	ev._returnValue = true;
};

/*
* Returns true if any menu is currently popped up.
*/
DwtMenu.menuShowing =
function() {
	return DwtMenu._activeMenuUp;
};

DwtMenu.closeActiveMenu =
function() {
	if (DwtMenu._activeMenuUp){
		DwtMenu._activeMenu.popdown();
	}
};
