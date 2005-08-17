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
function DwtMenuItem(parent, style, radioGroupId, index, className, posStyle) {

	className = className || "DwtMenuItem";
	DwtComposite.call(this, parent, className, posStyle);

	if (!(parent instanceof DwtMenu))
		throw new DwtException("Parent must be a DwtMenu object", DwtException.INVALIDPARENT, "DwtMenuItem");
		
	this._style = style ? style : DwtMenuItem.CASCADE_STYLE;
	if (parent._style == DwtMenu.BAR_STYLE && this._style != DwtMenuItem.PUSH_STYLE)
		throw new DwtException("DwtMenuItemInit: invalid style", DwtException.INVALID_PARAM, "DwtMenuItem"); 

	this._setMouseEventHdlrs();
	this._setKeyEventHdlrs();
	this._origClassName = className;
	this._selectedClassName = className + "-" + DwtCssStyle.SELECTED;
	this._triggeredClassName = className + "-" + DwtCssStyle.TRIGGERED;
	this._iconAreaClassName = className + "-IconArea";
	this._iconAreaSelClassname = this._iconAreaClassName + "-" + DwtCssStyle.SELECTED;
	this._checkedAreaClassName = className + "-CheckedArea";
	this._checkedAreaSelClassname = this._checkedAreaClassName + "-" + DwtCssStyle.SELECTED;
	this._table = this.getDocument().createElement("table");
	this._table.cellSpacing = this._table.cellPadding = 0;
	this._table.border = 0;
	this._table.borderCollapse = "separate";
	this._row = this._table.insertRow(0);

	switch (this._style) {
		case DwtMenuItem.SEPARATOR_STYLE:
			this._createSeparatorStyle();
			break;
		case DwtMenuItem.PUSH_STYLE:
			this._createPushStyle();
			break;
		case DwtMenuItem.RADIO_STYLE:
		case DwtMenuItem.CHECK_STYLE:
			this._createCheckedStyle(radioGroupId);
			break;
	    case DwtMenuItem.SELECT_STYLE:
			this._createSimpleStyle();
			break;
		default:
			this._createCascadeStyle();
	}

	this._mouseOverListener = new LsListener(this, DwtMenuItem.prototype._mouseOverListener);
	this._mouseOutListener = new LsListener(this, DwtMenuItem.prototype._mouseOutListener);
	this._mouseUpListener = new LsListener(this, DwtMenuItem.prototype._mouseUpListener);
	this._mouseDownListener = new LsListener(this, DwtMenuItem.prototype._mouseDownListener);
	this.addListener(DwtEvent.ONMOUSEOVER, this._mouseOverListener);
	this.addListener(DwtEvent.ONMOUSEOUT, this._mouseOutListener);
	this.addListener(DwtEvent.ONMOUSEUP, this._mouseUpListener);
	this.addListener(DwtEvent.ONMOUSEDOWN, this._mouseDownListener);
	this.getHtmlElement().appendChild(this._table);
	if (parent._addItem)
		parent._addItem(this, index);
	this.setCursor("default");
	this._menu = null;
	this._menuDisposeListener = new LsListener(this, DwtMenuItem.prototype._menuDisposed)
}

DwtMenuItem.prototype = new DwtComposite;
DwtMenuItem.prototype.constructor = DwtMenuItem;

DwtMenuItem.prototype.toString = 
function() {
	return "DwtMenuItem";
}

DwtMenuItem.CHECKED = 1;
DwtMenuItem.UNCHECKED = 2;

DwtMenuItem.NO_STYLE = 0;
DwtMenuItem.CHECK_STYLE = 1;
DwtMenuItem.RADIO_STYLE = 2;
DwtMenuItem.SEPARATOR_STYLE = 3;
DwtMenuItem.CASCADE_STYLE = 4;
DwtMenuItem.PUSH_STYLE = 5;
DwtMenuItem.SELECT_STYLE = 6;

