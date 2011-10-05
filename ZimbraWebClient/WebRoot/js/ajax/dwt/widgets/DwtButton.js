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
 * @overview
 * This file defines a button.
 *
 */

/**
 * Creates a button.
 * @class
 * This class represents a button, which is basically a smart label that can handle
 * various UI events. It knows when it has been hovered (the mouse is over it),
 * when it is active (mouse down), and when it has been pressed (mouse up).
 * In addition to a label's image and/or text, a button may have a dropdown menu.
 * <p>
 * There are several different types of button:
 * <ul>
 * <li><i>Push</i> - This is the standard push button</li>
 * <li><i>Toggle</i> - This is a button that exhibits selectable behaviour when clicked
 * 		e.g. on/off. To make a button selectable style "or" {@link DwtButton.SELECT_STYLE}
 * 		to the consturctor's style parameter</li>
 * <li><i>Menu</i> - By setting a mene via the {@link #setMenu} method a button will become
 * 		a drop down or menu button.</li>
 * </ul>
 *
 * <h4>CSS</h4>
 * <ul>
 * <li><i>className</i>-hover - hovered style</li>
 * <li><i>className</i>-active - mouse down style</li>
 * <li><i>className</i>-selected - permanently down style</li>
 * <li><i>className</i>-disabled - disabled style</li>
 * </ul>
 *
 * <h4>Keyboard Actions</h4>
 * <ul>
 * <li>{@link DwtKeyMap.SELECT} - triggers the button</li>
 * <li>{@link DwtKeyMap.SUBMENU} - display's the button's submenu if one is set</li>
 * </ul>
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 * 
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}	params.parent	the parent widget
 * @param {constant}	params.style		the button style
 * @param {string}	params.className		the CSS class
 * @param {constant}	params.posStyle		the positioning style
 * @param {DwtButton.ACTION_MOUSEUP|DwtButton.ACTION_MOUSEDOWN}	params.actionTiming	if {@link DwtButton.ACTION_MOUSEUP}, then the button is triggered
 *											on mouseup events, else if {@link DwtButton.ACTION_MOUSEDOWN},
 * 											then the button is triggered on mousedown events
 * @param {string}	params.id		the id to use for the control HTML element
 * @param {number}	params.index 		the index at which to add this control among parent's children
 * @param {hash}	params.listeners		a hash of event listeners
 *        
 * @extends		DwtLabel
 */
DwtButton = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtButton.PARAMS);
	
	params.className = params.className || "ZButton";
	DwtLabel.call(this, params);

	var parent = params.parent;
	if (!parent._hasSetMouseEvents || AjxEnv.isIE) {
		this._setMouseEvents();
	}
	
	var events;
	if (parent._hasSetMouseEvents) {
		events = AjxEnv.isIE ? [DwtEvent.ONMOUSEENTER, DwtEvent.ONMOUSELEAVE] : [];
	} else {
		events = AjxEnv.isIE
			? [DwtEvent.ONMOUSEENTER, DwtEvent.ONMOUSELEAVE]
			: [DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT];
		events = events.concat([DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP]);
	}
	if (events && events.length) {
		this._setEventHdlrs(events);
	}
	this._listeners = params.listeners || DwtButton._listeners;
	this._addMouseListeners();
	this._ignoreInternalOverOut = true;
	
	this._dropDownEvtMgr = new AjxEventMgr();

	this._selected = false;

	this._actionTiming = params.actionTiming || DwtButton.ACTION_MOUSEUP;
	this.__preventMenuFocus = null;
	this._menuPopupStyle = DwtButton.MENU_POPUP_STYLE_BELOW;
};

DwtButton.prototype = new DwtLabel;
DwtButton.prototype.constructor = DwtButton;

DwtButton.prototype.isDwtButton = true;
DwtButton.prototype.toString = function() { return "DwtButton"; };

//
// Constants
//
DwtButton.PARAMS = ["parent", "style", "className", "posStyle", "actionTiming", "id", "index", "listeners"];
DwtButton.TOGGLE_STYLE = DwtLabel._LAST_STYLE * 2; // NOTE: These must be powers of 2 because we do bit-arithmetic to check the style.
DwtButton.ALWAYS_FLAT = DwtLabel._LAST_STYLE * 4;
DwtButton._LAST_STYLE = DwtButton.ALWAYS_FLAT;

