/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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
 * This file contains classes for a Dwt dialog pop-up.
 */

/**
 * @class
 * This class represents a popup dialog with a title and standard buttons.
 * A client or subclass sets the dialog content. Dialogs always hang-off the main shell
 * since their stacking order is managed through z-index.
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 *
 * @param {hash}		params			a hash of parameters
 * @param	{DwtComposite}		params.parent			 		the parent widget (the shell)
 * @param	{string}	params.className					the CSS class
 * @param	{string}	params.title						the title of dialog
 * @param	{array|constant}	params.standardButtons		an array of standard buttons to include. Defaults to {@link DwtDialog.OK_BUTTON} and {@link DwtDialog.CANCEL_BUTTON}.
 * @param	{array}	params.extraButtons		  			a list of {@link DwtDialog_ButtonDescriptor} objects describing custom buttons to add to the dialog
 * @param	{number}	params.zIndex							the z-index to set for this dialog when it is visible. Defaults to {@link Dwt.Z_DIALOG}.
 * @param	{DwtDialog.MODELESS|DwtDialog.MODAL}	params.mode 						the modality of the dialog. Defaults to {@link DwtDialog.MODAL}.
 * @param	{DwtPoint}		params.loc						the location at which to popup the dialog. Defaults to centered within its parent.
 * 
 * @see		DwtDialog.CANCEL_BUTTON
 * @see		DwtDialog.OK_BUTTON
 * @see		DwtDialog.DISMISS_BUTTON
 * @see		DwtDialog.NO_BUTTON
 * @see		DwtDialog.YES_BUTTON
 * @see		DwtDialog.ALL_BUTTONS
 * @see		DwtDialog.NO_BUTTONS
 * 
 * @extends	DwtBaseDialog
 */
DwtDialog = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtDialog.PARAMS);
	params.className = params.className || "DwtDialog";
	this._title = params.title = params.title || "";

	// standard buttons default to OK / Cancel
	var standardButtons = params.standardButtons;
	var extraButtons = params.extraButtons;
	if (!standardButtons) {
		standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	} else if (standardButtons == DwtDialog.NO_BUTTONS) {
		standardButtons = null;
	} else if (standardButtons && !standardButtons.length) {
		standardButtons = [standardButtons];
	}
	
	// assemble the list of button IDs, and the list of button descriptors
	this._buttonList = [];
	var buttonOrder = {};
	buttonOrder[DwtDialog.ALIGN_LEFT] = [];
	buttonOrder[DwtDialog.ALIGN_CENTER] = [];
	buttonOrder[DwtDialog.ALIGN_RIGHT] = [];
	if (standardButtons || extraButtons) {
		this._buttonDesc = {};
		if (standardButtons && standardButtons.length) {
			this._initialEnterButtonId = this._enterButtonId = standardButtons[0];
			for (var i = 0; i < standardButtons.length; i++) {
				var buttonId = standardButtons[i];
				this._buttonList.push(buttonId);
				var align = DwtDialog.ALIGN[buttonId];
				if (align) {
					buttonOrder[align].push(buttonId);
				}
				// creating standard button descriptors on file read didn't work, so we create them here
				this._buttonDesc[buttonId] = new DwtDialog_ButtonDescriptor(buttonId, AjxMsg[DwtDialog.MSG_KEY[buttonId]], align);
			}
			// set standard callbacks
			this._resetCallbacks();
		}
		if (extraButtons && extraButtons.length) {
			if (!this._enterButtonId) {
				this._initialEnterButtonId = this._enterButtonId = extraButtons[0];
			}
			for (var i = 0; i < extraButtons.length; i++) {
				var buttonId = extraButtons[i].id;
				this._buttonList.push(buttonId);
				var align = extraButtons[i].align;
				if (align) {
					buttonOrder[align].push(buttonId);
				}
				this._buttonDesc[buttonId] = extraButtons[i];
			}
		}
	}

	// get button IDs
	this._buttonElementId = {};
	for (var i = 0; i < this._buttonList.length; i++) {
		var buttonId = this._buttonList[i];
		//this._buttonElementId[this._buttonList[i]] = params.id + "_button" + buttonId + "_cell";
		this._buttonElementId[buttonId] = this._buttonDesc[buttonId].label? this._buttonDesc[buttonId].label + "_" + Dwt.getNextId():Dwt.getNextId();
	}

	DwtBaseDialog.call(this, params);

	// set up buttons
	this._button = {};
	for (var i = 0; i < this._buttonList.length; i++) {
		var buttonId = this._buttonList[i];
		var b = this._button[buttonId] = new DwtButton({parent:this,id:this._htmlElId+"_button"+buttonId});
		b.setText(this._buttonDesc[buttonId].label);
		b.buttonId = buttonId;
		b.addSelectionListener(new AjxListener(this, this._buttonListener));
		var el = document.getElementById(this._buttonElementId[buttonId]);
		if (el) {
			el.appendChild(b.getHtmlElement());
		}
	}
	// add to tab group, in order
	var list = buttonOrder[DwtDialog.ALIGN_LEFT].concat(buttonOrder[DwtDialog.ALIGN_CENTER], buttonOrder[DwtDialog.ALIGN_RIGHT]);
	for (var i = 0; i < list.length; i++) {
		var button = this._button[list[i]];
		this._tabGroup.addMember(button);		
	}
};

