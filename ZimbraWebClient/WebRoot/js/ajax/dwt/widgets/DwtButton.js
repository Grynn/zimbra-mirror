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
* @constructor
* @class
* This class represents a button, which is basically a smart label that can handle
* various UI events. It knows when it has been hovered (the mouse is over it),
* when it is active (mouse down), and when it has been pressed (mouse up).
* In addition to a label's image and/or text, a button may have a dropdown menu.
*
* There are several different types of button:
* <ul>
* <li> Push - this is the standard push button </li>
* <li> Toggle - This is a button that exhibits selectable behaviour when clicked
* 		e.g. on/off. To make a button selectable style "or" <i>DwtButton.SELECT_STYLE<i>
* 		to the consturctor's style parameter</li>
* <li> Menu - By setting a mene via the <i>setMenu</i> method a button will become
* 		a drop down or menu button.</li>
* </ul>
*
* <h4>CSS</h4>
* <ul>
* <li><i>className</i>-hover - hovered style
* <li><i>className</i>-active - mouse down style
* <li><i>className</i>-selected - permanently down style
* <li><i>className</i>-disabled - disabled style
* </ul>
*
* <h4>Keyboard Actions</h4>
* <ul>
* <li>DwtKeyMap.SELECT - triggers the button</li>
* <li>DwtKeyMap.SUBMENU - display's the button's submenu if one is set
* </ul>
*
* @author Ross Dargahi
* @author Conrad Damon
*
* @param parent	{DwtControl} Parent widget (required)
* @param style	{string} the label style. This is an "or'ed" set of attributes (see DwtLabel)
* @param className {string} CSS class. If not provided defaults to the class name (optional)
* @param posStyle {string} Positioning style (absolute, static, or relative). If
* 		not provided defaults to DwtControl.STATIC_STYLE (optional)
* @param actionTiming {enum} if DwtButton.ACTION_MOUSEUP, then the button is triggered
* 		on mouseup events, else if DwtButton.ACTION_MOUSEDOWN, then the button is
* 		triggered on mousedown events
* @param {int} id An explicit ID to use for the control's HTML element. If not
* 		specified defaults to an auto-generated id (optional)
* @param {int} index index at which to add this control among parent's children (optional)
*
* @extends DwtLabel
*/
DwtButton = function(parent, style, className, posStyle, actionTiming, id, index) {
	if (arguments.length == 0) return;
	className = className || "ZButton";
	DwtLabel.call(this, parent, style, className, posStyle, id, index);

	this._setMouseEvents();

	this._dropDownEvtMgr = new AjxEventMgr();

	this._selected = false;

	this._actionTiming = actionTiming? actionTiming : DwtButton.ACTION_MOUSEUP;
	this.__preventMenuFocus = null;
}

DwtButton.prototype = new DwtLabel;
DwtButton.prototype.constructor = DwtButton;

DwtButton.prototype.toString =
function() {
	return "DwtButton";
}

//
// Constants
//

// NOTE: These must be powers of 2 because we do bit-arithmetic to
//       check the style.
DwtButton.TOGGLE_STYLE = DwtLabel._LAST_STYLE * 2;
DwtButton.ALWAYS_FLAT = DwtLabel._LAST_STYLE * 4;

DwtButton._LAST_STYLE = DwtButton.ALWAYS_FLAT;

DwtButton.ACTION_MOUSEUP = 1;
DwtButton.ACTION_MOUSEDOWN = 2; // No special appearance when hovered or active

//
// Data
//

DwtButton.prototype.TEMPLATE = "dwt.Widgets#ZButton"

//
// Public methods
//

/**
 * Adds a listener to be notified when the button is pressed.
 *
 * @param listener	[AjxListener]	a listener
 * @param index		[int]*			index at which to add listener
 */
DwtButton.prototype.addSelectionListener =
function(listener, index) {
	this.addListener(DwtEvent.SELECTION, listener, index);
}

/**
* Removes a selection listener.
*
* @param listener	the listener to remove
*/
DwtButton.prototype.removeSelectionListener =
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
}

/**
* Removes all the selection listeners.
*/
DwtButton.prototype.removeSelectionListeners =
function() {
	this.removeAllListeners(DwtEvent.SELECTION);
}

/**
* Adds a listener to be notified when the dropdown arrow is pressed.
*
* @param listener	a listener
*/
DwtButton.prototype.addDropDownSelectionListener =
function(listener) {
	return this._dropDownEvtMgr.addListener(DwtEvent.SELECTION, listener);
}

