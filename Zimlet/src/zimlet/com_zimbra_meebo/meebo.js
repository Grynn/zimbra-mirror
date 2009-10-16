/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
 *@Author Raja Rao DV
 */

function com_zimbra_meebo() {
}
com_zimbra_meebo.prototype = new ZmZimletBase();
com_zimbra_meebo.prototype.constructor = com_zimbra_meebo;

//meebo class
   var Meebo = {exec:function(){Meebo._.push(arguments)},_:[]}; 
   Meebo.disableSharePageButton=true;

com_zimbra_meebo.prototype.init =
function() {
	this.callCount = 0;
	this._createMeeboFrame();
	//meebo object takes some time to load, we check every 3 seconds for 30 secs before giving up
	 this.timer = setInterval(AjxCallback.simpleClosure(this._showMeebo, this), 3000);	
 };


com_zimbra_meebo.prototype._createMeeboFrame =
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

com_zimbra_meebo.prototype._showMeebo =
function() {
	this.callCount++;
	if(this.callCount == 10) {//after 3*10 seconds, stop checking
		clearInterval(this.timer);
		appCtxt.getAppController().setStatusMsg("Could not load Meebo bar even after 30 secs", ZmStatusView.LEVEL_WARNING);
	}
	if(Meebo) {
		Meebo.unhide(1);
		clearInterval(this.timer);
	}
};



