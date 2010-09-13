/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

function EmailToolTipSlide(html, visible, iconName, selectCallback, name) {
	this.html = html;
	this.visible = visible;
	this.iconName = iconName;
	this.id = Dwt.getNextId();
	this.iconDivId = Dwt.getNextId();
	this.selectIconDivId = Dwt.getNextId();
	this.slideShow = null;
	this.canvasElement = null;
	this._selectCallback = selectCallback;
	this.name = name;
};

EmailToolTipSlide.prototype.select =
function() {
	if(this.slideShow.currentSlideId) {
		document.getElementById(this.slideShow.currentSlideId).style.display = "none";
	}
	document.getElementById(this.id).style.height = document.getElementById(EmailToolTipSlideShow.mainDivId).offsetHeight;
	document.getElementById(this.id).style.display = "block";

	this.slideShow.currentSlideId = this.id;
	if(this.slideShow.currentSelectIconDivId) {
		document.getElementById(this.slideShow.currentSelectIconDivId).style.display = "none";
	}
	document.getElementById(this.selectIconDivId).style.display = "block";
	this.slideShow.currentSelectIconDivId = this.selectIconDivId;

	if(this._selectCallback) {
		this._selectCallback.run();
	}
};

/**
*Sets main div element that can be used to show info/error-msgs inline
*/
EmailToolTipSlide.prototype.setCanvasElement =
function(el) {
	this.canvasElement = el;
};



EmailToolTipSlide.prototype.setInfoMessage =
function(msg) {
	this._appendMsg2Slide(msg, "EmailToolTipSlideMsgColor");
};


EmailToolTipSlide.prototype.setErrorMessage =
function(msg) {
	this._appendMsg2Slide(msg, "EmailToolTipSlideErrorColor");
};

EmailToolTipSlide.prototype._appendMsg2Slide =
function(msg, colorClass) {
	var html = ["<div class='EmailToolTipSlideText ",colorClass,"'>",msg,"</div>"].join("");
	if(this.canvasElement) {
		this.canvasElement.innerHTML = html;
	}
};

EmailToolTipSlide.prototype.clearSlideMessage =
function(msg, colorClass) {
	if(this.canvasElement) {
		this.canvasElement.innerHTML = "";
	}
};
