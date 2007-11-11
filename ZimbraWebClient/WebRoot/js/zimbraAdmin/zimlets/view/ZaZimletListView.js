/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
* @class ZaZimletListView
* @param parent
* @author Greg Solovyev
**/

ZaZimletListView = function(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
}

ZaZimletListView.prototype = new ZaListView;
ZaZimletListView.prototype.constructor = ZaZimletListView;
ZaZimletListView.prototype.toString = 
function() {
	return "ZaZimletListView";
}

ZaZimletListView.prototype.getTitle = 
function () {
	return ZaMsg.Zimlets_view_title;
}

ZaZimletListView.prototype.getTabIcon = 
function () {
	return "zimlet" ;
}

/**
* Renders a single item as a DIV element.
*/
ZaZimletListView.prototype._createItemHtml =
function(zimlet, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(zimlet, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaZimlet.A_name)==0) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(zimlet[ZaZimlet.A_name]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaZimlet.A_zimbraZimletDescription )==0) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(zimlet.attrs[ZaZimlet.A_zimbraZimletDescription ]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaZimlet.A_zimbraZimletEnabled )==0) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = (zimlet.attrs[ZaZimlet.A_zimbraZimletEnabled] == "TRUE") ?  AjxStringUtil.htmlEncode(ZaMsg.NAD_Enabled) :AjxStringUtil.htmlEncode(ZaMsg.NAD_Disabled) ;
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaZimletListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaZimlet.A_name, ZaMsg.CLV_Name_col, null, "200px", sortable++, "name", true, true);

	headerList[1] = new ZaListHeaderItem(ZaZimlet.A_zimbraZimletDescription, ZaMsg.DLV_Description_col, null, "250px", null, ZaZimlet.A_zimbraZimletDescription, true, true);

	headerList[2] = new ZaListHeaderItem(ZaZimlet.A_zimbraZimletEnabled, ZaMsg.ALV_Status_col, null, "auto", null, ZaZimlet.A_zimbraZimletEnabled, true, true);	
		
	return headerList;
}