/**
* Removes a dropdown selection listener.
*
* @param listener	the listener to remove
*/
DwtButton.prototype.removeDropDownSelectionListener =
function(listener) {
	this._dropDownEvtMgr.removeListener(DwtEvent.SELECTION, listener);
}

// defaults for drop down images (set here once on prototype rather than on each button instance)
DwtButton.prototype._dropDownImg 	= "SelectPullDownArrow";
DwtButton.prototype._dropDownDepImg	= "SelectPullDownArrow";
DwtButton.prototype._dropDownHovImg = "SelectPullDownArrowHover";

DwtButton.prototype.setDropDownImages = function (enabledImg, disImg, hovImg, depImg) {
	this._dropDownImg = enabledImg;
//	this._dropDownDisImg = disImg;
	this._dropDownHovImg = hovImg;
	this._dropDownDepImg = depImg;
};

DwtButton.prototype._addMouseListeners =
function() {
	this.addListener(DwtEvent.ONMOUSEOVER, this._mouseOverListenerObj);
	this.addListener(DwtEvent.ONMOUSEOUT, this._mouseOutListenerObj);
	this.addListener(DwtEvent.ONMOUSEDOWN, this._mouseDownListenerObj);
	this.addListener(DwtEvent.ONMOUSEUP, this._mouseUpListenerObj);
};

DwtButton.prototype._removeMouseListeners =
function() {
	this.removeListener(DwtEvent.ONMOUSEOVER, this._mouseOverListenerObj);
	this.removeListener(DwtEvent.ONMOUSEOUT, this._mouseOutListenerObj);
	this.removeListener(DwtEvent.ONMOUSEDOWN, this._mouseDownListenerObj);
	this.removeListener(DwtEvent.ONMOUSEUP, this._mouseUpListenerObj);
};

DwtButton.prototype.setDisplayState = function(state, force) {
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
* @param enabled	whether to enable the button
*
*/
DwtButton.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtLabel.prototype.setEnabled.call(this, enabled); // handles image/text
        if (enabled) {
			this._addMouseListeners();
			// set event handler for pull down menu if applicable
			if (this._menu) {
				this._setupDropDownCellMouseHandlers();
                if (this._dropDownEl) {
                    AjxImg.setImage(this._dropDownEl, this._dropDownImg);
                }
            }

		} else {
			this._removeMouseListeners();
			// remove event handlers for pull down menu if applicable
			if (this._menu) {
				this._removeDropDownCellMouseHandlers();
                if (this._dropDownEl) {
                    AjxImg.setDisabledImage(this._dropDownEl, this._dropDownImg);
                }
			}
		}
	}
}

DwtButton.prototype.setImage =
function(imageInfo) {
	DwtLabel.prototype.setImage.call(this, imageInfo);
	this._setMinWidth();
}

DwtButton.prototype.setText =
function(text) {
	DwtLabel.prototype.setText.call(this, text);
	this._setMinWidth();
}

DwtButton.prototype._setMinWidth =
function() {
	if (this.getText() != null) {
		Dwt.addClass(this.getHtmlElement(), "ZHasText");
	} else {
		Dwt.delClass(this.getHtmlElement(), "ZHasText");
	}
}

DwtButton.prototype._setMouseEvents =
function() {
	// add custom mouse handlers to standard ones
	var mouseEvents = [DwtEvent.ONCONTEXTMENU, DwtEvent.ONDBLCLICK, DwtEvent.ONMOUSEDOWN,
					   DwtEvent.ONMOUSEMOVE, DwtEvent.ONMOUSEUP, DwtEvent.ONSELECTSTART];
	if (AjxEnv.isIE)
		mouseEvents.push(DwtEvent.ONMOUSEENTER, DwtEvent.ONMOUSELEAVE);
	else
		mouseEvents.push(DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT);
	this._setEventHdlrs(mouseEvents);
	this._mouseOverListenerObj = new AjxListener(this, this._mouseOverListener);
	this._mouseOutListenerObj = new AjxListener(this, this._mouseOutListener);
	this._mouseDownListenerObj = new AjxListener(this, this._mouseDownListener);
	this._mouseUpListenerObj = new AjxListener(this, this._mouseUpListener);
	this._addMouseListeners();
}