DwtDialog.PARAMS = ["parent", "className", "title", "standardButtons", "extraButtons", "zIndex", "mode", "loc", "id"];

DwtDialog.prototype = new DwtBaseDialog;
DwtDialog.prototype.constructor = DwtDialog;

DwtDialog.prototype.isDwtDialog = true;
DwtDialog.prototype.toString = function() { return "DwtDialog"; };

//
// Constants
//

/**
 * Defines the "left" align.
 */
DwtDialog.ALIGN_LEFT 		= 1;
/**
 * Defines the "right" align.
 */
DwtDialog.ALIGN_RIGHT 		= 2;
/**
 * Defines the "center" align.
 */
DwtDialog.ALIGN_CENTER 		= 3;

// standard buttons, their labels, and their positioning

/**
 * Defines the "Cancel" button.
 */
DwtDialog.CANCEL_BUTTON 	= 1;
/**
 * Defines the "OK" button.
 */
DwtDialog.OK_BUTTON 		= 2;
/**
 * Defines the "Dismiss" button.
 */
DwtDialog.DISMISS_BUTTON 	= 3;
/**
 * Defines the "No" button.
 */
DwtDialog.NO_BUTTON 		= 4;
/**
 * Defines the "Yes" button.
 */
DwtDialog.YES_BUTTON 		= 5;

DwtDialog.LAST_BUTTON 		= 5;

/**
 * Defines "no" buttons. This constant is used to show no buttons.
 */
DwtDialog.NO_BUTTONS 		= 256;
/**
 * Defines "all" buttons. This constant is used to show all buttons.
 */
DwtDialog.ALL_BUTTONS 		= [DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON, 
							   DwtDialog.DISMISS_BUTTON, DwtDialog.NO_BUTTON, 
							   DwtDialog.YES_BUTTON];

DwtDialog.MSG_KEY = {};
DwtDialog.MSG_KEY[DwtDialog.CANCEL_BUTTON] 	= "cancel";
DwtDialog.MSG_KEY[DwtDialog.OK_BUTTON] 		= "ok";
DwtDialog.MSG_KEY[DwtDialog.DISMISS_BUTTON] = "dismiss";
DwtDialog.MSG_KEY[DwtDialog.NO_BUTTON] 		= "no";
DwtDialog.MSG_KEY[DwtDialog.YES_BUTTON] 	= "yes";

DwtDialog.ALIGN = {};
DwtDialog.ALIGN[DwtDialog.CANCEL_BUTTON]	= DwtDialog.ALIGN_RIGHT;
DwtDialog.ALIGN[DwtDialog.OK_BUTTON] 		= DwtDialog.ALIGN_RIGHT;
DwtDialog.ALIGN[DwtDialog.DISMISS_BUTTON] 	= DwtDialog.ALIGN_RIGHT;
DwtDialog.ALIGN[DwtDialog.NO_BUTTON] 		= DwtDialog.ALIGN_RIGHT;
DwtDialog.ALIGN[DwtDialog.YES_BUTTON] 		= DwtDialog.ALIGN_RIGHT;

/**
 * Defines a "modeless" dialog.
 * 
 * @see	DwtBaseDialog.MODELESS
 */