DwtMenuItem._IMAGECELL_DIM = "22px";
DwtMenuItem._CASCADE_DIM = "16px";
DwtMenuItem._CHECKEDCELL_DIM = "13px";
DwtMenuItem._FILLCELL_DIM = "7px";
DwtMenuItem._MENU_POPUP_DELAY = 250;
DwtMenuItem._MENU_POPDOWN_DELAY = 250

DwtMenuItem.create =
function(parent, imageInfo, text, disImageInfo, enabled, style, radioGroupId, idx, className, posStyle) {
	var mi = new DwtMenuItem(parent, style, radioGroupId, idx, className, posStyle);
	if (imageInfo)
		mi.setImage(imageInfo);
	if (text)
		mi.setText(text);
	if (disImageInfo)
		mi.setDisabledImage(disImageInfo);
	mi.setEnabled(enabled !== false);
	return mi;
}


DwtMenuItem.prototype.addSelectionListener = 
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
}

DwtMenuItem.prototype.removeSelectionListener = 
function(listener) {
  this.removeListener(DwtEvent.SELECTION, listener);     	
}

DwtMenuItem.prototype.getChecked =
function() {
	return this._itemChecked;
}

DwtMenuItem.prototype.setChecked =
function(checked, skipNotify) {
	this._setChecked(checked, null, skipNotify);
}

DwtMenuItem.prototype._setChecked =
function(checked, ev, skipNotify) {
	if ((this._style == DwtMenuItem.CHECK_STYLE || this._style == DwtMenuItem.RADIO_STYLE)
		&& this._itemChecked != checked) {
		this._itemChecked = checked;
		
		if (checked) {
			if (this._style == DwtMenuItem.CHECK_STYLE) {
				LsImg.setImage(this._checkedCell, DwtImg.MENU_CHECK);
			} else {
				LsImg.setImage(this._checkedCell, DwtImg.MENU_RADIO);
				// This will cause the parent menu to deselect the currently selected radio item
				this.parent._radioItemSelected(this, skipNotify);
			}
			var gp = this.parent.parent ? this.parent.parent : null;
			if (gp && (gp instanceof DwtButton) && (gp._followIconStyle == this._style))
				gp.setImage(this._imageInfo);
		} else {
			LsImg.setImage(this._checkedCell, DwtImg.BLANK9);
		}
		
		if (skipNotify) return;
		
		// If we are being called as a result of a UI action then ev will not be null and we ahve
		// to initialize our selection event based on the the event.
		var selEv = DwtShell.selectionEvent;
		if (ev)
			DwtUiEvent.copy(selEv, ev);
		else
			selEv.reset();
		selEv.item = this;
		selEv.detail = (checked) ? DwtMenuItem.CHECKED : DwtMenuItem.UNCHECKED;
		this.notifyListeners(DwtEvent.SELECTION, selEv);			
	}
}

DwtMenuItem.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtControl.prototype.setEnabled.call(this, enabled);
		if (enabled) {
			this.addListener(DwtEvent.ONMOUSEOVER, this._mouseOverListener);
			this.addListener(DwtEvent.ONMOUSEOUT, this._mouseOutListener);
			this.addListener(DwtEvent.ONMOUSEUP, this._mouseUpListener);
			this.addListener(DwtEvent.ONMOUSEDOWN, this._mouseDownListener);
			if (this._imageInfo)
				this._setImage(this._imageInfo);
			if (this._textCell)
				this._textCell.className = "Text";
		} else {
			this.removeListener(DwtEvent.ONMOUSEOVER, this._mouseOverListener);
			this.removeListener(DwtEvent.ONMOUSEOUT, this._mouseOutListener);
			this.removeListener(DwtEvent.ONMOUSEUP, this._mouseUpListener);
			this.removeListener(DwtEvent.ONMOUSEDOWN, this._mouseDownListener);
			if (this._disabledImageInfo)
				this._setImage(this._disabledImageInfo);
			if (this._textCell)
				this._textCell.className = "DisabledText";
		}
	}
}


DwtMenuItem.prototype.getDisabledImage =
function() {
	return this._disabledImage;
}

