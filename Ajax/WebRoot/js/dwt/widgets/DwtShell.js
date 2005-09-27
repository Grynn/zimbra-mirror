/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
 * License Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra AJAX Toolkit.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

// Cannot be subclassed

/**
* Creates a shell. 
* @constructor
* @class
* This class represents a shell, the first widget that must be instantiated in a Dwt based 
* application. By default the shell covers the whole browser window, though it may also be 
* instantiated within an HTML element.<br>
*
* DwtShell should <b>NOT</b> be subclassed
*
* @author Ross Dargahi
* @param className [String] The CSS class name
* @param docBodyScrollable [Boolean] If true then the document body is set to be scrollable
* @param confirmExitMethod [Function] method which is called when the user attempts to navigate away from 
*              the application or close the browser window. If this method return a string that 
*              is displayed as part of the alert that is presented to the user. If this method 
*              returns null, then no alert is popped up this parameter may be null
* @param  userShell [Element] If not null then is an HTML element that will be reparented into an absolutely
*              postioned container in this shell. This is useful in the situation where you have an html 
*              template and want to use this in context of Dwt.
* @param useCurtain [Boolean] If true, a curtain overlay is created to be used between hidden and viewable elements 
*              using z-index. See Dwt.js for various layering constants
*/
function DwtShell(className, docBodyScrollable, confirmExitMethod, userShell, useCurtain) {

	if (window._dwtShell != null) 
		throw new DwtException("DwtShell already exists for window", DwtException.INVALID_OP, "DwtShell");
	className = className || "DwtShell";
	DwtComposite.call(this, null, className);

     
    // HACK! This is a hack to make sure that the control methods work 
    // with DwtShell since the parent of DwtShell is null. 
	this._ctrlInited = true;

	window._dwtShell = AjxCore.assignId(this);
	
	if ((confirmExitMethod != null) && (document.domain != "localhost"))
		window.onbeforeunload = confirmExitMethod;
		
	document.body.style.marginLeft = 0;
	document.body.style.marginRight = 0;
	document.body.style.marginTop = 0;
	document.body.style.marginBottom = 0;
	if (docBodyScrollable != null && !docBodyScrollable)
		document.body.style.overflow = "hidden";

    document.body.onselect = DwtShell._preventDefaultSelectPrt;
	document.body.onselectstart = DwtShell._preventDefaultSelectPrt;
    document.body.oncontextmenu = DwtShell._preventDefaultPrt;
    window.onresize = DwtShell._resizeHdlr;

    var htmlElement = document.createElement("div");
	this._htmlElId = htmlElement.id = Dwt.getNextId();

	htmlElement.className = className;
	htmlElement.style.width = "100%";
	htmlElement.style.height = "100%";
	//htmlElement.style.overflow = "hidden";
	if (htmlElement.style.overflow) htmlElement.style.overflow = null;

	// if there is a user shell (body content), move it below this shell
	// into a container that's absolutely positioned
	if (userShell)
		document.body.removeChild(userShell);
	document.body.appendChild(htmlElement);
	if (userShell) {
		var userShellContainer = new DwtControl(this, null, Dwt.ABSOLUTE_STYLE);
		userShellContainer.getHtmlElement().appendChild(userShell);
		userShellContainer.setSize("100%", "100%");
		userShellContainer.zShow(true);
	}
	Dwt.associateElementWithObject(htmlElement, this);
    this.shell = this;

    // Busy overlay - used when we want to enforce a modal busy state
    this._createBusyOverlay(htmlElement);
    
	// Veil overlay - used by DwtDialog to disable underlying app
	this._veilOverlay = document.createElement("div");
	this._veilOverlay.className = (!AjxEnv.isLinux) ? "VeilOverlay" : "VeilOverlay-linux";
	this._veilOverlay.style.position = "absolute";
	this._veilOverlay.style.cursor = AjxEnv.isIE6up ? "not-allowed" : "wait";
	Dwt.setBounds(this._veilOverlay, 0, 0, "100%", "100%");
    Dwt.setZIndex(this._veilOverlay, Dwt.Z_HIDDEN);
	this._veilOverlay.veilZ = new Array();
	this._veilOverlay.veilZ.push(Dwt.Z_HIDDEN);
	this._veilOverlay.dialogZ = new Array();
	this._veilOverlay.activeDialogs = new Array();
	this._veilOverlay.innerHTML = "<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'><tr><td>&nbsp;</td></tr></table>";
	htmlElement.appendChild(this._veilOverlay);
    
	// Curtain overlay - used between hidden and viewable elements using z-index
	if (useCurtain) {
		this._curtainOverlay = document.createElement("div");
		this._curtainOverlay.className = "CurtainOverlay";
		this._curtainOverlay.style.position = "absolute";
		Dwt.setBounds(this._curtainOverlay, 0, 0, "100%", "100%")
		Dwt.setZIndex(this._curtainOverlay, Dwt.Z_CURTAIN);
		this._curtainOverlay.innerHTML = "<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'><tr><td>&nbsp;</td></tr></table>";
		htmlElement.appendChild(this._curtainOverlay);
	}
    
    this._uiEvent = new DwtUiEvent(true);
	this._currWinSize = this.getWindowSize();

	// tooltip singleton used by all control in shell
	this._toolTip = new DwtToolTip(this);
	this._hoverMgr = new DwtHoverMgr();
}

