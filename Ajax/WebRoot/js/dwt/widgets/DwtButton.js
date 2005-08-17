/**
* Creates a button.
* @constructor
* @class
* This class represents a button, which is basically a smart label that can handle
* various UI events. It knows when it has been activated (the mouse is over it),
* when it has been triggered (mouse down), and when it has been pressed (mouse up).
* In addition to a label's image and/or text, a button may have a dropdown menu.
*
* @author Ross Dargahi
* @author Conrad Damon
* @param parent		the parent widget
* @param style		the label style (see DwtLabel)
* @param className	a CSS class
* @param posStyle	positioning style
*/
function DwtButton(parent, style, className, posStyle, actionTiming) {
	if (arguments.length == 0) return;
	className = className || "DwtButton";
	DwtLabel.call(this, parent, style, className, posStyle);

	// CSS classes to handle activated/triggered states
	this._origClassName = className;
	this._activatedClassName = this._className + "-" + DwtCssStyle.ACTIVATED;
	this._triggeredClassName = this._className + "-" + DwtCssStyle.TRIGGERED;
	this._toggledClassName = this._className + "-" + DwtCssStyle.TOGGLED;

	// add custom mouse handlers to standard ones
	this._setMouseEventHdlrs();
	this._setIERolloverEventHdlrs();
	this._setKeyEventHdlrs();
	this._mouseOverListener = new LsListener(this, DwtButton.prototype._mouseOverListener);
	this._mouseOutListener = new LsListener(this, DwtButton.prototype._mouseOutListener);
	this._mouseDownListener = new LsListener(this, DwtButton.prototype._mouseDownListener);
	this._mouseUpListener = new LsListener(this, DwtButton.prototype._mouseUpListener);
	this._addMouseListeners();
	
	this._dropDownEvtMgr = new LsEventMgr();

	this._toggled = false;

	this._actionTiming = actionTiming? actionTiming : DwtButton.ACTION_MOUSEUP;
}

DwtButton.prototype = new DwtLabel;
DwtButton.prototype.constructor = DwtButton;

DwtButton.TOGGLE_STYLE = DwtLabel._LAST_STYLE * 2;
DwtButton.ACTION_MOUSEUP = 1;
DwtButton.ACTION_MOUSEDOWN = 2;
// Public methods

DwtButton.prototype.toString = 
function() {
	return "DwtButton";
}

/**
* Adds a listener to be notified when the button is pressed.
*
* @param listener	a listener
*/
DwtButton.prototype.addSelectionListener = 
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
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
	this._dropDownEvtMgr.addListener(DwtEvent.SELECTION, listener);
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

DwtButton.prototype.setDropDownImages = function (enabledImg, disImg, hovImg, depImg) {
	this._dropDownImg = enabledImg;
	this._dropDownDisImg = disImg;
	this._dropDownHovImg = hovImg;
	this._dropDownDepImg = depImg;
};

/**
 * add all mouse listeners for our widget - this uses mouseenter and mouseleave
 * for IE.
 */
DwtButton.prototype._addMouseListeners = 
function() {
	if (LsEnv.isIE)	{ 
		this.addListener(DwtEvent.ONMOUSEENTER, this._mouseOverListener);
	} else {
		this.addListener(DwtEvent.ONMOUSEOVER, this._mouseOverListener);
	}
	if (LsEnv.isIE) {
		this.addListener(DwtEvent.ONMOUSELEAVE, this._mouseOutListener);
	} else {
		this.addListener(DwtEvent.ONMOUSEOUT, this._mouseOutListener);
	}
	this.addListener(DwtEvent.ONMOUSEDOWN, this._mouseDownListener);
	this.addListener(DwtEvent.ONMOUSEUP, this._mouseUpListener);
};

/**
 * remove all mouse listeners from our widget
 */
