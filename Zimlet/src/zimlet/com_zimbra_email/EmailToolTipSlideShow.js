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

EmailToolTipSlideShow.mainDivId = "EmailZimlet_slidesMainDiv";
EmailToolTipSlideShow.navDivId = "EmailZimlet_slidesNavDiv";
EmailToolTipSlideShow.navTableRowId = "EmailZimlet_slidesNavTableRow";
EmailToolTipSlideShow.navTableSelectRowId =  "EmailZimlet_navTableSelectRow";

function EmailToolTipSlideShow(zimlet, canvas) {
	this.slidesIconAndSlideMap = [];
	this.numberOfSlides = 0;
	this.emailZimlet = zimlet;
	this.canvas = canvas;
	this.isVeilShown = false;
	this._createFrame(canvas);
	this.mainDiv = document.getElementById(EmailToolTipSlideShow.mainDivId);
	this.navDiv = document.getElementById(EmailToolTipSlideShow.navDivId);
	this.navTableRow = document.getElementById(EmailToolTipSlideShow.navTableRowId);
	this.navTableSelectRow = document.getElementById(	EmailToolTipSlideShow.navTableSelectRowId);


	this.currentSelectIconDivId = null;
	this.currentSlideId = null;
	this.navDiv.onclick = AjxCallback.simpleClosure(this._handleClick, this);
	canvas.onmouseover =  AjxCallback.simpleClosure(this.showTooltipVeil, this);
	//this.mainDiv.onmouseout =  AjxCallback.simpleClosure(this.hideTooltipVeil, this);

};

EmailToolTipSlideShow.prototype._createFrame =
function(canvas) {
	canvas.innerHTML = ["<div id='", EmailToolTipSlideShow.mainDivId, "' height='", EmailTooltipZimlet.height, "'   width='",EmailTooltipZimlet.width , 
						"'  style='height:",EmailTooltipZimlet.height,"px;width:",EmailTooltipZimlet.width ,"px;'></div><div class='ImgEmailZimletNavBarBG' id='", 
						EmailToolTipSlideShow.navDivId, "'><table cellpadding=0 cellspacing=0><tr id='",EmailToolTipSlideShow.navTableRowId,"'></tr>",
						"<tr id='",EmailToolTipSlideShow.navTableSelectRowId,"'></tr></table></div>"].join("");
};

EmailToolTipSlideShow.prototype._handleClick =
function(e) {
	if (!e){
		var e = window.event;
	}
	var targ;
	if (e.target) {
		targ = e.target;
	} else if (e.srcElement) {
		targ = e.srcElement;
	}
	if (targ.nodeType == 3) {
		targ = targ.parentNode;
	}
	if(targ.id) {
		this.slidesIconAndSlideMap[targ.id].select();
	}
};

EmailToolTipSlideShow.prototype.addSlide =
function(slide, index) {
	slide.slideShow = this;
	if(!index) {
		index = this.numberOfSlides;
	}
	this.slidesIconAndSlideMap[slide.iconDivId] = slide;
	var div  =  document.createElement("div");
	div.id = slide.id;
	div.style.display = "none";
	this.mainDiv.appendChild(div);
	div.innerHTML = slide.html;

	this._insertSlideIcon(slide);
	this.numberOfSlides++;
};

EmailToolTipSlideShow.prototype._insertSlideIcon =
function(slide) {
	var iconName = slide.iconName;
	var iconDivId = slide.iconDivId;
	var selectIconDivId = slide.selectIconDivId;
	var name = slide.name;
	var iconCell = this.navTableRow.insertCell(0);
	iconCell.width= '25px';
	iconCell.align="center";
	iconCell.innerHTML = ["<div title='",name,"' id='",iconDivId,"' class='Img", iconName, "' style='cursor:pointer;'></div>"].join("");

	var selectIconCell = this.navTableSelectRow.insertCell(0);
	selectIconCell.width= '25px';
	selectIconCell.align="center";
	selectIconCell.innerHTML = ["<div id='",selectIconDivId,"' class='ImgEmailZimletRadio' style='width:16px;padding-top:2px;display:none'></div>"].join("");
};

EmailToolTipSlideShow.prototype.showTooltipVeil =
function() {
	var veilId = "EmailTooltipSlideShow_veil"+Dwt.getNextId();

	if (this._toolTipVeil) {
		this._toolTipVeil.style.display = "block";
		return;
	}
	this._toolTipVeil = this.emailZimlet.getShell().getHtmlElement().appendChild(document.createElement('div'));
	var styleObj = this._toolTipVeil.style;
	styleObj.position = "absolute";
	styleObj.id =veilId;
	styleObj.display = "block";
	styleObj.width = "100%";
	styleObj.height = "100%";
	styleObj.zIndex = "700";
	styleObj.background = "black";
	styleObj.opacity = 0.2;
	styleObj.zoom = 1;
	styleObj.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=20)";
	styleObj.cursor = "pointer";
	this.emailZimlet.seriesAnimation.addFadeIn(veilId);
	this.emailZimlet.seriesAnimation.startAnimation();
	this._toolTipVeil.onclick =  AjxCallback.simpleClosure(this.hideTooltipVeil, this);
	this._autoHideVeilIfTooltipIsPoppedDownTimer = setInterval(AjxCallback.simpleClosure(this._hideVeilIfToolTipIsDown, this), 2000);
	this.isVeilShown = true;
};

EmailToolTipSlideShow.prototype.hideTooltipVeil =
function() {
	if (this._toolTipVeil) {
		this._toolTipVeil.style.display = "none";
	}
	this.emailZimlet.tooltip.popdown();
	clearInterval(this._autoHideVeilIfTooltipIsPoppedDownTimer);
	this.isVeilShown = false;
};

EmailToolTipSlideShow.prototype._hideVeilIfToolTipIsDown =
function() {
	if(this.emailZimlet.tooltip && !this.emailZimlet.tooltip._poppedUp && this.isVeilShown) {
		this.hideTooltipVeil();
	}
};