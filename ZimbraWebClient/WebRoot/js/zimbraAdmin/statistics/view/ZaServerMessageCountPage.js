/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
* @class ZaServerMessageCountPage 
* @contructor ZaServerMessageCountPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerMessageCountPage = function(parent) {
	this.serverId = parent.serverId; //should save this server id firstly
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
        var serverId = this.serverId;

        var charts = document.getElementById('loggerchartservermc-' + serverId);
        charts.style.display = "block";
        var divIds = [ 'servermc-no-mta-' + serverId,
                       'server-message-count-48hours-' + serverId,
                       'server-message-count-30days-' + serverId,
                       'server-message-count-60days-' + serverId,
                       'server-message-count-year-' + serverId 
                      ];
        ZaGlobalAdvancedStatsPage.hideDIVs(divIds);

	    var hosts = ZaGlobalAdvancedStatsPage.getMTAHosts();
	    if (ZaGlobalAdvancedStatsPage.indexOf(hosts, item.name) != -1) {
	        ZaGlobalAdvancedStatsPage.detectFlash(document.getElementById('loggerchartservermc-flashdetect-' + serverId));
            var startTimes = [null, 'now-48h', 'now-30d', 'now-60d', 'now-1y'];
            for (var i=1; i < divIds.length; i++){ //skip divId[0] -- servermv-no-mta
                ZaGlobalAdvancedStatsPage.plotQuickChart( divIds[i], item.name, 'zmmtastats', ['mta_count'], ['msgs'], startTimes[i], 'now', { convertToCount: 1 });
            }


        } else {
            var nomta = document.getElementById('loggerchartservermc-no-mta-' + serverId);
            nomta.style.display = "block";
            charts.style.display = "none";
            ZaGlobalAdvancedStatsPage.setText(nomta, ZaMsg.Stats_NO_MTA);
        }
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
	var serverId = this.serverId;
	DwtTabViewPage.prototype._createHtml.call(this);
	html[idx++] = "<h1 style='display:none;' id='loggerchartservermc-flashdetect-" + serverId + "'></h1>";
	html[idx++] = "<h1 style='display:none;' id='loggerchartservermc-no-mta-" + serverId + "'></h1>";
	html[idx++] = "<div class='StatsHeader'>" + ZaMsg.Stats_MC_Header + "</div>";
	html[idx++] = "<div class='StatsDiv' id='loggerchartservermc-" + serverId + "'>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</div>";
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-count-48hours-" + serverId + "'></div>";
	html[idx++] = "</div>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</div>";
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-count-30days-" + serverId + "'></div>";
	html[idx++] = "</div>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</div>";
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-count-60days-" + serverId + "'></div>";
	html[idx++] = "</div>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</div>";
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-message-count-year-" + serverId + "'></div>";
	html[idx++] = "</div>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}
