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
 * Creates a menu.
 * @constructor
 * @class
 * Creates a menu object to menu items can be added. Menus can be created in various styles as
 * follows:
 * <ul>
 * <li>DwtMenu.BAR_STYLE - Traditional menu bar</li>
 * <li>DwtMenu.POPUP_STYLE - Popup menu</li>
 * <li>DwtMenu.DROPDOWN_STYLE - Used when a menu is a drop down (e.g. parent is a button or another menu item)</li>
 * <li>DwtMenu.COLOR_PICKER_STYLE - Menu is hosting a single color picker</li>
 * <li>DwtMenu.CALENDAR_PICKER_STYLE - Menu is hostng a single calendar</li>
 * <li>DwtMenu.GENERIC_WIDGET_STYLE - Menu is hosting a single "DwtInsertTableGrid"</li>
 * </ul>
  *
 * @author Ross Dargahi
 * 
 * @param {hash}	params		a hash of parameters
 * @param       {DwtComposite}	params.parent		the parent widget
 * @param {constant}      params.style			the menu style
 * @param {string}        params.className		the CSS class
 * @param {constant}      params.posStyle		the positioning style (see {@link DwtControl})
 * @param {constant}      params.layout			layout to use: DwtMenu.LAYOUT_STACK, DwtMenu.LAYOUT_CASCADE or DwtMenu.LAYOUT_SCROLL. A value of [true] defaults to DwtMenu.LAYOUT_CASCADE and a value of [false] defaults to DwtMenu.LAYOUT_STACK.
 * @param {int}		  params.maxRows=0	    	if >0 and layout = LAYOUT_CASCADE or DwtMenu.LAYOUT_SCROLL, define how many rows are allowed before cascading/scrolling
 * @param {boolean}		params.congruent		if the parent is a DwtMenuItem, align so that the submenu "merges" with the parent menu
 * 
 * @extends		DwtComposite
 */
DwtMenu = function(params) {
	this._created = false;
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtMenu.PARAMS);

	this._origStyle = params.style;
	var parent = params.parent;
	if (parent) {
		if (parent instanceof DwtMenuItem || parent instanceof DwtButton) {
			if (params.style == DwtMenu.GENERIC_WIDGET_STYLE) {
				this._style = params.style;
			} else {
				this._style = DwtMenu.DROPDOWN_STYLE;
			}
		} else {
			this._style = params.style || DwtMenu.POPUP_STYLE;
		}
		if (!params.posStyle) {
			params.posStyle = (this._style == DwtMenu.BAR_STYLE) ? DwtControl.STATIC_STYLE : DwtControl.ABSOLUTE_STYLE;
		}
	}
	params.className = params.className || "DwtMenu";

	this._layoutStyle = params.layout == null || params.layout;
	if (this._layoutStyle === true) {
		this._layoutStyle = DwtMenu.LAYOUT_CASCADE;
	} else if (this._layoutStyle === false) {
		this._layoutStyle = DwtMenu.LAYOUT_STACK;
	}
	this._maxRows = this._layoutStyle && params.maxRows || 0;
	this._congruent = params.congruent;

	// Hack to force us to hang off of the shell for positioning.
	params.parent = (parent instanceof DwtShell) ? parent : parent.shell;
	DwtComposite.call(this, params);
	this.parent = parent;

	var isPopup = (this._style == DwtMenu.POPUP_STYLE || this._style == DwtMenu.DROPDOWN_STYLE);
	if (isPopup && (this._layoutStyle == DwtMenu.LAYOUT_STACK)) {
		this.setScrollStyle(DwtControl.SCROLL);
	}

	if (!parent) { return; }

	var events = AjxEnv.isIE ? [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP] :
							   [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP, DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT];
	this._setEventHdlrs(events);
	this._hasSetMouseEvents = true;
	
	var htmlElement = this.getHtmlElement();

	if (params.posStyle != DwtControl.STATIC_STYLE) {
		Dwt.setLocation(htmlElement, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	}

	// Don't need to create table for color picker and calendar picker styles
	if (this._style != DwtMenu.COLOR_PICKER_STYLE &&
		this._style != DwtMenu.CALENDAR_PICKER_STYLE &&
		this._style != DwtMenu.GENERIC_WIDGET_STYLE)
	{
		this._table = document.createElement("table");
		this._table.border = this._table.cellPadding = this._table.cellSpacing = 0;
		this._table.className = "DwtMenuTable";
		this._table.id = Dwt.getNextId();
		if (this._layoutStyle == DwtMenu.LAYOUT_SCROLL) {
			this._setupScroll();
		} else {
			htmlElement.appendChild(this._table);
		}
		this._table.backgroundColor = DwtCssStyle.getProperty(htmlElement, "background-color");
	}

	if (params.style != DwtMenu.BAR_STYLE) {
		this.setZIndex(Dwt.Z_HIDDEN);
 		this._isPoppedUp = false;
	} else {
		DwtMenu._activeMenuIds.add(htmlElement.id, null, true);
		this._isPoppedUp = true;
 	}
	this._popdownAction = new AjxTimedAction(this, this._doPopdown);
	this._popdownActionId = -1;
	this._popupAction = new AjxTimedAction(this, this._doPopup);
	this._popupActionId = -1;

	this._outsideListener = new AjxListener(null, DwtMenu._outsideMouseDownListener);

	this._menuItemsHaveChecks = false;	
	this._menuItemsHaveIcons = false;
	this._menuItemsWithSubmenus = 0;
	this.__currentItem = null;
	this.__preventMenuFocus = false;

	// Default menu tab group. Note that we disable application handling of
	// keyboard shortcuts, since we don't want the view underneath reacting to
	// keystrokes in the menu.
	this._tabGroup = new DwtTabGroup(this.toString(), true);
	this._tabGroup.addMember(this);
	this._created = true;

    // When items are added, the menu listens to selection events
    // and will propagate the event to listeners that are registered
    // on the menu itself.
    this._itemSelectionListener = new AjxListener(this, this._propagateItemSelection);
};

