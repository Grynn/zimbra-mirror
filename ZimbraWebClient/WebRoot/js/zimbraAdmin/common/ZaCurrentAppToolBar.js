/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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
* This toolbar sits above the overview and represents the current app. It has a label
* that tells the user what the current app is, and an optional View button/menu for
* switching views within the current app.
* @class
*/
ZaCurrentAppToolBar = function(parent, className, buttons) {

	DwtToolBar.call(this, parent, className, Dwt.ABSOLUTE_STYLE);

	this._currentAppLabel = new ZaToolBarLabel(this, DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_LEFT, "currentAppLabel");
	this._currentTitle = null;
	this._lastWidth = null;
	this._currentWidth = null;
	this.addFiller();
}

ZaCurrentAppToolBar.prototype = new DwtToolBar;
ZaCurrentAppToolBar.prototype.constructor = ZaCurrentAppToolBar;

ZaCurrentAppToolBar.prototype.toString = 
function() {
	return "ZaCurrentAppToolBar";
}

ZaCurrentAppToolBar.prototype._resizeListener = 
function() {
	if(!this._currentTitle)
		return;
	
	if(!this._currentWidth && !this._lastWidth){
		this._lastWidth = this._currentWidth = this.getBounds().width;
		this.setCurrentAppLabel(this._currentTitle);
	} else {
		this._currentWidth = this.getBounds().width;
		if(this._lastWidth != this._currentWidth ){
	         	this._lastWidth = this._currentWidth;
			this.setCurrentAppLabel(this._currentTitle);
		}
	}
}

ZaCurrentAppToolBar.prototype.setCurrentAppLabel = 
function(title) {
	this._currentTitle = title;
	if(!this._currentWidth && !this._lastWidth){
        	this._lastWidth = this._currentWidth = this.getBounds().width;
	}
	
	var totalCharWidth = ZaCurrentAppToolBar.getWidth(title);

	if(totalCharWidth > (this._currentWidth - 10) ){ //reserver 10 px
		var textLength = this._currentWidth -  ZaCurrentAppToolBar.getWidth("...") - 10;
		var maxNumberOfLetters=  Math.floor(textLength * (title.length) / totalCharWidth);
		title = title.substring(0, (maxNumberOfLetters - 3)) + "...";
	}
	
	this._currentAppLabel.setText(title);
	//this._currentAppLabel.setImage(ZaZimbraAdmin.APP_ICON[appName]);
}

ZaCurrentAppToolBar.WIDTH = {};
ZaCurrentAppToolBar._cacheSize = 0;
ZaCurrentAppToolBar.MAX_CACHE_SIZE = 10;
ZaCurrentAppToolBar.getWidth = 
function(str){
	if(!ZaCurrentAppToolBar._testSpan){
		var span= ZaCurrentAppToolBar._testSpan = document.createElement("SPAN");
		span.style.position = Dwt.ABSOLUTE_STYLE;
		var shellEl =  DwtShell.getShell(window).getHtmlElement();
		shellEl.appendChild(span);
		Dwt.setLocation(span, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		span.style.fontWeight = "bold";
		span.style.fontSize = "13px";
	}

	var cache = ZaCurrentAppToolBar.WIDTH;
	if(cache[str])
		return cache[str];
	
	if(ZaCurrentAppToolBar._cacheSize >= ZaCurrentAppToolBar.MAX_CACHE_SIZE){
		ZaCurrentAppToolBar.WIDTH = {};
		ZaCurrentAppToolBar._cacheSize = 0;
	}
	
	var span = ZaCurrentAppToolBar._testSpan;
	span.innerHTML = str;
	var w = cache[str] = Dwt.getSize(span).x;
	ZaCurrentAppToolBar._cacheSize++;

	return w;
}