DwtButton.ACTION_MOUSEUP = 1;
DwtButton.ACTION_MOUSEDOWN = 2; // No special appearance when hovered or active

DwtButton.NOTIFY_WINDOW = 500;  // Time (in ms) during which to block additional clicks from being processed

DwtButton.MENU_POPUP_STYLE_BELOW	= "BELOW";		// menu pops up just below the button (default)
DwtButton.MENU_POPUP_STYLE_ABOVE	= "ABOVE";		// menu pops up above the button
DwtButton.MENU_POPUP_STYLE_RIGHT	= "RIGHT";		// menu pops up below the button, with right edges aligned
DwtButton.MENU_POPUP_STYLE_CASCADE	= "CASCADE";	// menu pops up to right of the button

//
// Data
//
DwtButton.prototype.TEMPLATE = "dwt.Widgets#ZButton";

//
// Public methods
//

/**
 * Disposes of the button.
 * 
 */
DwtButton.prototype.dispose =
function() {
	if (this._menu && this._menu.isDwtMenu && (this._menu.parent == this)) {
		this._menu.dispose();
		this._menu = null;
	}
	DwtLabel.prototype.dispose.call(this);
};

/**
 * Adds a listener to be notified when the button is pressed.
 *
 * @param {AjxListener}	listener	the listener
 * @param {number}	index		the index at which to add listener
 */
DwtButton.prototype.addSelectionListener =
function(listener, index) {
	this.addListener(DwtEvent.SELECTION, listener, index);
};

/**
 * Removes a selection listener.
 *
 * @param {AjxListener}		listener	the listener to remove
 */
DwtButton.prototype.removeSelectionListener =
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes all the selection listeners.
 */
DwtButton.prototype.removeSelectionListeners =
function() {
	this.removeAllListeners(DwtEvent.SELECTION);
};

/**
 * Adds a listener to be notified when the dropdown arrow is pressed.
 *
 * @param {AjxListener}		listener	the listener
 */
DwtButton.prototype.addDropDownSelectionListener =
function(listener) {
	return this._dropDownEvtMgr.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a dropdown selection listener.
 *
 * @param {AjxListener}		listener	the listener to remove
 */
DwtButton.prototype.removeDropDownSelectionListener =
function(listener) {
	this._dropDownEvtMgr.removeListener(DwtEvent.SELECTION, listener);
};

// defaults for drop down images (set here once on prototype rather than on each button instance)
DwtButton.prototype._dropDownImg 	= "SelectPullDownArrow";
DwtButton.prototype._dropDownDepImg	= "SelectPullDownArrow";
DwtButton.prototype._dropDownHovImg = "SelectPullDownArrowHover";

/**
 * Sets the dropdown images.
 * 
 * @param	{string}	enabledImg		the enabled image
 * @param	{string}	disImg		the disabled image
 * @param	{string}	hovImg		the hover image
 * @param	{string}	depImg		the depressed image
 */
DwtButton.prototype.setDropDownImages =
function (enabledImg, disImg, hovImg, depImg) {
	this._dropDownImg = enabledImg;
	this._dropDownHovImg = hovImg;
	this._dropDownDepImg = depImg;
};

/**
 * Sets the Drop Down Hover Image
 */
DwtButton.prototype.setDropDownHovImage =
function(hovImg) {
    this._dropDownHovImg = hovImg;    
}

/**
 * @private
 */
DwtButton.prototype._addMouseListeners =
function() {
	var events = [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP];
	events = events.concat(AjxEnv.isIE ? [DwtEvent.ONMOUSEENTER, DwtEvent.ONMOUSELEAVE] :
										 [DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT]);
	for (var i = 0; i < events.length; i++) {
		this.addListener(events[i], this._listeners[events[i]]);
	}
};

/**
 * @private
 */
DwtButton.prototype._removeMouseListeners =
function() {
	var events = [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP];
	events = events.concat(AjxEnv.isIE ? [DwtEvent.ONMOUSEENTER, DwtEvent.ONMOUSELEAVE] :
										 [DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT]);
	for (var i = 0; i < events.length; i++) {
		this.removeListener(events[i], this._listeners[events[i]]);
	}
};

/**
 * Sets the display state.
 * 
 * @param	{string}	state		the display state
 * @param	{boolean}	force		if <code>true</code>, force the state change
 * @see		DwtControl
 */
DwtButton.prototype.setDisplayState =
function(state, force) {
    if (this._selected && state != DwtControl.SELECTED && !force) {
        state = [ DwtControl.SELECTED, state ].join(" ");
    }
    DwtLabel.prototype.setDisplayState.call(this, state);
};

/**
 * Sets the enabled/disabled state of the button. A disabled button may have a different
 * image, and greyed out text. The button (and its menu) will only have listeners if it
 * is enabled.
 *
 * @param {boolean}	enabled			if <code>true</code>, enable the button
 *
 */
DwtButton.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtLabel.prototype.setEnabled.call(this, enabled); // handles image/text
        if (enabled) {
			// bug fix #36253 - HACK for IE. ARGH!!!
			var el = (AjxEnv.isIE) ? this.getHtmlElement().firstChild : null;
			if (el) {
				var cname = el.className;
				el.className = "";
				el.className = cname;
			}
			this._addMouseListeners();
			// set event handler for pull down menu if applicable
			if (this._menu) {
				this._setDropDownCellMouseHandlers(true);
                if (this._dropDownEl && this._dropDownImg) {
                    AjxImg.setImage(this._dropDownEl, this._dropDownImg);
                }
            }

		} else {
			this._removeMouseListeners();
			// remove event handlers for pull down menu if applicable
			if (this._menu) {
				this._setDropDownCellMouseHandlers(false);
                if (this._dropDownEl && this._dropDownImg) {
                    AjxImg.setDisabledImage(this._dropDownEl, this._dropDownImg);
                }
			}
		}
	}
};