DwtDialog.MODELESS = DwtBaseDialog.MODELESS;

/**
 * Defines a "modal" dialog.
 * 
 * @see	DwtBaseDialog.MODAL
 */
DwtDialog.MODAL = DwtBaseDialog.MODAL;

//
// Data
//
/**
 * @private
 */
DwtDialog.prototype.CONTROLS_TEMPLATE = "dwt.Widgets#DwtDialogControls";

//
// Public methods
//

DwtDialog.prototype.popdown =
function() {
	DwtBaseDialog.prototype.popdown.call(this);
	this.resetButtonStates();
};

/**
 * This method will pop-up the dialog.
 * 
 * @param	{DwtPoint}	loc		the location
 * @param	{constant}	focusButtonId		the button Id
 */
DwtDialog.prototype.popup =
function(loc, focusButtonId) {
	this._focusButtonId = focusButtonId;
	DwtBaseDialog.prototype.popup.call(this, loc);
};

/**
 * @private
 */
DwtDialog.prototype._resetTabFocus =
function(){
	if (this._focusButtonId) {
		var focusButton = this.getButton(this._focusButtonId);
		this._tabGroup.setFocusMember(focusButton, true);
	} else {
		DwtBaseDialog.prototype._resetTabFocus.call(this);
	}
};

DwtDialog.prototype.reset =
function() {
	this._resetCallbacks();
	this.resetButtonStates();
	DwtBaseDialog.prototype.reset.call(this);
};

/**
 * Sets all buttons back to inactive state.
 * 
 */
DwtDialog.prototype.resetButtonStates =
function() {
	for (b in this._button) {
		this._button[b].setEnabled(true);
		this._button[b].setHovered(false);
	}
	this.associateEnterWithButton(this._initialEnterButtonId);
};

/**
 * Gets a button by the specified Id.
 * 
 * @param	{constant}		buttonId		the button Id
 * @return	{DwtButton}		the button or <code>null</code> if not found
 */
DwtDialog.prototype.getButton =
function(buttonId) {
	return this._button[buttonId];
};

/**
 * Sets the button enabled state.
 * 
 * @param	{constant}		buttonId		the button Id
 * @param	{boolean}		enabled		if <code>true</code>, enable the button; <code>false</code> otherwise
 */
DwtDialog.prototype.setButtonEnabled = 
function(buttonId, enabled) {
	if (!this._button[buttonId]) {
		return;
	}
	this._button[buttonId].setEnabled(enabled);
};

/**
 * Sets the button visible state.
 * 
 * @param	{constant}		buttonId		the button Id
 * @param	{boolean}		enabled		if <code>true</code>, make the button visible; <code>false</code> otherwise
 */
DwtDialog.prototype.setButtonVisible = 
function(buttonId, visible) {
	if (!this._button[buttonId]) {
		return;
	}
	this._button[buttonId].setVisible(visible);
};

/**
 * Gets the button enabled state.
 * 
 * @param	{constant}		buttonId		the button Id
 * @return	{boolean}	<code>true</code> if the button is enabled; <code>false</code> otherwise
 */
DwtDialog.prototype.getButtonEnabled = 
function(buttonId) {
	return this._button[buttonId].getEnabled();
};

/**
 * Registers a callback for a given button. Can be passed an AjxCallback,
 * the params needed to create one, or as a bound function.
 *
 * @param {constant}		buttonId	one of the standard dialog buttons
 * @param {AjxCallback}	func		the callback method
 * @param {Object}		obj			the callback object
 * @param {array}		args		the callback args
 */
DwtDialog.prototype.registerCallback =
function(buttonId, func, obj, args) {
	this._buttonDesc[buttonId].callback = (func && (func.isAjxCallback || AjxUtil.isFunction(func) || (!obj && !args)))
		? func : (new AjxCallback(obj, func, args));
};

/**
 * Unregisters a callback for a given button.
 *
 * @param {constant}		buttonId	one of the standard dialog buttons
 */
DwtDialog.prototype.unregisterCallback =
function(buttonId) {
	this._buttonDesc[buttonId].callback = null;
};

/**
 * Sets the given listener as the only listener for the given button.
 *
 * @param {constant}		buttonId	one of the standard dialog buttons
 * @param {AjxListener}			listener	a listener
 */