DwtShell.prototype = new DwtComposite;
DwtShell.prototype.constructor = DwtShell;

// Event objects used to populate events so we dont need to create
// them for each event
DwtShell.controlEvent = new DwtControlEvent();
DwtShell.keyEvent = new DwtKeyEvent();
DwtShell.mouseEvent = new DwtMouseEvent();
DwtShell.selectionEvent = new DwtSelectionEvent(true);
DwtShell.treeEvent = new DwtTreeEvent();

DwtShell._BUSY_OVERLAY_CLASS = "BusyOverlay";

DwtShell.prototype.toString = 
function() {
	return "DwtShell";
}

/**
* Returns the shell managing the browser window (if any)
*
* @return DwtShell or null
*/
DwtShell.getShell =
function(win){
	return AjxCore.objectWithId(win._dwtShell);
};

/**
* <b>DEPRECATED</b>
*/
DwtShell.prototype.getWindowSize =
function() {
	return Dwt.getWindowSize();
}

/* Set's the busy overlay. The busy overlay disables input to the application and makes the 
 * cursor a wait cursor. Optionally a work in progress (WIP) dialog may be requested. 
 * 
 * @param busy [Boolean] true		Set the busy overlay, false bide the busy overlay
 * @param className [String]		The css classname for the busy dialog (if desiring to override the default)
 * @param showbusyDialog [Boolean] true - show the WIP dialgo
 * @param busyDialogDelay [Integer]	Number of ms to delay popping up the WIP dialog. This is useful for
 *            situation where the WIP should be popped up only if an operation is taking longer than
 *            expected. Application only if showbusyDialog is true
 * @param cancelBusyCallback [ZmCallback]		If provided, then the WIP will display a cancel button which when depressed will
 *            invoke the supplied callaback.
 */ 
DwtShell.prototype.setBusy =
function(busy, className, showbusyDialog, busyDialogDelay, cancelBusyCallback) {
    if (!busy) {
    	if (this._busyActionId != -1) {
    		AjxTimedAction.cancelAction(this._busyActionId);
    		this._busyActionId = -1;
    	}
   		if (this._busyDialog.isPoppedUp)
    		this._busyDialog.popdown();
	    Dwt.setCursor(this._busyOverlay, "default");
	    Dwt.setVisible(this._busyOverlay, false);
    } else {
		this._busyOverlay.className = className ? className : this._busyOverlayDefCName;
		Dwt.setCursor(this._busyOverlay, "wait");
    	Dwt.setVisible(this._busyOverlay, true);
		if (showbusyDialog) {
			if (busyDialogDelay && busyDialogDelay > 0)
				AjxTimedAction.scheduleAction(this._busyTimedAction, busyDialogDelay);
			else
				this._showBusyDialogAction();
				
			if (cancelBusyCallback) {
				this._cancelBusyCallback = cancelBusyCallback;
				this._busyDialog.setButtonEnabled(DwtDialog.CANCEL_BUTTON, true);
			} else {
				this._busyDialog.setButtonEnabled(DwtDialog.CANCEL_BUTTON, false);
			}
		}
    } 
}

/**
* Sets the text for the shell's busy dialog
*
* @param text The text to set (may be HTML)
*/
DwtShell.prototype.setBusyDialogText =
function(text) { 
	this._busyDialogTxt.innerHTML = (text) ? text : "";
}

/**
* Sets shell's busy dialog title. If null set's it to the default
*
* @param title The title text
*/

DwtShell.prototype.setBusyDialogTitle =
function(title) { 
	this._busyDialog.setTitle((title) ? title : AjxMsg.workInProgress);
}