DwtMenu.PARAMS = ["parent", "style", "className", "posStyle", "cascade", "id"];

DwtMenu.prototype = new DwtComposite;
DwtMenu.prototype.constructor = DwtMenu;

DwtMenu.prototype.isDwtMenu = true;
DwtMenu.prototype.toString = function() { return "DwtMenu"; };

DwtMenu.BAR_STYLE				= "BAR";
DwtMenu.POPUP_STYLE				= "POPUP";
DwtMenu.DROPDOWN_STYLE			= "DROPDOWN";
DwtMenu.COLOR_PICKER_STYLE		= "COLOR";
DwtMenu.CALENDAR_PICKER_STYLE	= "CALENDAR";
DwtMenu.GENERIC_WIDGET_STYLE	= "GENERIC";

DwtMenu.HAS_ICON = "ZHasIcon";
DwtMenu.HAS_CHECK = "ZHasCheck";
DwtMenu.HAS_SUBMENU = "ZHasSubMenu";

DwtMenu.LAYOUT_STACK 	= 0;
DwtMenu.LAYOUT_CASCADE 	= 1;
DwtMenu.LAYOUT_SCROLL 	= 2;

DwtMenu._activeMenuUp = false;
DwtMenu._activeMenuIds = new AjxVector();
DwtMenu._activeMenus = new AjxVector() ;

DwtMenu.prototype.dispose =
function() {
	this._table = null;
	DwtComposite.prototype.dispose.call(this);

	// Remove this from the shell. (Required because of hack in constructor.) 
	if (!(this.parent instanceof DwtShell)) {
		this.shell.removeChild(this);	
	}
};

/**
 * Adds a selection listener.
 * @param {AjxListener} listener The listener.
 */
