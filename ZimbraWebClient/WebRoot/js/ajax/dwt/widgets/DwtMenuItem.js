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
* @param parent			the parent widget
* @param style 			menu item's style
* @param radioGroupId 	radio group that the menu item is part of
* @param index 			position in menu
* @param className		a CSS class
* @param posStyle		positioning style
*/
DwtMenuItem = function(parent, style, radioGroupId, index, className, posStyle) {
    if (arguments.length == 0) return;

    // check parameters
    if (!(parent instanceof DwtMenu))
		throw new DwtException("Parent must be a DwtMenu object", DwtException.INVALIDPARENT, "DwtMenuItem");
		
	style = style != null ? style : DwtMenuItem.NO_STYLE;
	if (parent._style == DwtMenu.BAR_STYLE && style != DwtMenuItem.PUSH_STYLE)
		throw new DwtException("DwtMenuItemInit: invalid style", DwtException.INVALID_PARAM, "DwtMenuItem"); 

    // call super constructor
    style &= ~DwtLabel.IMAGE_RIGHT; // remove image right style
    style |= DwtButton.ALWAYS_FLAT | DwtLabel.IMAGE_LEFT; // set default styles
    className = style & DwtMenuItem.SEPARATOR_STYLE ? "ZMenuItemSeparator" : (className || "ZMenuItem");
    DwtButton.call(this, parent, style, className, posStyle);

    this.setDropDownImages("Cascade", "Cascade", "Cascade", "Cascade");
    this._radioGroupId = radioGroupId;

    // add this item at the specified index
    if (parent._addItem) {
		parent._addItem(this, index);
    }

    // add listeners if not menu item separator
	if (!(style & DwtMenuItem.SEPARATOR_STYLE)) {
		this._subMenuMouseOverListener = new AjxListener(this, this.__handleSubMenuMouseOver);
		this.addSelectionListener(new AjxListener(this, this.__handleItemSelect));
	}
}

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
DwtMenuItem.create =
function(parent, imageInfo, text, disImageInfo, enabled, style, radioGroupId, idx, className, posStyle) {
	var mi = new DwtMenuItem(parent, style, radioGroupId, idx, className, posStyle);
	if (imageInfo)
		mi.setImage(imageInfo);
	if (text)
		mi.setText(text);
	mi.setEnabled(enabled !== false);
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

DwtMenuItem.prototype._setMouseEvents =
function() {
	// onlyset mouse events for non-separator style
	if (!(this._style & DwtMenuItem.SEPARATOR_STYLE)) {
		DwtButton.prototype._setMouseEvents.call(this);
	}
};

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

DwtMenuItem.prototype._mouseOverListener = function(ev) {
    DwtButton.prototype._mouseOverListener.call(this, ev);

    this.parent._popdownSubmenus();
    if (this._menu) {
        this._popupMenu(this._hoverDelay);
    }
};

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