DwtButton.prototype.setHoverImage =
function (hoverImageInfo) {
    this._hoverImageInfo = hoverImageInfo;
}
/**
* Adds a dropdown menu to the button, available through a small down-arrow.
*
* @param menuOrCallback		The dropdown menu or an AjxCallback object. If a
*                           callback is given, it is called the first time the
*                           menu is requested. The callback must return a valid
*                           DwtMenu object.
* @param shouldToggle
* @param followIconStyle	style of menu item (should be checked or radio style) for
*							which the button icon should reflect the menu item icon
*/
DwtButton.prototype.setMenu =
function(menuOrCallback, shouldToggle, followIconStyle) {
	this._menu = menuOrCallback;
	this._shouldToggleMenu = (shouldToggle === true);
	this._followIconStyle = followIconStyle;
	if (this._menu) {
        if (this._dropDownEl) {
			var idx = (this._imageCell) ? 1 : 0;
			if (this._textCell)
				idx++;

			Dwt.addClass(this.getHtmlElement(), "ZHasDropDown");
            AjxImg.setImage(this._dropDownEl, this._dropDownImg);

			// set event handler if applicable
			if (this._enabled) {
				this._setupDropDownCellMouseHandlers();
			}

            if (!(this._menu instanceof AjxCallback)) {
                this._menu.setAssociatedElementId(this._dropDownEl.id);
            }
		}
		if ((this.__preventMenuFocus != null) && (this._menu instanceof DwtMenu))
			this._menu.dontStealFocus(this.__preventMenuFocus);
    }
    else if (this._dropDownEl) {
		Dwt.delClass(this.getHtmlElement(), "ZHasDropDown");
        this._dropDownEl.innerHTML = "";
    }
}

DwtButton.prototype._setupDropDownCellMouseHandlers =
function() {
    if (this._dropDownEl) {
        Dwt.setHandler(this._dropDownEl, DwtEvent.ONMOUSEDOWN, DwtButton._dropDownCellMouseDownHdlr);
        Dwt.setHandler(this._dropDownEl, DwtEvent.ONMOUSEUP, DwtButton._dropDownCellMouseUpHdlr);
    }
};

DwtButton.prototype._removeDropDownCellMouseHandlers =
function() {
    if (this._dropDownEl) {
        Dwt.clearHandler(this._dropDownEl, DwtEvent.ONMOUSEDOWN);
        Dwt.clearHandler(this._dropDownEl, DwtEvent.ONMOUSEUP);
    }
};

/**
* Returns the button's menu
*/
DwtButton.prototype.getMenu =
function() {
	if (this._menu instanceof AjxCallback) {
		var callback = this._menu;
		this.setMenu(callback.run());
		if ((this.__preventMenuFocus != null) && (this._menu instanceof DwtMenu))
			this._menu.dontStealFocus(this.__preventMenuFocus);
	}
    if (this._menu) {
        this.getHtmlElement().setAttribute("menuId", this._menu._htmlElId);
    }
    return this._menu;
}

/**
* Returns the button display to normal (not hovered or active).
*/
DwtButton.prototype.resetClassName =
function() {
    this.setDisplayState(DwtControl.NORMAL);
}
/*
 * Sets whether actions for this button should occur on mouse up or mouse
 * down.
 *
 * Currently supports DwtButton.ACTION_MOUSEDOWN and DwtButton.ACTION_MOUSEUP
 */
DwtButton.prototype.setActionTiming =
function(actionTiming) {
      this._actionTiming = actionTiming;
};

/**
* Activates/inactivates the button. A button is hovered when the mouse is over it.
*
* @param hovered		whether the button is hovered
*/
DwtButton.prototype.setHovered =
function(hovered) {
    this.setDisplayState(hovered ? DwtControl.HOVER : DwtControl.NORMAL);
}

DwtButton.prototype.setEnabledImage =
function (imageInfo) {
	this._enabledImageInfo = imageInfo;
	this.setImage(imageInfo);
}

DwtButton.prototype.setDepressedImage =
function (imageInfo) {
    this._depressedImageInfo = imageInfo;
}

DwtButton.prototype.setSelected =
function(selected) {
	if (this._selected != selected) {
		this._selected = selected;
        this.setDisplayState(selected ? DwtControl.SELECTED : DwtControl.NORMAL);
    }
}

DwtButton.prototype.isToggled =
function() {
	return this._selected;
}

DwtButton.prototype.popup =
function(menu) {
	menu = menu || this.getMenu();

    if (!menu)
		return;

    var parent = menu.parent;
	var parentBounds = parent.getBounds();
	var windowSize = menu.shell.getSize();
	var menuSize = menu.getSize();
	var parentElement = parent.getHtmlElement();
	// since buttons are often absolutely positioned, and menus aren't, we need x,y relative to window
	var parentLocation = Dwt.toWindow(parentElement, 0, 0);
	var verticalBorder = (parentElement.style.borderLeftWidth == "") ? 0 : parseInt(parentElement.style.borderLeftWidth);
	var x = parentLocation.x + verticalBorder;
	x = ((x + menuSize.x) >= windowSize.x) ? windowSize.x - menuSize.x : x;
	var horizontalBorder = (parentElement.style.borderTopWidth == "") ? 0 : parseInt(parentElement.style.borderTopWidth);
	horizontalBorder += (parentElement.style.borderBottomWidth == "") ? 0 : parseInt(parentElement.style.borderBottomWidth);
	var y = parentLocation.y + parentBounds.height + horizontalBorder;
	menu.popup(0, x, y);
};

