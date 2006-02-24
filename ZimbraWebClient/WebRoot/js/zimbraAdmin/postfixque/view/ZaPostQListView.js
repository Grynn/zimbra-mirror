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
* @class ZaPostQListView
* @param parent
* @author Greg Solovyev
**/

function ZaPostQListView(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
	//this.addControlListener(new AjxListener(this, ZaPostQListView.prototype._controlListener));
}

ZaPostQListView.prototype = new ZaListView;
ZaPostQListView.prototype.constructor = ZaPostQListView;

ZaPostQListView.prototype.toString = 
function() {
	return "ZaPostQListView";
}

ZaPostQListView.prototype.getTitle = 
function () {
	return ZaMsg.Servers_view_title;
}
/**
* Renders a single item as a DIV element.
*/
ZaPostQListView.prototype._createItemHtml =
function(server, now, isDndIcon) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(server, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaPostQ.A_Servername)==0) {	
			// name
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaPostQ.A_Servername]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaPostQ.A_MailDropQ)==0) {	
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaPostQ.A_MailDropQ]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaPostQ.A_HoldQ)==0) {	
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaPostQ.A_HoldQ]);
			html[idx++] = "</td>";
		}  else if(id.indexOf(ZaPostQ.A_IncommingQ)==0) {	
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaPostQ.A_IncommingQ]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaPostQ.A_ActiveQ)==0) {	
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaPostQ.A_ActiveQ]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaPostQ.A_DeferredQ)==0) {	
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaPostQ.A_DeferredQ]);
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaPostQListView.prototype._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible

	headerList[0] = new ZaListHeaderItem(ZaPostQ.A_Servername, ZaMsg.SLV_ServiceHName_col, null, 200, true, ZaServer.A_ServiceHostname, true, true);

	headerList[1] = new ZaListHeaderItem(ZaPostQ.A_MailDropQ, ZaMsg.PQV_MailDropQ_col, null, null, false, ZaServer.A_description, true, true);

	headerList[2] = new ZaListHeaderItem(ZaPostQ.A_HoldQ, ZaMsg.PQV_HoldQ_col, null, null, false, ZaServer.A_description, true, true);		
	
	headerList[3] = new ZaListHeaderItem(ZaPostQ.A_IncommingQ, ZaMsg.PQV_IncommingQ_col, null, null, false, ZaServer.A_description, true, true);		
	
	headerList[4] = new ZaListHeaderItem(ZaPostQ.A_ActiveQ, ZaMsg.PQV_ActiveQ_col, null, null, false, ZaServer.A_description, true, true);			
	
	headerList[5] = new ZaListHeaderItem(ZaPostQ.A_DeferredQ, ZaMsg.PQV_DeferredQ_col, null, null, false, ZaServer.A_description, true, true);		
		
	return headerList;
}


