/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2012, 2013 Zimbra Software, LLC.
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
 * @overview
 * This file contains a base Dwt dialog control.
 * 
 */

/**
 * @class
 * This is a base class for dialogs. Given content, this class will take care of 
 * showing and hiding the dialog, as well as dragging it.
 * <p>
 * Dialogs always hang off the main shell since their stacking order is managed through z-index.
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 * 
 * @param {hash}	params		a hash of parameters
 * @param	{DwtComposite}	params.parent	the parent widget (the shell)
 * @param	{string}	params.className		the CSS class
 * @param	{string}	params.title		the title
 * @param	{number}	[params.zIndex=Dwt.Z_DIALOG]		the z-index to set for this dialog when it is visible
 * @param	{DwtBaseDialog.MODAL|DwtBaseDialog.MODELESS}	[params.mode=DwtBaseDialog.MODAL] 		the modality of the dialog
 * @param	{DwtPoint}	params.loc			the location at which to popup the dialog. Defaults to being centered within its parent
 * @param	{DwtControl}	params.view 		the the control whose element is to be re-parented
 * @param	{string}	params.dragHandleId 		the the ID of element used as drag handle
 * 
 * @extends	DwtComposite
 */
DwtBaseDialog = function(params) {
	if (arguments.length == 0) { return; }

	params = Dwt.getParams(arguments, DwtBaseDialog.PARAMS);
	var parent = params.parent;
	if (!(parent instanceof DwtShell)) {
		throw new DwtException("DwtBaseDialog parent must be a DwtShell", DwtException.INVALIDPARENT, "DwtDialog");
	}
	params.className = params.className || "DwtBaseDialog";
	params.posStyle = DwtControl.ABSOLUTE_STYLE;
	this._title = params.title || "";

	DwtComposite.call(this, params);

	this._shell = parent;
	this._zIndex = params.zIndex || Dwt.Z_DIALOG;
	this._mode = params.mode || DwtBaseDialog.MODAL;
	
	this._loc = new DwtPoint();
	if (params.loc) {
		this._loc.x = params.loc.x;
		this._loc.y = params.loc.y
	} else {
		this._loc.x = this._loc.y = Dwt.LOC_NOWHERE;
	}
	
	// Default dialog tab group.
	this._tabGroup = new DwtTabGroup(this.toString());

    this._dragHandleId = params.dragHandleId || this._htmlElId + "_handle";
	this._createHtml();
    this._initializeDragging(this._dragHandleId);

	if (params.view) {
		this.setView(params.view);
    }

	// reset tab index
    this.setZIndex(Dwt.Z_HIDDEN); // not displayed until popup() called
	this._position(DwtBaseDialog.__nowhereLoc);
}

/**
 * @private
 */
DwtBaseDialog.PARAMS = ["parent", "className", "title", "zIndex", "mode", "loc", "view", "dragHandleId", "id"];

DwtBaseDialog.prototype = new DwtComposite;
DwtBaseDialog.prototype.constructor = DwtBaseDialog;

/**
 * Returns a string representation of the class.
 * 
 * @return {string}		a string representation of the class
 */
DwtBaseDialog.prototype.toString =
function() {
	return "DwtBaseDialog";
};

//
// Constants
//

// modes

/**
 * Defines a "modeless" dialog.
 */
DwtBaseDialog.MODELESS = 1;

/**
 * Defines a "modal" dialog.
 */
DwtBaseDialog.MODAL = 2;

/**
 * @private
 */
DwtBaseDialog.__nowhereLoc = new DwtPoint(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);

//
// Data
//

/**
 * @private
 */
DwtBaseDialog.prototype.TEMPLATE = "dwt.Widgets#DwtBaseDialog";

/**
 * <strong>Note:</strong>
 * This member variable will be set by sub-classes that want a control bar
 * to appear below the dialog contents.
 * 
 * @private
 */
