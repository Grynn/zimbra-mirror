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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaServerListView
* @param parent
* @author Greg Solovyev
**/

function ZaServerListView(parent) {

//	var className = "ZaServerListView";
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
	//this.addControlListener(new AjxListener(this, ZaServerListView.prototype._controlListener));
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
/**
* Renders a single item as a DIV element.
*/
ZaServerListView.prototype._createItemHtml =
function(server, now, isDndIcon) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(server, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaServer.A_ServiceHostname)==0) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaServer.A_ServiceHostname]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaServer.A_description)==0) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaServer.A_description]);
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaServerListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaServer.A_ServiceHostname, ZaMsg.SLV_ServiceHName_col, null, 200, sortable++, ZaServer.A_ServiceHostname, true, true);

	headerList[1] = new ZaListHeaderItem(ZaServer.A_description, ZaMsg.DLV_Description_col, null, null, null, ZaServer.A_description, true, true);
		
	return headerList;
}


