/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaGlobalMessageVolumePage 
* @contructor ZaGlobalMessageVolumePage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGlobalMessageVolumePage = function(parent) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements

	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaGlobalMessageVolumePage.prototype = new DwtTabViewPage;
ZaGlobalMessageVolumePage.prototype.constructor = ZaGlobalMessageVolumePage;

ZaGlobalMessageVolumePage.prototype.toString = 
function() {
	return "ZaGlobalMessageVolumePage";
}

ZaGlobalMessageVolumePage.prototype.showMe =  function(refresh) {
	DwtTabViewPage.prototype.showMe.call(this);	
	ZaGlobalAdvancedStatsPage.detectFlash(document.getElementById("loggerchartglobalmv-flashdetect"));
	if(refresh) {
		this.setObject();
	}
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-volume-48hours', 'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-48h', 'now', { convertToCount: 1 });
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-volume-30days',  'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-30d', 'now', { convertToCount: 1 });
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-volume-60days',  'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-60d', 'now', { convertToCount: 1 });
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-volume-year',    'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-1y',  'now', { convertToCount: 1 });
}

ZaGlobalMessageVolumePage.prototype.setObject =
function () {
    // noop
}

ZaGlobalMessageVolumePage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(50);
	html[idx++] = "<h1 style='display: none' id='loggerchartglobalmv-flashdetect'></h1>";	
	html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_MV_Header + "</h3>" ;	
	html[idx++] = "<div>";	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left' style='width: 90%'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-volume-48hours'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-volume-30days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-volume-60days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-volume-year'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}