/**
 * Sets the main (enabled) image. If the button is currently enabled, the image is updated.
 * 
 * @param	{string}	imageInfo		the image
 */
DwtButton.prototype.setImage =
function(imageInfo) {
	DwtLabel.prototype.setImage.call(this, imageInfo);
	this._setMinWidth();
};

/**
 * Sets the text.
 * 
 * @param	{string}	text		the text
 */
DwtButton.prototype.setText =
function(text) {
	DwtLabel.prototype.setText.call(this, text);
	this._setMinWidth();
};

/**
 * @private
 */
DwtButton.prototype._setMinWidth =
function() {
	if (this.getText() != null) {
		Dwt.addClass(this.getHtmlElement(), "ZHasText");
	} else {
		Dwt.delClass(this.getHtmlElement(), "ZHasText");
	}
};

/**
 * Sets the hover image.
 * 
 * @param	{string}	hoverImageInfo		the image
 */
DwtButton.prototype.setHoverImage =
function (hoverImageInfo) {
    this._hoverImageInfo = hoverImageInfo;
};

/**
 * Adds a dropdown menu to the button, available through a small down-arrow. If a
 * callback is passed as the dropdown menu, it is called the first time the
 * menu is requested. The callback must return a valid DwtMenu object.
 *
 * @param {hash}				params				hash of params:
 * @param {DwtMenu|AjxCallback}	menu				the dropdown menu or a callback
 * @param {boolean}				shouldToggle		if <code>true</code>, toggle
 * @param {string}				menuPopupStyle		one of DwtButton.MENU_POPUP_STYLE_* (default is BELOW)
 * @param {boolean}				popupAbove         if <code>true</code>, pop up the menu above the button
 * @param {boolean}				popupRight         if <code>true</code>, align the right edge of the menu to the right edge of the button
 */
DwtButton.prototype.setMenu =
function(params) {
	
	params = Dwt.getParams(arguments, DwtButton.setMenuParams, (arguments.length == 1 && !arguments[0].menu));
	
	this._menu = params.menu;
	this._shouldToggleMenu = (params.shouldToggle === true);
	if (params.popupAbove) {
		this._menuPopupStyle = DwtButton.MENU_POPUP_STYLE_ABOVE;
	}
	else if (params.popupRight) {
		this._menuPopupStyle = DwtButton.MENU_POPUP_STYLE_RIGHT;
	}
	else {
		this._menuPopupStyle = params.menuPopupStyle || DwtButton.MENU_POPUP_STYLE_BELOW;
	}
	if (this._menu) {
        if (this._dropDownEl) {
			var idx = (this._imageCell) ? 1 : 0;
			if (this._textCell)
				idx++;

			Dwt.addClass(this.getHtmlElement(), "ZHasDropDown");
			if (this._dropDownImg) {
            	AjxImg.setImage(this._dropDownEl, this._dropDownImg);
			}

			// set event handler if applicable
			if (this._enabled) {
				this._setDropDownCellMouseHandlers(true);
			}

            if (!this._menu.isAjxCallback) {
                this._menu.setAssociatedElementId(this._dropDownEl.id);
            }
		}
		if ((this.__preventMenuFocus != null) && this._menu.isDwtMenu) {
			this._menu.dontStealFocus(this.__preventMenuFocus);
		}
    }
    else if (this._dropDownEl) {
		Dwt.delClass(this.getHtmlElement(), "ZHasDropDown");
        this._dropDownEl.innerHTML = "";
    }
};
DwtButton.setMenuParams = ["menu", "shouldToggle", "followIconStyle", "popupAbove", "popupRight"];

