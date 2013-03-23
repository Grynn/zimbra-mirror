/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
* @class ZaServerDiskStatsPage 
* @contructor ZaServerDiskStatsPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerDiskStatsPage = function(parent) {
	this.serverId = parent.serverId; //should save this server id firstly

	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements

	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaServerDiskStatsPage.prototype = new DwtTabViewPage;
ZaServerDiskStatsPage.prototype.constructor = ZaServerDiskStatsPage;

ZaServerDiskStatsPage.prototype.toString = 
function() {
	return "ZaServerDiskStatsPage";
}

ZaServerDiskStatsPage.prototype.showMe =  function(refresh) {
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

        ZaGlobalAdvancedStatsPage.detectFlash(document.getElementById("loggerchart-flashdetect-" + serverId));
        if (!this._disks) {
            var counters = ZaGlobalAdvancedStatsPage.getCounters(item.name, 'df.csv');
            var diskKeys = {};
            for (var i = 0; i < counters.length; i++) {
                var disk = counters[i].split("::");
                diskKeys[disk[0]] = 1;
            }
            var disks = [];
            for (var i in diskKeys)
                disks.push(i);
            this._disks = disks;
        }
        
        var columns = [];
        for (var i = 0; i < this._disks.length; i++) {
            columns.push(this._disks[i] + "::disk_pct_used");
        }

        var divIds = [ 'server-disk-stat-48hours-' + serverId,
                       'server-disk-stat-30days-' + serverId,
                       'server-disk-stat-60days-' + serverId,
                       'server-disk-stat-year-' + serverId
                     ];

        var startTimes = ['now-48h', 'now-30d', 'now-60d', 'now-1y'];
        for (var i=0; i < divIds.length; i++){
            ZaGlobalAdvancedStatsPage.plotQuickChart(divIds[i],  item.name, 'df.csv', columns, null, startTimes[i], 'now');
        }
	}
}

ZaServerDiskStatsPage.prototype.setObject =
function (item) {
	this._currentObject = item;	
}

ZaServerDiskStatsPage.prototype._createHtml = 
function () {
    var idx = 0;
    var html = new Array(50);
	DwtTabViewPage.prototype._createHtml.call(this);
	var serverId = this.serverId;
	html[idx++] = "<h1 style='display:none;' id='loggerchart-flashdetect-" + serverId + "'></h1>";
	//html[idx++] = "<div class='StatsHeader'>" + ZaMsg.Stats_MC_Header + "</div>";	
	html[idx++] = "<div class='StatsDiv'>";	
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-48hours-" + serverId + "'></div>";	
	html[idx++] = "</div>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-30days-" + serverId + "'></div>";	
	html[idx++] = "</div>";
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-60days-" + serverId + "'></div>";	
	html[idx++] = "</div>";		
	html[idx++] = "<div class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</div>";	
	html[idx++] = "<div class='StatsImage'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-year-" + serverId + "'></div>";
	html[idx++] = "</div>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}