DwtShell.prototype.getHoverMgr = function() {
	return this._hoverMgr;
}

DwtShell.prototype.getToolTip = function() {
	return this._toolTip;
}

DwtShell._preventDefaultSelectPrt =
function(ev) {
    var evt = AjxCore.objectWithId(window._dwtShell)._uiEvent;
    evt.setFromDhtmlEvent(ev);

	if (evt.dwtObj && evt.dwtObj instanceof DwtControl && !evt.dwtObj.preventSelection(evt.target)) {
        evt._stopPropagation = false;
        evt._returnValue = true;
    } else {
        evt._stopPropagation = true;
        evt._returnValue = false;
    }
    evt.setToDhtmlEvent(ev);
    return !evt._stopPropagation;
}

DwtShell._preventDefaultPrt =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var target = ev.target ? ev.target : ev.srcElement;
	
    var evt = AjxCore.objectWithId(window._dwtShell)._uiEvent;
    evt.setFromDhtmlEvent(ev);
	//default behavior
    evt._stopPropagation = true;
    evt._returnValue = false;
	if (evt.dwtObj && evt.dwtObj instanceof DwtControl && !evt.dwtObj.preventContextMenu(evt.target)) {
        evt._stopPropagation = false;
        evt._returnValue = true;
    } else if (target != null && typeof(target) == 'object') {
     	if ((target.tagName == "A" ||  target.tagName == "a") && target.href) {
	        evt._stopPropagation = false;
    	    evt._returnValue = true;
    	}
    } 
    
    evt.setToDhtmlEvent(ev);
    return evt._returnValue;
}

/* This the resize handler to track when the browser window size changes */
DwtShell._resizeHdlr =
function(ev) {
	var shell = AjxCore.objectWithId(window._dwtShell);
	if (shell.isListenerRegistered(DwtEvent.CONTROL)) {
	 	var evt = DwtShell.controlEvent;
	 	evt.reset();
	 	evt.oldWidth = shell._currWinSize.x;
	 	evt.oldHeight = shell._currWinSize.y;
	 	shell._currWinSize = Dwt.getWindowSize();
	 	evt.newWidth = shell._currWinSize.x;
	 	evt.newHeight = shell._currWinSize.y;
	 	shell.notifyListeners(DwtEvent.CONTROL, evt);
	} else {
		shell._currWinSize = Dwt.getWindowSize();
	}
}

DwtShell.prototype._busyCancelButtonListener =
function(ev) {
	this._cancelBusyCallback.run();
	this._busyDialog.popdown();
}

DwtShell.prototype._showBusyDialogAction =
function(showDialog) {
	this._busyDialog.popup();
	this.__busyActionId = -1;
}


DwtShell.prototype._createBusyOverlay =
function(htmlElement) { 
    this._busyOverlay = document.createElement("div");
    this._busyOverlayDefCName = (!AjxEnv.isLinux) ? DwtShell._BUSY_OVERLAY_CLASS : DwtShell._BUSY_OVERLAY_CLASS + "-linux";
    this._busyOverlay.style.position = "absolute";
    Dwt.setBounds(this._busyOverlay, 0, 0, "100%", "100%")
    Dwt.setZIndex(this._busyOverlay, Dwt.Z_VEIL);
    this._busyOverlay.innerHTML = "<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'><tr><td>&nbsp;</td></tr></table>";
    htmlElement.appendChild(this._busyOverlay);

    this._busyDialog = new DwtDialog(this, "DwtShellbusyDialog", AjxMsg.workInProgress, [DwtDialog.CANCEL_BUTTON], null, Dwt.BUSY + 10); 
    this._busyDialog._disableFFhack();
    this._busyDialog.registerCallback(DwtDialog.CANCEL_BUTTON, this._busyCancelButtonListener, this);
    var txtId = Dwt.getNextId();
    var html = [
        "<table xborder=1 class='DialogContent'><tr>",
            "<td class='WaitIcon'></td><td class='MsgText' id='", txtId, "'>&nbsp;</td>",
        "</tr></table>"].join("");
    
    this._busyDialog.setContent(html);
    this._busyDialogTxt = Dwt.getDomObj(document, txtId);
       
	this._busyTimedAction = new AjxTimedAction();
	this._busyTimedAction.obj = this;
	this._busyTimedAction.method = this._showBusyDialogAction;
	this._busyActionId = -1;
}

