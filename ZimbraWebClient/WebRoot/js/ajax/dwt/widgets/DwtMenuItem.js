
/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
 * Creates a menu item.
 * @constructor
 * @class
 * Menu items can be part of a radio group, or can be checked style menu items.
 *
 * @author Ross Dargahi
 * 
 * @param {hash}	params		a hash of parameters
 * @param  {DwtComposite}     params.parent		the parent widget
 * @param  {constant}     params.style			the menu item style
 * @param  {string}     params.radioGroupId 	the radio group that the menu item is part of
 * @param  {number}     params.index 			the position in menu
 * @param  {string}     params.className		the CSS class
 * @param  {constant}     params.posStyle		the positioning style (see {@link DwtControl})
 * @param  {string}     params.id			an explicit ID to use for the control's HTML element
 * 
 * @extends		DwtButton
 */
DwtMenuItem = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtMenuItem.PARAMS);

	// check parameters
	var parent = params.parent;
	if (!(parent && parent.isDwtMenu)) {
		throw new DwtException("Parent must be a DwtMenu object", DwtException.INVALIDPARENT, "DwtMenuItem");
	}

	var style = params.style = params.style || DwtMenuItem.NO_STYLE;
	if (parent._style == DwtMenu.BAR_STYLE && style != DwtMenuItem.PUSH_STYLE) {
		throw new DwtException("DwtMenuItemInit: invalid style", DwtException.INVALID_PARAM, "DwtMenuItem");
	}

	// call super constructor
	style &= ~DwtLabel.IMAGE_RIGHT; // remove image right style
	style |= DwtButton.ALWAYS_FLAT | DwtLabel.IMAGE_LEFT; // set default styles
	params.className = (style & DwtMenuItem.SEPARATOR_STYLE)
		? "ZMenuItemSeparator" : (params.className || "ZMenuItem");
	params.listeners = DwtMenuItem._listeners;
	DwtButton.call(this, params);

	this.setDropDownImages("Cascade", "Cascade", "Cascade", "Cascade");
	this._radioGroupId = params.radioGroupId;

	// add this item at the specified index
	if (parent._addItem) {
		parent._addItem(this, params.index);
	}

	// add listeners if not menu item separator
	if (!(style & DwtMenuItem.SEPARATOR_STYLE)) {
		this.addSelectionListener(this.__handleItemSelect.bind(this));
	}
};

DwtMenuItem.PARAMS = ["parent", "style", "radioGroupId", "index", "className", "posStyle", "id"];

DwtMenuItem.prototype = new DwtButton;
DwtMenuItem.prototype.constructor = DwtMenuItem;

DwtMenuItem.prototype.isDwtMenuItem = true;
DwtMenuItem.prototype.toString = function() { return "DwtMenuItem"; };

//
// Constants
//

DwtMenuItem.CHECKED			= 1;
DwtMenuItem.UNCHECKED		= 2;

/**
 * Defines the "no" style.
 */
DwtMenuItem.NO_STYLE		= 0;
/**
 * Defines the "check" style.
 */
DwtMenuItem.CHECK_STYLE		= DwtButton._LAST_STYLE * 2;
/**
 * Defines the "radio" style.
 */
DwtMenuItem.RADIO_STYLE		= DwtButton._LAST_STYLE * 4;
/**
 * Defines the "separator" style.
 */
DwtMenuItem.SEPARATOR_STYLE = DwtButton._LAST_STYLE * 8;
/**
 * Defines the "cascade" style.
 */
DwtMenuItem.CASCADE_STYLE	= DwtButton._LAST_STYLE * 16;
/**
 * Defines the "push" style.
 */
DwtMenuItem.PUSH_STYLE		= DwtButton._LAST_STYLE * 32;
/**
 * Defines the "select" style.
 */
DwtMenuItem.SELECT_STYLE	= DwtButton._LAST_STYLE * 64;
DwtMenuItem._LAST_STYLE		= DwtMenuItem.SELECT_STYLE;

DwtMenuItem._MENU_POPUP_DELAY	= 250;
DwtMenuItem._MENU_POPDOWN_DELAY	= 250;

//
// Data
//

DwtMenuItem.prototype.TEMPLATE					= "dwt.Widgets#ZMenuItem";
DwtMenuItem.prototype.SEPARATOR_TEMPLATE		= "dwt.Widgets#ZMenuItemSeparator";
DwtMenuItem.prototype.BLANK_CHECK_TEMPLATE		= "dwt.Widgets#ZMenuItemBlankCheck";
DwtMenuItem.prototype.BLANK_ICON_TEMPLATE		= "dwt.Widgets#ZMenuItemBlankIcon";
DwtMenuItem.prototype.BLANK_CASCADE_TEMPLATE	= "dwt.Widgets#ZMenuItemBlankCascade";

