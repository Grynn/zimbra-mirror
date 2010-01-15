/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * @author Greg Solovyev
 **/
ZaGalObjMiniListView = function(parent, className, posStyle, headerList) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent, className, posStyle, headerList);
	this.hideHeader = true;
}

ZaGalObjMiniListView.prototype = new ZaListView;
ZaGalObjMiniListView.prototype.constructor = ZaGalObjMiniListView;

ZaGalObjMiniListView.prototype.toString = function() {
	return "ZaGalObjMiniListView";
};

ZaGalObjMiniListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	} 
}


ZaGalObjMiniListView.prototype._createItemHtml =
function(account, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(account, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(typeof(account)=="string") {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(account);
		html[idx++] = "</td>";			
	} else if(this._headerList) {
		var cnt = this._headerList.length;
		for(var i = 0; i < cnt; i++) {
			var field = this._headerList[i]._field;
		 	if(field == "fullName") {
				// name
				html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(account.fullName);
				html[idx++] = "</nobr></td>";
			} else if (field == "email") {
				// display name
				html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(account.email);
				html[idx++] = "</nobr></td>";	
			} 
		}
	} else if(typeof(account)=="object" && account.email) {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(account.email);
		html[idx++] = "</td>";
	} else {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(String(account));		
		html[idx++] = "</td>";		
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaGalObjMiniListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>",ZaMsg.Domain_GALTestNoResults,
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaGalObjMiniListView.prototype._sortColumn = function (columnItem, bSortAsc){
	if(this.parent.parent.searchAccounts) {
		this.parent.parent.searchAccounts(columnItem.getSortField(),bSortAsc);
	}
};
