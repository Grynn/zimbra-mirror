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
EmailToolTipSlideShow.closeBtnId = "emailTooltip_CloseTooltipBtnId";

function EmailToolTipSlideShow(zimlet, canvas) {
	this.slidesIconAndSlideMap = [];
	this.numberOfSlides = 0;
	this.emailZimlet = zimlet;
	this.canvas = canvas;
	this._createFrame(canvas);
	this.mainDiv = document.getElementById(EmailToolTipSlideShow.mainDivId);
	this.navDiv = document.getElementById(EmailToolTipSlideShow.navDivId);
	this.navTableRow = document.getElementById(EmailToolTipSlideShow.navTableRowId);
	this.currentSelectCellId = null;
	this.currentSlideId = null;
	this.navDiv.onclick = AjxCallback.simpleClosure(this._handleClick, this);
	canvas.onmouseover =  AjxCallback.simpleClosure(this.handleMouseOver, this);
	canvas.onmouseout = AjxCallback.simpleClosure(this.handleMouseOut, this);
	//set height and width to make it work in IE
	this.mainDiv.style.width = EmailTooltipZimlet.tooltipWidth + "px";
	this.mainDiv.style.height =  EmailTooltipZimlet.tooltipHeight + "px";
	this.navDiv.style.width = EmailTooltipZimlet.tooltipWidth + "px";
};

EmailToolTipSlideShow.prototype._createFrame =
function(canvas) {
	canvas.innerHTML = ["<div id='", EmailToolTipSlideShow.mainDivId, "' height='", EmailTooltipZimlet.height, "'   width='",EmailTooltipZimlet.width , 
						"'  style='height:",EmailTooltipZimlet.height,"px;width:",EmailTooltipZimlet.width ,"px;'></div><div  id='", 
						EmailToolTipSlideShow.navDivId, "'><table  cellpadding=0 cellspacing=0><tr><td><table width=100% cellpadding=0 cellspacing=0><tr id='",
						EmailToolTipSlideShow.navTableRowId,"'></tr></table></td><td width=90% id='emailTooltip_CloseTooltipBtnId'></td></tr></table></div>"].join("");
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
		if(this.slidesIconAndSlideMap[targ.id]){
			this.slidesIconAndSlideMap[targ.id].select();
		}
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
	var selectCellId = slide.selectCellId;
	var name = slide.name;
	var iconCell = this.navTableRow.insertCell(0);
	this._insertIconHtml(iconCell, selectCellId, name, iconDivId, iconName);
};

EmailToolTipSlideShow.prototype._insertIconHtml =
function(iconCell, selectCellId, name, iconDivId, iconName) {
	iconCell.width= '25px';
	iconCell.align="center";
	iconCell.id = selectCellId;
	iconCell.style.padding = "3px";
	iconCell.style.paddingTop = "0px";

	iconCell.innerHTML = ["<div title='",name,"' id='",iconDivId,"' class='Img", iconName, "' style='cursor:pointer;'></div>"].join("");
};

EmailToolTipSlideShow.prototype.handleMouseOver =
function() {
	this.isMouseOverTooltip = true;
};

EmailToolTipSlideShow.prototype.handleMouseOut =
function() {
	this.isMouseOverTooltip = false;
	this.emailZimlet.hoverOut();

};