//
// Public methods
//
DwtMenuItem.prototype.dispose =
function() {
	delete this._checkEl;
	DwtButton.prototype.dispose.call(this);
};

/**
 * Creates the menu item.
 * 
 * @param	{hash}	params		a hash of parameters
 */
DwtMenuItem.create =
function(params) {
	var mi = new DwtMenuItem(params);
	if (params.imageInfo) {
		mi.setImage(params.imageInfo);
	}
	if (params.text) {
		mi.setText(params.text);
	}
	mi.setEnabled(params.enabled !== false);
	return mi;
};

/**
 * Gets the checked flag.
 * 
 * @return	{boolean}	<code>true</code> if the item is checked
 */
DwtMenuItem.prototype.getChecked =
function() {
	return this._itemChecked;
};

/**
 * Sets the checked flag.
 * 
 * @param	{boolean}	checked			if <code>true</code>, check the item
 * @param	{boolean}	skipNotify		if <code>true</code>, do not notify listeners
 */
DwtMenuItem.prototype.setChecked =
function(checked, skipNotify) {
	this._setChecked(checked, null, skipNotify);
	this.parent._checkItemAdded(this);
};

DwtMenuItem.prototype.setImage =
function(imageInfo) {
	DwtButton.prototype.setImage.call(this, imageInfo);
	this.parent._iconItemAdded(this);
};

DwtMenuItem.prototype.setText =
function(text) {
	DwtButton.prototype.setText.call(this, text);
	if (this.parent.isPoppedUp()) {
		// resize menu if we reset text on the fly
		this.parent.render();
	}
};

DwtMenuItem.prototype.setMenu =
function(params) {
	var params = Dwt.getParams(arguments, DwtButton.setMenuParams);
	DwtButton.prototype.setMenu.call(this, params);
	this.parent._submenuItemAdded(this);
};

DwtMenuItem.prototype.setHoverDelay =
function(delay) {
	this._hoverDelay = delay;
};

DwtMenuItem.prototype.setShortcut =
function(shortcut) {
	if (shortcut && this._dropDownEl) {
		this._dropDownEl.innerHTML = shortcut;
	}
};

// Set whether the item is selectable even when it has an open submenu
DwtMenuItem.prototype.setSelectableWithSubmenu =
function(selectable) {
	this._selectableWithSubmenu = selectable;
};

DwtMenuItem.prototype.isSeparator =
function() {
	return Boolean(this._style & DwtMenuItem.SEPARATOR_STYLE);
};

//
// Protected methods
//

DwtMenuItem.prototype._createHtml =
function(templateId) {
	var defaultTemplate = this.isSeparator() ? this.SEPARATOR_TEMPLATE : this.TEMPLATE;
	DwtButton.prototype._createHtml.call(this, templateId || defaultTemplate);
};

DwtMenuItem.prototype._createHtmlFromTemplate =
function(templateId, data) {
	DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);
	this._checkEl = document.getElementById(data.id+"_check");
};

DwtMenuItem.prototype._setChecked =
function(checked, ev, skipNotify) {
	var isCheck = this._style & DwtMenuItem.CHECK_STYLE;
	var isRadio = this._style & DwtMenuItem.RADIO_STYLE;
	if ((isCheck || isRadio) && this._itemChecked != checked) {
		this._itemChecked = checked;

		if (this._checkEl) {
			this._checkEl.innerHTML = "";
			var icon = checked ? (isCheck ? "MenuCheck" : "MenuRadio") : "Blank_9";
			AjxImg.setImage(this._checkEl, icon);
			if (checked) {
				// deselect currently selected radio button
				if (isRadio) {
					this.parent._radioItemSelected(this, skipNotify);
				}
			}
		}
	}
};

DwtMenuItem.prototype._checkItemAdded = function(className) {};
DwtMenuItem.prototype._checkedItemsRemoved = function() {};

DwtMenuItem.prototype._submenuItemAdded =
function() {
	if (this.isSeparator()) { return; }

	if (this._cascCell == null) {
		this._cascCell = this._row.insertCell(-1);
		this._cascCell.noWrap = true;
	}
};

DwtMenuItem.prototype._submenuItemRemoved =
function() {
	if (this._dropDownEl) {
		this._dropDownEl.innerHTML = "";
	}
};