DwtMenu.prototype.addSelectionListener = function(listener) {
    this.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a selection listener.
 * @param {AjxListener} listener The listener.
 */
DwtMenu.prototype.removeSelectionListener = function(listener) {
    this.removeListener(DwtEvent.SELECTION, listener);
};

/**
 * Adds a popup listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtMenu.prototype.addPopupListener =
function(listener) {
	this.addListener(DwtEvent.POPUP, listener);
};

/**
 * Removes a popup listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtMenu.prototype.removePopupListener = 
function(listener) {
	this.removeListener(DwtEvent.POPUP, listener);
};

/**
 * Adds a popdown listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtMenu.prototype.addPopdownListener = 
function(listener) {
	this.addListener(DwtEvent.POPDOWN, listener);
};

/**
 * Removes a popdown listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtMenu.prototype.removePopdownListener = 
function(listener) {
	this.removeListener(DwtEvent.POPDOWN, listener);
};

DwtMenu.prototype.setWidth = 
function(width) {
	this._width = width;
};

/**
 * Gets a menu item.
 * 
 * @param	{string}	index		the index
 * @return	{DwtMenuItem}		the menu item
 */
DwtMenu.prototype.getItem =
function(index) {
	return this._children.get(index);
};

DwtMenu.prototype.getItemIndex =
function(item) {
	return this._children.indexOf(item);
};

/**
 * Gets the item by id.
 * 
 * @param	{string}	key		the id key
 * @param	{Object}	id		the id value
 * @return	{DwtMenuItem}	the menu item
 */
DwtMenu.prototype.getItemById =
function(key, id) {
	var items = this.getItems();
	for (var i = 0; i < items.length; i++) {
		var itemId = items[i].getData(key);
		if (itemId == id) {
			items[i].index = i; //needed in some caller
			return items[i];
		}
	}
	return null;
};

/**
 * Gets a count of the items.
 * 
 * @return	{number}	the count
 */
DwtMenu.prototype.getItemCount =
function() {
	return this._children.size();
};

/**
 * Gets an array of items.
 * 
 * @return	{array}	an array of {@link DwtMenuItem} objects
 */
DwtMenu.prototype.getItems =
function() {
	return this._children.getArray();
};

DwtMenu.prototype.getSelectedItem =
function(style) {
	var a = this._children.getArray();
	for (var i = 0; i < a.length; i++) {
		var mi = a[i];
		if ((style == null || (mi._style && style != 0)) && mi.getChecked())
			return mi;
	}
	return null;
};

/**
 * Checks if the menu is popped-up.
 * 
 * @return	{boolean}	<code>true</code> if popped-up
 */
DwtMenu.prototype.isPoppedUp =
function() {
	return this._isPoppedUp;
};

DwtMenu.prototype.popup =
function(msec, x, y, kbGenerated) {
	if (this._style == DwtMenu.BAR_STYLE) return;
	
	if (this._popdownActionId != -1) {
		AjxTimedAction.cancelAction(this._popdownActionId);
		this._popdownActionId = -1;
	} else {
		if (this._isPoppedUp || (this._popupActionId != -1 && msec && msec > 0)) {
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
};

DwtMenu.prototype.popdown =
function(msec, ev) {
	if (this._style == DwtMenu.BAR_STYLE) return;

	if (this._popupActionId != -1) {
		AjxTimedAction.cancelAction(this._popupActionId);	
		this._popupActionId = -1;
	} else {
		if (!this._isPoppedUp || this._popdownActionId != -1)
			return;
		if (msec == null || msec == 0)
			this._doPopdown(ev);
		else
			this._popdownActionId = AjxTimedAction.scheduleAction(this._popdownAction, msec);
	}
};

DwtMenu.prototype._setupScroll = function() {
	var htmlElement = this.getHtmlElement();
	this._table.style.position = "relative";
			
	this._topScroller = document.createElement("div");
	this._topScroller.className = "DwtMenuScrollTop";
	this._topScroller.id = Dwt.getNextId();
	
	this._imgDivTop = document.createElement("div");
	this._imgDivTop.className = "ImgUpArrowSmall";
	this._topScroller.appendChild(this._imgDivTop);
	Dwt.setHandler(this._imgDivTop, DwtEvent.ONMOUSEOUT, DwtMenu._stopEvent);
	Dwt.setHandler(this._imgDivTop, DwtEvent.ONMOUSEOVER, DwtMenu._stopEvent);
	htmlElement.appendChild(this._topScroller);

	this._tableContainer = document.createElement("div");
	this._tableContainer.appendChild(this._table);
	htmlElement.appendChild(this._tableContainer);

	this._bottomScroller = document.createElement("div");
	this._bottomScroller.className = "DwtMenuScrollBottom";
	this._bottomScroller.id = Dwt.getNextId();
	
	this._imgDivBottom = document.createElement("div");
	this._imgDivBottom.className = "ImgDownArrowSmall";
	Dwt.setHandler(this._imgDivBottom, DwtEvent.ONMOUSEOUT, DwtMenu._stopEvent);
	Dwt.setHandler(this._imgDivBottom, DwtEvent.ONMOUSEOVER, DwtMenu._stopEvent);
	this._bottomScroller.appendChild(this._imgDivBottom);
	htmlElement.appendChild(this._bottomScroller);

	//scroll up
	var scrollUpStartListener = AjxCallback.simpleClosure(this._scroll, this, this._table.id, true, false);
	var scrollUpStopListener = AjxCallback.simpleClosure(this._scroll, this, this._table.id, false, false);
	var mouseOutTopListener = AjxCallback.simpleClosure(this._handleMouseOut, this, this._topScroller.id, this._table.id);
	var mouseOutBottomListener = AjxCallback.simpleClosure(this._handleMouseOut, this, this._bottomScroller.id, this._table.id);

	Dwt.setHandler(this._topScroller, DwtEvent.ONMOUSEDOWN, scrollUpStartListener);
	Dwt.setHandler(this._topScroller, DwtEvent.ONMOUSEUP, scrollUpStopListener);
	if (!AjxEnv.isIE) {
		Dwt.setHandler(this._topScroller, DwtEvent.ONMOUSEOUT, mouseOutTopListener);
	} else {
		Dwt.setHandler(this._topScroller, DwtEvent.ONMOUSELEAVE, scrollUpStopListener);
	}

	//scroll down
	var scrollDownStartListener = AjxCallback.simpleClosure(this._scroll, this, this._table.id, true, true);
	var scrollDownStopListener = AjxCallback.simpleClosure(this._scroll, this, this._table.id, false, true);

	Dwt.setHandler(this._bottomScroller, DwtEvent.ONMOUSEDOWN, scrollDownStartListener);
	Dwt.setHandler(this._bottomScroller, DwtEvent.ONMOUSEUP, scrollDownStopListener);
	if (!AjxEnv.isIE) {
		Dwt.setHandler(this._bottomScroller, DwtEvent.ONMOUSEOUT, mouseOutBottomListener);
	} else {
		Dwt.setHandler(this._bottomScroller, DwtEvent.ONMOUSELEAVE, scrollDownStopListener);
	}

	var wheelListener = AjxCallback.simpleClosure(this._handleScroll, this, this._table.id);
	Dwt.setHandler(htmlElement, DwtEvent.ONMOUSEWHEEL, wheelListener);
};

DwtMenu.prototype.render =
function(x, y) {
	var windowSize = this.shell.getSize();
	var mySize = this.getSize();
	var htmlEl = this.getHtmlElement();

	// bug 9583 - can't query border size so just subtract generic padding
	windowSize.y -= 10 + (AjxEnv.isIE ? 20 : 0);
	windowSize.x -= 28;

	var isScroll = this._layoutStyle == DwtMenu.LAYOUT_SCROLL;
	var isPopup = (this._style == DwtMenu.POPUP_STYLE || this._style == DwtMenu.DROPDOWN_STYLE);
	var isCascade = this._layoutStyle == DwtMenu.LAYOUT_CASCADE;
	if (this._table) {
		if (isPopup && isCascade) {
			var space = windowSize.y;
			var newY = null;
			var rows = this._table.rows;
			var numRows = rows.length;
			var maxRows = this._maxRows;
			var height = mySize.y;
			var requiredSpace = space - 25; // Account for space on top & bottom of menu.
			for (var i = numRows - 1; i >= 0; i--) {
				height -= Dwt.getSize(rows[i]).y;
				if (height < requiredSpace) {
					break;
				}
			}
			var count = maxRows ? Math.min(i + 1, maxRows) : (i + 1);
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
		} else if (isPopup && isScroll) {
			var rows = this._table.rows;
			var numRows = rows.length;
			var maxRows = this._maxRows;
			var limRows = maxRows ? Math.min(maxRows, numRows) : numRows;
			var availableSpace = windowSize.y - 25; // Account for space on top & bottom of menu.

			var height = 20; //for scroll buttons
			for (var i = 0; i < limRows; i++) {
				var rowSize = Dwt.getSize(rows[i]).y;
				if (height + rowSize <= availableSpace)
					height += rowSize;
				else
					break;
			}
			mySize.y = height;
		}
	}
	var newW = "auto";
	var newH = "auto";
	if (isPopup && isScroll) {
		newH = mySize.y;
		if (this._tableContainer)
			this._tableContainer.style.height = (newH - 20) +"px";
	} else if ((isPopup && isCascade) || y + mySize.y < windowSize.y - 5 ) {
		newH = "auto";
	} else {
		newH = windowSize.y - y - 5;
	}
    if(isScroll) {
	    if (this._table) {
		    this._table.style.width = mySize.x;
        }
        newW = mySize.x;
    }
    this.setSize(newW, newH);
	// NOTE: This hack is needed for FF/Moz because the containing div
	//	   allows the inner table to overflow. When the menu cascades
	//	   and the menu items get pushed off of the visible area, the
	//	   div's border doesn't surround the menu items. This hack
	//	   forces the outer div's width to surround the table.

	if ((AjxEnv.isGeckoBased || AjxEnv.isSafari || (this._origStyle == DwtMenu.CALENDAR_PICKER_STYLE)) && this._table && !isScroll) {
		htmlEl.style.width = (mySize.x + (isPopup && !isCascade ? 10 : 0)) + "px";
	}

	// Popup menu type
	var newX = x + mySize.x >= windowSize.x ? windowSize.x - mySize.x : x;
	if (this.parent instanceof DwtMenuItem) {
		Dwt.delClass(htmlEl, "DwtMenu-congruentLeft");
		Dwt.delClass(htmlEl, "DwtMenu-congruentRight");

		var pbound = this.parent.getBounds();
		var pmstyle = DwtCssStyle.getComputedStyleObject(this.parent.parent.getHtmlElement()); // Get the style for the DwtMenu holding the parent DwtMenuItem
		var tstyle = DwtCssStyle.getComputedStyleObject(htmlEl); // Get the style for this menu (includes skinning)

		//if the cascading extends over the edge of the screen, cascade to the left
		if ( ((newX > pbound.x && newX < pbound.x + pbound.width) || (pbound.x > newX && pbound.x < newX + mySize.x)) && pbound.x >= mySize.x) {
			var totalWidth = parseInt(tstyle.width);
			if (!AjxEnv.isIE)
				totalWidth += parseInt(tstyle.paddingLeft) + parseInt(tstyle.paddingRight) + parseInt(tstyle.borderLeftWidth) + parseInt(tstyle.borderRightWidth);
			newX = (parseInt(pmstyle.left) || pbound.x) - (totalWidth || mySize.x);
			if (this._congruent) {
				var offset;
				if (AjxEnv.isIE)
					offset = parseInt(tstyle.borderLeftWidth);
				else
					offset = parseInt(tstyle.borderLeftWidth) + parseInt(tstyle.borderRightWidth);
				if (!isNaN(offset)) {
					newX += offset;
					Dwt.addClass(htmlEl, "DwtMenu-congruentLeft");
				}
			}

		} else { // Cascade to the right
			var left = parseInt(pmstyle.left) || (pbound.x - (parseInt(pmstyle.paddingLeft) || 0));
			var width = parseInt(pmstyle.width) || pbound.width;
			newX = left + width;
			if (this._congruent) {
				var offset = parseInt(pmstyle.paddingRight) + parseInt(tstyle.paddingLeft) + parseInt(tstyle.borderLeftWidth);
				if (!isNaN(offset)) {
					newX += offset;
					Dwt.addClass(htmlEl, "DwtMenu-congruentRight");
				}
			}
		}
	}
	var newY = isPopup && y + mySize.y >= windowSize.y ? windowSize.y - mySize.y : y;

	if (this.parent instanceof DwtMenuItem && this._congruent) {
		var offset = (parseInt(tstyle.paddingTop) || 0) - (parseInt(tstyle.borderTopWidth) || 0);
		if (offset>0)
			newY -= offset;
	}

	this.setLocation(newX, newY);
};

DwtMenu.prototype.getKeyMapName = 
function() {
	return "DwtMenu";
};

DwtMenu.prototype._handleScroll =
function(divID, ev) {
	if (!ev) ev = window.event;
	var div = Dwt.byId(divID);
	if (div && ev) {
	 	ev = ev ? ev : window.event;
	  	var wheelData = ev.detail ? ev.detail * -1 : ev.wheelDelta / 40;
		var rows = div.rows;
		var step = Dwt.getSize(rows[0]).y || 10;
		this._popdownSubmenus();
		if (wheelData > 0) { //scroll up
			this._doScroll(div, +step)
		} else if (wheelData < 0) { //scroll down
			this._doScroll(div, -step)
		}
	}
};

DwtMenu.prototype._handleMouseOut = 
function(divID, tableID, ev) {
	if (divID && ev.type && ev.type == "mouseout" && !AjxEnv.isIE) {
		var div = divID ? Dwt.byId(divID) : null;
		fromEl = ev.target;
		if (fromEl != div) {
			return;
		}
		toEl = ev.relatedTarget;
		while (toEl) {
			toEl = toEl.parentNode;
			if (toEl == div) {
				return;
			}
		}
		this._scroll(tableID, false, false, null);
	}
};

DwtMenu.prototype._scroll =
function(divID, scrolling, direction, ev) {
	var div = divID ? document.getElementById(divID) : null;
	if (div && scrolling) {
		var rows = div.rows;
		var step = Dwt.getSize(rows[0]).y || 10;
		if (this._direction != direction || !this._scrollTimer) {
			this._popdownSubmenus();
			this._direction = direction;
			if (this._scrollTimer) {
				clearInterval(this._scrollTimer);
				this._scrollTimer = null;
			}
	
			if (direction) { //scroll down
				this._scrollTimer = setInterval(AjxCallback.simpleClosure(this._doScroll, this, div, -step), 100);
				this._doScroll(div, -step);
			} else { //scroll up
				this._scrollTimer = setInterval(AjxCallback.simpleClosure(this._doScroll, this, div, step), 100);
				this._doScroll(div, step);
			}
		}
	} else {
		if (this._scrollTimer) {
			clearInterval(this._scrollTimer);
			this._scrollTimer = null;
		}
	}
};

DwtMenu.prototype._doScroll =
function(div, step) {
	if (div && step) {
		var old = parseInt(div.style.top) || 0;
		var top;
		if (step < 0) { // scroll down
			var rows = this._table.rows || null;
			var height = rows && rows.length && Dwt.getSize(rows[0]).y;
			var max = div.scrollHeight - (parseInt(div.parentNode.style.height) || ((this._maxRows || (rows && rows.length)) * height) || 0);
			if (Math.abs(old + step) <= max) {
				top = old + step;
			} else {
				top = -max;
			}
		} else { // scroll up
			if ((old + step) < 0) {
				top = old + step;
			} else {
				top = 0;
			}
		}
		Dwt.setLocation(div, Dwt.DEFAULT, top);
	}
};

/**
 * Checks a menu item (the menu must be radio or checkbox style). The menu item
 * is identified through the given field/value pair.
 *
 * @param {DwtMenuItem}		item				the menu item to scroll to
 * @param {boolean}			justMakeVisible		false: scroll so the item is in the topmost row; true: scroll so the item is visible (scrolling down to an item puts it in the bottom row, doesn't scroll if the item is already visible)
 * 
 */
DwtMenu.prototype.scrollToItem =
function(item, justMakeVisible) {
	var index = this.getItemIndex(item);
	if (index != -1)
		this.scrollToIndex(index, justMakeVisible);
};

DwtMenu.prototype.scrollToIndex = 
function(index, justMakeVisible) {
	if (this._created && this._layoutStyle == DwtMenu.LAYOUT_SCROLL && index !== null && index >= 0 && this._table) {
		var rows = this._table.rows;
		if (rows) {
			var maxRows = this._maxRows;
			var visibleHeight = 0;
			var rowHeights = [];
			for (var i = 0, numRows = rows.length; i < numRows; i++) {
				var h = Dwt.getSize(rows[i]).y;
				if (i < maxRows)
					visibleHeight += h;
				rowHeights.push(h);
			}
		
			var itemHeight = rowHeights[index];
			var currentOffset = parseInt(this._table.style.top) || 0;
			if (index >= rows.length)
				index = rows.length-1;
			
			var itemOffset = 0;
			for (var i=0; i<index && i<rowHeights.length; i++) {
				itemOffset += rowHeights[i];
			}
			var delta = 0;
			if (justMakeVisible) {
				if (itemOffset < -currentOffset) {
					delta = -(itemOffset + currentOffset); // Scroll up, making the item the topmost visible row
				} else if (itemOffset + itemHeight > visibleHeight - currentOffset) {
					delta = -(itemOffset + currentOffset - visibleHeight + itemHeight); // Scroll down, making the item the lowermost visible row
				} // else do not scroll; item is already visible
			} else {
				delta = -(itemOffset + currentOffset); // Scroll so that the item is the topmost visible row
			}
			if (delta) {
				this._popdownSubmenus();
				this._doScroll(this._table, delta);
			}
		}
	}
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

		case DwtKeyMap.PAGE_UP:
		case DwtKeyMap.PAGE_DOWN:
			var item = this.__currentItem || this._children.get(0);
			var index = this.getItemIndex(item);
			if (this._maxRows && index!=-1) {
				this.setSelectedItem(index + ((actionCode == DwtKeyMap.PAGE_UP) ? -this._maxRows : this._maxRows));
			} else {
				this.setSelectedItem(actionCode == DwtKeyMap.PAGE_DOWN);
			}
			break;

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
				var mev = new DwtMouseEvent();
				this._setMouseEvent(mev, {dwtObj:this.__currentItem});
				this.notifyListeners(DwtEvent.ONMOUSEOUT, mev);
				this.__currentItem = null;
			}
			this.popdown(0);
			break;		
			
		default:
			return false;		
	}
	
	return true;
};

DwtMenu.prototype._focus =
function() {
	//DBG.println(AjxDebug.DBG1, "DwtMenu.prototype._focus");
};

DwtMenu.prototype._blur =
function() {
	//DBG.println(AjxDebug.DBG1, "DwtMenu.prototype._blur");
};



/**
 * This allows the caller to associate one object with the menu. Association
 * means, for events, treat the menu, and this object as one. If I click on
 * elements pertaining to this object, we will think of them as part of the
 * menu. 
 * @see _outsideMouseListener.
 * 
 * @private
 */
DwtMenu.prototype.setAssociatedObj =
function(dwtObj) {
	this._associatedObj = dwtObj;
};

DwtMenu.prototype.setAssociatedElementId =
function(id){
	this._associatedElId = id;
};

/**
 * Checks a menu item (the menu must be radio or checkbox style). The menu item
 * is identified through the given field/value pair.
 *
 * @param {Object}	field		a key for menu item data
 * @param {Object}	value		value for the data of the menu item to check
 * 
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
};

/**
 * Programmatically selects a menu item. The item can be specified with an index,
 * or as the next or previous item based on which item is currently selected. If
 * the new item is a separator or is disabled, it won't be selected. Instead, the
 * next suitable item will be used.
 * 
 * @param {boolean|number}	which		if <code>true</code>, selects the next menu item
 * 									if <code>false</code>, selects the previous menu item
 * 									if <code>DwtMenuItem</code>, select that menu item
 * 									if <code>int</code>, selects the menu item with that index
 */
DwtMenu.prototype.setSelectedItem =
function(which) {
	var currItem = this.__currentItem;
	if (typeof(which) == "boolean") {
		currItem = !currItem
			? this._children.get(0)
			: which ? this._children.getNext(currItem) : this._children.getPrev(currItem);
	} else if (which instanceof DwtMenuItem) {
		if (this._children.contains(which))
			currItem = which;
	} else {
		which = Math.max(0, Math.min(this._children.size()-1, which));
		currItem = this._children.get(which);
	}
	// While the current item is not enabled or is a separator, try another
	while (currItem) {
		if (!currItem.isStyle) { //this is not a DwtMenuItem. e.g. it's DwtFolderChooser
			currItem.focus();
			break;
		}
		else if (!currItem.isStyle(DwtMenuItem.SEPARATOR_STYLE) && currItem.getEnabled() && currItem.getVisible()) {
			break;
		}
		currItem = (which === false) ? this._children.getPrev(currItem) : this._children.getNext(currItem);
	}
	if (!currItem) { return; }

	// if we have a current item then we need to make sure we simulate a
	// mouseout event so that the UI can behave correctly
	var mev = new DwtMouseEvent();
	if (this.__currentItem) {
		this._setMouseEvent(mev, {dwtObj:this.__currentItem});
		this.__currentItem.notifyListeners(AjxEnv.isIE ? DwtEvent.ONMOUSELEAVE : DwtEvent.ONMOUSEOUT, mev);
	}
	this._setMouseEvent(mev, {dwtObj:currItem});
	currItem.notifyListeners(AjxEnv.isIE ? DwtEvent.ONMOUSEENTER : DwtEvent.ONMOUSEOVER, mev);	// mouseover selects a menu item
	this.__currentItem = currItem;
	this.scrollToItem(currItem, true);
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
	if (this._table) {
		if (this._style == DwtMenu.BAR_STYLE) {
			var cell = child.getHtmlElement().parentNode;
			this._table.rows[0].deleteCell(Dwt.getCellIndex(cell));
		} else {
			var el = child.getHtmlElement();
			if (el)
				this._table.deleteRow(el.parentNode.parentNode.rowIndex);
		}
	}
	this._children.remove(child);

    if (child.removeSelectionListener) {
        child.removeSelectionListener(this._itemSelectionListener);
    }
};

DwtMenu.prototype.addChild = 
function(child) {
    DwtComposite.prototype.addChild.apply(this, arguments);
    // Color pickers and calendars are not menu aware so we have to deal with
	// them acordingly
	if (Dwt.instanceOf(child, "DwtColorPicker") || Dwt.instanceOf(child, "DwtCalendar") ||
	    (this._style == DwtMenu.GENERIC_WIDGET_STYLE)) {
		
		this._addItem(child);
	}

    if (child.addSelectionListener) {
        child.addSelectionListener(this._itemSelectionListener);
    }
};

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
		spc.width = "7px";
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
};

