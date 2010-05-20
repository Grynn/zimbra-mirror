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
var Meebo = {exec:function(){Meebo._.push(arguments)},_:[]}; 
Meebo.disableSharePageButton=true;

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
	document.body.appendChild(el);
};

/**
 * Shows the meebo iframe.
 * 
 */
MeeboZimlet.prototype._showMeebo =
function() {
	this.callCount++;
	if (this.callCount == (MeeboZimlet.LOAD_TIMEOUT/MeeboZimlet.LOAD_INTERVAL) ) { //after 60 seconds, stop checking
		clearInterval(this.timer);
		var errMsg = AjxMessageFormat.format(this.getMessage("MeeboZimlet_error_loadBar"), MeeboZimlet.LOAD_TIMEOUT/1000);
		appCtxt.getAppController().setStatusMsg(errMsg, ZmStatusView.LEVEL_WARNING);
	}
	if(Meebo) {
		Meebo.unhide(1);
		clearInterval(this.timer);
	}
};

/**
 * Adds the resize handler.
 * 
 */
MeeboZimlet.prototype._addResizeHandler =
function() {
	this._view = appCtxt.getCurrentView();
	this._view.addControlListener(new AjxListener(this, this._resizeHandler));//add resize handler
};

/**
 * Resize handler.
 * 
 */
MeeboZimlet.prototype._resizeHandler =
function(ev) {
	var el = appCtxt.getShell().getHtmlElement();
	var bodyHeight = el.offsetParent.offsetHeight;
	el.style.height = (bodyHeight - 26) + "px";//always set shell's height 26px less than body's height
};