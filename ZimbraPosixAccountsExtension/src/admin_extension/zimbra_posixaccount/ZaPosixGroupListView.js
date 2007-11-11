/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
* @class ZaPosixGroupListView
* @param parent
* @author Greg Solovyev
**/

function ZaPosixGroupListView(parent) {

//	var className = "ZaServerListView";
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
	//this.addControlListener(new AjxListener(this, ZaServerListView.prototype._controlListener));
}

ZaPosixGroupListView.prototype = new ZaListView;
ZaPosixGroupListView.prototype.prototype = ZaPosixGroupListView;

ZaPosixGroupListView.prototype.toString = 
function() {
	return "ZaPosixGroupListView";
}

ZaPosixGroupListView.prototype.getTitle = 
function () {
	return "Manage Samba Domains";
}
/**
* Renders a single item as a DIV element.
*/
ZaPosixGroupListView.prototype._createItemHtml =
function(object, now, isDndIcon) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(object, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaPosixGroup.A_gidNumber)==0) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(object.attrs[ZaPosixGroup.A_gidNumber]);
			html[idx++] = "</nobr></td>";
		} else if(id.indexOf(ZaPosixGroup.A_description)==0) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(object.attrs[ZaPosixGroup.A_description]);
			html[idx++] = "</nobr></td>";
		} else if(id.indexOf(ZaPosixGroup.A_cn)==0) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(object.name);
			html[idx++] = "</nobr></td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaPosixGroupListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaPosixGroup.A_gidNumber, "gidNumber", null, 100, null, ZaPosixGroup.A_gidNumber, true, true);
	headerList[1] = new ZaListHeaderItem(ZaPosixGroup.A_cn, "Domain Name", null, 200, null, ZaPosixGroup.A_cn, true, true);
	headerList[2] = new ZaListHeaderItem(ZaPosixGroup.A_description, "Description", null, null, null, ZaPosixGroup.A_description, true, true);
		
	return headerList;
}


