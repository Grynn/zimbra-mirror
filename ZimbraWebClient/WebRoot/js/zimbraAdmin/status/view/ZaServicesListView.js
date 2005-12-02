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
* @constructor
* @class ZaServicesListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaServicesListView(parent, app, clsName) {
	if (arguments.length == 0) return;
	this._app = app;
	var className = clsName || "ZaServicesListView";
	//var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	//this.setScrollStyle(DwtControl.SCROLL);
}

ZaServicesListView.prototype = new ZaListView;
ZaServicesListView.prototype.constructor = ZaServicesListView;


ZaServicesListView.prototype.toString = 
function() {
	return "ZaServicesListView";
}

ZaServicesListView.prototype.getTitle = 
function () {
	return ZaMsg.Status_view_title;
}


ZaServicesListView.prototype._getViewPrefix = 
function() {
	return "Status_Service";
}

/**
* Renders a single item as a DIV element.
*/
ZaServicesListView.prototype._createItemHtml =
function(item, now, isDndIcon) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table class='ZaServicesListView_table'>";
	
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaStatus.PRFX_Server)==0) {		
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			html[idx++] = AjxStringUtil.htmlEncode(item.serverName);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Service)==0) {		
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			html[idx++] = AjxStringUtil.htmlEncode(item.serviceName);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Time)==0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			html[idx++] = AjxStringUtil.htmlEncode(item.time);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaStatus.PRFX_Status)==0) {
			html[idx++] = "<td width=" + this._headerList[i]._width + " aligh=left>";
			if(item.status==1) {
				html[idx++] = AjxImg.getImageHtml("Check");
			} else {
				html[idx++] = AjxImg.getImageHtml("Cancel");
			}
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;

}

ZaServicesListView.prototype._setNoResultsHtml = 
function() {
	var	div = document.createElement("div");
	div.innerHTML = "<table width='100%' cellspacing='0' cellpadding='1'><tr><td class='NoResults'><br>Status data is not available.</td></tr></table>";
	this._parentEl.appendChild(div);
}

ZaServicesListView.prototype._sortColumn =
function(columnItem, bSortAsc) {
	var f = columnItem.getSortField();
	var sortFuncDesc = function (a,b) {
		return (a[f] < b[f])? -1: (( a[f] > b[f])? 1: 0);
	}
	var sortFuncAsc = function (a,b) {
		return (a[f] > b[f])? -1: (( a[f] < b[f])? 1: 0);
	}
	if (bSortAsc){
		this.getList().sort(sortFuncAsc);
	} else {
		this.getList().sort(sortFuncDesc);
	}
	this._resetListView();
	this._renderList(this.getList());
};

ZaServicesListView.prototype._getHeaderList =
function() {

	var headerList = [
					  new ZaListHeaderItem(ZaStatus.PRFX_Server, ZaMsg.STV_Server_col, null, 175, true, "serverName", true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Service, ZaMsg.STV_Service_col, null, 100, true, "serviceName", true, true),

					  new ZaListHeaderItem(ZaStatus.PRFX_Status, ZaMsg.STV_Status_col, null, 50, true, "status", true, true),
	
					  new ZaListHeaderItem(ZaStatus.PRFX_Time, ZaMsg.STV_Time_col, null, null, false, null, true, true)

					  ];
	
	return headerList;
}

