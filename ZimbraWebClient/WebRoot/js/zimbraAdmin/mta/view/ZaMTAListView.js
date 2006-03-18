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
* @class ZaMTAListView
* @param parent
* @author Greg Solovyev
**/

function ZaMTAListView(parent) {

	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
	//this.addControlListener(new AjxListener(this, ZaMTAListView.prototype._controlListener));
}

ZaMTAListView.prototype = new ZaListView;
ZaMTAListView.prototype.constructor = ZaMTAListView;

ZaMTAListView.prototype.toString = 
function() {
	return "ZaMTAListView";
}

ZaMTAListView.prototype.getTitle = 
function () {
	return ZaMsg.PostQ_title;
}
/**
* Renders a single item as a DIV element.
*/
ZaMTAListView.prototype._createItemHtml =
function(mta, now, isDndIcon) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(mta, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaMTA.A_Servername)==0) {	
			// name
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(mta[ZaMTA.A_name]);
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaMTA.A_DeferredQ)==0) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = mta[ZaMTA.A_DeferredQ][ZaMTA.A_count];
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaMTA.A_IncomingQ)==0) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = mta[ZaMTA.A_IncomingQ][ZaMTA.A_count];
			html[idx++] = "</td>";
		}  else if(id.indexOf(ZaMTA.A_ActiveQ)==0) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = mta[ZaMTA.A_ActiveQ][ZaMTA.A_count];
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaMTA.A_CorruptQ)==0) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = mta[ZaMTA.A_CorruptQ][ZaMTA.A_count];
			html[idx++] = "</td>";
		} else if(id.indexOf(ZaMTA.A_HoldQ)==0) {	

			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = mta[ZaMTA.A_HoldQ][ZaMTA.A_count];
			html[idx++] = "</td>";
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

	headerList[0] = new ZaListHeaderItem(ZaMTA.A_Servername, ZaMsg.SLV_ServiceHName_col, null, 195, false, null, true, true);

	headerList[1] = new ZaListHeaderItem(ZaMTA.A_DeferredQ, ZaMsg.PQV_DeferredQ_col, null, 60, false, null, true, true);

	headerList[2] = new ZaListHeaderItem(ZaMTA.A_IncomingQ, ZaMsg.PQV_IncomingQ_col, null, 60, false, null, true, true);		
	
	headerList[3] = new ZaListHeaderItem(ZaMTA.A_ActiveQ, ZaMsg.PQV_ActiveQ_col, null, 60, false, null, true, true);		
	
	headerList[4] = new ZaListHeaderItem(ZaMTA.A_CorruptQ, ZaMsg.PQV_CorruptQ_col, null, 60, false, null, true, true);			
	
	headerList[5] = new ZaListHeaderItem(ZaMTA.A_HoldQ, ZaMsg.PQV_HoldQ_col, null, 60, false, null, true, true);		
		
	return headerList;
}


