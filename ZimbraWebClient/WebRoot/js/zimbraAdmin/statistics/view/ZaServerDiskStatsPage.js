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
* @class ZaServerDiskStatsPage 
* @contructor ZaServerDiskStatsPage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerDiskStatsPage = function(parent) {
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
	DwtTabViewPage.prototype.showMe.call(this);	
	ZaGlobalAdvancedStatsPage.detectFlash(document.getElementById("loggerchart-flashdetect"));
	if(refresh && this._currentObject) {
		this.setObject(this._currentObject);
	}
	if (this._currentObject) {
	    var item = this._currentObject;
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
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-disk-stat-48hours', item.name, 'df.csv', columns, null, 'now-48h', 'now');
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-disk-stat-30days',  item.name, 'df.csv', columns, null, 'now-30d', 'now');
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-disk-stat-60days',  item.name, 'df.csv', columns, null, 'now-60d', 'now');
        ZaGlobalAdvancedStatsPage.plotQuickChart('server-disk-stat-year',    item.name, 'df.csv', columns, null, 'now-1y',  'now');
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
	html[idx++] = "<h1 style='display: none' id='loggerchart-flashdetect'></h1>";	
	//html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_MC_Header + "</h3>" ;
	html[idx++] = "<div>";	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left' style='width: 90%'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-48hours'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-30days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-60days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-disk-stat-year'></div>";
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}