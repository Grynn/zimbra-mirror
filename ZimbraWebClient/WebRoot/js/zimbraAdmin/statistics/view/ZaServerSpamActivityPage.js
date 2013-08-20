/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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
* @class ZaServerSpamActivityPage 
* @contructor ZaServerSpamActivityPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerSpamActivityPage = function(parent) {
	this.serverId = parent.serverId; //should pass this server id firstly

	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements

	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaServerSpamActivityPage.prototype = new DwtTabViewPage;
ZaServerSpamActivityPage.prototype.constructor = ZaServerSpamActivityPage;

ZaServerSpamActivityPage.prototype.toString = 
function() {
	return "ZaServerSpamActivityPage";
}

ZaServerSpamActivityPage.prototype.showMe =  function(refresh) {
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

	if(refresh && this._currentObject) {
		this.setObject(this._currentObject);
	}
	if (this._currentObject) {
	    var item = this._currentObject;
        var serverId = this.serverId;

        var charts = document.getElementById('loggerchartserverasav-' + serverId);
        charts.style.display = "block";
        var divIds = [ 'serverasav-no-mta-' + serverId,
            'server-message-asav-48hours-' + serverId,
            'server-message-asav-30days-' + serverId,
            'server-message-asav-60days-' + serverId,
            'server-message-asav-year-' + serverId
        ];

	    ZaGlobalAdvancedStatsPage.hideDIVs(divIds);
	    
	    var hosts = ZaGlobalAdvancedStatsPage.getMTAHosts();
	    if (ZaGlobalAdvancedStatsPage.indexOf(hosts, item.name) != -1) {
	        ZaGlobalAdvancedStatsPage.detectFlash(document.getElementById("loggerchartserverasav-flashdetect-" + serverId));
            var startTimes = [null, 'now-48h', 'now-30d', 'now-60d', 'now-1y'];
            for (var i=1; i < divIds.length; i++){ //skip divId[0] -- servermv-no-mta
                ZaGlobalAdvancedStatsPage.plotQuickChart(divIds[i], item.name, 'zmmtastats', ['filter_virus', 'filter_spam'], ['filtered'], startTimes[i], 'now', { convertToCount: 1 });
            }

        } else {
            var nomta = document.getElementById('loggerchartserverasav-no-mta-' + serverId);
            nomta.style.display = "block";
            charts.style.display = "none";
            ZaGlobalAdvancedStatsPage.setText(nomta, ZaMsg.Stats_NO_MTA);
        }
    }
}

ZaServerSpamActivityPage.prototype.setObject =
function (item) {
	this._currentObject = item;		
}

ZaServerSpamActivityPage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(50);
    var serverId = this.serverId;
	html[idx++] = "<h1 style='display:none;' id='loggerchartserverasav-flashdetect-" + serverId + "'></h1>";	
	html[idx++] = "<h1 style='display:none;' id='loggerchartserverasav-no-mta-" + serverId + "'></h1>";	
	html[idx++] = "<div class='StatsHeader'>" + ZaMsg.Stats_AV_Header + "</div>" ;	
	html[idx++] = "<div class='StatsDiv' id='loggerchartserverasav-" + serverId + "'>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-asav-48hours-" + serverId + "'></div>";	
	html[idx++] = "</div>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-asav-30days-" + serverId + "'></div>";	
	html[idx++] = "</div>";	
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-asav-60days-" + serverId + "'></div>";	
	html[idx++] = "</div>";		
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-asav-year-" + serverId + "'></div>";	
	html[idx++] = "</div>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}
