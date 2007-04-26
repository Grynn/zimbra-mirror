/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaServerMessageVolumePage 
* @contructor ZaServerMessageVolumePage
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerMessageVolumePage (parent, app) {
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	this._app = app;
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
}

ZaServerMessageVolumePage.prototype.setObject =
function (item) {
	this._currentObject = item;
	if(item) {
		if(item.attrs && item.attrs[ZaServer.A_ServiceHostname]) {
			var imgElement = document.getElementById(this._hourImgID);
			var newSrc = ["/service/statsimg/mta.", item.name, ".hour.Message_Bytes.gif?rand=",Math.random()].join("");
			if(imgElement) {
				imgElement.src = newSrc;
			}
			imgElement = document.getElementById(this._dayImgID);	
			newSrc = ["/service/statsimg/mta.", item.name, ".day.Message_Bytes.gif?rand=",Math.random()].join("");			
			if(imgElement) {
				imgElement.src = newSrc;
			}
			imgElement = document.getElementById(this._monthImgID);		
			newSrc = ["/service/statsimg/mta.",item.name, ".month.Message_Bytes.gif?rand=",Math.random()].join("");		
			if(imgElement) {
				imgElement.src = newSrc;
			}			
			imgElement = document.getElementById(this._yearImgID);		
			newSrc = ["/service/statsimg/mta.", item.name, ".year.Message_Bytes.gif?rand=",Math.random()].join("");		
			if(imgElement) {
				imgElement.src = newSrc;
			}			
		}
	}
}

ZaServerMessageVolumePage.prototype._createHtml = 
function () {
	DwtTabViewPage.prototype._createHtml.call(this);
	var idx = 0;
	var html = new Array(50);
	this._hourImgID = Dwt.getNextId();
	this._dayImgID = Dwt.getNextId();
	this._monthImgID = Dwt.getNextId();		
	this._yearImgID = Dwt.getNextId();		
	html[idx++] = "<div style='width:70ex;'>";	
	html[idx++] = "<table cellpadding='5' cellspacing='4' border='0' align='left'>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsHour) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._hourImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsDay) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._dayImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsMonth) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._monthImgID + "'>";	
	html[idx++] = "</td></tr>";
	html[idx++] = "<tr valign='top'><td align='left'>&nbsp;&nbsp;</td></tr>";		
	html[idx++] = "<tr valign='top'><td align='left' class='StatsImageTitle'>" + AjxStringUtil.htmlEncode(ZaMsg.NAD_StatsYear) + "</td></tr>";	
	html[idx++] = "<tr valign='top'><td align='left'>";
	html[idx++] = "<img src='#' alt='" + ZaMsg.Stats_Unavailable + "' id='" + this._yearImgID + "'>";
	html[idx++] = "</td></tr>";
	html[idx++] = "</table>";
	html[idx++] = "</div>";
	this.getHtmlElement().innerHTML = html.join("");
}