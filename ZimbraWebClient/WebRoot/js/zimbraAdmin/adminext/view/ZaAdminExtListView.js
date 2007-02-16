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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaAdminExtListView
* @param parent
* @author Greg Solovyev
**/

function ZaAdminExtListView(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
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
function(zimlet, now, isDndIcon) {
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
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(zimlet[ZaZimlet.A_name]);
			html[idx++] = "</nobr></td>";
		} else if(id.indexOf(ZaZimlet.A_zimbraZimletDescription )==0) {	
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
	headerList[0] = new ZaListHeaderItem(ZaZimlet.A_name, ZaMsg.CLV_Name_col, null, 200, sortable++, "name", true, true);
	headerList[1] = new ZaListHeaderItem(ZaZimlet.A_zimbraZimletDescription, ZaMsg.DLV_Description_col, null, "auto", null, ZaZimlet.A_zimbraZimletDescription, false, true);
	return headerList;
}


