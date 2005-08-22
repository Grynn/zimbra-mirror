/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

/**
* @class ZaGlobalDiskStatsPageD 
* @contructor ZaGlobalDiskStatsPageD
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaGlobalDiskStatsPageD (parent, app) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = app;
	this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);
}
 
ZaGlobalDiskStatsPageD.prototype = new DwtTabViewPage;
ZaGlobalDiskStatsPageD.prototype.constructor = ZaGlobalDiskStatsPageD;

ZaGlobalDiskStatsPageD.prototype.toString = 
function() {
	return "ZaGlobalDiskStatsPageD";
}

ZaGlobalDiskStatsPageD.prototype.showMe = 
function () {
	DwtTabViewPage.prototype.showMe.call(this);
}

ZaGlobalDiskStatsPageD.prototype._createHTML = 
function () {
	var idx = 0;
	var html = new Array(50);
	html[idx++] = "<div style='width:70ex;'>";		
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left'>";	
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLastDay) + "</td></tr>";	
//	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='/service/statsimg/$y$temw1de/zimbra/d/1'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
//	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='/service/statsimg/$y$temw1de/db/d/1'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
//	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='/service/statsimg/$y$temw1de/store/d/1'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
//	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='/service/statsimg/$y$temw1de/index/d/1'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
//	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='/service/statsimg/$y$temw1de/log/d/1'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
//	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='/service/statsimg/$y$temw1de/redolog/d/1'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";	
	this.getHtmlElement().innerHTML = html.join("");
}