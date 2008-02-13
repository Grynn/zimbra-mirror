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
 * Creates a menu item. Menu items can be part of a radio group, or can be checked style menu items
 *
 * @constructor
 * @class
 *
 * @author Ross Dargahi
 * 
 * @param params		[hash]				hash of params:
 *        parent		[DwtComposite] 		parent widget
 *        style			[constant]*			menu item style
 *        radioGroupId 	[string]*			radio group that the menu item is part of
 *        index 		[int]*				position in menu
 *        className		[string]*			CSS class
 *        posStyle		[constant]*			positioning style
 */
DwtMenuItem = function(params) {
    if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtMenuItem.PARAMS);

    // check parameters
    var parent = params.parent;
    if (!(parent instanceof DwtMenu)) {
		throw new DwtException("Parent must be a DwtMenu object", DwtException.INVALIDPARENT, "DwtMenuItem");
    }
		
	var style = params.style = params.style || DwtMenuItem.NO_STYLE;
	if (parent._style == DwtMenu.BAR_STYLE && style != DwtMenuItem.PUSH_STYLE) {
		throw new DwtException("DwtMenuItemInit: invalid style", DwtException.INVALID_PARAM, "DwtMenuItem");
	}

    // call super constructor
    style &= ~DwtLabel.IMAGE_RIGHT; // remove image right style
    style |= DwtButton.ALWAYS_FLAT | DwtLabel.IMAGE_LEFT; // set default styles
    params.className = (style & DwtMenuItem.SEPARATOR_STYLE) ?
    					"ZMenuItemSeparator" : (params.className || "ZMenuItem");
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
		this._subMenuMouseOverListener = new AjxListener(this, this.__handleSubMenuMouseOver);
		this.addSelectionListener(new AjxListener(this, this.__handleItemSelect));
	}
}

DwtMenuItem.PARAMS = ["parent", "style", "radioGroupId", "index", "className", "posStyle"];

DwtMenuItem.prototype = new DwtButton;
DwtMenuItem.prototype.constructor = DwtMenuItem;

DwtMenuItem.prototype.toString = 
function() {
	return "DwtMenuItem";
}

//
// Constants
//

DwtMenuItem.CHECKED = 1;
DwtMenuItem.UNCHECKED = 2;

DwtMenuItem.NO_STYLE = 0;
DwtMenuItem.CHECK_STYLE = DwtButton._LAST_STYLE * 2;
DwtMenuItem.RADIO_STYLE = DwtButton._LAST_STYLE * 4;
DwtMenuItem.SEPARATOR_STYLE = DwtButton._LAST_STYLE * 8;
DwtMenuItem.CASCADE_STYLE = DwtButton._LAST_STYLE * 16;
DwtMenuItem.PUSH_STYLE = DwtButton._LAST_STYLE * 32;
DwtMenuItem.SELECT_STYLE = DwtButton._LAST_STYLE * 64;

DwtMenuItem._LAST_STYLE = DwtMenuItem.SELECT_STYLE; 

DwtMenuItem._MENU_POPUP_DELAY = 250;
DwtMenuItem._MENU_POPDOWN_DELAY = 250

/***
DwtMenuItem._evt = new DwtUiEvent(true);
/***/

//
// Data
//

DwtMenuItem.prototype.TEMPLATE = "dwt.Widgets#ZMenuItem";

DwtMenuItem.prototype.SEPARATOR_TEMPLATE = "dwt.Widgets#ZMenuItemSeparator";

DwtMenuItem.prototype.BLANK_CHECK_TEMPLATE = "dwt.Widgets#ZMenuItemBlankCheck";
DwtMenuItem.prototype.BLANK_ICON_TEMPLATE = "dwt.Widgets#ZMenuItemBlankIcon";
DwtMenuItem.prototype.BLANK_CASCADE_TEMPLATE = "dwt.Widgets#ZMenuItemBlankCascade";

//
// Public methods
//
DwtMenuItem.prototype.dispose =
function() {
	delete this._checkEl;
	DwtButton.prototype.dispose.call(this);
};

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
}

DwtMenuItem.prototype.getChecked =
function() {
	return this._itemChecked;
}

DwtMenuItem.prototype.setChecked =
function(checked, skipNotify) {
	this._setChecked(checked, null, skipNotify);
	this.parent._checkItemAdded(this);
}

DwtMenuItem.prototype.setImage = function(imageInfo) {
	DwtButton.prototype.setImage.call(this, imageInfo);
	this.parent._iconItemAdded(this);
}

DwtMenuItem.prototype.setMenu = function(menuOrCallback, shouldToggle, followIconStyle) {
	DwtButton.prototype.setMenu.call(this, menuOrCallback, shouldToggle, followIconStyle);
	this.parent._submenuItemAdded(this);
}

DwtMenuItem.prototype.setHoverDelay =
function(delay) {
	this._hoverDelay = delay;
};

//
// Protected methods
//

