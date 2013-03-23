/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 VMware, Inc.
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
* @class ZaMTAListView
* @param parent
* @author Greg Solovyev
**/

ZaMTAListView = function(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList,
		id:ZaId.TAB_MTX_MANAGE
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
}

ZaMTAListView.prototype = new ZaListView;

ZaMTAListView.prototype.toString = 
function() {
	return "ZaMTAListView";
}

ZaMTAListView.prototype.getTitle = 
function () {
	return ZaMsg.PostQ_title;
}

ZaMTAListView.prototype.getTabIcon =
function () {
	return "Queue";
}

ZaMTAListView.prototype.setUI = 
function () {
	ZaListView.prototype.setUI.call(this);
	var list = this._list;
	var len = list.size();
	for (var i=0; i < len; i++) {
		var mta = list.get(i);
		var itemId = this._getItemId(mta);
		this.higlightQItem(itemId,ZaMTA.A_DeferredQ, (parseInt(mta[ZaMTA.A_DeferredQ][ZaMTA.A_count]) > parseInt(ZaMTA.threashHold)));
		this.higlightQItem(itemId,ZaMTA.A_IncomingQ, (parseInt(mta[ZaMTA.A_IncomingQ][ZaMTA.A_count]) > parseInt(ZaMTA.threashHold)));
		this.higlightQItem(itemId,ZaMTA.A_ActiveQ, (parseInt(mta[ZaMTA.A_ActiveQ][ZaMTA.A_count]) > parseInt(ZaMTA.threashHold)));
		this.higlightQItem(itemId,ZaMTA.A_CorruptQ, (parseInt(mta[ZaMTA.A_CorruptQ][ZaMTA.A_count]) > parseInt(ZaMTA.threashHold)));
		this.higlightQItem(itemId,ZaMTA.A_HoldQ, (parseInt(mta[ZaMTA.A_HoldQ][ZaMTA.A_count]) > parseInt(ZaMTA.threashHold)));
	}
}

ZaMTAListView.prototype.higlightQItem = function(itemId,queue, higlight) {

	var span = document.getElementById(itemId+"_"+queue);
	if(span) {
		span.style.color = higlight ? "#FD4545" : "black";
		span.style.fontWeight = higlight ? "bold" : "normal";
	}
}
/**
* Renders a single item as a DIV element.
*/
ZaMTAListView.prototype._createItemHtml =
function(mta, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(mta, div, DwtListView.TYPE_LIST_ITEM);
	var itemId = this._getItemId(mta);
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		if(field == ZaMTA.A_Servername) {	
			// name
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(mta[ZaMTA.A_name]);
			html[idx++] = "</td>";
		} else if(field == ZaMTA.A_DeferredQ) {	
			html[idx++] = "<td width=" + this._headerList[i]._width + "><span id='" + (itemId+"_"+ZaMTA.A_DeferredQ)+ "'>";


			html[idx++] = mta[ZaMTA.A_DeferredQ][ZaMTA.A_count];
			html[idx++] = "</span></td>";
		} else if(field == ZaMTA.A_IncomingQ) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + "><span id='" + (itemId+"_"+ZaMTA.A_IncomingQ)+ "'>";

			html[idx++] = mta[ZaMTA.A_IncomingQ][ZaMTA.A_count];
			html[idx++] = "</span></td>";
		}  else if(field == ZaMTA.A_ActiveQ) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + "><span id='" + (itemId+"_"+ZaMTA.A_ActiveQ)+ "'>";
			html[idx++] = mta[ZaMTA.A_ActiveQ][ZaMTA.A_count];
			html[idx++] = "</span></td>";
		} else if(field == ZaMTA.A_CorruptQ) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + "><span id='" + (itemId+"_"+ZaMTA.A_CorruptQ)+ "'>";
			html[idx++] = mta[ZaMTA.A_CorruptQ][ZaMTA.A_count];
			html[idx++] = "</span></td>";
		} else if(field == ZaMTA.A_HoldQ) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + "><span id='" + (itemId+"_"+ZaMTA.A_HoldQ)+ "'>";
			html[idx++] = mta[ZaMTA.A_HoldQ][ZaMTA.A_count];
			html[idx++] = "</span></td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaMTAListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible

	headerList[0] = new ZaListHeaderItem(ZaMTA.A_Servername, ZaMsg.SLV_ServiceHName_col, null, "auto", null, null, true, true);

	headerList[1] = new ZaListHeaderItem(ZaMTA.A_DeferredQ, ZaMsg.PQV_DeferredQ_col, null, 100, null, null, true, true);

	headerList[2] = new ZaListHeaderItem(ZaMTA.A_IncomingQ, ZaMsg.PQV_IncomingQ_col, null, 100, null, null, true, true);		
	
	headerList[3] = new ZaListHeaderItem(ZaMTA.A_ActiveQ, ZaMsg.PQV_ActiveQ_col, null, 100, null, null, true, true);		
	
	headerList[4] = new ZaListHeaderItem(ZaMTA.A_CorruptQ, ZaMsg.PQV_CorruptQ_col, null, 100, null, null, true, true);			
	
	headerList[5] = new ZaListHeaderItem(ZaMTA.A_HoldQ, ZaMsg.PQV_HoldQ_col, null, 100, null, null, true, true);		
		
	return headerList;
}


