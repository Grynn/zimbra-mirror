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
 * @author EMC
 **/
function ZaAccMiniListView(parent, className, posStyle, headerList) {
	//var headerList = this._getHeaderList();
	ZaListView.call(this, parent, className, posStyle, headerList);
}

ZaAccMiniListView.prototype = new ZaListView;
ZaAccMiniListView.prototype.constructor = ZaAccMiniListView;

ZaAccMiniListView.prototype.toString = function() {
	return "ZaAccMiniListView";
};

ZaAccMiniListView.prototype._createItemHtml =
function(account, now, isDndIcon) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(account, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		for(var i = 0; i < cnt; i++) {
			var id = this._headerList[i]._id;
			if(id.indexOf("type")==0) {
				// type
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				switch(account.type) {
					case ZaItem.ACCOUNT:
						html[idx++] = AjxImg.getImageHtml("Account");
					break;
					case ZaItem.DL:
						html[idx++] = AjxImg.getImageHtml("Group");				
					break;
					case ZaItem.ALIAS:
						html[idx++] = AjxImg.getImageHtml("AccountAlias");				
					break;								
					default:
						html[idx++] = AjxStringUtil.htmlEncode(account.type);
					break;
				}
				html[idx++] = "</td>";
			} else if(id.indexOf(ZaAccount.A_name)==0) {
				// name
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(account.name);
				html[idx++] = "</td>";
			} else if (id.indexOf(ZaAccount.A_displayname)==0) {
				// display name
				html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
				html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
				html[idx++] = "</nobr></td>";	
			} 
		}
	} else {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(account.name);
		html[idx++] = "</td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaAccMiniListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>&nbsp",
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaAccMiniListView.prototype._sortColumn = function (columnItem, bSortAsc){
	if (bSortAsc) {
		var comparator = function (a, b) {
			return (a < b)? 1 :((a > b)? -1 : 0);
		};
		this.getList().sort(comparator);
	} else {
		this.getList().sort();
	}
};