DwtMenu.prototype._propagateItemSelection = function(evt) {
    if (this.isListenerRegistered(DwtEvent.SELECTION)) {
        this.notifyListeners(DwtEvent.SELECTION, evt);
    }
};

DwtMenu.prototype._menuHasCheckedItems =
function() {
	return this._menuItemsHaveChecks;
};

DwtMenu.prototype._menuHasItemsWithIcons =
function() {
	return this._menuItemsHaveIcons;
};

DwtMenu.prototype._menuHasSubmenus =
function() {
	return (this._menuItemsWithSubmenus > 0);
};

/* Once an icon is added to any menuItem, then the menu will be considered
 * to contain menu items with icons in perpetuity */
DwtMenu.prototype._iconItemAdded =
function(item) {
	if (!this._menuItemsHaveIcons) Dwt.addClass(this.getHtmlElement(), DwtMenu.HAS_ICON);
	this._menuItemsHaveIcons = true;
};

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
};

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
};

DwtMenu.prototype._popdownSubmenus = function() {
	var sz = this._children.size();
	var a = this._children.getArray();
	for (var i = 0; i < sz; i++) {
		if (a[i]._popdownMenu) a[i]._popdownMenu();
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

	this.render(x, y);

	var isScroll = this._layoutStyle == DwtMenu.LAYOUT_SCROLL;
	var isPopup = (this._style == DwtMenu.POPUP_STYLE || this._style == DwtMenu.DROPDOWN_STYLE);
	var isCascade = this._layoutStyle == DwtMenu.LAYOUT_CASCADE;
	if (!isScroll) {
		this.setScrollStyle(isPopup && isCascade ? Dwt.CLIP : Dwt.SCROLL);
	} else if (this._tableContainer) {
		Dwt.setScrollStyle(this._tableContainer, Dwt.CLIP);
	}
	
	this.notifyListeners(DwtEvent.POPUP, this);

	// Hide the tooltip
	var tooltip = this.shell.getToolTip();
	if (tooltip) {
		tooltip.popdown();
	}

	// bump z-index if we're inside a dialog
	var zIndex = DwtBaseDialog.getActiveDialog() ? Dwt.Z_DIALOG_MENU : Dwt.Z_MENU;
	this.setZIndex(zIndex);
	this._popupActionId = -1;
	this._isPoppedUp = true;

	var omem = DwtOutsideMouseEventMgr.INSTANCE;
	var omemParams = {
		id:					"DwtMenu",
		obj:				this,
		outsideListener:	this._outsideListener
	}
	omem.startListening(omemParams);

	if (!DwtMenu._activeMenu) {
		DwtMenu._activeMenu = this;
		DwtMenu._activeMenuUp = true;
	}

	DwtMenu._activeMenuIds.add(this._htmlElId, null, true);
	DwtMenu._activeMenuIds.sort();	
	DwtMenu._activeMenus.add(this, null, true);

	// Put our tabgroup in play
	if (!this.__preventMenuFocus) {
		DwtShell.getShell(window).getKeyboardMgr().pushTabGroup(this._tabGroup);
	}
	
	/* If the popup was keyboard generated, then pick the first enabled child item
	 * we do this by simulating a DwtKeyMap.SELECT_NEXT keyboard action */
	if (kbGenerated) {
	 	this.handleKeyAction(DwtKeyMap.SELECT_NEXT);
	} else if (this.__currentItem) {
		this.setSelectedItem(this.__currentItem);
	}
};

DwtMenu.prototype.getSize =
function(incScroll) {
	var size;
	if (this._table) {
		size = Dwt.getSize(this._table, incScroll);
	} else {
		size = DwtComposite.prototype.getSize.call(this, incScroll);
	}
	if (this._width && this._width > size.x) size.x = this._width;
	return size;
};

DwtMenu.prototype._doPopdown =
function(ev) {
	// Notify all sub menus to pop themselves down
	var a = this._children.getArray();
	var s = this._children.size();
	for (var i = 0; i < s; i++) {
		if ((a[i] instanceof DwtMenuItem) && !(a[i].isStyle(DwtMenuItem.SEPARATOR_STYLE))) {
			a[i]._popdownMenu();
		}
	}
	this.setZIndex(Dwt.Z_HIDDEN);
	this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	this._ev = ev;

	this.notifyListeners(DwtEvent.POPDOWN, this);

	var omem = DwtOutsideMouseEventMgr.INSTANCE;
	omem.stopListening({id:"DwtMenu", obj:this});

	if (DwtMenu._activeMenu == this) {
		DwtMenu._activeMenu = null;
		DwtMenu._activeMenuUp = false;
	}
	DwtMenu._activeMenuIds.remove(this._htmlElId);
	DwtMenu._activeMenus.remove(this);
	this._popdownActionId = -1;
	this._isPoppedUp = false;

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

	// Undo highlight if there's a hovered-over item
	if (this._hoveredItem) {
		var ev = new DwtMouseEvent();
		ev.dwtObj = this._hoveredItem;
		DwtButton._mouseOutListener(ev);
	}
	
	// Take our tabgroup out of play
	DwtShell.getShell(window).getKeyboardMgr().popTabGroup(this._tabGroup);	
};

DwtMenu.prototype._getActiveItem = 
function(){
	var a = this._children.getArray();
	var s = this._children.size();
	for (var i = 0; i < s; i++) {
		if (a[i]._isMenuPoppedUp())
			return a[i];
	}
	return null;
};

/* Note that a hack has been added to DwtHtmlEditor to call this method when the 
 * editor gets focus. The reason for this is that the editor uses an Iframe 
 * whose events are independent of the menu's document. In this case event will 
 * be null.
 */
DwtMenu._outsideMouseDownListener =
function(ev) {

	if (DwtMenu._activeMenuUp) {
		var menu = DwtMenu._activeMenu;

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
		menu.popdown(0, ev);
		
		//it should remove all the active menus 
		var cMenu = null ;
		do {
			cMenu = DwtMenu._activeMenus.getLast();
			if (cMenu!= null && cMenu instanceof DwtMenu) cMenu.popdown();
		} while (cMenu != null) ;
	}
};

DwtMenu._stopEvent = function(e) {
	if (!e) e = window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) {
		e.stopPropagation();
	}
};

/*
* Returns true if any menu is currently popped up.
*/
DwtMenu.menuShowing =
function() {
	return DwtMenu._activeMenuUp;
};

DwtMenu.closeActiveMenu =
function(ev) {
	if (DwtMenu._activeMenuUp) {
		DwtMenu._activeMenu.popdown(0, ev);
	}
};