DwtDialog.prototype.setButtonListener =
function(buttonId, listener) {
	this._button[buttonId].removeSelectionListeners();
	this._button[buttonId].addSelectionListener(listener);
};

/**
 * Sets the enter key listener.
 * 
 * @param	{AjxListener}	listener	a listener
 */
DwtDialog.prototype.setEnterListener =
function(listener) {
	this.removeAllListeners(DwtEvent.ENTER);
	this.addEnterListener(listener);
};

/**
 * Associates the "enter" key with a given button.
 * 
 * @param {constant}		buttonId	one of the standard dialog buttons
 */
DwtDialog.prototype.associateEnterWithButton =
function(id) {
	this._enterButtonId = id;
};

DwtDialog.prototype.getKeyMapName = 
function() {
	return DwtKeyMap.MAP_DIALOG;
};

DwtDialog.prototype.handleKeyAction =
function(actionCode, ev) {
	switch (actionCode) {
		
		case DwtKeyMap.ENTER:
			this.notifyListeners(DwtEvent.ENTER, ev);
			break;
			
		case DwtKeyMap.CANCEL:
			// hitting ESC should act as a cancel
            //TODO: dialog should set ESC/Enter listeners so we don't have to guess the action to take
			var handled = false;
			handled = handled || this._runCallbackForButtonId(DwtDialog.CANCEL_BUTTON);
			handled = handled || this._runCallbackForButtonId(DwtDialog.NO_BUTTON);
			handled = handled || this._runCallbackForButtonId(DwtDialog.DISMISS_BUTTON);

            //don't let OK act as cancel if there are other buttons
            if (!handled && this._buttonDesc[DwtDialog.OK_BUTTON] && this._buttonList.length == 1) {
                handled = handled || this._runCallbackForButtonId(DwtDialog.OK_BUTTON);
            }
            this.popdown();
			return true;

		case DwtKeyMap.YES:
			if (this._buttonDesc[DwtDialog.YES_BUTTON]) {
				this._runCallbackForButtonId(DwtDialog.YES_BUTTON);
			}
			break;

		case DwtKeyMap.NO:
			if (this._buttonDesc[DwtDialog.NO_BUTTON]) {
				this._runCallbackForButtonId(DwtDialog.NO_BUTTON);
			}
			break;

		default:
			return false;
	}
	return true;
};

//
// Protected methods
//

/**
 * @private
 */
DwtDialog.prototype._createHtmlFromTemplate =
function(templateId, data) {
	DwtBaseDialog.prototype._createHtmlFromTemplate.call(this, templateId, data);

	var focusId = data.id+"_focus";
	if (document.getElementById(focusId)) {
		this._focusElementId = focusId;
	}
	this._buttonsEl = document.getElementById(data.id+"_buttons");
	if (this._buttonsEl) {
		var html = [];
		var idx = 0;
		this._addButtonsHtml(html,idx);
		this._buttonsEl.innerHTML = html.join("");
	}
};

// TODO: Get rid of these button template methods!
/**
 * @private
 */
DwtDialog.prototype._getButtonsContainerStartTemplate =
function () {
	return "<table width='100%'><tr>";
};

/**
 * @private
 */
DwtDialog.prototype._getButtonsAlignStartTemplate =
function () {
	return "<td align=\"{0}\"><table><tr>";
};

/**
 * @private
 */
DwtDialog.prototype._getButtonsAlignEndTemplate =
function () {
	return "</tr></table></td>";
};

/**
 * @private
 */
DwtDialog.prototype._getButtonsCellTemplate =
function () {
	return "<td id=\"{0}\"></td>";
};

/**
 * @private
 */
DwtDialog.prototype._getButtonsContainerEndTemplate =
function () {
	return  "</tr></table>";
};

/**
 * @private
 */