/**
 * @private
 */
DwtButton.prototype._setDropDownCellMouseHandlers =
function(set) {
	this._dropDownEventsEnabled = set;
};

/**
* Gets the button menu.
*
* @param {boolean}		dontCreate	 if <code>true</code>, the menu will not be lazily created
* @return	{DwtMenu}	the menu or <code>null</code> if menu is not set
*/
DwtButton.prototype.getMenu =
function(dontCreate) {
	if (this._menu && this._menu.isAjxCallback) {
		if (dontCreate) {
			return null;
		}
		var callback = this._menu;
		this.setMenu({menu: callback.run(this)});
		if ((this.__preventMenuFocus != null) && (this._menu.isDwtMenu)) {
			this._menu.dontStealFocus(this.__preventMenuFocus);
		}
	}
    if (this._menu) {
        this.getHtmlElement().setAttribute("menuId", this._menu._htmlElId);
    }
    return this._menu;
};

/**
 * Resets the button display to normal (not hovered or active).
 * 
 */
DwtButton.prototype.resetClassName =
function() {
    this.setDisplayState(DwtControl.NORMAL);
};

/**
 * Sets whether actions for this button should occur on mouse up or mouse down.
 *
 * @param	{DwtButton.ACTION_MOUSEDOWN|DwtButton.ACTION_MOUSEUP}		actionTiming		the action timing
 */
DwtButton.prototype.setActionTiming =
function(actionTiming) {
      this._actionTiming = actionTiming;
};

/**
 * Activates/de-activates the button. A button is hovered when the mouse is over it.
 *
 * @param {boolean}	hovered		if <code>true</code>, the button is hovered
 */
DwtButton.prototype.setHovered =
function(hovered) {
    this.setDisplayState(hovered ? DwtControl.HOVER : DwtControl.NORMAL);
};

/**
 * Sets the enabled image
 * 
 * @param	{string}	imageInfo	the image
 */
DwtButton.prototype.setEnabledImage =
function (imageInfo) {
	this._enabledImageInfo = imageInfo;
	this.setImage(imageInfo);
};

/**
 * Sets the depressed image
 * 
 * @param	{string}	imageInfo	the image
 */
DwtButton.prototype.setDepressedImage =
function (imageInfo) {
    this._depressedImageInfo = imageInfo;
};

/**
 * Sets the button as selected.
 * 
 * @param	{boolean}	selected		if <code>true</code>, the button is selected
 */
DwtButton.prototype.setSelected =
function(selected) {
	if (this._selected != selected) {
		this._selected = selected;
        this.setDisplayState(selected ? DwtControl.SELECTED : DwtControl.NORMAL);
    }
};

/**
 * Checks if the button is toggled.
 * 
 * @return	{boolean}	<code>true</code> if toggled
 */
DwtButton.prototype.isToggled =
function() {
	return this._selected;
};

/**
 * Pops-up the button menu (if present).
 * 
 * @param	{DwtMenu}	menu		the menu to use or <code>null</code> to use currently set menu
 */