DwtBaseDialog.prototype.CONTROLS_TEMPLATE = null;

//
// Public methods
//

/**
 * Adds a popup listener.
 * 
 * @param		{AjxListener}	listener		the listener to add
 */
DwtBaseDialog.prototype.addPopupListener =
function(listener) {
	this.addListener(DwtEvent.POPUP, listener);
}

/**
 * Removes a popup listener.
 * 
 * @param		{AjxListener}	listener		the listener to remove
 */
DwtBaseDialog.prototype.removePopupListener = 
function(listener) {
	this.removeListener(DwtEvent.POPUP, listener);
}

/**
 * Adds a popdown listener.
 * 
 * @param		{AjxListener}	listener		the listener to add
 */
DwtBaseDialog.prototype.addPopdownListener = 
function(listener) {
	this.addListener(DwtEvent.POPDOWN, listener);
}

/**
 * Removes a popdown listener.
 * 
 * @param		{AjxListener}	listener		the listener to remove
 */
DwtBaseDialog.prototype.removePopdownListener = 
function(listener) {
	this.removeListener(DwtEvent.POPDOWN, listener);
}

/**
 * Pops-up the dialog, makes the dialog visible in places. Everything under the dialog will
 * become veiled if we are modal. Note: popping up a dialog will block
 * keyboard actions from being delivered to the global key action handler (if one
 * is registered).
 *
 * @param {DwtPoint}		loc		the desired location
 */
DwtBaseDialog.prototype.popup =
function(loc) {
	if (this._poppedUp) { return; }

	this.cleanup(true);
	var thisZ = this._zIndex;

	// if we're modal, setup the veil effect, and track which dialogs are open
	if (this._mode == DwtBaseDialog.MODAL) {
		thisZ = this._setModalEffect(thisZ);
	}

	this._shell._veilOverlay.activeDialogs.push(this);
	
	// use whichever has a value, local has precedence
	if (loc) {
		this._loc.x = loc.x;
		this._loc.y = loc.y;
	}
	this._position(loc);

    //reset TAB focus before popup of dialog.
    //method be over-written to focus a different member.
    this._resetTabFocus();

	this.setZIndex(thisZ);
	this._poppedUp = true;

	// Push our tab group
	var kbMgr = this._shell.getKeyboardMgr();
	kbMgr.pushTabGroup(this._tabGroup);
	kbMgr.pushDefaultHandler(this);

	this.notifyListeners(DwtEvent.POPUP, this);
};

/**
 * @private
 */
DwtBaseDialog.prototype._resetTabFocus =
function(){
    this._tabGroup.resetFocusMember(true);
};

DwtBaseDialog.prototype.focus = 
function () {
	// if someone is listening for the focus to happen, give control to them,
	// otherwise focus on this dialog.
	if (this.isListenerRegistered(DwtEvent.ONFOCUS)) {
		this.notifyListeners(DwtEvent.ONFOCUS);
	} else if (this._focusElementId){
		var focEl = document.getElementById(this._focusElementId);
		if (focEl) {
			focEl.focus();
		}
	}
};

/**
 * Checks if the dialog is popped-up.
 * 
 * @return	{boolean}	<code>true</code> if the dialog is popped-up; <code>false</code> otherwise
 */
DwtBaseDialog.prototype.isPoppedUp =
function () {
	return this._poppedUp;
};

/**
 * Pops-down and hides the dialog.
 * 
 */
DwtBaseDialog.prototype.popdown =
function() {

	if (this._poppedUp) {
		this._poppedUp = false;
		this.cleanup(false);
	
		//var myZIndex = this.getZIndex();
	    var myZIndex = this._zIndex;
		this.setZIndex(Dwt.Z_HIDDEN);
		//TODO we should not create an object everytime we popdown a dialog (ditto w/popup)
		this._position(DwtBaseDialog.__nowhereLoc);
		if (this._mode == DwtBaseDialog.MODAL) {
			this._undoModality(myZIndex);
		} else {
			this._shell._veilOverlay.activeDialogs.pop();
		}
		//this.removeKeyListeners();
		
		// Pop our tab group
		var kbMgr = this._shell.getKeyboardMgr();
		kbMgr.popTabGroup(this._tabGroup);
		kbMgr.popDefaultHandler();
		
		this.notifyListeners(DwtEvent.POPDOWN, this);
	}
};

