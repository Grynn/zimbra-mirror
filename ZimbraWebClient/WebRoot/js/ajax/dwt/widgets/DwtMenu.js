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

/**
* Creates a menu object to menu items can be added. Menus can be created in various styles as
* follows:
*
* DwtMenu.BAR_STYLE - Traditional menu bar.
* DwtMenu.POPUP_STYLE - Popup menu
* DwtMenu.DROPDOWN_STYLE - Used when a menu is a drop down (e.g. parent is a button or another menu item);
* DwtMenu.COLOR_PICKER_STYLE - Menu is hosting a single color picker;
* DwtMenu.CALENDAR_PICKER_STYLE - Menu is hostng a single calendar;
* DwtMenu.GENERIC_WIDGET_STYLE - Menu is hosting a single "DwtInsertTableGrid";
*
* @constructor
* @class
*
* @author Ross Dargahi
* 
* @param parent		the parent widget
* @param style 		menu's style
* @param className	a CSS class
* @param posStyle	positioning style
* @param dialog 	Dialog that this menu is a part of (if any)
*/
DwtMenu = function(parent, style, className, posStyle, dialog) {
	if (arguments.length == 0) return;
	if (parent) {
		if (parent instanceof DwtMenuItem || parent instanceof DwtButton) {
			if (style == DwtMenu.GENERIC_WIDGET_STYLE)
				this._style = DwtMenu.GENERIC_WIDGET_STYLE;
			else
				this._style = DwtMenu.DROPDOWN_STYLE;
		} else {
			this._style = style || DwtMenu.POPUP_STYLE;
		}
		if (!posStyle)
			posStyle = (this._style == DwtMenu.BAR_STYLE) ? DwtControl.STATIC_STYLE : DwtControl.ABSOLUTE_STYLE; 
	}
	className = className || "DwtMenu";

	// Hack to force us to hang off of the shell for positioning.
	DwtComposite.call(this, (parent instanceof DwtShell) ? parent : parent.shell, className, posStyle);
	this.parent = parent;
	if (!parent) { return; }
	this._dialog = dialog;

	var events = AjxEnv.isIE ? [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP] :
							   [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP, DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT];
	this._setEventHdlrs(events);
	this._hasSetMouseEvents = true;
	
	var htmlElement = this.getHtmlElement();
	
	Dwt.setLocation(htmlElement, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);

	// Don't need to create table for color picker and calendar picker styles
	if (this._style != DwtMenu.COLOR_PICKER_STYLE &&
		this._style != DwtMenu.CALENDAR_PICKER_STYLE &&
		this._style != DwtMenu.GENERIC_WIDGET_STYLE)
	{
		this._table = document.createElement("table");
		this._table.border = this._table.cellPadding = this._table.cellSpacing = 0;
		this._table.className = "DwtMenuTable";
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
	this._popdownAction = new AjxTimedAction(this, this._doPopdown);
	this._popdownActionId = -1;
	this._popupAction = new AjxTimedAction(this, this._doPopup);
	this._popupActionId = -1;

	this._menuItemsHaveChecks = false;	
	this._menuItemsHaveIcons = false;
	this._menuItemsWithSubmenus = 0;
	this.__currentItem = null;
	this.__preventMenuFocus = false;

	// The global capture is used to detect mouse down events outside of the
	// popped up menus and specifically outside of our scope of influence
	// (particularly when Dwt is being used in existing HTML)
	this._menuCapObj = new DwtMouseEventCapture(this,
		"DwtMenu",
		null,						// mouse over
		DwtMenu._capMouseDownHdlr,
		null,						// mouse move
		null,						// mouse up
		null,						// mouse out
		DwtMenu._capMouseWheelHdlr,	// mouse wheel
		false
	);
	
	// Default menu tab group. Note that we disable application handling of
	// keyboard shortcuts, since we don't want the view underneath reacting to
	// keystrokes in the menu.
	this._tabGroup = new DwtTabGroup(this.toString(), true);
	this._tabGroup.addMember(this);
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
DwtMenu.GENERIC_WIDGET_STYLE = 6;

DwtMenu.HAS_ICON = "ZHasIcon";
DwtMenu.HAS_CHECK = "ZHasCheck";
DwtMenu.HAS_SUBMENU = "ZHasSubMenu";

DwtMenu._activeMenuUp = false;
DwtMenu._activeMenuIds = new AjxVector();
DwtMenu._activeMenus = new AjxVector() ;

DwtMenu.prototype.dispose =
function() {
	DwtComposite.prototype.dispose.call(this);

	// Remove this from the shell. (Required because of hack in constructor.) 
	if (!(this.parent instanceof DwtShell)) {
		this.shell.removeChild(this);	
	}
};

DwtMenu.prototype.addPopupListener =
function(listener) {
	this.addListener(DwtEvent.POPUP, listener);
}

DwtMenu.prototype.removePopupListener = 
function(listener) {
	this.removeListener(DwtEvent.POPUP, listener);
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
		if ((style == null || (mi._style & style != 0)) && mi.getChecked())
			return mi;
	}
	return null;
}

DwtMenu.prototype.isPoppedup =
function() {
	return this._isPoppedup;
}

DwtMenu.prototype.popup =
function(msec, x, y, kbGenerated) {
	if (this._style == DwtMenu.BAR_STYLE) return;
	
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
			this._doPopup(x, y, kbGenerated);
		} else {
			this._popupAction.args = [x, y, kbGenerated];
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

DwtMenu.prototype.getKeyMapName = 
function() {
	return "DwtMenu";
};

DwtMenu.prototype.handleKeyAction =
function(actionCode, ev) {
	// For now don't deal with anything but BAR, POPUP, and DROPDOWN style menus
	switch (this._style) {
		case DwtMenu.BAR_STYLE:
		case DwtMenu.POPUP_STYLE:
		case DwtMenu.DROPDOWN_STYLE:
			break;
			
		default:
			return false;
	}

	switch (actionCode) {
		case DwtKeyMap.SELECT_NEXT:
		case DwtKeyMap.SELECT_PREV: 
			this.setSelectedItem(actionCode == DwtKeyMap.SELECT_NEXT);
			break;

		case DwtKeyMap.SELECT:
			if (this.__currentItem) {
				this.__currentItem._emulateSingleClick();
			}
			break;
		
		case DwtKeyMap.SUBMENU:
			if (this.__currentItem && this.__currentItem._menu) {
				this.__currentItem._popupMenu(0, true);	
			}
			break;
			
		case DwtKeyMap.PARENTMENU:
			if (this.parent instanceof DwtMenuItem)
				this.popdown(0);
			break;
			
		case DwtKeyMap.CANCEL:
			if (this.__currentItem) {
				var mev = DwtShell.mouseEvent;
				this._setMouseEvent(mev, true, this.__currentItem, null, false, false, false, 0, 0);
				this.__currentItem._mouseOutListener(mev);
				this.__currentItem = null;
			}
			this.popdown(0);
			break;		
			
		default:
			return false;		
	}
	
	return true;
}

DwtMenu.prototype._setMouseEvent =
function(mev, ersatz, dwtObj, button, ctrl, shift, alt, docX, docY) {
	mev.reset();
	mev.target = dwtObj.getHtmlElement();
	mev.button = button;
	mev.docX = docX;
	mev.docY = docY;
	mev.shiftKey = shift;
	mev.altKey = alt;
	mev.ctrlKey = ctrl;
	mev.dwtObj = dwtObj;
	mev.ersatz = ersatz;
}


DwtMenu.prototype._focus =
function() {
	//DBG.println(AjxDebug.DBG1, "DwtMenu.prototype._focus");
}

DwtMenu.prototype._blur =
function() {
	//DBG.println(AjxDebug.DBG1, "DwtMenu.prototype._blur");
}



/**
 * This allows the caller to associate one object with the menu. Association
 * means, for events, treat the menu, and this object as one. If I click on
 * elements pertaining to this object, we will think of them as part of the
 * menu. 
 * @see _outsideMouseListener.
 */
DwtMenu.prototype.setAssociatedObj =
function(dwtObj) {
	this._associatedObj = dwtObj;
};

DwtMenu.prototype.setAssociatedElementId =
function(id){
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
		if (!(item.isStyle(DwtMenuItem.CHECK_STYLE) || item.isStyle(DwtMenuItem.RADIO_STYLE))) {
			continue;
		}
		var val = item.getData(field);
	 	if (val == value)
			item.setChecked(true, skipNotify);
	}
}

/**
 * Programmatically selects a menu item. The item can be specified with an index,
 * or as the next or previous item based on which item is currently selected. If
 * the new item is a separator or is disabled, it won't be selected. Instead, the
 * next suitable item will be used.
 * 
 * @param which		{boolean|int}	if true, selects the next menu item
 * 									if false, selects the previous menu item
 * 									if int, selects the menu item with that index
 */
DwtMenu.prototype.setSelectedItem =
function(which) {
	var currItem = this.__currentItem;
	if (typeof(which) == "boolean") {
		currItem = !currItem
			? this._children.get(0)
			: which ? this._children.getNext(currItem) : this._children.getPrev(currItem);
	} else {
		currItem = this._children.get(which);
	}
	// While the current item is not enabled or is a separator, try another
	while (currItem && (currItem.isStyle(DwtMenuItem.SEPARATOR_STYLE) || !currItem.getEnabled())) {
		currItem = (which === false) ? this._children.getPrev(currItem) : this._children.getNext(currItem);
	}
	if (!currItem) return;

	// if we have a current item then we need to make sure we simulate a
	// mouseout event so that the UI can behave correctly
	var mev = DwtShell.mouseEvent;
	if (this.__currentItem) {
		this._setMouseEvent(mev, true, this.__currentItem, null, false, false, false, 0, 0);
		this.__currentItem._mouseOutListener(mev);
	}
	this._setMouseEvent(mev, true, currItem, null, false, false, false, 0, 0);
	currItem._mouseOverListener(mev);	// mouseover selects a menu item
	this.__currentItem = currItem;
};

DwtMenu.prototype.clearExternallySelectedItems =
function() {
	if (this._externallySelected != null) {
		this._externallySelected._deselect();
		this._externallySelected = null;
	}
};

DwtMenu.prototype.removeChild =
function(child) {
	if (this._style == DwtMenu.BAR_STYLE) {
		var cell = child.getHtmlElement().parentNode;
		this._table.rows[0].deleteCell(Dwt.getCellIndex(cell));
	} else {
		var el = child.getHtmlElement();
		if (el)
			this._table.deleteRow(el.parentNode.parentNode.rowIndex);
	}
	this._children.remove(child);
}

DwtMenu.prototype.addChild = 
function(child) {
    DwtComposite.prototype.addChild.apply(this, arguments);
    // Color pickers and calendars are not menu aware so we have to deal with
	// them acordingly
	if (Dwt.instanceOf(child, "DwtColorPicker") || Dwt.instanceOf(child, "DwtCalendar") ||
	    (this._style == DwtMenu.GENERIC_WIDGET_STYLE))
		this._addItem(child);
}

// All children are added now, including menu items. Previously, it wasn't
// reparenting and that was preventing the menu items from using templates
// because they need to be in the DOM in order to get access to elements
// within the template.
DwtMenu.prototype._addItem =
function(item, index) {
	if (this._style == DwtMenu.COLOR_PICKER_STYLE ||
		this._style == DwtMenu.CALENDAR_PICKER_STYLE ||
		this._style == DwtMenu.GENERIC_WIDGET_STYLE)
	{
		return;
	}

	var row;
	var col;
	if (this._style == DwtMenu.BAR_STYLE) {
		var rows = this._table.rows;
		row = (rows.length != 0) ? rows[0]: this._table.insertRow(0);
		if (index == null || index > row.cells.length)
			index = rows.cells.length;
		col = row.insertCell(index);
		col.align = "center";
		col.vAlign = "middle";
		var spc = row.insertCell(-1);
		spc.nowrap = true;
		spc.width = "7px"
	} else {
		// If item we're adding is check/radio style, and its the first such
		// item in the menu, then we must instruct our other children to add 
		// a "checked column" to ensure that things line up
		if (item.isStyle && (item.isStyle(DwtMenuItem.CHECK_STYLE) || item.isStyle(DwtMenuItem.RADIO_STYLE))) {
			this._checkItemAdded();
		}
		if (index == null || index > this._table.rows.length)
			index = -1;
		row = this._table.insertRow(index);
		col = row.insertCell(0);
	}
	col.noWrap = true;
	col.appendChild(item.getHtmlElement());
//	this._children.add(item, index);
};

DwtMenu.prototype._radioItemSelected =
function(child, skipNotify) {
	var radioGroupId = child._radioGroupId;
	var sz = this._children.size();
	var a = this._children.getArray();
	for (var i = 0; i < sz; i++) {
		if (a[i] != child && a[i].isStyle(DwtMenuItem.RADIO_STYLE) &&
			a[i]._radioGroupId == radioGroupId && a[i]._itemChecked)
		{
			a[i].setChecked(false, skipNotify);
			break;
		}
	}
}

DwtMenu.prototype._menuHasCheckedItems =
function() {
	return this._menuItemsHaveChecks;
}

DwtMenu.prototype._menuHasItemsWithIcons =
function() {
	return this._menuItemsHaveIcons;
}

DwtMenu.prototype._menuHasSubmenus =
function() {
	return (this._menuItemsWithSubmenus > 0);
}

/* Once an icon is added to any menuItem, then the menu will be considered
 * to contain menu items with icons in perpetuity */
DwtMenu.prototype._iconItemAdded =
function(item) {
	if (!this._menuItemsHaveIcons) Dwt.addClass(this.getHtmlElement(), DwtMenu.HAS_ICON);
	this._menuItemsHaveIcons = true;
}

/* Once an check/radio is added to any menuItem, then the menu will be considered
 * to contain checked items in perpetuity */
DwtMenu.prototype._checkItemAdded = function(item) {
	if (!this._menuItemsHaveChecks) Dwt.addClass(this.getHtmlElement(), DwtMenu.HAS_CHECK);
	this._menuItemsHaveChecks = true;
};

DwtMenu.prototype._submenuItemAdded =
function() {
	Dwt.addClass(this.getHtmlElement(), DwtMenu.HAS_SUBMENU);
	this._menuItemsWithSubmenus++;
}

DwtMenu.prototype._submenuItemRemoved =
function() {
	if (this._menuItemsWithSubmenus == 1) {
		var sz = this._children.size();
		var a = this._children.getArray();
		for (var i = 0; i < sz; i++)
			a[i]._submenuItemRemoved();
	}
	this._menuItemsWithSubmenus--;
	if (this._menuItemsWithSubmenus == 0) {
		Dwt.delClass(this.getHtmlElement(), DwtMenu.HAS_SUBMENU);
	}
}

DwtMenu.prototype._popdownSubmenus = function() {
	var sz = this._children.size();
	var a = this._children.getArray();
	for (var i = 0; i < sz; i++) {
		a[i]._popdownMenu();
	}
};

DwtMenu.prototype.dontStealFocus =
function(val) {
	if (val == null)
		val = true;
	this.__preventMenuFocus = !!val;
};

DwtMenu.prototype._doPopup =
function(x, y, kbGenerated) {
	var windowSize = this.shell.getSize();
	var mySize = this.getSize();

	// bug 9583 - can't query border size so just subtract generic padding
	windowSize.y -= 10 + AjxEnv.isIE ? 20 : 0;
	windowSize.x -= 20;

	var isPopup = this._style == DwtMenu.POPUP_STYLE;
	if (isPopup) {
		var space = windowSize.y;
		var newY = null;
		var rows = this._table.rows;
		var numRows = rows.length;
		var height = mySize.y;
		var requiredSpace = space - 25; // Account for space on top & bottom of menu.
		for (var i = numRows - 1; i >= 0; i--) {
			height -= Dwt.getSize(rows[i]).y;
			if (height < requiredSpace) {
				break;
			}
		}
		var count = i + 1;
		for (var j = count; j < numRows; j++) {
			var row = rows[(j - count) % count];
			var cell = row.insertCell(-1);
			cell.className = "DwtMenuCascadeCell";
			var child = rows[j].cells[0].firstChild;
			while (child != null) {
				cell.appendChild(child);
				child = child.nextSibling;
			}
		}
		for (j = rows.length - 1; j >= count; j--) {
			this._table.deleteRow(count);
		}
		var offset = numRows % count;
		if (offset > 0) {
			for (var j = offset; j < count; j++) {
				var row = rows[j];
				var cell = row.insertCell(-1);
				cell.className = "DwtMenuCascadeCell";
				cell.empty = true;
				cell.innerHTML = "&nbsp;";
			}
		}
		
		mySize = this.getSize();
		if (newY) {
			y = newY - mySize.y;
		}
	}

	// Popup menu type
	var newX = ((x + mySize.x) >= windowSize.x) ? (windowSize.x - mySize.x) : x;
	var newY = ((y + mySize.y) >= windowSize.y) ? (windowSize.y - mySize.y) : y;
	this.setLocation(newX, isPopup ? newY : y);
	this.setSize("auto", isPopup || y + mySize.y < windowSize.y - 5 ? "auto" : windowSize.y - y - 5);
	this.setScrollStyle(isPopup ? Dwt.CLIP : Dwt.SCROLL);
	
	this.notifyListeners(DwtEvent.POPUP, this);

	// Hide the tooltip
	var tooltip = this.shell.getToolTip();
	if (tooltip)
		tooltip.popdown();

	// 5/2/2005
	// EMC -- changed this to Z_DIALOG_MENU so that you don't have to pass 
	// dialog object. This helps if you are adding an object to a dialog -- 
	// where the object doesn't know anything about its container.
	// var zIndex = this._dialog ? this._dialog.getZIndex() + Dwt._Z_INC : Dwt.Z_MENU;
	var zIndex = this._dialog ? Dwt.Z_DIALOG_MENU : Dwt.Z_MENU;
	this.setZIndex(zIndex);
	this._popupActionId = -1;
	this._isPoppedup = true;
	if (!DwtMenu._activeMenu) {
		DwtMenu._activeMenu = this;
		DwtMenu._activeMenuUp = true;
	}

	DwtMenu._activeMenuIds.add(this._htmlElId);
	DwtMenu._activeMenuIds.sort();	
	DwtMenu._activeMenus.add (this);
	
	// Capture events only if we are not a sub-menu. Event capturing is to catch mouse-events outside
	// of our framework (esp. vital when DWT is being used in existing HTML content)
	if (!this._menuCapObj.capturing()) {
		this._menuCapObj.capture();	
		this._capturing = true;
	} else {
		this._capturing = false;
	}
	
	// NOTE: This hack is needed for FF/Moz because the containing div
	//	   allows the inner table to overflow. When the menu cascades
	//	   and the menu items get pushed off of the visible area, the
	//	   div's border doesn't surround the menu items. This hack
	//	   forces the outer div's width to surround the table.
	if ((AjxEnv.isGeckoBased || AjxEnv.isSafari)&& this._table) {
		var htmlEl = this.getHtmlElement();
		htmlEl.style.width = mySize.x + "px";
	}
	
	// Put our tabgroup in play
	if (!this.__preventMenuFocus)
		DwtShell.getShell(window).getKeyboardMgr().pushTabGroup(this._tabGroup);
	
	/* If the popup was keyboard generated, then pick the first enabled child item
	 * we do this by simulating a DwtKeyMap.SELECT_NEXT keyboard action */
	 if (kbGenerated)
	 	this.handleKeyAction(DwtKeyMap.SELECT_NEXT);
	
};

DwtMenu.prototype.getSize =
function(incScroll) {
	if (this._table) {
		return Dwt.getSize(this._table, incScroll);
	}
	return DwtComposite.prototype.getSize.call(this, incScroll);
};

DwtMenu.prototype._doPopdown =
function() {
	// Notify all sub menus to pop themselves down
	var a = this._children.getArray();
	var s = this._children.size();
	for (var i = 0; i < s; i++) {
		if ((a[i] instanceof DwtMenuItem) && !(a[i].isStyle(DwtMenuItem.SEPARATOR_STYLE)))
			a[i]._popdownMenu();
	}
	this.setZIndex(Dwt.Z_HIDDEN);
	this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	
	this.notifyListeners(DwtEvent.POPDOWN, this);
	
	if (DwtMenu._activeMenu == this) {
		DwtMenu._activeMenu = null;
		DwtMenu._activeMenuUp = false;
	}
	DwtMenu._activeMenuIds.remove(this._htmlElId);
	DwtMenu._activeMenus.remove(this);
	this._popdownActionId = -1;
	this._isPoppedup = false;

	if (this._capturing) {
		this._menuCapObj.release();
		this._capturing = false;
	}

	if ((this._style == DwtMenu.POPUP_STYLE || this._style == DwtMenu.DROPDOWN_STYLE) &&
		this._table.rows.length && this._table.rows[0].cells.length)
	{
		var numColumns = this._table.rows[0].cells.length;
		var numRows = this._table.rows.length;
		for (var i = 1; i < numColumns; i++) {
			for (var j = 0; j < numRows; j++) {
				var cell = this._table.rows[j].cells[i];
				if (!cell.empty) {
					var child = cell.firstChild;
					var row = this._table.insertRow(this._table.rows.length);
					var cell = row.insertCell(0);
					while (child != null) {
						cell.appendChild(child);
						child = child.nextSibling;
					}
				}
			}
		}
		for (var j = 0; j < numRows; j++) {
			var row = this._table.rows[j];
			for (var i = row.cells.length - 1; i > 0; i--) {
				row.deleteCell(i);
			}
		}
	}
	
	// set the current item (used in kb nav) to null
	this.__currentItem = null;
	
	// Take our tabgroup out of play
	DwtShell.getShell(window).getKeyboardMgr().popTabGroup(this._tabGroup);	
};

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

/* Note that a hack has been added to DwtHtmlEditor to call this method when the 
 * editor gets focus. The reason for this is that the editor uses an Iframe 
 * whose events are independent of the menu's document. In this case event will 
 * be null.
 */
DwtMenu._outsideMouseDownListener =
function(ev) {
	if (DwtMenu._activeMenuUp) {
		// figure out if we are over the menu that is up
		var menu = DwtMenu._activeMenu;
		var nearestDwtObj = DwtUiEvent.getDwtObjFromEvent(ev);
		if (menu._associatedObj && menu._associatedObj == nearestDwtObj) {
			return false;
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
				return false;
			}
			htmlEl = htmlEl.parentNode;
		}

		// If we've gotten here, the mousedown happened outside the active
		// menu, so we hide it.
		menu.popdown();
		
		//it should remove all the active menus 
		var cMenu = null ;
		do {
			cMenu = DwtMenu._activeMenus.getLast() ;
			if (cMenu!= null && cMenu instanceof DwtMenu) cMenu.popdown();
		} while (cMenu != null) ;
	}
	// propagate the event
	ev._stopPropagation = false;
	ev._returnValue = true;
	return true;
};

DwtMenu._capMouseDownHdlr =
function(ev) {
	var menu = DwtMouseEventCapture.getTargetObj();
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	DwtMenu._outsideMouseDownListener(mouseEv);
	DwtUiEvent.setBehaviour(ev, false, true);
	return true;
}

DwtMenu._capMouseWheelHdlr = function(ev) {
	return DwtMenu._capMouseDownHdlr(ev);
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
	if (DwtMenu._activeMenuUp) {
		DwtMenu._activeMenu.popdown();
	}
};