DwtButton.prototype.popup =
function(menu) {
	menu = menu || this.getMenu();

    if (!menu) { return; }

    var parent = menu.parent;
	var parentBounds = parent.getBounds();
	var windowSize = menu.shell.getSize();
	var menuSize = menu.getSize();
	var parentElement = parent.getHtmlElement();
	// since buttons are often absolutely positioned, and menus aren't, we need x,y relative to window
	var parentLocation = Dwt.toWindow(parentElement, 0, 0);
	var leftBorder = (parentElement.style.borderLeftWidth == "") ? 0 : parseInt(parentElement.style.borderLeftWidth);

	var x;
	if (this._menuPopupStyle == DwtButton.MENU_POPUP_STYLE_RIGHT) {
		x = parentLocation.x + parentBounds.width - menuSize.x;
	}
	else if (this._menuPopupStyle == DwtButton.MENU_POPUP_STYLE_CASCADE) {
		x = parentLocation.x + parentBounds.width;
	}
	else {
		x = parentLocation.x + leftBorder;
		x = ((x + menuSize.x) >= windowSize.x) ? windowSize.x - menuSize.x : x;
	}

	var y;
	if (this._menuPopupStyle == DwtButton.MENU_POPUP_STYLE_ABOVE) {
		y = parentLocation.y - menuSize.y;
	}
	else if (this._menuPopupStyle == DwtButton.MENU_POPUP_STYLE_CASCADE) {
		y = parentLocation.y;
	}
	else {
		var horizontalBorder = (parentElement.style.borderTopWidth == "") ? 0 : parseInt(parentElement.style.borderTopWidth);
		horizontalBorder += (parentElement.style.borderBottomWidth == "") ? 0 : parseInt(parentElement.style.borderBottomWidth);
		y = parentLocation.y + parentBounds.height + horizontalBorder;
	}
	menu.popup(0, x, y);
};

/**
 * Gets the key map name.
 * 
 * @return	{string}	the key map name
 */
DwtButton.prototype.getKeyMapName =
function() {
	return "DwtButton";
};

/**
 * Handles a key action event.
 * 
 * @param	{constant}		actionCode		the action code (see {@link DwtKeyMap})
 * @param	{DwtEvent}		ev		the event
 * @return	{boolean}		<code>true</code> if the event is handled; <code>false</code> otherwise
 * @see		DwtKeyMap
 */
DwtButton.prototype.handleKeyAction =
function(actionCode, ev) {
	switch (actionCode) {
		case DwtKeyMap.SELECT:
			this._emulateSingleClick();
			break;

		case DwtKeyMap.SUBMENU:
			var menu = this.getMenu();
			if (!menu) return false;
			this._emulateDropDownClick();
			menu.setSelectedItem(0);
			break;
	}

	return true;
};

// Private methods

/**
 * @private
 */
DwtButton.prototype._emulateSingleClick =
function() {
	this.trigger();
	var htmlEl = this.getHtmlElement();
	var p = Dwt.toWindow(htmlEl);
	var mev = new DwtMouseEvent();
	this._setMouseEvent(mev, {dwtObj:this, target:htmlEl, button:DwtMouseEvent.LEFT, docX:p.x, docY:p.y});
	if (this._actionTiming == DwtButton.ACTION_MOUSEDOWN) {
		this.notifyListeners(DwtEvent.ONMOUSEDOWN, mev);
	} else {
		this.notifyListeners(DwtEvent.ONMOUSEUP, mev);
	}
};

/**
 * @private
 */
DwtButton.prototype._emulateDropDownClick =
function() {
    var htmlEl = this._dropDownEl;
    if (!htmlEl) { return; }

	var p = Dwt.toWindow(htmlEl);
	var mev = new DwtMouseEvent();
	this._setMouseEvent(mev, {dwtObj:this, target:htmlEl, button:DwtMouseEvent.LEFT, docX:p.x, docY:p.y});
	DwtButton._dropDownCellMouseDownHdlr(mev);
};

/**
 * This method is called from mouseUpHdlr in {@see DwtControl}.
 * 
 * @private
 */
DwtButton.prototype._focusByMouseUpEvent =
function()  {
	// don't steal focus if on a toolbar that's not part of focus ring
	if (!(this.parent && (this.parent instanceof DwtToolBar) && this.parent.noFocus)) {
		DwtShell.getShell(window).getKeyboardMgr().grabFocus(this.getTabGroupMember());
	}
};

/**
 * NOTE: _focus and _blur will be reworked to reflect styles correctly
 * 
 * @private
 */
DwtButton.prototype._focus =
function() {
    this.setDisplayState(DwtControl.FOCUSED);
};

/**
 * @private
 */
DwtButton.prototype._blur =
function() {
    this.setDisplayState(DwtControl.NORMAL);
};

/**
 * @private
 */
DwtButton.prototype._toggleMenu =
function () {
	if (this._shouldToggleMenu){
        var menu = this.getMenu();
        if (!menu.isPoppedUp()){
			this.popup();
			this._menuUp = true;
		} else {
			menu.popdown();
			this._menuUp = false;
            this.deactivate();
        }
	} else {
		this.popup();
	}
};

