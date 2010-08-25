/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
 * Constructor.
 *
 * @author		Raja Rao
 */
function com_zimbra_meebo_HandlerObject() {
}
com_zimbra_meebo_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_meebo_HandlerObject.prototype.constructor = com_zimbra_meebo_HandlerObject;

/**
 * Simplify handler object
 *
 */
var MeeboZimlet = com_zimbra_meebo_HandlerObject;

/**
 * Defines the "load" interval (milliseconds).
 */
MeeboZimlet.LOAD_INTERVAL = 5000;
/**
 * Defines the "load" timeout (milliseconds).
 */
MeeboZimlet.LOAD_TIMEOUT = 60000;

/**
 * Meebo class
 */
 
var Meebo = {exec:function() {
	Meebo._.push(arguments)
},_:[]};
Meebo.disableSharePageButton = true;

/**
 * Initializes the zimlet.
 */
MeeboZimlet.prototype.init =
function() {


	this._addResizeHandler();
	this.callCount = 0;
	this._createMeeboFrame();

	this.timer = setInterval(AjxCallback.simpleClosure(this._showMeebo, this), MeeboZimlet.LOAD_INTERVAL);
};

/**
 * Creates the Meebo iframe.
 *
 */
MeeboZimlet.prototype._createMeeboFrame =
function() {
	var network = "zimbra";
	var el = document.createElement("div");
	el.id = "meebo";
	el.style.display = "none";
	var frame = document.createElement("iframe");
	frame.id = "meebo-iframe";
	frame.src = this.getResource("/meebo.html?network=" + network);
	el.appendChild(frame);
	this.getShell().getHtmlElement().appendChild(el);

	this._taskBar = appCtxt.getAppViewMgr().getCurrentViewComponent(ZmAppViewMgr.C_TASKBAR);
	this.meeboBarEl = el;
};

/**
 * Shows the meebo iframe.
 *
 */
MeeboZimlet.prototype._showMeebo =
function() {
	this.callCount++;

	if (this.callCount == (MeeboZimlet.LOAD_TIMEOUT / MeeboZimlet.LOAD_INTERVAL)) { //after 60 seconds, stop checking
		clearInterval(this.timer);
		var errMsg = AjxMessageFormat.format(this.getMessage("MeeboZimlet_error_loadBar"), MeeboZimlet.LOAD_TIMEOUT / 1000);
		appCtxt.getAppController().setStatusMsg(errMsg, ZmStatusView.LEVEL_WARNING);
		if (!this._taskBar) {//remove space
			document.getElementById("skin_container_taskbar").innerHTML = "";
			document.getElementById("skin_container_taskbar").style.display = "none";
		}
		return;
	}
	if (typeof Meebo == 'undefined') {
		return;
	}

	if (Meebo) {
		try {
			Meebo.unhide(1);
		} catch(e) {
			clearInterval(this.timer);
			appCtxt.setStatusMsg(this.getMessage("MeeboZimlet_couldNotUnhide"), ZmStatusView.LEVEL_WARNING);
			if (!this._taskBar) {//remove space
				document.getElementById("skin_container_taskbar").innerHTML = "";
				document.getElementById("skin_container_taskbar").style.display = "none";
			}
			return;
		}
		clearInterval(this.timer);
		if (this._taskBar) {
			this._taskBar.getHtmlElement().style.display = "none";
		} else {//add space so the rest of the widgets think something will occupy this space
			document.getElementById("skin_container_taskbar").innerHTML = "<br/><br/>";
		}
		this.meeboBarEl.style.display = "block";
		this._resizeHandler();
		this._meeboBarShown = true;
	}
};

/**
 * Adds the resize handler.
 *
 */
MeeboZimlet.prototype._addResizeHandler =
function() {
	this.getShell().addControlListener(new AjxListener(this, this._resizeHandler));
};

/**
 * Resize handler.
 *
 */
MeeboZimlet.prototype._resizeHandler =
function() {
	var treeWidth = (document.getElementById("skin_container_tree").clientWidth + 5);
	var shellWidth = this.getShell().getHtmlElement().clientWidth;
	this.meeboBarEl.style.left = treeWidth + "px";//move the bar to the left
	this.meeboBarEl.style.width = (shellWidth - treeWidth) + "px"; //resize the width of the bar
	if (!this._meeboBarShown) {//hide if its not supposed to be shown
		this._meeboBarOldLeft = document.getElementById("meebo").style.left;
		this.menuItemSelected();
	}
};

/**
 * Called by framework upon single click
 *
 */
MeeboZimlet.prototype.singleClicked = function() {
	this.menuItemSelected();
};

/**
 * Called by framework upon double click
 *
 */
MeeboZimlet.prototype.doubleClicked = function() {
	this.menuItemSelected();
};


/**
 * Called by framework when context menu was clicked
 *
 */
MeeboZimlet.prototype.menuItemSelected = function() {
	if (this._meeboBarShown) {//hide
		document.getElementById("meebo").style.display = "none";
		this._meeboBarOldLeft = document.getElementById("meebo").style.left;
		document.getElementById("meebo").style.left = "-9999px";
		if (this._taskBar) {
			this._taskBar.getHtmlElement().style.display = "block";
		} else {
			document.getElementById("skin_container_taskbar").innerHTML = "";
		}
		this._meeboBarShown = false;
	} else {
		document.getElementById("meebo").style.display = "block";
		document.getElementById("meebo").style.left = this._meeboBarOldLeft;
		if (this._taskBar) {
			this._taskBar.getHtmlElement().style.display = "none";
		} else {
			document.getElementById("skin_container_taskbar").innerHTML = "<br/><br/>";
		}
		this._meeboBarShown = true;
	}
}