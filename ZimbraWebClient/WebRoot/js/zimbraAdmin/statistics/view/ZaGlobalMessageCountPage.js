/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaGlobalMessageCountPage 
* @contructor ZaGlobalMessageCountPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGlobalMessageCountPage = function(parent) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	//this._app = ZaApp.getInstance();
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
    this.setZIndex(DwtTabView.Z_ACTIVE_TAB);
	if (this.parent.getHtmlElement().offsetHeight > 26) { 						// if parent visible, use offsetHeight
		this._contentEl.style.height=this.parent.getHtmlElement().offsetHeight-26;
	} else {
		var parentHeight = parseInt(this.parent.getHtmlElement().style.height);	// if parent not visible, resize page to fit parent
		var units = AjxStringUtil.getUnitsFromSizeString(this.parent.getHtmlElement().style.height);
		if (parentHeight > 26) {
			this._contentEl.style.height = (Number(parentHeight-26).toString() + units);
		}
	}
	this._contentEl.style.width = this.parent.getHtmlElement().style.width;	// resize page to fit parent

	ZaGlobalAdvancedStatsPage.detectFlash(document.getElementById("loggerchartglobalmc-flashdetect"));
	if (refresh)
	    this.setObject();
}

ZaGlobalMessageCountPage.prototype.setObject =
function () {
    // noop
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-count-48hours', 'zmmtastats', [ 'mta_count' ], [ 'msgs' ], 'now-48h', 'now', { convertToCount: 1 });
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-count-30days',  'zmmtastats', [ 'mta_count' ], [ 'msgs' ], 'now-30d', 'now', { convertToCount: 1 });
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-count-60days',  'zmmtastats', [ 'mta_count' ], [ 'msgs' ], 'now-60d', 'now', { convertToCount: 1 });
    ZaGlobalAdvancedStatsPage.plotGlobalQuickChart('global-message-count-year',    'zmmtastats', [ 'mta_count' ], [ 'msgs' ], 'now-1y',  'now', { convertToCount: 1 });
}

ZaGlobalMessageCountPage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(50);
	html[idx++] = "<div style='display:none;' id='loggerchartglobalmc-flashdetect'></div>";	
	html[idx++] = "<div class='StatsHeader'>" + ZaMsg.Stats_MC_Header + "</div>";	
	html[idx++] = "<div class='StatsDiv'>";	
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartglobal-message-count-48hours'></div>";	
	html[idx++] = "</div>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartglobal-message-count-30days'></div>";	
	html[idx++] = "</div>";	
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartglobal-message-count-60days'></div>";	
	html[idx++] = "</div>";		
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartglobal-message-count-year'></div>";	
	html[idx++] = "</div>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}

