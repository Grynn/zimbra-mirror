/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaServerMsgsStatsPage 
* @contructor ZaServerMsgsStatsPage
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerMsgsStatsPage (parent, app) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = app;
	this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);	
}
 
ZaServerMsgsStatsPage.prototype = new DwtTabViewPage;
ZaServerMsgsStatsPage.prototype.constructor = ZaServerMsgsStatsPage;

ZaServerMsgsStatsPage.prototype.toString = 
function() {
	return "ZaServerMsgsStatsPage";
}

ZaServerMsgsStatsPage.prototype.setObject =
function (item) {
	if(item) {
		if(item.attrs && item.attrs[ZaServer.A_ServiceHostname]) {
			var newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/rcvdmsgs/d/1";
			var imgElement = Dwt.getDomObj(this.getDocument(), this._1DayImgID);
			if(imgElement) {
				imgElement.src = newSrc;
			}
			imgElement = Dwt.getDomObj(this.getDocument(), this._3MonthImgID);	
			newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/rcvdmsgs/m/3";			
			if(imgElement) {
				imgElement.src = newSrc;
			}
			imgElement = Dwt.getDomObj(this.getDocument(), this._12MonthImgID);		
			newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/rcvdmsgs/m/12";			
			if(imgElement) {
				imgElement.src = newSrc;
			}			
		}
	}
}

ZaServerMsgsStatsPage.prototype._createHTML = 
function () {
	var idx = 0;
	var html = new Array(50);
	this._12MonthImgID = Dwt.getNextId();
	this._3MonthImgID = Dwt.getNextId();
	this._1DayImgID = Dwt.getNextId();	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMsgsLastDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._1DayImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMsgsLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._3MonthImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMsgsLast12Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._12MonthImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	this.getHtmlElement().innerHTML = html.join("");
}