/**
 * @private
 */
DwtButton.prototype._isDropDownEvent =
function(ev) {
	if (this._dropDownEventsEnabled && this._dropDownEl) {
		var mouseX = ev.docX;
		var dropDownX = Dwt.toWindow(this._dropDownEl, 0, 0, window).x;
		if (mouseX >= dropDownX) {
			return true;
		}
	}
	return false;
};

/**
 * @private
 */
DwtButton.prototype.trigger =
function (){
    if (this._depressedImageInfo) {
        this.setImage(this._depressedImageInfo);
    }
    this.setDisplayState(DwtControl.ACTIVE, true);
    this.isActive = true;
};

/**
 * @private
 */
DwtButton.prototype.deactivate =
function() {
	this._showHoverImage(true);

	if (this._style & DwtButton.TOGGLE_STYLE){
		this._selected = !this._selected;
	}
    this.setDisplayState(DwtControl.HOVER);
};

/**
 * @private
 */
DwtButton.prototype.dontStealFocus = function(val) {
	if (val == null) {
		val = true;
	}
	if (this._menu && this._menu.isDwtMenu) {
		this._menu.dontStealFocus(val);
	}
	this.__preventMenuFocus = val;
};

/**
 * @private
 */
DwtButton.prototype._showHoverImage =
function(show) {
	// if the button is image-only, DwtLabel#setImage is bad
	// because it clears the element first
	// (innerHTML = "") causing a mouseout event, then it
	// re-sets the image, which results in a new mouseover
	// event, thus looping forever eating your CPU and
	// blinking.
	if (this._hoverImageInfo){
		var iconEl = this._getIconEl();
		if (iconEl) {  //add a null check so buttons with no icon elements don't break the app.
			var info = show ? this._hoverImageInfo : this.__imageInfo;
			iconEl.firstChild.className = AjxImg.getClassForImage(info);
		}
	}
};

/**
 * @private
 */
DwtButton.prototype._handleClick =
function(ev) {
	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
		var now = (new Date()).getTime();
		if (!this._lastNotify || (now - this._lastNotify > DwtButton.NOTIFY_WINDOW)) {
			var selEv = DwtShell.selectionEvent;
			DwtUiEvent.copy(selEv, ev);
			selEv.item = this;
			selEv.detail = (typeof this.__detail == "undefined") ? 0 : this.__detail;
			this.notifyListeners(DwtEvent.SELECTION, selEv);
			this._lastNotify = now;
			this.shell.notifyGlobalSelection(selEv);
		}
	} else if (this._menu) {
		if(this._menu.isDwtMenu && !this.isListenerRegistered(DwtEvent.SELECTION)) {
			this._menu.setAssociatedObj(this);	
		}		
		this._toggleMenu();
	}
};

/**
 * @private
 */
DwtButton.prototype._setMouseOutClassName =
function() {
    this.setDisplayState(DwtControl.NORMAL);
};

/**
 * @private
 */
DwtButton.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtLabel.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._dropDownEl = document.getElementById(data.id+"_dropdown");
};

/**
 * Pops up the dropdown menu.
 * 
 * @private
 */
DwtButton._dropDownCellMouseDownHdlr =
function(ev) {
	var obj = DwtControl.getTargetControl(ev);

    var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, obj);

	if (mouseEv.button == DwtMouseEvent.LEFT) {
	    if (this._depImg){
			AjxImg.setImage(this, this._depImg);
	    }
	}

	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;
};

/**
 * Updates the current mouse event (set from the previous mouse down).
 * 
 * @private
 */
DwtButton._dropDownCellMouseUpHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);

	if (mouseEv.button == DwtMouseEvent.LEFT) {
	    if (this._dropDownHovImg && !this.noMenuBar) {
			AjxImg.setImage(this, this._dropDownHovImg);
	    }

		DwtEventManager.notifyListeners(DwtEvent.ONMOUSEDOWN, mouseEv);

		var obj = DwtControl.getTargetControl(ev);
		if (obj) {
			if (obj.getMenu() && obj.getMenu().isPoppedUp()) {
				obj.getMenu().popdown();
			}
			else {
				if (obj._menu && obj._menu.isAjxCallback) {
					obj.popup();
				}

				if (obj._dropDownEvtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
					var selEv = DwtShell.selectionEvent;
					DwtUiEvent.copy(selEv, mouseEv);
					selEv.item = obj;
					obj._dropDownEvtMgr.notifyListeners(DwtEvent.SELECTION, selEv);
				} else {
					obj._toggleMenu();
				}
			}
		}
	}
	
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;
};