DwtMenuItem.prototype.setDisabledImage =
function(imageInfo) {
	this._disabledImageInfo = imageInfo;
	if (!this._enabled && imageInfo)
		this._setImage(imageInfo);
}

DwtMenuItem.prototype.getImage =
function() {
	return ((this._style == DwtMenuItem.CHECK_STYLE) == 0 && this._imageInfo)
		? this._imageInfo : null;
}

DwtMenuItem.prototype.setImage =
function(imageInfo) {
	this._imageInfo = imageInfo;
	if (this._enabled || (!this._enabled && !this._disabledImageInfo))
		this._setImage(imageInfo);
}

DwtMenuItem.prototype._setImage =
function(imageInfo) {
	if (this._style != DwtMenuItem.SEPARATOR_STYLE) {
		LsImg.setImage(this._iconCell, imageInfo);
	}
}

DwtMenuItem.prototype.getMenu =
function() {
	return this._menu;
}

DwtMenuItem.prototype.setMenu = 
function(menu) {
	if (this._menu == menu) {
		return;
	} else if (this._menu) {
		this._menu.removeDisposeListener(this._menuDisposeListener);
	}

	this._menu = menu;
	if (menu)
		this._menu.addDisposeListener(this._menuDisposeListener);
	
	if (this._style == DwtMenuItem.CASCADE_STYLE || this._style == DwtMenuItem.CHECK_STYLE
		|| this._style == DwtMenuItem.RADIO_STYLE) {
		if (menu) {
			LsImg.setImage(this._cascCell, DwtImg.CASCADE);
		} else if (!menu) {
			this._cascCell.innerHTML = "";
		}
	}
}

DwtMenuItem.prototype.setSize = 
function(width, height) {
	DwtComposite.prototype.setSize.call(this, width, height);
	if (width != DwtControl.DEFAULT) {
		width = (typeof(width) == "number") ? width + "px" : width;
		this._table.style.width = width;
	}
	if (height != DwtControl.DEFAULT) {
		height = (typeof(height) == "number") ? height + "px" : height;
		this._table.style.height = height;
	}
}

DwtMenuItem.prototype.getText =
function() {
	if ((this._style == DwtMenuItem.SEPARATOR_STYLE) != 0) return null;
	return this._textCell.innerHTML;
}

DwtMenuItem.prototype.setText =
function(text) {
	if ((this._style == DwtMenuItem.SEPARATOR_STYLE) != 0) return;
	this._textCell.innerHTML = text;
}

DwtMenuItem.prototype._createSeparatorStyle =
function() {
	// TODO - REMOVE MENU_BAR STUFF SINCE MENU BARS CANNOT HAVE SEPARATOR CHILDREN
	this._table.style.width = "100%";
	var i = 0;
	this._iconCell = this._row.insertCell(i++);
	this._iconCell.noWrap = true;
	this._iconCell.align = "center";
	this._iconCell.width = DwtMenuItem._IMAGECELL_DIM;
	this._iconCell.height = "1px"
	this._iconCell.className = this._iconAreaClassName;
	
	var fillCell = this._row.insertCell(i++);
	fillCell.width = DwtMenuItem._FILLCELL_DIM;
	fillCell.noWrap = true;
	
	fillCell = this._row.insertCell(i++);
	fillCell.noWrap = true;
	fillCell.style.width = "100%";
	fillCell.className = this._className + "-Separator";
	fillCell.style.height = "1px";
	
	if (this.parent._menuHasCheckedItems())
		this._checkItemAdded();
}

DwtMenuItem.prototype._createPushStyle =
function() {
	var i = 0;
	this._iconCell = this._row.insertCell(i++);
	this._iconCell.noWrap = true;
	this._iconCell.align = "center";
	this._iconCell.width = this._iconCell.height = DwtMenuItem._IMAGECELL_DIM;
	this._iconCell.className = this._iconAreaClassName;

	var fillCell = this._row.insertCell(i++);
	fillCell.width = DwtMenuItem._FILLCELL_DIM;
	fillCell.noWrap = true;
		
	this._textCell = this._row.insertCell(i++);
	this._textCell.noWrap = true;
	this._textCell.className = this._className + "-Text";
	
	fillCell = this._row.insertCell(i);
	fillCell.width = DwtMenuItem._FILLCELL_DIM;
	fillCell.noWrap = true;	
}

