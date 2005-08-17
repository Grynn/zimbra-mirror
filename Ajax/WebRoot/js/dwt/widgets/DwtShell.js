// Cannot be subclassed

/**
 * @param confirmExitMethod method which is called when the user attempts to navigate away from the application
 *              or close the browser window. If this method return a string that is displayed as part of the 
 *              alert that is presented to the user. If this method returns null, then no alert is popped up
 *              this parameter may be null
 */
function DwtShell(className, docBodyScrollable, confirmExitMethod, userShell) {

	if (window._dwtShell != null) 
		throw new DwtException("DwtShell already exists for window", DwtException.INVALID_OP, "DwtShell");
	className = className || "DwtShell";
	DwtComposite.call(this, null, className);

    // XXX: HACK! This is a hack to make sure that the control methods work 
    // with DwtShell since the parent of DwtShell is null. 
	this._ctrlInited = true;

	window._dwtShell = LsCore.assignId(this);
	
	if (confirmExitMethod != null) {
		// TEMPORARILY COMMENT OUT UNTIL WE FIGURE THIS OUT MORE
		// window.onbeforeunload = confirmExitMethod;
	}
		
	window.document.body.style.marginLeft = 0;
	window.document.body.style.marginRight = 0;
	window.document.body.style.marginTop = 0;
	window.document.body.style.marginBottom = 0;
	if (docBodyScrollable != null && !docBodyScrollable)
		window.document.body.style.overflow = "hidden";

    var htmlElement = window.document.createElement("div");
    this._htmlElId = htmlElement.id = Dwt.getNextId();
    htmlElement.className = className;
    htmlElement.style.width = "100%";
    htmlElement.style.height = "100%";

	// if there is a user shell (body content), move it below this shell
	// into a container that's absolutely positioned
	if (userShell)
		window.document.body.removeChild(userShell);
    window.document.body.appendChild(htmlElement);
    if (userShell) {
    	var userShellContainer = new DwtControl(this, null, Dwt.ABSOLUTE_STYLE);
    	userShellContainer.getHtmlElement().appendChild(userShell);
    	userShellContainer.setSize(Dwt.DEFAULT, "100%");
    	userShellContainer.zShow(true);
    }
	Dwt.associateElementWithObject(htmlElement, this);
    this.shell = this;

    // Busy overlay - used when we want to enforce a modal busy state
    this._busyOverlay = window.document.createElement("div");
    this._busyOverlayDefCName = (!LsEnv.isLinux) ? DwtShell.BUSY_OVERLAY_CLASS : DwtShell.BUSY_OVERLAY_CLASS + "-linux";
    this._busyOverlay.className = this._busyOverlayDefCName;
    this._busyOverlay.style.position = "absolute";
    Dwt.setBounds(this._busyOverlay, 0, 0, "100%", "100%")
    Dwt.setZIndex(this._busyOverlay, Dwt.Z_HIDDEN);
    this._busyOverlay.innerHTML = "<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'><tr><td>&nbsp;</td></tr></table>";
    htmlElement.appendChild(this._busyOverlay);
    
	// Veil overlay - used by DwtDialog to disable underlying app
	this._veilOverlay = window.document.createElement("div");
	this._veilOverlay.className = (!LsEnv.isLinux) ? "VeilOverlay" : "VeilOverlay-linux";
	this._veilOverlay.style.position = "absolute";
	this._veilOverlay.style.cursor = LsEnv.isIE6up ? "not-allowed" : "wait";
	Dwt.setBounds(this._veilOverlay, 0, 0, "100%", "100%");
    Dwt.setZIndex(this._veilOverlay, Dwt.Z_HIDDEN);
	this._veilOverlay.veilZ = new Array();
	this._veilOverlay.veilZ.push(Dwt.Z_HIDDEN);
	this._veilOverlay.dialogZ = new Array();
	this._veilOverlay.activeDialogs = new Array();
	this._veilOverlay.innerHTML = "<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'><tr><td>&nbsp;</td></tr></table>";
	htmlElement.appendChild(this._veilOverlay);
    
    window.document.body.onselect = DwtShell._preventDefaultSelectPrt;
	window.document.body.onselectstart = DwtShell._preventDefaultSelectPrt;
    window.document.body.oncontextmenu = DwtShell._preventDefaultPrt;
    window.onresize = DwtShell._resizeHdlr;

    this._uiEvent = new DwtUiEvent(true);
	this._currWinSize = this.getWindowSize();

	// tooltip singleton used by all control in shell
	this._toolTip = new DwtToolTip(this);
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

DwtShell.BUSY_OVERLAY_CLASS = "BusyOverlay";

DwtShell.prototype.toString = 
function() {
	return "DwtShell";
}

DwtShell.newWindow = 
function() {
	// TODO - Create new Top Level Window
}

DwtShell.prototype.getWindowSize =
function() {
	var p = new DwtPoint(0, 0);
	if (window.innerWidth) {
		p.x = window.innerWidth;
		p.y = window.innerHeight;
	} else if (LsEnv.isIE6CSS) {
		p.x = window.document.body.parentElement.clientWidth;
		p.y = window.document.body.parentElement.clientHeight;
	} else if (window.document.body && window.document.body.clientWidth) {
		p.x = window.document.body.clientWidth;
		p.y = window.document.body.clientHeight;
	}
	return p;
}

DwtShell.prototype.setBusy =
function(busy, className) {
	var cursor = busy ? "wait" : "default";
	var zIndex = busy ? Dwt.Z_VEIL : Dwt.Z_HIDDEN;
	this._busyOverlay.className = className ? className : this._busyOverlayDefCName;
	Dwt.setCursor(this._busyOverlay, cursor);
    Dwt.setZIndex(this._busyOverlay, zIndex);
}

DwtShell.prototype.setStatus =
function(statusStr) {
	this.window.status = statusStr;
}

DwtShell.prototype.getToolTip = function() {
	return this._toolTip;
}

DwtShell._preventDefaultSelectPrt =
function(ev) {
    var evt = LsCore.objectWithId(window._dwtShell)._uiEvent;
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
    var evt = LsCore.objectWithId(window._dwtShell)._uiEvent;
    evt.setFromDhtmlEvent(ev);
	//default behavior
    evt._stopPropagation = true;
    evt._returnValue = false;
	var target = ev.target ? ev.target : ev.srcElement;
	if (evt.dwtObj && evt.dwtObj instanceof DwtControl && !evt.dwtObj.preventContextMenu(evt.target))
    {
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

DwtShell._resizeHdlr =
function(ev) {
	var shell = LsCore.objectWithId(window._dwtShell);
	if (shell.isListenerRegistered(DwtEvent.CONTROL)) {
	 	var evt = DwtShell.controlEvent;
	 	evt.reset();
	 	evt.oldWidth = shell._currWinSize.x;
	 	evt.oldHeight = shell._currWinSize.y;
	 	shell._currWinSize = shell.getWindowSize();
	 	evt.newWidth = shell._currWinSize.x;
	 	evt.newHeight = shell._currWinSize.y;
	 	shell.notifyListeners(DwtEvent.CONTROL, evt);
	} else {
		shell._currWinSize = shell.getWindowSize();
	}
}

DwtShell.getShell =
function(win){
	return LsCore.objectWithId(win._dwtShell);
};
