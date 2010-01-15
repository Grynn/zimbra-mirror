/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
* @constructor
* @class ZaServerMiniListView
* @param parent
* @author Greg Solovyev
**/

ZaServerMiniListView = function(parent, className, posStyle, headerList) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent, className, posStyle, headerList);
	this.hideHeader = false;
}  

ZaServerMiniListView.prototype = new ZaListView;
ZaServerMiniListView.prototype.constructor = ZaServerMiniListView;

ZaServerMiniListView.prototype.toString = 
function() {
	return "ZaServerMiniListView";
}

ZaServerMiniListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	} 
}

/**
* Renders a single item as a DIV element.
*/
ZaServerMiniListView.prototype._createItemHtml =
function(server) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(server, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		if(field == ZaServer.A_ServiceHostname) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaServer.A_ServiceHostname]);
			html[idx++] = "</nobr></td>";
		} else if (field == "status") {
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			switch(server.status) {
				case "processing":
					html[idx++] = AjxImg.getImageHtml("DwtWait16Icon");
				break;
				case "error":
					html[idx++] = AjxImg.getImageHtml("Cancel");
				break;
				case "success":
					html[idx++] = AjxImg.getImageHtml("Check");
				break;
				default:
					html[idx++] = AjxImg.getImageHtml("QuestionMark");	
				break;
			}
			
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}