/**
 * Sets the content of the dialog to a new view. Essentially re-parents
 * the supplied control's HTML element to the dialogs HTML element
 * 
 * @param {DwtControl} newView		the control whose element is to be re-parented
 */
DwtBaseDialog.prototype.setView =
function(newView) {
	this.reset();
	if (newView) {
		this._getContentDiv().appendChild(newView.getHtmlElement());
	}
};

/**
 * Resets the dialog back to its original state. Subclasses should override this method
 * to add any additional behavior, but should still call up into this method.
 * 
 */
DwtBaseDialog.prototype.reset =
function() {
	this._loc.x = this._loc.y = Dwt.LOC_NOWHERE;
}

/**
* Cleans up the dialog so it can be used again later.
* 
* @param	{boolean}		bPoppedUp		if <code>true</code>, the dialog is popped-up; <code>false</code> otherwise
*/
DwtBaseDialog.prototype.cleanup =
function(bPoppedUp) {
	//TODO handle different types of input fields e.g. checkboxes etc
	var inputFields = this._getInputFields();
	
	if (inputFields) {
		var len = inputFields.length;
		for (var i = 0; i < len; i++) {
			inputFields[i].disabled = !bPoppedUp;
			if (bPoppedUp)
				inputFields[i].value = "";
		}
	}
}

/**
 * Sets the title.
 * 
 * @param	{string}		title		the title
 */
DwtBaseDialog.prototype.setTitle =
function(title) {
    if (this._titleEl) {
        this._titleEl.innerHTML = title || "";
    }
};

/**
* Sets the dialog content (below the title, above the buttons).
*
* @param {string}		text		the dialog content
*/
DwtBaseDialog.prototype.setContent =
function(text) {
	var d = this._getContentDiv();
	if (d) {
		d.innerHTML = text || "";
	}
}

/**
 * @private
 */
DwtBaseDialog.prototype._getContentDiv =
function() {
	return this._contentEl;
};

/**
 * Adds an enter listener.
 * 
 * @param	{AjxListener}	listener		the listener to add
 */
DwtBaseDialog.prototype.addEnterListener =
function(listener) {
	this.addListener(DwtEvent.ENTER, listener);
};

/**
 * Gets the active dialog.
 * 
 * @return	{DwtBaseDialog}		the active dialog
 */
DwtBaseDialog.getActiveDialog = 
function() {
	var dialog = null;
	var shellObj = DwtShell.getShell(window);
	if (shellObj) {
		var len = shellObj._veilOverlay.activeDialogs.length;
		if (len > 0) {
			dialog = shellObj._veilOverlay.activeDialogs[len - 1];
		}
	}
	return dialog;
};

//
// Protected methods
//

/**
 * @private
 */
DwtBaseDialog.prototype._initializeDragging =
function(dragHandleId) {
	var dragHandle = document.getElementById(dragHandleId);
	if (dragHandle) {
		var control = DwtControl.fromElementId(window._dwtShellId);
		if (control) {
			var p = Dwt.getSize(control.getHtmlElement());
			var dragObj = document.getElementById(this._htmlElId);
			var size = this.getSize();
			var dragEndCb = new AjxCallback(this, this._dragEnd);
			var dragCb = new AjxCallback(this, this._duringDrag);
			var dragStartCb = new AjxCallback(this, this._dragStart);

			DwtDraggable.init(dragHandle, dragObj, 0,
							  document.body.offsetWidth - 10, 0, document.body.offsetHeight - 10, dragStartCb, dragCb, dragEndCb);
		}
	}
};