DwtDialog.prototype._addButtonsHtml =
function(html, idx) {
	if (this._buttonList && this._buttonList.length) {
		var leftButtons = new Array();
		var rightButtons = new Array();
		var centerButtons = new Array();
		for (var i = 0; i < this._buttonList.length; i++) {
			var buttonId = this._buttonList[i];
			switch (this._buttonDesc[buttonId].align) {
				case DwtDialog.ALIGN_RIGHT: 	rightButtons.push(buttonId); break;
				case DwtDialog.ALIGN_LEFT: 		leftButtons.push(buttonId); break;
				case DwtDialog.ALIGN_CENTER:	centerButtons.push(buttonId); break;
			}
		}
		html[idx++] = this._getButtonsContainerStartTemplate();
		
		if (leftButtons.length) {
			html[idx++] = AjxMessageFormat.format(
								  this._getButtonsAlignStartTemplate(),
								  ["left"]);
			for (var i = 0; i < leftButtons.length; i++) {
				var buttonId = leftButtons[i];
				var cellTemplate = this._buttonDesc[buttonId].cellTemplate ? 
					this._buttonDesc[buttonId].cellTemplate : this._getButtonsCellTemplate();
		 		html[idx++] = AjxMessageFormat.format(
								  cellTemplate,
								  [this._buttonElementId[buttonId]]);
		 	}
			html[idx++] = this._getButtonsAlignEndTemplate();
		}
		if (centerButtons.length){
			html[idx++] = AjxMessageFormat.format(
								this._getButtonsAlignStartTemplate(),
								["center"]);
			for (var i = 0; i < centerButtons.length; i++) {
				var buttonId = centerButtons[i];
				var cellTemplate = this._buttonDesc[buttonId].cellTemplate ? 
					this._buttonDesc[buttonId].cellTemplate : this._getButtonsCellTemplate();				
		 		html[idx++] = AjxMessageFormat.format(
								cellTemplate,
								[this._buttonElementId[buttonId]]);
		 	}
			html[idx++] = this._getButtonsAlignEndTemplate();
		}
		if (rightButtons.length) {
			html[idx++] = AjxMessageFormat.format(
								this._getButtonsAlignStartTemplate(),
								["right"]);
			for (var i = 0; i < rightButtons.length; i++) {
				var buttonId = rightButtons[i];
				var cellTemplate = this._buttonDesc[buttonId].cellTemplate ? 
					this._buttonDesc[buttonId].cellTemplate : this._getButtonsCellTemplate();				

		 		html[idx++] = AjxMessageFormat.format(cellTemplate,
													[this._buttonElementId[buttonId]]);
		 	}
			html[idx++] = this._getButtonsAlignEndTemplate();
		}
		html[idx++] = this._getButtonsContainerEndTemplate();
	}	
	return idx;
};

/**
 * Button listener that checks for callbacks.
 * 
 * @private
 */
DwtDialog.prototype._buttonListener =
function(ev, args) {
	var obj = DwtControl.getTargetControl(ev);
	var buttonId = (obj && obj.buttonId) || this._enterButtonId;
	if (buttonId) {
		this._runCallbackForButtonId(buttonId, args);
	}
};

/**
 * @private
 */
DwtDialog.prototype._runCallbackForButtonId =
function(id, args) {
	var buttonDesc = this._buttonDesc[id];
	var callback = buttonDesc && buttonDesc.callback;
	if (!callback) {
		return false;
	}
	args = (args instanceof Array) ? args : [args];
	callback.run.apply(callback, args);
	return true;
};

/**
 * @private
 */
DwtDialog.prototype._runEnterCallback =
function(args) {
	if (this._enterButtonId && this.getButtonEnabled(this._enterButtonId)) {
		this._runCallbackForButtonId(this._enterButtonId, args);
	}
};

/**
 * Default callbacks for the standard buttons.
 * 
 * @private
 */
DwtDialog.prototype._resetCallbacks =
function() {
	if (this._buttonDesc) {
		for (var i = 0; i < DwtDialog.ALL_BUTTONS.length; i++) {
			var id = DwtDialog.ALL_BUTTONS[i];
			if (this._buttonDesc[id])
				this._buttonDesc[id].callback = new AjxCallback(this, this.popdown);
		}
	}
};

//
// Classes
//

/**
 * @class
 * This class represents a button descriptor.
 * 
 * @param	{string}	id		the button Id
 * @param	{string}	label		the button label
 * @param	{constant}	align		the alignment
 * @param	{AjxCallback}	callback		the callback
 * @param	{string}	cellTemplate		the template
 */
DwtDialog_ButtonDescriptor = function(id, label, align, callback, cellTemplate) {
	this.id = id;
	this.label = label;
	this.align = align;
	this.callback = callback;
	this.cellTemplate = cellTemplate;
};
