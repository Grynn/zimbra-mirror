/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaCosListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/

ZaCosListView = function(parent) {
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaCosListView.prototype = new ZaListView;
ZaCosListView.prototype.constructor = ZaCosListView;

ZaCosListView.prototype.toString = 
function() {
	return "ZaCosListView";
}

ZaCosListView.prototype.getTitle = 
function () {
	return ZaMsg.COS_view_title;
}

ZaCosListView.prototype.getTabIcon =
function () {
	return "COS" ;
}

/**
* Renders a single item as a DIV element.
*/
ZaCosListView.prototype._createItemHtml =
function(cos, no, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(cos, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";


	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaCos.A_name)==0) {
		// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(cos.name);
			html[idx++] = "</nobr></td>";
		} else if (id.indexOf(ZaCos.A_description)==0) {
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(cos.attrs[ZaCos.A_description]);
			html[idx++] = "</nobr></td>";	
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaCosListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
	var sortable = 1;
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	headerList[0] = new ZaListHeaderItem(ZaCos.A_name, ZaMsg.CLV_Name_col, null, 200, sortable++, ZaCos.A_name, true, true);

	headerList[1] = new ZaListHeaderItem(ZaCos.A_description, ZaMsg.CLV_Description_col, null, null, null, null, true, true);
	
	return headerList;
}