DwtButton.prototype._removeMouseListeners =
function() {
	if (LsEnv.isIE) {
		this.removeListener(DwtEvent.ONMOUSEENTER, this._mouseOverListener);
	} else {
		this.removeListener(DwtEvent.ONMOUSEOVER, this._mouseOverListener);
	}
	if (LsEnv.isIE) {
		this.removeListener(DwtEvent.ONMOUSELEAVE, this._mouseOutListener);
	} else {
		this.removeListener(DwtEvent.ONMOUSEOUT, this._mouseOutListener);
	}
	this.removeListener(DwtEvent.ONMOUSEDOWN, this._mouseDownListener);
	this.removeListener(DwtEvent.ONMOUSEUP, this._mouseUpListener);
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
				LsImg.setImage(this._dropDownCell, this._dropDownImg);
			}
		} else {
			this.setClassName(this._origClassName); // clear activated or triggered
			this._removeMouseListeners();
			// remove event handlers for pull down menu if applicable
			if (this._menu) {
				this._removeDropDownCellMouseHandlers();
				LsImg.setImage(this._dropDownCell, this._dropDownDisImg);
			}
		}
	}
}

DwtButton.prototype.setHoverImage =
function (hoverImageInfo) {
    this._hoverImageInfo = hoverImageInfo;
}
/**
* Adds a dropdown menu to the button, available through a small down-arrow.
*
* @param menu				the dropdown menu
* @param shouldToggle
* @param followIconStyle	style of menu item (should be checked or radio style) for
*							which the button icon should reflect the menu item icon
*/
DwtButton.prototype.setMenu =
	function(menu, shouldToggle, followIconStyle) {
	this._menu = menu;
	this._shouldToggleMenu = (shouldToggle === true);
	this._followIconStyle = followIconStyle;
	if (menu && !this._dropDownCell) {
		var idx = (this._imageCell) ? 1 : 0;
		if (this._textCell)
			idx++;
		this._dropDownCell = this._row.insertCell(idx);
		this._dropDownCell.id = Dwt.getNextId();
		this._dropDownCell.className = "dropDownCell";

		if (this._dropDownImg == null) this._dropDownImg = DwtImg.SELECT_PULL_DOWN;
		if (this._dropDownDisImg == null) this._dropDownDisImg = DwtImg.SELECT_PULL_DOWN_DISABLED;
		if (this._dropDownHovImg == null) this._dropDownHovImg = DwtImg.SELECT_PULL_DOWN_ENABLED;
		LsImg.setImage(this._dropDownCell, this._dropDownImg);

		this._menu.setAssociatedElementId(this._dropDownCell.id);
		// set event handler if applicable
		if (this._enabled) {
			this._setupDropDownCellMouseHandlers();
		}
	} else if (!menu && this._dropDownCell) {
		this._row.deleteCell(this._dropDownCell.cellIndex);
		this._dropDownCell = null;
	}
}

DwtButton.prototype._setupDropDownCellMouseHandlers = function () {
	this._dropDownCell.onmousedown = DwtButton._dropDownCellMouseDownHdlr;
	this._dropDownCell.onmouseup = DwtButton._dropDownCellMouseUpHdlr;
};

DwtButton.prototype._removeDropDownCellMouseHandlers = function () {
	this._dropDownCell.onmousedown = null;
	this._dropDownCell.onmouseup = null;
};
/**
* Returns the button's menu
*/
DwtButton.prototype.getMenu =
function() {
	return this._menu;
}

