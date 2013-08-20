/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2012, 2013 Zimbra Software, LLC.
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
* @constructor
* @class ZaServerListView
* @param parent
* @author Greg Solovyev
**/

ZaServerListView = function(parent) {
	if (arguments.length == 0) return;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;	
	var headerList = ZaServerListView._getHeaderList();
	
	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList,
		id:ZaId.TAB_SERVER_MANAGE
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaServerListView.prototype = new ZaListView;
ZaServerListView.prototype.constructor = ZaServerListView;

ZaServerListView.prototype.toString = 
function() {
	return "ZaServerListView";
}

ZaServerListView.prototype.getTitle = 
function () {
	return ZaMsg.Servers_view_title;
}

ZaServerListView.prototype.getTabIcon =
function () {
	return "Server";
}

/**
* Renders a single item as a DIV element.
*/
ZaServerListView.prototype._createItemHtml =
function(server, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(server, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		if(field == "type") {
			// type
			html[idx++] = "<td width=" + this._headerList[i]._width + ">" + AjxImg.getImageHtml("Server") + "</td>";
		} else if(field == ZaServer.A_ServiceHostname) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaServer.A_ServiceHostname]);
			html[idx++] = "</nobr></td>";
		} else if(field == ZaServer.A_description) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(
				ZaItem.getDescriptionValue(server.attrs[ZaServer.A_description]));
			html[idx++] = "</nobr></td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaServerListView._getHeaderList =
function() {

	var headerList = new Array();
	var sortable=1;
	var i = 0 ;
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	headerList[i++] = new ZaListHeaderItem("type", null, null, "22px", null, "objectClass", false, true);
	headerList[i++] = new ZaListHeaderItem(ZaServer.A_ServiceHostname, ZaMsg.SLV_ServiceHName_col, null, 200, sortable++, ZaServer.A_ServiceHostname, true, true);
	headerList[i++] = new ZaListHeaderItem(ZaServer.A_description, ZaMsg.DLV_Description_col, null, "auto", null, ZaServer.A_description, true, true);
		
	return headerList;
}


