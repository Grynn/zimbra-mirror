/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaServerMessageVolumePage 
* @contructor ZaServerMessageVolumePage
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaServerMessageVolumePage = function(parent) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements

	//this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaServerMessageVolumePage.prototype = new DwtTabViewPage;
ZaServerMessageVolumePage.prototype.constructor = ZaServerMessageVolumePage;

ZaServerMessageVolumePage.prototype.toString = 
function() {
	return "ZaServerMessageVolumePage";
}

ZaServerMessageVolumePage.prototype.showMe =  function(refresh) {
	DwtTabViewPage.prototype.showMe.call(this);	
	if(refresh && this._currentObject) {
		this.setObject(this._currentObject);
	}
	if (this._currentObject) {
	    var item = this._currentObject;
	    
        var charts = document.getElementById('loggerchartservermv');
        charts.style.display = "block";
	    ZaGlobalAdvancedStatsPage.hideDIVs([ 'servermv-no-mta',
	     'server-message-volume-48hours', 'server-message-volume-30days',
         'server-message-volume-60days', 'server-message-volume-year' ]);
	    
	    var hosts = ZaGlobalAdvancedStatsPage.getMTAHosts();
	    if (ZaGlobalAdvancedStatsPage.indexOf(hosts, item.name) != -1) {
	        ZaGlobalAdvancedStatsPage.detectFlash(document.getElementById("loggerchartservermv-flashdetect"));
            ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-volume-48hours', item.name, 'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-48h', 'now', { convertToCount: 1 });
            ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-volume-30days',  item.name, 'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-30d', 'now', { convertToCount: 1 });
            ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-volume-60days',  item.name, 'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-60d', 'now', { convertToCount: 1 });
            ZaGlobalAdvancedStatsPage.plotQuickChart('server-message-volume-year',    item.name, 'zmmtastats', [ 'mta_volume' ], [ 'bytes' ], 'now-1y',  'now', { convertToCount: 1 });
        } else {
            var nomta = document.getElementById('loggerchartservermv-no-mta');
            nomta.style.display = "block";
            charts.style.display = "none";
            ZaGlobalAdvancedStatsPage.setText(nomta, ZaMsg.Stats_NO_MTA);
        }
	}
}

ZaServerMessageVolumePage.prototype.setObject =
function (item) {
	this._currentObject = item;
}

ZaServerMessageVolumePage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(50);
	html[idx++] = "<h1 style='display: none' id='loggerchartservermv-flashdetect'></h1>";	
	html[idx++] = "<h1 style='display: none' id='loggerchartservermv-no-mta'></h1>";	
	html[idx++] = "<h3 style='padding-left: 10px'>" + ZaMsg.Stats_MV_Header + "</h3>" ;
	html[idx++] = "<div id='loggerchartservermv'>";	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left' style='width: 90%'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-volume-48hours'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-volume-30days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-volume-60days'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<div id='loggerchartserver-message-volume-year'></div>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}