/**
* Returns the button display to normal (not activated or triggered).
*/
DwtButton.prototype.resetClassName = 
function() {
	this.setClassName(this._origClassName);	
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
* Activates/inactivates the button. A button is activated when the mouse is over it.
*
* @param activated		whether the button is activated
*/
DwtButton.prototype.setActivated =
function(activated) {
	if (activated) {
		this.setClassName(this._activatedClassName);
	} else {
		this.setClassName(this._origClassName);
	}
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

DwtButton.prototype.setToggled =
function(toggled) {
	if ((this._style & DwtButton.TOGGLE_STYLE) && this._toggled != toggled) {
		this._toggled = toggled;
		this.setClassName((toggled) ? this._toggledClassName : this._origClassName);
	}
}

// Private methods

DwtButton.prototype._toggleMenu =
function () {
	if (this._shouldToggleMenu){
		if (!this._menu.isPoppedup()){
			this._menu.popup();
			this._menuUp = true;
		} else {
			this._menu.popdown();
			this._menuUp = false;
		}
	} else {
		this._menu.popup();
	}
};

// Activates the button.
DwtButton.prototype._mouseOverListener = 
function(ev) {
    if (this._hoverImageInfo) {
        this.setImage(this._hoverImageInfo);
    }
    this.setClassName(this._activatedClassName);
    if (this._dropDownCell && this._dropDownHovImg && !this.noMenuBar) {
		LsImg.setImage(this._dropDownCell, this._dropDownHovImg);
    }
    ev._stopPropagation = true;
}

// Triggers the button.
DwtButton.prototype._mouseDownListener = 
function(ev) {
    if (this._dropDownCell && this._dropDownDepImg) {
		LsImg.setImage(this._dropDownCell, this._dropDownDepImg);
    }
	switch (this._actionTiming) {
	  case DwtButton.ACTION_MOUSEDOWN:
		var el = this.getHtmlElement();
		this.trigger();
		if (this.isListenerRegistered(DwtEvent.SELECTION)) {
			var selEv = DwtShell.selectionEvent;
                       DwtUiEvent.copy(selEv, ev);
                       selEv.item = this;
                       selEv.detail = 0;
                       this.notifyListeners(DwtEvent.SELECTION, selEv);
		} else if (this._menu) {
			this._toggleMenu();
		}
		// So that listeners may remove this object from the flow, and not
		// get errors, when DwtControl tries to do a this.getHtmlElement ()
		// ROSSD - I don't get this, basically this method does a 
		// this.getHtmlElement as the first thing it does
		// so why would the line below cause a problem. It does have the
		// side-effect of making buttons behave weirdly
		// in that they will not remain active on mouse up
		//el.className = this._origClassName;
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
	this.setClassName(this._triggeredClassName);
	this.isTriggered = true;	
};

DwtButton.prototype.deactivate =
function (){
	if (this._depressedImageInfo){
		this.setImage(this._hoverImageInfo);
	}
	
	if (this._style & DwtButton.TOGGLE_STYLE){
		this._toggled = !this._toggled;
	}
	this.setClassName((!this._toggled) ? this._activatedClassName : 
					  this._toggledClassName);
};

// Button has been pressed, notify selection listeners.
DwtButton.prototype._mouseUpListener = 
function(ev) {
    if (this._dropDownCell && this._dropDownHovImg && !this.noMenuBar){
		LsImg.setImage(this._dropDownCell, this._dropDownHovImg);
    }	
	switch (this._actionTiming) {
	  case DwtButton.ACTION_MOUSEDOWN:
 	    this.deactivate();
		break;

	  case DwtButton.ACTION_MOUSEUP:
	    var el = this.getHtmlElement();
		if (this.isTriggered) {
			this.deactivate();
			if (this.isListenerRegistered(DwtEvent.SELECTION)) {
				var selEv = DwtShell.selectionEvent;
				DwtUiEvent.copy(selEv, ev);
				selEv.item = this;
				selEv.detail = 0;
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
    this.setClassName((this._toggled) ? this._toggledClassName : this._origClassName);
    this.isTriggered = false;
}

// Button no longer activated/triggered.
DwtButton.prototype._mouseOutListener = 
function(ev) {
    if (this._hoverImageInfo) {
        this.setImage(this._enabledImageInfo);
    }
	this._setMouseOutClassName();

    if (this._dropDownCell){
		LsImg.setImage(this._dropDownCell, this._dropDownImg);
    }	
}


// Pops up the dropdown menu.
DwtButton._dropDownCellMouseDownHdlr = 
function(ev) {
    if (this._depImg){
		LsImg.setImage(this, this._depImg);
    }	

	DwtEventManager.notifyListeners(DwtEvent.ONMOUSEDOWN, ev);
	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);

	if (obj._dropDownEvtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
    	var selEv = DwtShell.selectionEvent;
    	DwtUiEvent.copy(selEv, mouseEv);
    	selEv.item = obj;
    	obj._dropDownEvtMgr.notifyListeners(DwtEvent.SELECTION, selEv);
        
    } else if (mouseEv.button == DwtMouseEvent.LEFT) {
		obj._toggleMenu();
	}

	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;
}

// Updates the current mouse event (set from the previous mouse down).
DwtButton._dropDownCellMouseUpHdlr = 
function(ev) {
    if (this._hovImg && !this.noMenuBar) {
		LsImg.setImage(this, this._hovImg);
    }	

	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;
}