DwtMenuItem.prototype._createSimpleStyle =
function() {
	this._table.style.width = "100%";
	this._textCell = this._row.insertCell(-1);
	this._textCell.noWrap = true;
	this._textCell.className = this._className + "-Text";
	var fillCell = this._row.insertCell(-1);
	fillCell.className = this._className + "-LeftFill";
};

DwtMenuItem.prototype._createCascadeStyle =
function() {
	this._table.style.width = "100%";
	var i = 0;
	this._iconCell = this._row.insertCell(i++);
	this._iconCell.noWrap = true;
	this._iconCell.align = "center";
	this._iconCell.width = this._iconCell.height = DwtMenuItem._IMAGECELL_DIM;
	this._iconCell.className = this._iconAreaClassName;
	
	var fillCell = this._row.insertCell(i++);
	fillCell.width = DwtMenuItem._FILLCELL_DIM;
	fillCell.noWrap = true;
	
	this._textCell = this._row.insertCell(i++);
	this._textCell.noWrap = true;
	this._textCell.className = this._className + "-Text";
	
	this._cascCell = this._row.insertCell(i);
	this._cascCell.noWrap = true;
	this._cascCell.style.width = this._cascCell.style.height = DwtMenuItem._CASCADE_DIM;

	if (this.parent._menuHasCheckedItems())
		this._checkItemAdded();
}

DwtMenuItem.prototype._createCheckedStyle =
function(radioGroupId) {
	this._createCascadeStyle();
	this._checkItemAdded();
	this._radioGroupId = (radioGroupId != null) ? radioGroupId : 0;
	this._itemChecked = false;
}

/* This method is called by DwtMenuItem.prototype._createCheckedStyle when a check or radio style
 * menu item is being created. It is also called by DwtMenu._addItem when a check/radio style item
 * is added to the menu and it allows for the menu item to add a column so that it can align with
 * the new checked item */
DwtMenuItem.prototype._checkItemAdded =
function() {
	if (this._checkedCell == null) {
		this._checkedCell = this._row.insertCell(0);
		this._checkedCell.noWrap = true;
		this._checkedCell.align = "center";
		this._checkedCell.width = DwtMenuItem._CHECKEDCELL_DIM;
		this._checkedCell.height = (this._style != DwtMenuItem.SEPARATOR_STYLE) ?  DwtMenuItem._CHECKEDCELL_DIM : 1;
		this._checkedCell.className = this._checkedAreaClassName;
	}
}

/* This method is explicitly called by DwtMenu._removeChild when the last check/radio item is removed
 * from the menu. It allows for the item to remove its "bogus" check column*/
DwtMenuItem.prototype._checkedItemsRemoved =
function() {
	this._row.deleteCell(0);
}

DwtMenuItem.prototype._menuDisposed =
function(ev) {
	this.setMenu(null);
}

DwtMenuItem.prototype._popdownMenu =
function() {
	//if (this._menu && this._menu.isPoppedup())
		this._deselect(0);
}

DwtMenuItem.prototype._deselect =
function(msec) {
	if (this._style == DwtMenuItem.CASCADE_STYLE || this._style == DwtMenuItem.CHECK_STYLE
		|| this._style == DwtMenuItem.RADIO_STYLE) {
		this._iconCell.className = this._iconAreaClassName;
		if (this._checkedCell)
			this._checkedCell.className = this._checkedAreaClassName;		
		msec = (msec == null) ? DwtMenuItem._MENU_POPDOWN_DELAY : msec;
	}
	if (this._menu)
		this._menu.popdown(msec);
	this.setClassName(this._origClassName);
	this.setCursor("default");
}

DwtMenuItem.prototype._isMenuPoppedup =
function() {
	return (this._menu && this._menu.isPoppedup()) ? true : false;
}

