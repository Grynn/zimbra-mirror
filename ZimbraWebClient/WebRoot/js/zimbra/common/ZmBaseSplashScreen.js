/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 VMware, Inc.
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

ZmBaseSplashScreen = function(shell, imageInfo, className) {

 	if (arguments.length == 0) return;
	
 	if (!(shell instanceof DwtShell)) {
 		throw new AjxException("Parent must be a DwtShell", AjxException.INVALIDPARENT, "ZSplashScreen");
 	}
	
 	className = className || "ZSplashScreen";
 	DwtControl.call(this, {parent:shell, className:className, posStyle:Dwt.ABSOLUTE_STYLE});

	this.__createContents();
}

ZmBaseSplashScreen.prototype = new DwtControl;
ZmBaseSplashScreen.prototype.constructor = ZmBaseSplashScreen;

/** abstract **/
ZmBaseSplashScreen.prototype.getHtml = function() { }

ZmBaseSplashScreen.prototype.setVisible =
function(visible) {
	if (visible == this.getVisible()) {
		return;
	}
	
	if (visible) {
		this.__createContents();
	}		

	DwtControl.prototype.setVisible.call(this, visible);	
	
	if (!visible) {
		this.getHtmlElement().innerHTML = "";
	}
};

ZmBaseSplashScreen.prototype.__createContents =
function() {
	var htmlEl = this.getHtmlElement();
 	htmlEl.style.zIndex = Dwt.Z_SPLASH;
	
 	var myTable = document.createElement("table");
 	myTable.border = myTable.cellSpacing = myTable.cellPadding = 0;
 	Dwt.setSize(myTable, "100%", "100%");
	
 	var row = myTable.insertRow(0);
 	var cell = row.insertCell(0);
 	cell.vAlign = "middle";
 	cell.align = "center";
	cell.innerHTML = this.getHtml();
 	htmlEl.appendChild(myTable);
	htmlEl.style.cursor = "wait";
};
