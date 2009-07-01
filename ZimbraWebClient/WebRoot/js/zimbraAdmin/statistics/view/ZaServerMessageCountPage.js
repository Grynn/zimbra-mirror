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
* @class ZaServerMessageCountPage 
* @contructor ZaServerMessageCountPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerMessageCountPage = function(parent) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements

	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaServerMessageCountPage.prototype = new DwtTabViewPage;
ZaServerMessageCountPage.prototype.constructor = ZaServerMessageCountPage;

ZaServerMessageCountPage.prototype.toString = 
function() {
	return "ZaServerMessageCountPage";
}

ZaServerMessageCountPage.prototype.showMe =  function(refresh) {
	DwtTabViewPage.prototype.showMe.call(this);	
	if(refresh && this._currentObject) {
		this.setObject(this._currentObject);
	}
	if (this._currentObject) {
	    var item = this._currentObject;
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-count-48hours', item.name, 'zmmtastats', [ 'mta_count' ], [ 'msgs/s' ], 'now-48h', 'now');
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-count-30days',  item.name, 'zmmtastats', [ 'mta_count' ], [ 'msgs/s' ], 'now-30d', 'now');
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-count-60days',  item.name, 'zmmtastats', [ 'mta_count' ], [ 'msgs/s' ], 'now-60d', 'now');
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-count-year',    item.name, 'zmmtastats', [ 'mta_count' ], [ 'msgs/s' ], 'now-1y',  'now');
	}
}

ZaServerMessageCountPage.prototype.setObject =
function (item) {
	this._currentObject = item;	
}

ZaServerMessageCountPage.prototype._createHtml = 
function () {
    var idx = 0;
    var html = new Array(50);
	DwtTabViewPage.prototype._createHtml.call(this);
	html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_MC_Header + "</h3>" ;
	html[idx++] = "<div>";	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left' style='width: 90%'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-count-48hours'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-count-30days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-count-60days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-count-year'></div>";
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}