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
 * The Original Code is: Zimbra Collaboration Suite.
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
* @class ZaAccountListView
* @param parent
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaAccountListView(parent, app) {
	this._app = app;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	
	var headerList = this._getHeaderList();
	
	ZaListView.call(this, parent, className, posStyle, headerList);

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this.setScrollStyle(DwtControl.SCROLL);
}

ZaAccountListView.prototype = new ZaListView;
ZaAccountListView.prototype.constructor = ZaAccountListView;


ZaAccountListView.prototype.toString = 
function() {
	return "ZaAccountListView";
}

/**
* Renders a single item as a DIV element.
*/
ZaAccountListView.prototype._createItemHtml =
function(account, now, isDndIcon) {
	var html = new Array(50);
	var	div = this.getDocument().createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(account, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaAccount.A_name)==0) {
			// name
			html[idx++] = "<td width=" + this._headerList[i]._width + ">";
			html[idx++] = AjxStringUtil.htmlEncode(account.name);
			html[idx++] = "</td>";
		} else if (id.indexOf(ZaAccount.A_displayname)==0) {
			// display name
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
			html[idx++] = "</nobr></td>";	
		} else if(id.indexOf(ZaAccount.A_accountStatus)==0) {
			// status
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.accountStatus(account.attrs[ZaAccount.A_accountStatus]));
			html[idx++] = "</nobr></td>";		
		} else if (id.indexOf(ZaAccount.A_description)==0) {		
			// description
			html[idx++] = "<td width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_description]);
			html[idx++] = "</nobr></td>";	
		}
	}
		html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaAccountListView.prototype._getHeaderList =
function() {

	var headerList = new Array();

	headerList[0] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, 195, true, ZaAccount.A_uid, true, true);
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	headerList[1] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, 145, true,ZaAccount.A_displayname, true, true);

	headerList[2] = new ZaListHeaderItem(ZaAccount.A_accountStatus, ZaMsg.ALV_Status_col, null, 80, true,ZaAccount.A_accountStatus, true, true);

	headerList[3] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col, null, null, false, null,true, true );
	
	return headerList;
}


ZaAccountListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	try {
		var searchResult=ZaAccount.searchByQueryHolder(this._app.getAccountListController().getQuery(),this._app.getAccountListController().getPageNum(), columnItem.getSortField(), bSortAsc, this._app)
		this._app.getAccountListController().setSortOrder(bSortAsc);
		this._app.getAccountListController().show(searchResult);
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex);
	}
}