/**
 * @private
 */
DwtBaseDialog.prototype._getContentHtml =
function() {
    return "";
};

/**
 * @private
 */
DwtBaseDialog.prototype._createHtml = function(templateId) {
    var data = { id: this._htmlElId };
    this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

/**
 * @private
 */
DwtBaseDialog.prototype._createHtmlFromTemplate = function(templateId, data) {
    // set default params
    data.dragId = this._dragHandleId;
    data.title = this._title;
    data.icon = "";
    data.closeIcon1 = "";
    data.closeIcon2 = "";
    data.controlsTemplateId = this.CONTROLS_TEMPLATE;

    // expand template
    DwtComposite.prototype._createHtmlFromTemplate.call(this, templateId, data);

    // remember elements
    this._titleBarEl = document.getElementById(data.id+"_titlebar");
    this._titleEl = document.getElementById(data.id+"_title");
    this._contentEl = document.getElementById(data.id+"_content");

    // NOTE: This is for backwards compatibility. There are just
    //       too many sub-classes of dialog that expect to return
    //       the dialog contents via the _getContentHtml method.
    this.setContent(this._getContentHtml());
};

/**
 * @private
 */
DwtBaseDialog.prototype._setModalEffect =
function() {
	// place veil under this dialog
	var dialogZ = this._shell._veilOverlay.dialogZ;
	var currentDialogZ = null;
	var thisZ, veilZ;
	if (dialogZ.length)
		currentDialogZ = dialogZ[dialogZ.length - 1];
	if (currentDialogZ) {
		thisZ = currentDialogZ + 2;
		veilZ = currentDialogZ + 1;
	} else {
		thisZ = this._zIndex;
		veilZ = Dwt.Z_VEIL;
	}
	this._shell._veilOverlay.veilZ.push(veilZ);
	this._shell._veilOverlay.dialogZ.push(thisZ);
	Dwt.setZIndex(this._shell._veilOverlay, veilZ);
	return thisZ;
};

/**
 * @private
 */
DwtBaseDialog.prototype._undoModality =
function (myZIndex) {
	var veilZ = this._shell._veilOverlay.veilZ;
	veilZ.pop();
	var newVeilZ = veilZ[veilZ.length - 1];
	Dwt.setZIndex(this._shell._veilOverlay, newVeilZ);
	this._shell._veilOverlay.dialogZ.pop();
	this._shell._veilOverlay.activeDialogs.pop();
	if (this._shell._veilOverlay.activeDialogs.length > 0 ) {
		this._shell._veilOverlay.activeDialogs[0].focus();
	}
};

/**
 * Subclasses should implement this method to return an array of input fields that
 * they want to be cleaned up between instances of the dialog being popped up and
 * down
 * 
 * @return An array of the input fields to be reset
 */
DwtBaseDialog.prototype._getInputFields = 
function() {
	// overload me
}

/**
 * @private
 */
DwtBaseDialog.prototype._dragStart = 
function (x, y){
	// fix for bug 3177
	if (AjxEnv.isNav && !this._ignoreSetDragBoundries) {
		this._currSize = this.getSize();
		DwtDraggable.setDragBoundaries(DwtDraggable.dragEl, 0, document.body.offsetWidth - this._currSize.x, 0, 
									   document.body.offsetHeight - this._currSize.y);
	}
};

/**
 * @private
 */
DwtBaseDialog.prototype._dragEnd =
function(x, y) {
 	// save dropped position so popup(null) will not re-center dialog box
	this._loc.x = x;
	this._loc.y = y;
}

/**
 * @private
 */
DwtBaseDialog.prototype._duringDrag =
function(x, y) {
	// overload me
};

/**
 * @private
 */
DwtBaseDialog.prototype._doesContainElement = 
function (element) {
	return Dwt.contains(this.getHtmlElement(), element);
};
