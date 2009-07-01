/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaGlobalSpamActivityPage 
* @contructor ZaGlobalSpamActivityPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGlobalSpamActivityPage = function(parent) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements

	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaGlobalSpamActivityPage.prototype = new DwtTabViewPage;
ZaGlobalSpamActivityPage.prototype.constructor = ZaGlobalSpamActivityPage;

ZaGlobalSpamActivityPage.prototype.toString = 
function() {
	return "ZaGlobalSpamActivityPage";
}


ZaGlobalSpamActivityPage.prototype.showMe =  function(refresh) {
	DwtTabViewPage.prototype.showMe.call(this);	
	if(refresh) {
		this.setObject();
	}
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-asav-48hours', 'zmmtastats', [ 'filter_virus', 'filter_spam' ], [ 'filter/s' ], 'now-48h', 'now');
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-asav-30days',  'zmmtastats', [ 'filter_virus', 'filter_spam' ], [ 'filter/s' ], 'now-30d', 'now');
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-asav-60days',  'zmmtastats', [ 'filter_virus', 'filter_spam' ], [ 'filter/s' ], 'now-60d', 'now');
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-asav-year',    'zmmtastats', [ 'filter_virus', 'filter_spam' ], [ 'filter/s' ], 'now-1y',  'now');
}

ZaGlobalSpamActivityPage.prototype.setObject =
function () {
    // noop
}

ZaGlobalSpamActivityPage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(50);
	html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_AV_Header + "</h3>" ;	
	html[idx++] = "<div>";	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left' style='width: 90%'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-asav-48hours'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-asav-30days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-asav-60days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartglobal-message-asav-year'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}