DwtMenuItem.prototype._mouseOverListener = 
function(ev) {
	this.parent.popup();
	if (this._style == DwtMenuItem.SEPARATOR_STYLE)
		return;
	var activeItem = this.parent._getActiveItem();
	this.parent.clearExternallySelectedItems();
	if (this._style == DwtMenuItem.CASCADE_STYLE || this._style == DwtMenuItem.CHECK_STYLE
		|| this._style == DwtMenuItem.RADIO_STYLE) {
		if (activeItem)
			activeItem._deselect();	
		this._iconCell.className = this._iconAreaSelClassName;
		if (this._checkedCell)
			this._checkedCell.className = this._checkedAreaSelClassName;
		if (this._menu)
			this._menu.popup(DwtMenuItem._MENU_POPUP_DELAY);
		this.setSelectedStyle();
	} else if (this._style == DwtMenuItem.PUSH_STYLE || this._style == DwtMenuItem.SELECT_STYLE) {
		if (activeItem)
			activeItem._deselect(0);
		if (activeItem && this._menu) {
			this._menu.popup();
			this.setSelectedStyle();
		} else {
			this.setSelectedStyle()
		}
	}
	ev._stopPropagation = true;
}

DwtMenuItem.prototype.setSelectedStyle = 
function () {
	this.setClassName(this._selectedClassName);
	this._isSelected = true;
};

DwtMenuItem.prototype.setTriggeredStyle = 
function () {
	this.setCursor("wait");
	this.setClassName(this._triggeredClassName);
};

DwtMenuItem.prototype._mouseOutListener = 
function(ev) {
	if (this._style == DwtMenuItem.SEPARATOR_STYLE)
		return;
	if (this._menu == null || !this._menu.isPoppedup())
		this._deselect();
}

DwtMenuItem.prototype._mouseDownListener = 
function(ev) {
	if (ev.button != DwtMouseEvent.LEFT)
		return;
	this.setTriggeredStyle();
}

DwtMenuItem.prototype._mouseUpListener = 
function(ev) {
	if (ev.button != DwtMouseEvent.LEFT)
		return;

	if (this._style == DwtMenuItem.CHECK_STYLE) {
		this._deselect();
		this._setChecked(!this._itemChecked, ev);
		DwtMenu.closeActiveMenu();
	} else if (this._style == DwtMenuItem.RADIO_STYLE) {
		if (!this._itemChecked) {
			this._setChecked(!this._itemChecked, ev);
			if (this._menu) {
				this._menu.popup(0);
			} else {
				DwtMenu.closeActiveMenu();
			}
		} else if (this._menu){
			this._menu.popup(0);
		} else {
			DwtMenu.closeActiveMenu();
			// at the very least, notify menu item was clicked again
			var selEv = DwtShell.selectionEvent;
			if (ev)
				DwtUiEvent.copy(selEv, ev);
			else
				selEv.reset();
			selEv.item = this;
			selEv.detail = (this._itemChecked) ? DwtMenuItem.CHECKED : DwtMenuItem.UNCHECKED;
			this.notifyListeners(DwtEvent.SELECTION, selEv);			
		}
	} else if (this._style != DwtMenuItem.PUSH_STYLE) {
		if (this._menu) {
			// We know we have a menu to popup
			this._menu.popup(0);
		} else if (this.isListenerRegistered(DwtEvent.SELECTION)) {
			this._deselect();
			var selEv = DwtShell.selectionEvent;
			DwtUiEvent.copy(selEv, ev);
			selEv.item = selEv.dwtObj;
			selEv.detail = 0;
			this.notifyListeners(DwtEvent.SELECTION, selEv);
			DwtMenu.closeActiveMenu();
		}  else {
			this._deselect();
			DwtMenu.closeActiveMenu();
		}
	} else if (this._style == DwtMenuItem.PUSH_STYLE){
		if (this._menu){
			if (!this._isMenuPoppedup()){
				this._menu.popup(0);
			} else {
				this._deselect(0);
			}
		}
	}
	return true;
}
