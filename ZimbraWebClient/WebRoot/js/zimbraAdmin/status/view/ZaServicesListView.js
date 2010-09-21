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
* @constructor
* @class ZaServicesListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaServicesListView = function(parent,clsName) {
	if (arguments.length == 0) return;

	var className = clsName || "ZaServicesListView";
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList,
		id:ZaId.TAB_STATUS_MANAGE
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
}

ZaServicesListView.prototype = new ZaListView;
ZaServicesListView.prototype.constructor = ZaServicesListView;

ZaServicesListView.STYLE_CLASS = "Row";
ZaServicesListView.SELECTED_STYLE_CLASS = "Row" + "-" + DwtCssStyle.SELECTED;

ZaServicesListView.prototype.toString = 
function() {
	return "ZaServicesListView";
}

ZaServicesListView.prototype.getTitle = 
function () {
	return ZaMsg.Status_view_title;
}

ZaServicesListView.prototype.getTabIcon = 
function () {
	return "Status" ;
}
/**
* Renders a single item as a DIV element.
*/
ZaServicesListView.prototype._createItemHtml =
function(item, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	div[DwtListView._STYLE_CLASS] = ZaServicesListView.STYLE_CLASS;
	div[DwtListView._SELECTED_STYLE_CLASS] = ZaServicesListView.SELECTED_STYLE_CLASS;
	div.className = ZaServicesListView.STYLE_CLASS;
	
	idx = ZaServicesListView._writeElement.call(this, html, idx, item, false);
	if (item.serviceMap != null) {
		for (var i in item.serviceMap) {
			idx = ZaServicesListView._writeElement.call(this, html, idx, item.serviceMap[i], true, i);
		}
	}
	div.innerHTML = html.join("");
	div.style.height="auto";
	return div;
}

ZaServicesListView._writeElement =
function(html, idx, item, onlyServiceInfo, serviceName) {
	html[idx++] = "<table ";
	if (onlyServiceInfo) {
		html[idx++] = "class='ZaServicesListView_table'";
	} else {
		html[idx++] = "class='ZaServicesListView_server_table'";
	}

	html[idx++] = "_serviceInfo=";
	html[idx++] = onlyServiceInfo;
	html[idx++] = ">";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		if(field == ZaStatus.PRFX_Server) {
			if (!onlyServiceInfo) {
				html[idx++] = "<td width=";
				html[idx++] = (this._headerList[i]._width);
				html[idx++] = "><table cellpadding=0 cellspacing=0 border=0 style='table-layout:fixed;'>";
				html[idx++] = "<tr>";

				html[idx++] = "<td width=\"12px\" aligh=left onclick=\'javascript:ZaServicesListView.expand(event, this)\'>";
				html[idx++] = AjxImg.getImageHtml("NodeExpanded");
				html[idx++] = "</td>";
			
				html[idx++] = "<td align=left width=20>"
				if(item.status == 1) {
					html[idx++] = AjxImg.getImageHtml("Check");
				} else if (item.status == 0){
					html[idx++] = AjxImg.getImageHtml("Cancel");
				} else {
					html[idx++] = "&nbsp;";
				}
				html[idx++] = "</td>";			

				html[idx++] = AjxStringUtil.htmlEncode(item.name);

				html[idx++] = "</td>";
				html[idx++] = "</tr></table></td>";
			} else {
				html[idx++] = "<td width=";
				html[idx++] = (this._headerList[i]._width);
				html[idx++] = " aligh=left>";
				html[idx++] = AjxStringUtil.htmlEncode(" ");
				html[idx++] = "</td>";
			}

		} else if(field == ZaStatus.PRFX_Service) {
			if (onlyServiceInfo) {
				html[idx++] = "<td width=";
				html[idx++] = this._headerList[i]._width;
				html[idx++] = " ><table cellpadding=0 cellspacing=0 border=0><tr><td width=20>";
				if(item.status==1) {
					html[idx++] = AjxImg.getImageHtml("Check");
				} else {
					html[idx++] = AjxImg.getImageHtml("Cancel");
				}				
				html[idx++] = "</td><td>";
				html[idx++] = AjxStringUtil.htmlEncode(serviceName);
				html[idx++] = "</td></tr></table></td>";
			} else {
				html[idx++] = "<td width=";
				html[idx++] = this._headerList[i]._width;
				html[idx++] = " aligh=left>";

				html[idx++] = AjxStringUtil.htmlEncode(" ");
				html[idx++] = "</td>";
			}

		} else if(field == ZaStatus.PRFX_Time) {
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo) {
				if(((new Date()).getTime()- item.timestamp) > 21*60*1000) {
					html[idx++] = "<span class='ZaStaleData'>";
					html[idx++] = AjxStringUtil.htmlEncode(item.time);
					html[idx++] = "&nbsp;";
					html[idx++] = ZaMsg.DataIsStale;
					html[idx++] = "</span>";					
				} else {
					html[idx++] = AjxStringUtil.htmlEncode(item.time);
				}
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			}
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	return idx;
}

ZaServicesListView.prototype._setNoResultsHtml = 
function() {
	var	div = document.createElement("div");
	div.innerHTML = "<table width='100%' cellspacing='0' cellpadding='1'><tr><td class='NoResults'><br>"
					+ ZaMsg.ServerStatusUnavailable + "</td></tr></table>";
	this._parentEl.appendChild(div);
}

ZaServicesListView.prototype._getHeaderList =
function() {
	var headerList = [
		new ZaListHeaderItem(ZaStatus.PRFX_Server, ZaMsg.STV_Server_col, null, 250, null, null, true, true),
		new ZaListHeaderItem(ZaStatus.PRFX_Service, ZaMsg.STV_Service_col, null, 100, null, null, true, true),
		new ZaListHeaderItem(ZaStatus.PRFX_Time, ZaMsg.STV_Time_col, null, 200, null, null, true, true)
	];
	return headerList;
}


ZaServicesListView.expand = function (event, domObj) {
	var ev = DwtUiEvent.getEvent(event);
	var htmlEl = DwtUiEvent.getTarget(ev);
	var table = htmlEl;
	while (table != null){
		if (table.getAttribute("_serviceInfo") != null) {
			break;
		}
		table = table.parentNode;
	}
	var sibling = table.nextSibling;
	var collapse = true;
	if (sibling != null) {
		if (sibling.style.display == "none"){
			domObj.firstChild.className = AjxImg.getClassForImage("NodeExpanded");
			collapse = false;
		} else {
			domObj.firstChild.className = AjxImg.getClassForImage("NodeCollapsed");
		}
		while (sibling != null && sibling.getAttribute("_serviceInfo") == "true") {
			if (collapse){
				sibling.style.display = "none";
			} else {
				sibling.style.display = "";
			}
			sibling = sibling.nextSibling;
		}
	} else {
		domObj.firstChild.className = "";
	}
	
}

