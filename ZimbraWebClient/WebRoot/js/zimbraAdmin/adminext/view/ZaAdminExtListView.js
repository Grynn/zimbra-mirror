/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaAdminExtListView
* @param parent
* @author Greg Solovyev
**/

ZaAdminExtListView = function(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList,
		id:ZaId.TAB_AE_MANAGE
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
}

ZaAdminExtListView.prototype = new ZaListView;
ZaAdminExtListView.prototype.constructor = ZaAdminExtListView;

ZaAdminExtListView.prototype.toString = 
function() {
	return "ZaAdminExtListView";
}

ZaAdminExtListView.prototype.getTitle = 
function () {
	return ZaMsg.AdminZimlets_view_title;
}

ZaAdminExtListView.prototype.getTabIcon =
function () {
	return "AdminExtension" ;
}
/**
* Renders a single item as a DIV element.
*/
ZaAdminExtListView.prototype._createItemHtml =
function(zimlet, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(zimlet, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		if(field == ZaZimlet.A_name) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(zimlet[ZaZimlet.A_name]);
			html[idx++] = "</nobr></td>";
		} else if(field == ZaZimlet.A_zimbraZimletDescription) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(zimlet.attrs[ZaZimlet.A_zimbraZimletDescription ]);
			html[idx++] = "</nobr></td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaAdminExtListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaZimlet.A_name, ZaMsg.CLV_Name_col, null, 250, sortable++, "name", true, true);
	headerList[1] = new ZaListHeaderItem(ZaZimlet.A_zimbraZimletDescription, ZaMsg.DLV_Description_col, null, "auto", null, ZaZimlet.A_zimbraZimletDescription, false, true);
	return headerList;
}