/**
 * Activates the button.
 * 
 * @private
 */
DwtButton._mouseOverListener =
function(ev) {
	var button = ev.dwtObj;
	if (!button) { return false; }
	button._showHoverImage(true);
    button.setDisplayState(DwtControl.HOVER);

    var dropDown = button._dropDownEl;
    if (button._menu && dropDown && button._dropDownHovImg && !button.noMenuBar &&
        button.isListenerRegistered(DwtEvent.SELECTION)) {
		if (button._dropDownHovImg) {
			AjxImg.setImage(dropDown, button._dropDownHovImg);
		}
    }
	// bug fix 48266 IE hack, solution is similar to bug 36253
	// Just rewrite the el's Child's className to trigger IE to render it
	// In mouserOut, it seems the IE can render it automatically. 	
	if(AjxEnv.isIE){
	   	if(ev && ev.target && ev.target.firstChild){
			var el = ev.target.firstChild;
			var cname = el.className;
			el.className = "";
			el.className = cname;
		} 
	}    	
    ev._stopPropagation = true;
};

/**
 * @private
 */
DwtButton._mouseOutListener =
function(ev) {
	var button = ev.dwtObj;
	if (!button) { return false; }
	button._showHoverImage(false);
	button._setMouseOutClassName();
    button.isActive = false;

    var dropDown = button._dropDownEl;
    if (button._menu && dropDown && button._dropDownImg) {
		AjxImg.setImage(dropDown, button._dropDownImg);
    }
};

/**
 * @private
 */
DwtButton._mouseDownListener =
function(ev) {
	var button = ev.dwtObj;
	if (!button) { return false; }
	if (button._isDropDownEvent(ev)) {
		return DwtButton._dropDownCellMouseDownHdlr(ev);
	}

	if (ev.button != DwtMouseEvent.LEFT) { return; }

    var dropDown = button._dropDownEl;
    if (button._menu && dropDown && button._dropDownDepImg) {
		AjxImg.setImage(dropDown, button._dropDownDepImg);
    }
	switch (button._actionTiming) {
	  case DwtButton.ACTION_MOUSEDOWN:
		button.trigger();
		button._handleClick(ev);
		break;
	  case DwtButton.ACTION_MOUSEUP:
		button.trigger();
		break;
	}
};

/**
 * Button has been pressed, notify selection listeners.
 * 
 * @private
 */
DwtButton._mouseUpListener =
function(ev) {
	var button = ev.dwtObj;
	if (!button) { return false; }
	if (button._isDropDownEvent(ev)) {
		return DwtButton._dropDownCellMouseUpHdlr(ev);
	}
	if (ev.button != DwtMouseEvent.LEFT) { return; }

    var dropDown = button._dropDownEl;
    if (button._menu && dropDown && button._dropDownHovImg && !button.noMenuBar){
		AjxImg.setImage(dropDown, button._dropDownHovImg);
    }
	switch (button._actionTiming) {
	  case DwtButton.ACTION_MOUSEDOWN:
 	    button.deactivate();
		break;

	  case DwtButton.ACTION_MOUSEUP:
	    var el = button.getHtmlElement();
		if (button.isActive) {
			button.deactivate();
			button._handleClick(ev);
		}
		break;
	}
};

DwtButton._listeners = {};
DwtButton._listeners[DwtEvent.ONMOUSEOVER] = new AjxListener(null, DwtButton._mouseOverListener);
DwtButton._listeners[DwtEvent.ONMOUSEOUT] = new AjxListener(null, DwtButton._mouseOutListener);
DwtButton._listeners[DwtEvent.ONMOUSEDOWN] = new AjxListener(null, DwtButton._mouseDownListener);
DwtButton._listeners[DwtEvent.ONMOUSEUP] = new AjxListener(null, DwtButton._mouseUpListener);
DwtButton._listeners[DwtEvent.ONMOUSEENTER] = new AjxListener(null, DwtButton._mouseOverListener);
DwtButton._listeners[DwtEvent.ONMOUSELEAVE] = new AjxListener(null, DwtButton._mouseOutListener);