DwtMenuItem.prototype._createHtml = function(templateId) {
    var defaultTemplate = this._style & DwtMenuItem.SEPARATOR_STYLE ? this.SEPARATOR_TEMPLATE : this.TEMPLATE;
    DwtButton.prototype._createHtml.call(this, templateId || defaultTemplate);
};

DwtMenuItem.prototype._createHtmlFromTemplate = function(templateId, data) {
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

				/***
				// notify listeners
				if (this.isListenerRegistered(DwtEvent.ONCHANGE)) {
					var event = DwtMenuItem._evt;
					event._args = {
						selectObj: this,
						newValue: checked,
						oldValue: !checked
					};
					this.notifyListeners(DwtEvent.ONCHANGE, event);
				}
				/***/

                // follow icon
                var gp = this.parent.parent ? this.parent.parent : null;
                if (gp && (gp instanceof DwtButton) && (gp._followIconStyle == this._style)) {
                    gp.setImage(this._imageInfo);
                }
            }
        }
	}
}

DwtMenuItem.prototype._addIconCell = function() {
//    this.setImage(this.getImage());
};

DwtMenuItem.prototype._checkItemAdded = function(className) {};
DwtMenuItem.prototype._checkedItemsRemoved = function() {}

DwtMenuItem.prototype._submenuItemAdded =
function() {
	if (this._style & DwtMenuItem.SEPARATOR_STYLE) return;

    if (this._cascCell == null) {
        this._cascCell = this._row.insertCell(-1);
        this._cascCell.noWrap = true;
        this._cascCell.style.width = DwtMenuItem._CASCADE_DIM;
        this._cascCell.style.height = (this._style != DwtMenuItem.SEPARATOR_STYLE) ?  DwtMenuItem._CASCADE_DIM : DwtMenuItem._SEPAARATOR_DIM;
    }
};

DwtMenuItem.prototype._submenuItemRemoved = function() {
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
		//y = ((y + s.y) >= ws.y) ? y - (y + s.y - ws.y) : y;
	} else { // Drop Down
		vBorder = (ppHtmlElement.style.borderLeftWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderLeftWidth);
		vBorder += (ppHtmlElement.style.borderRightWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderRightWidth);
		x = pb.x + pb.width + vBorder;
		hBorder = (ppHtmlElement.style.borderTopWidth == "") ? 0 : parseInt(ppHtmlElement.style.borderTopWidth);
		y = pb.y + hBorder;
		x = ((x + s.x) >= ws.x) ? pb.x - s.x - vBorder: x;
		//y = ((y + s.y) >= ws.y) ? y - (y + s.y - ws.y) : y;
	}
	//this.setLocation(x, y);
    menu.addListener(DwtEvent.ONMOUSEOVER, this._subMenuMouseOverListener);
    menu.popup(delay, x, y, kbGenerated);
};

DwtMenuItem.prototype._popdownMenu =
function() {
    var menu = this.getMenu();
    if (menu) {
        menu.popdown();
        menu.removeListener(DwtEvent.ONMOUSEOVER, this._subMenuMouseOverListener);
    }
};

DwtMenuItem.prototype._isMenuPoppedup =
function() {
	var menu = this.getMenu();
	return (menu && menu.isPoppedup()) ? true : false;
}


//
// Private methods
//

DwtMenuItem.prototype.__handleItemSelect = function(event) {
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
                DwtMenu.closeActiveMenu();
            }
            else {
                this._popupMenu();
            }
        }
        return;
    }
    if (!this.isStyle(DwtMenuItem.CASCADE_STYLE)) {
		if (!this._menu || !this._menu.isPoppedup || !this._menu.isPoppedup()) {
			DwtMenu.closeActiveMenu();
		}
    }
};

DwtMenuItem.prototype.__handleSubMenuMouseOver = function(event) {
    this.setDisplayState(DwtControl.HOVER);
};

DwtMenuItem._mouseOverListener =
function(ev) {
	var menuItem = ev.dwtObj;
	if (!menuItem) { return false; }
	if (menuItem._style & DwtMenuItem.SEPARATOR_STYLE) { return false; }
    DwtButton._mouseOverListener(ev, menuItem);
    menuItem.parent._popdownSubmenus();
    if (menuItem._menu) {
        menuItem._popupMenu(menuItem._hoverDelay);
    }
};

DwtMenuItem._listeners = {};
DwtMenuItem._listeners[DwtEvent.ONMOUSEOVER] = new AjxListener(null, DwtMenuItem._mouseOverListener);
DwtMenuItem._listeners[DwtEvent.ONMOUSEOUT] = new AjxListener(null, DwtButton._mouseOutListener);
DwtMenuItem._listeners[DwtEvent.ONMOUSEDOWN] = new AjxListener(null, DwtButton._mouseDownListener);
DwtMenuItem._listeners[DwtEvent.ONMOUSEUP] = new AjxListener(null, DwtButton._mouseUpListener);
DwtMenuItem._listeners[DwtEvent.ONMOUSEENTER] = new AjxListener(null, DwtMenuItem._mouseOverListener);
DwtMenuItem._listeners[DwtEvent.ONMOUSELEAVE] = new AjxListener(null, DwtButton._mouseOutListener);

