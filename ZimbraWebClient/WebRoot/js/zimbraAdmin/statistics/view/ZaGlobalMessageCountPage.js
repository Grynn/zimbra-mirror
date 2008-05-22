/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
 */

/**
* @class ZaGlobalMessageCountPage 
* @contructor ZaGlobalMessageCountPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGlobalMessageCountPage = function(parent, app) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = app;
	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaGlobalMessageCountPage.prototype = new DwtTabViewPage;
ZaGlobalMessageCountPage.prototype.constructor = ZaGlobalMessageCountPage;

ZaGlobalMessageCountPage.prototype.toString = 
function() {
	return "ZaGlobalMessageCountPage";
}

ZaGlobalMessageCountPage.prototype.showMe =  function(refresh) {
	DwtTabViewPage.prototype.showMe.call(this);	
	if(refresh) {
		this.setObject();
	}
}

ZaGlobalMessageCountPage.prototype.setObject =
function () {
	var imgElement = document.getElementById(this._hourImgID);
	var newSrc = ["/service/statsimg/mta.ALL.hour.Message_Count.gif?rand=",Math.random()].join("");
	if(imgElement) {
		imgElement.src = newSrc;
	}
	imgElement = document.getElementById(this._dayImgID);	
	newSrc = ["/service/statsimg/mta.ALL.day.Message_Count.gif?rand=",Math.random()].join("");			
	if(imgElement) {
		imgElement.src = newSrc;
	}
	imgElement = document.getElementById(this._monthImgID);		
	newSrc = ["/service/statsimg/mta.ALL.month.Message_Count.gif?rand=",Math.random()].join("");			
	if(imgElement) {
		imgElement.src = newSrc;
	}			
	imgElement = document.getElementById(this._yearImgID);		
	newSrc = ["/service/statsimg/mta.ALL.year.Message_Count.gif?rand=",Math.random()].join("");			
	if(imgElement) {
		imgElement.src = newSrc;
	}			
}

ZaGlobalMessageCountPage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(50);
	this._hourImgID = Dwt.getNextId();
	this._dayImgID = Dwt.getNextId();
	this._monthImgID = Dwt.getNextId();		
	this._yearImgID = Dwt.getNextId();	
	html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_MC_Header + "</h3>" ;	
	html[idx++] = "<div style='width:70ex;'>";	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._hourImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#'  alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._dayImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#'  alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._monthImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#'  alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._yearImgID + "'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}