DwtMenuItem.prototype._popupMenu =
function(delay, kbGenerated) {
	var menu = this.getMenu();
	var pp = this.parent.parent;
	var pb = this.getBounds();
	var ws = menu.shell.getSize();
	var s = menu.getSize();
	var x;
	var y;
	var vBorder;
	var hBorder;
	var ppHtmlElement = pp.getHtmlElement();
	if (pp._style == DwtMenu.BAR_STYLE) {
		vBorder = (ppHtmlElement.style.borderLeftWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderLeftWidth);
		x = pb.x + vBorder;
		hBorder = (ppHtmlElement.style.borderTopWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderTopWidth);
		hBorder += (ppHtmlElement.style.borderBottomWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderBottonWidth);
		y = pb.y + pb.height + hBorder;		
		x = ((x + s.x) >= ws.x) ? x - (x + s.x - ws.x): x;
	}
	else { // Drop Down
		vBorder = (ppHtmlElement.style.borderLeftWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderLeftWidth);
		vBorder += (ppHtmlElement.style.borderRightWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderRightWidth);
		x = pb.x + pb.width + vBorder;
		hBorder = (ppHtmlElement.style.borderTopWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderTopWidth);
		y = pb.y + hBorder;
        if (menu.centerOnParentVertically()) {
            y += pb.height / 2;
        }
		//x = ((x + s.x) >= ws.x) ? pb.x - s.x - vBorder : x;
	}
	menu.popup(delay, x, y, kbGenerated);
};

DwtMenuItem.prototype._popdownMenu =
function() {
	var menu = this.getMenu();
	if (menu) {
		menu.popdown();
	}
};

DwtMenuItem.prototype._isMenuPoppedUp =
function() {
	var menu = this.getMenu();
	return (menu && menu.isPoppedUp());
};


//
// Private methods
//

DwtMenuItem.prototype.__handleItemSelect =
function(event) {
	this.setDisplayState(DwtControl.NORMAL);
	if (this.isStyle(DwtMenuItem.CHECK_STYLE)) {
		this._setChecked(!this._itemChecked, null, true);
		event.detail = this.getChecked() ? DwtMenuItem.CHECKED : DwtMenuItem.UNCHECKED;
	}
	else if (this.isStyle(DwtMenuItem.RADIO_STYLE)) {
		this._setChecked(true, true);
		this.parent._radioItemSelected(this, true);
		event.detail = this.getChecked() ? DwtMenuItem.CHECKED : DwtMenuItem.UNCHECKED;
	}
	else if (this.isStyle(DwtMenuItem.PUSH_STYLE)) {
		if (this._menu) {
			if (this._isMenuPoppedUp()) {
				DwtMenu.closeActiveMenu(event);
			}
			else {
				this._popupMenu();
			}
		}
		return;
	}
	if (!this.isStyle(DwtMenuItem.CASCADE_STYLE)) {
		if (this._selectableWithSubmenu || !this._menu || !this._menu.isPoppedUp || !this._menu.isPoppedUp()) {
			DwtMenu.closeActiveMenu(event);
		}
	}
};

DwtMenuItem._mouseOverListener =
function(ev) {
	var menuItem = ev.dwtObj;
	if (!menuItem) { return false; }
	var menu = menuItem.parent;
	if (menu._hoveredItem) {
		var mouseEv = new DwtMouseEvent();
		mouseEv.dwtObj = menu._hoveredItem;
		DwtButton._mouseOutListener(mouseEv);
	}
	if (menuItem.isSeparator()) { return false; }
	DwtButton._mouseOverListener(ev, menuItem);
	menu._hoveredItem = menuItem;
	menu._popdownSubmenus();
	if (menuItem._menu && !ev.ersatz) {
		menuItem._popupMenu(menuItem._hoverDelay);
	}
};

DwtMenuItem._mouseOutListener =
function(ev) {
	var menuItem = ev.dwtObj;
	var submenu = menuItem && menuItem.getMenu();
	if (submenu && submenu.isPoppedUp()) { return; }
	DwtButton._mouseOutListener(ev);
	if (menuItem) {
		menuItem.parent._hoveredItem = null;
	}
};

/*
 * returns menu item table row element
 */
DwtMenuItem.prototype.getRowElement =
function() {
   var el = this._textEl ? this._textEl : this._iconEl ? this._iconEl : this._dropDownEl;
   if (el)
    return el.parentNode;
};

DwtMenuItem._listeners = {};
DwtMenuItem._listeners[DwtEvent.ONMOUSEOVER]	= DwtMenuItem._mouseOverListener.bind();
DwtMenuItem._listeners[DwtEvent.ONMOUSEOUT]		= DwtMenuItem._mouseOutListener.bind();
DwtMenuItem._listeners[DwtEvent.ONMOUSEDOWN]	= DwtButton._mouseDownListener.bind();
DwtMenuItem._listeners[DwtEvent.ONMOUSEUP]		= DwtButton._mouseUpListener.bind();
DwtMenuItem._listeners[DwtEvent.ONMOUSEENTER]	= DwtMenuItem._mouseOverListener.bind();
DwtMenuItem._listeners[DwtEvent.ONMOUSELEAVE]	= DwtButton._mouseOutListener.bind();
