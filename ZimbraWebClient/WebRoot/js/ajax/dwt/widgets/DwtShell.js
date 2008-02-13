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
 * This class represents a shell, the first widget that must be instantiated in a Dwt based 
 * application. By default the shell covers the whole browser window, though it may also be 
 * instantiated within an HTML element.
 * <p>
 * DwtShell should <b>NOT</b> be subclassed</p>
 *
 * @author Ross Dargahi
 * 
 * @param className			[string]*		CSS class name
 * @param docBodyScrollable	[boolean]*		if true, then the document body is set to be scrollable
 * @param userShell			[Element]*		an HTML element that will be reparented into an absolutely
 *											postioned container in this shell. This is useful in the situation where you have an HTML 
 *											template and want to use this in context of Dwt.
 * @param useCurtain			[boolean]*		if true, a curtain overlay is created to be used between hidden and viewable elements 
 *											using z-index. See Dwt.js for various layering constants
 */
DwtShell = function(params) {
	if (window._dwtShell != null) {
		throw new DwtException("DwtShell already exists for window", DwtException.INVALID_OP, "DwtShell");
	}

	var className = params.className || "DwtShell";
	DwtComposite.call(this, {className:className});

    // HACK! This is a hack to make sure that the control methods work 
    // with DwtShell since the parent of DwtShell is null. 
	this.__ctrlInited = true;

	window._dwtShell = AjxCore.assignId(this);

	document.body.style.margin = 0;
	if (!params.docBodyScrollable) {
		if (AjxEnv.isIE) {
			document.body.onscroll = DwtShell.__onBodyScroll;
		}
		document.body.style.overflow = "hidden";
	}

    document.body.onselect = DwtShell._preventDefaultSelectPrt;
	document.body.onselectstart = DwtShell._preventDefaultSelectPrt;
    document.body.oncontextmenu = DwtShell._preventDefaultPrt;
    window.onresize = DwtShell._resizeHdlr;

    var htmlElement = document.createElement("div");
	this._htmlElId = htmlElement.id = Dwt.getNextId();

	htmlElement.className = className;
	htmlElement.style.width = htmlElement.style.height = "100%";
	if (htmlElement.style.overflow) {
		htmlElement.style.overflow = null;
	}

	// if there is a user shell (body content), move it below this shell
	// into a container that's absolutely positioned
	try {
		if (params.userShell) {
			document.body.removeChild(params.userShell);
		}
	} catch (ex) {}
	document.body.appendChild(htmlElement);
	if (params.userShell) {
		var userShellContainer = new DwtControl({parent:this, posStyle:Dwt.ABSOLUTE_STYLE});
		userShellContainer.getHtmlElement().appendChild(params.userShell);
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
	if (params.useCurtain) {
		this._curtainOverlay = document.createElement("div");
		this._curtainOverlay.className = "CurtainOverlay";
		this._curtainOverlay.style.position = "absolute";
		Dwt.setBounds(this._curtainOverlay, 0, 0, "100%", "100%")
		Dwt.setZIndex(this._curtainOverlay, Dwt.Z_CURTAIN);
		this._curtainOverlay.innerHTML = "<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'><tr><td>&nbsp;</td></tr></table>";
		htmlElement.appendChild(this._curtainOverlay);
	}

    this._uiEvent = new DwtUiEvent(true);
	this._currWinSize = Dwt.getWindowSize();

	// tooltip singleton used by all controls in shell
	this._toolTip = new DwtToolTip(this);
	this._hoverMgr = new DwtHoverMgr();
	
	this._keyboardMgr = new DwtKeyboardMgr(this);
}

DwtShell.prototype = new DwtComposite;
DwtShell.prototype.constructor = DwtShell;

/** DwtDialog not defined yet, can't base ID on it
 * @private
 */
DwtShell.CANCEL_BUTTON = -1;

// Event objects used to populate events so we dont need to create them for each event
DwtShell.controlEvent 	= new DwtControlEvent();
DwtShell.focusEvent 	= new DwtFocusEvent();
DwtShell.keyEvent 		= new DwtKeyEvent();
DwtShell.mouseEvent 	= new DwtMouseEvent();
DwtShell.selectionEvent = new DwtSelectionEvent(true);
DwtShell.treeEvent 		= new DwtTreeEvent();

// Public methods

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
 * Return's the shell's keyboard manager (see DwtKeybaordMgr)
 */
DwtShell.prototype.getKeyboardMgr =
function() {
	return this._keyboardMgr;
}

/**
 * Sets the busy overlay. The busy overlay disables input to the application and makes the 
 * cursor a wait cursor. Optionally a work in progress (WIP) dialog may be requested. Since
 * multiple calls to this method may be interleaved, it accepts a unique ID to keep them
 * separate. We also maintain a count of outstanding calls to setBusy(true). When that count
 * changes between 0 and 1, the busy overlay is applied or removed.
 * 
 * @param busy					[boolean]		if true, set the busy overlay, otherwise hide the busy overlay
 * @param id					[int]*			a unique ID for this instance
 * @param showBusyDialog 		[boolean]*		if true, show the WIP dialog
 * @param busyDialogDelay 		[int]*			number of ms to delay before popping up the WIP dialog
 * @param cancelBusyCallback	[AjxCallback]*	callback to run when OK button is pressed in WIP dialog
 */ 
DwtShell.prototype.setBusy =
function(busy, id, showBusyDialog, busyDialogDelay, cancelBusyCallback) {
	if (busy) {
		this._setBusyCount++;
	} else if (this._setBusyCount > 0) {
		this._setBusyCount--;
	}

    if (!this._setBusy && (this._setBusyCount > 0)) {
		// transition from non-busy to busy state
		Dwt.setCursor(this._busyOverlay, "wait");
    	Dwt.setVisible(this._busyOverlay, true);
    	this._setBusy = this._blockInput = true;
    	DBG.println(AjxDebug.DBG2, "set busy overlay, id = " + id);
    } else if (this._setBusy && (this._setBusyCount <= 0)) {
		// transition from busy to non-busy state
	    Dwt.setCursor(this._busyOverlay, "default");
	    Dwt.setVisible(this._busyOverlay, false);
	    this._setBusy = this._blockInput = false;
    	DBG.println(AjxDebug.DBG2, "remove busy overlay, id = " + id);
	}
	
	// handle busy dialog whether we've changed state or not
	if (busy && showBusyDialog) {
		if (busyDialogDelay && busyDialogDelay > 0) {
			this._busyActionId[id] = AjxTimedAction.scheduleAction(this._busyTimedAction, busyDialogDelay);
		} else {
			this._showBusyDialogAction(id);
		}

		this._cancelBusyCallback = cancelBusyCallback;
		if (this._busyDialog) {
			this._busyDialog.setButtonEnabled(DwtShell.CANCEL_BUTTON, (cancelBusyCallback != null));
		}
	} else {
    	if (this._busyActionId[id] && (this._busyActionId[id] != -1)) {
    		AjxTimedAction.cancelAction(this._busyActionId[id]);
    		this._busyActionId[id] = -1;
    	}
   		if (this._busyDialog && this._busyDialog.isPoppedUp) {
    		this._busyDialog.popdown();
   		}
    } 
}

// (hee hee)
DwtShell.prototype.getBusy =
function() {
	return this._setBusy;
};

/**
* Sets the text for the shell's busy dialog
*
* @param text The text to set (may be HTML)
*/
DwtShell.prototype.setBusyDialogText =
function(text) {
	this._busyDialogText = text;
	if (this._busyDialogTxt) {
		this._busyDialogTxt.innerHTML = (text) ? text : "";
	}
}

/**
* Sets shell's busy dialog title.
*
* @param title The title text
*/
DwtShell.prototype.setBusyDialogTitle =
function(title) {
	this._busyDialogTitle = title;
	if (this._busyDialog) {
		this._busyDialog.setTitle((title) ? title : AjxMsg.workInProgress);
	}
}

DwtShell.prototype.getHoverMgr = 
function() {
	return this._hoverMgr;
}

DwtShell.prototype.getToolTip = 
function() {
	return this._toolTip;
}

DwtShell.prototype.getH = 
function(incScroll) {
	return (!this._virtual) ? Dwt.getSize(this.getHtmlElement(), incScroll).y
	                        : Dwt.getSize(document.body, incScroll).y;
}

DwtShell.prototype.getW = 
function(incScroll) {
	return (!this._virtual) ? Dwt.getSize(this.getHtmlElement(), incScroll).x
	                        : Dwt.getSize(document.body, incScroll).x;
}

DwtShell.prototype.getSize = 
function(incScroll) {
	return (!this._virtual) ? Dwt.getSize(this.getHtmlElement(), incScroll)
	                        : Dwt.getSize(document.body, incScroll);
}

DwtShell.prototype.getLocation =
function() {
	return (!this._virtual) ? Dwt.getLocation(this.getHtmlElement())
	                        : Dwt.getLocation(document.body);
}

DwtShell.prototype.getX =
function() {
	return (!this._virtual) ? Dwt.getLocation(this.getHtmlElement()).x
	                        : Dwt.getLocation(document.body).x;
}

DwtShell.prototype.getY =
function() {
	return (!this._virtual) ? Dwt.getLocation(this.getHtmlElement()).y
	                        : Dwt.getLocation(document.body).y;
}


DwtShell.prototype.getBounds = 
function(incScroll) {
	return (!this._virtual) ? Dwt.getBounds(this.getHtmlElement(), incScroll)
	                        : Dwt.getBounds(document.body, incScroll);
}

/**
 * If the shell is set as a virtual shell, then all children that are 
 * directly added to the shell become children on the page's body element. This
 * is useful in the cases where Dwt is to beused  with existing HTML documents
 * rather than as the foundation for an application
 */
DwtShell.prototype.setVirtual =
function() {
	this._virtual = true;
	this.setVisible(false);
}

/**
 * @return true if the shell is virtual
 * @type Boolean
 */
DwtShell.prototype.isVirtual =
function() {
	return this._virtual;
}


// Private / protected methods

DwtShell.prototype._showBusyDialogAction =
function(id) {
	var bd = this._getBusyDialog();
	bd.popup();
	this._busyActionId[id] = -1;
}

DwtShell.prototype._createBusyOverlay =
function(htmlElement) {
    this._busyOverlay = document.createElement("div");
    this._busyOverlay.className = (!AjxEnv.isLinux) ? "BusyOverlay" : "BusyOverlay-linux";
    this._busyOverlay.style.position = "absolute";
    Dwt.setBounds(this._busyOverlay, 0, 0, "100%", "100%")
    Dwt.setZIndex(this._busyOverlay, Dwt.Z_VEIL);
    this._busyOverlay.innerHTML = "<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'><tr><td>&nbsp;</td></tr></table>";
    htmlElement.appendChild(this._busyOverlay);
	Dwt.setVisible(this._busyOverlay, false);

	this._busyTimedAction = new AjxTimedAction(this, this._showBusyDialogAction);
	this._busyActionId = {};
	
	this._setBusyCount = 0;
	this._setBusy = false;
}

DwtShell.prototype._getBusyDialog =
function(htmlElement) {
	if (!this._busyDialog) {
		var cancelButton = new DwtDialog_ButtonDescriptor(DwtShell.CANCEL_BUTTON, AjxMsg.cancelRequest, DwtDialog.ALIGN_CENTER);
	    this._busyDialog = new DwtDialog({parent:this, className:"DwtShellBusyDialog", title:AjxMsg.workInProgress,
	    								  standardButtons:DwtDialog.NO_BUTTONS, extraButtons:[cancelButton], zIndex:Dwt.BUSY + 10});
	    this._busyDialog.registerCallback(DwtShell.CANCEL_BUTTON, this._busyCancelButtonListener, this);
	    var txtId = Dwt.getNextId();
	    var html = [
	        "<table class='DialogContent'><tr>",
	            "<td><div class='WaitIcon'></div></td><td class='MsgText' id='", txtId, "'>&nbsp;</td>",
	        "</tr></table>"].join("");
	    
	    this._busyDialog.setContent(html);
	    this._busyDialogTxt = document.getElementById(txtId);
		if (this._busyDialogText) {
			this._busyDialogTxt.innerHTML = this._busyDialogText;
		}
		if (this._busyDialogTitle) {
			this._busyDialog.setTitle(this._busyDialogTitle);
		}
		this._busyDialog.setButtonEnabled(DwtShell.CANCEL_BUTTON, (this._cancelBusyCallback != null));
	}
	return this._busyDialog;
};


// Listeners

DwtShell.prototype._busyCancelButtonListener =
function(ev) {
	this._cancelBusyCallback.run();
	if (this._busyDialog) {
		this._busyDialog.popdown();
	}
}


// Static methods

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
};

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
};


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
};

DwtShell.__onBodyScroll = function() {
	// alert(document.body.scrollTop + "/" + document.body.scrollLeft);
	document.body.scrollTop = 0;
	document.body.scrollLeft = 0;
	// DwtShell._resizeHdlr();
};