DwtButton.prototype.getKeyMapName =
function() {
	return "DwtButton";
};

DwtButton.prototype.handleKeyAction =
function(actionCode, ev) {
    DBG.println("DwtButton.prototype.handleKeyAction");
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
}

// Private methods

DwtButton.prototype._emulateSingleClick =
function() {
	this.trigger();
	var htmlEl = this.getHtmlElement();
	var p = Dwt.toWindow(htmlEl);
	// Gotta do what mousedown listener does
	var mev = DwtShell.mouseEvent;
	mev.reset();
	mev.target = htmlEl;
	mev.button = DwtMouseEvent.LEFT;
	mev.docX = p.x;
	mev.docY = p.y;
	if (this._actionTiming == DwtButton.ACTION_MOUSEDOWN)
		this._mouseDownListener(mev);
	else
		this._mouseUpListener(mev);
};

DwtButton.prototype._emulateDropDownClick =
function() {
    var htmlEl = this._dropDownEl;
    if (!htmlEl) return;

	var p = Dwt.toWindow(htmlEl);
	// Gotta do what mousedown listener does
	var mev = DwtShell.mouseEvent;
	mev.reset();
	mev.target = htmlEl;
	mev.button = DwtMouseEvent.LEFT;
	mev.docX = p.x;
	mev.docY = p.y;
	DwtButton._dropDownCellMouseDownHdlr(mev);
};

/** This method is called from mouseUpHdl in <i>DwtControl</i>. */
DwtButton.prototype._focusByMouseUpEvent =
  function()  {
	DBG.println(AjxDebug.DBG3, "DwtButton.prototype._focusByMouseUpEvent");
	DwtShell.getShell(window).getKeyboardMgr().grabFocus(this.getTabGroupMember());
  }

// NOTE: _focus and _blur will be reworked to reflect styles correctly
DwtButton.prototype._focus =
function() {
	//DBG.println("DwtButton.prototype._focus");
    this.setDisplayState(DwtControl.FOCUSED);
}

DwtButton.prototype._blur =
function() {
	//DBG.println("DwtButton.prototype._blur");
    this.setDisplayState(DwtControl.NORMAL);
}

