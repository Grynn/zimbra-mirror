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

function EmailToolTipSlide(html, visible, iconName, selectCallback) {
	this.html = html;
	this.visible = visible;
	this.iconName = iconName;
	this.id = Dwt.getNextId();
	this.iconDivId = Dwt.getNextId();
	this.selectIconDivId = Dwt.getNextId();
	this.slideShow = null;
	this._selectCallback = selectCallback;
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
