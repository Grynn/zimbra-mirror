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
 * The Original Code is: Zimbra Collaboration Suite Web Client
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
* @class ZaServerDiskStatsPage3M 
* @contructor ZaServerDiskStatsPage3M
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerDiskStatsPage3M (parent, app) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = app;
	this._createHTML();
	this.initialized=false;
	this.setScrollStyle(DwtControl.SCROLL);
}
 
ZaServerDiskStatsPage3M.prototype = new DwtTabViewPage;
ZaServerDiskStatsPage3M.prototype.constructor = ZaServerDiskStatsPage3M;

ZaServerDiskStatsPage3M.prototype.toString = 
function() {
	return "ZaServerDiskStatsPage3M";
}

ZaServerDiskStatsPage3M.prototype.showMe = 
function () {
	DwtTabViewPage.prototype.showMe.call(this);
}

ZaServerDiskStatsPage3M.prototype.setObject =
function (item) {
	if(item) {
		if(item.attrs && item.attrs[ZaServer.A_ServiceHostname]) {
			var newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/zimbra/m/3";
			var imgElement = Dwt.getDomObj(this.getDocument(), this._ZimbraImgID);
			if(imgElement) {
				imgElement.src = newSrc;
			}
			imgElement = Dwt.getDomObj(this.getDocument(), this._DBImgID);	
			newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/db/m/3";			
			if(imgElement) {
				imgElement.src = newSrc;
			}
			imgElement = Dwt.getDomObj(this.getDocument(), this._StoreImgID);		
			newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/store/m/3";			
			if(imgElement) {
				imgElement.src = newSrc;
			}			
			imgElement = Dwt.getDomObj(this.getDocument(), this._IndexImgID);		
			newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/index/m/3";			
			if(imgElement) {
				imgElement.src = newSrc;
			}			
			imgElement = Dwt.getDomObj(this.getDocument(), this._LogImgID);		
			newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/log/m/3";			
			if(imgElement) {
				imgElement.src = newSrc;
			}			
			imgElement = Dwt.getDomObj(this.getDocument(), this._RedologImgID);		
			newSrc = "/service/statsimg/" + item.attrs[ZaServer.A_ServiceHostname] + "/redolog/m/3";			
			if(imgElement) {
				imgElement.src = newSrc;
			}			
		}
	}
}

ZaServerDiskStatsPage3M.prototype._createHTML = 
function () {
	this._ZimbraImgID = Dwt.getNextId();	
	this._DBImgID = Dwt.getNextId();	
	this._StoreImgID = Dwt.getNextId();		
	this._IndexImgID = Dwt.getNextId();			
	this._LogImgID = Dwt.getNextId();					
	this._RedologImgID = Dwt.getNextId();						
	var idx = 0;
	var html = new Array(50);
	html[idx++] = "<div style='width:70ex;'>";		
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left'>";	
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDataLast3Months) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._ZimbraImgID + "'>";		
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._DBImgID + "'>";			
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._StoreImgID + "'>";			
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._IndexImgID + "'>";			
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._LogImgID + "'>";			
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' id='" + this._RedologImgID + "'>";			
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";	
	this.getHtmlElement().innerHTML = html.join("");
}