DwtButton.prototype._toggleMenu =
function () {
	if (this._shouldToggleMenu){
        var menu = this.getMenu();
        if (!menu.isPoppedup()){
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

// Activates the button.
DwtButton.prototype._mouseOverListener =
function(ev) {
    if (this._hoverImageInfo) {

	    // if the button is image-only, the following is bad
	    // because DwtLabel#setImage clears the element first
	    // (innerHTML = "") causing a mouseout event, then it
	    // re-sets the image, which results in a new mouseover
	    // event, thus looping forever eating your CPU and
	    // blinking.

	    // this.setImage(this._hoverImageInfo); // sucks.

	    // hope I'm not breaking anything (mihai@zimbra.com):

	    var iconEl = this._getIconEl();
	    iconEl.firstChild.className = AjxImg.getClassForImage(this._hoverImageInfo);
    }
    this.setDisplayState(DwtControl.HOVER);

    var dropDown = this._dropDownEl;
    if (this._menu && dropDown && this._dropDownHovImg && !this.noMenuBar &&
        this.isListenerRegistered(DwtEvent.SELECTION)) {
		AjxImg.setImage(dropDown, this._dropDownHovImg);
    }

    ev._stopPropagation = true;
}

// Triggers the button.
DwtButton.prototype._mouseDownListener =
function(ev) {
	if (ev.button != DwtMouseEvent.LEFT)
		return;

    var dropDown = this._dropDownEl;
    if (this._menu && dropDown && this._dropDownDepImg) {
		AjxImg.setImage(dropDown, this._dropDownDepImg);
    }
	switch (this._actionTiming) {
	  case DwtButton.ACTION_MOUSEDOWN:
		this.trigger();
		if (this.isListenerRegistered(DwtEvent.SELECTION)) {
			var selEv = DwtShell.selectionEvent;
                       DwtUiEvent.copy(selEv, ev);
                       selEv.item = this;
                       selEv.detail = typeof this.__detail == "undefined" ? 0 : this.__detail;
                       this.notifyListeners(DwtEvent.SELECTION, selEv);
		} else if (this._menu) {
			this._toggleMenu();
		}
		break;
	  case DwtButton.ACTION_MOUSEUP:
		this.trigger();
		break;
	}
}

DwtButton.prototype.trigger =
function (){
    if (this._depressedImageInfo) {
        this.setImage(this._depressedImageInfo);
    }
    this.setDisplayState(DwtControl.ACTIVE, true);
    this.isActive = true;
};

DwtButton.prototype.deactivate =
function (){
	if (this._hoverImageInfo){
		this.setImage(this._hoverImageInfo);
	}

	if (this._style & DwtButton.TOGGLE_STYLE){
		this._selected = !this._selected;
	}
    this.setDisplayState(DwtControl.HOVER);
};

DwtButton.prototype.dontStealFocus = function(val) {
	if (val == null)
		val = true;
	if (this._menu instanceof DwtMenu)
		this._menu.dontStealFocus(val);
	this.__preventMenuFocus = val;
};

// Button has been pressed, notify selection listeners.
DwtButton.prototype._mouseUpListener =
function(ev) {
	if (ev.button != DwtMouseEvent.LEFT)
		return;

    var dropDown = this._dropDownEl;
    if (this._menu && dropDown && this._dropDownHovImg && !this.noMenuBar){
		AjxImg.setImage(dropDown, this._dropDownHovImg);
    }
	switch (this._actionTiming) {
	  case DwtButton.ACTION_MOUSEDOWN:
 	    this.deactivate();
		break;

	  case DwtButton.ACTION_MOUSEUP:
	    var el = this.getHtmlElement();
		if (this.isActive) {
			this.deactivate();
			if (this.isListenerRegistered(DwtEvent.SELECTION)) {
				var selEv = DwtShell.selectionEvent;
				DwtUiEvent.copy(selEv, ev);
				selEv.item = this;
				selEv.detail = typeof this.__detail == "undefined" ? 0 : this.__detail;
				this.notifyListeners(DwtEvent.SELECTION, selEv);
			} else if (this._menu) {
				this._toggleMenu();
			}
		}
		// So that listeners may remove this object from the flow, and not
		// get errors, when DwtControl tries to do a this.getHtmlElement()
		// ROSSD - I don't get this, basically this method does a this.getHtmlElement as the first thing it does
		// so why would the line below cause a problem. It does have the side-effect of making buttons behave weirdly
		// in that they will not remain active on mouse up
		//el.className = this._origClassName;
		break;
	}
};

DwtButton.prototype._setMouseOutClassName =
function() {
    this.setDisplayState(DwtControl.NORMAL);
}

// Button no longer hovered/active.
DwtButton.prototype._mouseOutListener =
function(ev) {
    if (this._hoverImageInfo) {
        this.setImage(this._enabledImageInfo);
    }
	this._setMouseOutClassName();
    this.isActive = false;

    var dropDown = this._dropDownEl;
    if (this._menu && dropDown) {
		AjxImg.setImage(dropDown, this._dropDownImg);
    }
}


// Pops up the dropdown menu.
DwtButton._dropDownCellMouseDownHdlr =
function(ev) {
	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
    /**
     * Below condition added for the bug 17089
     * If menu is there and already popped up, do pop it down first and then proceed.
     */
    if(obj && obj.getMenu() && obj.getMenu().isPoppedup && obj.getMenu().isPoppedup()){
        obj.getMenu().popdown();
        //return; //
    }

    var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);

	if (mouseEv.button == DwtMouseEvent.LEFT) {
	    if (this._depImg){
			AjxImg.setImage(this, this._depImg);
	    }

		DwtEventManager.notifyListeners(DwtEvent.ONMOUSEDOWN, mouseEv);

		if (obj._menu instanceof AjxCallback) {
			obj.popup();
		}

		if (obj._dropDownEvtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
	    	var selEv = DwtShell.selectionEvent;
	    	DwtUiEvent.copy(selEv, mouseEv);
	    	selEv.item = obj;
	    	obj._dropDownEvtMgr.notifyListeners(DwtEvent.SELECTION, selEv);
	    } else if (mouseEv.button == DwtMouseEvent.LEFT) {
			obj._toggleMenu();
		}
	}

	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;
}

// Updates the current mouse event (set from the previous mouse down).
DwtButton._dropDownCellMouseUpHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);

	if (mouseEv.button == DwtMouseEvent.LEFT) {
	    if (this._hovImg && !this.noMenuBar) {
			AjxImg.setImage(this, this._hovImg);
	    }
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;
}

DwtButton.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtLabel.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._dropDownEl = document.getElementById(data.id+"_dropdown");
};
